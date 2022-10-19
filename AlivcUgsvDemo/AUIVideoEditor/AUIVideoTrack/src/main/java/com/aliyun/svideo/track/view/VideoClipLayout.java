package com.aliyun.svideo.track.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.aliyun.svideo.track.R;
import com.aliyun.svideo.track.api.TrackConfig;
import com.aliyun.svideo.track.bean.BaseClipInfo;
import com.aliyun.svideo.track.bean.MainVideoClipInfo;
import com.aliyun.svideo.track.bean.PositionFlag;
import com.aliyun.svideo.track.util.Util;


public class VideoClipLayout extends BaseClipLayout {
    /**
     * 缩略图控件
     */
    private ClipFrameView mItemFrameView;
    /**
     * 转场按钮
     */
    private ImageView mTransitionView;
    /**
     * 下个片段转场按钮
     */
    private ImageView mNextTransitionView;
    /**
     * 是否是拖动排序状态
     */
    private boolean mIsDragSort;

    /**
     * 首个片段是否在移动
     */
    private boolean mFirstMove;

    public VideoClipLayout(Context context) {
        this(context, null);
    }

    public VideoClipLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoClipLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        int contentWidth = 0;
        if(getTrackClipInfo() != null){
            contentWidth = Math.round(getTrackClipInfo().getClipDuration() * TrackConfig.getPxUnit(mTimelineScale));
        }
        ViewGroup.LayoutParams contentLayoutParams = mContentView.getLayoutParams();
        if (mIsDragSort) {
            contentLayoutParams.width = TrackConfig.FRAME_WIDTH + TrackConfig.FRAME_MID_MARGIN;
            layoutParams.leftMargin = -2 * TrackConfig.MOVE_BTN_WIDTH;
            layoutParams.width = 2 * TrackConfig.MOVE_BTN_WIDTH + TrackConfig.FRAME_WIDTH + TrackConfig.FRAME_MID_MARGIN;
            setLayoutParams(layoutParams);
        } else  {
            contentLayoutParams.width = contentWidth;
            int transitionOverlapWidth = 0;
            int transitionLayoutLeftMargin = 0;

            int nextTransitionOverlapWidth = 0;
            int nextTransitionLayoutLeftMargin = 0;

            MainVideoClipInfo trackClipInfo = (MainVideoClipInfo)getTrackClipInfo();
            //计算转场按钮偏移及转场重叠间距
            if (trackClipInfo != null) {
                if (trackClipInfo.getTransitionOverlapHeadDuration() > 0) {
                    transitionOverlapWidth = Math.round(TrackConfig.getPxUnit(mTimelineScale) * trackClipInfo.getTransitionOverlapHeadDuration());
                    transitionLayoutLeftMargin = transitionOverlapWidth / 2 - Util.dp2px(18) / 2 - TrackConfig.FRAME_MID_MARGIN / 2;
                } else {
                    transitionLayoutLeftMargin = -Util.dp2px(18) / 2 - TrackConfig.FRAME_MID_MARGIN / 2;
                }

                if (trackClipInfo.getTransitionOverlapTailDuration() > 0) {
                    nextTransitionOverlapWidth = Math.round(TrackConfig.getPxUnit(mTimelineScale) * trackClipInfo.getTransitionOverlapTailDuration());
                    nextTransitionLayoutLeftMargin = Util.dp2px(18) / 2 - TrackConfig.FRAME_MID_MARGIN / 2 - nextTransitionOverlapWidth / 2;
                } else {
                    nextTransitionLayoutLeftMargin = Util.dp2px(18) / 2 - TrackConfig.FRAME_MID_MARGIN / 2;
                }

            } else {
                transitionLayoutLeftMargin = -Util.dp2px(18) / 2 - TrackConfig.FRAME_MID_MARGIN / 2;
                nextTransitionLayoutLeftMargin = Util.dp2px(18) / 2 - TrackConfig.FRAME_MID_MARGIN / 2;
            }
            //设置转场重叠间距
            if (trackClipInfo != null && trackClipInfo.getIndex() != 0) {
                if (mFocusMoveView != null) {
                    layoutParams.leftMargin = Math.round(getTrackClipInfo().getTimelineIn() * TrackConfig.getPxUnit(mTimelineScale));
                    layoutParams.width = 2 * TrackConfig.MOVE_BTN_WIDTH + contentWidth;
                    layoutParams.removeRule(RelativeLayout.RIGHT_OF);
                } else {
                    //首个片段在拖动时，第二个片段固定位置
                    if (isFirstMove() && trackClipInfo.getIndex() == 1) {
                        layoutParams.leftMargin = Math.round(getTrackClipInfo().getTimelineIn() * TrackConfig.getPxUnit(mTimelineScale));
                        layoutParams.removeRule(RelativeLayout.RIGHT_OF);
                    } else {
                        layoutParams.leftMargin = -2 * TrackConfig.MOVE_BTN_WIDTH - transitionOverlapWidth;
                    }
                    layoutParams.width = 2 * TrackConfig.MOVE_BTN_WIDTH + contentWidth;
                }
                setLayoutParams(layoutParams);
            }
            mTransitionView.setTranslationX(transitionLayoutLeftMargin);
            mNextTransitionView.setTranslationX(nextTransitionLayoutLeftMargin);
        }
        mContentView.setLayoutParams(contentLayoutParams);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        mItemFrameView.invalidate();
    }

    @Override
    public void inflateContentView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_video_clip, this).findViewById(R.id.container);
        mItemFrameView = findViewById(R.id.itemFrameView);
        mTransitionView = findViewById(R.id.ivTransition);
        mNextTransitionView = findViewById(R.id.ivNextTransition);
    }

    @Override
    public View findContentView() {
        return findViewById(R.id.framesLayout);

    }

    @Override
    public void onMoveFinish() {

    }

    @Override
    public void setTimelineScale(float timelineScale) {
        super.setTimelineScale(timelineScale);
        mItemFrameView.setTimelineScale(timelineScale);
    }

    public void setData(BaseClipInfo baseClipInfo) {
        super.setData(baseClipInfo);
        mItemFrameView.updateData((MainVideoClipInfo) baseClipInfo);
        setTransitionIcon(((MainVideoClipInfo) getTrackClipInfo()).getTransitionOverlapHeadDuration() > 0);
        setNextTransitionIcon(((MainVideoClipInfo) getTrackClipInfo()).getTransitionOverlapTailDuration() > 0);
    }

    @Override
    public void setSelected(boolean selected) {
        PositionFlag flag = ((MainVideoClipInfo) getTrackClipInfo()).getPositionFlag();
        if (this.mDragHorizontallyEnable) {
            //转场按钮聚焦时不显示
            mTransitionView.setVisibility(selected || getTrackClipInfo().getIndex() == 0 ? View.GONE : View.VISIBLE);
            mNextTransitionView.setVisibility(selected || flag == PositionFlag.FOOTER || flag == PositionFlag.ONLY_ONE ? View.GONE : View.VISIBLE);
            if (!selected) {
                mTransitionView.bringToFront();
            }
        } else {
            findViewById(R.id.trackTopLine).setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
            findViewById(R.id.trackBottomLine).setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
            findViewById(R.id.trackLeftLine).setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
            findViewById(R.id.trackRightLine).setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
            if (selected) {
                findViewById(R.id.trackTopLine).animate().alpha(1.0f).setDuration(100L).start();
                findViewById(R.id.trackBottomLine).animate().alpha(1.0f).setDuration(100L).start();
                findViewById(R.id.trackLeftLine).animate().alpha(1.0f).setDuration(100L).start();
                findViewById(R.id.trackRightLine).animate().alpha(1.0f).setDuration(100L).start();
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.bringChildToFront(this);
                }
            }
            mTransitionView.setVisibility(getTrackClipInfo().getIndex() == 0 ? View.GONE : View.VISIBLE);
            mNextTransitionView.setVisibility(flag == PositionFlag.FOOTER || flag == PositionFlag.ONLY_ONE ? View.GONE : View.VISIBLE);
        }
        super.setSelected(selected);
    }

    @Override
    public void setListener() {
        super.setListener();
        mTransitionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemTrackCallback != null) {
                    mItemTrackCallback.onTransitionClick(getTrackClipInfo().getIndex());
                }
            }
        });
        mNextTransitionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemTrackCallback != null) {
                    mItemTrackCallback.onTransitionClick(getTrackClipInfo().getIndex() + 1);
                }
            }
        });
    }

    @Override
    protected void onPreStartDrag(View v) {
        setSelected(false);
        mContentView.requestLayout();
        mContentView.post(new Runnable() {
            @Override
            public void run() {
                VideoClipLayout.super.onPreStartDrag(v);
            }
        });
    }

    public void setTransitionIcon(boolean apply) {
        mTransitionView.setImageResource(apply ? R.drawable.ugsv_ic_editor_transition_bg_selected : R.drawable.ugsv_ic_editor_transition_bg);
    }

    public void setNextTransitionIcon(boolean apply) {
        mNextTransitionView.setImageResource(apply ? R.drawable.ugsv_ic_editor_transition_bg_selected : R.drawable.ugsv_ic_editor_transition_bg);
    }

    public void setDragSort(boolean dragSort) {
        mIsDragSort = dragSort;
        if (dragSort) {
            Util.setViewGone(mTransitionView);
            Util.setViewGone(mNextTransitionView);
        } else {
            mTransitionView.setVisibility(getTrackClipInfo().getIndex() == 0 ? View.GONE : View.VISIBLE);
            PositionFlag flag = ((MainVideoClipInfo) getTrackClipInfo()).getPositionFlag();
            mNextTransitionView.setVisibility(flag == PositionFlag.FOOTER || flag == PositionFlag.ONLY_ONE ? View.GONE : View.VISIBLE);
        }
        mItemFrameView.setDragSort(dragSort);
        mItemFrameView.requestLayout();
        mItemFrameView.invalidate();
    }

    public boolean isFirstMove() {
        return mFirstMove;
    }

    public void setFirstMove(boolean firstMove) {
        this.mFirstMove = firstMove;
    }
}
