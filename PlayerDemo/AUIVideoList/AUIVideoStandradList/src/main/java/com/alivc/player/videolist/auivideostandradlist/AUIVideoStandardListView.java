package com.alivc.player.videolist.auivideostandradlist;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alivc.player.videolist.auivideolistcommon.AUIVideoListView;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListDiffCallback;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;
import com.alivc.player.videolist.auivideostandradlist.adapter.AUIVideoStandardListAdapter;
import com.aliyun.player.bean.InfoBean;

import java.util.List;

public class AUIVideoStandardListView extends AUIVideoListView {

    private AUIVideoStandardListController mController;
    private TextureView mTextureView;
    private boolean mAutoPlayNext;
    private int mCurrentPosition;

    public AUIVideoStandardListView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIVideoStandardListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIVideoStandardListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        initTextureView();
        mController = new AUIVideoStandardListController(mContext);

        mController.setPlayerListener(new PlayerListener() {
            @Override
            public void onPrepared(int position) {

            }

            @Override
            public void onInfo(int position, InfoBean infoBean) {

            }

            @Override
            public void onPlayStateChanged(int position, boolean isPaused) {

            }

            @Override
            public void onRenderingStart(int position, long duration) {

            }

            @Override
            public void onCompletion(int position) {
                if (mAutoPlayNext && mCurrentPosition < mAUIVideoListAdapter.getItemCount()) {
                    mRecyclerView.smoothScrollToPosition(mCurrentPosition + 1);
                }
            }
        });
    }

    private void initTextureView() {
        mTextureView = new TextureView(mContext);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

            private Surface surface;

            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                surface = new Surface(surfaceTexture);
                mController.setSurface(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                mController.surfaceChanged();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                mController.setSurface(null);
                if (surface != null) {
                    surface.release();
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

            }
        });
    }

    private void addTextureView(int position) {
        mRecyclerView.post(() -> {
            AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(position);
            if (viewHolderByPosition != null) {
                ViewGroup rootView = viewHolderByPosition.getRootView();
                ViewParent parent = mTextureView.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(mTextureView);
                }
                rootView.addView(mTextureView);
            }
        });

    }

    @Override
    protected AUIVideoListAdapter initAUIVideoListAdapter(Context context) {
        return new AUIVideoStandardListAdapter(new AUIVideoListDiffCallback());
    }

    @Override
    public void loadSources(List<VideoInfo> videoBeanList) {
        super.loadSources(videoBeanList);
        mController.loadSource(videoBeanList);
    }

    @Override
    public void addSources(List<VideoInfo> videoBeanList) {
        super.addSources(videoBeanList);
        mController.addSource(videoBeanList);
    }

    @Override
    public void openLoopPlay(boolean openLoopPlay) {
        mController.openLoopPlay(openLoopPlay);
    }

    @Override
    public void autoPlayNext(boolean autoPlayNext) {
        this.mAutoPlayNext = autoPlayNext;
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        this.mCurrentPosition = position;
        addTextureView(position);
        mController.onPageSelected(position);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mController.destroy();
    }
}
