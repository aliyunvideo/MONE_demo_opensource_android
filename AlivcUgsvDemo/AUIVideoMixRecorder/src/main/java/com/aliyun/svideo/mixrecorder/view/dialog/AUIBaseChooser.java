package com.aliyun.svideo.mixrecorder.view.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.aliyun.svideo.base.BaseChooser;
import com.aliyun.svideo.base.widget.PagerSlidingTabStrip;
import com.aliyun.svideo.record.R;

import java.util.List;

public abstract class AUIBaseChooser extends BaseChooser {
    private ViewPager mViewPager;
    protected List<Fragment> mPageList;
    private PagerSlidingTabStrip mTabPageIndicator;

    private int[] tabIcons;
    private LinearLayout llBlank;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ugsv_mixrecorder_chooser_base_layout, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTabPageIndicator = (PagerSlidingTabStrip) view.findViewById(R.id.alivc_dialog_indicator);
        mViewPager = (ViewPager) view.findViewById(R.id.alivc_dialog_container);
        llBlank = view.findViewById(R.id.ll_blank);

        mTabPageIndicator.setTextColorResource(R.color.aliyun_svideo_tab_text_color_selector);
        mTabPageIndicator.setTabViewId(R.layout.aliyun_svideo_layout_tab_top);

        if (mPageList == null) {
            mPageList = createPagerFragmentList();
        }
        mViewPager.setOffscreenPageLimit(mPageList.size());
        mViewPager.setAdapter(new AUIDialogPageAdapter(getChildFragmentManager(), mPageList));
        mTabPageIndicator.setViewPager(mViewPager);
    }

    ViewPager.SimpleOnPageChangeListener simpleOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            if (onUpdatePageSelectedListener != null) {
                onUpdatePageSelectedListener.onPageSelected(position);
            }
        }
    };

    /**
     * 初始化ViewPager包含的fragment
     */
    public abstract List<Fragment> createPagerFragmentList();

    @Override
    public void onStart() {
        super.onStart();

        if (mViewPager != null) {
            mViewPager.addOnPageChangeListener(simpleOnPageChangeListener);
        }

        llBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    解决crash:java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
                    原因:after onSaveInstanceState invoke commit,而 show 会触发 commit 操作
                    fragment is added and its state has already been saved，
                    Any operations that would change saved state should not be performed if this method returns true
                */
                if (isStateSaved()) {
                    return;
                }
                dismiss();
            }
        });
    }

    /**
     * tab切换更新
     */
    private OnUpdatePageSelectedListener onUpdatePageSelectedListener;

    public interface OnUpdatePageSelectedListener {
        void onPageSelected(int position);
    }

    public void setOnUpdatePageSelectedListener(
            OnUpdatePageSelectedListener onUpdatePageSelectedListener) {
        this.onUpdatePageSelectedListener = onUpdatePageSelectedListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(simpleOnPageChangeListener);
        }
    }
}
