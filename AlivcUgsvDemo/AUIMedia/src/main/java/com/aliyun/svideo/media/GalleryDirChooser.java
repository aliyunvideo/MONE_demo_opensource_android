/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.media;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;


public class GalleryDirChooser {

    private PopupWindow popupWindow;
    private View anchor;
    private boolean isShowGalleryDir;
    private GalleryDirAdapter adapter;
    private Activity mActivity;

    public GalleryDirChooser(Context context, View anchor,
                             ThumbnailGenerator thumbnailGenerator,
                             final MediaStorage storage) {
        this.anchor = anchor;
        this.mActivity = (Activity) context;
        RecyclerView recyclerView = (RecyclerView)View.inflate(context,
                                    R.layout.alivc_meida_ppw_container_gallery_dir, null);
        adapter = new GalleryDirAdapter(thumbnailGenerator);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        adapter.setData(storage.getDirs());
        popupWindow = new PopupWindow(recyclerView,
                                      WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(
                                              context.getResources().getColor(R.color.alivc_common_bg_white)));
        popupWindow.setOutsideTouchable(true);

        anchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storage.isActive()) {
                    showOrHideGalleryDir();
                }
            }
        });

        storage.setOnMediaDirUpdateListener(
        new MediaStorage.OnMediaDirUpdate() {
            @Override
            public void onDirUpdate(MediaDir dir) {
                GalleryDirChooser.this.anchor.post(
                new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                }
                );

            }
        }
        );

        adapter.setOnItemClickListener(new GalleryDirAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(GalleryDirAdapter adapter, int adapterPosition) {
                MediaDir dir = adapter.getItem(adapterPosition);
                showOrHideGalleryDir();
                storage.setCurrentDir(dir);
                return false;
            }
        });
    }

    public void setAllGalleryCount(int count) {
        adapter.setAllFileCount(count);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showOrHideGalleryDir() {
        if (mActivity.isDestroyed()) {
            return;
        }
        if (isShowGalleryDir) {
            popupWindow.dismiss();
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                popupWindow.showAsDropDown(anchor);
            } else {
                // 适配 android 7.0
                Rect visibleFrame = new Rect();
                anchor.getGlobalVisibleRect(visibleFrame);
                int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
                popupWindow.setHeight(height);
                popupWindow.showAsDropDown(anchor, 0, 0);

            }
        }
        isShowGalleryDir = !isShowGalleryDir;
        anchor.setActivated(isShowGalleryDir);
    }

    public boolean isShowGalleryDir() {
        return isShowGalleryDir;
    }

}
