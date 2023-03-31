package com.alivc.player.videolist.auivideostandradlist.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.aliyun.player.AliPlayer;

public class AUIVideoStandardListAdapter extends AUIVideoListAdapter {

    public AUIVideoStandardListAdapter(@NonNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
        super(diffCallback);
    }

    @Override
    public AUIVideoListViewHolder customCreateViewHolder(View view) {
        return new AUIVideoStandardListViewHolder(view);
    }

    public static class AUIVideoStandardListViewHolder extends AUIVideoListViewHolder {

        public AUIVideoStandardListViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindUrl(String url) {

        }

        @Override
        public AliPlayer getAliPlayer() {
            return null;
        }
    }
}
