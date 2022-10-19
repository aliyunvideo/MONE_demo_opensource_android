package com.aliyun.svideo.track.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;


import com.aliyun.svideo.track.api.TrackConfig;
import com.aliyun.svideo.track.bean.MainVideoClipInfo;
import com.aliyun.svideo.track.bean.PositionFlag;
import com.aliyun.svideo.track.thumbnail.ThumbnailBitmap;
import com.aliyun.svideo.track.thumbnail.ThumbnailFetcherManger;
import com.aliyun.svideo.track.thumbnail.ThumbnailRequestListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 片段缩略图控件
 */
public class ClipFrameView extends View implements ThumbnailRequestListener {
    /**
     * 片段信息
     */
    private MainVideoClipInfo mMainClipInfo;
    /**
     * 整条缩略图绘制范围
     */
    private RectF mTargetDrawRect;
    /**
     * 一张缩略图绘制范围
     */
    private Rect mFrameRect;
    /**
     * 片段形状路径
     */
    private Path mClipPath;
    /**
     * 是否是拖动排序状态
     */
    private boolean mIsDragSort;
    private Pair<Integer, Float> mFrameViewNumPair;
    /**
     * 当前时间刻度缩放值
     */
    private float mTimelineScale = 1.0f;

    List<ThumbnailBitmap> mBitmapList = new ArrayList<>();

    public ClipFrameView(Context context) {
        this(context, null);
    }

