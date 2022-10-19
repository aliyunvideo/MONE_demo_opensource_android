package com.aliyun.aio.avbaseui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.aio.avbaseui.R;


public class AVActionBar extends RelativeLayout {

    private ImageView mLeftImageView;
    private ImageView mRightImageView;
    private TextView mTitleView;


    public AVActionBar(Context context) {
        this(context, null);
    }

    public AVActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AVActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.av_actionbar_layout, this, true);
        mLeftImageView = findViewById(R.id.av_actionbar_left_image);
        mRightImageView = findViewById(R.id.av_actionbar_right_image);
        mTitleView = findViewById(R.id.av_actionbar_title);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.av_actionbar);
        if (ta == null) {
            return;
        }

        boolean isShowTitle = ta.getBoolean(R.styleable.av_actionbar_showTitle, false);
        mTitleView.setVisibility(isShowTitle ? VISIBLE : GONE);
        String title = ta.getString(R.styleable.av_actionbar_title);
        mTitleView.setText(title);

        boolean isShowLeftImage = ta.getBoolean(R.styleable.av_actionbar_showLeftView, false);
        mLeftImageView.setVisibility(isShowLeftImage ? VISIBLE : GONE);
        int leftBackground = ta.getResourceId(R.styleable.av_actionbar_leftImageBackground, 0);
        if (leftBackground != 0) {
            mLeftImageView.setBackgroundResource(leftBackground);
        }
        int leftImage = ta.getResourceId(R.styleable.av_actionbar_leftImageSrc, 0);
        if (leftImage != 0) {
            mLeftImageView.setImageResource(leftImage);
        }


        boolean isShowRightImage = ta.getBoolean(R.styleable.av_actionbar_showRightView, false);
        mRightImageView.setVisibility(isShowRightImage ? VISIBLE : GONE);
        int rightBackground = ta.getResourceId(R.styleable.av_actionbar_rightImageBackground, 0);
        if (rightBackground != 0) {
            mRightImageView.setBackgroundResource(rightBackground);
        }
        int rightImage = ta.getResourceId(R.styleable.av_actionbar_rightImageSrc, 0);
        if (rightImage != 0) {
            mRightImageView.setImageResource(rightImage);
        }
    }

    public void showLeftView() {
        mLeftImageView.setVisibility(VISIBLE);
    }

    public void hideLeftView() {
        mLeftImageView.setVisibility(GONE);
    }

    public void showRightView() {
        mRightImageView.setVisibility(VISIBLE);
    }

    public void hideRightView() {
        mRightImageView.setVisibility(GONE);
    }

    public void showTitleView() {
        mTitleView.setVisibility(VISIBLE);
    }

    public void hideTitleView() {
        mTitleView.setVisibility(GONE);
    }

    public ImageView getLeftImageView() {
        return mLeftImageView;
    }

    public ImageView getRightImageView() {
        return mRightImageView;
    }

    public TextView getTitleView() {
        return mTitleView;
    }

}
