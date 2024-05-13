package com.aliyun.player.alivcplayerexpand.view.more;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.auiplayerserver.bean.VideoInfo;
import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 投屏设备选择adapter
 */
public class SelectSeriesAdapter extends RecyclerView.Adapter<SelectSeriesAdapter.SelectSeriesViewHolder> {
    private static String CREATE_TIME_MSG = "原创·138万次观看·06-01发布";
    private List<VideoInfo> mList = new ArrayList<>();
    private int mCurrentPostion = 0;

    private SelectSeriesView.OnVideoItemClickListener mOnVideoItemClickListener;

    @Override
    public SelectSeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_series, parent, false);
        return new SelectSeriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SelectSeriesViewHolder holder, final int position) {
        if (position == mCurrentPostion){
            holder.mVideoTitle.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_00f2ff));
            holder.mVideoInfo.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_00f2ff));
            holder.currentVidTips.setVisibility(View.VISIBLE);
        }else {
            holder.mVideoTitle.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.mVideoInfo.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_888888));
            holder.currentVidTips.setVisibility(View.GONE);
        }


        VideoInfo episode = mList.get(position);
        String coverUrl = episode.getCoverUrl();
        ImageLoader.loadRoundImg(coverUrl,holder.mVideoCover,4);
        holder.mVideoTitle.setText(episode.getTitle());
        holder.mVideoInfo.setText(CREATE_TIME_MSG);
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnVideoItemClickListener != null) {
                    mOnVideoItemClickListener.onItemClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class SelectSeriesViewHolder extends RecyclerView.ViewHolder {

        private TextView mVideoInfo;
        private TextView mVideoTitle;
        private ImageView mVideoCover;
        private View mItemView;
        private View currentVidTips;

        public SelectSeriesViewHolder(View itemView) {
            super(itemView);
            mVideoCover = itemView.findViewById(R.id.img_cover);
            mVideoTitle = itemView.findViewById(R.id.tv_video_title);
            mVideoInfo = itemView.findViewById(R.id.tv_video_info);
            mItemView = itemView.findViewById(R.id.ll_dfm_item);
            currentVidTips = itemView.findViewById(R.id.tv_current_video);
        }
    }

    /**
     * 清空
     */
    public void clear() {
        if (mList != null && mList.size() > 0) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    public void update(List<VideoInfo> list, int currentPostion) {
        mList = list;
        mCurrentPostion = currentPostion;
        notifyDataSetChanged();
    }


    public void setmOnVideoItemClickListener(SelectSeriesView.OnVideoItemClickListener mOnVideoItemClickListener) {
        this.mOnVideoItemClickListener = mOnVideoItemClickListener;
    }
}
