package com.alivc.auiplayer.videoepisode.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alivc.auiplayer.videoepisode.AUIVideoEpisodeController;
import com.alivc.auiplayer.videoepisode.adapter.AUIVideoEpisodeAdapter;
import com.alivc.auiplayer.videoepisode.data.AUIEpisodeData;
import com.alivc.auiplayer.videoepisode.data.AUIEpisodeVideoInfo;
import com.alivc.auiplayer.videoepisode.listener.OnInteractiveEventListener;
import com.alivc.auiplayer.videoepisode.listener.OnPanelEventListener;
import com.alivc.auiplayer.videoepisode.listener.OnSurfaceListener;
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

import java.util.ArrayList;
import java.util.List;

public class AUIVideoEpisodeListView extends AUIVideoListView {
    private AUIVideoEpisodeController mController;

    private boolean mAutoPlayNext;

    // 默认初始跳转到的短剧集数
    private int mInitialEpisodeIndex = 0;

    private boolean mInited = false;

    private AUIEpisodeData mEpisodeData = null;

    public AUIVideoEpisodeListView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIVideoEpisodeListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIVideoEpisodeListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected AUIVideoListViewType getViewType() {
        return AUIVideoListViewType.EPISODE;
    }

    private void init(Context context) {
        this.mContext = context;
        setRefreshLayoutEnable(false);
        mController = new AUIVideoEpisodeController(mContext);
        mController.setPlayerListener(this);
        openLoopPlay(true);
        autoPlayNext(true);
    }

    /**
     * 设置短剧初始集数
     * <p>
     * 通过该接口，设置短剧初始集数，则默认跳转展示第几集；如果不设置，则默认从第1集开始
     *
     * @param index 短剧集数
     */
    public void setInitialEpisodeIndex(int index) {
        mInitialEpisodeIndex = index;
    }

    private void addTextureView(int position) {
        mRecyclerView.post(() -> {
            AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(position);
            if (viewHolderByPosition != null) {
                if (viewHolderByPosition instanceof AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder) {
                    mController.setSurface(((AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder) viewHolderByPosition).getSurface());
                }
            }
            Log.i("CheckFunc", "addTextureView" + " mSelectedPosition: " + mSelectedPosition);
        });
    }

