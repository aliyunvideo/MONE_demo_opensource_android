package com.alivc.auiplayer.videoepisode.adapter;

import android.graphics.SurfaceTexture;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.alivc.auiplayer.videoepisode.R;
import com.alivc.auiplayer.videoepisode.component.AUIEpisodeBarComponent;
import com.alivc.auiplayer.videoepisode.component.AUIEpisodePanelComponent;
import com.alivc.auiplayer.videoepisode.component.AUIVideoDetailComponent;
import com.alivc.auiplayer.videoepisode.component.AUIVideoInteractiveComponent;
import com.alivc.auiplayer.videoepisode.data.AUIEpisodeData;
import com.alivc.auiplayer.videoepisode.data.AUIEpisodeVideoInfo;
import com.alivc.auiplayer.videoepisode.listener.OnDetailEventListener;
import com.alivc.auiplayer.videoepisode.listener.OnInteractiveEventListener;
import com.alivc.auiplayer.videoepisode.listener.OnPanelEventListener;
import com.alivc.auiplayer.videoepisode.listener.OnSurfaceListener;
import com.alivc.player.videolist.auivideolistcommon.AUIVideoListViewType;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;

public class AUIVideoEpisodeAdapter extends AUIVideoListAdapter {

    private OnSurfaceListener mOnSurfaceListener = null;
    private OnDetailEventListener mOnDetailEventListener = null;
    private OnInteractiveEventListener mOnInteractiveEventListener = null;
    private OnPanelEventListener mOnPanelEventListener = null;

    private AUIEpisodeData mEpisodeData = null;

    public AUIVideoEpisodeAdapter(@NonNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
        super(diffCallback);
    }

