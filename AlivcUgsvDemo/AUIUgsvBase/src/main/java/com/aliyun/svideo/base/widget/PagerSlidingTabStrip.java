/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aliyun.svideo.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.GestureDetectorCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.aliyun.svideo.base.R;
import com.aliyun.common.utils.DensityUtil;

import java.util.Locale;

public class PagerSlidingTabStrip extends HorizontalScrollView {

    public interface IconTabProvider {
        int getPageIconResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[] { android.R.attr.textSize,
            android.R.attr.textColor
                                                 };
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public ViewPager.OnPageChangeListener delegatePageListener;

    private TabClickListener tabClickListener;
    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int tabCount;
    /**
     * Only draw more than 1 tab
     */
    private static final int TAB_DIVIDER_LINT = 1;

    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    private int indicatorColor ;
    private int underlineColor ;
    private int dividerColor ;

    private boolean shouldExpand = false;
    private boolean textAllCaps = true;

    private int scrollOffset = 52;
    private int indicatorHeight = 4;
    private int underlineHeight = 1;
    private int dividerPadding = 16;
    private int tabPadding = 2;
    private int dividerWidth = 0;

    private int tabTextSize = 36;
    private int tabTextColor;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;

    private int lastScrollX = 0;

    private int tabBackgroundResId;

    private int tabViewId;

    private Locale locale;
    private Context mContext;

    public PagerSlidingTabStrip(Context context) {
        this(context, null);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        mContext = context;
        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(
                                          LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(
                           TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(
                              TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(
                              TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(
                             TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(
                         TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(
                           TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(
                          TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
        tabTextColor = a.getColor(1, tabTextColor);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs,
                                           R.styleable.QuViewPagerSlidingTabStrip);

        indicatorColor = a.getColor(
                             R.styleable.QuViewPagerSlidingTabStrip_pstsIndicatorColor,
                             indicatorColor);
        underlineColor = a.getColor(
                             R.styleable.QuViewPagerSlidingTabStrip_pstsUnderlineColor,
                             underlineColor);
        dividerColor = a
                       .getColor(R.styleable.QuViewPagerSlidingTabStrip_pstsDividerColor,
                                 dividerColor);
        indicatorHeight = a.getDimensionPixelSize(
                              R.styleable.QuViewPagerSlidingTabStrip_pstsIndicatorHeight,
                              indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(
                              R.styleable.QuViewPagerSlidingTabStrip_pstsUnderlineHeight,
                              underlineHeight);
        dividerPadding = a.getDimensionPixelSize(
                             R.styleable.QuViewPagerSlidingTabStrip_pstsDividerPadding,
                             dividerPadding);
        tabPadding = a.getDimensionPixelSize(
                         R.styleable.QuViewPagerSlidingTabStrip_pstsTabPaddingLeftRight,
                         tabPadding);
        tabBackgroundResId = a.getResourceId(
                                 R.styleable.QuViewPagerSlidingTabStrip_pstsTabBackground,
                                 tabBackgroundResId);
        shouldExpand = a
                       .getBoolean(R.styleable.QuViewPagerSlidingTabStrip_pstsShouldExpand,
                                   shouldExpand);
        scrollOffset = a
                       .getDimensionPixelSize(
                           R.styleable.QuViewPagerSlidingTabStrip_pstsScrollOffset,
                           scrollOffset);
        textAllCaps = a.getBoolean(
                          R.styleable.QuViewPagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);

        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0,
                LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }



    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException(
                "ViewPager does not have adapter instance.");
        }
        pager.setOnPageChangeListener(pageListener);
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void setTabClickListener(TabClickListener tabClickListener) {
        this.tabClickListener = tabClickListener;
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();

        PagerAdapter adapter = pager.getAdapter();

        tabCount = adapter.getCount();

        for (int i = 0; i < tabCount; i++) {
            CharSequence title = pager.getAdapter().getPageTitle(i);
            if (pager.getAdapter() instanceof IconTabProvider) {
                addTabWithIcon(i, title,
                               ((IconTabProvider) pager.getAdapter())
                               .getPageIconResId(i));
            } else {
                addTextTab(i, title.toString());
            }

        }

        updateTabStyles(currentPosition);

//        getViewTreeObserver().addOnGlobalLayoutListener(
//                new OnGlobalLayoutListener() {
//
//                    @Override
//                    public void onGlobalLayout() {
//                        ViewTreeObserverUtil.removeOnGlobalLayoutListener(
//                                getViewTreeObserver(), this);
//
//                        currentPosition = pager.getCurrentItem();
//                        scrollToChild(currentPosition, 0);
//                    }
//                });

    }

    private void addTabWithIcon(int index, CharSequence text, int iconResId) {
        TabView tab = new TabView(tabsContainer, index);
        tab.title.setText(text);
        tab.title.setGravity(Gravity.CENTER);
        if (iconResId != 0) {
            tab.title.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0,
                    0);
            tab.title.setCompoundDrawablePadding(DensityUtil.dip2px(mContext, 5));
        }
        addTab(index, tab.getView());
    }

    private void addTextTab(final int position, String title) {

        // TextView tab = new TextView(getContext());
        // tab.setText(title);
        // tab.setGravity(Gravity.CENTER);
        // tab.setSingleLine();
        TabView tab = new TabView(tabsContainer, position);
        tab.title.setText(title);
        tab.title.setGravity(Gravity.CENTER);
        // tab.msgCount.setText(title);
        addTab(position, tab.getView());
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);
        addTab(position, tab);

    }

    private void   addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);
            }
        });

        tab.setPadding(tabPadding, 0, tabPadding, 0);
        tabsContainer
        .addView(tab, position, shouldExpand ? expandedTabLayoutParams
                 : defaultTabLayoutParams);
    }

