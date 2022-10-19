package com.aliyun.svideo.beauty.faceunity.view.skin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.svideo.beauty.faceunity.R;
import com.aliyun.svideo.beauty.faceunity.inteface.OnBeautyDetailClickListener;
import com.aliyun.svideo.beauty.faceunity.inteface.OnBeautyShapeTypeChangeListener;


public class AlivcBeautyShapeSettingView extends FrameLayout {

    private RadioGroup mRgSkinGroup;
    private int mCurrentLevel = 0;
    private OnBeautyShapeTypeChangeListener mOnBeautyShapeTypeChangeListener;
    private OnBeautyDetailClickListener mOnBeautyDetailClickListener;
    private ImageView mBtBeautyDetail;

    public AlivcBeautyShapeSettingView(Context context ) {
        this(context, null);
    }

    public AlivcBeautyShapeSettingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public AlivcBeautyShapeSettingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.alivc_faceunity_beauty_shape_layout, this);
        mRgSkinGroup = findViewById(R.id.beauty_normal_group);
        mBtBeautyDetail = findViewById(R.id.iv_beauty_detail);
        mRgSkinGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switchIdToLevel(checkedId);
                if (mOnBeautyShapeTypeChangeListener != null) {
                    mOnBeautyShapeTypeChangeListener.onShapeTypeChange(mCurrentLevel);
                }
            }
        });
        mBtBeautyDetail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBeautyDetailClickListener != null) {
                    mOnBeautyDetailClickListener.onDetailClick(mCurrentLevel);
                }
            }
        });
    }

    private void switchIdToLevel(int checkedId) {
        if (checkedId == R.id.beauty0 ) {
            mCurrentLevel = 0;
        } else if (checkedId == R.id.beauty1 ) {
            mCurrentLevel = 1;
        } else if (checkedId == R.id.beauty2 ) {
            mCurrentLevel = 2;
        } else if (checkedId == R.id.beauty3 ) {
            mCurrentLevel = 3;
        } else if (checkedId == R.id.beauty4 ) {
            mCurrentLevel = 4;
        } else if (checkedId == R.id.beauty5 ) {
            mCurrentLevel = 5;
        }
    }

    /**
     * 设置默认值
     *
     * @param level [0,5]
     */
    public void setDefaultSelect(int level) {
        if (level < 0 || level > 5) {
            return;
        }
        advancedCheck(level);

    }
    private void advancedCheck(int level) {
        int normalId;
        switch (level) {
            case 0:
                normalId = R.id.beauty0;
                break;
            case 1:
                normalId = R.id.beauty1;
                break;

            case 2:
                normalId = R.id.beauty2;
                break;

            case 3:
                normalId = R.id.beauty3;
                break;

            case 4:
                normalId = R.id.beauty4;
                break;

            case 5:
                normalId = R.id.beauty5;
                break;
            default:
                normalId = R.id.beauty0;
                break;
        }
        if (mRgSkinGroup != null) {
            mRgSkinGroup.check(normalId);
        }
    }

    public void setOnBeautyShapeLevelChangeListener(OnBeautyShapeTypeChangeListener onBeautyShapeTypeChangeListener) {
        mOnBeautyShapeTypeChangeListener = onBeautyShapeTypeChangeListener;
    }

    public void setOnBeautyDetailClickListener(OnBeautyDetailClickListener onBeautyDetailClickListener) {
        mOnBeautyDetailClickListener = onBeautyDetailClickListener;
    }
}
