package com.aliyun.svideo.template.sample.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
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

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(VideoPlayActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(VideoPlayActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
                    return;
                }
                saveToAlbum();
            }
        });
    }

    private void saveToAlbum() {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "SXTemplateDemo");
        if (!folder.exists()) {
            boolean ret = folder.mkdirs();
            if (!ret) {
                Toast.makeText(this, "创建文件夹失败", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        File src = new File(mVideoPath);
        File dest = new File(folder, System.currentTimeMillis() + ".mp4");
        copyFile(src, dest);

        MediaScannerConnection.scanFile(this, new String[]{dest.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VideoPlayActivity.this, "已保存到相册", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveToAlbum();
            } else {
                Toast.makeText(this, "保存失败，没有权限", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
