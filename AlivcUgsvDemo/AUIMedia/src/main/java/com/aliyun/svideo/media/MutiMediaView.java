package com.aliyun.svideo.media;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.ugsv.common.utils.ToastUtils;
import com.aliyun.svideo.media.JsonExtend.JSONSupportImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cross_ly DATE 2019/08/12
 * <p>描述:多功能媒体选择自定义View
 */
public class MutiMediaView extends FrameLayout {
    public final static int MODE_NORMAL = 0;
    public final static int MODE_TEMPLATE_IMPORT = 0;

    private static final String TAG = "MutiMediaView";
    private MediaStorage mMediaStorage;
    private ThumbnailGenerator mThumbnailGenerator;
    private GalleryMediaChooser galleryMediaChooser;
    private TextView mTvTotalDuration;
    private ImageButton mBackBtn;
    private TextView mTitleTv;
    private SelectedMediaAdapter mSelectedVideoAdapter;
    private TemplateImportMediaAdapter mTemplateImportAdapter;
    private Button mBtnNextStep;
    private OnActionListener mOnActionListener;
    private OnTemplateActionListener mOnTemplateActionListener;
    private OnMediaClickListener mOnMediaClickListener;
    private OnSelectMediaChangeListener mOnSelectMediaChangeListener;

    /**
     * 是否达到最大时长
     */
    private boolean mIsReachedMaxDuration;
    private List<MediaInfo> mTemplateImportData = new ArrayList<>();
    private RecyclerView mRvSelectedView;
    private int mMode = MODE_NORMAL;

    private List<Long> mTemplateParams = new ArrayList<>();

    public MutiMediaView(@NonNull Context context) {
        this(context, null);
    }

