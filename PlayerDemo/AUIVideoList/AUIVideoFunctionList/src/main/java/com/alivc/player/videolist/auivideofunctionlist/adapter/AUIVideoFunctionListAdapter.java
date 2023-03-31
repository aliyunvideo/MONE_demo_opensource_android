package com.alivc.player.videolist.auivideofunctionlist.adapter;

import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.alivc.player.videolist.auivideofunctionlist.player.AliyunRenderView;
import com.alivc.player.videolist.auivideofunctionlist.player.AliPlayerPool;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.bean.InfoBean;

public class AUIVideoFunctionListAdapter extends AUIVideoListAdapter {

    public AUIVideoFunctionListAdapter(@NonNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
        super(diffCallback);
    }

    @Override
    public AUIVideoListViewHolder customCreateViewHolder(View view) {
        return new AUIVideoFunctionListViewHolder(view);
    }

    public static class AUIVideoFunctionListViewHolder extends AUIVideoListViewHolder {

        private final AliyunRenderView mAliyunRenderView;

        public AUIVideoFunctionListViewHolder(View itemView) {
            super(itemView);

            mAliyunRenderView = AliPlayerPool.getPlayer();
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
                public void onRenderingStart(int position,long duration) {
                    if (mOnPlayerListener != null) {
                        mOnPlayerListener.onRenderingStart(getAdapterPosition(),duration);
                    }
                }

                @Override
                public void onCompletion(int position) {
                    if (mOnPlayerListener != null) {
                        mOnPlayerListener.onCompletion(getAdapterPosition());
                    }
                }
            });
        }

        @Override
        public void bindUrl(String url) {
            TextureView textureView = mAliyunRenderView.initTextureView();
            mRootFrameLayout.addView(textureView, 0);
            mAliyunRenderView.bindUrl(url);
        }

        public AliPlayer getAliPlayer() {
            return mAliyunRenderView.getAliPlayer();
        }
    }
}
