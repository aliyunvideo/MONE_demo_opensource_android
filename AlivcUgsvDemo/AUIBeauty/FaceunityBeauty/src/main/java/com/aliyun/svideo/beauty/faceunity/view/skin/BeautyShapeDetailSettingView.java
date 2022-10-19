package com.aliyun.svideo.beauty.faceunity.view.skin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aliyun.svideo.base.widget.beauty.listener.OnProgresschangeListener;
import com.aliyun.svideo.base.widget.beauty.seekbar.BeautySeekBar;
import com.aliyun.svideo.beauty.faceunity.R;
import com.aliyun.svideo.beauty.faceunity.bean.FaceUnityBeautyParams;
import com.aliyun.svideo.beauty.faceunity.constant.FaceUnityBeautyConstants;
import com.aliyun.svideo.beauty.faceunity.inteface.OnBeautyParamsChangeListener;
import com.aliyun.svideo.beauty.faceunity.inteface.OnBlankViewClickListener;


/**
 * 美型微调view
 *
 * @author xlx
 */
public class BeautyShapeDetailSettingView extends LinearLayout {
    /**
     * 美颜美肌参数, 包括 大眼, 瘦脸
     */

    private BeautySeekBar mSeekBar;
    private OnBlankViewClickListener mOnBlankViewClickListener;
    private OnBeautyParamsChangeListener mOnBeautyParamsChangeListener;
    private FaceUnityBeautyParams mFaceUnityBeautyShapeParams;
    private int mCheckedPosition = FaceUnityBeautyConstants.BIG_EYE;
    private FaceUnityBeautyParams defaultParams;

    public BeautyShapeDetailSettingView(Context context) {
        this(context, null);

    }

    public BeautyShapeDetailSettingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeautyShapeDetailSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.alivc_faceunity_beauty_shape_detail, this);
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setText(getResources().getString(com.aliyun.svideo.base.R.string.alivc_base_beauty_shape));
        mSeekBar = findViewById(R.id.beauty_seekbar);
        View blankView = findViewById(R.id.blank_view);
        ImageView mIvReset = findViewById(R.id.iv_reset);
        RadioGroup rgBeautySkinGroup = findViewById(R.id.beauty_skin_detail_group);
        rgBeautySkinGroup.check(com.aliyun.svideo.base.R.id.beauty_bigeye);
        blankView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBlankViewClickListener != null) {
                    mOnBlankViewClickListener.onBlankClick();
                }
            }
        });
        rgBeautySkinGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == com.aliyun.svideo.base.R.id.beauty_bigeye) {
                    // 大眼
                    mCheckedPosition = 0;
                } else if (checkedId == com.aliyun.svideo.base.R.id.beauty_thin_face) {
                    // 瘦脸
                    mCheckedPosition = 1;
                }
                changeSeekBarData(mFaceUnityBeautyShapeParams);
            }
        });



        mSeekBar.setProgressChangeListener(new OnProgresschangeListener() {
            @Override
            public void onProgressChange(int progress) {
                if (mFaceUnityBeautyShapeParams != null) {
                    switch (mCheckedPosition) {
                        case FaceUnityBeautyConstants.BIG_EYE:
                            if (mFaceUnityBeautyShapeParams.beautyBigEye == progress) {
                                return;
                            }
                            mFaceUnityBeautyShapeParams.beautyBigEye = progress;
                            break;

                        case FaceUnityBeautyConstants.SLIM_FACE:
                            if (mFaceUnityBeautyShapeParams.beautySlimFace == progress) {
                                return;
                            }
                            mFaceUnityBeautyShapeParams.beautySlimFace = progress;
                            break;
                        default:
                            break;
                    }
                }

                if (mOnBeautyParamsChangeListener != null) {
                    mOnBeautyParamsChangeListener.onBeautyShapeChange(mFaceUnityBeautyShapeParams);
                }
            }
        });

        tvBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBlankViewClickListener != null) {
                    mOnBlankViewClickListener.onBackClick();
                }
            }
        });

        mIvReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSeekBar.resetProgress();
            }
        });
    }



    public void setBeautyConstants(int beautyConstants) {
        this.mCheckedPosition = beautyConstants;
    }


    public void setParams(FaceUnityBeautyParams faceUnityBeautyParams) {
        mFaceUnityBeautyShapeParams = faceUnityBeautyParams;
        changeSeekBarData(faceUnityBeautyParams) ;
    }

    private void changeSeekBarData(FaceUnityBeautyParams faceUnityBeautyParams) {
        if (faceUnityBeautyParams == null){
            return;
        }
        defaultParams = FaceUnityBeautyConstants.BEAUTY_MAP.get(mFaceUnityBeautyShapeParams.beautyLevel);
        if (defaultParams == null){
            return;
        }
        switch (mCheckedPosition) {
            case FaceUnityBeautyConstants.BIG_EYE:
                mSeekBar.setSeekIndicator(defaultParams.beautyBigEye);
                mSeekBar.setLastProgress(faceUnityBeautyParams.beautyBigEye);
                break;

            case FaceUnityBeautyConstants.SLIM_FACE:
                mSeekBar.setSeekIndicator(defaultParams.beautySlimFace);
                mSeekBar.setLastProgress(faceUnityBeautyParams.beautySlimFace);
                break;

            default:
                break;
        }
    }



    public void setOnBlankViewClickListener(OnBlankViewClickListener onBlankViewClickListener) {
        mOnBlankViewClickListener = onBlankViewClickListener;
    }

    public void setOnBeautyParamsChangeListener(OnBeautyParamsChangeListener onBeautyParamsChangeListener) {
        mOnBeautyParamsChangeListener = onBeautyParamsChangeListener;
    }
}
