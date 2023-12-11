package com.aliyun.svideo.mixrecorder.view.dialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aliyun.svideo.mixrecorder.view.effects.filter.AUIEffectInfo;
import com.aliyun.svideo.mixrecorder.view.effects.filter.AlivcFilterChooseFragment;
import com.aliyun.svideo.mixrecorder.view.effects.filter.EffectInfo;
import com.aliyun.svideo.mixrecorder.view.effects.filter.OnFilterItemClickListener;
import com.aliyun.svideo.record.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zsy_18 data:2019/4/22
 */
public class FilterEffectChooser extends BasePageChooser {
    /**
     * 滤镜fragment
     */
    private AlivcFilterChooseFragment filterChooseFragment;
    /**
     * 滤镜item点击listener
     */
    private OnFilterItemClickListener onFilterItemClickListener;
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
        filterChooseFragment = new AlivcFilterChooseFragment();
        //滤镜
        filterChooseFragment.setTabTitle(getResources().getString(R.string.alivc_base_filter));
        initFilter();
        fragments.add(filterChooseFragment);
        return fragments;
    }
    @Override
    public void onStart() {
        super.onStart();
        filterChooseFragment.setFilterPosition(filterPosition);
    }
    private void initFilter() {
        filterChooseFragment.setOnFilterItemClickListener(new OnFilterItemClickListener() {
            @Override
            public void onItemClick(AUIEffectInfo effectInfo, int index) {
                if (onFilterItemClickListener != null) {
                    onFilterItemClickListener.onItemClick(effectInfo, index);
                }
            }
        });
    }

    /**
     * 设置滤镜item点击listener
     * @param listener OnFilterItemClickListener
     */
    public void setOnFilterItemClickListener(OnFilterItemClickListener listener) {
        this.onFilterItemClickListener = listener;
    }
    public void setFilterPosition(int filterPosition) {
        this.filterPosition = filterPosition;
    }


}
