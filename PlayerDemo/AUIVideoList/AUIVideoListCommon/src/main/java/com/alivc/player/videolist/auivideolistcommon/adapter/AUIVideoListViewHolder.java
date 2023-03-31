package com.alivc.player.videolist.auivideolistcommon.adapter;

import static com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter.mListener;
import static com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter.mSeekBarListener;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.videolist.auivideolistcommon.R;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.aliyun.player.AliPlayer;

public abstract class AUIVideoListViewHolder extends RecyclerView.ViewHolder {

    public void onBind(VideoInfo videoInfo) {
        mVideoTitleTextView.setText(videoInfo.getTitle());
        mAuthorTextView.setText("@阿里云视频云 MediaBox");
    }

    protected final FrameLayout mRootFrameLayout;
    protected final ImageView mIvCover;
    protected final AppCompatSeekBar mSeekBar;
    protected final TextView mVideoTitleTextView;
    protected final TextView mAuthorTextView;
    protected final ImageView mPlayImageView;

    public AUIVideoListViewHolder(View itemView) {
        super(itemView);

        setIsRecyclable(false);
        mRootFrameLayout = itemView.findViewById(R.id.fm_root);
        mIvCover = itemView.findViewById(R.id.iv_cover);
        mSeekBar = itemView.findViewById(R.id.seekbar);
        mVideoTitleTextView = itemView.findViewById(R.id.tv_video_title);
        mAuthorTextView = itemView.findViewById(R.id.tv_author);
        mPlayImageView = itemView.findViewById(R.id.iv_play);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mSeekBarListener != null) {
                    mSeekBarListener.onSeek(getAdapterPosition(), seekBar.getProgress());
                }
            }
        });

        mRootFrameLayout.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(getAdapterPosition());
            }
        });
    }

    /*
        update video source
     */
    public abstract void bindUrl(String url);

    public abstract AliPlayer getAliPlayer();

    public ViewGroup getRootView() {
        return mRootFrameLayout;
    }

    public View getCoverView() {
        return mIvCover;
    }

    public ProgressBar getSeekBar() {
        return mSeekBar;
    }

    public void showPlayIcon(boolean isShown) {
        if (isShown) {
            mPlayImageView.setVisibility(View.VISIBLE);
        } else {
            mPlayImageView.setVisibility(View.GONE);
        }
    }
}
