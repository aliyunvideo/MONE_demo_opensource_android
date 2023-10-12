package com.alivc.player.videolist.auivideolistcommon.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.OnRecyclerViewItemClickListener;
import com.alivc.player.videolist.auivideolistcommon.listener.OnSeekChangedListener;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;

public abstract class AUIVideoListAdapter extends ListAdapter<VideoInfo, AUIVideoListViewHolder> {

    public static PlayerListener mOnPlayerListener;
    public static OnRecyclerViewItemClickListener mListener;
    public static OnSeekChangedListener mSeekBarListener;

    protected AUIVideoListAdapter(@NonNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
        super(diffCallback);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull AUIVideoListViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.bindUrl(getItem(holder.getAdapterPosition()).getUrl());//episode不需要该逻辑实现，原因是调用了MoveTo逻辑，不需要新传入的url
    }

    @NonNull
    @Override
    public AUIVideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return customCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull AUIVideoListViewHolder holder, int position) {
        VideoInfo videoInfo = getItem(position);
        videoInfo.setPosition(position);
        holder.onBind(videoInfo);
    }

    public abstract AUIVideoListViewHolder customCreateViewHolder(@NonNull ViewGroup parent, int viewType);

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