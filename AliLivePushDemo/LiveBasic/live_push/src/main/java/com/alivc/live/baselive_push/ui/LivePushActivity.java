package com.alivc.live.baselive_push.ui;

import static com.alivc.live.pusher.AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT;
import static com.alivc.live.pusher.AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT;
import static com.alivc.live.pusher.AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.alivc.live.annotations.AlivcLiveMode;
import com.alivc.live.baselive_push.R;
import com.alivc.live.baselive_push.adapter.IPushController;
import com.alivc.live.commonutils.NetWorkUtils;
import com.alivc.live.commonutils.StatusBarUtil;
import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.AlivcLivePushStatsInfo;
import com.alivc.live.pusher.AlivcLivePusher;
import com.alivc.live.pusher.AlivcPreviewOrientationEnum;
import com.alivc.live.pusher.SurfaceStatus;
import com.alivc.live.pusher.WaterMarkInfo;
import com.aliyun.aio.avtheme.AVBaseThemeActivity;

import java.util.ArrayList;
import java.util.List;

public class LivePushActivity extends AVBaseThemeActivity implements IPushController {
    private static final String TAG = "LivePushActivity";
    private static final int FLING_MIN_DISTANCE = 50;
    private static final int FLING_MIN_VELOCITY = 0;
    private final long REFRESH_INTERVAL = 1000;
    private static final String URL_KEY = "url_key";
    private static final String ASYNC_KEY = "async_key";
    private static final String AUDIO_ONLY_KEY = "audio_only_key";
    private static final String VIDEO_ONLY_KEY = "video_only_key";
    private static final String ORIENTATION_KEY = "orientation_key";
    private static final String CAMERA_ID = "camera_id";
    private static final String FLASH_ON = "flash_on";
    private static final String AUTH_TIME = "auth_time";
    private static final String PRIVACY_KEY = "privacy_key";
    private static final String MIX_EXTERN = "mix_extern";
    private static final String MIX_MAIN = "mix_main";
    private static final String BEAUTY_CHECKED = "beauty_checked";
    private static final String FPS = "fps";
    private static final String WATER_MARK_INFOS = "water_mark";
    public static final int REQ_CODE_PUSH = 0x1112;
    public static final int CAPTURE_PERMISSION_REQUEST_CODE = 0x1123;

    // 互动模式下的预览view
    private FrameLayout mPreviewContainer;
    // 普通模式（默认）下的预览view
    private SurfaceView mPreviewView;
    private ViewPager mViewPager;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private FragmentAdapter mFragmentAdapter;

    private GestureDetector mDetector;
    private ScaleGestureDetector mScaleDetector;
    private LivePushFragment mLivePushFragment;
    private AlivcLivePushConfig mAlivcLivePushConfig;

    private static AlivcLivePusher mAlivcLivePusher = null;
    private String mPushUrl = null;

    private boolean mAsync = false;
    private boolean mAudioOnly = false;
    private boolean mVideoOnly = false;
    private int mOrientation = ORIENTATION_PORTRAIT.ordinal();

    private SurfaceStatus mSurfaceStatus = SurfaceStatus.UNINITED;
    //    private Handler mHandler = new Handler();
    private boolean isPause = false;

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private boolean mFlash = false;
    private boolean mMixExtern = false;
    private boolean mMixMain = false;
    private boolean mBeautyOn = true;
    AlivcLivePushStatsInfo alivcLivePushStatsInfo = null;
    private String mAuthTime = "";
    private String mPrivacyKey = "";

    //    private ConnectivityChangedReceiver mChangedReceiver = new ConnectivityChangedReceiver();

    private static int mNetWork = 0;
    private int mFps;

    private LivePushViewModel mLivePushViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLivePushViewModel = new ViewModelProvider(this).get(LivePushViewModel.class);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        StatusBarUtil.translucent(this, Color.TRANSPARENT);
        mPushUrl = getIntent().getStringExtra(URL_KEY);
        mAsync = getIntent().getBooleanExtra(ASYNC_KEY, false);
        mAudioOnly = getIntent().getBooleanExtra(AUDIO_ONLY_KEY, false);
        mVideoOnly = getIntent().getBooleanExtra(VIDEO_ONLY_KEY, false);
        mOrientation = getIntent().getIntExtra(ORIENTATION_KEY, ORIENTATION_PORTRAIT.ordinal());
        mCameraId = getIntent().getIntExtra(CAMERA_ID, Camera.CameraInfo.CAMERA_FACING_FRONT);
        mFlash = getIntent().getBooleanExtra(FLASH_ON, false);
        mAuthTime = getIntent().getStringExtra(AUTH_TIME);
        mPrivacyKey = getIntent().getStringExtra(PRIVACY_KEY);
        mMixExtern = getIntent().getBooleanExtra(MIX_EXTERN, false);
        mMixMain = getIntent().getBooleanExtra(MIX_MAIN, false);
        mBeautyOn = getIntent().getBooleanExtra(BEAUTY_CHECKED, true);
        mFps = getIntent().getIntExtra(FPS, 0);
        mAlivcLivePushConfig = (AlivcLivePushConfig) getIntent().getSerializableExtra(AlivcLivePushConfig.CONFIG);
        ArrayList<WaterMarkInfo> waterMarkInfos = (ArrayList<WaterMarkInfo>) getIntent().getSerializableExtra(WATER_MARK_INFOS);

