package com.aliyun.svideo.mixrecorder.view.control;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
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

import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.svideo.base.AliyunSnapVideoParam;
import com.aliyun.svideo.record.R;
import com.aliyun.svideo.mixrecorder.view.AUIMixRecordProgressView;
import com.aliyun.svideo.mixrecorder.view.BaseScrollPickerView;
import com.aliyun.svideo.mixrecorder.view.StringScrollPicker;
import com.aliyun.svideo.base.UIConfigManager;
import com.aliyun.svideo.base.utils.FastClickUtil;

import java.util.ArrayList;
import java.util.List;

public class AUIMixRecordControlView extends RelativeLayout implements View.OnTouchListener {
    private static final String TAG = AUIMixRecordControlView.class.getSimpleName();
    private static final int MAX_ITEM_COUNT = 5;
    private LinearLayout llBeautyFace;
    private LinearLayout llGifEffect;
    private ImageView ivReadyRecord;
    private ImageView aliyunSwitchLight;
    private ImageView aliyunSwitchCamera;
    public View aliyunComplete;
    private ImageView aliyunBack;
    private LinearLayout aliyunRecordLayoutBottom;
    private LinearLayout aliyunRateBar;
    private TextView aliyunRateQuarter;
    private TextView aliyunRateHalf;
    private TextView aliyunRateOrigin;
    private TextView aliyunRateDouble;
    private TextView aliyunRateDoublePower2;
    private TextView aliyunRecordDuration;
    private FrameLayout aliyunRecordBtn;
    private AUIMixRecordProgressView mRecordBtnProgress;
    private ImageView mRecordBtnIcon;
    private TextView aliyunDelete;
    private View mTitleView;
    private StringScrollPicker mPickerView;
    private ControlViewListener mListener;
    private LinearLayout mLlFilterEffect;
    private View mLayoutOptions;
    private ImageView mLayoutOptionsIcon;
    private View mAlivcAnimFilter;
    //录制模式
    private RecordMode recordMode = RecordMode.LONG_PRESS;
    //是否有录制片段，true可以删除，不可选择音乐、拍摄模式view消失
    private boolean hasRecordPiece = false;
    //是否可以完成录制，录制时长大于最小录制时长时为true
    private boolean canComplete = false;
    //音乐选择是否弹出
    private boolean isMusicSelViewShow = false;
    //其他弹窗选择是否弹出
    private boolean isEffectSelViewShow = false;
    //闪光灯类型
    private FlashType flashType = FlashType.OFF;
    //摄像头类型
    private CameraType cameraType = CameraType.FRONT;
    //录制速度
    private RecordRate recordRate = RecordRate.STANDARD;
    //录制状态，开始、暂停、准备,只是针对UI变化
    private RecordState recordState = RecordState.STOP;
    //录制按钮宽度
    private int itemWidth;
    //是否倒计时拍摄中
    private boolean isCountDownRecording = false;
    //是否实际正在录制，由于结束录制时UI立刻变化，但是尚未真正结束录制，所以此时不能继续录制视频否则会崩溃
    private boolean isRecording = false;
    //
    private int mAspectRatio = AliyunSnapVideoParam.RATIO_MODE_9_16;
    //录制类型 合拍true 正常录制false
    private Boolean mIsMixRecorderType = false;

    public AUIMixRecordControlView(Context context) {
        this(context, null);
    }

