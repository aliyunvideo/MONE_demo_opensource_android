package com.aliyun.svideo.beauty.faceunity.view.face;

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
 * 美颜微调
 *
 * @author xlx
 */
public class AlivcBeautyDetailSettingView extends LinearLayout {

    private BeautySeekBar mSeekBar;
    private FaceUnityBeautyParams mFaceUnityBeautyFaceParams;
    private int mCheckedPosition = 0;
    /**
     * 美颜美肌参数改变listener
     */
    private OnBeautyParamsChangeListener mBeautyParamsChangeListener;

    private OnBlankViewClickListener mOnBlankViewClickListener;


    public AlivcBeautyDetailSettingView(Context context) {
        this(context, null);

    }

    public AlivcBeautyDetailSettingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlivcBeautyDetailSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.alivc_faceunity_beauty_face_detail, this);
        View tvBack = findViewById(R.id.tv_back);
        mSeekBar = findViewById(R.id.beauty_seekbar);
        TextView blushTv = findViewById(R.id.alivc_base_beauty_blush_textview);
        blushTv.setText(R.string.alivc_base_beauty_sharpen);
        View blankView = findViewById(R.id.blank_view);
        ImageView ivReset = findViewById(R.id.iv_reset);
        RadioGroup rgBeautyFaceGroup = findViewById(R.id.beauty_detail_group);
        rgBeautyFaceGroup.check(R.id.beauty_buffing);
        blankView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBlankViewClickListener != null) {
                    mOnBlankViewClickListener.onBlankClick();
                }
            }
        });
        rgBeautyFaceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.beauty_buffing) {
                    // 磨皮
                    mCheckedPosition = FaceUnityBeautyConstants.BUFFING;
                } else if (checkedId == R.id.beauty_whitening) {
                    // 美白
                    mCheckedPosition = FaceUnityBeautyConstants.WHITENING;
                } else if (checkedId == R.id.beauty_ruddy) {
                    // 锐化
                    mCheckedPosition = FaceUnityBeautyConstants.RUDDY;
                }
                setRecommendProgress();
                setProgress();
            }
        });

        mSeekBar.setProgressChangeListener(new OnProgresschangeListener() {
            @Override
            public void onProgressChange(int progress) {

                if (mFaceUnityBeautyFaceParams != null) {
                    switch (mCheckedPosition) {
                        case FaceUnityBeautyConstants.BUFFING:
                            if (mFaceUnityBeautyFaceParams.beautyBuffing == progress) {
                                return;
                            }
                            mFaceUnityBeautyFaceParams.beautyBuffing = progress;
                            break;

                        case FaceUnityBeautyConstants.WHITENING:
                            if (mFaceUnityBeautyFaceParams.beautyWhite == progress) {
                                return;
                            }
                            mFaceUnityBeautyFaceParams.beautyWhite = progress;
                            break;

                        case FaceUnityBeautyConstants.RUDDY:
                            if (mFaceUnityBeautyFaceParams.beautyRuddy == progress) {
                                return;
                            }
                            mFaceUnityBeautyFaceParams.beautyRuddy = progress;
                            break;

                        default:
                            break;
                    }
                }

                if (mBeautyParamsChangeListener != null) {
                    mBeautyParamsChangeListener.onBeautyFaceChange(mFaceUnityBeautyFaceParams);
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

        ivReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSeekBar.resetProgress();
            }
        });
    }


    public void setParams(FaceUnityBeautyParams params) {
        if (params != null) {
            mFaceUnityBeautyFaceParams = params;
            setRecommendProgress();
            setProgress();
        }
    }

    public void setProgress() {
        if (mFaceUnityBeautyFaceParams != null) {
            switch (mCheckedPosition) {
                case FaceUnityBeautyConstants.BUFFING:
                    mSeekBar.setLastProgress(mFaceUnityBeautyFaceParams.beautyBuffing);
                    break;
                case FaceUnityBeautyConstants.WHITENING:
                    mSeekBar.setLastProgress(mFaceUnityBeautyFaceParams.beautyWhite);
                    break;
                case FaceUnityBeautyConstants.RUDDY:
                    mSeekBar.setLastProgress(mFaceUnityBeautyFaceParams.beautyRuddy);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 设置推荐参数
     */
    private void setRecommendProgress() {
        if (mFaceUnityBeautyFaceParams != null) {
            FaceUnityBeautyParams faceUnityBeautyParams = FaceUnityBeautyConstants.BEAUTY_MAP.get(mFaceUnityBeautyFaceParams.beautyLevel);
            if (faceUnityBeautyParams != null) {
                switch (mCheckedPosition) {
                    case FaceUnityBeautyConstants.BUFFING:
                        mSeekBar.setSeekIndicator(faceUnityBeautyParams.beautyBuffing);
                        break;
                    case FaceUnityBeautyConstants.WHITENING:
                        mSeekBar.setSeekIndicator(faceUnityBeautyParams.beautyWhite);
                        break;
                    case FaceUnityBeautyConstants.RUDDY:
                        mSeekBar.setSeekIndicator(faceUnityBeautyParams.beautyRuddy);
                        break;
                    default:
                        break;
                }
            }
        }
    }


    public void setBeautyParamsChangeListener(OnBeautyParamsChangeListener listener) {
        mBeautyParamsChangeListener = listener;
    }


    public void setOnBlankViewClickListener(OnBlankViewClickListener listener) {
        mOnBlankViewClickListener = listener;
    }
}