        mLivePushViewModel.initReadFile(mAlivcLivePushConfig);

        setOrientation(mOrientation);
        setContentView(R.layout.activity_push);
        initView();

        if (mAlivcLivePushConfig.getLivePushMode() == AlivcLiveMode.AlivcLiveInteractiveMode) {
            // 同一进程下，只能设置一次；如需修改，需要杀进程再设置
            mAlivcLivePushConfig.setH5CompatibleMode(true);
        }
        mAlivcLivePusher = new AlivcLivePusher();
        try {
            mAlivcLivePusher.init(getApplicationContext(), mAlivcLivePushConfig);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            showDialog(this, e.getMessage());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            showDialog(this, e.getMessage());
        }

        if (mAlivcLivePushConfig.getLivePushMode() == AlivcLiveMode.AlivcLiveInteractiveMode) {
            mAlivcLivePusher.startPreview(this, mPreviewContainer, true);
        }

        mLivePushFragment = LivePushFragment.newInstance(mAlivcLivePushConfig, mPushUrl, mAsync, mAudioOnly, mVideoOnly, mCameraId, mFlash, mAlivcLivePushConfig.getQualityMode().getQualityMode(),
                mAuthTime, mPrivacyKey, mMixExtern, mMixMain, mBeautyOn, mFps, mOrientation, waterMarkInfos);
        initViewPager();
        mScaleDetector = new ScaleGestureDetector(getApplicationContext(), mScaleGestureDetector);
        mDetector = new GestureDetector(getApplicationContext(), mGestureDetector);
        mNetWork = NetWorkUtils.getAPNType(this);
    }

    public void initView() {
        if (mAlivcLivePushConfig.getLivePushMode() == AlivcLiveMode.AlivcLiveInteractiveMode) {
            //互动模式需要 FrameLayout
            mPreviewContainer = findViewById(R.id.preview_container);
            mPreviewContainer.setVisibility(View.VISIBLE);
        } else {
            mPreviewView = (SurfaceView) findViewById(R.id.preview_view);
            mPreviewView.getHolder().addCallback(mCallback);
            mPreviewView.setVisibility(View.VISIBLE);
        }
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.tv_pager);
        mFragmentList.add(mLivePushFragment);
        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getPointerCount() >= 2 && mScaleDetector != null) {
                    mScaleDetector.onTouchEvent(motionEvent);
                } else if (motionEvent.getPointerCount() == 1 && mDetector != null) {
                    mDetector.onTouchEvent(motionEvent);
                }
