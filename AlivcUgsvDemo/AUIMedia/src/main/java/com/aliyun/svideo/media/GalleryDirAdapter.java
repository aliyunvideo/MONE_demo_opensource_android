/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.media;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GalleryDirAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements View.OnClickListener {

    public interface OnItemClickListener {
        /**
         * item click listener
         * @param adapter GalleryAdapter
         * @param adapterPosition adapterPosition
         * @return
         */
        boolean onItemClick(GalleryDirAdapter adapter, int adapterPosition);
    }

    private ThumbnailGenerator thumbnailGenerator;

    public GalleryDirAdapter(ThumbnailGenerator thumbnailGenerator) {
        this.thumbnailGenerator = thumbnailGenerator;
    }

    private List<MediaDir> mediaDirs;
    private int allFileCount;
    private OnItemClickListener onItemClickListener;

    public void setData(List<MediaDir> mediaDirs) {
        this.mediaDirs = mediaDirs;
        notifyDataSetChanged();
    }

    public void setAllFileCount(int allFileCount) {
        this.allFileCount = allFileCount;
        notifyItemChanged(0);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = new GalleryDirViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(
                R.layout.alivc_media_item_gallery_dir, null, false), thumbnailGenerator);

        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((GalleryDirViewHolder)holder).setData(getItem(position));
        if (position == 0) {
            ((GalleryDirViewHolder)holder).setFileCountWhenCompletion(allFileCount);
        }
    }

    @Override
    public int getItemCount() {
        return mediaDirs.size();
    }

    public MediaDir getItem(int position) {

        return mediaDirs.get(position);
    }

    @Override
    public void onClick(View v) {
        RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
        int adapterPos = holder.getAdapterPosition();

        if (onItemClickListener != null) {
            Log.d("active", "onItemClick");
            if (adapterPos == -1) {
                return ;
            }
            if (!onItemClickListener.onItemClick(this, adapterPos)) {
                Log.d("active", "onItemClick1");
            }
        }
    }

}
