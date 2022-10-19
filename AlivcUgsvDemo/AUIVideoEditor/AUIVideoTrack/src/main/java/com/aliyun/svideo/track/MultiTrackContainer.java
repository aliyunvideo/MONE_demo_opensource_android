package com.aliyun.svideo.track;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.svideo.track.api.ScrollState;
import com.aliyun.svideo.track.api.TrackConfig;
import com.aliyun.svideo.track.bean.BaseClipInfo;
import com.aliyun.svideo.track.bean.ClipType;
import com.aliyun.svideo.track.bean.MainVideoClipInfo;
import com.aliyun.svideo.track.inc.IMultiTrackListener;
import com.aliyun.svideo.track.inc.MultiTrackListener;
import com.aliyun.svideo.track.inc.OnScrollStateChangeListener;
import com.aliyun.svideo.track.thumbnail.ThumbnailFetcherManger;
import com.aliyun.svideo.track.view.AutoScrollTackLayout;
import com.aliyun.svideo.track.view.EditScroller;
import com.aliyun.svideo.track.view.HorizontalScrollContainer;
import com.aliyun.svideo.track.view.MainTrackLayout;
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip;

import java.util.ArrayList;
import java.util.List;

/**
 * 多级轨道容器
 */
public class MultiTrackContainer extends HorizontalScrollContainer {
    /**
     * 横向滚动容器
     */
    private EditScroller mEditScroller;
    /**
     * 主轨
     */
    private MainTrackLayout mMainTrackLayout;
    /**
     * 垂直滚动容器
     */
    private ScrollView mYScrollView;
    /**
     * 次级轨道
     */
    private AutoScrollTackLayout mSubTackLayout;

    private MultiTrackListener mListener;
    /**
     * 当前时间线长度
     */
    private long mTimelineDuration = 0;

    private float mSpeed = 1.0f;

