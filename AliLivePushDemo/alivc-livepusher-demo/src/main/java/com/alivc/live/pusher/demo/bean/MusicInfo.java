package com.alivc.live.pusher.demo.bean;

public class MusicInfo {
    private String mMusicName;
    private String mPlayTime;
    private String mTotalTime;
    private String mPath;

    public MusicInfo() {

    }

    public MusicInfo(String musicName, String playTime, String totalTime, String path) {
        mPlayTime = playTime;
        mTotalTime = totalTime;
        mMusicName = musicName;
        mPath = path;
    }

    public String getPlayTime() {
        return mPlayTime;
    }

    public void setPlayTime(String playTime) {
        mPlayTime = playTime;
    }

    public String getTotalTime() {
        return mTotalTime;
    }

    public void setTotalTime(String totalTime) {
        mTotalTime = totalTime;
    }

    public String getMusicName() {
        return mMusicName;
    }

    public void setMusicName(String musicName) {
        mMusicName = musicName;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }
}
