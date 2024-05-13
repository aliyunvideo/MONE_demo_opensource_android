package com.alivc.player.videolist.auivideofunctionlist;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alivc.player.videolist.auivdieofunctionlist.R;
import com.alivc.player.videolist.auivideofunctionlist.adapter.AUIVideoFunctionListAdapter;
import com.alivc.player.videolist.auivideofunctionlist.adapter.AUIVideoFunctionListLayoutManager;
import com.alivc.player.videolist.auivideolistcommon.AUIVideoListView;
import com.alivc.player.videolist.auivideolistcommon.AUIVideoListViewType;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListDiffCallback;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListLayoutManager;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.bumptech.glide.Glide;

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
        mController = new AUIVideoFunctionListController(context);
    }

    @Override
    protected AUIVideoListLayoutManager initLayoutManager() {
        return new AUIVideoFunctionListLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected AUIVideoListAdapter initAUIVideoListAdapter(Context context) {
        mAUIVideoListAdapter = new AUIVideoFunctionListAdapter(new AUIVideoListDiffCallback());
        return mAUIVideoListAdapter;
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
    public void onVideoFrameShow(int position, long duration) {
        mViewHolderForAdapterPosition = findRecyclerViewLastVisibleHolder();

        AUIVideoListViewHolder videoListViewHolder = getViewHolderByPosition(position);
        if (videoListViewHolder != null && videoListViewHolder.getSeekBar() != null) {
            videoListViewHolder.getSeekBar().setMax((int) duration);
        }
    }

    /**
     * update Current Position
     *
     * @param extraValue ms
     */
    public void updateCurrentPosition(int position, long extraValue) {
        AUIVideoListViewHolder videoListViewHolder = getViewHolderByPosition(position);
        if (videoListViewHolder != null && videoListViewHolder.getSeekBar() != null) {
            videoListViewHolder.getSeekBar().setProgress((int) extraValue);
        }
    }

    public void showError(ErrorInfo errorInfo) {
        Toast.makeText(mContext, "error: " + errorInfo.getCode() + " -- " + errorInfo.getMsg(), Toast.LENGTH_SHORT).show();
    }

    public void loadMore() {
        Toast.makeText(mContext, R.string.aui_video_list_coming_soon, Toast.LENGTH_SHORT).show();
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
        mController.onPageSelected(position, viewHolder);
    }

    @Override
    public void onPageRelease(int position) {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            mController.onPageRelease(position, viewHolder);
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
        if (infoBean.getCode() == InfoCode.CurrentPosition) {
            updateCurrentPosition(position, infoBean.getExtraValue());
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
        onVideoFrameShow(position, duration);
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