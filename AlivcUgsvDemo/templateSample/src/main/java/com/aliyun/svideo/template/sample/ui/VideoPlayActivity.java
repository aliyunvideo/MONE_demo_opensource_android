package com.aliyun.svideo.template.sample.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.aliyun.ugsv.common.utils.PermissionUtils;
import com.aliyun.ugsv.common.utils.ThreadUtils;
import com.aliyun.ugsv.common.utils.UriUtils;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.aliyun.svideo.template.sample.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class VideoPlayActivity extends AppCompatActivity {

    private String mVideoPath;
    private StyledPlayerView mVideoView;
    private SimpleExoPlayer player;

    public static void start(Context context, String path) {
        Intent starter = new Intent(context, VideoPlayActivity.class);
        starter.putExtra("path", path);
        context.startActivity(starter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        player.pause();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        player.release();
        player = null;
        super.onDestroy();
    }

    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String[] permissions33 = new String[]{
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_IMAGES
    };

    public String[] getPermissions(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            return permissions;
        }
        return permissions33;
    }

    private View mSaveBtn;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SXVE);
        setContentView(R.layout.activity_preview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mVideoPath = getIntent().getStringExtra("path");
        mVideoView = findViewById(R.id.exo_video_view);
        player = new SimpleExoPlayer.Builder(this).build();
        mVideoView.setPlayer(player);
        mVideoView.setShowNextButton(false);
        mVideoView.setShowPreviousButton(false);
        mVideoView.setControlDispatcher(new DefaultControlDispatcher(5000, 5000));
        MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(new File(mVideoPath)));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        mSaveBtn = findViewById(R.id.btn_save);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionUtils.checkPermissionsGroup(VideoPlayActivity.this, getPermissions())) {
                    PermissionUtils.requestPermissions(VideoPlayActivity.this, getPermissions(), 11);
                    return;
                }
                saveToAlbum();
            }
        });
    }

    private void saveToAlbum() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //适配android Q
            ThreadUtils.runOnSubThread(new Runnable() {
                @Override
                public void run() {
                    UriUtils.saveVideoToMediaStore(VideoPlayActivity.this.getApplicationContext(), mVideoPath);
                }
            });
        } else {
            scanFile();
        }
        Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
        mSaveBtn.setEnabled(false);
    }

    private void scanFile() {
        MediaScannerConnection.scanFile(getApplicationContext(),
                new String[] {mVideoPath}, new String[] {"video/mp4"}, null);
    }

    public static void copyFile(File src, File dest) {
        if (!src.getAbsolutePath().equals(dest.getAbsolutePath())) {
            try {
                InputStream in = new FileInputStream(src);
                FileOutputStream out = new FileOutputStream(dest);
                byte[] buf = new byte[1024];

                int len;
                while ((len = in.read(buf)) >= 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 11) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                saveToAlbum();
            } else {
                Toast.makeText(this, "保存失败，没有权限", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