//                }
                return false;
            }
        });
    }

    private void setOrientation(int orientation) {
        if (orientation == ORIENTATION_PORTRAIT.ordinal()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (orientation == ORIENTATION_LANDSCAPE_HOME_RIGHT.ordinal()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (orientation == ORIENTATION_LANDSCAPE_HOME_LEFT.ordinal()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
    }

    private GestureDetector.OnGestureListener mGestureDetector = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            float x = 0f;
            float y = 0f;
            if (mAlivcLivePushConfig.getLivePushMode() == AlivcLiveMode.AlivcLiveInteractiveMode) {
                if (mPreviewContainer.getWidth() > 0 && mPreviewContainer.getHeight() > 0) {
                    x = motionEvent.getX() / mPreviewContainer.getWidth();
                    y = motionEvent.getY() / mPreviewContainer.getHeight();
                }
            } else {
                if (mPreviewView.getWidth() > 0 && mPreviewView.getHeight() > 0) {
                    x = motionEvent.getX() / mPreviewView.getWidth();
                    y = motionEvent.getY() / mPreviewView.getHeight();
                }
            }
            try {
                mAlivcLivePusher.focusCameraAtAdjustedPoint(x, y, true);
            } catch (IllegalStateException e) {

            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            if (motionEvent == null || motionEvent1 == null) {
                return false;
            }
            if (motionEvent.getX() - motionEvent1.getX() > FLING_MIN_DISTANCE
                    && Math.abs(v) > FLING_MIN_VELOCITY) {
                // Fling left
            } else if (motionEvent1.getX() - motionEvent.getX() > FLING_MIN_DISTANCE
                    && Math.abs(v) > FLING_MIN_VELOCITY) {
                // Fling right
            }
            return false;
        }
    };

    private float scaleFactor = 1.0f;
    private ScaleGestureDetector.OnScaleGestureListener mScaleGestureDetector = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            if (scaleGestureDetector.getScaleFactor() > 1) {
                scaleFactor += 0.5;
            } else {
                scaleFactor -= 2;
            }
            if (scaleFactor <= 1) {
                scaleFactor = 1;
            }
            try {
                if (scaleFactor >= mAlivcLivePusher.getMaxZoom()) {
                    scaleFactor = mAlivcLivePusher.getMaxZoom();
                }
                mAlivcLivePusher.setZoom((int) scaleFactor);

            } catch (IllegalStateException e) {

            }
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

        }
    };

    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            if (mSurfaceStatus == SurfaceStatus.UNINITED) {
                mSurfaceStatus = SurfaceStatus.CREATED;
                mLivePushViewModel.onSurfaceCreated(mAsync, mPreviewView, mAlivcLivePusher);
            } else if (mSurfaceStatus == SurfaceStatus.DESTROYED) {
                mSurfaceStatus = SurfaceStatus.RECREATED;
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            mSurfaceStatus = SurfaceStatus.CHANGED;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            mSurfaceStatus = SurfaceStatus.DESTROYED;
        }
    };

    public static void startActivity(Activity activity, AlivcLivePushConfig alivcLivePushConfig, String url, boolean async,
                                     boolean audioOnly, boolean videoOnly, AlivcPreviewOrientationEnum orientation, int cameraId, boolean isFlash,
                                     String authTime, String privacyKey, boolean mixExtern, boolean mixMain, boolean ischecked, int fps,
                                     ArrayList<WaterMarkInfo> waterMarkInfos) {
        Intent intent = new Intent(activity, LivePushActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AlivcLivePushConfig.CONFIG, alivcLivePushConfig);
        bundle.putString(URL_KEY, url);
        bundle.putBoolean(ASYNC_KEY, async);
        bundle.putBoolean(AUDIO_ONLY_KEY, audioOnly);
        bundle.putBoolean(VIDEO_ONLY_KEY, videoOnly);
        bundle.putInt(ORIENTATION_KEY, orientation.ordinal());
        bundle.putInt(CAMERA_ID, cameraId);
        bundle.putBoolean(FLASH_ON, isFlash);
        bundle.putString(AUTH_TIME, authTime);
        bundle.putString(PRIVACY_KEY, privacyKey);
        bundle.putBoolean(MIX_EXTERN, mixExtern);
        bundle.putBoolean(MIX_MAIN, mixMain);
        bundle.putBoolean(BEAUTY_CHECKED, ischecked);
        bundle.putInt(FPS, fps);
        bundle.putSerializable(WATER_MARK_INFOS, waterMarkInfos);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, REQ_CODE_PUSH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAlivcLivePusher != null) {
            try {
                if (isPause) {
                    if (mAsync) {
                        mAlivcLivePusher.resumeAsync();
                    } else {
                        mAlivcLivePusher.resume();
                    }
                    mLivePushFragment.updateOperaButtonState(true);
                    isPause = false;
                    mAlivcLivePusher.resumeBGM();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAlivcLivePusher != null) {
            try {
                if (!isPause) {
                    mAlivcLivePusher.pause();
                    isPause = true;
                }
                if (mLivePushFragment.isBGMPlaying()) {
                    mAlivcLivePusher.pauseBGM();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mLivePushViewModel.stopYUV();
        mLivePushViewModel.stopPcm();
        if (mAlivcLivePusher != null) {
            try {
                mAlivcLivePusher.destroy();
                mAlivcLivePusher = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        mFragmentList = null;
        mPreviewView = null;
        mViewPager = null;
        mFragmentAdapter = null;
        mDetector = null;
        mScaleDetector = null;
        mLivePushFragment = null;
        mAlivcLivePushConfig = null;
        mAlivcLivePusher = null;
        alivcLivePushStatsInfo = null;
        super.onDestroy();
    }

    public class FragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<>();

        public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        AlivcPreviewOrientationEnum orientationEnum;
        if (mAlivcLivePusher != null) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientationEnum = ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientationEnum = ORIENTATION_LANDSCAPE_HOME_RIGHT;
                    break;
                case Surface.ROTATION_270:
                    orientationEnum = ORIENTATION_LANDSCAPE_HOME_LEFT;
                    break;
                default:
                    orientationEnum = ORIENTATION_PORTRAIT;
                    break;
            }
            try {
                mAlivcLivePusher.setPreviewOrientation(orientationEnum);
            } catch (IllegalStateException e) {

            }
        }
    }

    @Override
    public AlivcLivePusher getLivePusher() {
        return this.mAlivcLivePusher;
    }

    @Override
    public PauseState getPauseStateListener() {
        return this.mStateListener;
    }

    @Override
    public SurfaceView getPreviewView() {
        return this.mPreviewView;
    }

    private void showDialog(Context context, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(getString(R.string.dialog_title));
        dialog.setMessage(message);
        dialog.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.show();
    }

    public interface PauseState {
        void updatePause(boolean state);
    }

    private PauseState mStateListener = new PauseState() {
        @Override
        public void updatePause(boolean state) {
            isPause = state;
        }
    };

    public static class ConnectivityChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                if (mNetWork != NetWorkUtils.getAPNType(context)) {
                    mNetWork = NetWorkUtils.getAPNType(context);
                    if (mAlivcLivePusher != null) {
                        if (mAlivcLivePusher.isPushing()) {
                            try {
                                mAlivcLivePusher.reconnectPushAsync(null);
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        }
    }
}
