package com.alivc.player.videolist.auivideofunctionlist;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alivc.player.videolist.auivdieofunctionlist.R;
import com.alivc.player.videolist.auivideofunctionlist.adapter.AUIVideoFunctionListAdapter;
import com.alivc.player.videolist.auivideofunctionlist.domain.GestureGuidanceUseCase;
import com.alivc.player.videolist.auivideofunctionlist.player.AliPlayerPool;
import com.alivc.player.videolist.auivideofunctionlist.utils.GlobalSettings;
import com.alivc.player.videolist.auivideolistcommon.AUIVideoListView;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListAdapter;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListDiffCallback;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class AUIVideoFunctionListView extends AUIVideoListView {

    private AUIVideoListController mController;
    private AUIVideoListViewHolder mViewHolderForAdapterPosition;

    private Context mContext;
    private AUIVideoFunctionListAdapter mAUIVideoListAdapter;
    private boolean mAutoPlayNext;


    public AUIVideoFunctionListView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AUIVideoFunctionListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AUIVideoFunctionListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        AliPlayerPool.init(mContext);

        //Local Cache Dir  TODO 修改文件路径
        GlobalSettings.CACHE_DIR = mContext.getExternalCacheDir().getAbsolutePath() + File.separator + "Preload";
        GestureGuidanceUseCase gestureGuidanceUseCase = new GestureGuidanceUseCase(mContext);
        //TODO
//        AUIVideoListLocalDataSource auiVideoListLocalDataSource = new AUIVideoListLocalDataSource(mContext);
        mController = new AUIVideoListController(gestureGuidanceUseCase);

        initObserver();
    }


    private void initObserver() {
        mController.mGestureGuidanceLiveData.observe(this, shown -> {
            if (!shown) {
                showInteractiveGuidance();
            }
        });
    }
    @Override
    protected AUIVideoListAdapter initAUIVideoListAdapter(Context context) {
        mAUIVideoListAdapter = new AUIVideoFunctionListAdapter(new AUIVideoListDiffCallback());
        return mAUIVideoListAdapter;
    }

    public void showInteractiveGuidance() {
        mController.showGestureGuidanceLiveData();
        //TODO
//        mGestureLinearLayout.setVisibility(View.VISIBLE);
//        mGestureLinearLayout.postDelayed(() -> mGestureLinearLayout.setVisibility(View.GONE), 3000);
    }

    public void showPlayIcon(boolean isShow) {
        if (mViewHolderForAdapterPosition != null) {
            mViewHolderForAdapterPosition.showPlayIcon(isShow);
        }
    }

    /**
     * video duration
     *
     * @param duration company ms
     */
    public void onVideoFrameShow(long duration) {
        mViewHolderForAdapterPosition = findRecyclerViewLastVisibleHolder();
        if (mViewHolderForAdapterPosition != null) {
            mViewHolderForAdapterPosition.getSeekBar().setMax((int) duration);
        }
    }

    /**
     * update Current Position
     *
     * @param extraValue ms
     */
    public void updateCurrentPosition(long extraValue) {
        if (mViewHolderForAdapterPosition != null) {
            mViewHolderForAdapterPosition.getSeekBar().setProgress((int) extraValue);
        }
    }

    public void showError(ErrorInfo errorInfo) {
        AVToast.show(mContext, true, "error: " + errorInfo.getCode() + " -- " + errorInfo.getMsg());
    }

    public void loadMore() {
        AVToast.show(mContext, true, R.string.aui_video_list_coming_soon);
    }

    @Override
    public void loadSources(List<VideoInfo> videoBeanList) {
        super.loadSources(videoBeanList);
        mController.loadSources(videoBeanList);
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
    public void onItemClick(int position) {
        mController.changePlayState();
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
//        removeAndAddView(position);
//        mController.start(position);

        mController.changeSource(position);
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            mController.onPageSelected(position, viewHolder);
        }
    }

    @Override
    public void onPageRelease(int position) {
//        if (mGestureLinearLayout.isShown()) {
//            mGestureLinearLayout.setVisibility(View.GONE);
//        }

        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            mController.onPageRelease(position, viewHolder);
        }
    }

    @Override
    public void onPageHideHalf(int position) {
//        AUIVideoListAdapter.AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
//        if (viewHolder != null) {
//            mController.onPageHideHalf(position,viewHolder);
//        }

        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            mController.onPageHideHalf(position, viewHolder);
        }
    }

    /**
     * Player Listener onPrepared
     */
    @Override
    public void onPrepared(int position) {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            mController.onPrepared(position, viewHolder);
        }
    }

    /**
     * Player Listener onInfo
     */
    @Override
    public void onInfo(int position, InfoBean infoBean) {
        if (infoBean.getCode() == InfoCode.BufferedPosition) {
            long buffer = infoBean.getExtraValue();
            mController.updateBufferPosition(position, buffer);
        } else if (infoBean.getCode() == InfoCode.CurrentPosition) {
            updateCurrentPosition(infoBean.getExtraValue());
        }
    }

    @Override
    public void onPlayStateChanged(int position, boolean isPaused) {
        showPlayIcon(isPaused);
    }

    @Override
    public void onRenderingStart(int position,long duration) {
        onVideoFrameShow(duration);
    }

    @Override
    public void onCompletion(int position) {
        if (position < mAUIVideoListAdapter.getItemCount() && mAutoPlayNext) {
            mRecyclerView.smoothScrollToPosition(position + 1);
        }
    }

    @Override
    public void onSeek(int position, long seekPosition) {
        AUIVideoListViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            mController.seekTo(seekPosition,viewHolder);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Glide.get(mContext).clearMemory();
        new Thread(() -> Glide.get(mContext).clearDiskCache()).start();
        mController.destroy();
    }
}


