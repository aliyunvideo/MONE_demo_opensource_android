package com.alivc.live.beautyui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.live.beautyui.R;
import com.alivc.live.beautyui.adapter.BeautyItemListAdapter;
import com.alivc.live.beautyui.bean.BeautyItemBean;
import com.alivc.live.beautyui.listener.BeautyItemListener;

import java.util.ArrayList;

/**
 * 美颜多Item的横向Recyclerview
 */
public class BeautyItemRecyclerView extends FrameLayout {

    private static final String TAG = BeautyItemRecyclerView.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private final BeautyItemListAdapter mBeautyItemListAdapter = new BeautyItemListAdapter(getContext());

    private BeautyItemListener mBeautyItemListener;

    public BeautyItemRecyclerView(@NonNull Context context) {
        super(context);

        initViews(context);
    }

    public BeautyItemRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initViews(context);
    }

    public BeautyItemRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initViews(context);
    }

    public void setCanMultiSelect(boolean canMultiSelect) {
        mBeautyItemListAdapter.setCanMultiSelect(canMultiSelect);
    }

    public void setItemBeans(ArrayList<BeautyItemBean> itemBeans) {
        mBeautyItemListAdapter.setItemBeans(itemBeans);
    }

    public void setBeautyItemListener(BeautyItemListener listener) {
        mBeautyItemListener = listener;
    }

    public void resetAllItemViews() {
        mBeautyItemListAdapter.resetAllItemViews();
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_beauty_item_list_recyclerview, this, true);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.beauty_item_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mBeautyItemListAdapter);
        mRecyclerView.setItemViewCacheSize(0);
        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mBeautyItemListAdapter.setBeautyItemListener(new BeautyItemListener() {
            @Override
            public void onItemClicked(@Nullable BeautyItemBean beautyItemBean) {
                if (mBeautyItemListener != null) {
                    mBeautyItemListener.onItemClicked(beautyItemBean);
                }
            }

            @Override
            public void onItemDataChanged(@Nullable BeautyItemBean beautyItemBean) {
                if (mBeautyItemListener != null) {
                    mBeautyItemListener.onItemDataChanged(beautyItemBean);
                }
            }
        });
    }

}
