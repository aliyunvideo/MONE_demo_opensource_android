package com.aliyun.svideo.template.sample.ui.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.aliyun.svideosdk.template.utils.AffineTransform;

public class TemplateVideoView extends VideoView {

    public static final int FIT_WIDTH = 0;
    public static final int FIT_HEIGHT = 1;
    private int mFitMod = FIT_WIDTH;

    private int mTemplateWidth;
    private int mTemplateHeight;

    private int mVideoWidth;
    private int mVideoHeight;

    private MediaPlayer mMediaPlayer;
    private OnGetSizeListener mOnGetSizeListener;
    private boolean mMute;

    public TemplateVideoView(Context context) {
        super(context);
        init();
    }

    public TemplateVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TemplateVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer = mp;
                mVideoWidth = mp.getVideoWidth();
                mVideoHeight = mp.getVideoHeight();
                if (mOnGetSizeListener != null) {
                    mOnGetSizeListener.onGetVideoInfo(mVideoWidth, mVideoHeight, mp.getDuration());
                }

                start();
                mp.setLooping(true);
            }
        });
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            if (mFitMod == FIT_WIDTH) {
                height = mVideoHeight * width / mVideoWidth;
            } else {
                width = mVideoWidth * height / mVideoHeight;
            }

            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public float getScaleFactor() {
        if (mFitMod == FIT_WIDTH) {
            return (float) mTemplateWidth / mVideoWidth;
        } else {
            return (float) mTemplateHeight / mVideoHeight;
        }
    }

    public Matrix getMatrix() {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.set(
                new PointF(mVideoWidth / 2f, mVideoHeight / 2f),
                new PointF(mTemplateWidth / 2f, mTemplateHeight / 2f),
                new PointF(getScaleFactor(), getScaleFactor()),
                0);
        return affineTransform.getMatrix();
    }

    public void setTemplateWidthAndHeight(int templateWidth, int templateHeight) {
        mTemplateWidth = templateWidth;
        mTemplateHeight = templateHeight;
    }

    public int getFitMod() {
        return mFitMod;
    }

    public void setFitMod(int fitMod) {
        mFitMod = fitMod;
        requestLayout();
    }

    public void setMute(boolean mute) {
        mMute = mute;
        if (mMediaPlayer != null) {
            if (mute) {
                mMediaPlayer.setVolume(0, 0);
            } else {
                mMediaPlayer.setVolume(1, 1);
            }
        }
    }

    public boolean isMute() {
        return mMute;
    }

    public interface OnGetSizeListener {
        void onGetVideoInfo(int width, int height, int duration);
    }

    public void onGetViewSize(OnGetSizeListener onGetSizeListener) {
        mOnGetSizeListener = onGetSizeListener;
    }
}
