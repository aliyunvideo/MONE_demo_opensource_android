/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.media;

import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Administrator on 2016/5/18.
 */
public class GalleryMediaChooser {

    private RecyclerView mGallery;
    private GalleryAdapter adapter;
    private MediaStorage mStorage;

    public GalleryMediaChooser(RecyclerView gallery,
                               final GalleryDirChooser dirChooser,
                               MediaStorage storage, ThumbnailGenerator thumbnailGenerator) {
        this.mGallery = gallery;
        mGallery.addItemDecoration(new GalleryItemDecoration());
        this.mStorage = storage;
        adapter = new GalleryAdapter(thumbnailGenerator);
        gallery.setLayoutManager(new WrapContentGridLayoutManager(gallery.getContext(),
                                 4, GridLayoutManager.VERTICAL, false));
        gallery.setAdapter(adapter);
        adapter.setData(storage.getMedias());
        storage.setOnMediaDataUpdateListener(new MediaStorage.OnMediaDataUpdate() {
            @Override
            public void onDataUpdate(List<MediaInfo> list) {
                int count = adapter.getItemCount();
                int size = list.size();
                int insert = count - size;
                adapter.notifyItemRangeInserted(insert, size);

                if (size == MediaStorage.FIRST_NOTIFY_SIZE
                        || mStorage.getMedias().size() < MediaStorage.FIRST_NOTIFY_SIZE) {
                    selectedFirstMediaOnAll(list);
                }
                dirChooser.setAllGalleryCount(mStorage.getMedias().size());

            }
        });

        adapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(GalleryAdapter adapter, int adapterPosition) {
                if (adapter.getItemCount() > adapterPosition) {
                    MediaInfo info = adapter.getItem(adapterPosition);
                    if (info != null) {
                        mStorage.setCurrentDisplayMediaData(info);
                    }
                }

                return true;
            }
        });



        mGallery.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                GridLayoutManager layout = (GridLayoutManager) recyclerView.getLayoutManager();
                int first = layout.findFirstCompletelyVisibleItemPosition();

            }
        });

        mGallery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });
    }

    public void setCurrentMediaInfoActived() {
        int pos = adapter.setActiveDataItem(mStorage.getCurrentMedia());
        mGallery.smoothScrollToPosition(pos);
    }


    private void selectedFirstMediaOnAll(List<MediaInfo> list) {
        if (list.size() == 0) {
            return ;
        }
        MediaInfo info = list.get(0);
        adapter.setActiveDataItem(info);
    }

    public void changeMediaDir(MediaDir dir) {
        if (dir.id == -1) {
            adapter.setData(mStorage.getMedias());
            selectedFirstMediaOnAll(mStorage.getMedias());
        } else {
            adapter.setData(mStorage.findMediaByDir(dir));
            selectedFirstMediaOnAll(mStorage.findMediaByDir(dir));
        }
    }

    public void setMinDuration(long minDuration) {
        adapter.setMinDuration(minDuration);
    }

}
