package com.aliyun.svideo.recorder.views.dialog;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.aliyun.svideo.recorder.R;
import com.aliyun.svideo.recorder.views.effects.filter.AUIFilterChooseFragment;
import com.aliyun.svideo.recorder.views.effects.filter.OnFilterItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 滤镜选择器
 */
public class AUIFilterChooser extends AUIBaseChooser {

    private AUIFilterChooseFragment mChooseFragment;
    private OnFilterItemClickListener mOnItemClickListener;
    private int mSelectedPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //适配有底部导航栏的手机，在full的style下会盖住部分视图的bug
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.QUDemoFullStyle);
    }

    @Override
    public List<Fragment> createPagerFragmentList() {
        List<Fragment> fragments = new ArrayList<>();
        mChooseFragment = new AUIFilterChooseFragment();
        mChooseFragment.setTabTitle(getResources().getString(R.string.ugsv_recorder_control_filter));
        initFilter();
        fragments.add(mChooseFragment);
        return fragments;
    }

    @Override
    public void onStart() {
        super.onStart();
        mChooseFragment.setFilterSelectedPosition(mSelectedPosition);
    }

    private void initFilter() {
        mChooseFragment.setOnItemClickListener((effectInfo, index) -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(effectInfo, index);
            }
        });
    }

    public void setOnItemClickListener(OnFilterItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setFilterSelectedPosition(int filterSelectPosition) {
        this.mSelectedPosition = filterSelectPosition;
    }
}
