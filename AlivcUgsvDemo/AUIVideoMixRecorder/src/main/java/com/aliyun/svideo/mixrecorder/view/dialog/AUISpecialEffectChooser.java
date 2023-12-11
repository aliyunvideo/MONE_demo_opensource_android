package com.aliyun.svideo.mixrecorder.view.dialog;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.aliyun.svideo.mixrecorder.view.effects.filter.animfilter.AUISpecialEffectChooseFragment;
import com.aliyun.svideo.mixrecorder.view.effects.filter.animfilter.OnSpecialEffectItemClickListener;
import com.aliyun.svideo.record.R;
import com.aliyun.svideosdk.common.struct.effect.EffectFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 特效选择器
 */
public class AUISpecialEffectChooser extends AUIBaseChooser {
    private AUISpecialEffectChooseFragment mChooseFragment;
    private OnSpecialEffectItemClickListener mOnItemClickListener;
    private int mSelectedItemPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //适配有底部导航栏的手机，在full的style下会盖住部分视图的bug
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.QUDemoFullStyle);
    }

    @Override
    public List<Fragment> createPagerFragmentList() {
        List<Fragment> fragments = new ArrayList<>();
        mChooseFragment = new AUISpecialEffectChooseFragment();
        mChooseFragment.setTabTitle(getResources().getString(R.string.ugsv_mix_recorder_control_effect));
        initFilter();
        fragments.add(mChooseFragment);
        return fragments;
    }

    @Override
    public void onStart() {
        super.onStart();
        mChooseFragment.setSelectedPosition(mSelectedItemPosition);
    }

    private void initFilter() {
        mChooseFragment.setOnItemClickListener(new OnSpecialEffectItemClickListener() {
            @Override
            public void onItemClick(EffectFilter effectInfo, int index) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(effectInfo, index);
                }
            }

            @Override
            public void onItemUpdate(EffectFilter effectInfo) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemUpdate(effectInfo);
                }
            }
        });
    }

    public void setOnItemClickListener(OnSpecialEffectItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.mSelectedItemPosition = selectedPosition;
    }


}
