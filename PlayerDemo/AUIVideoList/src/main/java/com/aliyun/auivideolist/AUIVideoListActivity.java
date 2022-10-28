package com.aliyun.auivideolist;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.auivideolist.adapter.CustomLayoutManager;
import com.aliyun.auivideolist.adapter.RecyclerViewAdapter;
import com.aliyun.player.bean.ErrorInfo;
import com.bumptech.glide.Glide;


public class AUIVideoListActivity extends AppCompatActivity implements CustomLayoutManager.OnViewPagerListener,
        RecyclerViewAdapter.OnItemClickListener, SeekBar.OnSeekBarChangeListener {

    private RecyclerView mRecyclerView;
    private CustomLayoutManager mCustomLayoutManager;
    private AUIVideoListController mController;
    private TextureView mTextureView;
    private RecyclerViewAdapter.RecyclerViewHolder mViewHolderForAdapterPosition;
    private LinearLayout mGestureLinearLayout;
    private int mSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aui_video_list);
        mController = new AUIVideoListController(this);

        initView();
        initRecyclerView();
        initTextureView();

        mController.initSP();
    }

    private void initView(){
        mRecyclerView = findViewById(R.id.recyclerview);
        mGestureLinearLayout = findViewById(R.id.ll_gesture);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        mGestureLinearLayout.setOnClickListener(view -> mGestureLinearLayout.setVisibility(View.GONE));
    }

    private void initRecyclerView(){
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setItemViewCacheSize(5);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        RecyclerViewAdapter mRecyclerViewAdapter = new RecyclerViewAdapter(this);
        mCustomLayoutManager = new CustomLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        mCustomLayoutManager.setItemPrefetchEnabled(true);
        mCustomLayoutManager.setPreloadItemCount(2);
        mCustomLayoutManager.setOnViewPagerListener(this);
        mRecyclerView.setLayoutManager(mCustomLayoutManager);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.setData(mController.getData());
        mRecyclerViewAdapter.setOnItemClickListener(this);
        mRecyclerViewAdapter.setOnSeekBarStateChangeListener(this);
    }

    private void initTextureView(){
        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
                Surface surface = new Surface(surfaceTexture);
                mController.setRenderView(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
                mController.surfacChanged();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                mController.setRenderView(null);
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) { }
        });
    }

    /**
     * hide Cover ImageView
     */
    public void hideCoverView(){
        RecyclerViewAdapter.RecyclerViewHolder viewHolderForAdapterPosition = (RecyclerViewAdapter.RecyclerViewHolder) mRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
        if(viewHolderForAdapterPosition != null){
            viewHolderForAdapterPosition.getCoverView().setVisibility(View.INVISIBLE);
        }
    }

    /**
     * show Cover ImageView
     */
    public void showCoverView(int position){
        RecyclerViewAdapter.RecyclerViewHolder viewHolderForAdapterPosition = (RecyclerViewAdapter.RecyclerViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
        if(viewHolderForAdapterPosition != null){
            viewHolderForAdapterPosition.getCoverView().setVisibility(View.VISIBLE);
        }
    }

    public void showInteractiveGuidance(){
        mGestureLinearLayout.setVisibility(View.VISIBLE);
        mGestureLinearLayout.postDelayed(() -> mGestureLinearLayout.setVisibility(View.GONE),3000);
    }

    public void showPlayIcon(boolean isShow){
        if(mViewHolderForAdapterPosition != null){
            mViewHolderForAdapterPosition.showPlayIcon(isShow);
        }
    }

    /**
     * video duration
     * @param duration company ms
     */
    public void onVideoFrameShow(long duration) {
        mViewHolderForAdapterPosition = findRecyclerViewLastVisibleHolder();
        if(mViewHolderForAdapterPosition != null){
            mViewHolderForAdapterPosition.getSeekBar().setMax((int) duration);
        }
    }

    /**
     * update Current Position
     * @param extraValue company ms
     */
    public void updateCurrentPosition(long extraValue) {
        if(mViewHolderForAdapterPosition != null){
            mViewHolderForAdapterPosition.getSeekBar().setProgress((int) extraValue);
        }
    }

    /**
     * add TextureView
     */
    private void removeAndAddView(int position){
        ViewParent parent = mTextureView.getParent();
        if (parent instanceof FrameLayout) {
            ((ViewGroup) parent).removeView(mTextureView);
        }
        RecyclerViewAdapter.RecyclerViewHolder holder = (RecyclerViewAdapter.RecyclerViewHolder) mRecyclerView.findViewHolderForLayoutPosition(position);
        if (holder != null) {
            holder.getFrameLayout().addView(mTextureView);
        }
    }

    public void showError(ErrorInfo errorInfo) {
        AVToast.show(this,true,"error: " + errorInfo.getCode() + " -- " + errorInfo.getMsg());
    }

    public void loadMore() {
        AVToast.show(this,true,R.string.aui_video_list_coming_soon);
    }

    private RecyclerViewAdapter.RecyclerViewHolder findRecyclerViewLastVisibleHolder(){
        int lastVisibleItemPosition = mCustomLayoutManager.findLastVisibleItemPosition();
        return (RecyclerViewAdapter.RecyclerViewHolder) mRecyclerView.findViewHolderForAdapterPosition(lastVisibleItemPosition);
    }

    @Override
    public void onItemClick(int position) {
        mController.changePlayState();
    }

    @Override
    public void onPageShow(int position) {
        if(position != mSelectedPosition){
            showCoverView(position);
        }
    }

    @Override
    public void onPageSelected(int position) {
        this.mSelectedPosition = position;
        removeAndAddView(position);
        mController.start(position);
    }

    @Override
    public void onPageRelease() {
        if(mGestureLinearLayout.isShown()){
            mGestureLinearLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        Glide.get(AUIVideoListActivity.this).clearMemory();
        new Thread(() -> Glide.get(AUIVideoListActivity.this).clearDiskCache()).start();

        super.onDestroy();

        mController.destroy();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mController.seekTo(seekBar.getProgress());
    }
}