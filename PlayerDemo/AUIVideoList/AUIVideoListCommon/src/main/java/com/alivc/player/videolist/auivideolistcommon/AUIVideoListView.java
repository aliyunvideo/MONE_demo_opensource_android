package com.alivc.player.videolist.auivideolistcommon;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.adapter.CustomLayoutManager;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.OnLoadDataListener;
import com.alivc.player.videolist.auivideolistcommon.listener.OnRecyclerViewItemClickListener;
import com.alivc.player.videolist.auivideolistcommon.listener.OnSeekChangedListener;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;
import com.aliyun.player.bean.InfoBean;

import java.util.ArrayList;
import java.util.List;

public abstract class AUIVideoListView extends FrameLayout implements LifecycleOwner,
        OnRecyclerViewItemClickListener, PlayerListener, OnSeekChangedListener, CustomLayoutManager.OnViewPagerListener {

    private static final int DEFAULT_PRELOAD_NUMBER = 5;

    /**
     * Lifecycle
     */
    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    protected Context mContext;
    protected RecyclerView mRecyclerView;
    private LinearLayout mGestureLinearLayout;
    private CustomLayoutManager mCustomLayoutManager;

    private int mSelectedPosition;
    private OnLoadDataListener mOnLoadDataListener;
    private SwipeRefreshLayout mRefreshLayout;
    protected AUIVideoListAdapter mAUIVideoListAdapter;
    private boolean mIsLoadMore = false;
    private List<VideoInfo> mDataList = new ArrayList<>();

    public AUIVideoListView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIVideoListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIVideoListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        View mInflateView = LayoutInflater.from(mContext).inflate(R.layout.aui_video_list_view, this, true);
        mRecyclerView = mInflateView.findViewById(R.id.recyclerview);
        mGestureLinearLayout = mInflateView.findViewById(R.id.ll_gesture);
        mRefreshLayout = mInflateView.findViewById(R.id.refresh);

        mInflateView.findViewById(R.id.iv_back).setOnClickListener(v -> {
            if (mContext instanceof Activity) {
                ((Activity)mContext).finish();
            }
        });
        mGestureLinearLayout.setOnClickListener(view -> mGestureLinearLayout.setVisibility(View.GONE));

        mRefreshLayout.setOnRefreshListener(() -> {
            if (mOnLoadDataListener != null) {
                mOnLoadDataListener.onRefresh();
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(null);
//        mRecyclerView.setItemViewCacheSize(5);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mAUIVideoListAdapter = initAUIVideoListAdapter(mContext);
        mCustomLayoutManager = new CustomLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mCustomLayoutManager.setItemPrefetchEnabled(true);
//        mCustomLayoutManager.setPreloadItemCount(2);
        mCustomLayoutManager.setPreloadItemCount(1);
        mCustomLayoutManager.setOnViewPagerListener(this);
        mRecyclerView.setLayoutManager(mCustomLayoutManager);
        mRecyclerView.setAdapter(mAUIVideoListAdapter);

        mAUIVideoListAdapter.setOnItemClickListener(this);
        mAUIVideoListAdapter.setOnSeekBarStateChangeListener(this);
        mAUIVideoListAdapter.setOnPlayerListener(this);
    }

    abstract protected AUIVideoListAdapter initAUIVideoListAdapter(Context context);

    protected AUIVideoListViewHolder getViewHolderByPosition(int position) {
        return (AUIVideoListViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
    }

    protected AUIVideoListViewHolder findRecyclerViewLastVisibleHolder() {
        int lastVisibleItemPosition = mCustomLayoutManager.findLastVisibleItemPosition();
        return (AUIVideoListViewHolder) mRecyclerView.findViewHolderForAdapterPosition(lastVisibleItemPosition);
    }

    /**
     * hide Cover ImageView
     */
    public void hideCoverView() {
        AUIVideoListViewHolder viewHolderForAdapterPosition = (AUIVideoListViewHolder) mRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
        if (viewHolderForAdapterPosition != null) {
            viewHolderForAdapterPosition.getCoverView().setVisibility(View.INVISIBLE);
        }
    }

    /**
     * show Cover ImageView
     */
    public void showCoverView(int position) {
        AUIVideoListViewHolder viewHolderForAdapterPosition = (AUIVideoListViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolderForAdapterPosition != null) {
            viewHolderForAdapterPosition.getCoverView().setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mLifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        } else if (visibility == GONE || visibility == INVISIBLE) {
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
            mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onSeek(int position, long seekPosition) {

    }

    @Override
    public void onPrepared(int position) {

    }

    @Override
    public void onCompletion(int position) {

    }

    @Override
    public void onInfo(int position, InfoBean infoBean) {

    }

    @Override
    public void onPlayStateChanged(int position, boolean isPaused) {

    }

    @Override
    public void onRenderingStart(int position, long duration) {

    }

    @Override
    public void onPageShow(int position) {
        if (position != mSelectedPosition) {
            showCoverView(position);
        }
    }

    @Override
    public void onPageSelected(int position) {
        this.mSelectedPosition = position;
        System.out.println("abc : selected " + position + " --- size " + mDataList.size() + " --- loadMore " + mIsLoadMore);
        if (mDataList != null && mDataList.size() - position < DEFAULT_PRELOAD_NUMBER && !mIsLoadMore) {
            // 正在加载中, 防止网络太慢或其他情况造成重复请求列表
            mIsLoadMore = true;
            if (mOnLoadDataListener != null) {
                mOnLoadDataListener.onLoadMore();
            }
        }
    }

    @Override
    public void onPageRelease(int position) {

    }

    @Override
    public void onPageHideHalf(int position) {

    }

    /*
     * =============================================
     */

    public void setOnRefreshListener(OnLoadDataListener listener) {
        this.mOnLoadDataListener = listener;
    }

    public void loadSources(List<VideoInfo> videoBeanList) {
        this.mDataList.clear();
        this.mDataList.addAll(videoBeanList);
        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
        mAUIVideoListAdapter.submitList(videoBeanList);
    }

    public void addSources(List<VideoInfo> videoBeanList) {
        mIsLoadMore = false;
        this.mDataList.addAll(mDataList.size(), videoBeanList);
        mAUIVideoListAdapter.submitList(mDataList);
    }

    public abstract void openLoopPlay(boolean openLoopPlay);

    public abstract void autoPlayNext(boolean autoPlayNext);


    /*
     * =============================================
     */
}
