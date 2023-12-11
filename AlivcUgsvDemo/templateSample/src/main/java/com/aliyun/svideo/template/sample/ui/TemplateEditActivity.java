package com.aliyun.svideo.template.sample.ui;

import static com.zhihu.matisse.MimeType.AVI;
import static com.zhihu.matisse.MimeType.GIF;
import static com.zhihu.matisse.MimeType.JPEG;
import static com.zhihu.matisse.MimeType.MKV;
import static com.zhihu.matisse.MimeType.MP4;
import static com.zhihu.matisse.MimeType.MPEG;
import static com.zhihu.matisse.MimeType.PNG;
import static com.zhihu.matisse.MimeType.QUICKTIME;
import static com.zhihu.matisse.MimeType.THREEGPP;
import static com.zhihu.matisse.MimeType.THREEGPP2;
import static com.zhihu.matisse.MimeType.TS;
import static com.zhihu.matisse.MimeType.WEBM;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.svideo.base.Constants;
import com.aliyun.svideo.music.music.MusicFileBean;
import com.aliyun.svideo.template.sample.AssetsUtils;
import com.aliyun.svideo.template.sample.VideoClipActivity;
import com.aliyun.svideo.template.sample.bean.AlivcCropOutputParam;
import com.aliyun.svideo.template.sample.ui.controller.MusicSelectController;
import com.aliyun.svideo.template.sample.util.Common;
import com.aliyun.svideosdk.template.AliyunAEITemplate;
import com.aliyun.svideosdk.template.AliyunAETemplateCreator;
import com.aliyun.svideosdk.template.AliyunTemplatePlayStateListener;
import com.aliyun.svideosdk.template.AliyunTemplateRenderListener;
import com.aliyun.svideosdk.template.model.AliyunAETemplateAsset;
import com.aliyun.svideosdk.template.model.AliyunAETemplateAssetMedia;
import com.aliyun.svideosdk.template.model.AliyunAETemplateAssetText;
import com.aliyun.svideo.template.sample.util.DateTimeUtils;
import com.aliyun.svideosdk.template.AliyunAETemplateIEditor;
import com.aliyun.svideosdk.template.AliyunAETemplateIPlayer;
import com.aliyun.svideosdk.template.view.AliyunAETemplatePlayView;
import com.aliyun.svideo.template.sample.Template;
import com.aliyun.svideo.template.sample.R;
import com.aliyun.svideo.template.sample.ui.adapter.TemplateEditorAdapter;
import com.aliyun.svideo.template.sample.ui.view.ProgressDialog;
import com.aliyun.ugsv.common.utils.DensityUtil;
import com.aliyun.ugsv.common.utils.FileUtils;
import com.aliyun.ugsv.common.utils.PermissionUtils;
import com.zhihu.matisse.AVMatisse;
import com.zhihu.matisse.MimeType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TemplateEditActivity extends AppCompatActivity implements View.OnClickListener, MusicSelectController.MusicInnerListener{
    private static final String TAG = "TemplateEditActivity";
    private static final int TAB_VIDEO = 0;
    private static final int TAB_TEXT = 1;
    private static final int TAB_MUSIC = 2;
    private String mTemplateFolder;
    private String mTemplate;
    private ProgressDialog mDialog;
    private AliyunAETemplatePlayView mPlayerView;
    private AliyunAETemplateIEditor mAliyunTemplateEditor;
    private AliyunAETemplateIPlayer mAliyunTemplatePlayer;
    private AliyunAEITemplate mAliyunTemplate;
    private String mOutputPath;
    private ImageView mPlayView;
    private TextView mTvTime;
    private SeekBar mSeekBar;
    private TextView mTvDuration;
    private View mVFocus;
    private MusicSelectController mMusicController;

    private List<AliyunAETemplateAsset> mVideoParamList = new ArrayList<>();
    private List<AliyunAETemplateAsset> mTextParamList = new ArrayList<>();
    private AliyunAETemplateAsset mEditAsset;
    private View mBackBtn;
    PanelPresenter mPanelPresenter;
    private String mClipVideoPath;
    private int mMusicType = PanelPresenter.MUSIC_ORIGIN;
    private View mBottomCover;

    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String[] permissions33 = new String[]{
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
    };
    private AliyunTemplatePlayStateListener mListener = new AliyunTemplatePlayStateListener() {
        @Override
        public void onProgress(int frame) {
            mPlayView.post(new Runnable() {
                @Override
                public void run() {
                    mTvTime.setText(DateTimeUtils.formatMs((long)(frame / mAliyunTemplate.getFps() * 1000)));
                    mPlayView.setImageResource(R.drawable.aliyun_svideo_pause);
                    mSeekBar.setProgress(frame);
                }
            });
        }

        @Override
        public void onFinish() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPlayView.setImageResource(R.drawable.aliyun_svideo_play);
                    mAliyunTemplatePlayer.start();
                }
            });
        }
    };

    @Override
    public void onMusicSelect(MusicFileBean musicFileBean) {
        if (musicFileBean == null || TextUtils.isEmpty(musicFileBean.path)) return;
        mMusicType = PanelPresenter.MUSIC_OTHER;
        mAliyunTemplateEditor.replaceAudio(musicFileBean.path);
        mPanelPresenter.muteMusicOpt.setSelected(false);
        mPanelPresenter.originMusicOpt.setSelected(false);
        mPanelPresenter.otherMusicOpt.setSelected(true);
        mAliyunTemplateEditor.commit();
        onStartPlay();
    }

    @Override
    public void onDialogDismiss() {
        mBottomCover.setVisibility(View.GONE);
    }

    @Override
    public void onDialogShow() {
        mBottomCover.setVisibility(View.VISIBLE);
    }

    class PanelPresenter implements View.OnClickListener {
        public final static int MUSIC_MUTE = 0;
        public final static int MUSIC_ORIGIN = 1;
        public final static int MUSIC_OTHER = 2;
        View videoOpt, textOpt, musicOpt, musciOptPanel;
        RecyclerView recyclerView;
        TemplateEditorAdapter adapter;
        AppCompatActivity activity;
        PopupWindow editMenu;
        View replaceVideo, cuttingVideo, deleteVideo;
        View muteMusicOpt, originMusicOpt, otherMusicOpt;
        public PanelPresenter(AppCompatActivity act){
            activity = act;
            videoOpt = activity.findViewById(R.id.tv_editor_video);
            videoOpt.setOnClickListener(this);
            textOpt = activity.findViewById(R.id.tv_editor_text);
            textOpt.setOnClickListener(this);
            musicOpt = activity.findViewById(R.id.tv_editor_music);
            musicOpt.setOnClickListener(this);
            musciOptPanel = activity.findViewById(R.id.music_opt_panel);
            recyclerView = activity.findViewById(R.id.recycler_view);
            editMenu = new PopupWindow(activity);
            View popupMenuView = LayoutInflater.from(activity).inflate(R.layout.template_video_edit_menu, null);
            replaceVideo = popupMenuView.findViewById(R.id.replace_btn);
            cuttingVideo = popupMenuView.findViewById(R.id.cutting_btn);
            deleteVideo = popupMenuView.findViewById(R.id.delete_btn);
            editMenu.setContentView(popupMenuView);
            editMenu.setTouchable(true);
            editMenu.setFocusable(true);
            editMenu.setOutsideTouchable(true);
            editMenu.update();

            muteMusicOpt = activity.findViewById(R.id.mute);
            originMusicOpt = activity.findViewById(R.id.origin);
            otherMusicOpt = activity.findViewById(R.id.other);

            replaceVideo.setOnClickListener(this);
            cuttingVideo.setOnClickListener(this);
            deleteVideo.setOnClickListener(this);
            muteMusicOpt.setOnClickListener(this);
            originMusicOpt.setOnClickListener(this);
            otherMusicOpt.setOnClickListener(this);

            recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            adapter = new TemplateEditorAdapter();
            adapter.setOnItemClickCallback(new TemplateEditorAdapter.OnItemClickCallback() {
                @Override
                public void onEdit(final AliyunAETemplateAsset param, View view) {
                    mEditAsset = param;
                    if(mEditAsset instanceof AliyunAETemplateAssetMedia){
                        if (!PermissionUtils.checkPermissionsGroup(TemplateEditActivity.this, getPermissions())) {
                            PermissionUtils.requestPermissions(TemplateEditActivity.this, getPermissions(), Common.REQUEST_PERMISSION_SINGLE);
                        } else {
//                        pickSingleMedia();
                            editMenu.showAsDropDown(view, 0, -DensityUtil.dip2px(getApplicationContext(), 100));
                        }
                    }else {
                        TemplateTextEditDialogFragment templateTextEditDialogFragment = new TemplateTextEditDialogFragment();
                        templateTextEditDialogFragment.setText(((AliyunAETemplateAssetText)param).getReplaceText());
                        templateTextEditDialogFragment.setOnResultListener(new TemplateTextEditDialogFragment.OnResultListener() {
                            @Override
                            public void onResult(String text) {
                                ((AliyunAETemplateAssetText)param).setReplaceText(text);
                                mAliyunTemplateEditor.commit();
                                onSelect(param, view);
                            }
                        });
                        templateTextEditDialogFragment.show(getSupportFragmentManager(), TemplateTextEditDialogFragment.class.getSimpleName());
                    }
                }

                @Override
                public void onSelect(AliyunAETemplateAsset param, View view) {
//                mVFocus.setVisibility(View.VISIBLE);
                    onPausePlay();;
                    int targetFrame = (int)(param.getTimelineIn() * mAliyunTemplate.getFps());
                    mAliyunTemplatePlayer.seek(targetFrame);
                    mSeekBar.setProgress(targetFrame);
                    mTvTime.setText(DateTimeUtils.formatMs((long)(targetFrame / mAliyunTemplate.getFps() * 1000)));
                }
            });
            recyclerView.setAdapter(adapter);
        }

        public void showVideoEdit(){
            showTab(TAB_VIDEO);
        }

        public void showTextEdit(){
            showTab(TAB_TEXT);
        }

        public void showMusicEdit(){
            showTab(TAB_MUSIC);
        }

        public void showTab(int tab) {
            if (TAB_VIDEO == tab) {
                musciOptPanel.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                videoOpt.setSelected(true);
                textOpt.setSelected(false);
                musicOpt.setSelected(false);
                adapter.setData(mVideoParamList);
            } else if (TAB_TEXT == tab) {
                musciOptPanel.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                videoOpt.setSelected(false);
                textOpt.setSelected(true);
                musicOpt.setSelected(false);
                adapter.setData(mTextParamList);
            } else if(TAB_MUSIC == tab){
                musciOptPanel.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                videoOpt.setSelected(false);
                textOpt.setSelected(false);
                musicOpt.setSelected(true);
            }
        }

        public void selectMusicOpt(int type){
            if(type == MUSIC_MUTE){
                muteMusicOpt.setSelected(true);
                originMusicOpt.setSelected(false);
                otherMusicOpt.setSelected(false);
                mAliyunTemplateEditor.mute();
            } else if(type == MUSIC_ORIGIN){
                muteMusicOpt.setSelected(false);
                originMusicOpt.setSelected(true);
                otherMusicOpt.setSelected(false);
                mAliyunTemplateEditor.restoreAudio();
            } else if(type == MUSIC_OTHER){
                mBottomCover.setVisibility(View.VISIBLE);
                //open music choose panel
                onPausePlay();
                mMusicController.showMusicSelectPanel(activity.getSupportFragmentManager());
            }
        }

        public void notifyPanelDataChanged(){
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_editor_video) {
                if (!v.isSelected()) {
                    showTab(TAB_VIDEO);
                }
            } else if (v.getId() == R.id.tv_editor_text) {
                if (!v.isSelected()) {
                    showTab(TAB_TEXT);
                }
            } else if(v.getId() == R.id.tv_editor_music) {
                if (!v.isSelected()) {
                    showTab(TAB_MUSIC);
                }
            } else if(v.getId() == R.id.replace_btn){
                pickSingleMedia();
                editMenu.dismiss();
            } else if(v.getId() == R.id.cutting_btn){
                if(mEditAsset instanceof AliyunAETemplateAssetMedia){
                    String replacePath = ((AliyunAETemplateAssetMedia) mEditAsset).getReplacePath();
                    if(!((AliyunAETemplateAssetMedia) mEditAsset).getPath().equals(replacePath) && getFileType(replacePath) == 2){
                        VideoClipActivity.start(TemplateEditActivity.this, replacePath, 7000L);
                    }
                }
            } else if(v.getId() == R.id.delete_btn){
                editMenu.dismiss();
                mEditAsset.restore();
                this.notifyPanelDataChanged();
                mAliyunTemplateEditor.commit();
            } else if(v.getId() == R.id.mute) {
                mMusicType = MUSIC_MUTE;
                selectMusicOpt(MUSIC_MUTE);
            } else if(v.getId() == R.id.origin){
                mMusicType = MUSIC_ORIGIN;
                selectMusicOpt(MUSIC_ORIGIN);
            } else if(v.getId() == R.id.other){
                selectMusicOpt(MUSIC_OTHER);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SXVE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aliyun_template_preview_edit);
        mAliyunTemplateEditor = AliyunAETemplateCreator.createTemplateEditor(this);

        mPlayerView = findViewById(R.id.surface_view);
        mPlayerView.setPlayCallback(mListener);

        mAliyunTemplateEditor.setPlayView(mPlayerView);
        mPlayView = findViewById(R.id.aliyun_template_btn_play);
        mTvTime = findViewById(R.id.aliyun_template_tv_time);
        mSeekBar = findViewById(R.id.aliyun_template_play_seekbar);
        mTvDuration = findViewById(R.id.aliyun_template_tv_duration);
        mVFocus = findViewById(R.id.v_focus);
        mBackBtn = findViewById(R.id.back);
        mBackBtn.setOnClickListener(this);
        mDialog = new ProgressDialog();
        mPanelPresenter = new PanelPresenter(this);
        mBottomCover = findViewById(R.id.bottom_cover);

        mTemplateFolder = getIntent().getStringExtra(Common.KEY_FOLDER);
        mTemplate = getIntent().getStringExtra(Common.KEY_TEMPLATE);

//        mAliyunTemplateEditor.replaceAudio(new File(mTemplateFolder, "music.mp3").getPath());
        new LoadTemplateTask().execute(mTemplateFolder);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mAliyunTemplatePlayer.seek(progress);
                    mPlayView.setImageResource(R.drawable.aliyun_svideo_play);
                    mTvTime.setText(DateTimeUtils.formatMs((long)(progress / mAliyunTemplate.getFps() * 1000)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAliyunTemplatePlayer.isPlaying()) {
                    onPausePlay();
                } else {
                    onStartPlay();
                    mVFocus.setVisibility(View.GONE);
                }
            }
        });

    }

    private void initWhenTemplateReady(){
        if(mAliyunTemplate == null) throw new RuntimeException("no allow empty template");
        long duration = (long)(mAliyunTemplate.getDuration() * 1000);
        mTvDuration.setText(DateTimeUtils.formatMs(duration));
        mSeekBar.setMax((int) (mAliyunTemplate.getDuration() * mAliyunTemplate.getFps()));
        List<AliyunAETemplateAsset> assets = mAliyunTemplate.getAssets();
        for(int i = 0; i < assets.size(); ++i){
            if(assets.get(i) instanceof AliyunAETemplateAssetMedia){
                mVideoParamList.add(assets.get(i));
            }else{
                mTextParamList.add(assets.get(i));
            }
        }

        mPanelPresenter.showTab(TAB_VIDEO);
        mPanelPresenter.selectMusicOpt(PanelPresenter.MUSIC_ORIGIN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAliyunTemplatePlayer != null){
            mAliyunTemplatePlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAliyunTemplatePlayer != null){
            mAliyunTemplatePlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAliyunTemplatePlayer != null) mAliyunTemplatePlayer.stop();
        if(mAliyunTemplateEditor != null) mAliyunTemplateEditor.release();
        clearCacheClipVideo();
    }

    public void done(View view) {
        render();
    }

    private void render() {
        mOutputPath = null;
        TemplateExportActivity.start(this, mTemplateFolder, mAliyunTemplate.getConfig(), mMusicType, mMusicType == PanelPresenter.MUSIC_OTHER ? mAliyunTemplateEditor.getTemplate().getReplaceAudio() : null, getOutputPath());
    }

    private String getOutputPath() {
        if(mOutputPath == null){
            mOutputPath = Constants.SDCardConstants.getDir(this) + DateTimeUtils.getDateTimeFromMillisecond(System.currentTimeMillis()) + Constants.SDCardConstants.TEMPLATE_SUFFIX;
        }
        return mOutputPath;
    }

    private void pickSingleMedia() {
        AVMatisse.from(this)
                .choose(EnumSet.of(JPEG, PNG, MPEG, MP4, QUICKTIME, THREEGPP, THREEGPP2, MKV, WEBM, TS, AVI, GIF))
                .maxSelectable(1)
                .showSingleMediaType(true)
                .countable(true)
                .forResult(Common.REQUEST_SINGLE_MEDIA);
    }

    public void close(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.back) {
            close(null);
        }
    }

    class LoadTemplateTask extends AsyncTask<String, Void, AliyunAEITemplate> {

        @Override
        protected AliyunAEITemplate doInBackground(String... strings) {
            if(!new File(mTemplateFolder).exists()){
                AssetsUtils.copyDirFromAssets(TemplateEditActivity.this, Common.TEMPLATE_FOLDER + File.separator + mTemplate, mTemplateFolder);
            }
            try {
                return AliyunAETemplateCreator.createTemplate(TemplateEditActivity.this, strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(AliyunAEITemplate tpl) {
            if (tpl != null) {
                mAliyunTemplateEditor.setTemplate(tpl);
                mMusicController = new MusicSelectController(mAliyunTemplateEditor, TemplateEditActivity.this);
                mAliyunTemplate = tpl;
                initWhenTemplateReady();
                preview();
            }
        }
    }

    private void preview(){
        mAliyunTemplateEditor.commit();
        mAliyunTemplatePlayer = mAliyunTemplateEditor.getPreviewPlayer();
        mAliyunTemplatePlayer.start();
    }

    private int getFileType(String path){
        if(TextUtils.isEmpty(path)) return -1;
        int dotPos = path.lastIndexOf(".");
        String extension = dotPos >= 0 ? path.substring(dotPos + 1) : "";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (MimeType.isImage(mimeType)) {
            return 1;
        } else if (MimeType.isVideo(mimeType)) {
            return 2;
        } else {
            return -1;
        }
    }

    private void clearCacheClipVideo(){
        if(!TextUtils.isEmpty(mClipVideoPath) && new File(mClipVideoPath).exists()){
            FileUtils.deleteFile(mClipVideoPath);
            mClipVideoPath = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Common.REQUEST_SINGLE_MEDIA && resultCode == RESULT_OK) {
            if(mEditAsset == null || !(mEditAsset instanceof AliyunAETemplateAssetMedia)) throw new RuntimeException("no selected media asset");
            List<String> strings = AVMatisse.obtainPathResult(data);
            String path = strings.get(0);
            int type = getFileType(path);
            if (type == 1) {
                ((AliyunAETemplateAssetMedia) mEditAsset).setImageAsset(path);
                mAliyunTemplateEditor.commit();
            } else if (type == 2) {
                clearCacheClipVideo();
                ((AliyunAETemplateAssetMedia) mEditAsset).setVideoPath(path, false, 0f);
                mAliyunTemplateEditor.commit();
            } else {
                Log.e(TAG, "unknown mime type: " + path);
            }
            mPanelPresenter.notifyPanelDataChanged();
        } else if (requestCode == Common.REQUEST_CLIP_VIDEO && resultCode == RESULT_OK) {
            AlivcCropOutputParam clipResult = (AlivcCropOutputParam)data.getSerializableExtra(AlivcCropOutputParam.RESULT_KEY_OUTPUT_PARAM);
            if(clipResult == null || TextUtils.isEmpty(clipResult.getOutputPath()) || !new File(clipResult.getOutputPath()).exists()) return;
            clearCacheClipVideo();
            mClipVideoPath = clipResult.getOutputPath();
            ((AliyunAETemplateAssetMedia) mEditAsset).setVideoPath(mClipVideoPath, false, 0f);
            mAliyunTemplateEditor.commit();
            mPanelPresenter.notifyPanelDataChanged();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Common.REQUEST_PERMISSION_SINGLE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                pickSingleMedia();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onStartPlay(){
        mPlayView.setImageResource(R.drawable.aliyun_svideo_pause);
        mAliyunTemplatePlayer.start();
    }

    public void onPausePlay(){
        mAliyunTemplatePlayer.pause();
        mPlayView.setImageResource(R.drawable.aliyun_svideo_play);
    }

    public String[] getPermissions(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            return permissions;
        }
        return permissions33;
    }
}
