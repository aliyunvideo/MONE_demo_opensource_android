package com.aliyun.svideo.track.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.svideo.track.R;
import com.aliyun.svideo.track.api.TrackConfig;
import com.aliyun.svideo.track.bean.BaseClipInfo;
import com.aliyun.svideo.track.bean.CaptionClipInfo;
import com.aliyun.svideo.track.bean.EffectClipInfo;

public class SubClipLayout extends BaseClipLayout {
    public SubClipLayout(Context context) {
        this(context, null);
    }

    public SubClipLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubClipLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getTrackClipInfo() != null) {
            ViewGroup.LayoutParams contentLayoutParams = mContentView.getLayoutParams();
            contentLayoutParams.width = Math.round(getTrackClipInfo().getClipDuration() * TrackConfig.getPxUnit(mTimelineScale));
            mContentView.setLayoutParams(contentLayoutParams);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
            layoutParams.leftMargin = Math.round(getTrackClipInfo().getTimelineIn() * TrackConfig.getPxUnit(mTimelineScale));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void inflateContentView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_sub_clip, this);
    }

    @Override
    public View findContentView() {
        return findViewById(R.id.mContentView);
    }

    @Override
    public void setData(BaseClipInfo clipInfo) {
        super.setData(clipInfo);
        if (clipInfo instanceof CaptionClipInfo) {
            setClipText(((CaptionClipInfo) clipInfo).getText());
        } else if (clipInfo instanceof EffectClipInfo) {
            setClipText(((EffectClipInfo) clipInfo).getText());
        }
    }

    @Override
    public void setMarkImage(Bitmap bitmap) {
        ((ImageView) findViewById(R.id.iv_mark)).setImageBitmap(bitmap);
    }

    @Override
    public void setClipText(String text) {
        if (getTrackClipInfo() instanceof CaptionClipInfo) {
            ((CaptionClipInfo) getTrackClipInfo()).setText(text);
        }
        ((TextView) findViewById(R.id.tv_tip)).setText(text);
    }
}
