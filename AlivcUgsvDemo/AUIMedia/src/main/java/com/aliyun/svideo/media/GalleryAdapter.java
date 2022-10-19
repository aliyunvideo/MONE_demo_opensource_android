/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.media;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements View.OnClickListener {

    public interface OnItemClickListener {
        /**
         * item click listener
         * @param adapter GalleryAdapter
         * @param adapterPosition adapterPosition
         * @return
         */
        boolean onItemClick(GalleryAdapter adapter, int adapterPosition);
    }

    private static final int TYPE_ITEM_MEDIA = 1;
    private List<MediaInfo> medias;
    private ThumbnailGenerator thumbnailGenerator;
    private OnItemClickListener onItemClickListener;

    private long mMinDuration = -1;

    public GalleryAdapter(ThumbnailGenerator thumbnailGenerator) {
        this.thumbnailGenerator = thumbnailGenerator;
    }

    public void setData(List<MediaInfo> list) {
        medias = list;
        notifyDataSetChanged();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;

        holder = new GalleryItemViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(
                R.layout.alivc_media_item_gallery_media, parent, false), thumbnailGenerator);

        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        boolean actived = activeAdapterPosition == position;

        ((GalleryItemViewHolder) holder).onBind(getItem(position), actived, mMinDuration);

    }

    public MediaInfo getItem(int position) {
        if (medias.size() > 0 && position >= 0) {
            return medias.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM_MEDIA;
    }

    public int setActiveDataItem(MediaInfo info) {
        return setActiveDataItem(info == null ? -1 : info.id);
    }

    public int setActiveDataItem(int id) {
        int dataPos = findDataPosition(id);
        setActiveAdapterItem(dataPos);
        return dataPos;
    }

    private int activeAdapterPosition = 0;
    private void setActiveAdapterItem(int adapterPos) {
        int oldAdapterPos = activeAdapterPosition;
        if (oldAdapterPos == adapterPos) {
            return;
        }

        activeAdapterPosition = adapterPos;
        notifyItemChanged(adapterPos);
//        notifyItemChanged(old_adapter_pos);
    }

    public int getActiveAdapterPosition() {
        return activeAdapterPosition;
    }

    public int findDataPosition(int id) {
        for (int i = 0; i < medias.size(); i++) {
            MediaInfo info = medias.get(i);
            if (info.id == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }

    @Override
    public void onClick(View v) {
        RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
        int adapterPos = holder.getAdapterPosition();
        if (mMinDuration != -1) {
            MediaInfo info = medias.get(adapterPos);
            if (info.type == MediaStorage.TYPE_VIDEO && info.duration < mMinDuration) {
                Toast.makeText(v.getContext(), R.string.alivc_media_video_short_error, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (onItemClickListener != null) {
            if (!onItemClickListener.onItemClick(this, adapterPos)) {
                return;
            }
        }
        setActiveAdapterItem(adapterPos);
    }

    public void setMinDuration(long minDuration) {
        this.mMinDuration = minDuration;
        notifyDataSetChanged();
    }
}
