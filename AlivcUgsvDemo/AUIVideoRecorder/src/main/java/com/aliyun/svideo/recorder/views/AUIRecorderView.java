package com.aliyun.svideo.recorder.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.aliyun.aio.avbaseui.widget.AVLoadingDialog;
import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.common.global.AliyunTag;
import com.aliyun.common.utils.BitmapUtil;
import com.aliyun.common.utils.CommonUtil;
import com.aliyun.svideo.base.BaseChooser;
import com.aliyun.svideo.base.Constants;
import com.aliyun.svideo.base.utils.FastClickUtil;
import com.aliyun.svideo.recorder.RecorderConfig;
import com.aliyun.svideo.recorder.views.focus.AUIFocusPanel;
import com.aliyun.ugsv.auibeauty.api.BeautyFactory;
import com.aliyun.ugsv.auibeauty.BeautyInterface;
import com.aliyun.ugsv.auibeauty.IAliyunBeautyInitCallback;
import com.aliyun.ugsv.auibeauty.OnBeautyLayoutChangeListener;
import com.aliyun.ugsv.auibeauty.OnDefaultBeautyLevelChangeListener;
import com.aliyun.ugsv.auibeauty.api.constant.BeautyConstant;
import com.aliyun.ugsv.auibeauty.api.constant.BeautySDKType;
import com.aliyun.svideo.base.utils.VideoInfoUtils;
import com.aliyun.ugsv.common.utils.PermissionUtils;
import com.aliyun.ugsv.common.utils.ScreenUtils;
import com.aliyun.ugsv.common.utils.ThreadUtils;
import com.aliyun.ugsv.common.utils.ToastUtils;
import com.aliyun.ugsv.common.utils.UriUtils;
import com.aliyun.svideo.downloader.zipprocessor.DownloadFileUtils;
import com.aliyun.svideo.music.music.MusicFileBean;
import com.aliyun.svideo.recorder.R;
import com.aliyun.svideo.recorder.AUIVideoRecorderActivity;
import com.aliyun.svideo.recorder.utils.ActivityUtil;
import com.aliyun.svideo.recorder.utils.OrientationDetector;
import com.aliyun.svideo.recorder.utils.RecordCommon;
import com.aliyun.svideo.recorder.views.control.AUICameraType;
import com.aliyun.svideo.recorder.views.control.AUIControlView;
import com.aliyun.svideo.recorder.views.control.AUIControlViewListener;
import com.aliyun.svideo.recorder.views.control.AUIFlashType;
import com.aliyun.svideo.recorder.views.control.AUIRecordState;
import com.aliyun.svideo.recorder.views.countdown.AUICountDownView;
import com.aliyun.svideo.recorder.views.dialog.AUISpecialEffectChooser;
import com.aliyun.svideo.recorder.views.dialog.AUIFilterChooser;
import com.aliyun.svideo.recorder.views.dialog.AUIPropsChooser;
import com.aliyun.svideo.recorder.views.effects.filter.animfilter.AUISpecialEffectLoadingView;
import com.aliyun.svideo.recorder.views.effects.filter.animfilter.OnSpecialEffectItemClickListener;
import com.aliyun.svideo.recorder.views.effects.paster.PasterSelectListener;
import com.aliyun.svideo.recorder.views.music.AUIMusicChooser;
import com.aliyun.svideo.recorder.views.music.AUIMusicSelectListener;
import com.aliyun.svideosdk.common.AliyunErrorCode;
import com.aliyun.svideosdk.common.callback.recorder.OnFrameCallback;
import com.aliyun.svideosdk.common.callback.recorder.OnPictureCallback;
import com.aliyun.svideosdk.common.callback.recorder.OnRecordCallback;
import com.aliyun.svideosdk.common.callback.recorder.OnTextureIdCallback;
import com.aliyun.svideosdk.common.struct.common.AliyunSnapVideoParam;
import com.aliyun.svideosdk.common.struct.effect.EffectFilter;
import com.aliyun.svideosdk.common.struct.effect.EffectStream;
import com.aliyun.svideosdk.common.struct.form.PreviewPasterForm;
import com.aliyun.svideosdk.common.struct.project.Source;
import com.aliyun.svideosdk.common.struct.recorder.CameraParam;
import com.aliyun.svideosdk.recorder.AliyunIClipManager;
import com.aliyun.svideosdk.recorder.AliyunIRecorder;
import com.aliyun.svideosdk.recorder.impl.AliyunRecordPasterController;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ?????????(> 3.6.5??????)????????????????????????????????????
 */
