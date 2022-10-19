package com.aliyun.svideo.crop.bean;

import java.io.Serializable;

/**
 * data:2019/5/22
 */
public class AlivcCropOutputParam implements Serializable {
    /**
     * startActivityForResult模式下，向外输出参数在intent 中的key值
     */
    public static final String RESULT_KEY_OUTPUT_PARAM = "outputParam";

    /**
     * 裁剪输出路径
     */
    private String mOutputPath;
    /**
     * 假裁剪下，选择的视频开始时间
     */
    private long mStartTime;
    /**
     * 假裁剪下，选择的视频时长
     */
    private long mDuration;
    public String getOutputPath() {
        return mOutputPath;
    }

    public void setOutputPath(String mOutputPath) {
        this.mOutputPath = mOutputPath;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long mStartTime) {
        this.mStartTime = mStartTime;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long mDuration) {
        this.mDuration = mDuration;
    }
}
