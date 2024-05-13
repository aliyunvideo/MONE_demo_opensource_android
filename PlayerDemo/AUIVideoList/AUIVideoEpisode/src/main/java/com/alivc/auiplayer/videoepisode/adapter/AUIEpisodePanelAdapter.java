package com.alivc.auiplayer.videoepisode.adapter;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.auiplayer.videoepisode.R;
import com.alivc.auiplayer.videoepisode.data.AUIEpisodeVideoInfo;
import com.alivc.auiplayer.videoepisode.listener.OnPanelEventListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

/**
 * @author keria
 * @date 2023/8/29
 * @brief 短剧选集列表适配器
 */
public class AUIEpisodePanelAdapter extends RecyclerView.Adapter<AUIEpisodePanelAdapter.PanelViewHolder> {

    private List<AUIEpisodeVideoInfo> mEpisodeVideoInfos = null;
    private int mSelectedPosition = -1;

    private OnPanelEventListener mOnPanelEventListener = null;

    @NonNull
    @Override
    public PanelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ilr_view_episode_panel_item, parent, false);
        return new PanelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PanelViewHolder holder, int position) {
        if (mEpisodeVideoInfos == null || position >= mEpisodeVideoInfos.size()) {
            return;
        }
        AUIEpisodeVideoInfo episodeVideoInfo = mEpisodeVideoInfos.get(position);
        holder.bind(episodeVideoInfo);
        //Log.i("CheckFunc", "setSelected: " + position + ", " + mSelectedPosition);
        holder.setSelected(position == mSelectedPosition);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPanelEventListener != null) {
                    mOnPanelEventListener.onItemClicked(episodeVideoInfo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEpisodeVideoInfos != null ? mEpisodeVideoInfos.size() : 0;
    }

    public void initListener(OnPanelEventListener listener) {
        mOnPanelEventListener = listener;
    }

    public void initData(List<AUIEpisodeVideoInfo> episodeVideoInfos, int selectedPosition) {
        mEpisodeVideoInfos = episodeVideoInfos;
        mSelectedPosition = selectedPosition;
    }

    public static class PanelViewHolder extends RecyclerView.ViewHolder {
        private final View mContainerView = itemView.findViewById(R.id.v_item);
        private final ImageView mCoverImageView = itemView.findViewById(R.id.iv_cover);
        private final TextView mDescriptionTextView = itemView.findViewById(R.id.tv_description);
        private final TextView mVideoDurationTextView = itemView.findViewById(R.id.tv_video_duration);
        private final TextView mPlayCountTextView = itemView.findViewById(R.id.tv_play_count);
        private final ImageView mSoundImageView = itemView.findViewById(R.id.iv_sound_icon);

        public PanelViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(@NonNull AUIEpisodeVideoInfo episodeVideoInfo) {
            Glide.with(itemView.getContext())
                    .load(episodeVideoInfo.coverUrl)
                    .transform(new RoundedCorners((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, itemView.getContext().getResources().getDisplayMetrics())))
                    .centerCrop()
                    .into(mCoverImageView);
            mDescriptionTextView.setText(episodeVideoInfo.getTitle());
            mVideoDurationTextView.setText(AUIEpisodeVideoInfo.formatTimeDuration(episodeVideoInfo.videoDuration));
            mPlayCountTextView.setText(AUIEpisodeVideoInfo.formatNumber(episodeVideoInfo.videoPlayCount));
        }

        private void setSelected(boolean selected) {
            mSoundImageView.setVisibility(selected ? View.VISIBLE : View.GONE);
            mContainerView.setBackgroundColor(Color.parseColor(selected ? "#3A3D48" : "#1C1D22"));
        }
    }
}
