package com.alivc.live.beautyui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.live.beautyui.adapter.AnimojiItemAdapter;
import com.alivc.live.beautyui.bean.AnimojiItemBean;

import java.util.ArrayList;

public class AnimojiContainerView extends FrameLayout implements View.OnClickListener {

    private static final int VIEW_ANIMATION_DURATION_MS = 330;

    private AnimojiContainerViewCallback mCallback;

    private final AnimojiItemAdapter mAnimojiItemAdapter = new AnimojiItemAdapter();

    public AnimojiContainerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AnimojiContainerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AnimojiContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AnimojiContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        initViews(context);
        mAnimojiItemAdapter.setListener(new AnimojiItemAdapter.AnimojiItemListener() {
            @Override
            public void onItemClicked(@Nullable AnimojiItemBean itemBean) {
                if (null != mCallback) {
                    mCallback.onItemClicked(itemBean);
                }
            }
        });
    }

    public void setCallback(AnimojiContainerViewCallback callback) {
        this.mCallback = callback;
    }

    public void initData(ArrayList<AnimojiItemBean> itemBeans) {
        mAnimojiItemAdapter.initData(itemBeans);
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

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_animoji_container, this, true);
        RecyclerView recyclerView = findViewById(R.id.animoji_item_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mAnimojiItemAdapter);
        recyclerView.setItemViewCacheSize(0);
        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        LinearLayout resetBtn = findViewById(R.id.animoji_reset_btn);
        ImageView putAwayBtn = findViewById(R.id.animoji_put_away_btn);
        resetBtn.setOnClickListener(this);
        putAwayBtn.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.animoji_reset_btn) {
            if (null != mCallback) {
                mCallback.onTabReset();
            }
        } else if (id == R.id.animoji_put_away_btn) {
            if (null != mCallback) {
                mCallback.onTabPutAway();
            }
        }
    }

    public interface AnimojiContainerViewCallback {
        void onItemClicked(@Nullable AnimojiItemBean bean);

        void onTabPutAway();

        void onTabReset();
    }
}
