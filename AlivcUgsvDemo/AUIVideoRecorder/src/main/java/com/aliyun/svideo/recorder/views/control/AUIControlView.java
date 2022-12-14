package com.aliyun.svideo.recorder.views.control;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.svideo.base.utils.FastClickUtil;
import com.aliyun.svideo.recorder.RecorderConfig;
import com.aliyun.ugsv.common.utils.image.ImageLoaderImpl;
import com.aliyun.ugsv.common.utils.image.ImageLoaderOptions;
import com.aliyun.svideo.recorder.R;
import com.aliyun.svideo.recorder.utils.TimeFormatterUtils;
import com.aliyun.svideo.recorder.views.AUIRecordProgressView;
import com.aliyun.svideo.recorder.views.BaseScrollPickerView;
import com.aliyun.svideo.recorder.views.StringScrollPicker;
import com.aliyun.svideosdk.common.struct.common.AliyunSnapVideoParam;

import java.util.ArrayList;
import java.util.List;

public class AUIControlView extends RelativeLayout implements View.OnTouchListener {
    private static final String TAG = AUIControlView.class.getSimpleName();
    private LinearLayout mBeautyBtn;
    private LinearLayout mPropsBtn;
    private ImageView mCountdownBtn;
    private ImageView mLightBtn;
    private ImageView aliyunSwitchCamera;
    public ImageView mFinishBtn;
    private ImageView aliyunBack;
    private LinearLayout mRateBar;
    private TextView aliyunRateQuarter;
    private TextView aliyunRateHalf;
    private TextView aliyunRateOrigin;
    private TextView aliyunRateDouble;
    private TextView aliyunRateDoublePower2;

    private TextView mDeleteBtn;
    private LinearLayout mMusicBtn;
    private View mTopBar;
    private StringScrollPicker mModePicker;
    private AUIControlViewListener mListener;
    private ImageView mIvMusicIcon;
    private LinearLayout mFilterBtn;
    private TextView mTvMusic;
    private LinearLayout mRatioBtn;
    private TextView mTvAspectRatio;
    private ImageView mIVAspectRatio;
    private View mEffectBtn;
    private View mPhotoBtn;

    // ????????????
    private TextView mRecordDuration;
    private FrameLayout mRecordBtn;
    private AUIRecordProgressView mRecordBtnProgress;
    private ImageView mRecordBtnIcon;

    //????????????
    private AUIRecordMode recordMode = AUIRecordMode.LONG_PRESS;

    //???????????????
    private AUIFlashType flashType = AUIFlashType.OFF;
    //???????????????
    private AUICameraType mCameraType = AUICameraType.FRONT;

    // ?????????????????????
    private boolean mIsMusicSelected = false;

    // ??????????????????????????????
    private boolean mRecordBtnRecording = false;
    // ??????????????????????????????
    private boolean mRecordBtnPressing = false;

    // ????????????????????????
    private boolean mHasRecordedClip = false;
    // ??????????????????
    private boolean mCanFinish = false;
    // ??????????????????????????????
    private boolean mRecordedMaxTime = false;

    public AUIControlView(Context context) {
        this(context, null);
    }