    public MutiMediaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MutiMediaView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.alivc_media_activity_media_import, this);

        mBtnNextStep = findViewById(R.id.btn_next_step);
        mTitleTv = findViewById(R.id.gallery_title);
        mTitleTv.setText(R.string.alivc_media_gallery_all_media);
        mBackBtn = findViewById(R.id.gallery_closeBtn);
        if (mBtnNextStep != null) {
            mBtnNextStep.setOnClickListener(mOnClickLister);
        }
        mBackBtn.setOnClickListener(mOnClickLister);
        mMediaStorage = new MediaStorage(getContext(), new JSONSupportImpl());
        mThumbnailGenerator = new ThumbnailGenerator(getContext());
        RecyclerView galleryView = findViewById(R.id.gallery_media);
        GalleryDirChooser galleryDirChooser = new GalleryDirChooser(getContext(), findViewById(R.id.topPanel),
                mThumbnailGenerator, mMediaStorage);
        galleryMediaChooser = new GalleryMediaChooser(galleryView, galleryDirChooser, mMediaStorage,
                mThumbnailGenerator);
        mMediaStorage.setOnMediaDirChangeListener(new MediaStorage.OnMediaDirChange() {
            @Override
            public void onMediaDirChanged() {
                MediaDir dir = mMediaStorage.getCurrentDir();
                if (dir.id == -1) {
                    mTitleTv.setText(getContext().getString(R.string.alivc_media_gallery_all_media));
                } else {
                    mTitleTv.setText(dir.dirName);
                }
                galleryMediaChooser.changeMediaDir(dir);
            }
        });
        mMediaStorage.setOnCompletionListener(new MediaStorage.OnCompletion() {
            @Override
            public void onCompletion() {
                MediaDir dir = mMediaStorage.getCurrentDir();
                if (dir != null && dir.id != -1) {
                    //选择了媒体目录时，在加载完成的时候需要刷新一次
                    mTitleTv.setText(dir.dirName);
                    galleryMediaChooser.changeMediaDir(dir);
                }
            }
        });
        mMediaStorage.setOnCurrentMediaInfoChangeListener(new MediaStorage.OnCurrentMediaInfoChange() {
            @Override
            public void onCurrentMediaInfoChanged(MediaInfo info) {

                if (mOnMediaClickListener != null) {
                    mOnMediaClickListener.onClick(info);
                }
            }
        });

        mTvTotalDuration = findViewById(R.id.tv_duration_value);

        mTvTotalDuration.setText(convertDuration2Text(0));
        mTvTotalDuration.setActivated(false);

    }

    /**
     * 转化时间为文字
     *
     * @param duration 时长
     * @return String
     */
    private String convertDuration2Text(long duration) {
        int sec = Math.round(((float)duration) / 1000);
        int hour = sec / 3600;
        int min = (sec % 3600) / 60;
        sec = (sec % 60);
        return String.format(getContext().getString(R.string.alivc_media_video_duration),
                             hour,
                             min,
                             sec);
    }

    /**
     * 启用SelectView列表
     * @param maxDuration 选择视频的总计最大时长，单位ms 设置0时不限制
     */
    public void enableSelectView(long maxDuration) {

        if (mRvSelectedView == null) {
            findViewById(R.id.rl_select).setVisibility(VISIBLE);
            mRvSelectedView = findViewById(R.id.rv_selected_video);
            maxDuration = maxDuration == 0 ? Integer.MAX_VALUE : maxDuration;
            mSelectedVideoAdapter = new SelectedMediaAdapter(new MediaImageLoader(getContext()), maxDuration);
            mRvSelectedView.setAdapter(mSelectedVideoAdapter);
            mRvSelectedView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            mSelectedVideoAdapter.setItemViewCallback(new SelectedMediaAdapter.OnItemViewCallback() {
                @Override
                public void onItemPhotoClick(MediaInfo info, int position) {

                    if (mOnSelectMediaChangeListener != null) {
                        mOnSelectMediaChangeListener.onClick(info, position);
                    }
                }

                @Override
                public void onItemDeleteClick(MediaInfo info) {
                    if (mOnSelectMediaChangeListener != null) {
                        mOnSelectMediaChangeListener.onRemove(info);
                    }
                }

                @Override
                public void onDurationChange(long currDuration, boolean isReachedMaxDuration) {
                    mTvTotalDuration.setText(convertDuration2Text(currDuration));
                    mTvTotalDuration.setActivated(isReachedMaxDuration);
                    mIsReachedMaxDuration = isReachedMaxDuration;
                }
            });
        }

    }

    /**
     * 启用模板导入模式
     *
     * @param templateParams
     */
    public void enableTemplateImportView(List<Long> templateParams){
        if (templateParams == null || templateParams.isEmpty()) {
            return;
        }
        if (mRvSelectedView == null) {
            mBtnNextStep.setEnabled(false);
            ((TextView) findViewById(R.id.tv_duration_title)).setText(getResources().getString(R.string.alivc_media_video_template_import, templateParams.size()));
            mTvTotalDuration.setVisibility(GONE);
            findViewById(R.id.rl_select).setVisibility(VISIBLE);
            mRvSelectedView = findViewById(R.id.rv_selected_video);
            mTemplateImportAdapter = new TemplateImportMediaAdapter(new MediaImageLoader(getContext()));
            mTemplateImportAdapter.setTemplateParams(templateParams);
            mRvSelectedView.setAdapter(mTemplateImportAdapter);
            mRvSelectedView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            mTemplateImportAdapter.setItemViewCallback(new TemplateImportMediaAdapter.OnItemViewCallback() {
                @Override
                public void onItemPhotoClick(MediaInfo info, int position) {

                }

                @Override
                public void onItemDeleteClick(MediaInfo info) {
                    mBtnNextStep.setEnabled(false);
                }

                @Override
                public void onDurationChange(long currDuration) {
                    galleryMediaChooser.setMinDuration(currDuration);
                }

                @Override
                public void onFinish(List<MediaInfo> data) {
                    mTemplateImportData.clear();
                    mTemplateImportData.addAll(data);
                    mBtnNextStep.setEnabled(true);
                }
            });
            galleryMediaChooser.setMinDuration(templateParams.get(0));
        }
    }

    /**
     * 启用模板替换模式
     *
     * @param duration
     */
    public void enableTemplateReplace(long duration){
        List<Long> list = new ArrayList<>();
        list.add(duration);
        enableTemplateImportView(list);
        findViewById(R.id.rl_select).setVisibility(GONE);
    }

    /**
     * 设置select列表的media可以长按交换顺序，需要启用SelectView列表{@link #enableSelectView(long)} ()}
     */
    public void enableSwap() {
        if (mRvSelectedView == null) {
            Log.w(TAG, "设置enableSwap需要启用SelectView");
            return;
        }
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //首先回调的方法 返回int表示是否监听该方向
                int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//拖拽
                int swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//侧滑删除
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                //滑动事件
                mSelectedVideoAdapter.swap((SelectedMediaViewHolder)viewHolder, (SelectedMediaViewHolder)target);
                if (mOnSelectMediaChangeListener != null) {
                    mOnSelectMediaChangeListener.onSwap(recyclerView, viewHolder, target);
                }

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                //是否可拖拽
                return true;
            }
        });

        itemTouchHelper.attachToRecyclerView(mRvSelectedView);
    }

    private OnClickListener mOnClickLister = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mBackBtn) {
                if (mOnActionListener != null) {
                    mOnActionListener.onBack();
                }
                if (mOnTemplateActionListener != null) {
                    mOnTemplateActionListener.onBack();
                }
            } else if (v == mBtnNextStep) {
                if (mOnActionListener != null) {
                    mOnActionListener.onNext(mIsReachedMaxDuration);
                }
                if (mOnTemplateActionListener != null) {
                    mOnTemplateActionListener.onTemplateImport(mTemplateImportData);
                }
            }
        }
    };

    public void addSelectMedia(MediaInfo info) {
        if (mSelectedVideoAdapter != null) {
            mSelectedVideoAdapter.addMedia(info);
        } else if (mTemplateImportAdapter != null) {
            mTemplateImportAdapter.putData(info);
        }
    }

    public void setNextEnable(boolean isNextEnable) {
        mBtnNextStep.setEnabled(isNextEnable);
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        mOnActionListener = onActionListener;
    }

    public void setOnTemplateActionListener(OnTemplateActionListener onTemplateActionListener) {
        this.mOnTemplateActionListener = onTemplateActionListener;
    }

    public void setOnMediaClickListener(OnMediaClickListener onMediaClickListener) {
        mOnMediaClickListener = onMediaClickListener;
    }

    public void setOnSelectMediaChangeListener(
        OnSelectMediaChangeListener onSelectMediaChangeListener) {
        mOnSelectMediaChangeListener = onSelectMediaChangeListener;
    }

    public void onDestroy() {
        mMediaStorage.saveCurrentDirToCache();
        mMediaStorage.cancelTask();
        mThumbnailGenerator.cancelAllTask();
    }

    public void changeDurationPosition(int cropPosition, long duration) {
        mSelectedVideoAdapter.changeDurationPosition(cropPosition, duration);
    }

    public void setVideoDurationRange(int minVideoDuration, int maxVideoDuration) {
        mMediaStorage.setVideoDurationRange(minVideoDuration, maxVideoDuration);
    }

    public void setMode(int mode) {
        this.mMode = mode;
    }
    /**
     * 设置media种类
     * @param sortModeVideo {@link MediaStorage#SORT_MODE_VIDEO , SORT_MODE_PHOTO , SORT_MODE_MERGE}
     */
    public void setMediaSortMode(int sortModeVideo) {
        mMediaStorage.setSortMode(sortModeVideo);

    }

    public void loadMedia() {
        try {
            mMediaStorage.startFetchMedias();
        } catch (SecurityException e) {
            ToastUtils.show(getContext(), getResources().getString(R.string.alivc_common_no_permission));
        }
    }

    /**
     * 只能添加一个元素，如果也只需要获取一个元素，可以配合{@link #getOnlyOneMedia}使用
     */
    public void addOnlyFirstMedia(MediaInfo infoCopy) {
        mSelectedVideoAdapter.addOnlyFirstMedia(infoCopy);
    }

    public MediaInfo getOnlyOneMedia() {
        return mSelectedVideoAdapter.getOnlyOneMedia();
    }

    /**
     * 不存在已选择列表时，通过设置{@link OnMediaClickListener}来监听选择回调
     */
    public interface OnMediaClickListener {
        void onClick(MediaInfo info);
    }

    /**
     * 已选择media列表操作监听
     */
    public interface OnSelectMediaChangeListener {

        /**
         * 从选择的列表中删除
         */
        void onRemove(MediaInfo info);

        /**
         * 点击已选择的视频
         */
        void onClick(MediaInfo info, int position);

        /**
         * 交换已选择视频的位置
         *
         * @param recyclerView RecyclerView
         * @param viewHolder   ViewHolder
         * @param target       ViewHolder
         */
        void onSwap(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                    RecyclerView.ViewHolder target);

    }

    /**
     * 事件回调监听
     */
    public interface OnActionListener {
        /**
         * 是否达到了设置的最大时长
         * @param isReachedMaxDuration boolean
         */
        void onNext(boolean isReachedMaxDuration);

        void onBack();
    }

    public interface OnTemplateActionListener {

        void onTemplateImport(List<MediaInfo> data);

        void onBack();
    }
}
