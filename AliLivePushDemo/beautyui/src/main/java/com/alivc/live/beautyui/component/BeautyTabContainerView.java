package com.alivc.live.beautyui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alivc.live.beautyui.R;
import com.alivc.live.beautyui.adapter.BeautyTabPageAdapter;
import com.alivc.live.beautyui.bean.BeautyItemBean;
import com.alivc.live.beautyui.bean.BeautyTabBean;
import com.alivc.live.beautyui.listener.BeautyItemListener;
import com.alivc.live.beautyui.listener.BeautyTabListener;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

/**
 * 美颜TabLayout，包括：顶部调节SeekBar，中间TabLayout，ViewPager，和底部重置、收起按钮
 */
public class BeautyTabContainerView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = BeautyTabContainerView.class.getSimpleName();

    // top adjust view
    private TextView mTopAdjustTitleTv;
    private SeekBar mTopAdjustValueSb;
    private ImageView mBeautyCompareIv;

    // mid content view
    private TabLayout mTabLayout;

    // bottom ctrl view
    private LinearLayout mBeautyResetBtn;
    private ImageView mBeautyPutAwayBtn;

    private final BeautyTabPageAdapter mBeautyPageAdapter = new BeautyTabPageAdapter();
    private final ArrayList<BeautyTabBean> mBeautyTabBeans = new ArrayList<>();

    private BeautyTabListener mBeautyTabListener;

    private BeautyItemBean mBeautyItemBean;

    public BeautyTabContainerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BeautyTabContainerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BeautyTabContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_beauty_tab_container, this, true);
        initTopAdjustViews();
        initMidContentViews();
        initBottomControlViews();
    }

    private void initTopAdjustViews() {
        mTopAdjustTitleTv = findViewById(R.id.beauty_top_adjust_title_tv);
        mTopAdjustValueSb = findViewById(R.id.beauty_top_adjust_value_sb);
        mBeautyCompareIv = findViewById(R.id.beauty_compare_iv);
        // todo: feature not supported
        mBeautyCompareIv.setVisibility(View.INVISIBLE);

        mTopAdjustValueSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mBeautyItemBean != null) {
                    mBeautyItemBean.setSeekValue(seekBar.getProgress());
                }
                if (mBeautyTabListener != null) {
                    mBeautyTabListener.onTabItemDataChanged(getCurrentTabBean(), mBeautyItemBean);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initMidContentViews() {
        BeautyViewPager viewPager = findViewById(R.id.beauty_mid_content_view_pager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(mBeautyPageAdapter);
        viewPager.setCurrentItem(0);

        mTabLayout = findViewById(R.id.beauty_mid_content_tab_layout);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLayout.setupWithViewPager(viewPager);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onCurrentTabChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initBottomControlViews() {
        mBeautyResetBtn = findViewById(R.id.beauty_reset_btn);
        mBeautyResetBtn.setOnClickListener(this);

        mBeautyPutAwayBtn = findViewById(R.id.beauty_put_away_btn);
        mBeautyPutAwayBtn.setOnClickListener(this);
    }

    public void setBeautyTabListener(BeautyTabListener beautyTabListener) {
        mBeautyTabListener = beautyTabListener;
    }

    public void initData(BeautyTabBean tabBean) {
        mBeautyTabBeans.clear();
        mBeautyTabBeans.add(tabBean);
        updateTabBeans();
    }

    public void initData(ArrayList<BeautyTabBean> tabBeans) {
        mBeautyTabBeans.clear();
        mBeautyTabBeans.addAll(tabBeans);
        updateTabBeans();
    }

    private void updateTabBeans() {

        ArrayList<BeautyItemRecyclerView> viewCacheList = new ArrayList<>();
        for (BeautyTabBean tabBean : mBeautyTabBeans) {
            viewCacheList.add(getPageView(tabBean));
        }

        mBeautyPageAdapter.viewCacheList.clear();
        mBeautyPageAdapter.viewCacheList.addAll(viewCacheList);
        mBeautyPageAdapter.notifyDataSetChanged();

        for (int i = 0; i < mBeautyTabBeans.size(); ++i) {
            BeautyTabBean tabBean = mBeautyTabBeans.get(i);
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(tabBean));
                tab.setId(tabBean.getId());
            }
        }

        // if tab count is 1, hide the tab indicator.
        if (mBeautyTabBeans.size() < 2) {
            mTabLayout.setSelectedTabIndicatorHeight(0);
        }

    }

    public void resetAllTabs() {
        ArrayList<BeautyTabBean> beautyTabBeans = getBeautyTabBeans();
        final int tabCount = beautyTabBeans.size();
        for (int index = 0; index < tabCount; ++index) {
            BeautyTabBean tabBean = getTabBeanByPosition(index);
            if (index < mBeautyPageAdapter.viewCacheList.size()) {
                hideValueSeekBar();

                BeautyItemRecyclerView recyclerView = mBeautyPageAdapter.viewCacheList.get(index);
                recyclerView.resetAllItemViews();

                if (mBeautyTabListener != null) {
                    mBeautyTabListener.onTabReset(tabBean);
                }
            }
        }
    }

    // This is a bad design,
    // We should reset the data instead of the view,
    // because there can be multiple data, but it's better to have only one view.
    public void resetCurrentTab() {
        // Reset all the item data of the current tab and callback to reset all the item data of its subtab recursively.
        int index = mTabLayout.getSelectedTabPosition();
        if (index < mBeautyPageAdapter.viewCacheList.size()) {
            hideValueSeekBar();

            BeautyItemRecyclerView recyclerView = mBeautyPageAdapter.viewCacheList.get(index);
            recyclerView.resetAllItemViews();

            if (mBeautyTabListener != null) {
                mBeautyTabListener.onTabReset(getCurrentTabBean());
            }
        }

        mBeautyItemBean = null;
    }

    @Nullable
    public BeautyTabBean getCurrentTabBean() {
        int index = mTabLayout.getSelectedTabPosition();
        return getTabBeanByPosition(index);
    }

    public BeautyTabBean getTabBeanByPosition(int position) {
        if (mTabLayout != null) {
            if (position >= 0 && mBeautyTabBeans.size() > position) {
                return mBeautyTabBeans.get(position);
            }
        }
        return null;
    }

    public ArrayList<BeautyTabBean> getBeautyTabBeans() {
        return mBeautyTabBeans;
    }

    private View getTabView(@NonNull BeautyTabBean tabBean) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_beauty_tab_title, null);
        TextView titleView = view.findViewById(R.id.title_tv);
        titleView.setText(tabBean.getTitle());
        return view;
    }

    private BeautyItemRecyclerView getPageView(BeautyTabBean tabBean) {
        BeautyItemRecyclerView beautyItemRecyclerView = new BeautyItemRecyclerView(getContext());
        beautyItemRecyclerView.setBeautyItemListener(new BeautyItemListener() {
            @Override
            public void onItemClicked(@Nullable BeautyItemBean beautyItemBean) {
                if (beautyItemBean == null) return;
                mBeautyItemBean = beautyItemBean;
                doItemDataChanged(beautyItemBean);
            }

            @Override
            public void onItemDataChanged(@Nullable BeautyItemBean beautyItemBean) {
                if (beautyItemBean == null) return;
                doItemDataChanged(beautyItemBean);
            }

        });
        beautyItemRecyclerView.setCanMultiSelect(tabBean.isCanMultiSelect());
        beautyItemRecyclerView.setItemBeans(tabBean.getItemBeans());
        return beautyItemRecyclerView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.beauty_reset_btn) {
            resetCurrentTab();
        } else if (id == R.id.beauty_put_away_btn) {
            doPutAway();
        }
    }

    private void doPutAway() {

        if (mBeautyTabListener != null) {
            mBeautyTabListener.onTabPutAway(getCurrentTabBean());
        }

        mBeautyItemBean = null;

    }

    private void doItemDataChanged(@NonNull BeautyItemBean beautyItemBean) {
        if (mBeautyTabListener != null) {
            mBeautyTabListener.onTabItemDataChanged(getCurrentTabBean(), beautyItemBean);
        }

        if (beautyItemBean.getItemType() == BeautyItemBean.BeautyItemType.SEEK_BAR && beautyItemBean.isSelected()) {
            showValueSeekBar(beautyItemBean);
        } else {
            hideValueSeekBar();
        }

    }

    private void showValueSeekBar(@Nullable BeautyItemBean itemBean) {
        if (itemBean == null) return;

        if (mTopAdjustValueSb != null) {
            mTopAdjustValueSb.setProgress(itemBean.getSeekValue());
            mTopAdjustValueSb.setVisibility(View.VISIBLE);
        }

        if (mTopAdjustTitleTv != null) {
            mTopAdjustTitleTv.setText(itemBean.getTitle());
            mTopAdjustTitleTv.setVisibility(View.VISIBLE);
        }
    }

    private void hideValueSeekBar() {
        if (mTopAdjustValueSb != null) {
            mTopAdjustValueSb.setVisibility(View.GONE);
        }

        if (mTopAdjustTitleTv != null) {
            mTopAdjustTitleTv.setVisibility(View.GONE);
        }
    }

    private void onCurrentTabChanged() {
        BeautyTabBean tabBean = getCurrentTabBean();
        if (tabBean == null) return;

        hideValueSeekBar();

        /// pick the first select item
        for (BeautyItemBean itemBean : tabBean.getItemBeans()) {
            if (itemBean.isSelected()) {
                mBeautyItemBean = itemBean;
                if (itemBean.getItemType() == BeautyItemBean.BeautyItemType.SEEK_BAR) {
                    showValueSeekBar(itemBean);
                }
                break;
            }
        }

        // update reset btn status.
        if (tabBean.isResetShow()) {
            mBeautyResetBtn.setVisibility(View.VISIBLE);
        } else {
            mBeautyResetBtn.setVisibility(View.GONE);
        }
    }
}
