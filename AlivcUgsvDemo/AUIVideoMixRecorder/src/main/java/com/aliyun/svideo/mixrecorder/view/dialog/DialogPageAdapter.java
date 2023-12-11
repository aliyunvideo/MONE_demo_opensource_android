package com.aliyun.svideo.mixrecorder.view.dialog;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aliyun.svideo.base.widget.PagerSlidingTabStrip;

import java.util.List;

public class DialogPageAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider  {
    private List<Fragment> mPageList ;
    public DialogPageAdapter(FragmentManager fm, List<Fragment> pageList) {
        super(fm);
        mPageList = pageList;
    }


    @Override
    public Fragment getItem(int position) {
        return mPageList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Fragment fragment = mPageList.get(position);
        if (fragment instanceof IPageTab) {
            return ((IPageTab)fragment).getTabTitle();
        } else {
            return "";
        }
    }

    @Override
    public int getCount() {
        return mPageList.size();
    }

    @Override
    public int getPageIconResId(int position) {
        Fragment fragment = mPageList.get(position);
        if (fragment instanceof IPageTab) {
            return ((IPageTab)fragment).getTabIcon();
        } else {
            return 0;
        }
    }
}
