package com.alivc.player.videolist.auivideofunctionlist.adapter;

import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.alivc.player.videolist.auivdieofunctionlist.R;
import com.alivc.player.videolist.auivideofunctionlist.player.AliPlayerPool;
import com.alivc.player.videolist.auivideofunctionlist.player.AliyunRenderView;
import com.alivc.player.videolist.auivideolistcommon.AUIVideoListViewType;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;

public class AUIVideoFunctionListAdapter extends AUIVideoListAdapter {

    public AUIVideoFunctionListAdapter(@NonNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
        super(diffCallback);
    }

    @Override
    public AUIVideoListViewHolder customCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.ilr_view_function_list_item, parent, false);
        return new AUIVideoFunctionListViewHolder(inflate);
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
                public void onError(ErrorInfo errorInfo){

                }
            });
        }

        @Override
        public void bindUrl(String url) {
            TextureView textureView = mAliyunRenderView.initTextureView();
            mRootFrameLayout.addView(textureView, 0);
            mAliyunRenderView.bindUrl(url);
        }

        public AliyunRenderView getAliPlayer() {
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
    }
}