    public AUIControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AUIControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //Inflate??????
        LayoutInflater.from(getContext()).inflate(R.layout.ugsv_recorder_control_panel, this, true);
        assignViews();
        //??????view???????????????
        setViewListener();
        //??????view?????????
        onRecordStateChanged(AUIRecordState.WAITING);
    }

    private void assignViews() {
        mRatioBtn = findViewById(R.id.ugsv_recorder_ratio);
        mEffectBtn = findViewById(R.id.alivc_record_anim_filter);
        mPhotoBtn = findViewById(R.id.alivc_record_take_photo);
        mTvAspectRatio = findViewById(R.id.alivc_record_aspect_ratio_tv_change);
        mIVAspectRatio = findViewById(R.id.alivc_aspect_iv_ratio);
        mCountdownBtn = findViewById(R.id.ugsv_recorder_countdown);
        mLightBtn = findViewById(R.id.ugsv_recorder_flash);
        mFilterBtn = findViewById(R.id.ugsv_recorder_filter);
        aliyunSwitchCamera = findViewById(R.id.ugsv_recorder_camera_switch);
        mIvMusicIcon = findViewById(R.id.alivc_record_iv_music);
        mFinishBtn = findViewById(R.id.ugsv_recorder_finish_btn);
        aliyunBack = findViewById(R.id.ugsv_recorder_back);

        //?????????
        mRateBar = findViewById(R.id.ugsv_recorder_rate_bar);
        aliyunRateQuarter = findViewById(R.id.aliyun_rate_quarter);
        aliyunRateHalf = findViewById(R.id.aliyun_rate_half);
        aliyunRateOrigin = findViewById(R.id.aliyun_rate_origin);
        aliyunRateDouble = findViewById(R.id.aliyun_rate_double);
        aliyunRateDoublePower2 = findViewById(R.id.aliyun_rate_double_power2);
        updateRateBtn(AUIRecordRate.STANDARD);

        //?????????

        //?????????????????????
        mModePicker = findViewById(R.id.alivc_video_picker_view);
        List<String> strings = new ArrayList<>(2);
        strings.add(getResources().getString(R.string.ugsv_recorder_record_mode_click));
        strings.add(getResources().getString(R.string.ugsv_recorder_record_mode_press));
        mModePicker.setData(strings);
        mModePicker.setCursorIcon(getResources().getColor(R.color.colourful_fill_strong));
        mModePicker.setSelectedPosition(1);//????????? ??????????????????

        mRecordDuration = findViewById(R.id.ugsv_recorder_record_time);
        mRecordBtn = findViewById(R.id.ugsv_recorder_record_btn);
        mRecordBtnProgress = findViewById(R.id.ugsv_recorder_record_btn_progress);
        mRecordBtnIcon = findViewById(R.id.ugsv_recorder_record_btn_icon);

        mDeleteBtn = findViewById(R.id.ugsv_recorder_delete_btn);
        mBeautyBtn = findViewById(R.id.ugsv_recorder_beauty);
        mPropsBtn = findViewById(R.id.ugsv_recorder_props);

        mTopBar = findViewById(R.id.ugsv_recorder_top_bar);
        mMusicBtn = findViewById(R.id.ugsv_recorder_music);
        mTvMusic = findViewById(R.id.tv_music);
    }

    /**
     * ?????????view????????????
     */
    private void setViewListener() {
        mModePicker.setOnSelectedListener(new BaseScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(BaseScrollPickerView baseScrollPickerView, int position) {
                Log.i(TAG, "onSelected:" + position);
                if (position == 0) {
                    recordMode = AUIRecordMode.SINGLE_CLICK;
                } else {
                    recordMode = AUIRecordMode.LONG_PRESS;
                }
                updateRecordBtn(AUIRecordState.WAITING);
            }
        });

        // ????????????
        aliyunBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onBackClick();
                }
            }
        });

        // ????????????
        mCountdownBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onCountdownClick();
                }
            }
        });

        // ?????????
        mLightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mCameraType == AUICameraType.FRONT) {
                    AVToast.show(getContext(), true,
                            getResources().getString(R.string.ugsv_recorder_toast_flash_disable));
                    return;
                }
                if (flashType == AUIFlashType.ON) {
                    flashType = AUIFlashType.OFF;
                } else {
                    flashType = AUIFlashType.ON;
                }
                updateFlashBtn();
                if (mListener != null) {
                    mListener.onLightSwitch(flashType);
                }
            }
        });

        // ????????????
        aliyunSwitchCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onCameraSwitch();
                }
            }
        });
        // ?????????(????????????)
        mFinishBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onFinishClick();
                }
            }
        });
        aliyunRateQuarter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onRateSelect(AUIRecordRate.VERY_FLOW.getRate());
                }
                updateRateBtn(AUIRecordRate.VERY_FLOW);
            }
        });
        aliyunRateHalf.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onRateSelect(AUIRecordRate.FLOW.getRate());
                }
                updateRateBtn(AUIRecordRate.FLOW);
            }
        });
        aliyunRateOrigin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onRateSelect(AUIRecordRate.STANDARD.getRate());
                }
                updateRateBtn(AUIRecordRate.STANDARD);
            }
        });
        aliyunRateDouble.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onRateSelect(AUIRecordRate.FAST.getRate());
                }
                updateRateBtn(AUIRecordRate.FAST);
            }
        });
        aliyunRateDoublePower2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onRateSelect(AUIRecordRate.VERY_FAST.getRate());
                }
                updateRateBtn(AUIRecordRate.VERY_FAST);
            }
        });
        // ????????????
        mBeautyBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    if (FastClickUtil.isFastClick()) {
                        return;
                    }
                    mListener.onBeautyFaceClick();
                }
            }
        });
        // ????????????
        mMusicBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mHasRecordedClip) {
                    AVToast.show(getContext(), true,
                            getResources().getString(R.string.ugsv_recorder_toast_disable_after_recording));
                    return;
                }
                if (mListener != null) {
                    mListener.onMusicClick();
                }
            }
        });
        // ????????????
        mDeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDeleteClick();
                }
            }
        });
        // ????????????
        mPropsBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onGifEffectClick();
                }
            }
        });
        mFilterBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onFilterEffectClick();
                }
            }
        });
        mRatioBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mHasRecordedClip) {
                    AVToast.show(getContext(), true,
                            getResources().getString(R.string.ugsv_recorder_toast_disable_after_recording));
                    return;
                }
                changeAspectRatio();
                if (mListener != null) {
                    mListener.onChangeAspectRatioClick();
                }

            }
        });
        mEffectBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }

                if (mListener != null) {
                    mListener.onAnimFilterClick();
                }
            }
        });
        mPhotoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }

                if (mListener != null) {
                    mListener.onTakePhotoClick();
                }
            }
        });
        //?????????????????????????????????????????????
        mRecordBtn.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (FastClickUtil.isRecordWithOtherClick()) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "RecordBtn Down: "
                    + (recordMode == AUIRecordMode.LONG_PRESS ? "LONG_PRESS" : "SINGLE_CLICK")
                    + ", recording=" + (mRecordBtnRecording ? "true" : "false")
                    + ", btnPressing=" + (mRecordBtnPressing ? "true" : "false"));
            if (recordMode == AUIRecordMode.LONG_PRESS && !mRecordBtnRecording) {
                mRecordBtnPressing = true;
                if (mListener != null) {
                    Log.d(TAG, "onStartRecordClick");
                    mListener.onStartRecordClick();
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                || event.getAction() == MotionEvent.ACTION_UP) {
            Log.d(TAG, "RecordBtn UP  : "
                    + (recordMode == AUIRecordMode.LONG_PRESS ? "LONG_PRESS" : "SINGLE_CLICK")
                    + ", recording=" + (mRecordBtnRecording ? "true" : "false")
                    + ", btnPressing=" + (mRecordBtnPressing ? "true" : "false"));
            if (mRecordBtnRecording) {
                //??????????????????????????????????????????
                if (mListener != null) {
                    Log.d(TAG, "onStopRecordClick");
                    mListener.onStopRecordClick();
                }
            } else if (!mRecordBtnPressing) {
                // ????????????????????????????????????????????????????????????
                if (mListener != null) {
                    Log.d(TAG, "onStartRecordClick");
                    mListener.onStartRecordClick();
                }
            }
            mRecordBtnPressing = false;
        }
        return true;
    }

    /**
     * ??????????????????
     */
    public void onRecordStateChanged(AUIRecordState recordState) {
        //?????????????????????????????????
        if (recordState == AUIRecordState.RECORD_PENDING) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            updateTopBar(recordState);
            updateSideBar(recordState);
            updateRateBar(recordState);
            updateBottomBar(recordState);
        }
    }

    /**
     * ????????????????????????
     *
     * @param recordedTime  ??????????????????
     * @param minRecordTime ??????????????????
     * @param maxRecordTime ??????????????????
     * @param clipTimeRatio ????????????????????????
     * @param updateView    ????????????UI
     */
    public void onRecordedClipsChanged(int recordedTime,
                                       int minRecordTime,
                                       int maxRecordTime,
                                       @NonNull List<Float> clipTimeRatio,
                                       boolean updateView) {
        mCanFinish = recordedTime >= minRecordTime;
        mRecordedMaxTime = recordedTime >= maxRecordTime;
        mHasRecordedClip = recordedTime > 0;
        mRecordBtnProgress.updateClipTimeRatioList(clipTimeRatio);
        setRecordTime(recordedTime, maxRecordTime);
        if (updateView) {
            updateCountdownBtn();
            updateMusicBtn();
            updateRatioBtn();
            updateDeleteBtn();
            updateFinishBtn();
            updateModePicker();
        }
    }

    private void changeAspectRatio() {
        if (RecorderConfig.Companion.getInstance().getRatio() == RecorderConfig.RATIO_MODE_9_16) {
            RecorderConfig.Companion.getInstance().setRatio(RecorderConfig.RATIO_MODE_3_4);
        } else if (RecorderConfig.Companion.getInstance().getRatio() == RecorderConfig.RATIO_MODE_3_4) {
            RecorderConfig.Companion.getInstance().setRatio(RecorderConfig.RATIO_MODE_1_1);
        } else if (RecorderConfig.Companion.getInstance().getRatio() == RecorderConfig.RATIO_MODE_1_1) {
            RecorderConfig.Companion.getInstance().setRatio(RecorderConfig.RATIO_MODE_9_16);
        }
        updateRatioBtn();
    }

    /***************************** ????????? *****************************/
    private void updateTopBar(AUIRecordState recordState) {
        if (recordState == AUIRecordState.RECORD_PENDING
                || recordState == AUIRecordState.RECORDING) {
            // hide top bar
            mTopBar.setVisibility(View.GONE);
        } else {
            mTopBar.setVisibility(VISIBLE);
            updateFlashBtn();
            updateCountdownBtn();
            updateCameraBtn();
        }
    }

    /**
     * ?????????
     */
    private void updateFlashBtn() {
        if (mCameraType == AUICameraType.FRONT) {
            mLightBtn.setImageResource(R.drawable.ic_ugsv_recorder_flash_off);
        } else if (mCameraType == AUICameraType.BACK) {
            switch (flashType) {
                case ON:
                    mLightBtn.setImageResource(R.drawable.ic_ugsv_recorder_flash_on);
                    break;
                case OFF:
                    mLightBtn.setImageResource(R.drawable.ic_ugsv_recorder_flash_off);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * ?????????
     */
    private void updateCountdownBtn() {
        mCountdownBtn.setClickable(mRecordedMaxTime ? false : true);
    }

    /**
     * ???????????????
     */
    private void updateCameraBtn() {
        // TODO: 2022/6/11
    }

    /***************************** ????????? *****************************/
    private void updateSideBar(AUIRecordState recordState) {
        if (recordState == AUIRecordState.RECORD_PENDING
                || recordState == AUIRecordState.RECORDING
                || recordState == AUIRecordState.RECORD_STOPPING) {
            mMusicBtn.setVisibility(GONE);
            mFilterBtn.setVisibility(GONE);
            mRatioBtn.setVisibility(GONE);
            mEffectBtn.setVisibility(GONE);
            mPhotoBtn.setVisibility(GONE);
        } else {
            mMusicBtn.setVisibility(VISIBLE);
            mFilterBtn.setVisibility(VISIBLE);
            mRatioBtn.setVisibility(VISIBLE);
            mEffectBtn.setVisibility(VISIBLE);
            mPhotoBtn.setVisibility(VISIBLE);
            updateMusicBtn();
            updateRatioBtn();
        }
    }

    /**
     * ?????????
     */
    private void updateMusicBtn() {
        if (mHasRecordedClip) {
            //???????????????????????????????????????
            if (mIsMusicSelected) {
                mIvMusicIcon.setImageAlpha(128);
            } else {
                mIvMusicIcon.setImageResource(R.drawable.ic_ugsv_recorder_music_off);
            }
            mTvMusic.setTextColor(ContextCompat.getColor(getContext(), R.color.text_ultraweak));
        } else {
            if (mIsMusicSelected) {
                mIvMusicIcon.setImageAlpha(255);
            } else {
                mIvMusicIcon.setImageResource(R.drawable.ic_ugsv_recorder_music);
            }
            mTvMusic.setTextColor(ContextCompat.getColor(getContext(), R.color.text_strong));
        }
    }

    /**
     * ?????????
     */
    private void updateRatioBtn() {
        float ratio = RecorderConfig.Companion.getInstance().getRatio();
        if (mHasRecordedClip) {
            //???????????????????????????????????????
            if (ratio == RecorderConfig.RATIO_MODE_9_16) {
                mIVAspectRatio.setImageResource(R.drawable.ic_ugsv_recorder_ratio_9_16_off);
            } else if (ratio == RecorderConfig.RATIO_MODE_3_4) {
                mIVAspectRatio.setImageResource(R.drawable.ic_ugsv_recorder_ratio_3_4_off);
            } else if (ratio == RecorderConfig.RATIO_MODE_1_1) {
                mIVAspectRatio.setImageResource(R.drawable.ic_ugsv_recorder_ratio_1_1_off);
            }
            mTvAspectRatio.setTextColor(ContextCompat.getColor(getContext(), R.color.text_ultraweak));
        } else {
            if (ratio == RecorderConfig.RATIO_MODE_9_16) {
                mIVAspectRatio.setImageResource(R.drawable.ic_ugsv_recorder_ratio_9_16);
            } else if (ratio == RecorderConfig.RATIO_MODE_3_4) {
                mIVAspectRatio.setImageResource(R.drawable.ic_ugsv_recorder_ratio_3_4);
            } else if (ratio == RecorderConfig.RATIO_MODE_1_1) {
                mIVAspectRatio.setImageResource(R.drawable.ic_ugsv_recorder_ratio_1_1);
            }
            mTvAspectRatio.setTextColor(ContextCompat.getColor(getContext(), R.color.text_strong));
        }
    }

    /***************************** ????????? *****************************/
    private void updateRateBar(AUIRecordState recordState) {
        if (recordState == AUIRecordState.RECORD_PENDING
                || recordState == AUIRecordState.RECORDING) {
            mRateBar.setVisibility(GONE);
        } else {
            mRateBar.setVisibility(VISIBLE);
        }
    }

    /**
     * ????????????
     */
    private void updateRateBtn(AUIRecordRate recordRate) {
        aliyunRateQuarter.setSelected(false);
        aliyunRateHalf.setSelected(false);
        aliyunRateOrigin.setSelected(false);
        aliyunRateDouble.setSelected(false);
        aliyunRateDoublePower2.setSelected(false);
        switch (recordRate) {
            case VERY_FLOW:
                aliyunRateQuarter.setSelected(true);
                break;
            case FLOW:
                aliyunRateHalf.setSelected(true);
                break;
            case FAST:
                aliyunRateDouble.setSelected(true);
                break;
            case VERY_FAST:
                aliyunRateDoublePower2.setSelected(true);
                break;
            default:
                aliyunRateOrigin.setSelected(true);
        }
    }

    /***************************** ????????? *****************************/
    private void updateBottomBar(AUIRecordState recordState) {
        if (recordState == AUIRecordState.RECORD_PENDING
                || recordState == AUIRecordState.RECORDING) {
            mBeautyBtn.setVisibility(View.GONE);
            mPropsBtn.setVisibility(View.GONE);
            mDeleteBtn.setVisibility(GONE);
            mFinishBtn.setVisibility(GONE);
            mModePicker.setVisibility(View.GONE);
        } else {
            mBeautyBtn.setVisibility(View.VISIBLE);
            mPropsBtn.setVisibility(View.VISIBLE);
            updateDeleteBtn();
            updateFinishBtn();
            if (recordState != AUIRecordState.RECORD_STOPPING) {
                updateModePicker();
            }
        }
        updateRecordBtn(recordState);
    }

    /**
     * ????????????
     */
    private void updateDeleteBtn() {
        mDeleteBtn.setVisibility(mHasRecordedClip ? View.VISIBLE : View.GONE);
    }

    /**
     * ????????????
     */
    private void updateFinishBtn() {
        mFinishBtn.setVisibility(mHasRecordedClip ? View.VISIBLE : View.GONE);
        mFinishBtn.setImageAlpha(mCanFinish ? 255 : 128);
        mFinishBtn.setClickable(mCanFinish);
    }

    /**
     * ???????????????
     */
    private void updateModePicker() {
        mModePicker.setVisibility(mHasRecordedClip ? View.GONE : View.VISIBLE);
    }

    /**
     * ????????????
     */
    private void updateRecordBtn(AUIRecordState recordState) {
        mRecordBtnRecording = recordState == AUIRecordState.RECORDING;
        if (recordState != AUIRecordState.RECORDING) {
            //????????????
            mRecordDuration.setVisibility(View.GONE);
            mRecordBtnIcon.setVisibility(View.VISIBLE);
            mRecordBtnIcon.setImageResource(recordMode == AUIRecordMode.SINGLE_CLICK
                    ? R.drawable.ic_ugsv_recorder_record_btn
                    : R.drawable.ic_ugsv_recorder_record_btn_longpress);
        } else if (mRecordBtnPressing) {
            //???????????????
            mRecordDuration.setVisibility(View.VISIBLE);
            mRecordBtnIcon.setVisibility(View.GONE);
        } else {
            //???????????????
            mRecordDuration.setVisibility(View.VISIBLE);
            mRecordBtnIcon.setVisibility(View.VISIBLE);
            mRecordBtnIcon.setImageResource(R.drawable.ic_ugsv_recorder_record_btn_recording);
        }
    }

    public AUIFlashType getFlashType() {
        return flashType;
    }

    public void setFlashType(AUIFlashType flashType) {
        this.flashType = flashType;
        updateFlashBtn();
    }

    public AUICameraType getCameraType() {
        return mCameraType;
    }

    /**
     * ???????????????????????????????????????????????????
     */
    public void setRecordTime(long recordTime, long maxTime) {
        mRecordDuration.setText(TimeFormatterUtils.formatTime(recordTime));
        mRecordBtnProgress.setProgress(Math.round(recordTime / (float) maxTime * 100.0f));
    }

    /**
     * ??????????????????????????????
     *
     * @param mListener
     */
    public void setControlViewListener(AUIControlViewListener mListener) {
        this.mListener = mListener;
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     *
     * @param cameraType
     */
    public void setCameraType(AUICameraType cameraType) {
        mCameraType = cameraType;
        updateFlashBtn();
    }

    /**
     * ??????????????????icon
     *
     * @param icon
     */
    public void setMusicIcon(String icon, boolean isMusicSelected) {
        if (mIvMusicIcon == null) {
            return;
        }
        new ImageLoaderImpl()
                .loadImage(getContext(), icon, new ImageLoaderOptions.Builder()
                        .circle()
                        .error(R.drawable.ic_ugsv_recorder_music)
                        .crossFade()
                        .build())
                .into(mIvMusicIcon);
        mIsMusicSelected = isMusicSelected;
    }

    /**
     * ??????????????????icon
     *
     * @param id
     */
    public void setMusicIconId(@DrawableRes int id, boolean isMusicSelected) {
        mIvMusicIcon.setImageResource(id);
        mIsMusicSelected = isMusicSelected;
    }

}
