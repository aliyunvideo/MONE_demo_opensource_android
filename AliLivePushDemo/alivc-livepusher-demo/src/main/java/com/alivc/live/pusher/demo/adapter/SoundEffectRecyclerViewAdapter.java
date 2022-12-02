package com.alivc.live.pusher.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.live.pusher.demo.R;
import com.alivc.live.pusher.demo.bean.SoundEffectBean;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SoundEffectRecyclerViewAdapter extends RecyclerView.Adapter<SoundEffectRecyclerViewAdapter.SoundEffectViewHolder> {

    private final Map<Integer, SoundEffectBean> mDataMap;
    private int mSelectIndex = 0;
    private OnSoundEffectItemClickListener mOnSoundEffectItemClickListener;
    private final int mNormalColor;
    private final int mSelectColor;

    public SoundEffectRecyclerViewAdapter(Context context, Map<Integer, SoundEffectBean> data) {
        this.mDataMap = data;
        mNormalColor = context.getResources().getColor(R.color.camera_panel_content);
        mSelectColor = context.getResources().getColor(R.color.colorBaseStyle);
    }

    @NonNull
    @NotNull
    @Override
    public SoundEffectViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sound_effect_recycler_view, parent, false);
        return new SoundEffectViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SoundEffectViewHolder holder, int position) {
        SoundEffectBean soundEffectBean = mDataMap.get(position);
        if (soundEffectBean != null) {
            holder.mSoundEffectTitle.setText(soundEffectBean.getDescriptionId());
            holder.mIconImageView.setImageResource(soundEffectBean.getIconId());
            if (mSelectIndex == position) {
                holder.mIconImageView.setColorFilter(mSelectColor);
                holder.mSoundEffectTitle.setTextColor(mSelectColor);
            } else {
                holder.mIconImageView.setColorFilter(mNormalColor);
                holder.mSoundEffectTitle.setTextColor(mNormalColor);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataMap == null ? 0 : mDataMap.keySet().size();
    }

    public void setSelectIndex(int index) {
        this.mSelectIndex = index;
        notifyDataSetChanged();
    }

    public class SoundEffectViewHolder extends RecyclerView.ViewHolder {

        private final TextView mSoundEffectTitle;
        private final ImageView mIconImageView;

        public SoundEffectViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ConstraintLayout mRootView = itemView.findViewById(R.id.item_root);
            mSoundEffectTitle = itemView.findViewById(R.id.item_sound_effect_title);
            mIconImageView = itemView.findViewById(R.id.iv_icon);

            mRootView.setOnClickListener(view -> {
                if (mOnSoundEffectItemClickListener != null && mSelectIndex != getAdapterPosition()) {
                    mOnSoundEffectItemClickListener.onSoundEffectItemClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnSoundEffectItemClickListener {
        void onSoundEffectItemClick(int position);
    }

    public void setOnSoundEffectItemClickListener(OnSoundEffectItemClickListener listener) {
        this.mOnSoundEffectItemClickListener = listener;
    }
}
