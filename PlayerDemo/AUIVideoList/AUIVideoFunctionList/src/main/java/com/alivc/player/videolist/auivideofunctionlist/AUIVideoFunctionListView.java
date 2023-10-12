package com.alivc.player.videolist.auivideofunctionlist;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alivc.player.videolist.auivdieofunctionlist.R;
import com.alivc.player.videolist.auivideofunctionlist.adapter.AUIVideoFunctionListAdapter;
import com.alivc.player.videolist.auivideofunctionlist.adapter.AUIVideoFunctionListLayoutManager;
import com.alivc.player.videolist.auivideofunctionlist.domain.GestureGuidanceUseCase;
import com.alivc.player.videolist.auivideofunctionlist.player.AliPlayerPool;
import com.alivc.player.videolist.auivideofunctionlist.utils.GlobalSettings;
import com.alivc.player.videolist.auivideolistcommon.AUIVideoListView;
import com.alivc.player.videolist.auivideolistcommon.AUIVideoListViewType;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListDiffCallback;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListLayoutManager;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class AUIVideoFunctionListView extends AUIVideoListView {

    private AUIVideoFunctionListController mController;
    private AUIVideoListViewHolder mViewHolderForAdapterPosition;

    private Context mContext;
    private AUIVideoFunctionListAdapter mAUIVideoListAdapter;
    private boolean mAutoPlayNext;


    public AUIVideoFunctionListView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIVideoFunctionListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIVideoFunctionListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected AUIVideoListViewType getViewType() {
        return AUIVideoListViewType.FUNCTION_LIST;
    }

    private void init(Context context) {
        this.mContext = context;
        AliPlayerPool.init(mContext);

        //Local Cache Dir  TODO 修改文件路径
        GlobalSettings.CACHE_DIR = mContext.getExternalCacheDir().getAbsolutePath() + File.separator + "Preload";
        GestureGuidanceUseCase gestureGuidanceUseCase = new GestureGuidanceUseCase(mContext);
        //TODO
//        AUIVideoListLocalDataSource auiVideoListLocalDataSource = new AUIVideoListLocalDataSource(mContext);
        mController = new AUIVideoFunctionListController(gestureGuidanceUseCase);

        initObserver();
    }

    @Override
    protected AUIVideoListLayoutManager initLayoutManager() {
        AUIVideoFunctionListLayoutManager mCustomLayoutManager = new AUIVideoFunctionListLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mCustomLayoutManager.setItemPrefetchEnabled(true);
        mCustomLayoutManager.setPreloadItemCount(1);
        return mCustomLayoutManager;
    }


    private void initObserver() {
        mController.mGestureGuidanceLiveData.observe(this, shown -> {
            if (!shown) {
                showInteractiveGuidance();
            }
        });
    }

    @Override
    protected AUIVideoListAdapter initAUIVideoListAdapter(Context context) {
        mAUIVideoListAdapter = new AUIVideoFunctionListAdapter(new AUIVideoListDiffCallback());
        return mAUIVideoListAdapter;
    }

    public void showInteractiveGuidance() {
        mController.showGestureGuidanceLiveData();
        //TODO
//        mGestureLinearLayout.setVisibility(View.VISIBLE);
//        mGestureLinearLayout.postDelayed(() -> mGestureLinearLayout.setVisibility(View.GONE), 3000);
    }

    public void showPlayIcon(boolean isShow) {
        if (mViewHolderForAdapterPosition != null) {
            mViewHolderForAdapterPosition.showPlayIcon(isShow);
        }
    }

    /**
     * video duration
     *
     * @param duration company ms
     */
    public void onVideoFrameShow(long duration) {
        mViewHolderForAdapterPosition = findRecyclerViewLastVisibleHolder();
        if (mViewHolderForAdapterPosition != null) {
            mViewHolderForAdapterPosition.getSeekBar().setMax((int) duration);
        }
    }

    /**
     * update Current Position
     *
     * @param extraValue ms
     */
    public void updateCurrentPosition(long extraValue) {
        if (mViewHolderForAdapterPosition != null) {
            mViewHolderForAdapterPosition.getSeekBar().setProgress((int) extraValue);
        }
    }

    public void showError(ErrorInfo errorInfo) {
        AVToast.show(mContext, true, "error: " + errorInfo.getCode() + " -- " + errorInfo.getMsg());
    }

    public void loadMore() {
        AVToast.show(mContext, true, R.string.aui_video_list_coming_soon);
    }

    @Override
    public void loadSources(List<VideoInfo> videoBeanList) {
        super.loadSources(videoBeanList);
        mController.loadSources(videoBeanList);
    }

    @Override
    public void addSources(List<VideoInfo> videoBeanList) {
        super.addSources(videoBeanList);
        mController.addSource(videoBeanList);
    }

    @Override
    public void openLoopPlay(boolean openLoopPlay) {
        mController.openLoopPlay(openLoopPlay);
    }

    @Override
    public void autoPlayNext(boolean autoPlayNext) {
        this.mAutoPlayNext = autoPlayNext;
    }

    @Override
    public void onItemClick(int position) {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            mController.changePlayState(viewHolder);
        }
    }

    @Override
    public void onBackPress() {
        if (mContext instanceof Activity) {
            if (!((Activity) mContext).isFinishing()) {
                ((Activity) mContext).finish();
            }
        }
    }

    @Override
    public void onInitComplete() {

    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            mController.onPageSelected(position, viewHolder);
        }
    }

    @Override
    public void onPageRelease(int position) {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            mController.onPageRelease(position, viewHolder);
        }
    }

    @Override
    public void onPageHideHalf(int position) {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            mController.onPageHideHalf(position, viewHolder);
        }
    }

    /**
     * Player Listener onPrepared
     */
    @Override
    public void onPrepared(int position) {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            mController.onPrepared(position, viewHolder);
        }
    }

    /**
     * Player Listener onInfo
     */
    @Override
    public void onInfo(int position, InfoBean infoBean) {
        if (infoBean.getCode() == InfoCode.BufferedPosition) {
            long buffer = infoBean.getExtraValue();
            mController.updateBufferPosition(position, buffer);
        } else if (infoBean.getCode() == InfoCode.CurrentPosition) {
            updateCurrentPosition(infoBean.getExtraValue());
        }
    }

    @Override
    public void onPlayStateChanged(int position, boolean isPaused) {
        if (position == mSelectedPosition) {
            showPlayIcon(isPaused);
        }
    }

    @Override
    public void onRenderingStart(int position, long duration) {
        onVideoFrameShow(duration);
    }

    @Override
    public void onCompletion(int position) {
        if (position < mAUIVideoListAdapter.getItemCount() && mAutoPlayNext) {
            mRecyclerView.smoothScrollToPosition(position + 1);
        }
    }

    @Override
    public void onSeek(int position, long seekPosition) {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            mController.seekTo(seekPosition, viewHolder);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Glide.get(mContext).clearMemory();
        new Thread(() -> Glide.get(mContext).clearDiskCache()).start();
        mController.destroy();
    }

}