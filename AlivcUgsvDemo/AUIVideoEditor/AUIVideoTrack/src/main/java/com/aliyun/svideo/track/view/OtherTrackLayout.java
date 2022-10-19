package com.aliyun.svideo.track.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.svideo.track.api.TrackConfig;
import com.aliyun.svideo.track.bean.BaseClipInfo;
import com.aliyun.svideo.track.bean.CaptionClipInfo;
import com.aliyun.svideo.track.bean.MultiClipInfo;
import com.aliyun.svideo.track.inc.IMultiTrackListener;
import com.aliyun.svideo.track.inc.ItemTrackCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 非主轨轨道容器
 */
public class OtherTrackLayout extends FrameLayout {
    /**
     * 音频轨道展开容器
     */
    protected FrameLayout mExpendContentLayout;
    /**
     * 轨道片段列表
     */
    private List<BaseClipLayout> mClipLayoutList = new ArrayList<>();
    /**
     * 当前时间线时长
     */
    protected long mTimelineDuration;
    /**
     * 当前时间刻度缩放值
     */
    protected float mTimelineScale = 1.0f;
    private ItemTrackCallback mItemTrackCallback;
    protected IMultiTrackListener mMultiTrackListener;
    private int trackLayoutId = 2001;

    public OtherTrackLayout(@NonNull Context context) {
        this(context, null);
    }

