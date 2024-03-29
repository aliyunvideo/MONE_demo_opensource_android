package com.aliyun.svideo.recorder;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.system.Os;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.aliyun.aio.avbaseui.widget.AVLoadingDialog;
import com.aliyun.aio.avtheme.AVBaseThemeActivity;
import com.aliyun.svideo.music.music.MusicFileBean;
import com.aliyun.svideo.recorder.utils.FixedToastUtils;
import com.aliyun.svideo.recorder.utils.PhoneStateManger;
import com.aliyun.svideo.recorder.utils.RecordCommon;
import com.aliyun.svideo.recorder.views.AUIRecorderView;
import com.aliyun.svideo.recorder.views.music.AUIMusicSelectListener;
import com.aliyun.svideosdk.recorder.AliyunIRecorder;
import com.aliyun.svideosdk.recorder.impl.AliyunRecorderCreator;
import com.aliyun.ugsv.common.utils.PermissionUtils;
import com.aliyun.ugsv.common.utils.ThreadUtils;
import com.aliyun.ugsv.common.utils.ToastUtils;
import com.aliyun.ugsv.common.utils.UriUtils;

import java.io.File;
import java.lang.ref.WeakReference;

public class AUIVideoRecorderActivity extends AVBaseThemeActivity {

    private AUIRecorderView mVideoRecordView;
    private static final int REQUEST_CODE_PLAY = 2002;
    /**
     * 录制过程中是否使用了音乐
     */
    private boolean isUseMusic;
    /**
     * 判断是否电话状态
     * true: 响铃, 通话
     * false: 挂断
     */
    private boolean isCalling = false;
    /**
     * 权限申请
     */
    String[] permission = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
    private Toast phoningToast;
    private PhoneStateManger phoneStateManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //乐视x820手机在AndroidManifest中设置横竖屏无效，并且只在该activity无效其他activity有效
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.ugsv_recorder_activity_layout);
        mVideoRecordView = findViewById(R.id.ugsv_recorder_recordview);
        boolean checkResult = PermissionUtils.checkPermissionsGroup(this, getPermissions());
        if (!checkResult) {
            PermissionUtils.requestPermissions(this, getPermissions(), PERMISSION_REQUEST_CODE);
        } else {
            initRecord();
        }
    }

    private void initRecord() {
        mVideoRecordView.setActivity(this);
        mVideoRecordView.isUseFlip(RecorderConfig.Companion.getInstance().isVideoFlip());
        mVideoRecordView.setMaxRecordTime(RecorderConfig.Companion.getInstance().getMaxDuration());
        mVideoRecordView.setMinRecordTime(RecorderConfig.Companion.getInstance().getMinDuration());
        mVideoRecordView.setBeautyType(RecorderConfig.Companion.getInstance().getBeautyType());
        AliyunIRecorder alivcRecorder = AliyunRecorderCreator.getRecorderInstance(this);
        com.aliyun.svideosdk.common.struct.recorder.MediaInfo outputInfo = new com.aliyun.svideosdk.common.struct.recorder.MediaInfo();
        outputInfo.setFps(RecorderConfig.Companion.getInstance().getFps());
        outputInfo.setGop(RecorderConfig.Companion.getInstance().getGop());
        outputInfo.setVideoCodec(RecorderConfig.Companion.getInstance().getCodec());
        outputInfo.setVideoQuality(RecorderConfig.Companion.getInstance().getVideoQuality());
        int width = RecorderConfig.Companion.getInstance().getResolution();
        int height = (int) (width / RecorderConfig.Companion.getInstance().getRatio());
        outputInfo.setVideoWidth(width);
        outputInfo.setVideoHeight(height);
        if (RecorderConfig.Companion.getInstance().getBitRate() != RecorderConfig.DEFAULT_BITRATE) {
            outputInfo.setVideoBitrate(RecorderConfig.Companion.getInstance().getBitRate());
        }
        alivcRecorder.setMediaInfo(outputInfo);
        alivcRecorder.setIsAutoClearClipVideos(RecorderConfig.Companion.getInstance().isClearCache());
        mVideoRecordView.init(alivcRecorder);
        if (PermissionUtils.checkPermissionsGroup(this, getPermissions())) {
            //有存储权限的时候才去copy资源
            copyAssets();
        }
    }


    private AsyncTask<Void, Void, Void> copyAssetsTask;

    private void copyAssets() {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                copyAssetsTask = new CopyAssetsTask(AUIVideoRecorderActivity.this).executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }, 700);
    }

    public static class CopyAssetsTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<AUIVideoRecorderActivity> weakReference;
        private volatile boolean mIsLoading;
        private AVLoadingDialog mLoadingDialog;

        CopyAssetsTask(AUIVideoRecorderActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mIsLoading = true;
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mIsLoading) {
                        return;
                    }
                    AUIVideoRecorderActivity activity = weakReference == null ? null : weakReference.get();
                    if (activity == null || activity.isFinishing()) {
                        return;
                    }
                    String tip = activity.getString(R.string.ugsv_recorder_resource_loading);
                    mLoadingDialog = new AVLoadingDialog(activity).tip(tip);
                    mLoadingDialog.show();
                }
            }, 500);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AUIVideoRecorderActivity activity = weakReference.get();
            if (activity != null) {
                RecordCommon.copyAll(activity);
                RecordCommon.copyQueen(activity);
            }
            mIsLoading = false;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AUIVideoRecorderActivity activity = weakReference.get();
            if (activity != null && !activity.isFinishing()) {
                if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                    mLoadingDialog.dismiss();
                    mLoadingDialog = null;
                }
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

    private void initPhoneStateManger() {
        if (phoneStateManger == null) {
            phoneStateManger = new PhoneStateManger(this);
            phoneStateManger.registPhoneStateListener();
            phoneStateManger.setOnPhoneStateChangeListener(new PhoneStateManger.OnPhoneStateChangeListener() {

                @Override
                public void stateIdel() {
                    // 挂断
                    mVideoRecordView.setRecordMute(false);
                    isCalling = false;
                }

                @Override
                public void stateOff() {
                    // 接听
                    mVideoRecordView.setRecordMute(true);
                    isCalling = true;
                }

                @Override
                public void stateRinging() {
                    // 响铃
                    mVideoRecordView.setRecordMute(true);
                    isCalling = true;
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoRecordView.show();
        mVideoRecordView.setBackClickListener(new AUIRecorderView.OnBackClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        mVideoRecordView.setOnMusicSelectListener(new AUIMusicSelectListener() {
            @Override
            public void onMusicSelect(MusicFileBean musicFileBean, long startTime) {
                String path = musicFileBean.getPath();
                if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                    isUseMusic = true;
                } else {
                    isUseMusic = false;
                }

            }
        });

        mVideoRecordView.setCompleteListener(new AUIRecorderView.OnFinishListener() {
            @Override
            public void onComplete(final String path, int duration) {
                // 如果是RACE单独包，直接finish
                if (RecorderConfig.Companion.getInstance().getNeedEdit()) {
                    AUIVideoRecorderRouter.jumpEditor(AUIVideoRecorderActivity.this, path, duration);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        //适配android Q
                        ThreadUtils.runOnSubThread(new Runnable() {
                            @Override
                            public void run() {
                                UriUtils.saveVideoToMediaStore(AUIVideoRecorderActivity.this, path);
                                ThreadUtils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.show(AUIVideoRecorderActivity.this, "已保存到相册");
                                        AUIVideoRecorderActivity.this.finish();
                                    }
                                });
                            }
                        });

                    } else {
                        MediaScannerConnection.scanFile(AUIVideoRecorderActivity.this.getApplicationContext(),
                                new String[]{path},
                                new String[]{"video/mp4"}, null);
                        ToastUtils.show(AUIVideoRecorderActivity.this, "已保存到相册");
                        AUIVideoRecorderActivity.this.finish();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        mVideoRecordView.hide();
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
            phoningToast = FixedToastUtils.show(this, getResources().getString(R.string.ugsv_recorder_toast_phone_state_calling));
        }
    }

    public static final int PERMISSION_REQUEST_CODE = 1000;

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

            if (isAllGranted) {
                // 如果所有的权限都授予了
                initRecord();
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                showPermissionDialog();
            }
        }
    }

    //系统授权设置的弹框
    AlertDialog openAppDetDialog = null;

    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.ugc_app_name) + getResources().getString(R.string.ugsv_recorder_permission_remind));
        builder.setPositiveButton(getResources().getString(R.string.alivc_record_request_permission_positive_btn_text), new DialogInterface.OnClickListener() {
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
        });
        builder.setCancelable(false);
        builder.setNegativeButton(getResources().getString(R.string.ugsv_recorder_permission_not_set), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });
        if (null == openAppDetDialog) {
            openAppDetDialog = builder.create();
        }
        if (null != openAppDetDialog && !openAppDetDialog.isShowing()) {
            openAppDetDialog.show();
        }
    }
}
