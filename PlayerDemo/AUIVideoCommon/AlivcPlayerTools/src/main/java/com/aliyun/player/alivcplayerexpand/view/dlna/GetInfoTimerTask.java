package com.aliyun.player.alivcplayerexpand.view.dlna;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 获取播放进度的定时任务
 */
public class GetInfoTimerTask {

    private Timer mTimer;
    private TimerTask mTimerTask;
    private long mTime;

    /**
     * @param time 间隔时长
     * @param task TimerTask
     */
    public GetInfoTimerTask(long time, TimerTask task) {
        this.mTimerTask = task;
        this.mTime = time;
        if (mTimer == null) {
            mTimer = new Timer();
        }
    }

    public GetInfoTimerTask(){
        if (mTimer == null) {
            mTimer = new Timer();
        }
    }

    public void setTime(long time){
        this.mTime = time;
    }

    public void setTimerTask(TimerTask task){
        this.mTimerTask = task;
    }

    public void start() {
        if(mTimer != null){
            mTimer.schedule(mTimerTask, 0, mTime);//每隔time时间段就执行一次
        }
    }

    public void cancel() {
        if (mTimer != null) {
            mTimer.cancel();
            if (mTimerTask != null) {
                mTimerTask.cancel();
            }
        }
    }

}
