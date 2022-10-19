/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.editor.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.aliyun.ugsv.common.utils.ScreenUtils;
import com.aliyun.svideo.editor.common.R;
import com.aliyun.svideo.editor.common.util.MatrixUtil;

public abstract class BaseAliyunPasterView extends ViewGroup {


    public interface OnChangeListener {

        void onChange(BaseAliyunPasterView overlay);

    }

    /**
     * normalized 2d coordinates: x y: (0, 0) right bottom: (1, 1) object
     * coordinates: (0, 0) (1, 1)
     */
    public static final int MODE_NORMALIZED = 1;

    /**
     * device coordinates: x y: (- CanvasWidth / 2, - CanvasHeight / 2)
     * right bottom: (+ CanvasWidth / 2, + CanvasHeight / 2) object coordinates:
     * (- ContentWidth / 2, - ContentHeight / 2) (+ ContentWidth / 2, +
     * ContentHeight / 2)
     */
    public static final int MODE_DEVICE = 2;

    /**
     * viewport coordinates: x y: (- ViewportWidth / 2, - ViewportHeight /
     * 2) right bottom: (+ ViewportWidth / 2, + ViewportWidth / 2) object
     * coordinates: (- ContentWidth / 2, - ContentHeight / 2) (+ ContentWidth /
     * 2, + ContentHeight / 2)
     */
    public static final int MODE_VIEWPORT = 3;

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray ta = c.obtainStyledAttributes(attrs,
                            R.styleable.EditOverlay_Layout);

            gravity = ta.getInteger(
                          R.styleable.EditOverlay_Layout_android_layout_gravity,
                          Gravity.LEFT | Gravity.TOP);

