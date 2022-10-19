package com.aliyun.svideo.track;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.svideo.track.api.ScrollState;
import com.aliyun.svideo.track.bean.BaseClipInfo;
import com.aliyun.svideo.track.bean.ClipType;
import com.aliyun.svideo.track.bean.MainVideoClipInfo;
import com.aliyun.svideo.track.inc.ClipTrackListener;
import com.aliyun.svideo.track.inc.IMultiTrackListener;
import com.aliyun.svideo.track.inc.OnScrollStateChangeListener;
import com.aliyun.svideo.track.thumbnail.ThumbnailFetcherManger;
import com.aliyun.svideo.track.view.EditScroller;
import com.aliyun.svideo.track.view.HorizontalScrollContainer;
import com.aliyun.svideo.track.view.MainTrackLayout;
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip;

import java.util.ArrayList;
import java.util.List;

/**
 * 剪辑轨道容器
 */
public class MainTrackContainer extends HorizontalScrollContainer {
    private static final String TAG = "TrackScrollContainer";
    /**
     * 横向滚动容器
     */
    private EditScroller mEditScroller;
    /**
     * 主轨
     */
    private MainTrackLayout mMainTrackLayout;

    private ClipTrackListener mListener;
    /**
     * 当前时间线长度
     */
    private long mTimelineDuration = 0;

    private MainVideoClipInfo mCurFocusClip;

    private final IMultiTrackListener mMultiTrackListener = new IMultiTrackListener() {

        @Override
        public void onScrollByX(int scrollDx, boolean invokeChangeListener) {
            MainTrackContainer.this.onScrollByX(scrollDx, false);
        }

        @Override
        public void onScrollByY(int scrollDy, boolean invokeChangeListener) {

        }

        @Override
        public int getParentScrollX() {
            return getChildScrollX();
        }

        @Override
        public int getParentScrollY() {
            return 0;
        }

        @Override
        public int getParentMaxScrollX() {
            return getChildMaxScrollX();
        }

        @Override
        public void onParentTranslationX(float translationX) {
            if (mEditScroller.getTranslationX() != translationX) {
                mEditScroller.setTranslationX(translationX);
            }
        }

        @Override
        public void onTransitionClick(int index) {
            if (mListener != null) {
                mListener.onTransitionClick(index);
            }
        }

        @Override
        public void onSubTrackClick(BaseClipInfo clipInfo, boolean isFocus) {

        }

        @Override
        public void onSubClipFocusChanged(BaseClipInfo clipInfo, boolean isFocus) {

        }

        @Override
        public void onVideoTrackClick(int id, boolean isFocus) {

        }

        @Override
        public void onUpdateMainClipIndex(int oldIndex, int newIndex) {

        }

        @Override
        public void onUpdateClipTime(ClipType type, int id, long timeIn, long timeOut, long timelineIn, long timelineOut) {

        }

        @Override
        public void onClipTouchPosition(long time) {

        }
    };

    public MainTrackContainer(@NonNull Context context) {
        this(context, null);
    }

    public MainTrackContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainTrackContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected EditScroller getEditScroller() {
        return mEditScroller;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_main_track_panel, this, true);
        if (null == mMainTrackLayout) {
            mMainTrackLayout = findViewById(R.id.mMultiTrackLayout);
            mEditScroller = findViewById(R.id.editScroller);
            mMainTrackLayout.setDragEnable(false);
            mMainTrackLayout.setDragHorizontallyEnable(false);
            setOnScrollStateChangeListener(new OnScrollStateChangeListener() {
                @Override
                public void onScrollStateChanged(ScrollState scrollState, int scrollX, int scrollY) {
                    long time = (long) (scrollX * 1.0f / getEditScroller().getMaxScrollX() * getTimelineDuration());
                    if (mListener != null) {
                        ThumbnailFetcherManger.getInstance().onUpdatePlayTime(time);
                        updateFocusClip(time);
                        mListener.onScrollChangedTime(time);
                    }
                }
            });
        }
    }

    /**
     * 更新播放时间进度
     *
     * @param playtime 当前播放时间 单位:毫秒
     */
    public void updatePlayProgress(long playtime) {
        scrollChildToPlayTime(playtime);
        updateFocusClip(playtime);
    }

    /**
     * 设置主轨数据
     *
     * @param list 视频列表信息
     */
    public void setVideoData(List<VideoTrackClip> list) {
        List<MainVideoClipInfo> data = new ArrayList<>();
        for (VideoTrackClip item : list) {
            long overlapDuration = 0;
            if (item.getTransition() != null) {
                overlapDuration = item.getTransition().getOverlapDuration() / 1000;
                int preIndex = data.size() - 1;
                if (preIndex > -1) {
                    data.get(preIndex).setTransitionOverlapTailDuration(overlapDuration);
                }
            }
            MainVideoClipInfo clipInfo = new MainVideoClipInfo(item);
            clipInfo.setTransitionOverlapHeadDuration(overlapDuration);
            data.add(clipInfo);
        }
        mTimelineDuration = 0;
        if (mMainTrackLayout != null) {
            mMainTrackLayout.setVideoClip(data);
            mMainTrackLayout.setMultiTrackListener(mMultiTrackListener);
            if (mMainTrackLayout.getMainClipList().size() > 0) {
                mTimelineDuration = mMainTrackLayout.getMainClipList().get(mMainTrackLayout.getMainClipList().size() - 1).getTimelineOut();
            }
            mEditScroller.setTimelineDuration(mTimelineDuration);
            mMainTrackLayout.setDragEnable(false);
        }
    }

    public void setListener(ClipTrackListener listener) {
        this.mListener = listener;
    }

    /**
     * 获取当前时间线时长
     *
     * @return 时间线时长
     */
    public long getTimelineDuration() {
        return mTimelineDuration;
    }

    /**
     * 更新转场状态
     * @param index 转场片段
     * @param duration 转场时长
     * @param apply 载入或者移除
     */
    public void updateTransition(int index, long duration, boolean apply) {
        mMainTrackLayout.updateTransition(index, duration, apply);
        if (mMainTrackLayout.getMainClipList().size() > 0) {
            mTimelineDuration = mMainTrackLayout.getMainClipList().get(mMainTrackLayout.getMainClipList().size() - 1).getTimelineOut();
        }
        mEditScroller.setTimelineDuration(mTimelineDuration);
    }

    private void updateFocusClip(long time) {
        List<MainVideoClipInfo> list = mMainTrackLayout.getMainClipList();
        MainVideoClipInfo focusClip = null;
        for (MainVideoClipInfo clipInfo : list) {
            long timelineIn = clipInfo.getTimelineIn() - clipInfo.getTransitionOverlapHeadDuration() / 2;
            long timelineOut = clipInfo.getTimelineOut() - clipInfo.getTransitionOverlapTailDuration() / 2;
            if (timelineIn <= time && time <= timelineOut) {
                focusClip = clipInfo;
                break;
            }
        }
        if (mCurFocusClip != focusClip) {
            if (this.mListener != null) {
                if (mCurFocusClip != null) {
                    this.mListener.onFocusChanged(mCurFocusClip, false);
                    mMainTrackLayout.updateTrackFocus(mCurFocusClip.getClipId(), false);
                }
                if (focusClip != null) {
                    this.mListener.onFocusChanged(focusClip, true);
                }
            }
            mCurFocusClip = focusClip;
            if (mCurFocusClip != null) {
                mMainTrackLayout.updateTrackFocus(mCurFocusClip.getClipId(), true);
            }
        }
    }

}
