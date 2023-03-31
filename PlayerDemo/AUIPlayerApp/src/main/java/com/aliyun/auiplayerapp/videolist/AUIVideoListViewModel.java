package com.aliyun.auiplayerapp.videolist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.aliyun.auiplayerapp.videolist.repository.local.AUIVideoListLocalDataSource;

import java.util.ArrayList;
import java.util.List;


public class AUIVideoListViewModel extends ViewModel {

    private final AUIVideoListLocalDataSource mVideoListDataSource;

    /**
     * Video List Data
     */
    private final MutableLiveData<List<VideoInfo>> _mVideoListLiveData = new MutableLiveData<>();
    LiveData<List<VideoInfo>> mVideoListLiveData = _mVideoListLiveData;

    /**
     * is Load More
     */
    private final MutableLiveData<Boolean> _mIsLoadMore = new MutableLiveData<>();
    LiveData<Boolean> mIsLoadMore = _mIsLoadMore;


    public AUIVideoListViewModel(AUIVideoListLocalDataSource auiVideoListLocalDataSource) {
        this.mVideoListDataSource = auiVideoListLocalDataSource;
        loadSource();
    }


    public void loadSource() {
        _mIsLoadMore.postValue(false);
        List<VideoInfo> videoList = mVideoListDataSource.getVideoList();
        _mVideoListLiveData.setValue(videoList);

    }

    public void addSource() {
        Boolean isLoadMore = _mIsLoadMore.getValue();
        if (!isLoadMore) {
            _mIsLoadMore.setValue(true);
            List<VideoInfo> videoList =  new ArrayList<>();
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setId(Integer.MIN_VALUE);
            videoInfo.setUrl("https://vod.h5video.vip/f0dc61b0b67f71ed949f6732a78f0102/917faa9d22d4479082f95a20349d4fae-5b8c4873c090c077aed6c3f95c0f9102-sd-nbv1.mp4");
            videoInfo.setTitle("阳光、海浪、沙滩、仙人掌……");
            videoInfo.setCoverUrl("https://alivc-demo-vod.aliyuncs.com/4c6f9fcfddd24e00a225746479a6419a/snapshots/normal/45B357F3-17FDFE38B6D-1103-1445-334-2638600001.jpg");
            videoList.add(videoInfo);

            VideoInfo videoInfo2 = new VideoInfo();
            videoInfo2.setId(Integer.MIN_VALUE + 1);
            videoInfo2.setUrl("https://vod.h5video.vip/e6743270b67f71ed82b30764a3fd0102/99328e456e7941de8d128dc3d9a842bf-d337bffd94bd61d33248748ad3fbe875-sd-nbv1.mp4");
            videoInfo2.setTitle("荷兰阿姆斯特丹，球迷观看世界杯足球赛");
            videoInfo2.setCoverUrl("https://alivc-demo-vod.aliyuncs.com/58002116f9454c79b71134c35187084f/snapshots/normal/4B4AC225-17FDFE2A9BD-1103-1445-334-2638600001.jpg");
            videoList.add(videoInfo2);
            _mVideoListLiveData.setValue(videoList);
        }
    }
}
