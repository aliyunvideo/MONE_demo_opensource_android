package com.aliyun.svideo.track.bean;


import com.aliyun.svideosdk.common.struct.project.VideoTrackClip;

/**
 * 主轨道片段
 */
public class MainVideoClipInfo extends BaseClipInfo {
    /**
     * 片段资源路径
     */
    private String mPath;

    /**
     * 开头转场重叠时间
     */
    private long mTransitionOverlapHeadDuration;

    /**
     * 尾部转场重叠时间
     */
    private long mTransitionOverlapTailDuration;

    /**
     * 主轨片段在轨道中的位置
     */
    private PositionFlag mPositionFlag;

    public MainVideoClipInfo(VideoTrackClip item) {
        setClipType(ClipType.MAIN_VIDEO);
        setClipId(item.getClipId());
        setIn((long) (item.getIn() * 1000));
        setOut((long) (item.getOut() * 1000));
        setPath(item.getSource().getPath());
        setDuration(item.getType() == VideoTrackClip.TYPE_IMAGE ? 0 : (long) (item.getDuration() * 1000));
    }

    public MainVideoClipInfo(int clipId, String path, long in, long out, long duration) {
        setClipType(ClipType.MAIN_VIDEO);
        setClipId(clipId);
        setIn(in);
        setOut(out);
        setDuration(duration);
        this.mPath = path;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public long getTransitionOverlapHeadDuration() {
        return mTransitionOverlapHeadDuration;
    }

    public void setTransitionOverlapHeadDuration(long transitionOverlapHeadDuration) {
        this.mTransitionOverlapHeadDuration = transitionOverlapHeadDuration;
    }

    public long getTransitionOverlapTailDuration() {
        return mTransitionOverlapTailDuration;
    }

    public void setTransitionOverlapTailDuration(long transitionOverlapTailDuration) {
        this.mTransitionOverlapTailDuration = transitionOverlapTailDuration;
    }

    public PositionFlag getPositionFlag() {
        return mPositionFlag;
    }

    public void setPositionFlag(PositionFlag positionFlag) {
        this.mPositionFlag = positionFlag;
    }
}
