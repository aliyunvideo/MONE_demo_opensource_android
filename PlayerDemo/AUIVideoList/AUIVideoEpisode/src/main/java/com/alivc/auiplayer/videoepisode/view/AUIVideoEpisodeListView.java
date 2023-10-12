package com.alivc.auiplayer.videoepisode.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alivc.auiplayer.videoepisode.AUIVideoEpisodeController;
import com.alivc.auiplayer.videoepisode.adapter.AUIVideoEpisodeAdapter;
import com.alivc.auiplayer.videoepisode.data.AUIEpisodeData;
import com.alivc.auiplayer.videoepisode.data.AUIEpisodeDataEvent;
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
import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AUIVideoEpisodeListView extends AUIVideoListView {
    private AUIVideoEpisodeController mController;

    private boolean mAutoPlayNext;

    private int mCurrentIndex = -1;

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
        initTextureView();
        mController = new AUIVideoEpisodeController(mContext);
        mController.setPlayerListener(this);
        openLoopPlay(true);
        autoPlayNext(true);
    }

    private void initTextureView() {
    }

    private void addTextureView(int position) {
        mRecyclerView.post(() -> {
            AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(position);
            if (viewHolderByPosition != null) {
                if (viewHolderByPosition instanceof AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder) {
                    mController.setSurface(((AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder) viewHolderByPosition).getSurface());
                }
            }
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

                int position = episodeVideoInfo.getPosition();
                MoveToPosition(position);
                updateSelectedPosition(position);
                addTextureView(position);
                mController.onPageSelected(position);
            }

            @Override
            public void onClickRetract() {

            }
        });

        episodeAdapter.initSurfaceListener(new OnSurfaceListener() {
            @Override
            public void onSurfaceCreate(int index, Surface surface) {
                // TODO: 当前版本，PreRender Player仅支持预渲染列表下一个视频的画面；指定预渲染上一个视频的画面，有待后续版本支持。
                // 只对后面的 viewHolder 进行预加载
                if (index > mCurrentIndex) {
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
            AVToast.show(mContext, true, "网络异常，请检查网络是否正确连接");
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
        if(IsInited()){
            return;
        }
        this.mSelectedPosition = 0;
        updateSelectedPosition(mSelectedPosition);
        addTextureView(mSelectedPosition);
        mController.onPageSelected(mSelectedPosition);
        setPreRenderViewHolder(mSelectedPosition + 1);

        SetInited(true);
    }

    @Override
    public void onPageSelected(int position) {
        Log.i("CheckFunc", "onPageSelected " + " position " + position);
        super.onPageSelected(position);//校验，并不做实际上的跳转

        updateSelectedPosition(position);

        mRecyclerView.post(() -> {
            AUIVideoListViewHolder playerViewHolder = getViewHolderByPosition(position);
            if (playerViewHolder == null) {
                return;
            }
            mController.onPageSelected(position, ((AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder) playerViewHolder).getSurface());
            // 只对后面的 viewHolder 进行预加载
            setPreRenderViewHolder(position + 1);
        });
    }

    @Override
    public void onPageScrollTo(int position) {
        super.onPageScrollTo(position);
        // TODO: 当前版本，PreRender Player仅支持预渲染列表下一个视频的画面；指定预渲染上一个视频的画面，有待后续版本支持。
        // 判断当前滑动趋势，对即将要滑到的item及时增加预渲染能力，避免在滑动到位前一直处于黑屏状态，优化滑动体验
        // setPreRenderViewHolder(position);
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
            //Log.i("CheckFunc", "onInfo  " + "progress: " + progress + " duration:  " + duration);
            if (progress >= duration) {
                onCompletion(duration);
            }
        }
    }

    @Override
    public void onCompletion(int position) {
        super.onCompletion(position);
        if (position == -1) {
            return;
        }
        if (mAutoPlayNext && mSelectedPosition + 1 < mAUIVideoListAdapter.getItemCount()) {
            Log.i("CheckFunc", "onCompletion  " + "movetoNextPosition: " + (mSelectedPosition + 1) + " duration:  " + position + "getItemCount: " + mAUIVideoListAdapter.getItemCount());
            mRecyclerView.smoothScrollToPosition(mSelectedPosition + 1);
            onPageSelected(mSelectedPosition + 1);
        }
    }

    @Override
    public void onError(ErrorInfo errorInfo) {
        super.onError(errorInfo);
        AVToast.show(mContext, true, "error: " + errorInfo.getCode() + " -- " + errorInfo.getMsg());
    }

    private void updateSelectedPosition(int index) {
        if (mCurrentIndex == index) {
            return;
        }
        if (mEpisodeData == null || mEpisodeData.list == null) {
            return;
        }
        for (int i = 0; i < mEpisodeData.list.size(); ++i) {
            AUIEpisodeVideoInfo episodeVideoInfo = mEpisodeData.list.get(i);
            if (i == mCurrentIndex) {
                episodeVideoInfo.isSelected = false;
            } else if (i == index) {
                episodeVideoInfo.isSelected = true;
                AUIEpisodeDataEvent event = new AUIEpisodeDataEvent(mCurrentIndex, index, episodeVideoInfo);
                EventBus.getDefault().post(event);
            }
        }
        mCurrentIndex = index;
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