    private OnClickListener mOutsideOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setSubClipFocus(-1);
        }
    };

    private final IMultiTrackListener mMultiTrackListener = new IMultiTrackListener() {

        @Override
        public void onScrollByX(int scrollDx, boolean invokeChangeListener) {
            MultiTrackContainer.this.onScrollByX(scrollDx, false);
        }

        @Override
        public void onScrollByY(int scrollDy, boolean invokeChangeListener) {
            if (scrollDy != 0) {
                mYScrollView.scrollBy(0, scrollDy);
            }
        }

        @Override
        public int getParentScrollX() {
            return getChildScrollX();
        }

        @Override
        public int getParentScrollY() {
            return mYScrollView.getScrollY();
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
            mOutsideOnClick.onClick(MultiTrackContainer.this);
        }

        @Override
        public void onSubTrackClick(BaseClipInfo clipInfo, boolean isFocus) {
            if (mListener != null) {
                mListener.onClipClick(clipInfo);
                setSubClipFocus(isFocus ? -1 : clipInfo.getClipId());
            }
        }

        @Override
        public void onSubClipFocusChanged(BaseClipInfo clipInfo, boolean isFocus) {
            if (mListener != null) {
                mListener.onFocusChanged(clipInfo, isFocus);
            }
        }

        @Override
        public void onVideoTrackClick(int index, boolean isFocus) {
            mOutsideOnClick.onClick(MultiTrackContainer.this);
        }

        @Override
        public void onUpdateMainClipIndex(int oldIndex, int newIndex) {

        }

        @Override
        public void onUpdateClipTime(ClipType type, int id, long timeIn, long timeOut, long timelineIn, long timelineOut) {
            if (mListener != null) {
                mListener.onUpdateClipTime(mSubTackLayout.getClipInfo(id));
            }
        }

        @Override
        public void onClipTouchPosition(long time) {
            if (mListener != null) {
                mListener.onClipTouchPosition(time);
            }
        }
    };

    public MultiTrackContainer(@NonNull Context context) {
        this(context, null);
    }

    public MultiTrackContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiTrackContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected EditScroller getEditScroller() {
        return mEditScroller;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_multi_track_panel, this, true);
        if (null == mMainTrackLayout) {
            mMainTrackLayout = findViewById(R.id.mMultiTrackLayout);
            mYScrollView = findViewById(R.id.y_scroll_view);
            mSubTackLayout = findViewById(R.id.subTrack);
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
            mSubTackLayout.setMultiTrackListener(mMultiTrackListener);
            mEditScroller.setOnClickListener(mOutsideOnClick);
            findViewById(R.id.sub_layout).setOnClickListener(mOutsideOnClick);
            setOnDragListener(new OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    //获取事件
                    int action = event.getAction();
                    switch (action) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            mSubTackLayout.onDragStart(event.getX(), event.getY());
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            mSubTackLayout.onDragEnd(event.getX(), event.getY());
                            TrackConfig.draggedId = -1;
                            break;
                        case DragEvent.ACTION_DRAG_LOCATION:
                            mSubTackLayout.onDragLocationChange(event.getX(), event.getY());
                            break;
                    }
                    return true;
                }
            });
        }
    }

    /**
     * 更新时间线时长
     *
     * @param timelineDuration 间线时长 单位:毫秒
     */
    public void setTimelineDuration(long timelineDuration) {
        if (mTimelineDuration != timelineDuration) {
            mTimelineDuration = timelineDuration;
            mEditScroller.setTimelineDuration(timelineDuration);
            mSubTackLayout.updateTimelineDuration(timelineDuration);
        }
    }

    /**
     * 更新播放时间进度
     *
     * @param playTime 当前播放时间 单位:毫秒
     */
    public void updatePlayProgress(long playTime) {
        scrollChildToPlayTime(playTime);
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
            mSubTackLayout.updateTimelineDuration(mTimelineDuration);
            mSubTackLayout.setMultiTrackListener(mMultiTrackListener);
            mMainTrackLayout.setDragEnable(false);
        }
    }

    /**
     * 添加片段列表
     *
     * @param list
     */
    public void addSubClipList(List<BaseClipInfo> list) {
        for (BaseClipInfo item : list) {
            mSubTackLayout.addClip(item);
            mSubTackLayout.setSelected(false);
        }
    }

    /**
     * 添加片段
     *
     * @param clipInfo
     */
    public void addSubClip(BaseClipInfo clipInfo) {
        mSubTackLayout.addClip(clipInfo);
        mSubTackLayout.setSelected(false);
    }

    /**
     * 设置片段图标
     *
     * @param clipId
     * @param bitmap
     */
    public void setSubClipMark(int clipId, Bitmap bitmap) {
        mSubTackLayout.setSubClipMark(clipId, bitmap);
    }

    /**
     * 设置片段文案
     *
     * @param clipId
     * @param text
     */
    public void setSubClipText(int clipId, String text) {
        mSubTackLayout.setSubClipText(clipId, text);
    }

    /**
     * 根据片段ID移除片段
     *
     * @param clipId
     */
    public void removeSubClip(int clipId){
        mSubTackLayout.removeClip(clipId);
    }

    /**
     * 设置指定片段获取焦点(传-1时可清空所有焦点)
     *
     * @param clipId
     */
    public void setSubClipFocus(int clipId) {
        mSubTackLayout.setSubClipFocus(clipId, true);
    }

    /**
     * 竖向滚动到指定片段
     *
     * @param clipId
     */
    public void scrollToSubClip(int clipId) {
        BaseClipInfo clipInfo = mSubTackLayout.getClipInfo(clipId);
        if (clipInfo != null) {
            int y = (int) mSubTackLayout.getTranslationYByIndex(clipInfo.getIndex());
            mYScrollView.scrollTo(0, y);
        }
    }

    /**
     * 获取当前焦点片段ID
     *
     * @return
     */
    public int getFocusClipId() {
        return mSubTackLayout.getFocusClipId();
    }

    /**
     * 监听轨道事件
     *
     * @param listener
     */
    public void setTrackListener(MultiTrackListener listener) {
        this.mListener = listener;
    }

}
