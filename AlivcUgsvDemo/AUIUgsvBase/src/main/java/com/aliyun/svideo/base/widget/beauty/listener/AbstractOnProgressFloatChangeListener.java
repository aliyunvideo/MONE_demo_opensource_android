package com.aliyun.svideo.base.widget.beauty.listener;


public abstract class AbstractOnProgressFloatChangeListener implements OnProgresschangeListener {

    @Override
    public void onProgressChange(int progress) {

    }

    public abstract void onProgressFloatChange(int progress, float progressFloat);
}
