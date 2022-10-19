package com.aliyun.player.alivcplayerexpand.view.thumbnail;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.player.alivcplayerexpand.R;


/**
 * 缩略图View
 */
public class ThumbnailView extends LinearLayout {

    /**
     * 缩略图 time
     */
    private TextView mPositionTextView;
    /**
     * 视频总时长
     */
    private TextView mVideoDurationTextView;
    /**
     * 缩略图 picture
     */
    private ImageView mThumbnailImageView;

    public ThumbnailView(Context context) {
        super(context);
        init();
    }

    public ThumbnailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThumbnailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //Inflate布局
        LayoutInflater.from(getContext()).inflate(R.layout.alivc_view_thumbnail, this, true);
        findAllViews();

    }

    private void findAllViews() {
        mPositionTextView = findViewById(R.id.seek_play_duration_tv);
        mThumbnailImageView = findViewById(R.id.iv_thumbnail);
        mVideoDurationTextView = findViewById(R.id.seek_video_duration_tv);
    }

    public void setTime(String playTime, String totalTime) {
        mPositionTextView.setText(playTime);
        mVideoDurationTextView.setText(totalTime);
    }

    public void setThumbnailPicture(Bitmap bitmap) {
        mThumbnailImageView.setImageBitmap(bitmap);
    }

    public void showThumbnailView() {
        setVisibility(View.VISIBLE);
    }

    public void hideThumbnailView() {
        setVisibility(View.GONE);
    }

    public ImageView getThumbnailImageView() {
        return mThumbnailImageView;
    }
}
