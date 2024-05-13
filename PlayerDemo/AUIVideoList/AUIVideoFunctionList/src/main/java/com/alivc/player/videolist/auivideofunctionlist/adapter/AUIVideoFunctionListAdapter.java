package com.alivc.player.videolist.auivideofunctionlist.adapter;

import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.videolist.auivdieofunctionlist.R;
import com.alivc.player.videolist.auivideofunctionlist.player.AliPlayerPool;
import com.alivc.player.videolist.auivideofunctionlist.player.AliyunRenderView;
import com.alivc.player.videolist.auivideolistcommon.AUIVideoListViewType;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;

public class AUIVideoFunctionListAdapter extends AUIVideoListAdapter {
    private AliPlayerPool mAliPlayerPool;

    public AUIVideoFunctionListAdapter(@NonNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
        super(diffCallback);
    }

    @Override
    public AUIVideoListViewHolder customCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.ilr_view_function_list_item, parent, false);
        return new AUIVideoFunctionListViewHolder(inflate);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mAliPlayerPool = new AliPlayerPool();
        mAliPlayerPool.init(recyclerView.getContext());
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mAliPlayerPool != null) {
            mAliPlayerPool.release();
            mAliPlayerPool = null;
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull AUIVideoListViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof AUIVideoFunctionListViewHolder) {
            AliPlayer aliPlayer = (mAliPlayerPool != null) ? mAliPlayerPool.getPlayer(holder.toString()) : null;
            ((AUIVideoFunctionListViewHolder) holder).bindVideo(aliPlayer, getItem(holder.getAdapterPosition()));
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull AUIVideoListViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof AUIVideoFunctionListViewHolder) {
            ((AUIVideoFunctionListViewHolder) holder).unbind();
        }
    }

    public static class AUIVideoFunctionListViewHolder extends AUIVideoListViewHolder {

        private final AliyunRenderView mAliyunRenderView;

        public AUIVideoFunctionListViewHolder(View itemView) {
            super(itemView);
            mAliyunRenderView = new AliyunRenderView(itemView.getContext());
            mAliyunRenderView.setOnPlayerListener(new PlayerListener() {
                @Override
                public void onPrepared(int position) {
                    if (mOnPlayerListener != null) {
                        mOnPlayerListener.onPrepared(getAdapterPosition());
                    }
                }

                @Override
                public void onInfo(int position, InfoBean infoBean) {
                    if (mOnPlayerListener != null) {
                        mOnPlayerListener.onInfo(getAdapterPosition(), infoBean);
                    }
                }

                @Override
                public void onPlayStateChanged(int position, boolean isPaused) {
                    if (mOnPlayerListener != null) {
                        mOnPlayerListener.onPlayStateChanged(getAdapterPosition(), isPaused);
                    }
                }

                @Override
                public void onRenderingStart(int position, long duration) {
                    if (mOnPlayerListener != null) {
                        mOnPlayerListener.onRenderingStart(getAdapterPosition(), duration);
                    }
                }

                @Override
                public void onCompletion(int position) {
                    if (mOnPlayerListener != null) {
                        mOnPlayerListener.onCompletion(getAdapterPosition());
                    }
                }

                @Override
                public void onError(ErrorInfo errorInfo) {

                }
            });
        }

        public void bindVideo(AliPlayer aliPlayer, VideoInfo videoInfo) {
            TextureView textureView = mAliyunRenderView.initTextureView();
            mRootFrameLayout.addView(textureView, 0);
            mAliyunRenderView.bindVideo(aliPlayer, videoInfo);
        }

        public void unbind() {
            if (mAliyunRenderView != null) {
                mAliyunRenderView.unbind();
            }
        }

        public AliyunRenderView getAliPlayerView() {
            return mAliyunRenderView;
        }

        @Override
        public void changePlayState() {
            super.changePlayState();
            if (mAliyunRenderView.isPlaying()) {
                mAliyunRenderView.pause();
                showPlayIcon(true);
            } else {
                mAliyunRenderView.start();
                showPlayIcon(false);
            }
        }

        @Override
        protected AUIVideoListViewType getViewType() {
            return AUIVideoListViewType.FUNCTION_LIST;
        }

        @Override
        public String toString() {
            return "ViewHolder-" + super.hashCode();
        }
    }
}
