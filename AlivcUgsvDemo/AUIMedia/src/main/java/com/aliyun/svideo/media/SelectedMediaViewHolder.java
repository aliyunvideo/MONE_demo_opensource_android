/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.media;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class SelectedMediaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static String sDurationFormat = null;

    private ImageView mIvPhoto;
    private ImageView mIvDelete;
    private TextView mTvDuration;
    private int mPosition;
    private OnItemCallback mCallback;
    private MediaImageLoader mMediaImageLoader;


    public SelectedMediaViewHolder(View itemView,
                                   ImageView ivPhoto,
                                   ImageView ivDelete,
                                   TextView tvDuration,
                                   MediaImageLoader imageLoader) {
        super(itemView);
        this.mIvPhoto = ivPhoto;
        this.mIvDelete = ivDelete;
        this.mTvDuration = tvDuration;
        ivPhoto.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        this.mMediaImageLoader = imageLoader;
        if (sDurationFormat == null) {
            sDurationFormat = itemView.getResources().getString(R.string.alivc_media_video_duration);
        }
    }


    public void updateData(int position, MediaInfo info) {
        this.mPosition = position;
        if (info != null) {
            mMediaImageLoader.displayImage(info, mIvPhoto);
            int sec = Math.round(((float) info.duration) / 1000);
            int hour = sec / 3600;
            int min = (sec % 3600) / 60;
            sec = (sec % 60);
            mTvDuration.setText(String.format(sDurationFormat,
                                              hour,
                                              min,
                                              sec));
        }
    }

    public void updatePosition(int position) {
        this.mPosition = position;
    }

    @Override
    public void onClick(View v) {
        if (mCallback != null) {
            if (v == mIvPhoto) {
                mCallback.onPhotoClick(this, mPosition);
            } else if (v == mIvDelete) {
                mCallback.onItemDelete(this, mPosition);
            }
        }
    }

    public void setCallback(OnItemCallback callback) {
        mCallback = callback;
    }

    interface OnItemCallback {
        void onPhotoClick(SelectedMediaViewHolder holder, int position);

        void onItemDelete(SelectedMediaViewHolder holder, int position);
    }

}
