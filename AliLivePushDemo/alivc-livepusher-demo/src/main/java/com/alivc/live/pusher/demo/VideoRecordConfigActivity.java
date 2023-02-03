package com.alivc.live.pusher.demo;

import static com.alivc.live.pusher.AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT;
import static com.alivc.live.pusher.AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT;
import static com.alivc.live.pusher.AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.acker.simplezxing.activity.CaptureActivity;
import com.alivc.live.pusher.AlivcLiveBase;
import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.AlivcLivePushError;
import com.alivc.live.pusher.AlivcLivePushErrorListener;
import com.alivc.live.pusher.AlivcLivePushInfoListener;
import com.alivc.live.pusher.AlivcLivePushNetworkListener;
import com.alivc.live.pusher.AlivcLivePushStatsInfo;
import com.alivc.live.pusher.AlivcLivePusher;
import com.alivc.live.pusher.AlivcPreviewOrientationEnum;
import com.alivc.live.pusher.AlivcResolutionEnum;
import com.alivc.live.pusher.demo.floatwindowpermission.FloatWindowManager;
import com.alivc.live.pusher.demo.test.PushDemoTestConstants;
import com.alivc.live.pusher.widget.PushConfigBottomSheet;
import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.aio.avtheme.AVBaseThemeActivity;
import com.alivc.live.commonutils.StatusBarUtil;

import java.io.File;

public class VideoRecordConfigActivity extends AVBaseThemeActivity {
    private static final String TAG = "VideoRecordConfig";

    private AlivcResolutionEnum mDefinition = AlivcResolutionEnum.RESOLUTION_540P;
    private static final int REQ_CODE_PERMISSION = 0x1111;
    private static final int PROGRESS_0 = 0;
    private static final int PROGRESS_16 = 16;
    private static final int PROGRESS_20 = 20;
    private static final int PROGRESS_33 = 33;
    private static final int PROGRESS_40 = 40;
    private static final int PROGRESS_50 = 50;
    private static final int PROGRESS_60 = 60;
    private static final int PROGRESS_66 = 66;
    private static final int PROGRESS_75 = 75;
    private static final int PROGRESS_80 = 80;
    private static final int PROGRESS_90 = 90;
    private static final int PROGRESS_100 = 100;
    public static final int CAPTURE_PERMISSION_REQUEST_CODE = 0x1123;
    public static final int OVERLAY_PERMISSION_REQUEST_CODE = 0x1124;

    private InputMethodManager manager;
    private SeekBar mResolution;
    private TextView mResolutionText;
    private EditText mUrl;
    private ImageView mQr;
    private ImageView mBack;
    private TextView mPushTex;
    private TextView mOrientation;
    private TextView mNoteText;

    private AlivcLivePushConfig mAlivcLivePushConfig;

    private AlivcLivePusher mAlivcLivePusher = null;

    private OrientationEventListener mOrientationEventListener;


    private Switch mNarrowBandHDConfig;

    private int mLastRotation;
    private boolean mIsStartPushing = false;
    private PushConfigDialogImpl mPushConfigDialog = new PushConfigDialogImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.translucent(this, Color.TRANSPARENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.video_recording_setting);

        // 注册sdk
        AlivcLiveBase.registerSDK();

