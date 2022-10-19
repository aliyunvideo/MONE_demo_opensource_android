package com.aliyun.svideo.track.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.aliyun.svideo.track.api.HorizontallyState;
import com.aliyun.svideo.track.api.TrackConfig;
import com.aliyun.svideo.track.bean.ClipType;
import com.aliyun.svideo.track.bean.MainVideoClipInfo;
import com.aliyun.svideo.track.bean.PositionFlag;
import com.aliyun.svideo.track.inc.IMultiTrackListener;
import com.aliyun.svideo.track.inc.ItemTrackCallback;
import com.aliyun.svideo.track.inc.OnClipDragListener;
import com.aliyun.svideo.track.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 主轨容器
 */
public class MainTrackLayout extends RelativeLayout implements OnClipDragListener {
    /**
     * 主轨容器
     */
    private RelativeLayout mContentLayout;
    private boolean isInit;
    private int mScreenWidth;
    /**
     * 主轨片段信息列表
     */
    private List<MainVideoClipInfo> mMainClipList;
    /**
     * 主轨片段控件
     */
    private List<VideoClipLayout> mTrackLayoutList;
    /**
     * 主轨ID初始值，递增
     */
    private int trackLayoutId = 1001;
    private ItemTrackCallback mItemTrackCallback;
    private IMultiTrackListener mMultiTrackListener;
    /**
     * 父容器超过滚动范围偏移
     */
    private float mParentTranslationX;
    /**
     * 当前时间刻度缩放值
     */
    private float mTimelineScale = 1.0f;

    /**
     * 是否可长按拖动
     */
    private boolean mDragEnable = true;
    /**
     * 是否可以左右拖动
     */
    private boolean mDragHorizontallyEnable = true;

    public MainTrackLayout(Context context) {
        this(context, null);
    }

