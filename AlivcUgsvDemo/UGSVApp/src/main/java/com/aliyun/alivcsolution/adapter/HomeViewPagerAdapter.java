package com.aliyun.alivcsolution.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

/**
 * Created by Mulberry on 2018/4/11.
 */
public class HomeViewPagerAdapter extends PagerAdapter {

    private List<View> viewLists;

    public HomeViewPagerAdapter(List<View> viewLists) {
        this.viewLists = viewLists;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //remove item
        container.removeView(viewLists.get(position));
    }

    /**
     * 将当前View添加到ViewGroup容器中
     * 这个方法，return一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //add item return item view
        container.addView(viewLists.get(position));
        return viewLists.get(position);
    }

    @Override
    public int getCount() {
        return viewLists != null ? viewLists.size() : 0;
    }

    /**
     *用于标识一个视图是否和给定的Key对象相关
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