    public OtherTrackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OtherTrackLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.round(mTimelineDuration * TrackConfig.getPxUnit(mTimelineScale)) + TrackConfig.MOVE_BTN_WIDTH * 2;
        setMeasuredDimension(width, getMeasuredHeight());
    }

    private void initView(Context context, AttributeSet attrs) {
        initExpendContentLayout(context);
        setClipChildren(false);
        setWillNotDraw(false);
        setListener();
    }

    protected BaseClipLayout getTrackLayoutById(int id) {
        for (BaseClipLayout layout : mClipLayoutList) {
            if (layout.getTrackClipInfo().getClipId() == id) {
                return layout;
            }
        }
        return null;
    }

    private void setListener() {
        mItemTrackCallback = new ItemTrackCallback() {

            @Override
            public void onClipTouchMove(int id, int dx, int orientation, float clipLength, int scrollByX) {
                if (mMultiTrackListener != null) {
                    mMultiTrackListener.onScrollByX(scrollByX, false);
                    BaseClipInfo clipInfo = getClipInfo(id);
                    long contentDuration = (long) (clipLength / TrackConfig.getPxUnit(mTimelineScale));
                    if (orientation == 0) {
                        mMultiTrackListener.onClipTouchPosition(clipInfo.getTimelineOut() - contentDuration);
                    } else {
                        mMultiTrackListener.onClipTouchPosition(clipInfo.getTimelineIn() + contentDuration);
                    }
                }
            }

            @Override
            public void onTransitionClick(int index) {

            }

            @Override
            public void onTrackTouch(int id, boolean isFocus) {
                setSubClipFocus(id, isFocus);
                if (mMultiTrackListener != null) {
                    BaseClipLayout baseTrackLayout = getTrackLayoutById(id);
                    if (baseTrackLayout != null) {
                        mMultiTrackListener.onSubTrackClick(baseTrackLayout.getTrackClipInfo(), isFocus);
                    }
                }
            }

            @Override
            public void onClipTouchUp(int index, int orientation, long clipInTime, long clipOutTime) {

            }

            @Override
            public void onClipTouchDown(int index, int orientation, float clipLength, int contentLeftPosition) {

            }

            @Override
            public void onUpdateClipTime(int index, int orientation, long timeIn, long timeOut, long timelineIn, long timelineOut) {
                BaseClipLayout clipLayout = getTrackLayoutById(index);
                if (mMultiTrackListener != null && clipLayout != null && clipLayout.getTrackClipInfo() instanceof MultiClipInfo) {
                    mMultiTrackListener.onUpdateClipTime(clipLayout.getTrackClipInfo().getClipType(), index, timeIn, timeOut, timelineIn, timelineOut);
                    updateTimelineLimit(clipLayout.getTrackClipInfo().getIndex());
                }
            }
        };
    }

    private void initExpendContentLayout(Context context) {
        mExpendContentLayout = new FrameLayout(context);
        mExpendContentLayout.setVisibility(VISIBLE);
        mExpendContentLayout.setMinimumHeight((TrackConfig.SUB_CLIP_HEIGHT + TrackConfig.TRACK_LAYOUT_MARGIN_START) * 3 + TrackConfig.TRACK_LAYOUT_MARGIN_START);
        addView(mExpendContentLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setTimelineScale(float timelineScale) {
        this.mTimelineScale = timelineScale;
        for (BaseClipLayout baseTrackLayout : mClipLayoutList) {
            baseTrackLayout.setTimelineScale(mTimelineScale);
        }
    }

    public void addClip(BaseClipInfo clipInfo) {
        if (clipInfo == null || clipInfo.getClipId() == -1) {
            return;
        }
        BaseClipLayout baseTrackLayout = null;
        if (clipInfo instanceof CaptionClipInfo) {
            baseTrackLayout = getTrackLayoutById(clipInfo.getClipId());
        }

        if (baseTrackLayout != null) {
            int index = baseTrackLayout.getTrackClipInfo().getIndex();
            clipInfo.setIndex(index);
            baseTrackLayout.setData(clipInfo);
            baseTrackLayout.setSelected(true);
        } else {
            addSubView(clipInfo);
        }
    }

    public void setSubClipMark(int clipId, Bitmap bitmap) {
        BaseClipLayout clipLayout = getTrackLayoutById(clipId);
        if (clipLayout != null) {
            clipLayout.setMarkImage(bitmap);
        }
    }

    public void setSubClipText(int clipId, String text) {
        BaseClipLayout clipLayout = getTrackLayoutById(clipId);
        if (clipLayout != null) {
            clipLayout.setClipText(text);
        }
    }

    private void addSubView(BaseClipInfo clipInfo) {
        trackLayoutId++;
        BaseClipLayout subClipLayout = new SubClipLayout(getContext());
        subClipLayout.setItemTrackCallback(mItemTrackCallback);
        subClipLayout.setId(trackLayoutId);
        subClipLayout.setData(clipInfo);
        subClipLayout.setSelected(false);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = Math.round(clipInfo.getTimelineIn() * TrackConfig.getPxUnit(mTimelineScale));
        int index = 0;
        for (BaseClipLayout item : mClipLayoutList) {
            if (item.getTrackClipInfo().getIndex() > index) {
                break;
            }
            index++;
        }
        subClipLayout.setTimelineScale(mTimelineScale);
        mClipLayoutList.add(subClipLayout);
        subClipLayout.getTrackClipInfo().setIndex(index);
        layoutParams.topMargin = clipInfo.getIndex() * (TrackConfig.SUB_CLIP_HEIGHT + TrackConfig.TRACK_LAYOUT_MARGIN_START) + TrackConfig.TRACK_LAYOUT_MARGIN_START;
        mExpendContentLayout.addView(subClipLayout, layoutParams);
        onClipSort();
        resizeHeight();
    }

    /**
     * 重新计算高度
     */
    protected void resizeHeight() {
        if (mClipLayoutList.size() > 0) {
            LayoutParams params = (LayoutParams) mExpendContentLayout.getLayoutParams();
            params.height = ((mClipLayoutList.get(mClipLayoutList.size() - 1)).getTrackClipInfo().getIndex() + 2) *
                    (TrackConfig.SUB_CLIP_HEIGHT + TrackConfig.TRACK_LAYOUT_MARGIN_START);
            mExpendContentLayout.setLayoutParams(params);
        } else {
            LayoutParams params = (LayoutParams) mExpendContentLayout.getLayoutParams();
            params.height = TrackConfig.SUB_CLIP_HEIGHT + TrackConfig.TRACK_LAYOUT_MARGIN_START;
            mExpendContentLayout.setLayoutParams(params);
        }
    }

    /**
     * 重新排序
     */
    protected void onClipSort() {
        Collections.sort(mClipLayoutList, new Comparator<BaseClipLayout>() {
            @Override
            public int compare(BaseClipLayout o1, BaseClipLayout o2) {
                int d = o1.getTrackClipInfo().getIndex() - o2.getTrackClipInfo().getIndex();
                if (d == 0) {
                    return o1.getTrackClipInfo().getTimelineIn() > o2.getTrackClipInfo().getTimelineIn() ? 1 : -1;
                }
                return d;
            }
        });
    }

    protected List<BaseClipLayout> getLayoutListByIndex(int index) {
        List<BaseClipLayout> list = new ArrayList<>();
        for (BaseClipLayout item : mClipLayoutList) {
            if (item.getTrackClipInfo().getIndex() == index) {
                list.add(item);
            }
        }
        return list;
    }

    /**
     * 更新可拖动边界
     *
     * @param index
     */
    protected void updateTimelineLimit(int index) {
        List<BaseClipLayout> list = getLayoutListByIndex(index);
        List<MultiClipInfo> multiClipList = new ArrayList<>();
        if (list.size() > 0) {
            for (BaseClipLayout item : list) {
                if (item.getTrackClipInfo() instanceof MultiClipInfo) {
                    multiClipList.add((MultiClipInfo) item.getTrackClipInfo());
                }
            }

            Collections.sort(multiClipList, new Comparator<MultiClipInfo>() {
                @Override
                public int compare(MultiClipInfo o1, MultiClipInfo o2) {
                    return (int) (o1.getTimelineIn() - o2.getTimelineIn());
                }
            });
            for (int i = 0; i < multiClipList.size(); i++) {
                MultiClipInfo item = multiClipList.get(i);
                long minTimelineIn = 0;
                long maxTimelineOut = 0;
                if (i != 0) {
                    minTimelineIn = multiClipList.get(i - 1).getTimelineOut();
                }
                if (i != multiClipList.size() - 1) {
                    maxTimelineOut = multiClipList.get(i + 1).getTimelineIn();
                }
                item.setMinTimelineIn(minTimelineIn);
                item.setMaxTimelineOut(maxTimelineOut);
            }
        }
    }

    public void removeClip(int clipId) {
        BaseClipLayout trackLayout = getTrackLayoutById(clipId);
        mClipLayoutList.remove(trackLayout);
        mExpendContentLayout.removeView(trackLayout);
        resizeHeight();
    }

    private boolean hasMaterialData() {
        return mClipLayoutList.size() > 0;
    }

    public void updateTimelineDuration(long duration) {
        if (mTimelineDuration == duration) {
            return;
        }
        mTimelineDuration = duration;
    }

    public long getTimelineDuration() {
        return mTimelineDuration;
    }

    public void setMultiTrackListener(IMultiTrackListener multiTrackListener) {
        mMultiTrackListener = multiTrackListener;
    }

    public int getFocusClipId() {
        int focusId = TrackConfig.ERROR;
        if (mExpendContentLayout != null &&
                mExpendContentLayout.getVisibility() == VISIBLE &&
                hasMaterialData()) {
            for (BaseClipLayout value : mClipLayoutList) {
                if (value.isSelected()) {
                    focusId = value.getTrackClipInfo().getClipId();
                    break;
                }
            }
        }
        return focusId;
    }

    @Nullable
    public BaseClipInfo getClipInfo(int clipId) {
        for (BaseClipLayout baseClipLayout : mClipLayoutList) {
            if (baseClipLayout != null) {
                if (baseClipLayout.getTrackClipInfo().getClipId() == clipId) {
                    return baseClipLayout.getTrackClipInfo();
                }
            }
        }
        return null;
    }

    public void setSubClipFocus(int clipId, boolean isFocus) {
        BaseClipLayout oldFocus = getTrackLayoutById(getFocusClipId());
        BaseClipLayout newSet = getTrackLayoutById(clipId);
        if (isFocus) {
            if (oldFocus != null && oldFocus == newSet) {
                return;
            }
            if (oldFocus != null) {
                oldFocus.setClipFocus(false);
                if (mMultiTrackListener != null) {
                    mMultiTrackListener.onSubClipFocusChanged(oldFocus.getTrackClipInfo(), false);
                }
            }
            if (newSet != null) {
                newSet.setClipFocus(true);
                if (mMultiTrackListener != null) {
                    mMultiTrackListener.onSubClipFocusChanged(newSet.getTrackClipInfo(), true);
                }
            }
        } else if (oldFocus != null) {
            oldFocus.setClipFocus(false);
            if (mMultiTrackListener != null) {
                mMultiTrackListener.onSubClipFocusChanged(oldFocus.getTrackClipInfo(), false);
            }
        }
    }
}