    public ClipFrameView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipFrameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setDragSort(boolean dragSort) {
        this.mIsDragSort = dragSort;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height = getDefaultSize(heightSize, heightMeasureSpec);
        int width = 0;
        if (mIsDragSort) {
            width = TrackConfig.FRAME_WIDTH;
        } else if (mMainClipInfo != null) {
            mFrameViewNumPair = initTrackFrameNum(mMainClipInfo);
            long targetDuration = mMainClipInfo.getClipDuration();
            float contentWidth = targetDuration * TrackConfig.getPxUnit(mTimelineScale);
            width = Math.round(contentWidth);
        } else {
            width = getDefaultSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    private void initView() {
        mTargetDrawRect = new RectF();
        mFrameRect = new Rect();
        mClipPath = new Path();
    }

    public void setTimelineScale(float timelineScale) {
        this.mTimelineScale = timelineScale;
    }

    public void updateData(MainVideoClipInfo mainVideoClipInfo) {
        mMainClipInfo = mainVideoClipInfo;
        requestLayout();
        refresh();
    }

    private void refresh() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        long[] times = null;
        //0时为图片
        if (mMainClipInfo.getDuration() == 0) {
            times = new long[1];
        } else {
            int size = (int) Math.ceil(mMainClipInfo.getDuration() / 1000.0f);
            times = new long[size];
            for (int i = 0; i < times.length; i++) {
                times[i] = i * 1000L;
            }
        }
        ThumbnailFetcherManger.getInstance().requestThumbnailImage(mMainClipInfo.getPath(), times, this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ThumbnailFetcherManger.getInstance().removeThumbnailRequestListener(mMainClipInfo.getPath(), this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mIsDragSort) {
            drawContent(canvas);
        } else {
            //拖动排序状态下只显示一张
            float startIndex = mMainClipInfo.getIn() / 1000.0f;
            Bitmap bitmap = getBitmap((long) (Math.floor(startIndex) * 1000L));
            if (bitmap != null && !bitmap.isRecycled()) {
                mFrameRect.set(0, 0, TrackConfig.FRAME_WIDTH, TrackConfig.FRAME_WIDTH);
                canvas.drawBitmap(bitmap, null, mFrameRect, null);
            }
        }
    }

    private void drawContent(Canvas canvas) {
        float transitionHeadWidthPx = TrackConfig.getPxUnit(mTimelineScale) * mMainClipInfo.getTransitionOverlapHeadDuration();
        float transitionTailWidthPx = TrackConfig.getPxUnit(mTimelineScale) * mMainClipInfo.getTransitionOverlapTailDuration();
        PositionFlag positionFlag = mMainClipInfo.getPositionFlag();
        //轨道间距
        long marginEnd = (positionFlag == PositionFlag.ONLY_ONE || positionFlag == PositionFlag.FOOTER || isSelected()) ? 0 : TrackConfig.FRAME_MID_MARGIN;
        //计算尾部转场间隔线底部长度
        if (marginEnd != 0 && transitionTailWidthPx > 0) {
            float angle = getAngle(transitionTailWidthPx, -getHeight(), 0, 0);
            marginEnd = Math.round(marginEnd / Math.sin(Math.toRadians(angle)));
        }

        float right = mMainClipInfo.getClipDuration() * TrackConfig.getPxUnit(mTimelineScale) - marginEnd;
        float bottom = getHeight();

        mTargetDrawRect.set(0.0f, 0.0f, right, bottom);

        mClipPath.reset();

        if (transitionHeadWidthPx > 0 || transitionTailWidthPx > 0) {
            if (transitionHeadWidthPx > 0) {
                //转场在开头部分裁切
                mClipPath.moveTo(0, 0);
                mClipPath.lineTo(transitionHeadWidthPx, 0);
                mClipPath.lineTo(0, bottom);
                mClipPath.close();
            }

            if (transitionTailWidthPx > 0) {
                //转场在尾部部分裁切
                mClipPath.moveTo(right - transitionTailWidthPx, bottom);
                mClipPath.lineTo(right, 0);
                mClipPath.lineTo(right, bottom);
                mClipPath.close();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Path.Op op = isSelected() ? Path.Op.UNION : Path.Op.XOR;
                Path pathXOR = new Path();
                pathXOR.moveTo(0, 0);
                pathXOR.lineTo(right, 0);
                pathXOR.lineTo(right, getHeight());
                pathXOR.lineTo(0, getHeight());
                pathXOR.close();
                pathXOR.op(mClipPath, op);
                canvas.clipPath(pathXOR);
            } else {
                Region.Op op = isSelected() ? Region.Op.UNION : Region.Op.XOR;
                canvas.clipPath(mClipPath, op);
            }

        } else {
            mClipPath.addRect(mTargetDrawRect, Path.Direction.CW);
            canvas.clipPath(mClipPath, Region.Op.INTERSECT);
        }

        int index = 0;
        float startIndex = mMainClipInfo.getIn() / 1000.0f;
        int startPx = (int) (-(startIndex - Math.floor(startIndex)) * TrackConfig.FRAME_WIDTH);
        while (true) {
            int currentPx = startPx + TrackConfig.FRAME_WIDTH * index;
            float borderLeft = mTargetDrawRect.left - TrackConfig.FRAME_WIDTH;
            float borderRight = mTargetDrawRect.right + TrackConfig.FRAME_WIDTH;
            if (currentPx >= borderLeft && currentPx <= borderRight) {
                Bitmap bitmap = getBitmap((long) ((Math.floor(startIndex) + index) * 1000L));
                if (bitmap != null && !bitmap.isRecycled()) {
                    mFrameRect.set(currentPx, 0, TrackConfig.FRAME_WIDTH + currentPx, TrackConfig.FRAME_WIDTH);
                    canvas.drawBitmap(bitmap, null, mFrameRect, null);
                }
            }
            if (index == mFrameViewNumPair.first) {
                break;
            }
            index++;
        }
        //选择状态下转场部分蒙层
        if (isSelected() && (transitionHeadWidthPx > 0 || transitionTailWidthPx > 0)) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            paint.setAlpha(125);
            canvas.drawPath(mClipPath, paint);
        }
    }

    /**
     * 获取转场切割线角度
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private float getAngle(float x1, float y1, float x2, float y2) {
        float x = Math.abs(x1 - x2);
        float y = Math.abs(y1 - y2);
        double z = Math.sqrt(x * x + y * y);
        return (float) (Math.asin(y / z) / Math.PI * 180);
    }

    private Pair<Integer, Float> initTrackFrameNum(MainVideoClipInfo flipInfo) {
        long duration = flipInfo.getDuration() == 0 ? flipInfo.getTimelineOut() - flipInfo.getTimelineIn() : flipInfo.getDuration();
        float num = duration * TrackConfig.getPxUnit(mTimelineScale) / TrackConfig.FRAME_WIDTH;
        return new Pair<Integer, Float>((int) num, num - ((int) num));
    }

    @Override
    public String getPath() {
        if (mMainClipInfo == null) {
            return null;
        }
        return mMainClipInfo.getPath();
    }

    @Override
    public long getTimelineIn() {
        if (mMainClipInfo == null) {
            return 0;
        }
        return mMainClipInfo.getTimelineIn();
    }

    @Override
    public long getTimelineOut() {
        if (mMainClipInfo == null) {
            return 0;
        }
        return mMainClipInfo.getTimelineOut();
    }

    @Override
    public void onThumbnailReady(String path, List<ThumbnailBitmap> list) {
        mBitmapList.addAll(list);
        sortBitmapList();
        invalidate();
    }

    @Override
    public void onThumbnailReady(String path, ThumbnailBitmap thumbnailBitmap) {
        mBitmapList.add(thumbnailBitmap);
        sortBitmapList();
        invalidate();
    }

    @Override
    public void onError(String path, int errorCode) {

    }

    private void sortBitmapList() {
        Collections.sort(mBitmapList, new Comparator<ThumbnailBitmap>() {
            @Override
            public int compare(ThumbnailBitmap o1, ThumbnailBitmap o2) {
                return (int) (o1.getTime() - o2.getTime());
            }
        });
    }

    private Bitmap getBitmap(long time) {
        Bitmap result = null;
        for (ThumbnailBitmap item : mBitmapList) {
            if (item.getTime() > time) {
                return result;
            }
            result = item.getBitmap();
        }
        return result;
    }

}
