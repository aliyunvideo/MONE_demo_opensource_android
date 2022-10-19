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
import com.aliyun.svideo.track.inc.CropTrackListener;
import com.aliyun.svideo.track.inc.IMultiTrackListener;
import com.aliyun.svideo.track.inc.OnScrollStateChangeListener;
import com.aliyun.svideo.track.thumbnail.ThumbnailFetcherManger;
import com.aliyun.svideo.track.view.EditScroller;
import com.aliyun.svideo.track.view.HorizontalScrollContainer;
import com.aliyun.svideo.track.view.MainTrackLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 裁剪轨道容器
 */
public class CropTrackContainer extends HorizontalScrollContainer {
    /**
     * 横向滚动容器
     */
    private EditScroller mEditScroller;
    /**
     * 主轨
     */
    private MainTrackLayout mMainTrackLayout;

    private CropTrackListener mListener;
    /**
     * 当前时间线长度
     */
    private long mTimelineDuration = 0;

    private long mTimeIn;
    private long mTimeOut;

    private final IMultiTrackListener mMultiTrackListener = new IMultiTrackListener() {

        @Override
        public void onScrollByX(int scrollDx, boolean invokeChangeListener) {
            CropTrackContainer.this.onScrollByX(scrollDx, false);
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

        }

        @Override
        public void onSubTrackClick(BaseClipInfo clipInfo, boolean isFocus) {

        }

        @Override
        public void onSubClipFocusChanged(BaseClipInfo clipInfo, boolean isFocus) {

        }

        @Override
        public void onVideoTrackClick(int index, boolean isFocus) {

        }

        @Override
        public void onUpdateMainClipIndex(int oldIndex, int newIndex) {

        }

        @Override
        public void onUpdateClipTime(ClipType type, int id, long timeIn, long timeOut, long timelineIn, long timelineOut) {
            if (mListener != null && (timeIn != mTimeIn || timeOut != mTimeOut)) {
                mListener.onUpdateClipTime(timeIn, timeOut);
                if (mTimeIn != timeIn) {
                    updatePlayProgress(0, timeOut - timeIn);
                } else {
                    updatePlayProgress(timeOut - timeIn, timeOut - timeIn);
                }
                mTimeIn = timeIn;
                mTimeOut = timeOut;
            }
        }

        @Override
        public void onClipTouchPosition(long time) {
            if (mListener != null) {
                mListener.onClipTouchPosition(time);
            }
        }
    };

    public CropTrackContainer(@NonNull Context context) {
        this(context, null);
    }

    public CropTrackContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropTrackContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected EditScroller getEditScroller() {
        return mEditScroller;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_crop_track_panel, this, true);
        if (null == mMainTrackLayout) {
            mMainTrackLayout = findViewById(R.id.mMultiTrackLayout);
            mEditScroller = findViewById(R.id.editScroller);
            mMainTrackLayout.setDragEnable(false);
            setOnScrollStateChangeListener(new OnScrollStateChangeListener() {
                @Override
                public void onScrollStateChanged(ScrollState scrollState, int scrollX, int scrollY) {
                    long time = (long) (scrollX * 1.0f / getEditScroller().getMaxScrollX() * mTimelineDuration);
                    if (mListener != null) {
                        ThumbnailFetcherManger.getInstance().onUpdatePlayTime(time);
                        mListener.onScrollChangedTime(time);
                    }
                }
            });
        }
    }

    /**
     * 更新播放时间进度
     *
     * @param playTime 当前播放时间 单位:毫秒
     * @param timelineDuration 时间线长度 单位:毫秒
     */
    public void updatePlayProgress(long playTime, long timelineDuration) {
        if (mTimelineDuration != timelineDuration) {
            mTimelineDuration = timelineDuration;
            mEditScroller.setTimelineDuration(timelineDuration);
        }
        scrollChildToPlayTime(playTime);
    }

    /**
     * 设置主轨数据
     *
     * @param clipInfo 视频信息
     */
    public void setVideoData(MainVideoClipInfo clipInfo) {
        mTimelineDuration = 0;
        if (mMainTrackLayout != null) {
            List<MainVideoClipInfo> list = new ArrayList<>();
            list.add(clipInfo);
            mTimeIn = clipInfo.getIn();
            mTimeOut = clipInfo.getOut();
            mMainTrackLayout.setVideoClip(list);
            mMainTrackLayout.setMultiTrackListener(mMultiTrackListener);
            if (mMainTrackLayout.getMainClipList().size() > 0) {
                mTimelineDuration = mMainTrackLayout.getMainClipList().get(mMainTrackLayout.getMainClipList().size() - 1).getTimelineOut();
            }
            mEditScroller.setTimelineDuration(mTimelineDuration);
            mMainTrackLayout.updateTrackFocus(list.get(0).getClipId(), true);
        }
    }

    public void setListener(CropTrackListener listener) {
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

}
