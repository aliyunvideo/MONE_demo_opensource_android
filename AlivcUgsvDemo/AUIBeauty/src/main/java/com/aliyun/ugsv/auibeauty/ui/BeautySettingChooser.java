package com.aliyun.ugsv.auibeauty.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.aliyun.svideo.base.R;
import com.aliyun.ugsv.auibeauty.OnDefaultBeautyLevelChangeListener;
import com.aliyun.ugsv.auibeauty.ui.view.AlivcBeautyFaceSettingView;
import com.aliyun.svideo.base.BaseChooser;


/**
 *
 *
 * @author xlx
 */
public class BeautySettingChooser extends BaseChooser {

    private AlivcBeautyFaceSettingView mBeautyDetailSettingView;
    private OnDefaultBeautyLevelChangeListener mOnDefaultBeautyLevelChangeListener;
    private int mCurrentLevel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.QUDemoFullStyle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return new AlivcBeautyFaceSettingView(getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBeautyDetailSettingView = (AlivcBeautyFaceSettingView) view;
        mBeautyDetailSettingView.setOnBeautyFaceLevelChangeListener(new AlivcBeautyFaceSettingView.OnLevelChangeListener() {
            @Override
            public void onChangeListener(int level) {
                if (mOnDefaultBeautyLevelChangeListener != null){
                    mOnDefaultBeautyLevelChangeListener.onDefaultBeautyLevelChange(level);
                }
            }
        });
        mBeautyDetailSettingView.setOnBlankClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        mBeautyDetailSettingView.setDefaultSelect(mCurrentLevel);
    }

    public void setOnDefaultBeautyLevelChangeListener(OnDefaultBeautyLevelChangeListener onDefaultBeautyLevelChangeListener) {
        this.mOnDefaultBeautyLevelChangeListener = onDefaultBeautyLevelChangeListener;
    }

    public void show(FragmentManager manager, String tag, int currentLevel) {
        mCurrentLevel = currentLevel;
        super.show(manager, tag);
    }
}
