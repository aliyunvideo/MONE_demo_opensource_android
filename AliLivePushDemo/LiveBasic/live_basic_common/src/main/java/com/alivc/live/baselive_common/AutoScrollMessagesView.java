package com.alivc.live.baselive_common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 自动追加滚动消息视图
 */
public class AutoScrollMessagesView extends RecyclerView {
    private final AutoScrollMessagesAdapter mAdapter = new AutoScrollMessagesAdapter();

    public AutoScrollMessagesView(@NonNull Context context) {
        this(context, null, 0);
    }

    public AutoScrollMessagesView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollMessagesView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(linearLayoutManager);
        setAdapter(mAdapter);

        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    public void appendMessage(String msg) {
        mAdapter.applyDataMessage(msg);
        smoothScrollToPosition(mAdapter.getItemCount());
    }
}
