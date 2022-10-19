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

    // 录制按钮
    private TextView mRecordDuration;
    private FrameLayout mRecordBtn;
    private AUIRecordProgressView mRecordBtnProgress;
    private ImageView mRecordBtnIcon;

    //录制模式
    private AUIRecordMode recordMode = AUIRecordMode.LONG_PRESS;

    //闪光灯类型
    private AUIFlashType flashType = AUIFlashType.OFF;
    //摄像头类型
    private AUICameraType mCameraType = AUICameraType.FRONT;
    private int mAspectRatio = AliyunSnapVideoParam.RATIO_MODE_9_16;

    // 已选择背景音乐
    private boolean mIsMusicSelected = false;

    // 录制按钮处于录制状态
    private boolean mRecordBtnRecording = false;
    // 录制按钮处于按压状态
    private boolean mRecordBtnPressing = false;

    // 是否已有录制片段
    private boolean mHasRecordedClip = false;
    // 是否可以合成
    private boolean mCanFinish = false;
    // 是否达到最大录制时长
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
        //Inflate布局
        LayoutInflater.from(getContext()).inflate(R.layout.ugsv_recorder_control_panel, this, true);
        assignViews();
        //设置view的监听事件
        setViewListener();
        //更新view的显示
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

        //变速栏
        mRateBar = findViewById(R.id.ugsv_recorder_rate_bar);
        aliyunRateQuarter = findViewById(R.id.aliyun_rate_quarter);
        aliyunRateHalf = findViewById(R.id.aliyun_rate_half);
        aliyunRateOrigin = findViewById(R.id.aliyun_rate_origin);
        aliyunRateDouble = findViewById(R.id.aliyun_rate_double);
        aliyunRateDoublePower2 = findViewById(R.id.aliyun_rate_double_power2);
        updateRateBtn(AUIRecordRate.STANDARD);

        //底部栏

        //拍摄模式选择器
        mModePicker = findViewById(R.id.alivc_video_picker_view);
        List<String> strings = new ArrayList<>(2);
        strings.add(getResources().getString(R.string.ugsv_recorder_record_mode_click));
        strings.add(getResources().getString(R.string.ugsv_recorder_record_mode_press));
        mModePicker.setData(strings);
        mModePicker.setCursorIcon(getResources().getColor(R.color.colourful_fill_strong));
        mModePicker.setSelectedPosition(1);//默认是 长按拍摄模式

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
     * 给各个view设置监听
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

        // 返回按钮
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

        // 准备录制
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

        // 闪光灯
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

        // 切换相机
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
        // 下一步(跳转编辑)
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
        // 点击美颜
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
        // 点击音乐
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
        // 点击回删
        mDeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDeleteClick();
                }
            }
        });
        // 点击动图
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
                    mListener.onChangeAspectRatioClick(mAspectRatio);
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
        //长按拍需求是按下就拍抬手停止拍
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
                //录制状态下抬手，便是停止录制
                if (mListener != null) {
                    Log.d(TAG, "onStopRecordClick");
                    mListener.onStopRecordClick();
                }
            } else if (!mRecordBtnPressing) {
                // 其他情况下抬手，只有点击模式才是启动录制
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
     * 录制状态变化
     */
    public void onRecordStateChanged(AUIRecordState recordState) {
        //倒计时中，控制面板隐藏
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
     * 录制片段发生变化
     *
     * @param recordedTime  已录制总时长
     * @param minRecordTime 最小录制时长
     * @param maxRecordTime 最大录制时长
     * @param clipTimeRatio 各个分段时长占比
     * @param updateView    是否更新UI
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
        switch (mAspectRatio) {
            case AliyunSnapVideoParam.RATIO_MODE_9_16:
                mAspectRatio = AliyunSnapVideoParam.RATIO_MODE_3_4;
                break;
            case AliyunSnapVideoParam.RATIO_MODE_3_4:
                mAspectRatio = AliyunSnapVideoParam.RATIO_MODE_1_1;
                break;
            default:
                mAspectRatio = AliyunSnapVideoParam.RATIO_MODE_9_16;
                break;
        }
        updateRatioBtn();
    }

    /***************************** 顶部栏 *****************************/
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
     * 闪光灯
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
     * 倒计时
     */
    private void updateCountdownBtn() {
        mCountdownBtn.setClickable(mRecordedMaxTime ? false : true);
    }

    /**
     * 摄像头切换
     */
    private void updateCameraBtn() {
        // TODO: 2022/6/11
    }

    /***************************** 侧边栏 *****************************/
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
     * 剪音乐
     */
    private void updateMusicBtn() {
        if (mHasRecordedClip) {
            //已经开始录制不允许更改音乐
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
     * 切画幅
     */
    private void updateRatioBtn() {
        if (mHasRecordedClip) {
            //已经开始录制不允许更改画幅
            switch (mAspectRatio) {
                case AliyunSnapVideoParam.RATIO_MODE_3_4:
                    mIVAspectRatio.setImageResource(R.drawable.ic_ugsv_recorder_ratio_3_4_off);
                    break;
                case AliyunSnapVideoParam.RATIO_MODE_1_1:
                    mIVAspectRatio.setImageResource(R.drawable.ic_ugsv_recorder_ratio_1_1_off);
                    break;
                default:
                    mIVAspectRatio.setImageResource(R.drawable.ic_ugsv_recorder_ratio_9_16_off);
                    break;
            }
            mTvAspectRatio.setTextColor(ContextCompat.getColor(getContext(), R.color.text_ultraweak));
        } else {
            switch (mAspectRatio) {
                case AliyunSnapVideoParam.RATIO_MODE_3_4:
                    mIVAspectRatio.setImageResource(R.drawable.ic_ugsv_recorder_ratio_3_4);
                    break;
                case AliyunSnapVideoParam.RATIO_MODE_1_1:
                    mIVAspectRatio.setImageResource(R.drawable.ic_ugsv_recorder_ratio_1_1);
                    break;
                default:
                    mIVAspectRatio.setImageResource(R.drawable.ic_ugsv_recorder_ratio_9_16);
                    break;
            }
            mTvAspectRatio.setTextColor(ContextCompat.getColor(getContext(), R.color.text_strong));
        }
    }

    /***************************** 变速栏 *****************************/
    private void updateRateBar(AUIRecordState recordState) {
        if (recordState == AUIRecordState.RECORD_PENDING
                || recordState == AUIRecordState.RECORDING) {
            mRateBar.setVisibility(GONE);
        } else {
            mRateBar.setVisibility(VISIBLE);
        }
    }

    /**
     * 变速按钮
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

    /***************************** 底部栏 *****************************/
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
     * 回删按钮
     */
    private void updateDeleteBtn() {
        mDeleteBtn.setVisibility(mHasRecordedClip ? View.VISIBLE : View.GONE);
    }

    /**
     * 完成按钮
     */
    private void updateFinishBtn() {
        mFinishBtn.setVisibility(mHasRecordedClip ? View.VISIBLE : View.GONE);
        mFinishBtn.setImageAlpha(mCanFinish ? 255 : 128);
        mFinishBtn.setClickable(mCanFinish);
    }

    /**
     * 模式选择器
     */
    private void updateModePicker() {
        mModePicker.setVisibility(mHasRecordedClip ? View.GONE : View.VISIBLE);
    }

    /**
     * 拍摄按钮
     */
    private void updateRecordBtn(AUIRecordState recordState) {
        mRecordBtnRecording = recordState == AUIRecordState.RECORDING;
        if (recordState != AUIRecordState.RECORDING) {
            //等待录制
            mRecordDuration.setVisibility(View.GONE);
            mRecordBtnIcon.setVisibility(View.VISIBLE);
            mRecordBtnIcon.setImageResource(recordMode == AUIRecordMode.SINGLE_CLICK
                    ? R.drawable.ic_ugsv_recorder_record_btn
                    : R.drawable.ic_ugsv_recorder_record_btn_longpress);
        } else if (mRecordBtnPressing) {
            //长按录制中
            mRecordDuration.setVisibility(View.VISIBLE);
            mRecordBtnIcon.setVisibility(View.GONE);
        } else {
            //点击录制中
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
     * 设置录制事件，录制过程中持续被调用
     */
    public void setRecordTime(long recordTime, long maxTime) {
        mRecordDuration.setText(TimeFormatterUtils.formatTime(recordTime));
        mRecordBtnProgress.setProgress(Math.round(recordTime / (float) maxTime * 100.0f));
    }

    /**
     * 添加各个控件点击监听
     *
     * @param mListener
     */
    public void setControlViewListener(AUIControlViewListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 设置摄像头类型，并刷新页面，摄像头切换后被调用
     *
     * @param cameraType
     */
    public void setCameraType(AUICameraType cameraType) {
        mCameraType = cameraType;
        updateFlashBtn();
    }

    /**
     * 设置应用音乐icon
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
     * 设置应用音乐icon
     *
     * @param id
     */
    public void setMusicIconId(@DrawableRes int id, boolean isMusicSelected) {
        mIvMusicIcon.setImageResource(id);
        mIsMusicSelected = isMusicSelected;
    }

    /**
     * 设置画幅比例
     */
    public void setAspectRatio(int radio) {
        mAspectRatio = radio;
        updateRatioBtn();
    }
}
