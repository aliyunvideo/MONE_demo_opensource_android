package com.alivc.player.videolist.auivideostandradlist;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alivc.player.videolist.auivideolistcommon.AUIVideoListView;
import com.alivc.player.videolist.auivideolistcommon.AUIVideoListViewType;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListDiffCallback;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListLayoutManager;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideostandradlist.adapter.AUIVideoStandardListAdapter;
import com.alivc.player.videolist.auivideostandradlist.adapter.AUIVideoStandardListLayoutManager;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;

import java.util.List;

public class AUIVideoStandardListView extends AUIVideoListView {

    private AUIVideoStandardListController mController;
    private TextureView mTextureView;
    private boolean mAutoPlayNext;

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

    @Override
    protected AUIVideoListViewType getViewType() {
        return AUIVideoListViewType.STANDARD_LIST;
    }

    private void init(Context context) {
        this.mContext = context;
        initTextureView();
        mController = new AUIVideoStandardListController(mContext);

        mController.setPlayerListener(this);
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
    protected AUIVideoListLayoutManager initLayoutManager() {
        return new AUIVideoStandardListLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
    }

    @Override
    protected AUIVideoListAdapter initAUIVideoListAdapter(Context context) {
        return new AUIVideoStandardListAdapter(new AUIVideoListDiffCallback());
    }

    @Override
    public void loadSources(List<VideoInfo> videoBeanList) {
        mController.loadSource(videoBeanList);
        super.loadSources(videoBeanList);
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
    public void onPlayStateChanged(int position, boolean isPaused) {
        super.onPlayStateChanged(position, isPaused);
        AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(mSelectedPosition);
        if (viewHolderByPosition != null) {
            viewHolderByPosition.showPlayIcon(isPaused);
        }
    }

    @Override
    public void onSeek(int position, long seekPosition) {
        super.onSeek(position, seekPosition);
        mController.seek(seekPosition);
    }

    @Override
    public void onItemClick(int position) {
        mController.onPlayStateChange();
    }

    @Override
    public void onBackPress() {
        if (mContext instanceof Activity) {
            if (!((Activity) mContext).isFinishing()) {
                ((Activity) mContext).finish();
            }
        }
    }

    @Override
    public void onInitComplete() {
        this.mSelectedPosition = 0;
        addTextureView(mSelectedPosition);
        mController.onPageSelected(mSelectedPosition);
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        addTextureView(position);
        mController.onPageSelected(position);
    }

    @Override
    public void onPageRelease(int position) {
        super.onPageRelease(position);
        AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(position);
        if (viewHolderByPosition != null) {
            viewHolderByPosition.showPlayIcon(false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mController.destroy();
    }

    @Override
    public void onRenderingStart(int position, long duration) {
        super.onRenderingStart(position, duration);
        AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(mSelectedPosition);
        if (viewHolderByPosition != null) {
            viewHolderByPosition.getSeekBar().setMax((int) duration);
        }
    }

    @Override
    public void onInfo(int position, InfoBean infoBean) {
        super.onInfo(position, infoBean);
        if (infoBean.getCode() == InfoCode.CurrentPosition) {
            AUIVideoListViewHolder viewHolderByPosition = getViewHolderByPosition(mSelectedPosition);
            if (viewHolderByPosition != null) {
                viewHolderByPosition.getSeekBar().setProgress((int) infoBean.getExtraValue());
            }
        }
    }

    @Override
    public void onCompletion(int position) {
        super.onCompletion(position);
        if (mAutoPlayNext && mSelectedPosition < mAUIVideoListAdapter.getItemCount()) {
            mRecyclerView.smoothScrollToPosition(mSelectedPosition + 1);
        }
    }

    /**
     * 设置预加载数量
     */
    public void setPreloadCount(int preloadCount) {
        mController.setPreloadCount(preloadCount);
    }

    public void enableLocalCache(boolean enable,String path) {
        mController.enableLocalCache(enable,path);
    }
}
