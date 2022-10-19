package com.aliyun.svideo.recorder.views.effects.filter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.svideo.recorder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 滤镜选择器UI
 */
public class AUIFilterLoadingView extends FrameLayout {
    private AUIFilterAdapter mAdapter;
    private List<String> mDataList;
    private ContentLoadingProgressBar mLoadingProgressBar;
    private RecyclerView mRecyclerView;
    private OnFilterItemClickListener mOnItemClickListener;

    public AUIFilterLoadingView(@NonNull Context context) {
        this(context, null);
    }

    public AUIFilterLoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AUIFilterLoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.ugsv_recorder_chooser_filter, this, true);
        initProgressView(view);
        initRecyclerView(view);
    }

    private void initRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.ugsv_recorder_filter_container);
        mDataList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new AUIFilterAdapter(getContext(), mDataList);

        mRecyclerView.setAdapter(mAdapter);
        // item点击事件
        mAdapter.setOnItemClickListener((effectInfo, index) -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(effectInfo, index);
            }
        });
    }

    private void progressLoading() {
        mLoadingProgressBar.postDelayed(() -> mLoadingProgressBar.hide(), 3000);
    }

    private void initProgressView(View view) {
        mLoadingProgressBar = view.findViewById(R.id.content_progress);
        mLoadingProgressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getContext(), R.color.alivc_common_bg_cyan_light),
                PorterDuff.Mode.MULTIPLY);
        progressLoading();
    }

    public void notifyDataChange(List<String> filterInfoList) {
        this.mDataList.clear();
        this.mDataList.addAll(filterInfoList);
        if (mLoadingProgressBar != null) {
            mLoadingProgressBar.hide();
        }
        updateRecyclerState();
        mAdapter.notifyDataSetChanged();
    }

    public void setOnFilterListItemClickListener(OnFilterItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private void updateRecyclerState() {
        if (mDataList != null && mDataList.size() != 0) {
            mRecyclerView.setVisibility(VISIBLE);
        } else {
            mRecyclerView.setVisibility(GONE);
        }
    }

    public void setFilterSelectedPosition(int filterSelectedPosition) {
        mAdapter.setSelectedPos(filterSelectedPosition);
    }
}
