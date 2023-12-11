package com.aliyun.svideo.mixrecorder.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Pair;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.aliyun.svideo.base.AliyunSnapVideoParam;
import com.aliyun.svideo.base.widget.ProgressDialog;
import com.aliyun.svideo.mixrecorder.mixrecorder.AlivcMixRecorder;
import com.aliyun.svideo.mixrecorder.util.voice.PhoneStateManger;
import com.aliyun.svideo.record.R;
import com.aliyun.svideo.mixrecorder.AUIVideoMixRecorderRouter;
import com.aliyun.svideo.mixrecorder.bean.AlivcMixBorderParam;
import com.aliyun.svideo.mixrecorder.bean.MixRecorderConfig;
import com.aliyun.svideo.mixrecorder.bean.VideoDisplayParam;
import com.aliyun.svideo.mixrecorder.util.FixedToastUtils;
import com.aliyun.svideo.mixrecorder.util.NotchScreenUtil;
import com.aliyun.svideo.mixrecorder.util.RecordCommon;
import com.aliyun.svideo.mixrecorder.view.AUIMixRecordView;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;
import com.aliyun.svideosdk.mixrecorder.MixAudioAecType;
import com.aliyun.svideosdk.mixrecorder.MixAudioSourceType;
import com.aliyun.ugsv.auibeauty.api.constant.BeautySDKType;
import com.aliyun.ugsv.common.utils.PermissionUtils;
import com.aliyun.ugsv.common.utils.StorageUtils;
import com.aliyun.ugsv.common.utils.ThreadUtils;
import com.aliyun.ugsv.common.utils.ToastUtils;
import com.aliyun.ugsv.common.utils.UriUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 3.10.5 合拍展示界面
 */