    private void updateTabStyles(int pos) {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            v.setBackgroundResource(tabBackgroundResId);

            if (v instanceof TextView) {

                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);
                tab.setTextColor(tabTextColor);

                // setAllCaps() is only available from API 14, so the upper case
                // is made manually if we are on a
                // pre-ICS-build
                if (textAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString()
                                    .toUpperCase(locale));
                    }
                }
            } else {
                Object obj = v.getTag();
                if (obj != null && obj instanceof TabView) {
                    TabView tab = (TabView) obj;
                    // tab.title.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    // tabTextSize);
                    // tab.title.setTypeface(tabTypeface, tabTypefaceStyle);
                    if (i == pos) {
                        tab.title.setSelected(true);
                    } else {
                        tab.title.setSelected(false);
                    }

                    if (textAllCaps) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            tab.title.setAllCaps(true);
                        } else {
                            tab.title.setText(tab.title.getText().toString()
                                              .toUpperCase(locale));
                        }
                    }
                }
            }
        }

    }

    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount <= TAB_DIVIDER_LINT) {
            return;
        }

        final int height = getHeight();

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        final int tabPadding = currentTab.getWidth() / 3;
        float lineLeft = currentTab.getLeft() + tabPadding;
        float lineRight = currentTab.getRight()  - tabPadding;

        // if there is an offset, start interpolating left and right coordinates
        // between current and next tab
        float padding = 0;
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft + tabPadding);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight - tabPadding);
        }

        // draw underline

        rectPaint.setColor(underlineColor);
        //canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(),
        //        height, rectPaint);

        rectPaint.setColor(indicatorColor);
        canvas.drawRoundRect(new RectF(lineLeft + padding, height - indicatorHeight, lineRight - padding, height), 5f, 5f, rectPaint);
//        canvas.drawRect(lineLeft + padding, height - indicatorHeight, lineRight - padding, height,
//                rectPaint);

        // draw divider

        dividerPaint.setColor(dividerColor);

        for (int i = 0; i < tabCount - 1; i++) {
            View tab = tabsContainer.getChildAt(i);
            canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(),
                            height - dividerPadding, dividerPaint);
        }

    }

    private class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {

            currentPosition = position;
            currentPositionOffset = positionOffset;

            scrollToChild(position, (int) (positionOffset * tabsContainer
                                           .getChildAt(position).getWidth()));

            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset,
                                                    positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }

            updateTabStyles(position);
        }

    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles(currentPosition);
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles(currentPosition);
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles(currentPosition);
    }

    public int getTextColor() {
        return tabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles(currentPosition);
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles(currentPosition);
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }










    public interface TabClickListener {

        void onTabClickListener(int position);

        void onTabDoubleClickListener(int position);

    }

    public void setTabViewId(int id) {
        this.tabViewId = id;
    }

    public class TabView {

        private final TextView title;

        private final View _Root;


        public TabView(ViewGroup container, final int id) {
            _Root = View.inflate(container.getContext(),
                                 tabViewId, null);
            _Root.setTag(this);

            title = (TextView) _Root.findViewById(R.id.aliyun_tv_title);

            final GestureDetectorCompat gesture = new GestureDetectorCompat(
                _Root.getContext(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    // if(tabClickListener != null){
                    // tabClickListener.onTabDoubleClickListener(id);
                    // }
                    return super.onDoubleTap(e);
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    if (tabClickListener != null) {
                        Log.d("onSingleTapUp", "id" + id);
                        tabClickListener.onTabClickListener(id);
                    }
                    return super.onSingleTapUp(e);
                }

            });
            _Root.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gesture.onTouchEvent(event);
                    return false;
                }
            });
        }


        public View getView() {
            return _Root;
        }

    }

}
