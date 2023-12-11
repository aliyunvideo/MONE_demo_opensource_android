package com.aliyun.svideo.mixrecorder.view.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aliyun.svideo.mixrecorder.view.effects.filter.animfilter.AlivcAnimFilterChooseFragment;
import com.aliyun.svideo.mixrecorder.view.effects.filter.animfilter.OnAnimFilterItemClickListener;
import com.aliyun.svideo.record.R;
import com.aliyun.svideosdk.common.struct.effect.EffectFilter;

import java.util.ArrayList;
import java.util.List;

public class AnimFilterEffectChooser extends BasePageChooser {
    /**
     * 特效滤镜fragment
     */
    private AlivcAnimFilterChooseFragment mAlivcAnimFilterChooseFragment;
    /**
     * 滤镜item点击listener
     */
    private OnAnimFilterItemClickListener mOnAnimFilterItemClickListener;
    private int filterPosition;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //适配有底部导航栏的手机，在full的style下会盖住部分视图的bug
        setStyle(STYLE_NO_FRAME, R.style.QUDemoFullStyle);
    }
    @Override
    public List<Fragment> createPagerFragmentList() {
        List<Fragment> fragments = new ArrayList<>();
        mAlivcAnimFilterChooseFragment = new AlivcAnimFilterChooseFragment();
        //滤镜
        mAlivcAnimFilterChooseFragment.setTabTitle(getResources().getString(R.string.alivc_base_filter));
        initFilter();
        fragments.add(mAlivcAnimFilterChooseFragment);
        return fragments;
    }
    @Override
    public void onStart() {
        super.onStart();
        mAlivcAnimFilterChooseFragment.setFilterPosition(filterPosition);
    }
    private void initFilter() {
        mAlivcAnimFilterChooseFragment.setOnAnimFilterItemClickListener(new OnAnimFilterItemClickListener() {
            @Override
            public void onItemClick(EffectFilter effectInfo, int index) {
                if (mOnAnimFilterItemClickListener != null) {
                    mOnAnimFilterItemClickListener.onItemClick(effectInfo, index);
                }
            }

            @Override
            public void onItemUpdate(EffectFilter effectInfo) {
                if (mOnAnimFilterItemClickListener != null) {
                    mOnAnimFilterItemClickListener.onItemUpdate(effectInfo);
                }
            }

        });
    }

    /**
     * 设置滤镜item点击listener
     * @param listener OnFilterItemClickListener
     */
    public void setOnAnimFilterItemClickListener(OnAnimFilterItemClickListener listener) {
        this.mOnAnimFilterItemClickListener = listener;
    }
    public void setFilterPosition(int filterPosition) {
        this.filterPosition = filterPosition;
    }


}
