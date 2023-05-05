package com.alivc.player.videolist.auivideofunctionlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alivc.player.videolist.auivideofunctionlist.adapter.AUIVideoFunctionListAdapter;
import com.alivc.player.videolist.auivideofunctionlist.domain.GestureGuidanceUseCase;
import com.alivc.player.videolist.auivideofunctionlist.player.AliPlayerPool;
import com.alivc.player.videolist.auivideofunctionlist.player.AliPlayerPreload;
import com.alivc.player.videolist.auivideofunctionlist.player.AliyunRenderView;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;

import java.util.List;

public class AUIVideoFunctionListController {

    private static final int BUFFER_POSITION = 5000;
    private final GestureGuidanceUseCase mGestureGuidanceUseCase;
    private boolean mIsPreloading = false;
    private final AliPlayerPreload mAliPlayerPreload;
    private int mCurrentPosition;
    //Play the 0 th video for the first time
    /**
     * Gesture Guidance show/hide
     */
    private final MutableLiveData<Boolean> _mGestureGuidanceLiveData = new MutableLiveData<>();
    LiveData<Boolean> mGestureGuidanceLiveData = _mGestureGuidanceLiveData;

    public AUIVideoFunctionListController(GestureGuidanceUseCase gestureGuidanceUseCase) {
        this.mGestureGuidanceUseCase = gestureGuidanceUseCase;
        mAliPlayerPreload = AliPlayerPreload.getInstance();
    }

    /**
     * load Video List
     * 1. set Video List to AliPlayerPreload
     */
    public void loadSources(List<VideoInfo> videoList) {
        mAliPlayerPreload.setUrls(videoList);
        _mGestureGuidanceLiveData.setValue(mGestureGuidanceUseCase.isShowGestureGuidance());
    }

    public void addSource(List<VideoInfo> videoBeanList) {
        mAliPlayerPreload.addUrls(videoBeanList);
    }

    public void showGestureGuidanceLiveData() {
        _mGestureGuidanceLiveData.setValue(true);
        mGestureGuidanceUseCase.setGestureGuidance(true);
    }

    public void onPrepared(int position, AUIVideoListViewHolder viewHolder) {
        if (mCurrentPosition == position) {
            if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
                ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayer().start();
            }
        }
    }

    public void openLoopPlay(boolean openLoopPlay) {
        AliPlayerPool.openLoopPlay(openLoopPlay);
    }

    /**
     * start MediaLoader after Player Buffer position >= {@link AUIVideoFunctionListController#BUFFER_POSITION}
     *
     * @param position RecyclerView item AdapterPosition
     * @param buffer   Player Buffer position
     */
    public void updateBufferPosition(int position, long buffer) {
        if (!mIsPreloading && position == mCurrentPosition && buffer >= BUFFER_POSITION) {
            mAliPlayerPreload.moveToSerial(position);
            mIsPreloading = true;
        }
    }

    public void onPageSelected(int position, AUIVideoListViewHolder viewHolder) {
        this.mCurrentPosition = position;
        mIsPreloading = false;
        mAliPlayerPreload.cancel(position);
        if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
            AliyunRenderView aliPlayer = ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayer();
            aliPlayer.getAliPlayer().start();
        }
    }

    /**
     * Pause Pre Video when Slide to half the screen
     */
    public void onPageHideHalf(int position, AUIVideoListViewHolder viewHolder) {
        if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
            ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayer().pause();
            viewHolder.showPlayIcon(false);
        }
    }

    public void onPageRelease(int position, AUIVideoListViewHolder viewHolder) {
        if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
            ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayer().pause();
        }
    }

    public void seekTo(long progress, AUIVideoListViewHolder viewHolder) {
        if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
            ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayer().seekTo(progress);
        }
    }

    public void changePlayState(AUIVideoListViewHolder viewHolder) {
        if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
            AliyunRenderView aliPlayer = ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayer();
            if (aliPlayer.isPlaying()) {
                aliPlayer.pause();
                viewHolder.showPlayIcon(true);
            } else {
                aliPlayer.start();
                viewHolder.showPlayIcon(false);
            }
        }
    }

    public void destroy() {
        AliPlayerPool.release();
    }

}