//public class AUIVideoListActivity extends AppCompatActivity implements CustomLayoutManager.OnViewPagerListener,
//        RecyclerViewAdapter.OnItemClickListener, SeekBar.OnSeekBarChangeListener {
//
//    private RecyclerView mRecyclerView;
//    private CustomLayoutManager mCustomLayoutManager;
//    private AUIVideoListController mController;
//    private TextureView mTextureView;
//    private RecyclerViewAdapter.RecyclerViewHolder mViewHolderForAdapterPosition;
//    private LinearLayout mGestureLinearLayout;
//    private int mSelectedPosition;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_aui_video_list);
//        mController = new AUIVideoListController(this);
//
//        initView();
//        initRecyclerView();
//        initTextureView();
//
//        mController.initSP();
//    }
//
//    private void initView(){
//        mRecyclerView = findViewById(R.id.recyclerview);
//        mGestureLinearLayout = findViewById(R.id.ll_gesture);
//
//        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
//        mGestureLinearLayout.setOnClickListener(view -> mGestureLinearLayout.setVisibility(View.GONE));
//    }
//
//    private void initRecyclerView(){
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setItemAnimator(null);
//        mRecyclerView.setItemViewCacheSize(5);
//        mRecyclerView.setDrawingCacheEnabled(true);
//        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        RecyclerViewAdapter mRecyclerViewAdapter = new RecyclerViewAdapter(this);
//        mCustomLayoutManager = new CustomLayoutManager(this, LinearLayoutManager.VERTICAL,false);
//        mCustomLayoutManager.setItemPrefetchEnabled(true);
//        mCustomLayoutManager.setPreloadItemCount(2);
//        mCustomLayoutManager.setOnViewPagerListener(this);
//        mRecyclerView.setLayoutManager(mCustomLayoutManager);
//        mRecyclerView.setAdapter(mRecyclerViewAdapter);
//
//        mRecyclerViewAdapter.setData(mController.getData());
//        mRecyclerViewAdapter.setOnItemClickListener(this);
//        mRecyclerViewAdapter.setOnSeekBarStateChangeListener(this);
//    }
//
//    private void initTextureView(){
//        mTextureView = new TextureView(this);
//        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
//            @Override
//            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
//                Surface surface = new Surface(surfaceTexture);
//                mController.setRenderView(surface);
//            }
//
//            @Override
//            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
//                mController.surfacChanged();
//            }
//
//            @Override
//            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
//                mController.setRenderView(null);
//                return false;
//            }
//
//            @Override
//            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) { }
//        });
//    }
//
//    /**
//     * hide Cover ImageView
//     */
//    public void hideCoverView(){
//        RecyclerViewAdapter.RecyclerViewHolder viewHolderForAdapterPosition = (RecyclerViewAdapter.RecyclerViewHolder) mRecyclerView.findViewHolderForAdapterPosition(mSelectedPosition);
//        if(viewHolderForAdapterPosition != null){
//            viewHolderForAdapterPosition.getCoverView().setVisibility(View.INVISIBLE);
//        }
//    }
//
//    /**
//     * show Cover ImageView
//     */
//    public void showCoverView(int position){
//        RecyclerViewAdapter.RecyclerViewHolder viewHolderForAdapterPosition = (RecyclerViewAdapter.RecyclerViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
//        if(viewHolderForAdapterPosition != null){
//            viewHolderForAdapterPosition.getCoverView().setVisibility(View.VISIBLE);
//        }
//    }
//
//    public void showInteractiveGuidance(){
//        mGestureLinearLayout.setVisibility(View.VISIBLE);
//        mGestureLinearLayout.postDelayed(() -> mGestureLinearLayout.setVisibility(View.GONE),3000);
//    }
//
//    public void showPlayIcon(boolean isShow){
//        if(mViewHolderForAdapterPosition != null){
//            mViewHolderForAdapterPosition.showPlayIcon(isShow);
//        }
//    }
//
//    /**
//     * video duration
//     * @param duration company ms
//     */
//    public void onVideoFrameShow(long duration) {
//        mViewHolderForAdapterPosition = findRecyclerViewLastVisibleHolder();
//        if(mViewHolderForAdapterPosition != null){
//            mViewHolderForAdapterPosition.getSeekBar().setMax((int) duration);
//        }
//    }
//
//
//    /**
//     * add TextureView
//     */
//    private void removeAndAddView(int position){
//        ViewParent parent = mTextureView.getParent();
//        if (parent instanceof FrameLayout) {
//            ((ViewGroup) parent).removeView(mTextureView);
//        }
//        RecyclerViewAdapter.RecyclerViewHolder holder = (RecyclerViewAdapter.RecyclerViewHolder) mRecyclerView.findViewHolderForLayoutPosition(position);
//        if (holder != null) {
//            holder.getFrameLayout().addView(mTextureView);
//        }
//    }
//
//    public void showError(ErrorInfo errorInfo) {
//        AVToast.show(this,true,"error: " + errorInfo.getCode() + " -- " + errorInfo.getMsg());
//    }
//
//    public void loadMore() {
//        AVToast.show(this,true,R.string.aui_video_list_coming_soon);
//    }
//
//    private RecyclerViewAdapter.RecyclerViewHolder findRecyclerViewLastVisibleHolder(){
//        int lastVisibleItemPosition = mCustomLayoutManager.findLastVisibleItemPosition();
//        return (RecyclerViewAdapter.RecyclerViewHolder) mRecyclerView.findViewHolderForAdapterPosition(lastVisibleItemPosition);
//    }
//
//    @Override
//    public void onItemClick(int position) {
//        mController.changePlayState();
//    }
//
//    @Override
//    public void onPageShow(int position) {
//        if(position != mSelectedPosition){
//            showCoverView(position);
//        }
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//        this.mSelectedPosition = position;
//        removeAndAddView(position);
//        mController.start(position);
//    }
//
//    @Override
//    public void onPageRelease() {
//        if(mGestureLinearLayout.isShown()){
//            mGestureLinearLayout.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        Glide.get(AUIVideoListActivity.this).clearMemory();
//        new Thread(() -> Glide.get(AUIVideoListActivity.this).clearDiskCache()).start();
//
//        super.onDestroy();
//
//        mController.destroy();
//    }
//
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
//
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        mController.seekTo(seekBar.getProgress());
//    }
//}
