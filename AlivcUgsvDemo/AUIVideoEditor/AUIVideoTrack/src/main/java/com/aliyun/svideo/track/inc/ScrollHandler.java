package com.aliyun.svideo.track.inc;

public interface ScrollHandler {

    void onScrollByX(int scrollDx, boolean invokeChangeListener);

    void onScrollByY(int scrollDy, boolean invokeChangeListener);

}
