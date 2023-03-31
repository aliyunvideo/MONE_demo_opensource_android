package com.alivc.player.videolist.auivideolistcommon.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.alivc.player.videolist.auivideolistcommon.R;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.OnRecyclerViewItemClickListener;
import com.alivc.player.videolist.auivideolistcommon.listener.OnSeekChangedListener;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;

public abstract class AUIVideoListAdapter extends ListAdapter<VideoInfo,AUIVideoListViewHolder>{

    public static PlayerListener mOnPlayerListener;
    public static OnRecyclerViewItemClickListener mListener;
    public static OnSeekChangedListener mSeekBarListener;

    protected AUIVideoListAdapter(@NonNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
        super(diffCallback);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull AUIVideoListViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.bindUrl(getItem(holder.getAdapterPosition()).getUrl());
    }

    @NonNull
    @Override
    public AUIVideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_item, parent, false);
        return customCreateViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull AUIVideoListViewHolder holder, int position) {
        VideoInfo videoInfo = getItem(position);
        holder.onBind(videoInfo);
    }

    abstract public AUIVideoListViewHolder customCreateViewHolder(View view);

    public void setOnPlayerListener(PlayerListener listener) {
        mOnPlayerListener = listener;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mListener = listener;
    }

    public void setOnSeekBarStateChangeListener(OnSeekChangedListener listener) {
        mSeekBarListener = listener;
    }
}