    public MainTrackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainTrackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mScreenWidth = Util.getScreenWidth();
        mContentLayout = new RelativeLayout(context);
        mMainClipList = new ArrayList<>();
        mTrackLayoutList = new ArrayList<>();
        mItemTrackCallback = new ItemTrackCallback() {

            @Override
            public void onClipTouchMove(int id, int dx, int orientation, float clipLength, int scrollByX) {
                int index = getTrackLayoutById(id).getTrackClipInfo().getIndex();
                if (orientation == 0) {
                    VideoClipLayout trackLayout = mTrackLayoutList.get(index);
                    if (index == 0) {
                        trackLayout.setTranslationX(trackLayout.getTranslationX() + dx);
                    } else {
                        for (int i = 0; i < index; i++) {
                            VideoClipLayout itemTrackLayout = mTrackLayoutList.get(i);
                            itemTrackLayout.setTranslationX(itemTrackLayout.getTranslationX() + dx);
                        }
                    }
                }
                if (mMultiTrackListener != null) {
                    MainVideoClipInfo clipInfo = mMainClipList.get(index);
                    long contentDuration = (long) (clipLength / TrackConfig.getPxUnit(mTimelineScale));
                    //处理超过滚动范围情况
                    if (orientation == 0) {
                        if (scrollByX < 0) {
                            int curScrollX = mMultiTrackListener.getParentScrollX();
                            if (curScrollX == 0) {
                                mParentTranslationX = mParentTranslationX + scrollByX;
                                scrollByX = 0;
                            } else {
                                int newScrollX = curScrollX + scrollByX;
                                if (newScrollX < 0) {
                                    mParentTranslationX = mParentTranslationX + newScrollX;
                                    scrollByX = scrollByX - newScrollX;
                                }
                            }
                        } else {
                            if (mParentTranslationX < 0) {
                                mParentTranslationX = mParentTranslationX + scrollByX;
                            }
                            if (mParentTranslationX > 0) {
                                scrollByX = (int) mParentTranslationX;
                                mParentTranslationX = 0;
                            }
                            if (mParentTranslationX < 0) {
                                scrollByX = 0;
                            }
                        }
                        mMultiTrackListener.onClipTouchPosition(clipInfo.getTimelineOut() - contentDuration);
                    } else {
                        if (scrollByX > 0) {
                            int curScrollX = mMultiTrackListener.getParentScrollX();
                            if (curScrollX == mMultiTrackListener.getParentMaxScrollX()) {
                                mParentTranslationX = mParentTranslationX + scrollByX;
                                scrollByX = 0;
                            } else {
                                int newScrollX = curScrollX + scrollByX;
                                if (newScrollX > mMultiTrackListener.getParentMaxScrollX()) {
                                    mParentTranslationX = mParentTranslationX + newScrollX;
                                    scrollByX = scrollByX - newScrollX;
                                }
                            }
                        } else {
                            if (mParentTranslationX > 0) {
                                mParentTranslationX = mParentTranslationX + scrollByX;
                            }
                            if (mParentTranslationX < 0) {
                                scrollByX = (int) mParentTranslationX;
                                mParentTranslationX = 0;
                            }
                            if (mParentTranslationX > 0) {
                                scrollByX = 0;
                            }
                        }
                        mMultiTrackListener.onClipTouchPosition(clipInfo.getTimelineIn() + contentDuration);
                    }

                    mMultiTrackListener.onParentTranslationX(-mParentTranslationX);
                    if (scrollByX != 0) {
                        mMultiTrackListener.onScrollByX(scrollByX,false);
                    }
                }
            }

            @Override
            public void onTransitionClick(int index) {
                if (mMultiTrackListener != null) {
                    mMultiTrackListener.onTransitionClick(index);
                }

            }

            @Override
            public void onTrackTouch(int id, boolean isFocus) {
                if (mMultiTrackListener != null) {
                    mMultiTrackListener.onVideoTrackClick(id, isFocus);
                }
            }

            @Override
            public void onClipTouchUp(int id, int orientation, long clipInTime, long clipOutTime) {
                int index = getTrackLayoutById(id).getTrackClipInfo().getIndex();
                if (index == 0 && mTrackLayoutList.size() > 1) {
                    VideoClipLayout nextVideoClipLayout = mTrackLayoutList.get(index + 1);
                    nextVideoClipLayout.setFirstMove(false);
                }
                mParentTranslationX = 0;
                mMultiTrackListener.onParentTranslationX(mParentTranslationX);
                mContentLayout.removeAllViews();
                addTrackLayoutView();
                mTrackLayoutList.get(index).setSelected(true);
            }

            @Override
            public void onClipTouchDown(int id, int orientation, float clipLength, int contentLeftPosition) {
                int index = getTrackLayoutById(id).getTrackClipInfo().getIndex();
                if (orientation == 0) {
                    if (index == 0 && mTrackLayoutList.size() > 1) {
                        VideoClipLayout nextVideoClipLayout = mTrackLayoutList.get(index + 1);
                        nextVideoClipLayout.setFirstMove(true);
                    }
                }
            }

            @Override
            public void onUpdateClipTime(int id, int orientation, long timeIn, long timeOut, long timelineIn, long timelineOut) {
                if (mMultiTrackListener != null) {
                    mMultiTrackListener.onUpdateClipTime(ClipType.MAIN_VIDEO, id, timeIn, timeOut, timelineIn, timelineOut);
                }
            }
        };
    }

    public void setTimelineScale(float timelineScale) {
        this.mTimelineScale = timelineScale;
        for (VideoClipLayout videoTrackLayout : mTrackLayoutList) {
            videoTrackLayout.setTimelineScale(this.mTimelineScale);
        }
    }

    protected VideoClipLayout getTrackLayoutById(int id) {
        for (VideoClipLayout layout : mTrackLayoutList) {
            if (layout.getTrackClipInfo().getClipId() == id) {
                return layout;
            }
        }
        return null;
    }

    public void updateTrackFocus(int id, boolean isFocus) {
        BaseClipLayout oldFocus = null;
        BaseClipLayout newSet = null;
        for (VideoClipLayout item : mTrackLayoutList) {
            if (item.isSelected()) {
                oldFocus = item;
            }
            if (item.getTrackClipInfo() != null && item.getTrackClipInfo().getClipId() == id) {
                newSet = item;
            }
        }
        if (isFocus) {
            if (oldFocus != null && oldFocus != newSet) {
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
//        //非拖动模式时把转场按钮提到前面
//        if (!mDragHorizontallyEnable) {
//            for (int i = 0; i < mTrackLayoutList.size(); i++) {
//                mTrackLayoutList.get(i).bringTransitionToFront();
//                Log.d("tantai","updateTrackFocus>"+i+">bringTransitionToFront");
//            }
//        }
    }

    public void setVideoClip(List<MainVideoClipInfo> list) {
        if (!isInit) {
            addView(mContentLayout, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            setClipChildren(false);
            isInit = true;
        }
        setVideoData(list);
    }

    private void setVideoData(List<MainVideoClipInfo> list) {
        mContentLayout.removeAllViews();
        mTrackLayoutList.clear();
        mMainClipList.clear();
        mMainClipList.addAll(list);
        for (int i = 0; i < mMainClipList.size(); i++) {
            trackLayoutId++;
            VideoClipLayout itemTrackLayout = new VideoClipLayout(getContext());
            itemTrackLayout.setDragEnable(mDragEnable);
            itemTrackLayout.setDragHorizontallyEnable(mDragHorizontallyEnable);
            itemTrackLayout.setId(trackLayoutId);
            itemTrackLayout.setItemTrackCallback(mItemTrackCallback);
            itemTrackLayout.setTimelineScale(this.mTimelineScale);
            mTrackLayoutList.add(itemTrackLayout);
            MainVideoClipInfo mainVideoClipInfo = mMainClipList.get(i);
            mainVideoClipInfo.setIndex(i);
            itemTrackLayout.setData(mainVideoClipInfo);
        }
        sortListLayout();
        addTrackLayoutView();
    }

    public void setDragEnable(boolean dragEnable) {
        mDragEnable = dragEnable;
        for (VideoClipLayout videoTrackLayout : mTrackLayoutList) {
            videoTrackLayout.setDragEnable(dragEnable);
        }
    }

    public void setDragHorizontallyEnable(boolean dragHorizontallyEnable) {
        this.mDragHorizontallyEnable = dragHorizontallyEnable;
        for (VideoClipLayout videoTrackLayout : mTrackLayoutList) {
            videoTrackLayout.setDragHorizontallyEnable(this.mDragHorizontallyEnable);
        }
    }

    /**
     * 更新转场状态
     * @param index 转场片段
     * @param duration 转场时长
     * @param apply 载入或者移除
     */
    public void updateTransition(int index, long duration, boolean apply) {
        if (index < mTrackLayoutList.size() && index > -1) {
            if (index > 0) {
                VideoClipLayout preTrackLayout = mTrackLayoutList.get(index - 1);
                if (apply) {
                    ((MainVideoClipInfo) preTrackLayout.getTrackClipInfo()).setTransitionOverlapTailDuration(duration);
                } else {
                    ((MainVideoClipInfo) preTrackLayout.getTrackClipInfo()).setTransitionOverlapTailDuration(0);
                }
                preTrackLayout.setNextTransitionIcon(apply);
                preTrackLayout.requestLayout();
                preTrackLayout.invalidate();
            }

            VideoClipLayout itemTrackLayout = mTrackLayoutList.get(index);
            if (apply) {
                ((MainVideoClipInfo) itemTrackLayout.getTrackClipInfo()).setTransitionOverlapHeadDuration(duration);
            } else {
                ((MainVideoClipInfo) itemTrackLayout.getTrackClipInfo()).setTransitionOverlapHeadDuration(0);
            }
            itemTrackLayout.setTransitionIcon(apply);
            itemTrackLayout.requestLayout();
            itemTrackLayout.invalidate();
        }
        updateTimeline();
    }

    public void updateSpeed(float speed){
        for (MainVideoClipInfo item : getMainClipList()) {
            item.setSpeed(speed);
        }
        updateTimeline();
    }

    /**
     * 更新主轨时间轴
     */
    protected void updateTimeline() {
        for (int i = 0; i < mMainClipList.size(); i++) {
            MainVideoClipInfo mainVideoClipInfo = mMainClipList.get(i);
            if (i != 0) {
                MainVideoClipInfo preClipInfo = mMainClipList.get(i - 1);
                mainVideoClipInfo.setTimelineIn(preClipInfo.getTimelineOut() - preClipInfo.getTransitionOverlapTailDuration() + 1);
            } else {
                mainVideoClipInfo.setTimelineIn(0);
            }
            mainVideoClipInfo.setTimelineOut(mainVideoClipInfo.getTimelineIn() + (mainVideoClipInfo.getOut() - mainVideoClipInfo.getIn()));
        }
    }

    private void addTrackLayoutView() {
        for (int i = 0; i < mTrackLayoutList.size(); i++) {
            VideoClipLayout itemTrackLayout = mTrackLayoutList.get(i);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i != 0) {
                layoutParams.leftMargin = -2 * TrackConfig.MOVE_BTN_WIDTH;
                layoutParams.addRule(RelativeLayout.RIGHT_OF, mTrackLayoutList.get(i - 1).getId());
            }
            itemTrackLayout.setTranslationX(0);
            itemTrackLayout.setSelected(false);
            Log.d("tantai","onUpdatePlayTime>addView"+itemTrackLayout.getTrackClipInfo().getTimelineIn());
            mContentLayout.addView(itemTrackLayout, layoutParams);
        }
    }

    private void sortListLayout() {
        if (mMainClipList.size() > 0) {
            for (int i = 0; i < mMainClipList.size(); i++) {
                mMainClipList.get(i).setIndex(i);
                PositionFlag positionFlag = PositionFlag.MIDDLE;
                if (mMainClipList.size() == 1) {
                    positionFlag = PositionFlag.ONLY_ONE;
                } else if (i == 0) {
                    positionFlag = PositionFlag.HEADER;
                } else if (i == mMainClipList.size() - 1) {
                    positionFlag = PositionFlag.FOOTER;
                }
                mMainClipList.get(i).setPositionFlag(positionFlag);
            }
        }
        updateTimeline();
        Collections.sort(mTrackLayoutList, new Comparator<VideoClipLayout>() {
            @Override
            public int compare(VideoClipLayout o1, VideoClipLayout o2) {
                return o1.getTrackClipInfo().getIndex() - o2.getTrackClipInfo().getIndex();
            }
        });
    }

    private void removeTrackLayoutView(int index) {
        if (mMainClipList != null && index > -1 && index < mMainClipList.size()) {
            mMainClipList.remove(index);
            VideoClipLayout videoTrackLayout = mTrackLayoutList.get(index);
            int size = mTrackLayoutList.size();
            if (index == 0 && size > 1) {
                VideoClipLayout nextVideoTrack = mTrackLayoutList.get(index + 1);
                LayoutParams layoutParams = (LayoutParams) nextVideoTrack.getLayoutParams();
                layoutParams.leftMargin = 0;
                nextVideoTrack.setLayoutParams(layoutParams);
            } else if (size > 3 && index < size - 1) {
                VideoClipLayout nextVideoTrack = mTrackLayoutList.get(index + 1);
                VideoClipLayout previousTrack = mTrackLayoutList.get(index - 1);
                LayoutParams layoutParams = (LayoutParams) nextVideoTrack.getLayoutParams();
                layoutParams.addRule(RelativeLayout.RIGHT_OF, previousTrack.getId());
            }
            mContentLayout.removeView(videoTrackLayout);
            sortListLayout();
        }
    }

    public void updateVideoData(List<MainVideoClipInfo> list) {
        if (list != null && list.size() == mMainClipList.size()) {
            mMainClipList.clear();
            mMainClipList.addAll(list);
            sortListLayout();
        }
    }

    public void setMultiTrackListener(IMultiTrackListener iMultiTrackListener) {
        mMultiTrackListener = iMultiTrackListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        Log.d("AlTimelineView", "MultiTrackLayout width:" + width);
    }

    public void removeTrack(int index) {
        removeTrackLayoutView(index);
        if (mMainClipList != null && index > -1 && index < mMainClipList.size()) {
            mMainClipList.remove(index);
            List<MainVideoClipInfo> list = new ArrayList<>(mMainClipList);
            setVideoData(list);
        }
    }

    public List<MainVideoClipInfo> getMainClipList() {
        return mMainClipList;
    }

    VideoClipLayout draggedClipLayout;
    float startPointX;
    float translationX;
    /**
     * 拖动时所在位置
     */
    int draggedIndex;
    /**
     * 当前所在位置
     */
    int targetIndex;
    /**
     * 轨道父控件开始时偏移
     */
    float startTranslationX;
    /**
     * 轨道父控件最小偏移
     */
    int minTranslationX;
    /**
     * 轨道父控件最大偏移
     */
    int maxTranslationX;
    /**
     * 上次拖动点位置
     */
    float oldPointX;
    /**
     * 拖动片段自身偏移
     */
    float draggedTranslationX = 0;
    /**
     * 自动偏移速度
     */
    private float mAutoTranslationXSpeed;
    /**
     * 自动偏移动画
     */
    private ValueAnimator mAutoTranslationXAnim;
    private HorizontallyState mTranslationXState = HorizontallyState.NULL;

    private final ValueAnimator.AnimatorUpdateListener mAnimUpdateListener = new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float targetX = (Float) animation.getAnimatedValue();
            mContentLayout.setTranslationX(targetX);
            if (draggedClipLayout != null) {
                draggedClipLayout.setTranslationX(draggedTranslationX - (mContentLayout.getTranslationX() - startTranslationX));
            }
        }
    };

    private boolean isNeedTranslationX(float rawX, float dx) {
        HorizontallyState horizontallyState;
        if (rawX >= mScreenWidth - TrackConfig.AUTO_SCROLL_WIDTH &&
                mContentLayout.getTranslationX() > minTranslationX
                && dx > -1) {
            horizontallyState = HorizontallyState.RIGHT;
        } else if (rawX <= TrackConfig.AUTO_SCROLL_WIDTH &&
                mContentLayout.getTranslationX() < maxTranslationX
                && dx < 1) {
            horizontallyState = HorizontallyState.LEFT;
        } else {
            horizontallyState = HorizontallyState.NULL;
        }
        onAutoTranslationX(horizontallyState);
        return horizontallyState != HorizontallyState.NULL;
    }


    private void onAutoTranslationX(HorizontallyState horizontallyState) {
        if (horizontallyState == mTranslationXState) {
            return;
        }
        if (mAutoTranslationXAnim != null) {
            mAutoTranslationXAnim.removeUpdateListener(mAnimUpdateListener);
            mAutoTranslationXAnim.cancel();
        }
        mTranslationXState = horizontallyState;
        //左移
        if (horizontallyState == HorizontallyState.LEFT) {
            //左移，从当前位置移动到最小位置
            int length = (int) (maxTranslationX - mContentLayout.getTranslationX());
            mAutoTranslationXAnim = ValueAnimator.ofFloat(mContentLayout.getTranslationX(), maxTranslationX);
            mAutoTranslationXAnim.setDuration((long) (length / mAutoTranslationXSpeed));
            mAutoTranslationXAnim.addUpdateListener(mAnimUpdateListener);
            mAutoTranslationXAnim.start();
        } else if (horizontallyState == HorizontallyState.RIGHT) {
            //右移，从当前位置移动到最小位置
            int length = (int) (mContentLayout.getTranslationX() - minTranslationX);
            mAutoTranslationXAnim = ValueAnimator.ofFloat(mContentLayout.getTranslationX(), minTranslationX);
            mAutoTranslationXAnim.setDuration((long) (length / mAutoTranslationXSpeed));
            mAutoTranslationXAnim.addUpdateListener(mAnimUpdateListener);
            mAutoTranslationXAnim.start();
        }
    }

    private void onDragSort(boolean dragSort, float pointX) {
        int scrollX = mMultiTrackListener.getParentScrollX();
        for (VideoClipLayout videoTrackLayout : mTrackLayoutList) {
            videoTrackLayout.setSelected(false);
            videoTrackLayout.setDragSort(dragSort);
        }
        int draggedTranslationX = 0;
        if (dragSort) {
            draggedClipLayout = getTrackLayoutById(TrackConfig.draggedId);
            draggedIndex = draggedClipLayout.getTrackClipInfo().getIndex();
            targetIndex = draggedIndex;
            startPointX = pointX;
            translationX = draggedIndex * (TrackConfig.FRAME_WIDTH + TrackConfig.FRAME_MID_MARGIN);
            LayoutParams draggedLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            draggedClipLayout.setLayoutParams(draggedLayoutParams);
            mContentLayout.removeView(draggedClipLayout);
            mContentLayout.addView(draggedClipLayout);
            draggedClipLayout.setTranslationX(translationX);
            if (draggedIndex > -1 && draggedIndex < mTrackLayoutList.size()) {
                LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (draggedIndex != mTrackLayoutList.size() - 1) {
                    if (draggedIndex != 0) {
                        VideoClipLayout previousTrack = mTrackLayoutList.get(draggedIndex - 1);
                        layoutParams.addRule(RelativeLayout.RIGHT_OF, previousTrack.getId());
                        layoutParams.leftMargin = -2 * TrackConfig.MOVE_BTN_WIDTH;
                    } else {
                        layoutParams.leftMargin = 0;
                    }
                    VideoClipLayout nextVideoTrack = mTrackLayoutList.get(draggedIndex + 1);
                    nextVideoTrack.setLayoutParams(layoutParams);
                }
                sort(targetIndex);
            }
            minTranslationX = scrollX - (mTrackLayoutList.size() * (TrackConfig.FRAME_WIDTH + TrackConfig.FRAME_MID_MARGIN));
            maxTranslationX = scrollX;
            draggedTranslationX = (int) (pointX - (Util.getScreenWidth() / 2 + draggedIndex * (TrackConfig.FRAME_WIDTH + TrackConfig.FRAME_MID_MARGIN) + TrackConfig.FRAME_WIDTH / 2));
            startTranslationX = scrollX + draggedTranslationX;
            mContentLayout.setTranslationX(startTranslationX);
        } else {
            onAutoTranslationX(HorizontallyState.NULL);
            int oldIndex = draggedIndex;
            int newIndex = targetIndex;
            if (oldIndex != newIndex) {
                MainVideoClipInfo clip = mMainClipList.get(oldIndex);
                mMainClipList.remove(clip);
                mMainClipList.add(newIndex, clip);
                if (mMultiTrackListener != null) {
                    mMultiTrackListener.onUpdateMainClipIndex(oldIndex, newIndex);
                }
            }
            draggedClipLayout = null;
            mContentLayout.setTranslationX(0);
            mContentLayout.removeAllViews();
            sortListLayout();
            addTrackLayoutView();
        }
    }

    private void sort(int target) {
        for (int i = 0; i < mTrackLayoutList.size(); i++) {
            VideoClipLayout item = mTrackLayoutList.get(i);
            if (i == draggedIndex) {
                continue;
            }
            if (target <= draggedIndex) {
                if (target <= i) {
                    item.setTranslationX(TrackConfig.FRAME_WIDTH + TrackConfig.FRAME_MID_MARGIN);
                } else {
                    item.setTranslationX(0);
                }
            } else {
                if (target < i) {
                    item.setTranslationX(TrackConfig.FRAME_WIDTH + TrackConfig.FRAME_MID_MARGIN);
                } else {
                    item.setTranslationX(0);
                }
            }
        }
    }

    private void onDragLocationChange(float pointX) {
        mAutoTranslationXSpeed = TrackConfig.autoScrollSpeed(pointX, mScreenWidth);
        draggedTranslationX = translationX + (pointX - startPointX);
        draggedClipLayout.setTranslationX(draggedTranslationX - (mContentLayout.getTranslationX() - startTranslationX));
        isNeedTranslationX(pointX, pointX - oldPointX);
        oldPointX = pointX;
        Rect rect = new Rect();
        mContentLayout.getGlobalVisibleRect(rect);
        int[] location = new int[2];
        mContentLayout.getLocationInWindow(location);
        int index = (int) ((pointX - (location[0] + TrackConfig.MOVE_BTN_WIDTH)) / (TrackConfig.FRAME_WIDTH + TrackConfig.FRAME_MID_MARGIN));
        if (index < 0) {
            index = 0;
        } else if (index >= mTrackLayoutList.size()) {
            index = mTrackLayoutList.size() - 1;
        }
        if (targetIndex != index) {
            sort(index);
            targetIndex = index;
        }
    }

    @Override
    public void onDragStart(float pointX, float pointY) {
        onDragSort(true, pointX);
    }

    @Override
    public void onDragEnd(float pointX, float pointY) {
        onDragSort(false, pointX);
    }

    @Override
    public void onDragLocationChange(float pointX, float pointY) {
        onDragLocationChange(pointX);
    }
}