    public AUIMixRecordControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AUIMixRecordControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        calculateItemWidth();
        //Inflate布局
        LayoutInflater.from(getContext()).inflate(R.layout.alivc_recorder_view_control, this, true);
        assignViews();
        //设置view的监听事件
        setViewListener();
        //更新view的显示
        updateAllViews();
    }

    private void assignViews() {
        mAlivcAnimFilter = findViewById(R.id.alivc_record_anim_filter);
        ivReadyRecord = (ImageView) findViewById(R.id.ugsv_recorder_countdown);
        aliyunSwitchLight = (ImageView) findViewById(R.id.aliyun_switch_light);
        mLlFilterEffect = findViewById(R.id.alivc_record_effect_filter);
        mLayoutOptions = findViewById(R.id.alivc_record_layout);
        mLayoutOptionsIcon = findViewById(R.id.alivc_record_layout_icon);
        aliyunSwitchCamera = (ImageView) findViewById(R.id.aliyun_switch_camera);
        aliyunComplete = findViewById(R.id.ugsv_mix_recorder_finish_btn);
        aliyunBack = (ImageView) findViewById(R.id.aliyun_back);
        aliyunRecordLayoutBottom = (LinearLayout) findViewById(R.id.aliyun_record_layout_bottom);
        aliyunRateBar = (LinearLayout) findViewById(R.id.aliyun_rate_bar);
        aliyunRateQuarter = (TextView) findViewById(R.id.aliyun_rate_quarter);
        aliyunRateHalf = (TextView) findViewById(R.id.aliyun_rate_half);
        aliyunRateOrigin = (TextView) findViewById(R.id.aliyun_rate_origin);
        aliyunRateDouble = (TextView) findViewById(R.id.aliyun_rate_double);
        aliyunRateDoublePower2 = (TextView) findViewById(R.id.aliyun_rate_double_power2);
        aliyunRecordDuration = (TextView) findViewById(R.id.ugsv_recorder_record_time);
        aliyunRecordBtn = (FrameLayout) findViewById(R.id.ugsv_recorder_record_btn);
        mRecordBtnProgress = findViewById(R.id.ugsv_recorder_record_btn_progress);
        mRecordBtnIcon = findViewById(R.id.ugsv_recorder_record_btn_icon);
        aliyunDelete = (TextView) findViewById(R.id.ugsv_recorder_delete_btn);
        llBeautyFace = findViewById(R.id.ll_beauty_face);
        llGifEffect = findViewById(R.id.ll_gif_effect);
        mPickerView = findViewById(R.id.alivc_video_picker_view);
        mTitleView = findViewById(R.id.alivc_record_title_view);

        List<String> strings = new ArrayList<>(2);
        strings.add(getResources().getString(R.string.alivc_recorder_control_click));
        strings.add(getResources().getString(R.string.alivc_recorder_control_long_press));
        mPickerView.setData(strings);
        mPickerView.setCursorIcon(getResources().getColor(R.color.colourful_fill_strong));
        mPickerView.setSelectedPosition(1);//默认是 长按拍摄模式
    }

    /**
     * 获取切换摄像头的图片的selector
     *
     * @return Drawable
     */
    private Drawable getSwitchCameraDrawable() {

        Drawable drawable = UIConfigManager.getDrawableResources(getContext(), R.attr.switchCameraImage, R.mipmap.ic_ugsv_recorder_camera);
        Drawable pressDrawable = drawable.getConstantState().newDrawable().mutate();
        pressDrawable.setAlpha(66);//透明度60%
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[] {android.R.attr.state_pressed},
                                   pressDrawable);
        stateListDrawable.addState(new int[] {},
                                   drawable);
        return stateListDrawable;
    }

    //更新进度按钮
    public void updateClipRange(List<Float> ratioTimes){
        mRecordBtnProgress.updateClipTimeRatioList(ratioTimes);
    }

    public void updateProgress(int progress){
        mRecordBtnProgress.setProgress(progress);
    }

    private Drawable getSwitchVoiceDrawable(boolean voiceOff) {
        Drawable drawable;
        if (voiceOff) {
            drawable = ContextCompat.getDrawable(getContext(), R.mipmap.alivc_svideo_icon_voice_off);
        } else {
            drawable = ContextCompat.getDrawable(getContext(), R.mipmap.alivc_svideo_icon_voice_on);

        }
        return drawable;
    }

    /**
     * 给各个view设置监听
     */
    private void setViewListener() {
        mPickerView.setOnSelectedListener(new BaseScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(BaseScrollPickerView baseScrollPickerView, int position) {
                Log.i(TAG, "onSelected:" + position);
                //if (FastClickUtil.isFastClick()) {
                //    return;
                //}
                if (position == 0) {
                    recordMode = RecordMode.SINGLE_CLICK;
                } else {
                    recordMode = RecordMode.LONG_PRESS;
                }
                updateRecordBtnView();
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
        ivReadyRecord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (isRecording) {
                    return;
                }
                if (recordState == RecordState.STOP) {
                    recordState = RecordState.READY;
                    updateAllViews();
                    if (mListener != null) {
                        mListener.onReadyRecordClick(false);
                    }
                } else {
                    recordState = RecordState.STOP;
                    if (mListener != null) {
                        mListener.onReadyRecordClick(true);
                    }
                }

            }
        });

        // 闪光灯
        aliyunSwitchLight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }

                if (cameraType == CameraType.FRONT) {
                    AVToast.show(getContext(), true,
                            getResources().getString(R.string.ugsv_mix_recorder_toast_flash_disable));
                    return;
                }

                if (flashType == FlashType.ON) {
                    flashType = FlashType.OFF;
                } else {
                    flashType = FlashType.ON;
                }

                updateLightSwitchView();
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
        aliyunComplete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if (mListener != null) {
                    mListener.onNextClick();
                }
            }
        });
        aliyunRateQuarter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                recordRate = RecordRate.VERY_FLOW;
                if (mListener != null) {
                    mListener.onRateSelect(recordRate.getRate());
                }
                updateRateItemView();
            }
        });
        aliyunRateHalf.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                recordRate = RecordRate.FLOW;
                if (mListener != null) {
                    mListener.onRateSelect(recordRate.getRate());
                }
                updateRateItemView();
            }
        });
        aliyunRateOrigin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                recordRate = RecordRate.STANDARD;
                if (mListener != null) {
                    mListener.onRateSelect(recordRate.getRate());
                }
                updateRateItemView();
            }
        });
        aliyunRateDouble.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                recordRate = RecordRate.FAST;
                if (mListener != null) {
                    mListener.onRateSelect(recordRate.getRate());
                }
                updateRateItemView();
            }
        });
        aliyunRateDoublePower2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                recordRate = RecordRate.VERY_FAST;
                if (mListener != null) {
                    mListener.onRateSelect(recordRate.getRate());
                }
                updateRateItemView();
            }
        });
        // 点击美颜
        llBeautyFace.setOnClickListener(new OnClickListener() {

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
        // 点击回删
        aliyunDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDeleteClick();
                }
            }
        });
        // 点击动图
        llGifEffect.setOnClickListener(new OnClickListener() {
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
        mLlFilterEffect.setOnClickListener(new OnClickListener() {
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

        mLayoutOptions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                if(mListener != null){
                    mListener.onLayoutOptionClick();
                }
            }
        });

        mAlivcAnimFilter.setOnClickListener(new OnClickListener() {
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

        //长按拍需求是按下就拍抬手停止拍
        aliyunRecordBtn.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (FastClickUtil.isRecordWithOtherClick()) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (recordState != RecordState.COUNT_DOWN_RECORDING && recordMode == RecordMode.LONG_PRESS) {
                if (isRecording) {
                    return true;
                } else {
                    if (mListener != null) {
                        mListener.onStartRecordClick();
                    }
                }
            }

        } else if (event.getAction() == MotionEvent.ACTION_CANCEL
                   || event.getAction() == MotionEvent.ACTION_UP) {
            if (recordState == RecordState.COUNT_DOWN_RECORDING) {
                if (mListener != null) {
                    mListener.onStopRecordClick();
                    setRecordState(RecordState.STOP);
                    //停止拍摄后立即展示回删
                    if (hasRecordPiece) {
                        setHasRecordPiece(true);
                    }

                }
            } else {
                if (recordMode == RecordMode.LONG_PRESS) {
                    if (mListener != null && recordState == RecordState.RECORDING) {
                        mListener.onStopRecordClick();
                        setRecordState(RecordState.STOP);
                        //停止拍摄后立即展示回删
                        if (hasRecordPiece) {
                            setHasRecordPiece(true);
                        }
                    }
                } else {
                    if (recordState == RecordState.RECORDING) {
                        if (mListener != null) {
                            mListener.onStopRecordClick();
                            setRecordState(RecordState.STOP);
                            //停止拍摄后立即展示回删
                            if (hasRecordPiece) {
                                setHasRecordPiece(true);
                            }
                        }
                    } else {
                        if (mListener != null && !isRecording) {
                            mListener.onStartRecordClick();
                        }
                    }
                }
            }

        }
        return true;
    }

    /**
     * 改变录制按钮大小
     *
     * @param scaleRate
     */
    private void recordBtnScale(float scaleRate) {
//        RelativeLayout.LayoutParams recordBgLp = (RelativeLayout.LayoutParams) aliyunRecordBtn.getLayoutParams();
//        recordBgLp.width = (int) (itemWidth * scaleRate);
//        recordBgLp.height = (int) (itemWidth * scaleRate);
//        aliyunRecordBtn.setLayoutParams(recordBgLp);
    }

    /**
     * 获取录制按钮宽高
     */
    private void calculateItemWidth() {
        itemWidth = getResources().getDisplayMetrics().widthPixels / MAX_ITEM_COUNT;
    }

    /**
     * 更新所有视图
     */
    private void updateAllViews() {
        //准备录制和音乐选择的时候所有view隐藏
        if (isMusicSelViewShow || recordState == RecordState.READY) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            updateBottomView();
            updateTittleView();
        }
    }

    /**
     * 更新顶部视图
     */
    private void updateTittleView() {
        if (recordState == RecordState.STOP) {
            mTitleView.setVisibility(VISIBLE);
            mLlFilterEffect.setVisibility(VISIBLE);
            mAlivcAnimFilter.setVisibility(VISIBLE);
            mLayoutOptions.setVisibility(VISIBLE);
            aliyunComplete.setVisibility(VISIBLE);

            updateLightSwitchView();
            updateMusicSelView();
            updateCompleteView();
        } else {
            mTitleView.setVisibility(GONE);
            mLlFilterEffect.setVisibility(GONE);
            mAlivcAnimFilter.setVisibility(GONE);
            mLayoutOptions.setVisibility(GONE);
            aliyunComplete.setVisibility(GONE);

        }
        if (mIsMixRecorderType) {
        }
    }

    /**
     * 更新完成录制按钮
     */
    private void updateCompleteView() {
        if (canComplete) {
            aliyunComplete.setSelected(true);
            aliyunComplete.setEnabled(true);
            //完成的按钮图片 - 可用
            //UIConfigManager.setImageResourceConfig(aliyunComplete, R.attr.finishImageAble, R.mipmap.alivc_svideo_icon_next_complete);
        } else {
            aliyunComplete.setSelected(false);
            aliyunComplete.setEnabled(false);
            //完成的按钮图片 - 不可用
            //UIConfigManager.setImageResourceConfig(aliyunComplete, R.attr.finishImageUnable, R.mipmap.alivc_svideo_icon_next_not_ready);
        }
    }

    /**
     * 倒计时按钮是否可以点击
     *
     * @param isClickable true: 可点击, 按钮白色, false: 不可点击, 按钮置灰
     */
    public void updateCutDownView(boolean isClickable) {
        if (isClickable) {
            ivReadyRecord.setColorFilter(null);
            ivReadyRecord.setClickable(true);
        } else {
            ivReadyRecord.setColorFilter(ContextCompat.getColor(getContext(), R.color.alivc_record_color_filter));
            ivReadyRecord.setClickable(false);
        }
    }

    /**
     * 更新音乐弹窗按钮
     */
    private void updateMusicSelView() {
        Drawable ratioDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.alivc_svideo_icon_aspect_ratio);
        if (hasRecordPiece) {
            //已经开始录制不允许更改音乐
        } else {

        }
    }

    /**
     * 更新底部控制按钮
     */
    private void updateBottomView() {
        if (isEffectSelViewShow) {
            aliyunRecordLayoutBottom.setVisibility(GONE);
        } else {
            aliyunRecordLayoutBottom.setVisibility(VISIBLE);
            updateModeSelView();
            updateRateItemView();
            updateRecordBtnView();
            updateDeleteView();
            if (recordState == RecordState.STOP) {
                //其他按钮现实
                llBeautyFace.setVisibility(VISIBLE);
                llGifEffect.setVisibility(VISIBLE);
            } else {
                llGifEffect.setVisibility(INVISIBLE);
                llBeautyFace.setVisibility(INVISIBLE);
            }
        }

    }

    /**
     * 更新速录选择按钮
     */
    private void updateRateItemView() {
        if (recordState == RecordState.RECORDING || recordState == RecordState.COUNT_DOWN_RECORDING) {
            aliyunRateBar.setVisibility(INVISIBLE);
        } else {
            aliyunRateBar.setVisibility(VISIBLE);
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
            case STANDARD:
                aliyunRateOrigin.setSelected(true);
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

    }


    private void changeAspectRatio() {
        switch (mAspectRatio) {
        case AliyunSnapVideoParam.RATIO_MODE_9_16:
            mAspectRatio = AliyunSnapVideoParam.RATIO_MODE_3_4;
            break;
        case AliyunSnapVideoParam.RATIO_MODE_3_4:
            mAspectRatio = AliyunSnapVideoParam.RATIO_MODE_1_1;
            break;
        case AliyunSnapVideoParam.RATIO_MODE_1_1:
            mAspectRatio = AliyunSnapVideoParam.RATIO_MODE_9_16;
            break;
        default:
            mAspectRatio = AliyunSnapVideoParam.RATIO_MODE_9_16;
            break;
        }
    }

    /**
     * 更新拍摄模式选择view
     */
    private void updateModeSelView() {
        if (hasRecordPiece || recordState == RecordState.RECORDING || recordState == RecordState.COUNT_DOWN_RECORDING) {
            mPickerView.setVisibility(GONE);
            disableLayoutOption();
        } else {
            mPickerView.setVisibility(VISIBLE);
            if (recordMode == RecordMode.SINGLE_CLICK) {
                mPickerView.setSelectedPosition(0);
            } else {
                mPickerView.setSelectedPosition(1);
            }
            enableLayoutOption();
        }
    }

    /**
     * 更新删除按钮
     */
    private void updateDeleteView() {

        if (!hasRecordPiece || recordState == RecordState.RECORDING
                || recordState == RecordState.COUNT_DOWN_RECORDING) {
            aliyunDelete.setVisibility(GONE);
        } else {
            aliyunDelete.setVisibility(VISIBLE);
        }
    }

    /**
     * 更新录制按钮状态
     */
    private void updateRecordBtnView() {

        if (recordState == RecordState.STOP) {
            recordBtnScale(1f);
            //拍摄按钮图片 - 未开始拍摄
//            UIConfigManager.setImageBackgroundConfig(aliyunRecordBtn, R.attr.videoShootImageNormal, R.mipmap.alivc_svideo_bg_record_storp);
            aliyunRecordDuration.setVisibility(GONE);
            mRecordBtnIcon.setVisibility(View.VISIBLE);
            mRecordBtnIcon.setImageResource(recordMode == RecordMode.SINGLE_CLICK
                    ? R.mipmap.ic_ugsv_recorder_record_btn
                    : R.mipmap.ic_ugsv_recorder_record_btn_longpress);
        } else if (recordState == RecordState.COUNT_DOWN_RECORDING) {
            aliyunRecordDuration.setVisibility(VISIBLE);
//            recordBtnScale(1.25f);
//            aliyunRecordBtn.setBackgroundResource(R.mipmap.alivc_svideo_bg_record_pause);
//            //拍摄按钮图片 - 拍摄中
//            UIConfigManager.setImageBackgroundConfig(aliyunRecordBtn, R.attr.videoShootImageShooting, R.mipmap.alivc_svideo_bg_record_pause);

        } else {
            aliyunRecordDuration.setVisibility(VISIBLE);
//            recordBtnScale(1.25f);
            if (recordMode == RecordMode.LONG_PRESS) {
//                aliyunRecordBtn.setBackgroundResource(R.mipmap.alivc_svideo_bg_record_start);
                //拍摄按钮图片 - 长按中
//                UIConfigManager.setImageBackgroundConfig(aliyunRecordBtn, R.attr.videoShootImageLongPressing, R.mipmap.alivc_svideo_bg_record_start);
                mRecordBtnIcon.setVisibility(View.GONE);
            } else {
//                aliyunRecordBtn.setBackgroundResource(R.mipmap.alivc_svideo_bg_record_pause);
                //拍摄按钮图片 - 拍摄中
//                UIConfigManager.setImageBackgroundConfig(aliyunRecordBtn, R.attr.videoShootImageShooting, R.mipmap.alivc_svideo_bg_record_pause);
                mRecordBtnIcon.setVisibility(View.VISIBLE);
                mRecordBtnIcon.setImageResource(R.mipmap.ic_ugsv_recorder_record_btn_recording);
            }
        }
    }

    /**
     * 更新闪光灯按钮
     */
    private void updateLightSwitchView() {
        if (cameraType == CameraType.FRONT) {
            // 前置摄像头状态, 闪光灯图标变灰
            aliyunSwitchLight.setImageResource(R.mipmap.ic_ugsv_recorder_flash_off);
        } else if (cameraType == CameraType.BACK) {
            switch (flashType) {
            case ON:
                aliyunSwitchLight.setImageResource(R.mipmap.ic_ugsv_recorder_flash_on);
                break;
            case OFF:
                aliyunSwitchLight.setImageResource(R.mipmap.ic_ugsv_recorder_flash_off);
                break;
            default:
                break;
            }
        }

    }

    public FlashType getFlashType() {
        return flashType;
    }

    public void setFlashType(FlashType flashType) {
        this.flashType = flashType;
        updateLightSwitchView();
    }

    public CameraType getCameraType() {
        return cameraType;
    }

    public RecordState getRecordState() {
        if (recordState.equals(RecordState.COUNT_DOWN_RECORDING) || recordState.equals(RecordState.RECORDING)) {
            return RecordState.RECORDING;
        }
        return recordState;
    }

    public void setRecordState(RecordState recordState) {
        if (recordState == RecordState.RECORDING) {
            if (this.recordState == RecordState.READY) {
                this.recordState = RecordState.COUNT_DOWN_RECORDING;
            } else {
                this.recordState = recordState;
            }
        } else {
            this.recordState = recordState;
        }
        updateAllViews();
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public boolean isRecording() {
        return isRecording;
    }

    /**
     * 是否有录制片段
     *
     * @param hasRecordPiece
     */
    public void setHasRecordPiece(boolean hasRecordPiece) {
        this.hasRecordPiece = hasRecordPiece;
        updateModeSelView();
        updateDeleteView();
        updateMusicSelView();
    }

    /**
     * 音乐选择弹窗显示回调
     *
     * @param musicSelViewShow
     */
    public void setMusicSelViewShow(boolean musicSelViewShow) {
        isMusicSelViewShow = musicSelViewShow;
        updateAllViews();
    }

    /**
     * 其他特效选择弹窗回调
     *
     * @param effectSelViewShow
     */
    public void setEffectSelViewShow(boolean effectSelViewShow) {
        isEffectSelViewShow = effectSelViewShow;
        updateBottomView();
    }

    /**
     * 设置录制事件，录制过程中持续被调用
     *
     * @param recordTime
     */
    public void setRecordTime(String recordTime) {
        aliyunRecordDuration.setText(recordTime);
    }

    /**
     * 添加各个控件点击监听
     *
     * @param mListener
     */
    public void setControlViewListener(ControlViewListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 设置摄像头类型，并刷新页面，摄像头切换后被调用
     *
     * @param cameraType
     */
    public void setCameraType(CameraType cameraType) {
        this.cameraType = cameraType;
        updateLightSwitchView();
    }

    /**
     * 设置complete按钮是否可以点击
     *
     * @param enable
     */
    public void setCompleteEnable(boolean enable) {
        canComplete = enable;
        updateCompleteView();
    }

    /**
     * 设置应用音乐icon
     *
     * @param icon
     */
    public void setMusicIcon(String icon) {

    }

    /**
     * 设置应用音乐icon
     *
     * @param id
     */
    public void setMusicIconId(@DrawableRes int id) {
    }

    /**
     * 设置画幅比例
     */
    public void setAspectRatio(int radio) {
        mAspectRatio = radio;
    }


    /**
     * 设置下一步图标的显示text
     * @param stringId 资源id
     */
    public void setAliyunCompleteText(int stringId) {

    }

    /**
     * 设置是普通录制还是合拍，合拍不需要音乐和切画幅
     */
    public void setRecordType(Boolean recordType) {
        mIsMixRecorderType = recordType;
        updateTittleView();
    }

    public void enableLayoutOption(){
        mLayoutOptionsIcon.setEnabled(true);
        mLayoutOptions.setEnabled(true);
    }

    public void disableLayoutOption(){
        mLayoutOptionsIcon.setEnabled(false);
        mLayoutOptions.setEnabled(false);
    }

    public void onHorizontalLayout(){
        mLayoutOptionsIcon.setImageResource(R.drawable.ic_layout_option_entry_horizontal_btn);
    }

    public void onVerticalLayout(){
        mLayoutOptionsIcon.setImageResource(R.drawable.ic_layout_option_entry_vertical_btn);
    }

    public void onFloatLayout(){
        mLayoutOptionsIcon.setImageResource(R.drawable.ic_layout_option_entry_float_btn);
    }
}
