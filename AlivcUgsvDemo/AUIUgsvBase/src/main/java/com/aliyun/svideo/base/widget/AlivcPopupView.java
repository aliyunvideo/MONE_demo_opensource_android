package com.aliyun.svideo.base.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.LayoutRes;
import androidx.core.view.ViewCompat;

import com.aliyun.svideo.base.R;
import com.aliyun.common.global.AliyunTag;

/**
 * 提供一个浮层，支持自定义浮层的内容，支持在指定的方向旁边展示该浮层
 * @author Mulberry
 *         create on 2018/7/24.
 */
public class AlivcPopupView {
    private Context context;
    protected PopupWindow popupWindow;
    protected ContentView contentView;
    protected View rootView;
    protected ImageView mArrowDown;
    protected int mWindowHeight = 0;
    protected int mWindowWidth = 0;
    protected int mX = -1;
    protected int mY = -1;
    protected int mArrowCenter;
    protected WindowManager mWindowManager;
    protected Point mScreenSize = new Point();

    public AlivcPopupView(Context  mContext) {
        context = mContext;
        this.popupWindow = new PopupWindow(context);

        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }


    public final void show(View view) {
        show(view, view);
    }

    public final void show(View parent, View anchorView) {
        Log.d(AliyunTag.TAG, "yds-----show");
        if (!ViewCompat.isAttachedToWindow(anchorView)) {
            return;
        }
        onShowConfig();

        if (mWindowWidth == 0 || mWindowHeight == 0) {
            measureWindowSize();
        }
        Point point = onShowBegin(anchorView);

        /**
         * 指定popupWindow的位置
         */
        popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, point.x, point.y);

        // 在相关的View被移除时，window也自动移除。避免当Fragment退出后，Fragment中弹出的PopupWindow还存在于界面上。
        parent.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                Log.d(AliyunTag.TAG, "yds--------onViewAttachedToWindow");
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                Log.d(AliyunTag.TAG, "yds--------onViewDetachedFromWindow");
                if (popupWindow != null && popupWindow.isShowing()) {
                    Log.d(AliyunTag.TAG, "yds--------onViewDetachedFromWindow-----dismiss");
                    popupWindow.dismiss();
                }
            }
        });
    }

    protected void onShowConfig() {
        if (contentView == null) {
            throw new IllegalStateException("setContentView was not called with a view to display.");
        }

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        popupWindow.setContentView(contentView);

        Display screenDisplay = mWindowManager.getDefaultDisplay();
        screenDisplay.getSize(mScreenSize);
    }

    private void measureWindowSize() {
        int widthMeasureSpec = makeWidthMeasureSpec();
        int heightMeasureSpec = makeHeightMeasureSpec();
        rootView.measure(widthMeasureSpec, heightMeasureSpec);
        mWindowWidth = rootView.getMeasuredWidth();
        mWindowHeight = rootView.getMeasuredHeight();
    }

    protected int makeWidthMeasureSpec() {
        return View.MeasureSpec.makeMeasureSpec(getWidth(context), View.MeasureSpec.AT_MOST);
    }

    protected int makeHeightMeasureSpec() {
        return View.MeasureSpec.makeMeasureSpec(getHeight(context), View.MeasureSpec.AT_MOST);
    }

    /**
     * 获取宽度
     *
     * @param mContext 上下文
     * @return 宽度值，px
     */
    public static int getWidth(Context mContext) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE))
        .getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 获取高度
     *
     * @param mContext 上下文
     * @return 高度值，px
     */
    public static int getHeight(Context mContext) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE))
        .getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public Point onShowBegin(View attachedView) {
        //计算位置
        calculatePosition(attachedView);

        showArrow();
        return new Point(mX, mY);
    }

    /**
     * 根据需要显示的View计算位置
     * @param anchorView
     */
    private void calculatePosition(View anchorView) {
        if (anchorView == null) {
            throw new IllegalStateException("setContentView was not called with a view to display.");
        }

        if (anchorView != null) {
            int[] attachedViewLocation = new int[2];
            anchorView.getLocationInWindow(attachedViewLocation);
            mArrowCenter = attachedViewLocation[0] + anchorView.getWidth() / 2;
            if (mArrowCenter < mScreenSize.x / 2) {//描点在左侧
                mX = mArrowCenter - mWindowWidth / 2;
            } else {//描点在右侧
                mX = mArrowCenter - mWindowWidth / 2;
            }

            mY = attachedViewLocation[1] - mWindowHeight;

        }
    }

    /**
     * 显示箭头（上/下）
     */
    private void showArrow() {
        final int arrowWidth = mArrowDown.getMeasuredWidth();
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) mArrowDown.getLayoutParams();
        param.leftMargin = mArrowCenter - mX + arrowWidth / 2;
    }

    public void setContentView(View root) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context)
                              .inflate(getRootLayout(), null, false);
        mArrowDown = (ImageView) layout.findViewById(R.id.arrow_down);
        FrameLayout content = (FrameLayout) layout.findViewById(R.id.content_layout);
        content.addView(root);

        if (root == null) {
            throw new IllegalStateException("call setContentView view can not be null");
        }

        contentView = new ContentView(context);
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rootView = layout;
        contentView.addView(layout);
        popupWindow.setContentView(contentView);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                AlivcPopupView.this.popupWindow.dismiss();
            }
        });
    }

    /**
     * the root layout: must provide ids: arrow_down(ImageView), arrow_up(ImageView), box(FrameLayout)
     *
     * @return
     */
    @LayoutRes
    protected int getRootLayout() {
        return R.layout.alivc_popup_layout;
    }

    public ViewGroup.LayoutParams generateLayoutParam(int width, int height) {
        return new FrameLayout.LayoutParams(width, height);
    }

    public class ContentView extends ViewGroup {
        public ContentView(Context context) {
            super(context);
        }

        public ContentView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onConfigurationChanged(Configuration newConfig) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }

        @Override
        public void addView(View child) {
            if (getChildCount() > 0) {
                throw new RuntimeException("only support one child");
            }
            super.addView(child);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (getChildCount() == 0) {
                setMeasuredDimension(0, 0);
            }
            int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);
            widthMeasureSpec = makeWidthMeasureSpec();
            heightMeasureSpec = makeHeightMeasureSpec();
            int targetHeightSize = MeasureSpec.getSize(heightMeasureSpec);
            int targetHeightMode = MeasureSpec.getMode(heightMeasureSpec);
            if (parentHeightSize < targetHeightSize) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeightSize, targetHeightMode);
            }
            View child = getChildAt(0);
            child.measure(widthMeasureSpec, heightMeasureSpec);
            mWindowWidth = child.getMeasuredWidth();
            mWindowHeight = child.getMeasuredHeight();
            setMeasuredDimension(mWindowWidth, mWindowHeight);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (getChildCount() == 0) {
                return;
            }
            View child = getChildAt(0);
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }
}
