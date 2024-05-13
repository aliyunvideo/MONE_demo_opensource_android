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

    private PlayerListener mOnPlayerListener;
    private OnRecyclerViewItemClickListener mListener;
    private OnSeekChangedListener mSeekBarListener;

    protected AUIVideoListAdapter(@NonNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public AUIVideoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return customCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull AUIVideoListViewHolder holder, int position) {
        VideoInfo videoInfo = getItem(position);
        holder.onBind(videoInfo);
        holder.setOnItemClickListener(mListener);
        holder.setOnSeekBarStateChangeListener(mSeekBarListener);
        holder.setOnPlayerListener(mOnPlayerListener);
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