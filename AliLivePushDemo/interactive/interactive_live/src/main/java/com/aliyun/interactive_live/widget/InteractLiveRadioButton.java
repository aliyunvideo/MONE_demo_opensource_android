package com.aliyun.interactive_live.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.aliyun.interactive_live.R;

public class InteractLiveRadioButton extends ConstraintLayout {

    private final Context mContext;
    private ConstraintLayout mConstraintLayoutRootView;
    private TextView mTitleTextView;

    public InteractLiveRadioButton(Context context) {
        this(context,null);
    }

    public InteractLiveRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public InteractLiveRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
        initAttribute(attrs);
    }

    private void initView(){
        View mRootView = LayoutInflater.from(mContext).inflate(R.layout.layout_interact_live_radio_button, this, true);

        mConstraintLayoutRootView = mRootView.findViewById(R.id.root);
        mTitleTextView = mRootView.findViewById(R.id.tv_title);
    }

    private void initAttribute(AttributeSet attrs){
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.InteractLiveRadioButton);
        String mText = typedArray.getString(R.styleable.InteractLiveRadioButton_ilrb_text);
        typedArray.recycle();

        mTitleTextView.setText(mText);
    }

    public void setChecked(boolean isChecked){
        if(isChecked){
            mConstraintLayoutRootView.setBackground(getResources().getDrawable(R.drawable.shape_interact_live_selected));
        }else{
            mConstraintLayoutRootView.setBackground(getResources().getDrawable(R.drawable.shape_interact_live_unselected));
        }
    }
}
