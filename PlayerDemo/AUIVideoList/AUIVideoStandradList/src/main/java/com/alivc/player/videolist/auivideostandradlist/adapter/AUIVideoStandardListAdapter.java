package com.alivc.player.videolist.auivideostandradlist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.alivc.player.videolist.auivideolistcommon.AUIVideoListViewType;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideostandradlist.R;

public class AUIVideoStandardListAdapter extends AUIVideoListAdapter {

    public AUIVideoStandardListAdapter(@NonNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
        super(diffCallback);
    }

    @Override
    public AUIVideoListViewHolder customCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.ilr_view_standard_list_item, parent, false);
        return new AUIVideoStandardListViewHolder(inflate);
    }

    public static class AUIVideoStandardListViewHolder extends AUIVideoListViewHolder {

        public AUIVideoStandardListViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected AUIVideoListViewType getViewType() {
            return AUIVideoListViewType.STANDARD_LIST;
        }
    }
}
