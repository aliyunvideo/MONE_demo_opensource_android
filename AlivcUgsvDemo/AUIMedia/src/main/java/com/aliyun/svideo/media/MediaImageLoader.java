/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.widget.ImageView;
import com.aliyun.ugsv.common.utils.image.ImageLoaderImpl;

import java.io.File;

public class MediaImageLoader {
    private ThumbnailGenerator mThumbnailGenerator;
    public MediaImageLoader(Context context) {
        mThumbnailGenerator = new ThumbnailGenerator(context);
    }

    public void displayImage(final MediaInfo info, final ImageView view) {
        if (info.thumbnailPath != null
                && onCheckFileExistence(info.thumbnailPath)) {
            String uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri = info.fileUri;
            } else {
                uri = "file://" + info.thumbnailPath;
            }
            new ImageLoaderImpl().loadImage(view.getContext(), uri).into(view);
        } else {
            view.setImageDrawable(new ColorDrawable(Color.GRAY));
            mThumbnailGenerator.generateThumbnail(info.type, info.id, 0,
            new ThumbnailGenerator.OnThumbnailGenerateListener() {
                @Override
                public void onThumbnailGenerate(int key, Bitmap thumbnail) {
                    int currentKey = ThumbnailGenerator.generateKey(info.type, info.id);
                    if (key == currentKey) {
                        view.setImageBitmap(thumbnail);
                    }
                }
            });
        }
    }

    private boolean onCheckFileExistence(String path) {
        boolean res = false;
        if (path == null) {
            return res;
        }

        File file = new File(path);
        if (file.exists()) {
            res = true;
        }

        return res;
    }
}
