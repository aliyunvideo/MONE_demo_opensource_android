package com.alivc.player.videolist.auivideolistcommon.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;

public class AUIVideoListDiffCallback extends DiffUtil.ItemCallback<VideoInfo>{

    @Override
    public boolean areItemsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
        return oldItem.equals(newItem);
    }
}
