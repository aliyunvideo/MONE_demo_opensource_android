package com.alivc.player.videolist.auivideolistcommon.listener;

public interface OnViewPagerListener {

    void onInitComplete();

    void onPageShow(int position);

    void onPageSelected(int position);

    void onPageRelease(int position);

    void onPageHideHalf(int position);

    void onPageScrollTo(int position);

    int onSelectedPosition();
}