public class AUIRecorderView extends FrameLayout
        implements ScaleGestureDetector.OnScaleGestureListener {
    private static final String TAG = AUIRecorderView.class.getSimpleName();
    private static final String TAG_GIF_CHOOSER = "gif";
    private static final String TAG_BEAUTY_CHOOSER = "beauty";
    private static final String TAG_MUSIC_CHOOSER = "music";
    private static final String TAG_FILTER_CHOOSER = "filter";
    private static final String TAG_ANIM_FILTER_CHOOSER = "anim_filter";

    // ????????????????????????
    private static final int COUNTDOWN_SECONDS = 3;

    //??????????????????
    private static final int MIN_RECORD_TIME = 0;
    //??????????????????
    private static final int MAX_RECORD_TIME = Integer.MAX_VALUE;

    private AUIRecordState mCurRecordSTate = AUIRecordState.WAITING;

    private SurfaceView mRecorderSurfaceView;
    private FrameLayout mVideoContainer;
    private AUIControlView mControlView;
    private AUICountDownView mCountDownView;

    private AliyunIRecorder recorder;

    private AliyunIClipManager mClipManager;
    private LinkedList<Float> mClipTimeRatioList = new LinkedList<>();
    private com.aliyun.svideosdk.common.struct.recorder.CameraType cameraType
            = com.aliyun.svideosdk.common.struct.recorder.CameraType.FRONT;
    private FragmentActivity mActivity;
    //?????????????????????????????????
    private boolean isMaxDuration = false;
    //????????????
    private int recordTime = 0;

    //??????????????????
    private int minRecordTime = 2000;
    //??????????????????
    private int maxRecordTime = 15 * 1000;
    //???????????????
    private int mGop = 5;
    //????????????
    private BeautySDKType mRenderingMode = BeautySDKType.QUEEN;

    private AUIPropsChooser gifEffectChooser;
    /**
     * ???????????????
     */
    private AUIFilterChooser mFilterChooser;
    /**
     * ???????????????
     */
    private AUISpecialEffectChooser mSpecialEffectChooser;

    //?????????????????????
    private AliyunRecordPasterController mPasterController;
    private OrientationDetector orientationDetector;
    private AUIMusicChooser musicChooseView;


    public static final int TYPE_FILTER = 1;
    public static final int TYPE_MUSIC = 3;
    private LinkedHashMap<Integer, Object> mConflictEffects = new LinkedHashMap<>();
    private AsyncTask<Void, Integer, Integer> finishRecodingTask;
    private AsyncTask<Void, Void, Void> faceTrackPathTask;

    /**
     * ??????filter?????????item??????
     */
    private int mFilterSelectedPosition = 0;

    /**
     * ????????????filter?????????item??????
     */
    private int mSpecialEffectSelectedPosition = 0;

    /**
     * ??????mv?????????, ???????????????,???????????????mv
     */
    private boolean isAllowChangeMv = true;
    private AVLoadingDialog progressBar;

    /**
     * ??????????????????
     */
    private boolean mIsHide;

    private boolean isHasMusic = false;
    private AUIFocusPanel mFocusView;
    private EffectFilter mCurrentAnimFilterEffect;
    private boolean mIsUseFlip = false;
    private BeautyInterface mBeautyInterface;

    /**
     * ????????????????????????????????????
     */
    private AUIMusicSelectListener mOutMusicSelectListener;

    /**
     * ??????????????????????????????????????????????????????????????????????????????MV?????????MV??????????????????????????????MV???????????????????????? ??????????????????????????????????????????????????????????????????????????????????????????????????????
     * ???????????????????????????????????????????????????????????????????????????????????? ?????????
     */
    private void restoreConflictEffect() {
        if (!mConflictEffects.isEmpty() && recorder != null) {
            for (Map.Entry<Integer, Object> entry : mConflictEffects.entrySet()) {
                switch (entry.getKey()) {
                    case TYPE_FILTER:
                        recorder.applyFilter((EffectFilter) entry.getValue());
                        break;
                    case TYPE_MUSIC:
                        EffectStream music = (EffectStream) entry.getValue();

                        if (music != null) {
                            // ???????????????????????????????????????????????????,
                            // ???????????????, ?????????????????????????????????, ???????????????????????????
                            isHasMusic = !TextUtils.isEmpty(music.getSource().getPath());
                            if (isHasMusic) {
                                recorder.applyBackgroundMusic(music);
                            }

                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public AUIRecorderView(Context context) {
        super(context);
    }

    public AUIRecorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AUIRecorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initViews() {
        initControlView();
        initCountDownView();
        initFocusView();
        initBeauty();
        setFaceTrackModePath();
    }

    /**
     * ??????focus
     */
    private void initFocusView() {
        mFocusView = new AUIFocusPanel(getContext());
        addSubView(mFocusView);
        mFocusView.setVisibility(View.GONE);
    }

    public void onStop() {
        if (mFocusView != null) {
            mFocusView.activityStop();
        }
    }

    /**
     * ???????????????SDK
     */
    private void initBeauty() {
        if (mRenderingMode != null) {
            final BeautyInterface beautyInterface = BeautyFactory.createBeauty(mRenderingMode, getContext());
            if (beautyInterface != null) {
                beautyInterface.init(getContext(), new IAliyunBeautyInitCallback() {
                    @Override
                    public void onInit(int code) {
                        if (code == BeautyConstant.BEAUTY_INIT_SUCCEED) {
                            if (mRenderingMode == BeautySDKType.DEFAULT && recorder != null) {
                                beautyInterface.addDefaultBeautyLevelChangeListener(new OnDefaultBeautyLevelChangeListener() {
                                    @Override
                                    public void onDefaultBeautyLevelChange(int level) {
                                        if (recorder != null) {
                                            recorder.setBeautyLevel(level);
                                        }
                                    }
                                });
                            }
                            beautyInterface.initParams();
                            mBeautyInterface = beautyInterface;
                        }

                    }
                });
            }

        }

    }

    public void isUseFlip(boolean isUseFlip) {
        this.mIsUseFlip = isUseFlip;
    }

    /**
     * ??????????????????view
     */
    private void initCountDownView() {
        if (mCountDownView == null) {
            mCountDownView = new AUICountDownView(getContext());
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            addView(mCountDownView, params);
        }
    }

    private void initVideoContainer() {
        mVideoContainer = new FrameLayout(getContext());
//        int videoOutputWidth = getVideoWidth();
//        int videoOutputHeight = getVideoHeight();
//        int width = ScreenUtils.getRealWidth(getContext());
//        int height = width * videoOutputHeight / videoOutputWidth;
//        LayoutParams params = new LayoutParams(width, height);
//        params.gravity = Gravity.CENTER;
        addView(mVideoContainer, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initRecorderSurfaceView();
        //????????????surFaceView
        recorder.setDisplayView(mRecorderSurfaceView);
    }

    /**
     * ?????????RecordersurfaceView
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initRecorderSurfaceView() {
        mRecorderSurfaceView = new SurfaceView(getContext());
        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getContext(), this);
        final GestureDetector gestureDetector = new GestureDetector(getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        if (recorder == null) {
                            return true;
                        }
                        float x = e.getX() / mRecorderSurfaceView.getWidth();
                        float y = e.getY() / mRecorderSurfaceView.getHeight();
                        recorder.setFocus(x, y);

                        mFocusView.showView();
                        mFocusView.setLocation(e.getRawX(), e.getRawY());
                        return true;
                    }
                });
        mRecorderSurfaceView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() >= 2) {
                    scaleGestureDetector.onTouchEvent(event);
                } else if (event.getPointerCount() == 1) {
                    gestureDetector.onTouchEvent(event);
                }
                return true;
            }
        });

        mVideoContainer.addView(mRecorderSurfaceView);
        mRecorderSurfaceView.setLayoutParams(getVideoLayoutParams());

        //TODO ????????????

//        int border = 0;
//        int color = Color.TRANSPARENT;
//        float tempRadius = 0;

//        final float radius = tempRadius;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mRecorderSurfaceView.setOutlineProvider(new ViewOutlineProvider() {
//                @Override
//                public void getOutline(View view, Outline outline) {
//                    Rect rect = new Rect();
//                    view.getGlobalVisibleRect(rect);
//                    int leftMargin = 0;
//                    int topMargin = 0;
//                    Rect selfRect = new Rect(leftMargin, topMargin,
//                            rect.right - rect.left - leftMargin,
//                            rect.bottom - rect.top - topMargin);
//                    outline.setRoundRect(selfRect, radius);
//                }
//            });
//            mRecorderSurfaceView.setClipToOutline(true);
//        }
//
//        AUIVideoDisplayParam displayParam = new AUIVideoDisplayParam.Builder().build();
//        int videoOutputWidth = getVideoWidth();
//        int videoOutputHeight = getVideoHeight();
//        int parentWidth = ScreenUtils.getRealWidth(getContext());
//        int parentHeight = parentWidth * videoOutputHeight / videoOutputWidth;
//        int width = (int) (parentWidth * displayParam.getWidthRatio());
//        int height = (int) (parentHeight * displayParam.getHeightRatio());
//        int marginLeft = (int) ((displayParam.getCenterX() - displayParam.getWidthRatio() / 2) * parentWidth);
//        int marginTop = (int) ((displayParam.getCenterY() - displayParam.getHeightRatio() / 2) * parentHeight);
//
//        FrameLayout container = new FrameLayout(getContext());
//        container.setBackgroundColor(color);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            container.setOutlineProvider(new ViewOutlineProvider() {
//                @Override
//                public void getOutline(View view, Outline outline) {
//                    Rect rect = new Rect();
//                    view.getGlobalVisibleRect(rect);
//                    int leftMargin = 0;
//                    int topMargin = 0;
//                    Rect selfRect = new Rect(leftMargin, topMargin,
//                            rect.right - rect.left - leftMargin,
//                            rect.bottom - rect.top - topMargin);
//                    outline.setRoundRect(selfRect, radius);
//                }
//            });
//            container.setClipToOutline(true);
//        }
//
//        LayoutParams slp = new LayoutParams(width - border * 2, height - border * 2);
//        slp.gravity = Gravity.CENTER;
//        container.addView(mRecorderSurfaceView, slp);
//
//        LayoutParams layoutParams = new LayoutParams(width, height);
//        layoutParams.leftMargin = marginLeft;
//        layoutParams.topMargin = marginTop;
//        mVideoContainer.addView(container, layoutParams);

    }

    /**
     * ??????????????????view
     */
    private void initControlView() {
        mControlView = new AUIControlView(getContext());
        mControlView.setControlViewListener(new AUIControlViewListener() {
            @Override
            public void onBackClick() {
                if (mBackClickListener != null) {
                    mBackClickListener.onClick();
                }
            }

            @Override
            public void onFinishClick() {
                // ????????????
                finishRecording();
            }

            @Override
            public void onBeautyFaceClick() {
                if (mBeautyInterface != null) {
                    mBeautyInterface.showControllerView(getFragmentManager(), new OnBeautyLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(int visibility) {
                        }
                    });
                }
            }

            @Override
            public void onMusicClick() {
                showMusicSelView();
            }

            @Override
            public void onCameraSwitch() {
                if (recorder != null) {
                    int cameraId = recorder.switchCamera();
                    for (com.aliyun.svideosdk.common.struct.recorder.CameraType type : com.aliyun.svideosdk.common.struct.recorder.CameraType.values()) {
                        if (type.getType() == cameraId) {
                            cameraType = type;
                        }
                    }
                    if (mControlView != null) {
                        for (AUICameraType type : AUICameraType.values()) {
                            if (type.getType() == cameraId) {
                                mControlView.setCameraType(type);
                            }
                        }

                        if (mControlView.getFlashType() == AUIFlashType.ON
                                && mControlView.getCameraType() == AUICameraType.BACK) {
                            recorder.setLight(com.aliyun.svideosdk.common.struct.recorder.FlashType.TORCH);
                        }
                    }
                }
            }

            @Override
            public void onLightSwitch(AUIFlashType flashType) {
                if (recorder != null) {
                    for (com.aliyun.svideosdk.common.struct.recorder.FlashType type : com.aliyun.svideosdk.common.struct.recorder.FlashType.values()) {
                        if (flashType.toString().equals(type.toString())) {
                            recorder.setLight(type);
                        }
                    }

                }
                if (mControlView.getFlashType() == AUIFlashType.ON
                        && mControlView.getCameraType() == AUICameraType.BACK) {
                    recorder.setLight(com.aliyun.svideosdk.common.struct.recorder.FlashType.TORCH);
                }
            }

            @Override
            public void onRateSelect(float rate) {
                if (recorder != null) {
                    recorder.setRate(rate);
                }
            }

            @Override
            public void onGifEffectClick() {
                showGifEffectView();
            }

            @Override
            public void onCountdownClick() {
                startCountdownRecord();
            }

            @Override
            public void onStartRecordClick() {
                if (checkIsDisableRecord()) {
                    return;
                }
                if (isWaiting()) {
                    startRecord();
                }
            }

            @Override
            public void onStopRecordClick() {
                stopRecord();
            }

            @Override
            public void onDeleteClick() {
                if (!isWaiting()) {
                    return;
                }
                // clipManager.deletePart();
                recorder.getClipManager().deleteLastPart();
                if (mClipTimeRatioList.size() > 0) {
                    mClipTimeRatioList.removeLast();
                }
                isMaxDuration = false;
                mControlView.onRecordedClipsChanged(
                        mClipManager.getDuration(),
                        mClipManager.getMinDuration(),
                        mClipManager.getMaxDuration(),
                        mClipTimeRatioList,
                        true);
                if (mClipManager.getDuration() == 0) {
                    isAllowChangeMv = true;
                }
            }

            @Override
            public void onFilterEffectClick() {
                // ????????????????????????
                showFilterEffectView();
            }

            @Override
            public void onChangeAspectRatioClick() {
                //??????????????????
                setReSizeRatioMode();
            }

            @Override
            public void onAnimFilterClick() {
                showAnimFilterEffectView();
            }

            @Override
            public void onTakePhotoClick() {
                //??????
                recorder.takeSnapshot(true, new OnPictureCallback() {
                    @Override
                    public void onPicture(final Bitmap bitmap, byte[] data) {
                        ThreadUtils.runOnSubThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void run() {
                                final String imgPath = Constants.SDCardConstants.getDir(getContext().getApplicationContext()) + File.separator + System.currentTimeMillis() + "-photo.jpg";
                                try {
                                    BitmapUtil.generateFileFromBitmap(bitmap, imgPath, "jpg");

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        //??????android Q
                                        ThreadUtils.runOnSubThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                UriUtils.saveImgToMediaStore(getContext().getApplicationContext(), imgPath);
                                            }
                                        });
                                    } else {
                                        MediaScannerConnection.scanFile(getContext().getApplicationContext(),
                                                new String[]{imgPath}, new String[]{"image/jpeg"}, null);
                                    }

                                    ThreadUtils.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AVToast.show(getContext(), true, R.string.ugsv_recorder_toast_photo_saved);
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onRaceDebug(boolean debug) {
                if (mBeautyInterface != null) {
                    mBeautyInterface.setDebug(debug);
                }
            }
        });
        addSubView(mControlView);
    }

    /**
     * ????????????
     */
    String[] permission = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private void setRecordState(AUIRecordState newSate) {
        mCurRecordSTate = newSate;
        mControlView.onRecordStateChanged(mCurRecordSTate);
    }

    private boolean isWaiting() {
        return mCurRecordSTate == AUIRecordState.WAITING;
    }

    private boolean isRecordPending() {
        return mCurRecordSTate == AUIRecordState.RECORD_PENDING;
    }

    private boolean isRecording() {
        return mCurRecordSTate == AUIRecordState.RECORDING;
    }

    private boolean checkIsDisableRecord() {
        if (!PermissionUtils.checkPermissionsGroup(getContext(), permission)) {
            Log.w(TAG, "??????????????????????????????");
            return true;
        }
        if (CommonUtil.SDFreeSize() < 50 * 1000 * 1000) {
            Log.w(TAG, "???????????????????????????????????????");
            return true;
        }
        if (isMaxDuration) {
            Log.w(TAG, "???????????????????????????????????????");
            return true;
        }
        if (mIsHide) {
            return true;
        }
        return false;
    }

    /**
     * ????????????
     */
    private void startRecord() {
        Log.d(TAG, "startRecord");
        setRecordState(AUIRecordState.RECORDING);
        String videoPath = Constants.SDCardConstants.getDir(getContext().getApplicationContext()) + File.separator + System.currentTimeMillis() + "-record.mp4";
        recorder.setOutputPath(videoPath);
        recorder.startRecording();
    }

    /**
     * ????????????
     */
    private void stopRecord() {
        //RECORDING -> RECORD_STOPPING
        if (!isRecording()) {
            return;
        }
        Log.d(TAG, "stopRecord");
        setRecordState(AUIRecordState.RECORD_STOPPING);
        recorder.stopRecording();
    }

    /**
     * ?????????????????????
     */
    private void cancelCountdownRecord() {
        if (!isRecordPending()) {
            return;
        }
        mCountDownView.cancel();
        setRecordState(AUIRecordState.WAITING);
    }

    /**
     * ?????????????????????
     */
    private void startCountdownRecord() {
        if (!isWaiting()) {
            return;
        }
        setRecordState(AUIRecordState.RECORD_PENDING);
        mCountDownView.start(COUNTDOWN_SECONDS);
    }

    /**
     * ???????????????????????????
     */
    private void showMusicSelView() {
        if (musicChooseView == null) {
            musicChooseView = new AUIMusicChooser();

            musicChooseView.setRecordTime(getMaxRecordTime());

            musicChooseView.setMusicSelectListener(new AUIMusicSelectListener() {

                @Override
                public void onMusicSelect(MusicFileBean musicFileBean, long startTime) {
                    if (musicFileBean != null) {
                        if (mOutMusicSelectListener != null) {
                            mOutMusicSelectListener.onMusicSelect(musicFileBean, startTime);
                        }
                        //???????????????????????????????????????????????????????????????
                        if (TextUtils.isEmpty(musicFileBean.getImage()) || TextUtils.isEmpty(musicFileBean.getPath())) {
                            mControlView.setMusicIconId(R.drawable.ic_ugsv_recorder_music, false);
                        } else {
                            mControlView.setMusicIcon(musicFileBean.getImage(), true);
                        }
                        if (TextUtils.isEmpty(musicFileBean.getPath())) {
                            recorder.removeBackgroundMusic();
                        } else {
                            int during = getMaxRecordTime();
                            if (during > musicFileBean.getDuration()) {
                                during = musicFileBean.getDuration() - 100;
                            }
                            EffectStream effectMusic = new EffectStream.Builder()
                                    .source(new Source(musicFileBean.getMusicId(), musicFileBean.getPath()))
                                    .streamStartTime(startTime, TimeUnit.MILLISECONDS)
                                    .streamDuration(during, TimeUnit.MILLISECONDS)
                                    .build();
                            recorder.applyBackgroundMusic(effectMusic);
                            mConflictEffects.put(TYPE_MUSIC, effectMusic);
                        }
                    } else {
                        mControlView.setMusicIconId(R.drawable.ic_ugsv_recorder_music, false);
                    }
                }
            });

            musicChooseView.setDismissListener(new BaseChooser.DialogVisibleListener() {
                @Override
                public void onDialogDismiss() {
                }

                @Override
                public void onDialogShow() {
                }
            });
        }
        if (musicChooseView.isAdded()) {
            return;
        }
        musicChooseView.show(getFragmentManager(), TAG_MUSIC_CHOOSER);
    }

    /**
     * ??????????????????????????????
     */
    private Source currPaster;

    /**
     * ??????????????????????????????
     */
    private void showGifEffectView() {
        if (gifEffectChooser == null) {
            gifEffectChooser = new AUIPropsChooser();
            gifEffectChooser.setPasterSelectListener(new PasterSelectListener() {
                int id;

                @Override
                public void onPasterSelected(PreviewPasterForm imvForm) {
                    String path;
                    id = imvForm.getId();
                    if (imvForm.getId() == 150) {
                        //id=150????????????????????????
                        path = imvForm.getPath();
                    } else {
                        path = DownloadFileUtils.getAssetPackageDir(getContext(),
                                imvForm.getName(), imvForm.getId()).getAbsolutePath();
                    }
                    currPaster = new Source(String.valueOf(imvForm.getId()), path);
                    addEffectToRecord(currPaster);
                }

                @Override
                public void onSelectPasterDownloadFinish(String path) {
                    // ?????????paster???????????????, ?????????paster ???path
                    currPaster = new Source(String.valueOf(id), path);
                }
            });

            gifEffectChooser.setDismissListener(new BaseChooser.DialogVisibleListener() {
                @Override
                public void onDialogDismiss() {
                }

                @Override
                public void onDialogShow() {
                    if (currPaster != null && !TextUtils.isEmpty(currPaster.getPath())) {
                        // dialog?????????,???????????????paster?????????, ?????????paster
                        addEffectToRecord(currPaster);
                    }
                }
            });
        }
        gifEffectChooser.show(getFragmentManager(), TAG_GIF_CHOOSER);
    }

    private void addEffectToRecord(Source source) {

        if (mPasterController != null) {
            recorder.getPasterManager().remove(mPasterController);
        }

        mPasterController = recorder.getPasterManager().addFacePaster(source);
        mPasterController.endEdit();

    }

    private FragmentManager getFragmentManager() {
        FragmentManager fm = null;
        if (mActivity != null) {
            fm = mActivity.getSupportFragmentManager();
        } else {
            Context mContext = getContext();
            if (mContext instanceof FragmentActivity) {
                fm = ((FragmentActivity) mContext).getSupportFragmentManager();
            }
        }
        return fm;
    }

    /**
     * ???????????????????????????
     */
    private void showFilterEffectView() {
        if (mFilterChooser == null) {
            mFilterChooser = new AUIFilterChooser();
        }
        if (mFilterChooser.isAdded()) {
            return;
        }
        mFilterChooser.setOnItemClickListener((effectInfo, index) -> {
            if (effectInfo != null) {
                if (index == 0) {
                    recorder.removeFilter();
                    mConflictEffects.remove(TYPE_FILTER);
                } else {
                    EffectFilter filterEffect = new EffectFilter(effectInfo.getSource());
                    recorder.applyFilter(filterEffect);
                    mConflictEffects.put(TYPE_FILTER, filterEffect);
                }
            }
            mFilterSelectedPosition = index;
        });
        mFilterChooser.setFilterSelectedPosition(mFilterSelectedPosition);
        mFilterChooser.show(getFragmentManager(), TAG_FILTER_CHOOSER);
    }

    /**
     * ?????????????????????????????????
     */
    private void showAnimFilterEffectView() {
        if (mSpecialEffectChooser == null) {
            mSpecialEffectChooser = new AUISpecialEffectChooser();
        }
        if (mSpecialEffectChooser.isAdded()) {
            return;
        }
        // ????????????listener
        mSpecialEffectChooser.setOnItemClickListener(new OnSpecialEffectItemClickListener() {
            @Override
            public void onItemClick(EffectFilter effectInfo, int index) {

                String path = null;
                if (effectInfo.getSource() != null) {
                    path = effectInfo.getSource().getPath();
                }
                if (path == null || index == 0) {
                    recorder.removeAnimationFilter(mCurrentAnimFilterEffect);
                } else {
                    mCurrentAnimFilterEffect = effectInfo;
                    recorder.applyAnimationFilter(mCurrentAnimFilterEffect);
                }

                mSpecialEffectSelectedPosition = index;
            }

            @Override
            public void onItemUpdate(EffectFilter effectInfo) {
                recorder.updateAnimationFilter(effectInfo);
            }

        });
        mSpecialEffectChooser.setSelectedPosition(mSpecialEffectSelectedPosition);
        mSpecialEffectChooser.setDismissListener(new BaseChooser.DialogVisibleListener() {
            @Override
            public void onDialogDismiss() {
            }

            @Override
            public void onDialogShow() {
            }
        });
        mSpecialEffectChooser.show(getFragmentManager(), TAG_ANIM_FILTER_CHOOSER);
    }

    /**
     * ??????????????????
     */
    public void init(AliyunIRecorder recorder) {
        this.recorder = recorder;
        initRecorder();
        initViews();
    }

    private void initRecorder() {
        recorder.setGop(mGop);
        recorder.setVideoFlipH(mIsUseFlip);
        mClipManager = recorder.getClipManager();
        mClipManager.setMaxDuration(getMaxRecordTime());
        mClipManager.setMinDuration(minRecordTime);
        recorder.setFocusMode(CameraParam.FOCUS_MODE_CONTINUE);
        //mediaInfo.setHWAutoSize(true);//???????????????????????????16?????????
        cameraType = recorder.getCameraCount() == 1 ? com.aliyun.svideosdk.common.struct.recorder.CameraType.BACK : cameraType;
        recorder.setCamera(cameraType);
        recorder.setBeautyLevel(0);

        initOrientationDetector();

        //???????????????surFaceView??????
        initVideoContainer();

        recorder.setOnFrameCallback(new OnFrameCallback() {
            @Override
            public void onFrameBack(byte[] bytes, int width, int height, Camera.CameraInfo info) {
                //?????????????????? NV21,???????????????????????????????????????faceUnity??????????????????
                if (mBeautyInterface != null) {
                    mBeautyInterface.onFrameBack(bytes, width, height, info);
                }
            }

            @Override
            public Camera.Size onChoosePreviewSize(List<Camera.Size> supportedPreviewSizes,
                                                   Camera.Size preferredPreviewSizeForVideo) {
                return null;
            }

            @Override
            public void openFailed() {
                Log.e(AliyunTag.TAG, "openFailed----------");
            }
        });
        recorder.setOnRecordCallback(new OnRecordCallback() {
            @Override
            public void onClipComplete(final boolean validClip, final long clipDuration) {
                Log.i(TAG, "onComplete: duration=" + clipDuration + ", clipManager.getDuration()=" + mClipManager.getDuration());
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "onClipComplete");
                        handleStopCallback(validClip, clipDuration);
                        if (isMaxDuration && validClip) {
                            finishRecording();
                        }
                    }
                });
            }

            /**
             * ?????????????????????
             * @param outputPath
             */
            @Override
            public void onFinish(final String outputPath) {
                Log.i(TAG, "onFinish:" + outputPath);
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onFinishEnd();
                        if (mCompleteListener != null) {
                            final OnFinishListener onFinishListener = mCompleteListener;
                            final int duration = mClipManager.getDuration();
                            //deleteAllPart();
                            // ???????????????, ????????????????????????????????????
                            // ????????????????????????, sdk???????????????, ???????????????????????????, ?????????????????????????????????
                            if (activityStoped) {
                                pendingCompseFinishRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        onFinishListener.onComplete(outputPath, duration);
                                    }
                                };
                            } else {
                                onFinishListener.onComplete(outputPath, duration);
                            }
                        }
                        VideoInfoUtils.printVideoInfo(outputPath);
                    }
                });

            }

            @Override
            public void onProgress(final long duration) {
                final int currentDuration = mClipManager.getDuration();
                final int maxDuration = mClipManager.getMaxDuration();
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isAllowChangeMv = false;
                        //??????????????????
                        recordTime = (int) (currentDuration + duration);
                        if (!isRecording()) {
                            return;
                        }
                        mControlView.setRecordTime(recordTime, maxDuration);
                    }
                });

            }

            @Override
            public void onMaxDuration() {
                Log.i(TAG, "onMaxDuration:");
                isMaxDuration = true;

            }

            @Override
            public void onError(int errorCode) {
                Log.e(TAG, "onError:" + errorCode);
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleStopCallback(false, 0);
                    }
                });
            }

            @Override
            public void onInitReady() {
                Log.i(TAG, "onInitReady");
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        restoreConflictEffect();
                        if (mPasterController != null) {
                            addEffectToRecord(mPasterController.getSource());
                        }
                    }
                });
            }


        });
        recorder.setOnTextureIdCallback(new OnTextureIdCallback() {
            @Override
            public int onTextureIdBack(int textureId, int textureWidth, int textureHeight, float[] matrix) {
                if (mBeautyInterface != null) {
                    return mBeautyInterface.onTextureIdBack(textureId, textureWidth, textureHeight, matrix, mControlView.getCameraType().getType());
                }
                return textureId;
            }

            @Override
            public int onScaledIdBack(int scaledId, int textureWidth, int textureHeight, float[] matrix) {

                return scaledId;
            }

            @Override
            public void onTextureDestroyed() {
                // sdk3.7.8??????, ??????????????????????????????????????????gl???????????????GLSurfaceView???????????????GLSurfaceView.queueEvent?????????
                // ?????????????????????gl??????????????????????????????????????????????????????
                if (mBeautyInterface != null) {
                    mBeautyInterface.release();
                    mBeautyInterface = null;
                }
            }
        });
        recorder.setFaceTrackInternalMaxFaceCount(2);
    }


    public void setFaceTrackModePath() {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                String path = getContext().getExternalFilesDir("") + File.separator + RecordCommon.QU_NAME + File.separator;
                if (recorder != null) {
                    recorder.needFaceTrackInternal(true);
                    recorder.setFaceTrackInternalModelPath(path + "/model");
                }
            }
        });

    }

    public void setRecordMute(boolean recordMute) {
        if (recorder != null) {
            recorder.setMute(recordMute);
        }
    }

    private void initOrientationDetector() {
        orientationDetector = new OrientationDetector(getContext().getApplicationContext());
        orientationDetector.setOrientationChangedListener(new OrientationDetector.OrientationChangedListener() {
            @Override
            public void onOrientationChanged() {
                int rotation = getCameraRotation();
                recorder.setRotation(rotation);
                if (mBeautyInterface != null) {
                    mBeautyInterface.setDeviceOrientation(0, ActivityUtil.getDegrees(mActivity));
                }
            }
        });
    }

    private int getCameraRotation() {
        int orientation = orientationDetector.getOrientation();
        int rotation = 90;
        if ((orientation >= 45) && (orientation < 135)) {
            rotation = 180;
        }
        if ((orientation >= 135) && (orientation < 225)) {
            rotation = 270;
        }
        if ((orientation >= 225) && (orientation < 315)) {
            rotation = 0;
        }

        if (Camera.getNumberOfCameras() > cameraType.getType()) {

            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraType.getType(), cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                if (rotation != 0) {
                    rotation = 360 - rotation;
                }
            }
        }
        return rotation;
    }


    private boolean activityStoped;
    private Runnable pendingCompseFinishRunnable;

    /**
     * ???????????????
     */
    public void show() {
        mIsHide = false;
        activityStoped = false;
        if (recorder == null) {
            return;
        }
        if (pendingCompseFinishRunnable != null) {
            pendingCompseFinishRunnable.run();
        }
        pendingCompseFinishRunnable = null;

        recorder.startPreview();
        if (isAllowChangeMv) {
            restoreConflictEffect();
        }
        if (orientationDetector != null && orientationDetector.canDetectOrientation()) {
            orientationDetector.enable();
        }
        mCountDownView.setOnCountdownListener(new AUICountDownView.OnCountdownListener() {
            @Override
            public void onFinish() {
                if (checkIsDisableRecord()) {
                    setRecordState(AUIRecordState.WAITING);
                    return;
                }
                startRecord();
            }

            @Override
            public void onCancel() {
                setRecordState(AUIRecordState.WAITING);
            }
        });
    }

    /**
     * ???????????????
     */
    public void hide() {
        mIsHide = true;
        activityStoped = true;
        if (recorder == null) {
            return;
        }
        if (isRecordPending()) {
            cancelCountdownRecord();
        } else if (isRecording()) {
            stopRecord();
        }
        recorder.stopPreview();
        if (orientationDetector != null) {
            orientationDetector.disable();
        }
//        if (mControlView != null && mControlView.getFlashType() == AUIFlashType.ON
//                && mControlView.getCameraType() == AUICameraType.BACK) {
//            mControlView.setFlashType(AUIFlashType.OFF);
//        }
    }

    /**
     * ??????????????????activity??????fragment???????????????????????????
     */
    public void destroyRecorder() {
        //destroy????????????????????????????????????
//        deleteSliceFile();//??????AlivcIMixRecorderInterface#setIsAutoClearClipVideos(true)??????
        if (finishRecodingTask != null) {
            finishRecodingTask.cancel(true);
            finishRecodingTask = null;
        }

        if (faceTrackPathTask != null) {
            faceTrackPathTask.cancel(true);
            faceTrackPathTask = null;
        }

        if (recorder != null) {
            recorder.release();
            recorder = null;
            Log.i(TAG, "recorder destroy");
        }

        if (orientationDetector != null) {
            orientationDetector.setOrientationChangedListener(null);
        }

        AUISpecialEffectLoadingView.clearCacheEffectFilter();

    }

    /**
     * ??????????????????????????????
     */
    private void deleteSliceFile() {
        if (recorder != null) {
            recorder.getClipManager().deleteAllPart();
        }
    }

    /**
     * ??????????????????????????????????????????????????????????????? ??????editorActivity???????????????????????????????????????{@link AUIVideoRecorderActivity#()}
     */
    private void finishRecording() {
        //waiting -> finishing
        if (!isWaiting()) {
            return;
        }
        setRecordState(AUIRecordState.FINISHING);
        //????????????
        if (progressBar == null) {
            String tip = getResources().getString(R.string.ugsv_recorder_record_create_video);
            progressBar = new AVLoadingDialog(getContext()).tip(tip);
        }
        progressBar.show();
        finishRecodingTask = new FinishRecodingTask(this).executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void onFinishEnd() {
        if (progressBar != null && progressBar.isShowing()) {
            progressBar.dismiss();
        }
        setRecordState(AUIRecordState.WAITING);
    }

    /**
     * ???????????????AsyncTask
     */
    public static class FinishRecodingTask extends AsyncTask<Void, Integer, Integer> {
        WeakReference<AUIRecorderView> weakReference;

        FinishRecodingTask(AUIRecorderView recordView) {
            weakReference = new WeakReference<>(recordView);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (weakReference == null) {
                return -1;
            }

            AUIRecorderView recordView = weakReference.get();
            if (recordView != null) {
                Log.i(TAG, "finishRecording");
                return recordView.recorder.finishRecording();

            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer code) {
            if (weakReference == null) {
                return;
            }
            if (code != AliyunErrorCode.ALIVC_COMMON_RETURN_SUCCESS) {
                Log.e(TAG, "???????????? ????????? : " + code);
                AUIRecorderView recordView = weakReference.get();
                if (recordView != null) {
                    ToastUtils.show(recordView.getContext(), R.string.ugsv_recorder_finish_fail);
                    recordView.onFinishEnd();
                }
            }
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param isValid
     * @param duration
     */
    private void handleStopCallback(final boolean isValid, final long duration) {
        if (isValid) {
            mClipTimeRatioList.add((float) duration / mClipManager.getMaxDuration());
        }
        mControlView.onRecordedClipsChanged(mClipManager.getDuration(),
                mClipManager.getMinDuration(),
                mClipManager.getMaxDuration(),
                mClipTimeRatioList,
                false);
        setRecordState(AUIRecordState.WAITING);
        if (!isValid) {
            return;
        }
        isAllowChangeMv = false;
    }

    /**
     * addSubView ?????????view????????????
     *
     * @param view ???view
     */
    private void addSubView(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(view, params);//??????????????????
    }

    /**
     * ????????????????????????click listener
     */
    private OnBackClickListener mBackClickListener;

    public void setBackClickListener(OnBackClickListener listener) {
        this.mBackClickListener = listener;
    }

    private OnFinishListener mCompleteListener;

    /**
     * ????????????????????????
     */
    public void deleteAllPart() {
        if (mClipManager != null) {
            mClipManager.deleteAllPart();
            mClipTimeRatioList.clear();
            mControlView.onRecordedClipsChanged(mClipManager.getDuration(),
                    mClipManager.getMinDuration(),
                    mClipManager.getMaxDuration(),
                    mClipTimeRatioList,
                    true);
            if (mClipManager.getDuration() == 0) {
                isAllowChangeMv = true;
            }
        }
    }

    private float lastScaleFactor;
    private float scaleFactor;

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float factorOffset = detector.getScaleFactor() - lastScaleFactor;
        scaleFactor += factorOffset;
        lastScaleFactor = detector.getScaleFactor();
        if (scaleFactor < 0) {
            scaleFactor = 0;
        }
        if (scaleFactor > 1) {
            scaleFactor = 1;
        }
        recorder.setZoom(scaleFactor);
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        lastScaleFactor = detector.getScaleFactor();
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    /**
     * ????????????????????????
     */
    public interface OnBackClickListener {
        void onClick();
    }

    /**
     * ????????????????????????
     */
    public interface OnFinishListener {
        void onComplete(String path, int duration);
    }

    public void setCompleteListener(OnFinishListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }

    public void setActivity(FragmentActivity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public int getMaxRecordTime() {
        if (maxRecordTime < MIN_RECORD_TIME) {
            return MIN_RECORD_TIME;
        } else if (maxRecordTime > MAX_RECORD_TIME) {
            return MAX_RECORD_TIME;
        } else {

            return maxRecordTime;
        }

    }

    /**
     * ??????????????????
     *
     * @param maxRecordTime
     */
    public void setMaxRecordTime(int maxRecordTime) {
        this.maxRecordTime = maxRecordTime;
    }

    /**
     * ????????????????????????
     *
     * @param minRecordTime
     */
    public void setMinRecordTime(int minRecordTime) {
        this.minRecordTime = minRecordTime;
    }

    /**
     * ??????Gop
     *
     * @param mGop
     */
    public void setGop(int mGop) {
        this.mGop = mGop;
    }

    /**
     * ????????????SurFaceView?????????
     */
    public void setReSizeRatioMode() {
        FrameLayout.LayoutParams params = getVideoLayoutParams();
        mRecorderSurfaceView.setLayoutParams(params);
        if (recorder != null) {
            recorder.resizePreviewSize(params.width, params.height);
        }
    }

    private FrameLayout.LayoutParams getVideoLayoutParams() {
        int screenWidth = ScreenUtils.getRealWidth(getContext());
        int screenHeight = ScreenUtils.getRealHeight(getContext());
        int ugsvTopBarHeight = getResources().getDimensionPixelSize(R.dimen.ugsv_recorder_top_bar_height);
        int height = 0;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(screenWidth, height);
        height = (int) (screenWidth / RecorderConfig.Companion.getInstance().getRatio());
        if (screenHeight - height >= ugsvTopBarHeight) {
            params.setMargins(0, ugsvTopBarHeight, 0, 0);
        } else {
            params.gravity = Gravity.BOTTOM;
        }
        params.height = height;
        return params;
    }

    public void setOnMusicSelectListener(AUIMusicSelectListener musicSelectListener) {
        this.mOutMusicSelectListener = musicSelectListener;
    }
}