    @Override
    public AUIVideoListViewHolder customCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.ilr_view_episode_item, parent, false);
        AUIVideoEpisodeViewHolder viewHolder = new AUIVideoEpisodeViewHolder(inflate);
        viewHolder.initSurfaceListener(mOnSurfaceListener);
        viewHolder.initDetailEventListener(mOnDetailEventListener);
        viewHolder.initInteractiveEventListener(mOnInteractiveEventListener);
        viewHolder.initPanelEventListener(mOnPanelEventListener);
        return viewHolder;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull AUIVideoListViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof AUIVideoEpisodeViewHolder) {
            ((AUIVideoEpisodeViewHolder) holder).hidePanelIfNeed();
        }
    }

    public void initSurfaceListener(OnSurfaceListener listener) {
        mOnSurfaceListener = listener;
    }

    public void initInteractiveEventListener(OnInteractiveEventListener listener) {
        mOnInteractiveEventListener = listener;
    }

    public void initDetailEventListener(OnDetailEventListener listener) {
        mOnDetailEventListener = listener;
    }

    public void initPanelEventListener(OnPanelEventListener listener) {
        mOnPanelEventListener = listener;
    }

    public void provideData(AUIEpisodeData episodeData) {
        mEpisodeData = episodeData;
    }

    @Override
    public int getItemCount() {
        return (mEpisodeData != null && mEpisodeData.list != null) ? mEpisodeData.list.size() : 0;
    }

    public class AUIVideoEpisodeViewHolder extends AUIVideoListViewHolder {

        private static final long VIEW_ANIMATION_DURATION_MS = 330L;

        private AUIEpisodeVideoInfo mEpisodeVideoInfo;
        private int mPosition;

        private AUIVideoInteractiveComponent mInteractiveComponent;
        private AUIVideoDetailComponent mDetailComponent;

        private AUIEpisodeBarComponent mBarComponent;
        private AUIEpisodePanelComponent mPanelComponent;

        private TextureView mTextureView;
        private Surface mSurface;

        private OnSurfaceListener mOnSurfaceListener = null;
        private OnDetailEventListener mOnDetailEventListener = null;
        private OnInteractiveEventListener mOnInteractiveEventListener = null;
        private OnPanelEventListener mOnPanelEventListener = null;

        private final TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mSurface = new Surface(surface);
                if (mOnSurfaceListener != null) {
                    mOnSurfaceListener.onSurfaceCreate(getAdapterPosition(), mSurface);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                mSurface = null;
                if (mOnSurfaceListener != null) {
                    mOnSurfaceListener.onSurfaceDestroyed(getAdapterPosition());
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        };

        private AUIVideoEpisodeViewHolder(View itemView) {
            super(itemView);
            initViews();
            initCallback();
        }

        public Surface getSurface() {
            return mSurface;
        }

        private void initViews() {
            bindTextureView();
            mInteractiveComponent = itemView.findViewById(R.id.v_interactive);
            mDetailComponent = itemView.findViewById(R.id.v_detail);
            mBarComponent = itemView.findViewById(R.id.v_bar);
            mPanelComponent = itemView.findViewById(R.id.v_panel);
        }

        private void initCallback() {
            mInteractiveComponent.initListener(mOnInteractiveEventListener);
            mDetailComponent.initListener(mOnDetailEventListener);
            mBarComponent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPanel();
                }
            });
            mPanelComponent.initListener(new OnPanelEventListener() {
                @Override
                public void onClickRetract() {
                    hidePanel();
                }

                @Override
                public void onItemClicked(AUIEpisodeVideoInfo episodeVideoInfo) {
                    if (mOnPanelEventListener != null) {
                        mOnPanelEventListener.onItemClicked(episodeVideoInfo);
                    }
                }
            });
        }

        private void bindTextureView() {
            mTextureView = new TextureView(itemView.getContext());
            mTextureView.setSurfaceTextureListener(surfaceTextureListener);

            ViewParent parent = mTextureView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mTextureView);
            }
            getRootView().addView(mTextureView);
        }

        public void initSurfaceListener(OnSurfaceListener listener) {
            mOnSurfaceListener = listener;
        }

        public void initInteractiveEventListener(OnInteractiveEventListener listener) {
            mOnInteractiveEventListener = listener;
            mInteractiveComponent.initListener(mOnInteractiveEventListener);
        }

        public void initDetailEventListener(OnDetailEventListener listener) {
            mOnDetailEventListener = listener;
            mDetailComponent.initListener(mOnDetailEventListener);
        }

        public void initPanelEventListener(OnPanelEventListener listener) {
            mOnPanelEventListener = listener;
        }

        @Override
        public void onBind(VideoInfo videoInfo) {
            mEpisodeVideoInfo = (AUIEpisodeVideoInfo) videoInfo;
            mPosition = AUIEpisodeData.getEpisodeIndex(mEpisodeData, mEpisodeVideoInfo);
            mBarComponent.initData(mEpisodeData);
            mPanelComponent.initData(mEpisodeData, mPosition);
            mPanelComponent.updateView(mEpisodeVideoInfo, mPosition);
            mDetailComponent.initData(mEpisodeVideoInfo);
            mInteractiveComponent.initData(mEpisodeVideoInfo);
        }

        @Override
        protected AUIVideoListViewType getViewType() {
            return AUIVideoListViewType.EPISODE;
        }

        public boolean isPanelShow() {
            return mPanelComponent.getVisibility() == View.VISIBLE;
        }

        public boolean hidePanelIfNeed() {
            boolean panelShow = isPanelShow();
            if (panelShow) {
                hidePanel();
            }
            return panelShow;
        }

        private void showPanel() {
            mPanelComponent.updateView(mEpisodeVideoInfo, mPosition);
            mBarComponent.setVisibility(View.GONE);
            setVisibilityWithAnimation(mPanelComponent, true);
        }

        public void hidePanel() {
            setVisibilityWithAnimation(mPanelComponent, false);
            mBarComponent.setVisibility(View.VISIBLE);
        }

        private void setVisibilityWithAnimation(View view, boolean visible) {
            if (view == null) {
                return;
            }
            float fromY = visible ? 1.0f : 0.0f;
            float toY = visible ? 0.0f : 1.0f;
            TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, fromY, Animation.RELATIVE_TO_SELF, toY);
            animation.setDuration(VIEW_ANIMATION_DURATION_MS);
            view.startAnimation(animation);
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}