        mAlivcLivePushConfig = new AlivcLivePushConfig();
        if (mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.getOrientation() || mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT.getOrientation()) {
            mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network_land.png");
            mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push_land.png");
        } else {
            mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network.png");
            mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push.png");
        }
        AlivcLivePushConfig.setMediaProjectionPermissionResultData(null);
        initView();
        setClick();
        mOrientationEventListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation = getDisplayRotation();
                if (rotation != mLastRotation) {
                    if (mAlivcLivePusher != null) {
                        mAlivcLivePusher.setScreenOrientation(rotation);
                    }

                    mLastRotation = rotation;
                }
            }
        };
        Intent intent = getIntent();
        String url = intent.getStringExtra("pushUrl");
        if (!TextUtils.isEmpty(url)) {
            mUrl.setText(url);
        }
    }

    private int getDisplayRotation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                break;
        }
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        VideoRecordViewManager.hideViewRecordWindow();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAlivcLivePusher != null && mAlivcLivePusher.isPushing()) {
            VideoRecordViewManager.createViewoRecordWindow(VideoRecordConfigActivity.this, getApplicationContext(), mAlivcLivePusher, cameraOnListener);
            VideoRecordViewManager.showViewRecordWindow();
        }
    }

    private VideoRecordViewManager.CameraOn cameraOnListener = new VideoRecordViewManager.CameraOn() {
        @Override
        public void onCameraOn(boolean on) {
            if (on) {
                VideoRecordViewManager.createViewoRecordCameraWindow(VideoRecordConfigActivity.this, getApplicationContext(), mAlivcLivePusher);
            } else {
                VideoRecordViewManager.removeVideoRecordCameraWindow(getApplicationContext());
            }
        }
    };

    private void initView() {
        mUrl = (EditText) findViewById(R.id.url_editor);
        mPushTex = (TextView) findViewById(R.id.pushStatusTex);
        mResolution = (SeekBar) findViewById(R.id.resolution_seekbar);
        mResolutionText = (TextView) findViewById(R.id.resolution_text);
        mOrientation = findViewById(R.id.main_orientation);
        mQr = (ImageView) findViewById(R.id.qr_code);
        mBack = (ImageView) findViewById(R.id.iv_back);
        mNoteText = (TextView) findViewById(R.id.note_text);
        mNarrowBandHDConfig = (Switch) findViewById(R.id.narrowband_hd);

        String initUrl = PushDemoTestConstants.getTestPushUrl();
        if (!initUrl.isEmpty()) {
            mUrl.setText(initUrl);
        }
    }

    private void setClick() {
        mResolution.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mQr.setOnClickListener(onClickListener);
        mBack.setOnClickListener(onClickListener);
        mOrientation.setOnClickListener(onClickListener);
        mPushTex.setOnClickListener(onClickListener);
        mNarrowBandHDConfig.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.pushStatusTex) {
                if (mIsStartPushing) {
                    return;
                }
                if (!(mUrl.getText().toString().contains("rtmp://") || mUrl.getText().toString().contains("artc://"))) {
                    AVToast.show(VideoRecordConfigActivity.this, true, "url format unsupported");
                    return;
                }

                mIsStartPushing = true;

                if (getPushConfig() != null) {
                    if (mAlivcLivePusher == null) {
                        //if(VideoRecordViewManager.permission(getApplicationContext()))
                        if (FloatWindowManager.getInstance().applyFloatWindow(VideoRecordConfigActivity.this)) {
                            Intent intent = new Intent(VideoRecordConfigActivity.this, ForegroundService.class);
                            startService(intent);
                            startScreenCapture();
                        } else {
                            mIsStartPushing = false;
                        }
                    } else {
                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mIsStartPushing = false;
                            }
                        }, 1000);
                        Intent intent = new Intent(VideoRecordConfigActivity.this, ForegroundService.class);
                        stopService(intent);
                        stopPushWithoutSurface();
                    }
                }
            } else if (id == R.id.qr_code) {
                if (ContextCompat.checkSelfPermission(VideoRecordConfigActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Do not have the permission of camera, request it.
                    ActivityCompat.requestPermissions(VideoRecordConfigActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                } else {
                    // Have gotten the permission
                    startCaptureActivityForResult();
                }
            } else if (id == R.id.iv_back) {
                finish();
            } else if (id == R.id.main_orientation) {
                mPushConfigDialog.showConfigDialog(mOrientation, mOrientationListener, 0);
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int seekBarId = seekBar.getId();
            if (mResolution.getId() == seekBarId) {
                if (progress <= PROGRESS_0) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_180P;
                    mResolutionText.setText(R.string.setting_resolution_180P);
                } else if (progress > PROGRESS_0 && progress <= PROGRESS_20) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_240P;
                    mResolutionText.setText(R.string.setting_resolution_240P);
                } else if (progress > PROGRESS_20 && progress <= PROGRESS_40) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_360P;
                    mResolutionText.setText(R.string.setting_resolution_360P);
                } else if (progress > PROGRESS_40 && progress <= PROGRESS_60) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_480P;
                    mResolutionText.setText(R.string.setting_resolution_480P);
                } else if (progress > PROGRESS_60 && progress <= PROGRESS_80) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_540P;
                    mResolutionText.setText(R.string.setting_resolution_540P);
                } else if (progress > PROGRESS_80 && progress <= PROGRESS_90) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_720P;
                    mResolutionText.setText(R.string.setting_resolution_720P);
                } else if (progress > PROGRESS_90) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_1080P;
                    mResolutionText.setText(R.string.setting_resolution_1080P);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (mResolution.getId() == seekBar.getId()) {
                if (progress < PROGRESS_0) {
                    seekBar.setProgress(0);
                } else if (progress > PROGRESS_0 && progress <= PROGRESS_20) {
                    seekBar.setProgress(PROGRESS_20);
                } else if (progress > PROGRESS_20 && progress <= PROGRESS_40) {
                    seekBar.setProgress(PROGRESS_40);
                } else if (progress > PROGRESS_40 && progress <= PROGRESS_60) {
                    seekBar.setProgress(PROGRESS_60);
                } else if (progress > PROGRESS_60 && progress <= PROGRESS_80) {
                    seekBar.setProgress(PROGRESS_80);
                } else if (progress > PROGRESS_80 && progress <= PROGRESS_90) {
                    seekBar.setProgress(PROGRESS_90);
                } else if (progress > PROGRESS_90) {
                    seekBar.setProgress(PROGRESS_100);
                }
            }
        }
    };

    private PushConfigBottomSheet.OnPushConfigSelectorListener mOrientationListener = new PushConfigBottomSheet.OnPushConfigSelectorListener() {
        @Override
        public void confirm(String data, int index) {
            if (index == 0) {
                if (mAlivcLivePushConfig != null) {
                    mAlivcLivePushConfig.setPreviewOrientation(ORIENTATION_PORTRAIT);
                    mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push.png");
                    mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network.png");
                }
            } else if (index == 1) {
                if (mAlivcLivePushConfig != null) {
                    mAlivcLivePushConfig.setPreviewOrientation(ORIENTATION_LANDSCAPE_HOME_LEFT);
                    VideoRecordViewManager.cameraRotation = 90;
                    mAlivcLivePushConfig.setPausePushImage(getFilesDir() + File.separator + "alivc_resource/background_push_land.png");
                    mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network_land.png");
                }
            } else if (index == 2) {
                if (mAlivcLivePushConfig != null) {
                    mAlivcLivePushConfig.setPreviewOrientation(ORIENTATION_LANDSCAPE_HOME_RIGHT);
                    VideoRecordViewManager.cameraRotation = 270;
                    mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push_land.png");
                    mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network_land.png");
                }
            }
        }

    };

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            if (id == R.id.narrowband_hd) {
                mAlivcLivePushConfig.setEnableNarrowbandHDForScreenPusher(isChecked);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        VideoRecordViewManager.refreshFloatWindowPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mOrientationEventListener != null &&
                mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }

    }

    private void startCaptureActivityForResult() {
        Intent intent = new Intent(VideoRecordConfigActivity.this, CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
        bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
        bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
        bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
        bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
        bundle.putBoolean(CaptureActivity.KEY_SCAN_AREA_FULL_SCREEN, CaptureActivity.VALUE_SCAN_AREA_FULL_SCREEN);
        bundle.putBoolean(CaptureActivity.KEY_NEED_SCAN_HINT_TEXT, CaptureActivity.VALUE_SCAN_HINT_TEXT);
        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, CaptureActivity.REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    startCaptureActivityForResult();
                } else {
                    // User disagree the permission
                    Toast.makeText(this, "You must agree the camera permission request before you use the code scan function", Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
                break;
        }
    }

    private AlivcLivePushConfig getPushConfig() {
        if (mUrl.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.url_empty), Toast.LENGTH_LONG).show();
            return null;
        }
        mAlivcLivePushConfig.setResolution(mDefinition);

        return mAlivcLivePushConfig;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        mUrl.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));  //or do sth
                        break;
                    case RESULT_CANCELED:
                        if (data != null) {
                            // for some reason camera is not working correctly
                            mUrl.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }
                        break;
                    default:
                        break;
                }
                break;
            case CAPTURE_PERMISSION_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    mAlivcLivePushConfig.setMediaProjectionPermissionResultData(data);
                    if (mAlivcLivePushConfig.getMediaProjectionPermissionResultData() != null) {
                        if (mAlivcLivePusher == null) {
                            startPushWithoutSurface(mUrl.getText().toString());
                        } else {
                            stopPushWithoutSurface();
                        }
                    }
                }
            }
            break;
            case OVERLAY_PERMISSION_REQUEST_CODE:
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                if (manager == null) {
                    manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(VideoRecordConfigActivity.this, ForegroundService.class);
        stopService(intent);
        VideoRecordViewManager.removeVideoRecordCameraWindow(getApplicationContext());
        VideoRecordViewManager.removeVideoRecordWindow(getApplicationContext());
        if (mAlivcLivePusher != null) {
            try {
                mAlivcLivePusher.stopCamera();
            } catch (Exception e) {
            }
            try {
                mAlivcLivePusher.stopCameraMix();
            } catch (Exception e) {

            }
            try {
                mAlivcLivePusher.stopPush();
            } catch (Exception e) {
            }
            try {
                mAlivcLivePusher.stopPreview();
            } catch (Exception e) {
            }
            mAlivcLivePusher.destroy();
            mAlivcLivePusher.setLivePushInfoListener(null);
            mAlivcLivePusher = null;
        }
        if (mOrientationEventListener != null) {
            mOrientationEventListener.disable();
        }
        super.onDestroy();
    }

    @TargetApi(21)
    private void startScreenCapture() {
        if (Build.VERSION.SDK_INT >= 21) {
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                    getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            try {
                this.startActivityForResult(
                        mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext()
                                , "Start ScreenRecording failed, current device is NOT suuported!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(this, "录屏需要5.0版本以上", Toast.LENGTH_LONG).show();
        }
    }

    private void stopPushWithoutSurface() {
        VideoRecordViewManager.removeVideoRecordCameraWindow(getApplicationContext());
        VideoRecordViewManager.removeVideoRecordWindow(getApplicationContext());
        if (mAlivcLivePusher != null) {
            try {
                mAlivcLivePusher.stopCamera();
            } catch (Exception e) {
            }
            try {
                mAlivcLivePusher.stopCameraMix();
            } catch (Exception e) {
            }
            try {
                mAlivcLivePusher.stopPush();
            } catch (Exception e) {
            }
            try {
                mAlivcLivePusher.stopPreview();
            } catch (Exception e) {
            }
            try {
                mAlivcLivePusher.destroy();
            } catch (Exception e) {
            }

            mAlivcLivePusher.setLivePushInfoListener(null);
            mAlivcLivePusher = null;
        }
        mPushTex.setText(R.string.start_push);
        mNoteText.setText(getString(R.string.screen_note1));
        mNoteText.setVisibility(View.VISIBLE);
        mResolution.setEnabled(true);
        mNarrowBandHDConfig.setEnabled(true);
    }

    private void startPushWithoutSurface(String url) {
        mAlivcLivePusher = new AlivcLivePusher();
        try {
            mAlivcLivePusher.init(getApplicationContext(), mAlivcLivePushConfig);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        mAlivcLivePusher.setLivePushInfoListener(new AlivcLivePushInfoListener() {
            @Override
            public void onPreviewStarted(AlivcLivePusher pusher) {
                Log.d(TAG, "onPreviewStarted: ");
            }

            @Override
            public void onPreviewStoped(AlivcLivePusher pusher) {
                Log.d(TAG, "onPreviewStoped: ");
            }

            @Override
            public void onPushStarted(AlivcLivePusher pusher) {
                Log.d(TAG, "onPushStarted: ");
            }

            @Override
            public void onFirstAVFramePushed(AlivcLivePusher pusher) {
                Log.d(TAG, "onFirstAVFramePushed: ");
            }

            @Override
            public void onPushPauesed(AlivcLivePusher pusher) {
                Log.d(TAG, "onPushPauesed: ");
            }

            @Override
            public void onPushResumed(AlivcLivePusher pusher) {
                Log.d(TAG, "onPushResumed: ");
            }

            @Override
            public void onPushStoped(AlivcLivePusher pusher) {
                Log.d(TAG, "onPushStoped: ");
            }

            @Override
            public void onPushRestarted(AlivcLivePusher pusher) {
                Log.d(TAG, "onPushRestarted: ");
            }

            @Override
            public void onFirstFramePreviewed(AlivcLivePusher pusher) {
                Log.d(TAG, "onFirstFramePreviewed: ");
                mIsStartPushing = false;
            }

            @Override
            public void onDropFrame(AlivcLivePusher pusher, int countBef, int countAft) {
                Log.d(TAG, "onDropFrame: ");
            }

            @Override
            public void onAdjustBitRate(AlivcLivePusher pusher, int curBr, int targetBr) {
                Log.i(TAG, "onAdjustBitRate: ");
            }

            @Override
            public void onAdjustFps(AlivcLivePusher pusher, int curFps, int targetFps) {
                Log.d(TAG, "onAdjustFps: ");
            }

            @Override
            public void onPushStatistics(AlivcLivePusher pusher, AlivcLivePushStatsInfo statistics) {
                Log.i(TAG, "onPushStatistics: ");
            }

            @Override
            public void onSetLiveMixTranscodingConfig(AlivcLivePusher alivcLivePusher, boolean b, String s) {
                Log.d(TAG, "onSetLiveMixTranscodingConfig: ");
            }
        });

        mAlivcLivePusher.setLivePushErrorListener(new AlivcLivePushErrorListener() {
            @Override
            public void onSystemError(AlivcLivePusher livePusher, AlivcLivePushError error) {
                showDialog(getString(R.string.system_error) + error.toString());
            }

            @Override
            public void onSDKError(AlivcLivePusher livePusher, AlivcLivePushError error) {
                showDialog(getString(R.string.sdk_error) + error.toString());
            }
        });

        mAlivcLivePusher.setLivePushNetworkListener(new AlivcLivePushNetworkListener() {
            @Override
            public void onNetworkPoor(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onNetworkPoor: ");
            }

            @Override
            public void onNetworkRecovery(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onNetworkRecovery: ");
            }

            @Override
            public void onReconnectStart(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onReconnectStart: ");
            }

            @Override
            public void onConnectionLost(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onConnectionLost: ");
            }

            @Override
            public void onReconnectFail(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onReconnectFail: ");
            }

            @Override
            public void onReconnectSucceed(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onReconnectSucceed: ");
            }

            @Override
            public void onSendDataTimeout(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onSendDataTimeout: ");
            }

            @Override
            public void onConnectFail(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onConnectFail: ");
                showDialog(getResources().getString(R.string.connect_fail));
            }

            @Override
            public String onPushURLAuthenticationOverdue(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPushURLAuthenticationOverdue: ");
                return null;
            }

            @Override
            public void onSendMessage(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onSendMessage: ");
            }

            @Override
            public void onPacketsLost(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPacketsLost: ");
            }
        });

        try {
            mAlivcLivePusher.startPreview(null);
        } catch (Exception e) {
            showDialog("StartPreview failed");
            return;
        }
        try {
            mAlivcLivePusher.startPush(url);
        } catch (Exception e) {
            showDialog("startPush failed");
            return;
        }

        mPushTex.setText(R.string.stop_button);
        mNoteText.setText(getString(R.string.screen_note));
        mNoteText.setVisibility(View.VISIBLE);
        mResolution.setEnabled(false);
        mNarrowBandHDConfig.setEnabled(false);
    }

    private void showDialog(final String message) {
        if (getApplicationContext() == null || message == null) {
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mNoteText.setText(message);
            }
        });
    }
}
