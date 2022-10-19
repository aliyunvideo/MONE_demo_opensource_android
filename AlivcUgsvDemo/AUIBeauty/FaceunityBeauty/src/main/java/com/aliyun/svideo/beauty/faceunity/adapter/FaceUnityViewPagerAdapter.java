/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.beauty.faceunity.adapter;

import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.aliyun.svideo.base.widget.PagerSlidingTabStrip;
import com.aliyun.svideo.beauty.faceunity.R;
import com.aliyun.svideo.beauty.faceunity.adapter.holder.BaseCaptionViewHolder;

import java.util.ArrayList;
import java.util.List;

public class FaceUnityViewPagerAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
    private List<BaseCaptionViewHolder> mTabHolder = new ArrayList<>();

    @Override
    public int getCount() {
        return mTabHolder.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BaseCaptionViewHolder holder = mTabHolder.get(position);
        container.addView(holder.getItemView());
        holder.onBindViewHolder();
        return holder.getItemView();
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof View) {
            container.removeView((View) object);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return position ==0?"美颜":"美型";
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void addViewHolder(BaseCaptionViewHolder holder) {
        mTabHolder.add(holder);
    }


    public int size() {
        return mTabHolder != null ? mTabHolder.size() : 0;
    }

    @Override
    public int getPageIconResId(int position) {
        return position == 0 ? R.mipmap.alivc_svideo_icon_tab_beauty_face:R.mipmap.alivc_svideo_icon_tab_beauty_skin;
    }
}