            ta.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public int gravity;
    }

    private static final String TAG = "EditOverlay";

    public BaseAliyunPasterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        post(new Runnable() {
            @Override
            public void run() {
                ViewGroup parent = (ViewGroup) getParent();
                if (parent != null ) {
                    width = parent.getWidth();
                    height = parent.getHeight();
                }
            }
        });
    }

    public BaseAliyunPasterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseAliyunPasterView(Context context) {
        this(context, null);
    }

    private int mViewportWidth = 640;
    private int mViewportHeight = 640;

    private int width;
    private int height;

    private boolean isMirror;

    public int getLayoutWidth() {
        return width;
    }

    public int getLayoutHeight() {
        return height;
    }

    public void setViewport(int w, int h) {
        mViewportWidth = w;
        mViewportHeight = h;
    }

    public boolean isMirror() {
        return isMirror;
    }

    public void setMirror(boolean mirror) {
        isMirror = mirror;
    }

    private OnChangeListener mListener;

    public void setOnChangeListener(OnChangeListener listener) {
        mListener = listener;
    }

    public float[] getScale() {
        float[] scale = new float[2];
        scale[0] = mMatrixUtil.getScaleX();
        scale[1] = mMatrixUtil.getScaleY();
        return scale;
    }

    public MatrixUtil getMatrixUtil() {
        return mMatrixUtil;
    }

    @Override
    public float getRotation() {
        return mMatrixUtil.getRotation();
    }

    public abstract int getContentWidth();

    public abstract int getContentHeight();

    public abstract void setContentWidth(int width);

    public abstract void setContentHeight(int height);

    public abstract View getContentView();

    public boolean isCouldShowLabel() {
        return false;
    }

    public void setShowTextLabel(boolean isShow) {

    }

    public View getTextLabel() {
        return null;
    }

    public void setEditCompleted(boolean isEditCompleted) {

    }

    protected void validateTransform() {
        Log.d("TRANSFORM", "before validateTransform : " + mTransform.toString() + "mode : " + mTransformMode);
        if (getContentWidth() == 0 ||  getContentHeight() == 0) {
            return;
        }
        switch (mTransformMode) {
        case MODE_DEVICE:
            return;
        case MODE_NORMALIZED:
            mTransform.preTranslate(0.5f, 0.5f);
            Log.d("VALIDATE", "content_width : " + getContentWidth() + " content_height : " + getContentHeight());
            mTransform.preScale(1.0f / getContentWidth(), 1.0f / getContentHeight());
            mTransform.postTranslate(-0.5f, -0.5f);
            //Log.d("VALIDATE", "getWidth : " + getWidth() + " getHeight : " + getHeight());

            mTransform.postScale(getWidth(), getHeight());
            break;
        case MODE_VIEWPORT:
            mTransform.postScale((float) getWidth() / mViewportWidth,
                                 (float) getHeight() / mViewportHeight);
            break;

        default:
            break;
        }

        Log.d("TRANSFORM", "after validateTransform : " + mTransform.toString() + "mode : " + mTransformMode);

        mTransformMode = MODE_DEVICE;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        validateTransform();

        onLayoutContent();

        float hw, hh;
        int baseX;
        int baseY;

        hw = getContentWidth() / 2;
        hh = getContentHeight() / 2;

        // lt, rt, lb, rb
        POINT_LIST[0] = -hw;
        POINT_LIST[1] = -hh;
        POINT_LIST[2] = hw;
        POINT_LIST[3] = -hh;
        POINT_LIST[4] = -hw;
        POINT_LIST[5] = hh;
        POINT_LIST[6] = hw;
        POINT_LIST[7] = hh;
        baseX = getWidth() / 2;
        baseY = getHeight() / 2;

        Matrix together = converge();
        together.mapPoints(POINT_LIST);

        for (int i = 0, count = getChildCount(); i < count; i++) {
            View child = getChildAt(i);

            if (child == getContentView()) {
                continue;
            }

            int gravity = ((LayoutParams) child.getLayoutParams()).gravity;

            int ix = getPointFromMatrix(gravity);

            int centerX = (int) POINT_LIST[ix];
            int centerY = (int) POINT_LIST[ix + 1];

            int left = baseX + centerX;
            int top = baseY + centerY;
            int right = baseX + centerX;
            int bottom = baseY + centerY;

            int halfW = child.getMeasuredWidth() / 2;
            int halfH = child.getMeasuredHeight() / 2;
            int childLeft = left - halfW;
            int childTop = top - halfH;
            int childRight = right + halfW;
            int childBottom = bottom + halfH;

            Log.d("DIY_FRAME", "child_left : " + childLeft + " child_top : " + childTop +
                  " child_right : " + childRight + " child_bottom : " + childBottom);

            child.layout(childLeft, childTop, childRight, childBottom);
        }

    }

    private int getPointFromMatrix(int gravity) {
        int x = 0, y = 0;
        switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
        case Gravity.TOP:
            y = 0;
            break;
        case Gravity.BOTTOM:
            y = 1;
            break;
        default:
            break;
        }
        switch (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
        case Gravity.LEFT:
            x = 0;
            break;
        case Gravity.RIGHT:
            x = 1;
            break;
        default:
            break;
        }
        return (x + y * 2) * 2;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * bitmap to editor
     */
    protected final Matrix mTransform = new Matrix();
    private int mTransformMode = MODE_DEVICE;
    private final Matrix mInverseTransform = new Matrix();
    private boolean mInverseTransformInvalidated = false;

    public void invalidateTransform() {
        mInverseTransformInvalidated = true;
        validateTransform();
        requestLayout();
    }

    private void onLayoutContent() {
        mMatrixUtil.decomposeTSR(mTransform);
        Log.d("EDIT", "Content " + mMatrixUtil.toString());

        float hw = mMatrixUtil.getScaleX() * getContentWidth() / 2;
        float hh = mMatrixUtil.getScaleY() * getContentHeight() / 2;

        float[] center = getCenter();
        float cx = center[0];
        float cy = center[1];

        getContentView().setRotation(mMatrixUtil.getRotationDeg());

        getContentView().layout((int) (cx - hw), (int) (cy - hh), (int) (cx + hw),
                                (int) (cy + hh));
    }

    @Nullable
    public float[] getCenter() {
        int w = getWidth();
        int h = getHeight();

        if (w == 0 || h == 0) {
            return null;
        }
        float[] center = new float[2];
        center[0] = getWidth() / 2 + mMatrixUtil.getTranslateX();
        center[1] = getHeight() / 2 + mMatrixUtil.getTranslateY();
        return center;
    }

    public void reset() {
        mTransform.reset();
        mTransformMode = MODE_DEVICE;

        invalidateTransform();
    }

    private void commit() {
        if (mListener != null) {
            mListener.onChange(BaseAliyunPasterView.this);
        }
    }

    public boolean contentContains(float x, float y) {
        fromEditorToContent(x, y);

        float ix = POINT_LIST[0];
        float iy = POINT_LIST[1];

        boolean isContains;
        isContains = Math.abs(ix) <= getContentWidth() / 2
                     && Math.abs(iy) <= getContentHeight() / 2;
        return isContains;

    }

    private Matrix converge() {
        return mTransform;
    }


    /**
     * from Editor coordinates (lt as (0, 0)) to Content coordinates (center as
     * (0, 0))
     */
    protected final void fromEditorToContent(float x, float y) {
        if (mInverseTransformInvalidated) {
            Matrix together = converge();
            together.invert(mInverseTransform);
            mInverseTransformInvalidated = false;
        }
        POINT_LIST[2] = x - width / 2;
        POINT_LIST[3] = y - height / 2;

        mInverseTransform.mapPoints(POINT_LIST, 0, POINT_LIST, 2, 1);
    }

    protected final void fromContentToEditor(float x, float y) {
        POINT_LIST[2] = x;
        POINT_LIST[3] = y;

        mTransform.mapPoints(POINT_LIST, 0, POINT_LIST, 2, 1);

        POINT_LIST[0] += width / 2;
        POINT_LIST[1] += height / 2;
    }

    // shared, ui thread only

    protected final MatrixUtil mMatrixUtil = new MatrixUtil();

    private static final float[] POINT_LIST = new float[8];

    public void moveContent(float dx, float dy) {
        mTransform.postTranslate(dx, dy);

        invalidateTransform();
    }

    public void scaleContent(float sx, float sy) {
        Matrix m = new Matrix();
        m.set(mTransform);
        m.preScale(sx, sy);

        mMatrixUtil.decomposeTSR(m);
        int width = (int) (mMatrixUtil.getScaleX() * getContentWidth());
        int height = (int) (mMatrixUtil.getScaleY() * getContentHeight());

        float thold = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        Point screenPoint = ScreenUtils.getScreenPoint(getContext());
        thold = Math.max(screenPoint.x, screenPoint.y);
        float minScale = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        if ((width >= thold || height >= thold)
                && sx > 1) {
        } else if ((width <= minScale || height <= minScale) && sx < 1) {
        } else {
            mTransform.set(m);
        }

        invalidateTransform();
    }

    public void rotateContent(float rot) {
        mTransform.postRotate((float) Math.toDegrees(rot));

        mMatrixUtil.decomposeTSR(mTransform);
        invalidateTransform();
    }
    public void rotateContent(float rot,float x,float y) {
        mTransform.postRotate((float) Math.toDegrees(rot),x,y);
        mMatrixUtil.decomposeTSR(mTransform);
        invalidateTransform();
    }
    public Matrix getTransform() {
        return mTransform;
    }
}
