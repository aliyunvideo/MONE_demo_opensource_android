package com.alivc.player.videolist.auivideolistcommon.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.videolist.auivideolistcommon.AUIVideoListViewType;
import com.alivc.player.videolist.auivideolistcommon.R;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.OnRecyclerViewItemClickListener;
import com.alivc.player.videolist.auivideolistcommon.listener.OnSeekChangedListener;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;

public abstract class AUIVideoListViewHolder extends RecyclerView.ViewHolder {

    private static boolean mEnableTitle = true;
    private static boolean mEnableAuth = true;
    private static boolean mEnableSeekbar = true;
    protected static boolean mEnablePlayIcon = true;

    protected PlayerListener mOnPlayerListener;
    protected OnRecyclerViewItemClickListener mListener;
    protected OnSeekChangedListener mSeekBarListener;

    public void onBind(VideoInfo videoInfo) {
        mVideoTitleTextView.setText(videoInfo.getTitle());
        mAuthorTextView.setText("@阿里云视频云 MediaBox");

        mVideoTitleTextView.setVisibility(mEnableTitle ? View.VISIBLE : View.GONE);
        mAuthorTextView.setVisibility(mEnableAuth ? View.VISIBLE : View.GONE);
        mSeekBar.setVisibility(mEnableSeekbar ? View.VISIBLE : View.GONE);
    }

    protected final FrameLayout mRootFrameLayout;
    protected final AppCompatSeekBar mSeekBar;
    protected final TextView mVideoTitleTextView;
    protected final TextView mAuthorTextView;
    protected final ImageView mPlayImageView;
    protected final ImageView mBackImageView;

    public AUIVideoListViewHolder(View itemView) {
        super(itemView);

        mRootFrameLayout = itemView.findViewById(R.id.fm_root);
        mSeekBar = itemView.findViewById(R.id.seekbar);
        mVideoTitleTextView = itemView.findViewById(R.id.tv_video_title);
        mAuthorTextView = itemView.findViewById(R.id.tv_author);
        mPlayImageView = itemView.findViewById(R.id.iv_play);
        mBackImageView = itemView.findViewById(R.id.iv_back);

        mSeekBar.setVisibility(mEnableSeekbar ? View.VISIBLE : View.GONE);
        if (getViewType() == AUIVideoListViewType.EPISODE) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mSeekBar.getLayoutParams();
            layoutParams.bottomMargin = dip2px(mSeekBar.getContext(), 84);
            mSeekBar.setLayoutParams(layoutParams);
            mSeekBar.requestLayout();
        }

        mVideoTitleTextView.setVisibility(mEnableTitle ? View.VISIBLE : View.GONE);
        mAuthorTextView.setVisibility(mEnableAuth ? View.VISIBLE : View.GONE);
        mPlayImageView.setVisibility(View.GONE);

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

        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onBackPress();
                }
            }
        });
    }

    public void setOnPlayerListener(PlayerListener listener) {
        mOnPlayerListener = listener;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mListener = listener;
    }

    public void setOnSeekBarStateChangeListener(OnSeekChangedListener listener) {
        mSeekBarListener = listener;
    }

    protected abstract AUIVideoListViewType getViewType();

    public ViewGroup getRootView() {
        return mRootFrameLayout;
    }

    public static void enableTitleTextView(boolean isShown) {
        mEnableTitle = isShown;
    }

    public static void enableAuthTextView(boolean isShown) {
        mEnableAuth = isShown;
    }

    public ProgressBar getSeekBar() {
        return mSeekBar;
    }

    public static void enableSeekBar(boolean isShown) {
        mEnableSeekbar = isShown;
    }

    public static void enablePlayIcon(boolean isShown) {
        mEnablePlayIcon = isShown;
    }

    public void showPlayIcon(boolean isShown) {
        if (mEnablePlayIcon) {
            mPlayImageView.setVisibility(isShown ? View.VISIBLE : View.GONE);
        } else {
            mPlayImageView.setVisibility(View.GONE);
        }
    }

    public void changePlayState() {

    }

    private static int dip2px(Context paramContext, float paramFloat) {
        return (int)(0.5F + paramFloat * paramContext.getResources().getDisplayMetrics().density);
    }
}
