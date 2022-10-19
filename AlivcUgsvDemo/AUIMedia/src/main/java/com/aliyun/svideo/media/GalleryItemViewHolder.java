/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.media;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.ugsv.common.utils.image.ImageLoaderImpl;
import com.aliyun.ugsv.common.utils.image.ImageLoaderOptions;

import java.io.File;

/**
 * Created by Administrator on 2016/5/18.
 */
public class GalleryItemViewHolder extends RecyclerView.ViewHolder {

    private ImageView thumbImage;
    private TextView duration;
    private View durationLayoput;
    private ImageView mIvMask;
    private ThumbnailGenerator thumbnailGenerator;
    private int mScreenWidth;

    public GalleryItemViewHolder(View itemView, ThumbnailGenerator thumbnailGenerator) {
        super(itemView);
        DisplayMetrics displayMetrics = itemView.getContext().getResources().getDisplayMetrics();
        mScreenWidth = displayMetrics.widthPixels;
        this.thumbnailGenerator = thumbnailGenerator;
        thumbImage = (ImageView) itemView.findViewById(R.id.draft_thumbnail);
        duration = (TextView) itemView.findViewById(R.id.draft_duration);
        durationLayoput = itemView.findViewById(R.id.duration_layoput);
        mIvMask = (ImageView) itemView.findViewById(R.id.iv_mask);

        itemView.setTag(this);
    }


    public void setData(final MediaInfo info, long minDuration) {
        if (info == null) {
            return;
        }
        //每一个imageView都需要设置tag，video异步生成缩略图，需要对应最后设置给imageView的info key
        thumbImage.setTag(R.id.tag_first, ThumbnailGenerator.generateKey(info.type, info.id));
        mIvMask.setVisibility(View.GONE);
        if (info.thumbnailPath != null
                && onCheckFileExists(info.thumbnailPath)) {
            String uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri = info.fileUri;
            } else {
                uri = "file://" + info.thumbnailPath;
            }
            new ImageLoaderImpl().loadImage(thumbImage.getContext(), uri,
                                            new ImageLoaderOptions.Builder().override(mScreenWidth / 5, mScreenWidth / 5)
                                            .skipMemoryCache()
                                            .placeholder(new ColorDrawable(Color.GRAY))
                                            .build()
                                           ).into(thumbImage);
        } else if (info.type == MediaStorage.TYPE_PHOTO) {
            String uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri = info.fileUri;
            } else {
                uri = "file://" + info.filePath;
            }
            new ImageLoaderImpl().loadImage(thumbImage.getContext(), uri,
                                            new ImageLoaderOptions.Builder().override(mScreenWidth / 5, mScreenWidth / 5)
                                            .skipMemoryCache()
                                            .placeholder(new ColorDrawable(Color.GRAY))
                                            .build()
                                           ).into(thumbImage);
        } else {
            thumbImage.setImageDrawable(new ColorDrawable(Color.GRAY));
            thumbnailGenerator.generateThumbnail(info.type, info.id, 0,
            new ThumbnailGenerator.OnThumbnailGenerateListener() {
                @Override
                public void onThumbnailGenerate(int key, Bitmap thumbnail) {
                    if (key == (Integer)(thumbImage.getTag(R.id.tag_first))) {
                        thumbImage.setImageBitmap(thumbnail);
                    }
                }
            });
        }

        int du = info.duration;
        if (du == 0) {
            durationLayoput.setVisibility(View.GONE);
        } else {
            if (minDuration != -1 && info.type != MediaStorage.TYPE_PHOTO && du < minDuration) {
                mIvMask.setVisibility(View.VISIBLE);
            }
            durationLayoput.setVisibility(View.VISIBLE);
            onMetaDataUpdate(duration, du);
        }

    }

    public void onBind(MediaInfo info, boolean actived, long minDuration) {
        setData(info, minDuration);
        itemView.setActivated(actived);
    }

    private boolean onCheckFileExists(String path) {
        boolean res = false;
        if (path == null) {
            return false;
        }

        File file = new File(path);
        if (file.exists()) {
            res = true;
        }

        return res;
    }


    private void onMetaDataUpdate(TextView view, int duration) {
        if (duration == 0) {
            return;
        }

        int sec = Math.round((float) duration / 1000);
        int min = sec / 60;
        sec %= 60;

        view.setText(String.format(String.format("%d:%02d", min, sec)));
    }

}
