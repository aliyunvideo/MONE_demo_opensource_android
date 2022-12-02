package com.alivc.live.pusher.demo;

import static com.alivc.live.pusher.AlivcLivePushCameraTypeEnum.CAMERA_TYPE_BACK;
import static com.alivc.live.pusher.AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.alivc.component.custom.AlivcLivePushCustomFilter;
import com.alivc.live.annotations.AlivcLiveMode;
import com.alivc.live.beauty.BeautyFactory;
import com.alivc.live.beauty.BeautyInterface;
import com.alivc.live.beauty.constant.BeautySDKType;
import com.alivc.live.beautyui.AnimojiContainerView;
import com.alivc.live.beautyui.bean.AnimojiItemBean;
import com.alivc.live.pusher.AlivcLivePushAudioEffectReverbMode;
import com.alivc.live.pusher.AlivcLivePushAudioEffectVoiceChangeMode;
import com.alivc.live.pusher.AlivcLivePushBGMListener;
import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.AlivcLivePushError;
import com.alivc.live.pusher.AlivcLivePushErrorListener;
import com.alivc.live.pusher.AlivcLivePushInfoListener;
import com.alivc.live.pusher.AlivcLivePushNetworkListener;
import com.alivc.live.pusher.AlivcLivePushStatsInfo;
import com.alivc.live.pusher.AlivcLivePusher;
import com.alivc.live.pusher.AlivcPreviewDisplayMode;
import com.alivc.live.pusher.AlivcPreviewOrientationEnum;
import com.alivc.live.pusher.AlivcSnapshotListener;
import com.alivc.live.pusher.WaterMarkInfo;
import com.alivc.live.pusher.demo.adapter.OnSoundEffectChangedListener;
import com.alivc.live.pusher.widget.CommonDialog;
import com.alivc.live.pusher.widget.DataView;
import com.alivc.live.pusher.widget.PushMoreConfigBottomSheet;
import com.alivc.live.pusher.widget.PushMusicBottomSheet;
import com.alivc.live.pusher.widget.SoundEffectView;
import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.animoji.AnimojiDataFactory;
import com.aliyun.animoji.AnimojiEngine;
import com.aliyun.animoji.AnimojiError;
import com.aliyun.animoji.Flip;
import com.aliyun.animoji.utils.DeviceOrientationDetector;
import com.alivc.live.utils.FastClickUtil;
import com.alivc.live.commonbiz.SharedPreferenceUtils;
import com.alivc.live.utils.TextFormatUtil;
import com.alivc.live.utils.ToastUtils;
import com.aliyunsdk.queen.menu.BeautyMenuPanel;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class LivePushFragment extends Fragment {
    public static final String TAG = "LivePushFragment";

    private static final String PUSH_CONFIG = "push_config";
    private static final String URL_KEY = "url_key";
    private static final String ASYNC_KEY = "async_key";
    private static final String AUDIO_ONLY_KEY = "audio_only_key";
    private static final String VIDEO_ONLY_KEY = "video_only_key";
    private static final String QUALITY_MODE_KEY = "quality_mode_key";
    private static final String CAMERA_ID = "camera_id";
    private static final String FLASH_ON = "flash_on";
    private static final String AUTH_TIME = "auth_time";
    private static final String PRIVACY_KEY = "privacy_key";
    private static final String MIX_EXTERN = "mix_extern";
    private static final String MIX_MAIN = "mix_main";
    private static final String BEAUTY_CHECKED = "beauty_checked";
    private static final String FPS = "fps";
    private static final String PREVIEW_ORIENTATION = "preview_orientation";
    private static final String WATER_MARK_INFOS = "water_mark";
    private ImageView mExit;
    private View mMusic;
    private View mFlash;
    private View mCamera;
    private View mSnapshot;
    private View mBeautyButton;
    private View mAnimojiButton;
    private View mSoundEffectButton;

    private TextView mPreviewButton;
    private TextView mPushButton;
    private TextView mOperaButton;
    private TextView mMore;
    private TextView mRestartButton;
    private TextView mDataButton;

    private AlivcLivePushConfig mAlivcLivePushConfig = null;
    private String mPushUrl = null;
    private boolean mAsync = false;

    private boolean mAudio = false;
    private boolean mVideoOnly = false;

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private boolean isFlash = false;
    private boolean mMixExtern = false;
    private boolean mMixMain = false;
    private boolean flashState = true;

    private int snapshotCount = 0;

    private int mQualityMode = 0;

    ScheduledExecutorService mExecutorService = new ScheduledThreadPoolExecutor(5,
            new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
    private boolean audioThreadOn = false;
    private boolean mIsStartAsnycPushing = false;

    private PushMusicBottomSheet mMusicDialog = null;

    private String mAuthString = "?auth_key=%1$d-%2$d-%3$d-%4$s";
    private String mMd5String = "%1$s-%2$d-%3$d-%4$d-%5$s";
    private String mTempUrl = null;
    private String mAuthTime = "";
    private String mPrivacyKey = "";
    private TextView mStatusTV;
    private LinearLayout mActionBar;
    Vector<Integer> mDynamicals = new Vector<>();
    // 高级美颜管理类
    private BeautyInterface mBeautyManager;
    private boolean isBeautyEnable = true;
    private AnimojiContainerView mAnimojiContainerView;
    private AnimojiEngine mAnimojiEngine;
    private DataView mDataView;
    private int mCurBr;
    private int mTargetBr;
    private boolean mBeautyOn = true;
    private boolean mAnimojiOn = false;
    private int mFps;
    private int mPreviewOrientation;
    private CommonDialog mDialog;
    private AlivcLivePushStatsInfo mPushStatsInfo;
    private boolean isConnectResult = false;//是否正在链接中
    private TextView mTotalLivePushStatsInfoTV;//数据指标
    private BeautyMenuPanel mBeautyBeautyContainerView;
    private IPushController mPushController = null;
    private final DeviceOrientationDetector mDeviceOrientationDetector = new DeviceOrientationDetector();
    private int mDeviceOrientation = 0;
    private ArrayList<WaterMarkInfo> waterMarkInfos = null;
    //音效
    private SoundEffectView mSoundEffectView;

    public static LivePushFragment newInstance(AlivcLivePushConfig livePushConfig, String url, boolean async, boolean mAudio, boolean mVideoOnly, int cameraId,
                                               boolean isFlash, int mode, String authTime, String privacyKey, boolean mixExtern,
                                               boolean mixMain, boolean beautyOn, int fps, int previewOrientation,
                                               ArrayList<WaterMarkInfo> waterMarkInfos) {
        LivePushFragment livePushFragment = new LivePushFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PUSH_CONFIG, livePushConfig);
        bundle.putString(URL_KEY, url);
        bundle.putBoolean(ASYNC_KEY, async);
        bundle.putBoolean(AUDIO_ONLY_KEY, mAudio);
        bundle.putBoolean(VIDEO_ONLY_KEY, mVideoOnly);
        bundle.putInt(QUALITY_MODE_KEY, mode);
        bundle.putInt(CAMERA_ID, cameraId);
        bundle.putBoolean(FLASH_ON, isFlash);
        bundle.putString(AUTH_TIME, authTime);
        bundle.putString(PRIVACY_KEY, privacyKey);
        bundle.putBoolean(MIX_EXTERN, mixExtern);
        bundle.putBoolean(MIX_MAIN, mixMain);
        bundle.putBoolean(BEAUTY_CHECKED, beautyOn);
        bundle.putInt(FPS, fps);
        bundle.putInt(PREVIEW_ORIENTATION, previewOrientation);
        bundle.putSerializable(WATER_MARK_INFOS, waterMarkInfos);
        livePushFragment.setArguments(bundle);
        return livePushFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof IPushController) {
            mPushController = (IPushController) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fragment may be recreated, so move all init logic to onActivityCreated
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AlivcLivePusher pusher = mPushController.getLivePusher();
        if (getArguments() != null) {
            mAlivcLivePushConfig = (AlivcLivePushConfig) getArguments().getSerializable(PUSH_CONFIG);
            mPushUrl = getArguments().getString(URL_KEY);
            mTempUrl = mPushUrl;
            mAsync = getArguments().getBoolean(ASYNC_KEY, false);
            mAudio = getArguments().getBoolean(AUDIO_ONLY_KEY, false);
            mVideoOnly = getArguments().getBoolean(VIDEO_ONLY_KEY, false);
            mCameraId = getArguments().getInt(CAMERA_ID);
            isFlash = getArguments().getBoolean(FLASH_ON, false);
            mMixExtern = getArguments().getBoolean(MIX_EXTERN, false);
            mMixMain = getArguments().getBoolean(MIX_MAIN, false);
            mQualityMode = getArguments().getInt(QUALITY_MODE_KEY);
            mAuthTime = getArguments().getString(AUTH_TIME);
            mPrivacyKey = getArguments().getString(PRIVACY_KEY);
            mBeautyOn = getArguments().getBoolean(BEAUTY_CHECKED);
            mFps = getArguments().getInt(FPS);
            mPreviewOrientation = getArguments().getInt(PREVIEW_ORIENTATION);
            waterMarkInfos = (ArrayList<WaterMarkInfo>) getArguments().getSerializable(WATER_MARK_INFOS);
            flashState = isFlash;
        }
        if (pusher != null) {
            pusher.setLivePushInfoListener(mPushInfoListener);
            pusher.setLivePushErrorListener(mPushErrorListener);
            pusher.setLivePushNetworkListener(mPushNetworkListener);
            pusher.setLivePushBGMListener(mPushBGMListener);
        }

        mDeviceOrientationDetector.initDeviceDetector(getContext(), orientation -> {
            mDeviceOrientation = orientation;
        });

        // Only when device level matches, can use animoji feature.
        boolean isDeviceSupportAnimoji = AnimojiEngine.isDeviceSupported(getContext());
        mAnimojiOn = SharedPreferenceUtils.isAnimojiOn(getContext()) && isDeviceSupportAnimoji;
        initAnimojiEngine(getContext());
        mAnimojiButton.setVisibility(mAnimojiOn ? View.VISIBLE : View.GONE);

        if (pusher != null && (mBeautyOn || mAnimojiOn)) {
            pusher.setCustomFilter(new AlivcLivePushCustomFilter() {
                @Override
                public void customFilterCreate(long var1) {
                    Log.d(TAG, "customFilterCreate start-" + var1);

                    initBeautyManager(var1);
                    initAnimojiEngine(getContext());

                    Log.d(TAG, "customFilterCreate end");
                }

                @Override
                public int customFilterProcess(int inputTexture, int textureWidth, int textureHeight, float[] textureMatrix, boolean isOES, long extra) {
                    if (mBeautyManager == null) {
                        return inputTexture;
                    }

                    int ret = inputTexture;
                    if (mAlivcLivePushConfig != null && mAlivcLivePushConfig.getLivePushMode() == AlivcLiveMode.AlivcLiveInteractiveMode) {
                        ret = mBeautyManager.onTextureInput(inputTexture, textureWidth, textureHeight, textureMatrix, isOES);
                    } else {
                        ret = mBeautyManager.onTextureInput(inputTexture, textureWidth, textureHeight, textureMatrix, false);

                        // todo keria, 互动模式下，不处理animoji相关...
                        // should do in texture thread!!!
                        if (null != mAnimojiEngine) {
                            if (mDeviceOrientation % 180 == 0) {
                                mAnimojiEngine.setParams(180 - mDeviceOrientation, 180 - mDeviceOrientation, Flip.NONE);
                            } else {
                                mAnimojiEngine.setParams(mDeviceOrientation, mDeviceOrientation, Flip.NONE);
                            }
                            mAnimojiEngine.process(textureWidth, textureHeight, ret, 0);
                        }

                        // should do in texture thread!!!
                        if (null != mAnimojiEngine) {
                            if (mDeviceOrientation % 180 == 0) {
                                mAnimojiEngine.setParams(180 - mDeviceOrientation, 180 - mDeviceOrientation, Flip.NONE);
                            } else {
                                mAnimojiEngine.setParams(mDeviceOrientation, mDeviceOrientation, Flip.NONE);
                            }
                            mAnimojiEngine.process(textureWidth, textureHeight, ret, 0);
                        }
                    }

                    return ret;
                }

                @Override
                public void customFilterDestroy() {
                    destroyBeautyManager();
                    if (mAnimojiButton.getVisibility() == View.VISIBLE) {
                        destroyAnimojiEngine();
                    }
                    Log.d(TAG, "customFilterDestroy---> thread_id: " + Thread.currentThread().getId());
                }
            });
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.push_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBeautyBeautyContainerView = view.findViewById(R.id.beauty_beauty_menuPanel);
        mBeautyBeautyContainerView.onHideMenu();
        mSoundEffectView = view.findViewById(R.id.sound_effect_view);
        mAnimojiContainerView = view.findViewById(R.id.animoji_container_view);
        mAnimojiContainerView.initData(getAnimojiItemBeans(getContext()));

        mSoundEffectView.setOnSoundEffectChangedListener(new OnSoundEffectChangedListener() {
            @Override
            public void onSoundEffectChangeVoiceModeSelected(int position) {
                if (position >= 0 && position < AlivcLivePushAudioEffectVoiceChangeMode.values().length) {
                    AlivcLivePusher livePusher = mPushController.getLivePusher();
                    Log.d(TAG, "onSoundEffectChangeVoiceModeSelected: " + position + " --- " + AlivcLivePushAudioEffectVoiceChangeMode.values()[position]);
                    livePusher.setAudioEffectVoiceChangeMode(AlivcLivePushAudioEffectVoiceChangeMode.values()[position]);
                }
            }

            @Override
            public void onSoundEffectRevertBSelected(int position) {
                if (position >= 0 && position < AlivcLivePushAudioEffectReverbMode.values().length) {
                    AlivcLivePusher livePusher = mPushController.getLivePusher();
                    Log.d(TAG, "onSoundEffectRevertBSelected: " + position + " --- " + AlivcLivePushAudioEffectReverbMode.values()[position]);
                    livePusher.setAudioEffectReverbMode(AlivcLivePushAudioEffectReverbMode.values()[position]);
                }
            }
        });
        mAnimojiContainerView.setCallback(new AnimojiContainerView.AnimojiContainerViewCallback() {
            @Override
            public void onItemClicked(@Nullable AnimojiItemBean bean) {
                if (null != bean && null != mAnimojiEngine) {
                    mAnimojiEngine.setMaterialPath(bean.getPath());
                }
            }

            @Override
            public void onTabPutAway() {
                changeAnimojiContainerVisibility();
            }

            @Override
            public void onTabReset() {
                if (null != mAnimojiEngine) {
                    mAnimojiEngine.setMaterialPath(null);
                }
            }
        });
        mDataButton = (TextView) view.findViewById(R.id.data);
        mDataView = (DataView) view.findViewById(R.id.ll_data);
        mDataView.setVisibility(View.GONE);
        mStatusTV = (TextView) view.findViewById(R.id.tv_status);
        mExit = (ImageView) view.findViewById(R.id.exit);
        mMusic = view.findViewById(R.id.music);
        mFlash = view.findViewById(R.id.flash);
        mFlash.setSelected(isFlash);
        mCamera = view.findViewById(R.id.camera);
        mSnapshot = view.findViewById(R.id.snapshot);
        mActionBar = (LinearLayout) view.findViewById(R.id.action_bar);
        mCamera.setSelected(true);
        mSnapshot.setSelected(true);
        mPreviewButton = view.findViewById(R.id.preview_button);
        mPreviewButton.setSelected(true);
        mPushButton = view.findViewById(R.id.push_button);
        mPushButton.setSelected(false);
        mOperaButton = view.findViewById(R.id.opera_button);
        mOperaButton.setSelected(false);
        mMore = (TextView) view.findViewById(R.id.more);
        mBeautyButton = view.findViewById(R.id.beauty_button);
        mBeautyButton.setSelected(SharedPreferenceUtils.isBeautyOn(getActivity().getApplicationContext()));
        mSoundEffectButton = view.findViewById(R.id.sound_effect_button);
        mAnimojiButton = view.findViewById(R.id.animoji_button);
        mAnimojiButton.setSelected(SharedPreferenceUtils.isAnimojiOn(getActivity().getApplicationContext()));
        mRestartButton = (TextView) view.findViewById(R.id.restart_button);
        mTotalLivePushStatsInfoTV = ((TextView) mDataView.findViewById(R.id.tv_data));
        mExit.setOnClickListener(onClickListener);
        mMusic.setOnClickListener(onClickListener);
        mFlash.setOnClickListener(onClickListener);

        mCamera.setOnClickListener(onClickListener);
        mSnapshot.setOnClickListener(onClickListener);
        mPreviewButton.setOnClickListener(onClickListener);
        mSoundEffectButton.setOnClickListener(onClickListener);
        mPushButton.setOnClickListener(onClickListener);
        mOperaButton.setOnClickListener(onClickListener);
        mBeautyButton.setOnClickListener(onClickListener);
        mAnimojiButton.setOnClickListener(onClickListener);
        mRestartButton.setOnClickListener(onClickListener);
        mMore.setOnClickListener(onClickListener);
        mDataButton.setOnClickListener(onClickListener);

        if (mVideoOnly) {
            mMusic.setVisibility(View.GONE);
        }
        if (mAudio) {
            mPreviewButton.setVisibility(View.GONE);
        }
        if (mMixMain) {
            mBeautyButton.setVisibility(View.GONE);
            mAnimojiButton.setVisibility(View.GONE);
            mMusic.setVisibility(View.GONE);
            mFlash.setVisibility(View.GONE);
            mCamera.setVisibility(View.GONE);
        }
        mMore.setVisibility(mAudio ? View.GONE : View.VISIBLE);
        mBeautyButton.setVisibility(mAudio ? View.GONE : View.VISIBLE);
        mAnimojiButton.setVisibility(isSupportAnimoji(getContext()) ? View.VISIBLE : View.GONE);
        mFlash.setVisibility(mAudio ? View.GONE : View.VISIBLE);
        mCamera.setVisibility(mAudio ? View.GONE : View.VISIBLE);
        mFlash.setClickable(mCameraId != CAMERA_TYPE_FRONT.getCameraId());
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mPushButton.isSelected() && !isConnectResult) {
                        showDialog(getSafeString(R.string.connecting_dialog_tips));
                    } else {
                        getActivity().finish();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int id = view.getId();
            AlivcLivePusher pusher = mPushController.getLivePusher();
            if (pusher == null) {
                return;
            }

            if (id == R.id.music) {
                if (!mAlivcLivePushConfig.isVideoOnly()) {
                    if (mMusicDialog == null) {
                        mMusicDialog = new PushMusicBottomSheet(getContext());
                        mMusicDialog.setOnMusicSelectListener(new PushMusicBottomSheet.OnMusicSelectListener() {
                            @Override
                            public void onBGMEarsBack(boolean state) {
                                pusher.setBGMEarsBack(state);
                            }

                            @Override
                            public void onAudioNoise(boolean state) {
                                pusher.setAudioDenoise(state);
                            }

                            @Override
                            public void onBGPlay(boolean state) {
                                if (state) {
                                    pusher.resumeBGM();
                                } else {
                                    pusher.pauseBGM();
                                }
                            }

                            @Override
                            public void onBgResource(String path) {
                                if (!TextUtils.isEmpty(path)) {
                                    pusher.startBGMAsync(path);
                                } else {
                                    try {
                                        pusher.stopBGMAsync();
                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onBGLoop(boolean state) {
                                pusher.setBGMLoop(state);
                            }

                            @Override
                            public void onMute(boolean state) {
                                pusher.setMute(state);
                            }

                            @Override
                            public void onCaptureVolume(int progress) {
                                pusher.setCaptureVolume(progress);
                            }

                            @Override
                            public void onBGMVolume(int progress) {
                                pusher.setBGMVolume(progress);
                            }
                        });
                    }
                    mMusicDialog.show();
                }
                return;
            }


            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    LivePushActivity.PauseState stateListener = mPushController.getPauseStateListener();
                    try {
                        if (id == R.id.exit) {
                            if (mPushButton.isSelected() && !isConnectResult) {
                                showDialog(getSafeString(R.string.connecting_dialog_tips));
                                return;
                            }
                            getActivity().finish();
                        } else if (id == R.id.flash) {
                            pusher.setFlash(!mFlash.isSelected());
                            flashState = !mFlash.isSelected();
                            mFlash.post(new Runnable() {
                                @Override
                                public void run() {
                                    mFlash.setSelected(!mFlash.isSelected());
                                }
                            });
                        } else if (id == R.id.camera) {
                            if (mCameraId == CAMERA_TYPE_FRONT.getCameraId()) {
                                mCameraId = CAMERA_TYPE_BACK.getCameraId();
                            } else {
                                mCameraId = CAMERA_TYPE_FRONT.getCameraId();
                            }
                            pusher.switchCamera();
                            if (mBeautyManager != null) {
                                mBeautyManager.switchCameraId(mCameraId);
                            }
                            mFlash.post(new Runnable() {
                                @Override
                                public void run() {
                                    mFlash.setClickable(mCameraId != CAMERA_TYPE_FRONT.getCameraId());
                                    if (mCameraId == CAMERA_TYPE_FRONT.getCameraId()) {
                                        mFlash.setSelected(false);
                                    } else {
                                        mFlash.setSelected(flashState);
                                    }
                                }
                            });
                        } else if (id == R.id.preview_button) {
                            if (FastClickUtil.isFastClick()) {
                                return;//点击间隔 至少1秒
                            }
                            final boolean isPreview = mPreviewButton.isSelected();
                            if (isPreview) {
                                pusher.stopPreview();
                            } else {
                                SurfaceView previewView = mPushController.getPreviewView();
                                if (mAsync) {
                                    pusher.startPreviewAysnc(previewView);
                                } else {
                                    pusher.startPreview(previewView);
                                }
                            }

                            mPreviewButton.post(new Runnable() {
                                @Override
                                public void run() {
                                    mPreviewButton.setText(isPreview ? getSafeString(R.string.start_preview_button) : getSafeString(R.string.stop_preview_button));
                                    mPreviewButton.setSelected(!isPreview);
                                }
                            });
                        } else if (id == R.id.push_button) {
                            final boolean isPush = mPushButton.isSelected();
                            if (!isPush) {
                                if (mAsync) {
                                    pusher.startPushAysnc(mPushUrl);
                                } else {
                                    pusher.startPush(mPushUrl);
                                }
                            } else {
                                pusher.stopPush();
                                stopPcm();
                                mOperaButton.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mOperaButton.setText(getSafeString(R.string.pause_button));
                                        mOperaButton.setSelected(false);
                                    }
                                });
                                if (stateListener != null) {
                                    stateListener.updatePause(false);
                                }
                            }

                            mPushButton.post(new Runnable() {
                                @Override
                                public void run() {
                                    mStatusTV.setText(isPush ? getSafeString(R.string.wating_push) : getSafeString(R.string.pushing));
                                    mPushButton.setText(isPush ? getSafeString(R.string.start_push) : getSafeString(R.string.stop_button));
                                    mPushButton.setSelected(!isPush);
                                }
                            });
                        } else if (id == R.id.opera_button) {
                            final boolean isPause = mOperaButton.isSelected();
                            if (!isPause) {
                                pusher.pause();
                            } else {
                                if (mAsync) {
                                    pusher.resumeAsync();
                                } else {
                                    pusher.resume();
                                }
                            }

                            if (stateListener != null) {
                                stateListener.updatePause(!isPause);
                            }
                            updateOperaButtonState(isPause);
                        } else if (id == R.id.animoji_button) {
                            mAnimojiButton.post(() -> {
                                if (mBeautyBeautyContainerView.getVisibility() == View.VISIBLE) {
                                    changeBeautyContainerVisibility();
                                }
                                if (mSoundEffectView.getVisibility() == View.VISIBLE) {
                                    changeSoundEffectVisibility();
                                }
                                changeAnimojiContainerVisibility();
                            });
                        } else if (id == R.id.sound_effect_button) {
                            if (!mAlivcLivePushConfig.isVideoOnly()) {
                                mSoundEffectView.post(() -> {
                                    if (mBeautyBeautyContainerView.getVisibility() == View.VISIBLE) {
                                        changeBeautyContainerVisibility();
                                    }
                                    if (mAnimojiContainerView.getVisibility() == View.VISIBLE) {
                                        changeAnimojiContainerVisibility();
                                    }
                                    changeSoundEffectVisibility();
                                });
                            }
                        } else if (id == R.id.beauty_button) {
                            if (!mBeautyOn) {
                                ToastUtils.show(getSafeString(R.string.beauty_off_tips));
                                return;
                            }
                            if (!mAlivcLivePushConfig.isAudioOnly()) {
                                mBeautyButton.post(() -> {
                                    if (mAnimojiContainerView.getVisibility() == View.VISIBLE) {
                                        changeAnimojiContainerVisibility();
                                    }
                                    if (mSoundEffectView.getVisibility() == View.VISIBLE) {
                                        changeSoundEffectVisibility();
                                    }
                                    changeBeautyContainerVisibility();
                                });
                            }
                        } else if (id == R.id.restart_button) {
                            if (mAsync) {
                                if (!mIsStartAsnycPushing) {
                                    mIsStartAsnycPushing = true;
                                    pusher.restartPushAync();
                                }
                            } else {
                                pusher.restartPush();
                            }
                        } else if (id == R.id.more) {
                            mMore.post(new Runnable() {
                                @Override
                                public void run() {
                                    PushMoreConfigBottomSheet pushMoreDialog = new PushMoreConfigBottomSheet(getActivity(), R.style.AIO_BottomSheetDialog);
                                    pushMoreDialog.setOnMoreConfigListener(new PushMoreConfigBottomSheet.OnMoreConfigListener() {
                                        @Override
                                        public void onDisplayModeChanged(AlivcPreviewDisplayMode mode) {
                                            pusher.setPreviewMode(mode);
                                        }

                                        @Override
                                        public void onPushMirror(boolean state) {
                                            pusher.setPushMirror(state);
                                        }

                                        @Override
                                        public void onPreviewMirror(boolean state) {
                                            pusher.setPreviewMirror(state);

                                        }

                                        @Override
                                        public void onAutoFocus(boolean state) {
                                            pusher.setAutoFocus(state);
                                        }

                                        @Override
                                        public void onAddDynamic() {
                                            if (mDynamicals.size() < 5) {
                                                float startX = 0.1f + mDynamicals.size() * 0.2f;
                                                float startY = 0.1f + mDynamicals.size() * 0.2f;
                                                int id = pusher.addDynamicsAddons(getActivity().getFilesDir().getPath() + File.separator + "alivc_resource/qizi/", startX, startY, 0.2f, 0.2f);
                                                if (id > 0) {
                                                    mDynamicals.add(id);
                                                } else {
                                                    ToastUtils.show(getSafeString(R.string.add_dynamic_failed) + id);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onRemoveDynamic() {
                                            if (mDynamicals.size() > 0) {
                                                int index = mDynamicals.size() - 1;
                                                int id = mDynamicals.get(index);
                                                pusher.removeDynamicsAddons(id);
                                                mDynamicals.remove(index);
                                            }
                                        }
                                    });
                                    pushMoreDialog.setQualityMode(mQualityMode);
                                    pushMoreDialog.show();
                                }
                            });


//                            PushMoreDialog pushMoreDialog = new PushMoreDialog();
//                            pushMoreDialog.setAlivcLivePusher(pusher, new DynamicListener() {
//                                @Override
//                                public void onAddDynamic() {
//                                    if (mDynamicals.size() < 5) {
//                                        float startX = 0.1f + mDynamicals.size() * 0.2f;
//                                        float startY = 0.1f + mDynamicals.size() * 0.2f;
//                                        int id = pusher.addDynamicsAddons(getActivity().getFilesDir().getPath() + File.separator + "alivc_resource/qizi/", startX, startY, 0.2f, 0.2f);
//                                        if (id > 0) {
//                                            mDynamicals.add(id);
//                                        } else {
//                                            ToastUtils.show(getSafeString(R.string.add_dynamic_failed) + id);
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onRemoveDynamic() {
//                                    if (mDynamicals.size() > 0) {
//                                        int index = mDynamicals.size() - 1;
//                                        int id = mDynamicals.get(index);
//                                        pusher.removeDynamicsAddons(id);
//                                        mDynamicals.remove(index);
//                                    }
//                                }
//                            });
//                            pushMoreDialog.setQualityMode(mQualityMode);
//                            pushMoreDialog.setPushUrl(mPushUrl);
//                            pushMoreDialog.show(getFragmentManager(), "moreDialog");
                        } else if (id == R.id.snapshot) {
                            pusher.snapshot(1, 0, new AlivcSnapshotListener() {
                                @Override
                                public void onSnapshot(Bitmap bmp) {
                                    String dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SS").format(new Date());
                                    File f = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "snapshot-" + dateFormat + ".png");
                                    if (f.exists()) {
                                        f.delete();
                                    }
                                    try {
                                        FileOutputStream out = new FileOutputStream(f);
                                        bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                                        out.flush();
                                        out.close();
                                    } catch (FileNotFoundException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    showDialog("截图已保存：" + getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + "/snapshot-" + dateFormat + ".png");
                                }
                            });
                        } else if (id == R.id.data) {
                            mDataView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mDataView.getVisibility() == View.VISIBLE) {
                                        mDataView.setVisibility(View.INVISIBLE);
                                    } else {
                                        mDataView.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    } catch (IllegalArgumentException e) {
                        showDialog(e.getMessage());
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        showDialog(e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        }
    };

    AlivcLivePushInfoListener mPushInfoListener = new AlivcLivePushInfoListener() {
        @Override
        public void onPreviewStarted(AlivcLivePusher pusher) {
            //AlivcLivePusher pusher = mPushController.getLivePusher();
            // 添加水印
            for (int i = 0; i < waterMarkInfos.size(); i++) {
                pusher.addWaterMark(waterMarkInfos.get(i).mWaterMarkPath, waterMarkInfos.get(i).mWaterMarkCoordX, waterMarkInfos.get(i).mWaterMarkCoordY, waterMarkInfos.get(i).mWaterMarkWidth);
            }
            showToast(getSafeString(R.string.start_preview));
        }

        @Override
        public void onPreviewStoped(AlivcLivePusher pusher) {
            if (isAdded()) {
                showToast(getSafeString(R.string.stop_preview));
            }
        }

        @Override
        public void onPushStarted(AlivcLivePusher pusher) {
            isConnectResult = true;
            mIsStartAsnycPushing = false;
            if (isAdded()) {
                showToast(getSafeString(R.string.start_push));
            }
        }

        @Override
        public void onFirstAVFramePushed(AlivcLivePusher pusher) {
            Log.d(TAG, "onFirstAVFramePushed: ");
        }

        @Override
        public void onPushPauesed(AlivcLivePusher pusher) {
            showToast(getSafeString(R.string.pause_push));
        }

        @Override
        public void onPushResumed(AlivcLivePusher pusher) {
            showToast(getSafeString(R.string.resume_push));
        }

        @Override
        public void onPushStoped(AlivcLivePusher pusher) {
            showToast(getSafeString(R.string.stop_push));
        }

        /**
         * 推流重启通知
         *
         * @param pusher AlivcLivePusher实例
         */
        @Override
        public void onPushRestarted(AlivcLivePusher pusher) {
            mIsStartAsnycPushing = false;
            showToast(getSafeString(R.string.restart_success));
        }

        @Override
        public void onFirstFramePreviewed(AlivcLivePusher pusher) {
            Log.d(TAG, "onFirstFramePreviewed: ");
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
            if (mPushController.getLivePusher() != null) {
                if (mTotalLivePushStatsInfoTV != null) {
                    mTotalLivePushStatsInfoTV.post(new Runnable() {
                        @Override
                        public void run() {
                            if (statistics != null && statistics.getVideoEncodeBitrate() != 0) {
                                mTotalLivePushStatsInfoTV.setText(statistics.toString());
                            }
                        }
                    });
                }
            }
        }

        @Override
        public void onSetLiveMixTranscodingConfig(AlivcLivePusher alivcLivePusher, boolean b, String s) {
            Log.d(TAG, "onSetLiveMixTranscodingConfig: ");
        }
    };

    AlivcLivePushErrorListener mPushErrorListener = new AlivcLivePushErrorListener() {

        @Override
        public void onSystemError(AlivcLivePusher livePusher, AlivcLivePushError error) {
            mIsStartAsnycPushing = false;
            showDialog(getSafeString(R.string.system_error) + error.toString());
        }

        @Override
        public void onSDKError(AlivcLivePusher livePusher, AlivcLivePushError error) {
            if (error != null) {
                mIsStartAsnycPushing = false;
                showDialog(getSafeString(R.string.sdk_error) + error.toString());
            }
        }
    };

    AlivcLivePushNetworkListener mPushNetworkListener = new AlivcLivePushNetworkListener() {
        @Override
        public void onNetworkPoor(AlivcLivePusher pusher) {
            showNetWorkDialog(getSafeString(R.string.network_poor));
        }

        @Override
        public void onNetworkRecovery(AlivcLivePusher pusher) {
            showToast(getSafeString(R.string.network_recovery));
        }

        @Override
        public void onReconnectStart(AlivcLivePusher pusher) {
            showToastShort(getSafeString(R.string.reconnect_start));
        }

        @Override
        public void onReconnectFail(AlivcLivePusher pusher) {
            mIsStartAsnycPushing = false;
            showDialog(getSafeString(R.string.reconnect_fail));
        }

        @Override
        public void onReconnectSucceed(AlivcLivePusher pusher) {
            showToast(getSafeString(R.string.reconnect_success));
        }

        @Override
        public void onSendDataTimeout(AlivcLivePusher pusher) {
            mIsStartAsnycPushing = false;
            showDialog(getSafeString(R.string.senddata_timeout));
        }

        @Override
        public void onConnectFail(AlivcLivePusher pusher) {
            isConnectResult = true;
            mIsStartAsnycPushing = false;
            showDialog(getSafeString(R.string.connect_fail));
        }

        @Override
        public void onConnectionLost(AlivcLivePusher pusher) {
            mIsStartAsnycPushing = false;
            showToast("推流已断开");
        }

        @Override
        public String onPushURLAuthenticationOverdue(AlivcLivePusher pusher) {
            Log.d(TAG, "onPushURLAuthenticationOverdue: ");
            return "";
        }

        @Override
        public void onSendMessage(AlivcLivePusher pusher) {
            showToast(getSafeString(R.string.send_message));
        }

        @Override
        public void onPacketsLost(AlivcLivePusher pusher) {
            showToast("推流丢包通知");
        }
    };

    private AlivcLivePushBGMListener mPushBGMListener = new AlivcLivePushBGMListener() {
        @Override
        public void onStarted() {
            Log.d(TAG, "onStarted: ");
        }

        @Override
        public void onStoped() {
            Log.d(TAG, "onStoped: ");
        }

        @Override
        public void onPaused() {
            Log.d(TAG, "onPaused: ");
        }

        @Override
        public void onResumed() {
            Log.d(TAG, "onResumed: ");
        }

        @Override
        public void onProgress(final long progress, final long duration) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMusicDialog != null) {
                        mMusicDialog.updateProgress(progress, duration);
                    }
                }
            });
        }

        @Override
        public void onCompleted() {
            Log.d(TAG, "onCompleted: ");
        }

        @Override
        public void onDownloadTimeout() {
            Log.d(TAG, "onDownloadTimeout: ");
        }

        @Override
        public void onOpenFailed() {
            Log.d(TAG, "onOpenFailed: ");
            showDialog(getSafeString(R.string.bgm_open_failed));
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMusicDialog != null && mMusicDialog.isShowing()) {
            mMusicDialog.dismiss();
            mMusicDialog = null;
        }
        if (mExecutorService != null && !mExecutorService.isShutdown()) {
            mExecutorService.shutdown();
        }
        destroyAnimojiEngine();
        destroyBeautyManager();
    }

    private void showToast(final String text) {
        if (getActivity() == null || text == null) {
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    AVToast.show(getActivity(), true, text);
                }
            }
        });
    }

    private void showToastShort(final String text) {
        if (getActivity() == null || text == null) {
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }

    private void showDialog(final String message) {
        if (getActivity() == null || message == null) {
            return;
        }
        if (mDialog == null || !mDialog.isShowing()) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        mDialog = new CommonDialog(getActivity());
                        mDialog.setDialogTitle(getSafeString(R.string.dialog_title));
                        mDialog.setDialogContent(message);
                        mDialog.setConfirmButton(TextFormatUtil.getTextFormat(getActivity(), R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        mDialog.show();
                    }
                }
            });
        }
    }

    private void showDialog(final String title, final String message) {
        if (getActivity() == null || message == null) {
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setMessage(message);
                    dialog.setPositiveButton(getSafeString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    private void showNetWorkDialog(final String message) {
        if (getActivity() == null || message == null) {
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle(getSafeString(R.string.dialog_title));
                    dialog.setMessage(message);
                    dialog.setNegativeButton(getSafeString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    dialog.setNeutralButton(getSafeString(R.string.reconnect), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AlivcLivePusher pusher = mPushController.getLivePusher();
                            try {
                                pusher.reconnectPushAsync(null);
                            } catch (IllegalStateException e) {

                            }
                        }
                    });
                    dialog.show();
                }
            }
        });
    }


    private String getMD5(String string) {

        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    private String getUri(String url) {
        String result = "";
        String temp = url.substring(7);
        if (temp != null && !temp.isEmpty()) {
            result = temp.substring(temp.indexOf("/"));
        }
        return result;
    }


    public final String getSafeString(@StringRes int resId) {
        Context context = getContext();
        if (context != null) {
            return getResources().getString(resId);
        } else {
            return "";
        }
    }

    private String getAuthString(String time) {
        if (!time.isEmpty() && !mPrivacyKey.isEmpty()) {
            long tempTime = (System.currentTimeMillis() + Integer.valueOf(time)) / 1000;
            String tempprivacyKey = String.format(mMd5String, getUri(mPushUrl), tempTime, 0, 0, mPrivacyKey);
            String auth = String.format(mAuthString, tempTime, 0, 0, getMD5(tempprivacyKey));
            mTempUrl = mPushUrl + auth;
        } else {
            mTempUrl = mPushUrl;
        }
        return mTempUrl;
    }

    private void startPCM(final Context context) {
        AlivcLivePusher pusher = mPushController.getLivePusher();
        new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            private AtomicInteger atoInteger = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("LivePushActivity-readPCM-Thread" + atoInteger.getAndIncrement());
                return t;
            }
        }).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                audioThreadOn = true;
                byte[] pcm;
                int allSended = 0;
                int sizePerSecond = 44100 * 2;
                InputStream myInput = null;
                OutputStream myOutput = null;
                boolean reUse = false;
                long startPts = System.nanoTime() / 1000;
                try {
                    File f = new File(getActivity().getFilesDir().getPath() + File.separator + "alivc_resource/441.pcm");
                    myInput = new FileInputStream(f);
                    // File f = new File("/sdcard/alivc_resource/441.pcm");
                    byte[] buffer = new byte[2048];
                    int length = myInput.read(buffer, 0, 2048);
                    while (length > 0 && audioThreadOn) {
                        long pts = System.nanoTime() / 1000;
                        pusher.inputStreamAudioData(buffer, length, 44100, 1, pts);
                        allSended += length;
                        if ((allSended * 1000000L / sizePerSecond - 50000) > (pts - startPts)) {
                            try {
                                Thread.sleep(45);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        length = myInput.read(buffer);
                        if (length < 2048) {
                            myInput.close();
                            myInput = new FileInputStream(f);
                            length = myInput.read(buffer);
                        }
                        try {
                            Thread.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    myInput.close();
                    audioThreadOn = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void stopPcm() {
        audioThreadOn = false;
    }


    private void initBeautyManager(long glContext) {
        if (mBeautyManager == null) {
            Log.d(TAG, "initBeautyManager start");
            if (mAlivcLivePushConfig.getLivePushMode() == AlivcLiveMode.AlivcLiveBasicMode) {
                //非互动模式下，美颜初始化
                mBeautyManager = BeautyFactory.createBeauty(BeautySDKType.QUEEN, LivePushFragment.this.getActivity());
            } else {
                //互动模式下，美颜初始化
                mBeautyManager = BeautyFactory.createBeauty(BeautySDKType.INTERACT_QUEEN, LivePushFragment.this.getActivity());
            }
            // initialize in texture thread.
            mBeautyManager.init(glContext);
            mBeautyManager.setBeautyEnable(isBeautyEnable);
            mBeautyManager.switchCameraId(mCameraId);
            Log.d(TAG, "initBeautyManager end");
        }
    }

    private void destroyBeautyManager() {
        if (mBeautyManager != null) {
            mBeautyManager.release();
            mBeautyManager = null;
        }
    }

    private void changeBeautyContainerVisibility() {
        if (mBeautyBeautyContainerView.getVisibility() == View.VISIBLE) {
            mActionBar.setVisibility(View.VISIBLE);
            mBeautyBeautyContainerView.onHideMenu();
            mBeautyBeautyContainerView.setVisibility(View.GONE);
        } else {
            mActionBar.setVisibility(View.GONE);
            mBeautyBeautyContainerView.onShowMenu();
            mBeautyBeautyContainerView.setVisibility(View.VISIBLE);
        }
    }


    private void changeAnimojiContainerVisibility() {
        if (mAnimojiContainerView.getVisibility() == View.VISIBLE) {
            mAnimojiContainerView.setVisibilityWithAnimation(false);
            mActionBar.setVisibility(View.VISIBLE);
        } else {
            mAnimojiContainerView.setVisibilityWithAnimation(true);
            mActionBar.setVisibility(View.INVISIBLE);
        }
    }

    private void changeSoundEffectVisibility() {
        if (mSoundEffectView.getVisibility() == View.VISIBLE) {
            mActionBar.setVisibility(View.VISIBLE);
            mSoundEffectView.setVisibility(View.GONE);
        } else {
            mActionBar.setVisibility(View.GONE);
            mSoundEffectView.setVisibility(View.VISIBLE);
        }
    }

    // should do in texture thread!!!
    private void initAnimojiEngine(@NonNull Context context) {
        if (!mAnimojiOn) {
            return;
        }
        if (null == mAnimojiEngine) {
            mAnimojiEngine = new AnimojiEngine();
            int result = mAnimojiEngine.initEngine(context);
            if (AnimojiError.ANIMOJI_NO_ERROR == result) {
                mAnimojiEngine.setWebTrackEnable(true);
                mAnimojiEngine.setCallback((code, msg) -> {
                    Log.e(TAG, "animoji error: " + code + ", " + msg);
                });
            } else {
                Log.e(TAG, "init animoji error! " + result);
                destroyAnimojiEngine();
            }
        }
    }

    // should do in texture thread!!!
    private void destroyAnimojiEngine() {
        if (null != mAnimojiEngine) {
            mAnimojiEngine.destroy();
            mAnimojiEngine = null;
        }
    }

    // only high-performance android device supports animoji feature.
    private boolean isSupportAnimoji(@NonNull Context context) {
        boolean isDeviceSupportAnimoji = AnimojiEngine.isDeviceSupported(context);
        return !mAudio && mPreviewOrientation == AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT.getOrientation() && isDeviceSupportAnimoji;
    }

    private ArrayList<AnimojiItemBean> getAnimojiItemBeans(@NonNull Context context) {
        ArrayList<AnimojiItemBean> itemBeans = new ArrayList<>();
        String alibear = AnimojiDataFactory.INSTANCE.getResourcePath(context, AnimojiDataFactory.ALIBEAR_BUNDLE_FILE);
        itemBeans.add(new AnimojiItemBean(com.alivc.live.queenbeauty.R.drawable.icon_animoji_alibear, "熊猫", alibear));
//        String ding3duo = AnimojiDataFactory.INSTANCE.getResourcePath(context, AnimojiDataFactory.DING3DUO_BUNDLE_FILE);
//        itemBeans.add(new AnimojiItemBean(com.alivc.live.queenbeauty.R.drawable.icon_animoji_ding3duo, "钉三多", ding3duo));
        return itemBeans;
    }

    public void updateOperaButtonState(boolean bool){
        mOperaButton.post(() -> {
            mOperaButton.setText(bool ? getSafeString(R.string.pause_button) : getSafeString(R.string.resume_button));
            mOperaButton.setSelected(!bool);
            mPreviewButton.setText(bool ? getSafeString(R.string.stop_preview_button) : getSafeString(R.string.start_preview_button));
            mPreviewButton.setSelected(bool);
        });
    }
}
