package com.alivc.live.beautyui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.alivc.live.beautyui.component.BeautyItemRecyclerView;

import java.util.ArrayList;

/**
 * 美颜界面View适配器
 */
public class BeautyTabPageAdapter extends PagerAdapter {

    public ArrayList<BeautyItemRecyclerView> viewCacheList = new ArrayList<>();

    @Override
    public int getCount() {
        return viewCacheList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = viewCacheList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
