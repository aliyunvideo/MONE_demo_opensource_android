package com.aliyun.ugsv.auibeauty.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.svideo.base.R;


/**
 * 短视频SDK美颜设置
 */
public class AlivcBeautyFaceSettingView extends FrameLayout {
    private RadioGroup mRgAdvancedGroup;
    private int mCurrentLevel;
    private OnLevelChangeListener mOnBeautyFaceLevelChangeListener;
    private OnClickListener mOnBlankClickListener;
    public AlivcBeautyFaceSettingView(Context context) {
        this(context, null);
    }

    public AlivcBeautyFaceSettingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public AlivcBeautyFaceSettingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.alivc_beauty_face_view, this);
        mRgAdvancedGroup = findViewById(R.id.beauty_advanced_group);

        mRgAdvancedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mOnBeautyFaceLevelChangeListener != null) {
                    mCurrentLevel = switchIdToLevel(checkedId);
                    mOnBeautyFaceLevelChangeListener.onChangeListener(mCurrentLevel);
                }

            }
        });

        View blankView = findViewById(R.id.blank_view);
        blankView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBlankClickListener != null){
                    mOnBlankClickListener.onClick(v);
                }
            }
        });
    }


    public void setDefaultSelect(int level) {
        mCurrentLevel = level;
        advancedCheck(level);

    }

    private void advancedCheck(int level) {
        int advanceId;
        switch (level) {
            case 0:
                advanceId = R.id.beauty_advanced_0;
                break;
            case 20:
                advanceId = R.id.beauty_advanced_1;
                break;

            case 40:
                advanceId = R.id.beauty_advanced_2;
                break;

            case 60:
                advanceId = R.id.beauty_advanced_3;
                break;

            case 80:
                advanceId = R.id.beauty_advanced_4;
                break;

            case 100:
                advanceId = R.id.beauty_advanced_5;
                break;

            default:
                advanceId = R.id.beauty_advanced_3;
                break;
        }
        if (mRgAdvancedGroup != null) {
            mRgAdvancedGroup.check(advanceId);
        }
    }

    private int switchIdToLevel(int checkedId) {
        int index = 0;
        if (checkedId == R.id.beauty_advanced_0) {
            index = 0;
        } else if ( checkedId == R.id.beauty_advanced_1) {
            index = 20;
        } else if ( checkedId == R.id.beauty_advanced_2) {
            index = 40;
        } else if ( checkedId == R.id.beauty_advanced_3) {
            index = 60;
        } else if ( checkedId == R.id.beauty_advanced_4) {
            index = 80;
        } else if ( checkedId == R.id.beauty_advanced_5) {
            index = 100;
        }
        return index;
    }


    public void setOnBeautyFaceLevelChangeListener(OnLevelChangeListener onBeautyFaceLevelChangeListener) {
        this.mOnBeautyFaceLevelChangeListener = onBeautyFaceLevelChangeListener;
    }

    public void setOnBlankClickListener(OnClickListener onBlankClickListener) {
        this.mOnBlankClickListener = onBlankClickListener;
    }

    public interface OnLevelChangeListener{
        void onChangeListener(int level);
    }



}
