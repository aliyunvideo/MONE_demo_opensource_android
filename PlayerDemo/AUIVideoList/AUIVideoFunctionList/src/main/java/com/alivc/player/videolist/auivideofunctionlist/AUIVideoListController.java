package com.alivc.player.videolist.auivideofunctionlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alivc.player.videolist.auivideofunctionlist.domain.GestureGuidanceUseCase;
import com.alivc.player.videolist.auivideofunctionlist.player.AliPlayerPool;
import com.alivc.player.videolist.auivideofunctionlist.player.AliPlayerPreload;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;

import java.util.List;

public class AUIVideoListController {

    private static final int BUFFER_POSITION = 5000;
    private final GestureGuidanceUseCase mGestureGuidanceUseCase;
    private boolean mIsPreloading = false;
    private final AliPlayerPreload mAliPlayerPreload;
    private int mCurrentPosition;
    //Play the 0 th video for the first time
    private boolean isOnCreate;

    /**
     * Gesture Guidance show/hide
     */
    private final MutableLiveData<Boolean> _mGestureGuidanceLiveData = new MutableLiveData<>();
    LiveData<Boolean> mGestureGuidanceLiveData = _mGestureGuidanceLiveData;
    public AUIVideoListController(GestureGuidanceUseCase gestureGuidanceUseCase) {
        isOnCreate = true;
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

    public void changeSource(int position) {
        this.mCurrentPosition = position;
        mIsPreloading = false;
    }

    public void onPrepared(int position, AUIVideoListViewHolder viewHolder) {
        if (position == 0 && isOnCreate) {
            viewHolder.getAliPlayer().start();
            isOnCreate = false;
        }
    }

    public void openLoopPlay(boolean openLoopPlay) {
        AliPlayerPool.openLoopPlay(openLoopPlay);
    }

    /**
     * start MediaLoader after Player Buffer position >= {@link AUIVideoListController#BUFFER_POSITION}
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
        mAliPlayerPreload.cancel(position);
        viewHolder.getAliPlayer().start();
    }

    /**
     * Pause Pre Video when Slide to half the screen
     */
    public void onPageHideHalf(int position, AUIVideoListViewHolder viewHolder) {
        viewHolder.getAliPlayer().pause();
    }

    public void onPageRelease(int position, AUIVideoListViewHolder viewHolder) {
        viewHolder.getAliPlayer().stop();
    }

    public void seekTo(long progress,AUIVideoListViewHolder viewHolder) {
        viewHolder.getAliPlayer().seekTo(progress);
    }

    public void destroy() {
        isOnCreate = false;
        AliPlayerPool.release();
    }

    //TODO
    public void start(int position) {

    }

    //TODO
    public void changePlayState() {

    }

    /*

    private static final AUIVideoListModel M_VIDEO_LIST_MODEL = new AUIVideoListModel();
    private AliPlayerManager mAliPlayerManager;
    private AliPlayerPreload mAliPlayerPreload;
    private boolean mIsPlay = true;
    private final Context mContext;
    private int mOldPosition = 0;
    private boolean mIsFirstPlay = true;

    private void initDir(){

        M_VIDEO_LIST_MODEL.loadData(mContext.getApplicationContext());
    }

    private void initPlayer(){
        mAliPlayerPreload = AliPlayerPreload.getInstance();
        mAliPlayerManager = new AliPlayerManager(mContext);

        mAliPlayerPreload.setUrls(M_VIDEO_LIST_MODEL.getData());
        mAliPlayerManager.setUrls(M_VIDEO_LIST_MODEL.getData());

        mAliPlayerManager.setOnPlayerListener(new AliPlayerManager.OnPlayerListener() {

            @Override
            public void onInfo(InfoBean infoBean) {
                if(mContext instanceof AUIVideoListActivity){
                    if(infoBean.getCode() == InfoCode.CurrentPosition){
                        ((AUIVideoListActivity)mContext).updateCurrentPosition(infoBean.getExtraValue());
                    }
                }
            }
            @Override
            public void onRenderingStart(long duration) {
                if(mContext instanceof AUIVideoListActivity){
                    ((AUIVideoListActivity)mContext).onVideoFrameShow(duration);
                    ((AUIVideoListActivity)mContext).hideCoverView();
                }
                mIsPlay = true;
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                if(mContext instanceof AUIVideoListActivity){
                    ((AUIVideoListActivity)mContext).showError(errorInfo);
                }
            }

            @Override
            public void onPlayerStateChanged(int newState) {
                if(mContext instanceof AUIVideoListActivity){
                    ((AUIVideoListActivity)mContext).showPlayIcon(newState == IPlayer.paused);
                }
            }
        });
    }


    public LinkedList<ListVideoBean> getData(){
        return M_VIDEO_LIST_MODEL.getData();
    }

    public void setRenderView(Surface surface) {
        mAliPlayerManager.setSurface(surface);
    }

    public void surfacChanged() {
        mAliPlayerManager.surfaceChanged();
    }

    public void start(int position) {
        if(position == mOldPosition && !mIsFirstPlay){
            return ;
        }
        int flag = position - mOldPosition;
        if (Math.abs(flag) == 1) {
            if (flag < 0) {
                mAliPlayerManager.moveToPre();
            } else {
                mAliPlayerManager.moveToNext();
            }
        } else {
            mAliPlayerManager.moveTo(position);
            mIsFirstPlay = false;
        }
        mAliPlayerPreload.moveTo(position);
        mOldPosition = position;
        if(position == M_VIDEO_LIST_MODEL.getData().size() - 1){
            ((AUIVideoListActivity)mContext).loadMore();
        }
    }

    public void destroy() {
        mAliPlayerManager.release();
    }

    public void startPlay(int position) {
        LinkedList<ListVideoBean> data = M_VIDEO_LIST_MODEL.getData();
        ListVideoBean listVideoBean = data.get(position);
        mAliPlayerManager.startPlay(listVideoBean.getUrl());
    }

    public void seekTo(long position){
        mAliPlayerManager.seekTo(position);
    }

    public void seekToAccurate(long position){
        mAliPlayerManager.seekToAccurate(position);
    }

    public void changePlayState(){
        if(mIsPlay){
            mAliPlayerManager.pause();
        }else{
            mAliPlayerManager.start();
        }
        mIsPlay = !mIsPlay;
    }
     */
}
