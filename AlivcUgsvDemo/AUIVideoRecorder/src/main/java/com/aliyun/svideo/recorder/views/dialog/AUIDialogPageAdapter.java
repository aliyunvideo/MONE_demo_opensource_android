package com.aliyun.svideo.recorder.views.dialog;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.aliyun.svideo.base.widget.PagerSlidingTabStrip;

import java.util.List;

public class AUIDialogPageAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider  {
    private List<Fragment> mPageList ;
    public AUIDialogPageAdapter(FragmentManager fm, List<Fragment> pageList) {
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
        if (fragment instanceof AUIIPageTab) {
            return ((AUIIPageTab)fragment).getTabTitle();
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
        if (fragment instanceof AUIIPageTab) {
            return ((AUIIPageTab)fragment).getTabIcon();
        } else {
            return 0;
        }
    }
}