    @Override
    protected AUIVideoListLayoutManager initLayoutManager() {
        return new AUIVideoEpisodeLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected AUIVideoListAdapter initAUIVideoListAdapter(Context context) {
        AUIVideoEpisodeAdapter episodeAdapter = new AUIVideoEpisodeAdapter(new AUIVideoListDiffCallback());
        episodeAdapter.initPanelEventListener(new OnPanelEventListener() {
            @Override
            public void onItemClicked(AUIEpisodeVideoInfo episodeVideoInfo) {
                AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(mSelectedPosition);
                if (viewHolderByPosition instanceof AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder) {
                    ((AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder) viewHolderByPosition).hidePanelIfNeed();
                }
                int position = AUIEpisodeData.getEpisodeIndex(mEpisodeData, episodeVideoInfo);
                MoveToPosition(position);
                addTextureView(position);
                mController.onPageSelected(position);
                mSelectedPosition = position;
            }

            @Override
            public void onClickRetract() {

            }
        });

        episodeAdapter.initSurfaceListener(new OnSurfaceListener() {
            @Override
            public void onSurfaceCreate(int index, Surface surface) {
                if (index == mSelectedPosition) {
                    mController.setSurface(surface);
                } else if (index == mSelectedPosition + 1) {
                    // TODO: 当前版本，PreRender Player仅支持预渲染列表下一个视频的画面；指定预渲染上一个视频的画面，有待后续版本支持。
                    // 只对后面的 viewHolder 进行预加载
                    mController.setSurfaceToPreRenderPlayer(surface);
                }
            }

            @Override
            public void onSurfaceChanged(int index, int width, int height) {

            }

            @Override
            public void onSurfaceDestroyed(int index) {
            }
        });

        episodeAdapter.initInteractiveEventListener(new OnInteractiveEventListener() {
            @Override
            public void onClickLike(AUIEpisodeVideoInfo episodeVideoInfo, boolean isSelected) {
                if (mEpisodeData == null || mEpisodeData.list == null) {
                    return;
                }
                // 更新本地数据
                for (AUIEpisodeVideoInfo dataSource : mEpisodeData.list) {
                    if (dataSource.getId() == episodeVideoInfo.getId()) {
                        dataSource.likeCount = isSelected ? dataSource.likeCount + 1 : dataSource.likeCount - 1;
                        dataSource.isLiked = isSelected;
                    }
                }
                // 请求点赞API...
            }

            @Override
            public void onClickComment(AUIEpisodeVideoInfo episodeVideoInfo) {
            }

            @Override
            public void onClickShare(AUIEpisodeVideoInfo episodeVideoInfo) {

            }
        });

        return episodeAdapter;
    }

    @Override
    public void loadSources(List<VideoInfo> videoBeanList) {
        List<AUIEpisodeVideoInfo> tempVideoBeanList = new ArrayList<>();
        for (VideoInfo videoInfo : videoBeanList) {
            if (videoInfo instanceof AUIEpisodeVideoInfo) {
                tempVideoBeanList.add((AUIEpisodeVideoInfo) videoInfo);
            }
        }

        mController.loadSource(tempVideoBeanList);
        super.loadSources(videoBeanList);
    }

    @Override
    public void addSources(List<VideoInfo> videoBeanList) {
        List<AUIEpisodeVideoInfo> tempVideoBeanList = new ArrayList<>();
        for (VideoInfo videoInfo : videoBeanList) {
            if (videoInfo instanceof AUIEpisodeVideoInfo) {
                tempVideoBeanList.add((AUIEpisodeVideoInfo) videoInfo);
            }
        }

        mController.addSource(tempVideoBeanList);
        super.addSources(videoBeanList);
    }

    public void updateEpisodeData(AUIEpisodeData episodeData, boolean isRefresh) {
        if (episodeData == null || episodeData.list == null) {
            Toast.makeText(mContext, "网络异常，请检查网络是否正确连接", Toast.LENGTH_SHORT).show();
            setRefreshing(false);
            return;
        }

        mEpisodeData = episodeData;
        if (mAUIVideoListAdapter instanceof AUIVideoEpisodeAdapter) {
            ((AUIVideoEpisodeAdapter) mAUIVideoListAdapter).provideData(mEpisodeData);
        }
        ArrayList<VideoInfo> tempVideoInfoList = new ArrayList<>();
        tempVideoInfoList.addAll(episodeData.list);

        if (isRefresh) {
            loadSources(tempVideoInfoList);
        } else {
            addSources(tempVideoInfoList);
        }
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
    public void onPlayStateChanged(int position, boolean isPaused) {
        super.onPlayStateChanged(position, isPaused);
        Log.i("CheckFunc", " onPlayStateChanged " + " mSelectedPosition: " + mSelectedPosition + " isPaused: " + isPaused);
        AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(mSelectedPosition);
        if (viewHolderByPosition != null) {
            viewHolderByPosition.showPlayIcon(isPaused);
        }
    }

    @Override
    public void onSeek(int position, long seekPosition) {
        super.onSeek(position, seekPosition);
        mController.seek(seekPosition);
    }

    @Override
    public void onItemClick(int position) {
        // 交互体验优化，如果当前短剧item页的面板处于展开状态，全屏触摸事件显示时，先关闭面板
        AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(position);
        if (viewHolderByPosition instanceof AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder) {
            if (((AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder) viewHolderByPosition).hidePanelIfNeed()) {
                return;
            }
        }
        mController.onPlayStateChange();
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
        Log.i("CheckFunc", "onInitComplete" + " mSelectedPosition: " + mSelectedPosition);

        // 只允许回调一次onInitComplete；已有的逻辑，如果去掉，会导致2->1会直接跳到0，存在bug
        if (mInited) {
            return;
        }
        mInited = true;

        this.mSelectedPosition = mInitialEpisodeIndex;
        mInitialEpisodeIndex = 0;

        MoveToPosition(mSelectedPosition);
        addTextureView(mSelectedPosition);
        mController.onPageSelected(mSelectedPosition);
        setPreRenderViewHolder(mSelectedPosition + 1);
    }

    @Override
    public void onPageSelected(int position) {
        Log.i("CheckFunc", "onPageSelected " + " position " + position);
        super.onPageSelected(position);//校验，并不做实际上的跳转

        mRecyclerView.post(() -> {
            AUIVideoListViewHolder playerViewHolder = getViewHolderByPosition(position);
            if (playerViewHolder == null) {
                Log.w("CheckFunc", "onPageSelected , playerViewHolder is null !" + " position:  " + position);
                return;
            }
            mController.onPageSelected(position, ((AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder) playerViewHolder).getSurface());
            // 只对后面的 viewHolder 进行预加载
            setPreRenderViewHolder(position + 1);
        });
    }

    @Override
    public void onPageRelease(int position) {
        super.onPageRelease(position);
        AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(position);
        if (viewHolderByPosition != null) {
            viewHolderByPosition.showPlayIcon(false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mController.destroy();
    }

    @Override
    public void onRenderingStart(int position, long duration) {
        super.onRenderingStart(position, duration);
        AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(mSelectedPosition);
        if (viewHolderByPosition != null) {
            viewHolderByPosition.getSeekBar().setMax((int) duration);
        }
    }

    @Override
    public void onInfo(int duration, InfoBean infoBean) {
        super.onInfo(duration, infoBean);
        if (infoBean.getCode() == InfoCode.CurrentPosition) {
            int progress = (int) infoBean.getExtraValue();
            AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(mSelectedPosition);
            if (viewHolderByPosition != null) {
                viewHolderByPosition.getSeekBar().setProgress(progress);//从listener获取到的信息，不断回调获取
            }
            if (progress >= duration && !mController.isCurrentPlayerStateCallBackPaused()) {
                //Log.i("CheckFunc", "onInfo  " + "progress: " + progress + " duration:  " + duration);
                onCompletion(duration);
            }
        }
    }

    @Override
    public void onCompletion(int duration) {
        super.onCompletion(duration);
        if (duration == -1) {
            return;
        }
        if (mAutoPlayNext && mSelectedPosition + 1 < mAUIVideoListAdapter.getItemCount()) {
            Log.i("CheckFunc", "onCompletion  " + "moveToNextPosition: " + (mSelectedPosition + 1) + " duration:  " + duration + " getItemCount: " + mAUIVideoListAdapter.getItemCount());

            // 直接跳转，可以避免获取不到viewHolder
            mRecyclerView.smoothScrollToPosition(mSelectedPosition + 1);
            onPageSelected(mSelectedPosition + 1);
        }
    }

    @Override
    public void onError(ErrorInfo errorInfo) {
        super.onError(errorInfo);
        Toast.makeText(mContext, "error: " + errorInfo.getCode() + " -- " + errorInfo.getMsg(), Toast.LENGTH_SHORT).show();
    }

    private void setPreRenderViewHolder(int preRenderPosition) {
        AUIVideoListViewHolder playerViewHolder = getViewHolderByPosition(preRenderPosition);
        if (playerViewHolder == null) {
            Log.w("CheckFunc", "setPreRenderViewHolder failed: " + preRenderPosition);
            return;
        }
        mController.setSurfaceToPreRenderPlayer(((AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder) playerViewHolder).getSurface());
    }

    public void setOnBackground(boolean isOnBackground) {
        if (isOnBackground) {
            mController.pausePlay();
        } else {
            mController.resumePlay();
        }
    }
}

