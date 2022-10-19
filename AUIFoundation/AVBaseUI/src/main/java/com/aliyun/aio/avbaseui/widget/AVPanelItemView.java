package com.aliyun.aio.avbaseui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.aliyun.aio.avbaseui.R;

/**
 * @author geekeraven
 */
public class AVPanelItemView extends LinearLayout {

    private ImageView mImageView;
    private TextView mTextView;

    private int mDrawableIDEnable;
    private int mDrawableIDDisable;

    private int mTextColorEnable;
    private int mTextColorDisable;

    public AVPanelItemView(Context context) {
        this(context, null);
    }

    public AVPanelItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AVPanelItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.av_panel_item_view, this, true);
        mImageView = findViewById(R.id.av_panel_item_image);
        mTextView = findViewById(R.id.av_panel_item_text);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AVPanelItemView);
        if (ta == null) {
            return;
        }
        mTextColorEnable = ta.getColor(R.styleable.AVPanelItemView_textColorEnable, context.getResources().getColor(R.color.text_strong));
        mTextColorDisable = ta.getColor(R.styleable.AVPanelItemView_textColorDisable, context.getResources().getColor(R.color.text_weak));

        mDrawableIDEnable = ta.getResourceId(R.styleable.AVPanelItemView_imageEnable, 0);
        mDrawableIDDisable = ta.getResourceId(R.styleable.AVPanelItemView_imageDisable, 0);

        int textId = ta.getResourceId(R.styleable.AVPanelItemView_itemName, 0);
        mTextView.setText(textId);

        boolean isEnable = ta.getBoolean(R.styleable.AVPanelItemView_statusEnable, true);
        if (isEnable) {
            setStatusEnable();
        } else {
            setStatusDisable();
        }
    }

    public void setStatusEnable() {
        mTextView.setTextColor(mTextColorEnable);
        mImageView.setImageResource(mDrawableIDEnable);
    }

    public void setStatusDisable() {
        mTextView.setTextColor(mTextColorDisable);
        mImageView.setImageResource(mDrawableIDDisable);
    }
}
