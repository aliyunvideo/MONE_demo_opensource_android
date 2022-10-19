package com.alivc.live.beautyui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alivc.live.beautyui.bean.BeautyItemBean;
import com.alivc.live.beautyui.bean.BeautyTabBean;
import com.alivc.live.beautyui.component.BeautyTabContainerView;
import com.alivc.live.beautyui.listener.BeautyTabListener;

import java.util.ArrayList;

/**
 * 美颜界面主View
 * 里面包含：一级View（多TAB组成），二级View（单TAB组成，由点击一级View的Item弹出）
 */
public class BeautyContainerView extends FrameLayout {

    private static final String TAG = BeautyContainerView.class.getSimpleName();

    private static final int VIEW_ANIMATION_DURATION_MS = 330;

    private BeautyTabContainerView mBeautyMainContainer;
    private FrameLayout mBeautySecondaryContainer;

    private BeautyTabListener mBeautyTabListener;

    public ArrayList<BeautyTabContainerView> mSubTabViewCacheList = new ArrayList<>();

    public BeautyContainerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BeautyContainerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BeautyContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setBeautyTabListener(BeautyTabListener BeautyTabListener) {
        mBeautyTabListener = BeautyTabListener;
    }

    public void setVisibilityWithAnimation(boolean visible) {
        float fromY = visible ? 1.0f : 0.0f;
        float toY = visible ? 0.0f : 1.0f;
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, fromY, Animation.RELATIVE_TO_SELF, toY);
        animation.setDuration(VIEW_ANIMATION_DURATION_MS);
        startAnimation(animation);

        int visibility = visible ? View.VISIBLE : View.GONE;
        setVisibility(visibility);
    }

    public void initData(BeautyUIDataInjectorInterface dataInjectorItf) {
        if (dataInjectorItf != null && dataInjectorItf.getBeautyTabBeans() != null) {
            mBeautyMainContainer.initData(dataInjectorItf.getBeautyTabBeans());
        }
    }

    private void init(Context context) {
        initViews(context);
    }

    private void initViews(Context context) {

        LayoutInflater.from(context).inflate(R.layout.layout_beauty_container, this, true);

        mBeautyMainContainer = findViewById(R.id.layout_beauty_main_content);

        mBeautyMainContainer.setBeautyTabListener(new BeautyTabListener() {

            @Override
            public void onTabItemDataChanged(@Nullable BeautyTabBean tabBean, @Nullable BeautyItemBean beautyItemBean) {
                if (tabBean == null || beautyItemBean == null) return;

                if (beautyItemBean.getItemType() == BeautyItemBean.BeautyItemType.CONFIG_PAGE) {
                    showSecondaryContainer(beautyItemBean);
                } else {

                    if (mBeautyTabListener != null) {
                        mBeautyTabListener.onTabItemDataChanged(tabBean, beautyItemBean);
                    }
                }

            }

            @Override
            public void onTabPutAway(@Nullable BeautyTabBean tabBean) {
                if (mBeautyTabListener != null) {
                    mBeautyTabListener.onTabPutAway(tabBean);
                }
            }

            @Override
            public void onTabReset(@Nullable BeautyTabBean tabBean) {
                resetAllSubTabs(tabBean);
            }
        });

        mBeautySecondaryContainer = findViewById(R.id.layout_beauty_type_config);
    }

    public void reset() {
        mBeautyMainContainer.resetAllTabs();
    }

    private void resetAllSubTabs(@Nullable BeautyTabBean tabBean) {
        if (tabBean == null) return;
        for (BeautyItemBean itemBean : tabBean.getItemBeans()) {
            // 现在需要找到当前TAB下的item有子TAB的，并重置它，单纯的子item已经在回调的地方`BeautyTabContainerView`里面重置了。
            if (itemBean.getItemType() == BeautyItemBean.BeautyItemType.CONFIG_PAGE) {
                BeautyTabBean subTabBean = itemBean.getTabBean();
                BeautyTabContainerView view = getBeautySubTabViewFromCacheList(subTabBean);
                if (view != null) {
                    view.resetCurrentTab();
                }
            }
        }
    }

    private void showMainContainer() {
        // remove all views from secondary container.
        // just like `removeAllSubviews` on iOS platform.
        if (mBeautySecondaryContainer != null) {
            mBeautySecondaryContainer.removeAllViews();
        }

        // make main container visible again.
        if (mBeautyMainContainer != null) {
            mBeautyMainContainer.setVisibility(View.VISIBLE);
        }
    }

    private void showSecondaryContainer(@Nullable BeautyItemBean beautyItemBean) {

        if (beautyItemBean != null && beautyItemBean.getTabBean() != null && mBeautySecondaryContainer != null) {
            // init a new config view, and put it onto secondary container.
            BeautyTabContainerView containerView = getBeautySubTabViewContainer(beautyItemBean.getTabBean());
            mSubTabViewCacheList.add(containerView);
            mBeautySecondaryContainer.addView(containerView);

            // make main container invisible.
            if (mBeautyMainContainer != null) {
                mBeautyMainContainer.setVisibility(View.GONE);
            }
        }

    }

    private BeautyTabContainerView getBeautySubTabViewContainer(@NonNull BeautyTabBean tabBean) {
        BeautyTabContainerView containerView = getBeautySubTabViewFromCacheList(tabBean);

        if (containerView == null) {
            containerView = initBeautySubTabViewContainer(tabBean);
        }

        return containerView;
    }

    // The reason for setting multiple views is,
    // when we open it again, we can remember where we slid to before.
    private BeautyTabContainerView initBeautySubTabViewContainer(@Nullable BeautyTabBean beautyTabBean) {

        BeautyTabContainerView containerView = new BeautyTabContainerView(getContext());
        containerView.initData(beautyTabBean);

        containerView.setBeautyTabListener(new BeautyTabListener() {

            @Override
            public void onTabItemDataChanged(@Nullable BeautyTabBean tabBean, @Nullable BeautyItemBean beautyItemBean) {
                if (tabBean == null || beautyItemBean == null) return;
                if (mBeautyTabListener != null) {
                    mBeautyTabListener.onTabItemDataChanged(tabBean, beautyItemBean);
                }
            }

            @Override
            public void onTabPutAway(@Nullable BeautyTabBean tabBean) {
                showMainContainer();
            }

            @Override
            public void onTabReset(@Nullable BeautyTabBean tabBean) {

            }

        });

        return containerView;
    }

    private BeautyTabContainerView getBeautySubTabViewFromCacheList(@Nullable BeautyTabBean beautyTabBean) {
        if (beautyTabBean == null) return null;

        for (BeautyTabContainerView subTabView : mSubTabViewCacheList) {
            ArrayList<BeautyTabBean> subTabBeans = subTabView.getBeautyTabBeans();
            if (subTabBeans != null) {
                for (BeautyTabBean subTabBean : subTabBeans) {
                    if (subTabBean.getId() == beautyTabBean.getId()) {
                        return subTabView;
                    }
                }
            }
        }

        return null;
    }
}