public class AUIVideoMixRecordActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_CODE = 1000;
    public static final int REQUEST_CODE_PLAY = 2002;
    public static final String KEY_PARAM_VIDEO = "VIDEO_PATH";

    /**
     * 上个页面传参
     */
    private int mResolutionMode;
    private int mGop;
    private VideoQuality mVideoQuality = VideoQuality.HD;
    private VideoCodecs mVideoCodec = VideoCodecs.H264_HARDWARE;
    private int mRatioMode = AliyunSnapVideoParam.RATIO_MODE_3_4;
    private BeautySDKType renderingMode = BeautySDKType.QUEEN;
    private String mVideoPath;
    private AUIMixRecordView mVideoRecordView;
    private int mFrame;
    private boolean isSvideoQueen = false;
    private MixAudioSourceType mMixAudioSourceType;
    /**
     * 设置合成窗口非填充模式下的背景颜色
     */
    private int mBackgroundColor;
    /**
     * 设置合成窗口非填充模式下的背景图片路径
     */
    private String mBackgroundImagePath;
    /**
     * displayMode 0：裁切 1：填充 2：拉伸
     */
    private int mBackgroundImageDisplayMode;
    /**
     * 判断是否电话状态
     * true: 响铃, 通话
     * false: 挂断
     */
    private boolean isCalling = false;
    /**
     * 录制过程中是否使用了音乐
     */
    private boolean isUseMusic = false;
    /**
     * 是否添加水印
     */
    private boolean mHasWatermark = true;
    /**
     * 主要用于调整合拍功能中录制画面在视频中的布局；普通录制使用默认参数，不需要额外设置
     */
    private VideoDisplayParam mRecordDisplayParam;
    /**
     * 合拍设置视频边框
     * */
    private AlivcMixBorderParam mMixBorderParam;
    /**
     * 主要用于调整合拍功能中导入视频在视频中的布局；普通录制为null，不需要额外设置
     */
    private VideoDisplayParam mPlayDisplayParam;
    /**
     * 判断是否有音乐, 如果有音乐, 编辑界面不能使用音效
     */
    private static final String INTENT_PARAM_KEY_HAS_MUSIC = "hasRecordMusic";

    /**
     * 判断是否是合拍，如果是合拍，则不能使用音乐
     */
    private static final String INTENT_PARAM_KEY_IS_MIX = "isMixRecord";
    /**
     * 权限申请
     */
    String[] permission = {
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * 判断是编辑模块进入还是通过社区模块的编辑功能进入
     */
    private static final String INTENT_PARAM_KEY_ENTRANCE = "entrance";
    /**
     * 判断是编辑模块进入还是通过社区模块的编辑功能进入
     * svideo: 短视频
     * community: 社区
     */
    private String mMixRecordEntrance;

    private Toast phoningToast;
    private PhoneStateManger phoneStateManger;
    private String[] mEffDirs;
    private AsyncTask<Void, Void, Void> initAssetPath;
    private AsyncTask<Void, Void, Void> copyAssetsTask;
    private int mMaxDuration;

    private boolean mIsAutoClearTemp = false;

    String[] permission33 = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
    };

    public String[] getPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return permission;
        }
        return permission33;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        // 检测是否是全面屏手机, 如果不是, 设置FullScreen
        if (!NotchScreenUtil.checkNotchScreen(this)) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initAssetPath();

        setContentView(R.layout.alivc_recorder_activity_record);
        getData();

        mVideoRecordView = findViewById(R.id.alivc_recordView);
        mVideoRecordView.setActivity(this);

        if (!PermissionUtils.checkPermissionsGroup(this, getPermissions())) {
            PermissionUtils.requestPermissions(this, getPermissions(), PERMISSION_REQUEST_CODE);
        }else{
            initRecorder();
        }
    }

    private void initRecorder(){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(mVideoPath);
            mMaxDuration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            mmr.release();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //合拍最大录制时长和最小录制时长一致
        mVideoRecordView.setMaxRecordTime(mMaxDuration);
        mVideoRecordView.setMinRecordTime(mMaxDuration);
        mVideoRecordView.setRatioMode(mRatioMode);
        mVideoRecordView.setResolutionMode(mResolutionMode);
        mVideoRecordView.setVideoPath(mVideoPath);
        mVideoRecordView.setRenderingMode(renderingMode);
        mVideoRecordView.setSvideoRace(isSvideoQueen);
        mVideoRecordView.setMixBorderParam(mMixBorderParam);
        //配置合拍recorder
        AlivcMixRecorder recorderInterface = new AlivcMixRecorder(this);
        recorderInterface.setMixAudioSource(mMixAudioSourceType);
        recorderInterface.setMixAudioAecType(MixAudioAecType.TYPE_AEC_SOFT);
        recorderInterface.setBackgroundColor(mBackgroundColor);
        recorderInterface.setBackgroundImage(mBackgroundImagePath, mBackgroundImageDisplayMode);
        com.aliyun.svideosdk.common.struct.recorder.MediaInfo outputInfo = new com.aliyun.svideosdk.common.struct.recorder.MediaInfo();
        outputInfo.setFps(35);
        outputInfo.setGop(mGop);
        outputInfo.setVideoCodec(mVideoCodec);
        outputInfo.setVideoQuality(mVideoQuality);
        outputInfo.setVideoWidth(MixRecorderConfig.getInstance().getVideoWidth());
        outputInfo.setVideoHeight(MixRecorderConfig.getInstance().getVideoHeight());
        outputInfo.setVideoCodec(mVideoCodec);
        recorderInterface.setMediaInfo(mVideoPath, mPlayDisplayParam, mRecordDisplayParam, outputInfo);
        recorderInterface.setIsAutoClearClipVideos(mIsAutoClearTemp);
        mVideoRecordView.setRecorder(recorderInterface);
        if (PermissionUtils.checkPermissionsGroup(this, getPermissions())) {
            //有存储权限的时候才去copy资源
            copyAssets();
        }
    }

    /**
     * 获取上个页面的传参
     */
    private void getData() {
        mResolutionMode = MixRecorderConfig.getInstance().getResolutionMode();
        mRatioMode = MixRecorderConfig.getInstance().getRatioMode();
        mGop = MixRecorderConfig.getInstance().getGop();
        mVideoQuality = MixRecorderConfig.getInstance().getVideoQuality();
        mVideoPath = MixRecorderConfig.getInstance().getMixVideoFilePath();
        mMixRecordEntrance = getIntent().getStringExtra(INTENT_PARAM_KEY_ENTRANCE);
        isSvideoQueen = MixRecorderConfig.getInstance().getRenderingMode() == BeautySDKType.QUEEN;
        if (mVideoQuality == null) {
            mVideoQuality = VideoQuality.HD;
        }
        mHasWatermark = false;
        mVideoCodec = MixRecorderConfig.getInstance().getVideoCodec();
        if (mVideoCodec == null) {
            mVideoCodec = VideoCodecs.H264_HARDWARE;
        }
        renderingMode = MixRecorderConfig.getInstance().getRenderingMode();
        if (renderingMode == null) {
            renderingMode = BeautySDKType.QUEEN;
        }
        mMixAudioSourceType = MixRecorderConfig.getInstance().getMixAudioSourceType();
        if (mMixAudioSourceType == null) {
            mMixAudioSourceType = MixAudioSourceType.Original;
        }
        mBackgroundColor = MixRecorderConfig.getInstance().getMixBackgroundColor();
        mBackgroundImagePath = MixRecorderConfig.getInstance().getMixBackgroundImagePath();
        mBackgroundImageDisplayMode = 0;
        Pair<VideoDisplayParam, VideoDisplayParam> layout = MixRecorderConfig.getInstance().getLayout(MixRecorderConfig.LayoutOption.HORIZONTAL);
        mPlayDisplayParam = layout.first;
        mRecordDisplayParam = layout.second;
        mMixBorderParam = MixRecorderConfig.getInstance().getMixBorderParam();
        /**
         * 帧率裁剪参数,默认30
         */
        mFrame = MixRecorderConfig.getInstance().getFPS();

        VideoDisplayMode cropMode = null;
        if (cropMode == null) {
            cropMode = VideoDisplayMode.SCALE;
        }

        mIsAutoClearTemp = true;
    }

    private void initAssetPath() {
        initAssetPath = new AssetPathInitTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public static class AssetPathInitTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<AUIVideoMixRecordActivity> weakReference;

        AssetPathInitTask(AUIVideoMixRecordActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AUIVideoMixRecordActivity activity = weakReference.get();
            if (activity != null) {
                activity.setAssetPath();
            }
            return null;
        }
    }

    private void setAssetPath() {
        String path = StorageUtils.getCacheDirectory(this).getAbsolutePath() + File.separator + RecordCommon.QU_NAME
                      + File.separator;
        File filter = new File(new File(path), "filter");
        String[] list = filter.list();
        if (list == null || list.length == 0) {
            return;
        }
        mEffDirs = new String[list.length + 1];
        mEffDirs[0] = null;
        int length = list.length;
        for (int i = 0; i < length; i++) {
            mEffDirs[i + 1] = filter.getPath() + File.separator + list[i];
        }
    }

    private void copyAssets() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                copyAssetsTask = new CopyAssetsTask(AUIVideoMixRecordActivity.this).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }, 700);

    }

    public static class CopyAssetsTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<AUIVideoMixRecordActivity> weakReference;
        ProgressDialog progressBar;

        CopyAssetsTask(AUIVideoMixRecordActivity activity) {

            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AUIVideoMixRecordActivity activity = weakReference.get();
            if (activity != null && !activity.isFinishing()) {
                progressBar = new ProgressDialog(activity);
                progressBar.setMessage(activity.getString(R.string.alivc_progress_content_text));
                progressBar.setCanceledOnTouchOutside(false);
                progressBar.setCancelable(false);
                progressBar.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER);
                progressBar.show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AUIVideoMixRecordActivity activity = weakReference.get();
            if (activity != null) {
                RecordCommon.copyAll(activity);
//                RecordCommon.copyRace(activity);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AUIVideoMixRecordActivity activity = weakReference.get();
            if (activity != null && !activity.isFinishing()) {
                progressBar.dismiss();
                //资源复制完成之后设置一下人脸追踪，防止第一次人脸动图应用失败的问题
                activity.mVideoRecordView.setFaceTrackModePath();
            }

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        initPhoneStateManger();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoRecordView.onResume();
        mVideoRecordView.startPreview();
        mVideoRecordView.setBackClickListener(new AUIMixRecordView.OnBackClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });


        mVideoRecordView.setCompleteListener(new AUIMixRecordView.OnFinishListener() {

            @Override
            public void onComplete(final String path, int duration, int ratioMode) {
                // 如果是RACE单独包，直接finish
                boolean needEdit = true;
                if (needEdit) {
                    AUIVideoMixRecorderRouter.jumpEditor(AUIVideoMixRecordActivity.this, path, duration);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        //适配android Q
                        ThreadUtils.runOnSubThread(new Runnable() {
                            @Override
                            public void run() {
                                UriUtils.saveVideoToMediaStore(AUIVideoMixRecordActivity.this, path);
                                ThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.show(AUIVideoMixRecordActivity.this, "已保存到相册");
                                        AUIVideoMixRecordActivity.this.finish();
                                    }
                                });
                            }
                        });

                    } else {
                        MediaScannerConnection.scanFile(AUIVideoMixRecordActivity.this.getApplicationContext(),
                                new String[]{path},
                                new String[]{"video/mp4"}, null);
                        ToastUtils.show(AUIVideoMixRecordActivity.this, "已保存到相册");
                        AUIVideoMixRecordActivity.this.finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        mVideoRecordView.onPause();
        mVideoRecordView.stopPreview();
        super.onPause();
        if (phoningToast != null) {
            phoningToast.cancel();
            phoningToast = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (phoneStateManger != null) {
            phoneStateManger.setOnPhoneStateChangeListener(null);
            phoneStateManger.unRegistPhoneStateListener();
            phoneStateManger = null;
        }

        if (mVideoRecordView != null) {
            mVideoRecordView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        mVideoRecordView.destroyRecorder();
        super.onDestroy();
        if (copyAssetsTask != null) {
            copyAssetsTask.cancel(true);
            copyAssetsTask = null;
        }

        if (initAssetPath != null) {
            initAssetPath.cancel(true);
            initAssetPath = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PLAY) {
            if (resultCode == Activity.RESULT_OK) {
                mVideoRecordView.deleteAllPart();
                finish();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isCalling) {
            phoningToast = FixedToastUtils.show(this, getResources().getString(R.string.alivc_recorder_record_tip_phone_state_calling));
        }
    }

    private void initPhoneStateManger() {
        if (phoneStateManger == null) {
            phoneStateManger = new PhoneStateManger(this);
            phoneStateManger.registPhoneStateListener();
            phoneStateManger.setOnPhoneStateChangeListener(new PhoneStateManger.OnPhoneStateChangeListener() {

                @Override
                public void stateIdel() {
                    // 挂断
                    // mVideoRecordView.setRecordMute(false);
                    isCalling = false;
                }

                @Override
                public void stateOff() {
                    // 接听
                    //   mVideoRecordView.setRecordMute(true);
                    isCalling = true;
                }

                @Override
                public void stateRinging() {
                    // 响铃
                    //  mVideoRecordView.setRecordMute(true);
                    isCalling = true;
                }
            });
        }

    }

    /**
     * 合拍入口
     */
    public static void startMixRecord(Context context, List<String> path) {
        MixRecorderConfig.getInstance().setMixVideoFilePath(path.get(0));
        Intent intent = new Intent(context, AUIVideoMixRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (!isAllGranted) {
                // 如果所有的权限都授予了
                showPermissionDialog();

            }else{
                initRecorder();
            }
        }
    }
    //系统授权设置的弹框
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.app_name) + getString(R.string.alivc_record_request_permission_content_text))
        .setPositiveButton(R.string.alivc_record_request_permission_positive_btn_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        })
        .setNegativeButton(R.string.alivc_record_request_permission_negative_btn_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        })
        .setCancelable(false)
        .create()
        .show();

    }

}
