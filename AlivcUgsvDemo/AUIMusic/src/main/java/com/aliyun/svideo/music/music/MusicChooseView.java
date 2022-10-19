package com.aliyun.svideo.music.music;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.common.utils.ToastUtil;
import com.aliyun.svideo.base.CopyrightWebActivity;
import com.aliyun.svideo.downloader.FileDownloaderCallback;
import com.aliyun.svideo.music.R;
import com.liulishuo.filedownloader.BaseDownloadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicChooseView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "MusicChooseView";
    private ImageView mAliyunMusicResetBtn;
    private RecyclerView mAliyunMusicRecyclerView;
    private MusicAdapter mMusicAdapter;
    private Handler mPlayHandler = new Handler(Looper.getMainLooper());
    private MediaPlayer mMediaPlayer;
    //视频录制时长
    private long mRecordTime = 10 * 1000;
    //截取音乐的开始时间
    private int mStartTime;
    //音乐播放循环时间
    private long mLoopTime = 10 * 1000;
    //选中音乐是否是本地音乐
    private boolean isLocalMusic = false;
    private int playedTime;
    private ArrayList<EffectBody<MusicFileBean>> mLocalMusicList = new ArrayList<>();
    private ArrayList<EffectBody<MusicFileBean>> mOnlineMusicList = new ArrayList<>();
    private MediaMetadataRetriever mmr;
    private MusicLoader musicLoader;
    private MusicSelectListener musicSelectListener;
    private MusicFileBean mSelectMusic;
    private TextView mAlivcMusicCopyrightTV;
    /**
     * 选中的角标
     */
    private int mSelectPosition;

    private boolean isViewAttached;
    /**
     * 用于判断当前界面是否可见, 如果不可见, 下载完成后不能自动播放
     */
    private boolean isVisible;

    /**
     * 判断该界面是否显示过
     */
    boolean isShowed;
    /**
     * 缓存上次选择的音乐
     */
    private MusicFileBean mCacheMusic;
    /**
     * 缓存上次选择的时间
     */
    private int mCacheStartTime;
    /**
     * 缓存上次选择的角标
     */
    private int mCachePosition;
    /**
     * 缓存上次选择的tab 网络/本地
     */
    private boolean mCacheIsLocalMusic;

    public MusicChooseView(Context context) {
        super(context);
        init();
    }

    public MusicChooseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MusicChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
        initData();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mMediaPlayer = new MediaPlayer();
        mmr = new MediaMetadataRetriever();
        isViewAttached = true;
        setVisibleStatus(true);
        if (mCacheIsLocalMusic) {
            selectLocalTab(mCachePosition);
        } else {
            selectOnlineTab(mCachePosition);
        }
        //恢复上次选择的音乐和开始时间 并且开始播放
        if (isShowed && mCacheMusic != null && mMusicAdapter != null) {

            mMusicAdapter.notifySelectPosition(mCacheStartTime, mCachePosition);
            mAliyunMusicRecyclerView.scrollToPosition(mCachePosition);
            Log.d(TAG, "onAttachedToWindow notifySelectPosition");
            try {
                prepareMusicInfo(mCacheMusic);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setLooping(true);
            mPlayHandler.postDelayed(mMusciRunnable, 0);
        } else if (isShowed && mMusicAdapter != null) {
            mMusicAdapter.notifySelectPosition(0, -1);
            mAliyunMusicRecyclerView.scrollToPosition(0);
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setVisibleStatus(false);
        isViewAttached = false;
        mPlayHandler.removeCallbacks(mMusciRunnable);
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mmr.release();
    }


    private void initData() {
        if (musicLoader == null) {
            musicLoader = new MusicLoader(getContext().getApplicationContext());
            musicLoader.setCallback(new MusicLoader.LoadCallback() {
                @Override
                public void onLoadLocalMusicCompleted(List<EffectBody<MusicFileBean>> loacalMusic) {
                    mLocalMusicList.clear();
                    mLocalMusicList.addAll(loacalMusic);
                    if (isLocalMusic) {
                        mMusicAdapter.setData(mLocalMusicList, 0);
                    }
                }

                @Override
                public void onLoadNetMusicCompleted(List<EffectBody<MusicFileBean>> netMusic) {
                    mOnlineMusicList.addAll(netMusic);
                    if (!isLocalMusic) {
                        mMusicAdapter.setData(mOnlineMusicList, 0);
                    }
                }

                @Override
                public void onSearchNetMusicCompleted(List<EffectBody<MusicFileBean>> result) {

                }
            });
            musicLoader.loadAllMusic();
        }

    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.alivc_music_view_chooser_music, this, true);
        mAliyunMusicResetBtn = findViewById(R.id.ugsv_music_reset);
        mAliyunMusicResetBtn.setOnClickListener(this);
        mAliyunMusicResetBtn.setEnabled(false);
        mAliyunMusicRecyclerView = findViewById(R.id.aliyun_music_list);
        mAlivcMusicCopyrightTV = findViewById(R.id.alivc_music_copyright);
        mAlivcMusicCopyrightTV.setText(getClickalbeSpan());
        mAlivcMusicCopyrightTV.setMovementMethod(LinkMovementMethod.getInstance());
        setFocusable(true);

        mAliyunMusicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (mMusicAdapter == null) {
            mMusicAdapter = new MusicAdapter();
            mMusicAdapter.setStreamDuration(mRecordTime);
            mMusicAdapter.setOnMusicSeekListener(new MusicAdapter.OnMusicSeek() {
                @Override
                public void onSeekStop(long start) {
                    mPlayHandler.removeCallbacks(mMusciRunnable);
                    mStartTime = (int)start;
                    mPlayHandler.postDelayed(mMusciRunnable, 0);
                    if (musicSelectListener != null && mSelectMusic != null) {
                        musicSelectListener.onMusicSelect(mSelectMusic, mStartTime);
                    }
                }

                @Override
                public void onSelectMusic(final int position, final EffectBody<MusicFileBean> effectBody) {
                    final MusicFileBean musicFileBean = effectBody.getData();
                    mSelectMusic = musicFileBean;
                    mSelectPosition = position;

                    //判断音乐是否已经加载完毕
                    if (effectBody.isLocal()) {
                        onMusicSelected(musicFileBean, position);
                    } else {

                        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                            //如果正在播放音乐，停止播放
                            mMediaPlayer.stop();
                        }
                        musicLoader.downloadMusic(musicFileBean, new FileDownloaderCallback() {
                            @Override
                            public void onStart(int downloadId, long soFarBytes, long totalBytes, int preProgress) {
                                super.onStart(downloadId, soFarBytes, totalBytes, preProgress);
                                if (!isLocalMusic) {
                                    mMusicAdapter.notifyDownloadingStart(effectBody);
                                }

                            }

                            @Override
                            public void onProgress(int downloadId, long soFarBytes, long totalBytes, long speed,
                                                   int progress) {
                                super.onProgress(downloadId, soFarBytes, totalBytes, speed, progress);
                                if (!isLocalMusic) {
                                    mMusicAdapter.updateProcess(
                                        (MusicAdapter.MusicViewHolder) mAliyunMusicRecyclerView
                                        .findViewHolderForAdapterPosition(position), progress, position);
                                }
                            }

                            @Override
                            public void onFinish(int downloadId, String path) {
                                super.onFinish(downloadId, path);
                                effectBody.getData().setPath(path);
                                if (mMusicAdapter == null) {
                                    return;
                                }
                                // 无论是否可见, 都要去刷新界面信息
                                // 否则在下载过程中退后台, 当下载进度完成再返回前台时, 下载进度会卡在99%的状态
                                if (position == mMusicAdapter.getSelectIndex() && !isLocalMusic) {
                                    onMusicSelected(effectBody.getData(), position);

                                }
                                mMusicAdapter.notifyDownloadingComplete((MusicAdapter.MusicViewHolder)
                                                                        mAliyunMusicRecyclerView
                                                                        .findViewHolderForAdapterPosition(position), effectBody, position);

                            }

                            @Override
                            public void onError(BaseDownloadTask task, Throwable e) {
                                super.onError(task, e);
                                ToastUtil.showToast(getContext(), e.getMessage());
                            }
                        });

                    }

                }
            });
        }
        mAliyunMusicRecyclerView.setAdapter(mMusicAdapter);
        mAliyunMusicRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !isLocalMusic) {
                    LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int totalItemCount = recyclerView.getAdapter().getItemCount();
                    int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                    int visibleItemCount = recyclerView.getChildCount();

                    if (lastVisibleItemPosition > totalItemCount - 5
                            && visibleItemCount > 5) {
                        //加载更多
                        musicLoader.loadMoreOnlineMusic();
                    }
                }
            }
        });


    }

    private void onMusicSelected(MusicFileBean musicFileBean, int position) {


        if (mCachePosition != position ) {
            //恢复时，不能重置
            mStartTime = 0;
        } else {
            mStartTime = mCacheStartTime;
        }

        if (mCacheIsLocalMusic != isLocalMusic) {
            //切换tab时重置startTime
            mStartTime = 0;
        }
        mAliyunMusicResetBtn.setEnabled(true);
        try {
            if (isVisible) {
                prepareMusicInfo(musicFileBean);
                mMusicAdapter.notifyItemChanged(position);
                mMediaPlayer.setLooping(true);
                mPlayHandler.postDelayed(mMusciRunnable, 0);
            } else if (isShowed) {
                // 如果界面不可见, 且曾经显示过, 再去更新item信息, 但不能播放
                prepareMusicInfo(musicFileBean);
                mMusicAdapter.notifyItemChanged(position);
                mMediaPlayer.setLooping(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        if (musicSelectListener != null) {
            Log.i(TAG, "log_music_start_time : " + mStartTime);

            musicSelectListener.onMusicSelect(mSelectMusic, mStartTime);
            //缓存选择的值
            mCacheMusic = mSelectMusic;
            mCacheStartTime = mStartTime;
            mCachePosition = mSelectPosition;
            mCacheIsLocalMusic = isLocalMusic;
        }
    }

    /**
     * 准备要播放的音乐资源
     */
    private void prepareMusicInfo(MusicFileBean musicFileBean) throws IOException, IllegalStateException {
        mPlayHandler.removeCallbacks(mMusciRunnable);
        mMediaPlayer.reset();
        Uri uri = null;
        if (!TextUtils.isEmpty(musicFileBean.path)) {
            uri = Uri.parse(musicFileBean.path);
        }
        if (!TextUtils.isEmpty(musicFileBean.uri)) {
            uri = Uri.parse(musicFileBean.uri);
        }
        if (uri == null) {
            return;
        }
        mMediaPlayer.setDataSource(getContext(), uri);
        mMediaPlayer.prepare();

        int duration = mMediaPlayer.getDuration();
        if (mSelectMusic != null) {
            mSelectMusic.duration = duration;
        }
        if (duration < mRecordTime) {
            mLoopTime = duration;
        } else {
            mLoopTime = mRecordTime;
        }
        musicFileBean.setDuration(duration);

    }

    private Runnable mMusciRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (MusicChooseView.class) {
                if (isVisible) {

                    mMediaPlayer.seekTo(mStartTime);
                    mMediaPlayer.start();
                    mPlayHandler.postDelayed(this, mLoopTime);
                }
            }

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //取消的话，不能记录上一次未mAliyunCompeletBtn的值
            mSelectMusic = mCacheMusic;
            mStartTime = mCacheStartTime;
            mSelectPosition = mCachePosition;
            isLocalMusic = mCacheIsLocalMusic;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (v == mAliyunMusicResetBtn) {
            mSelectMusic = new MusicFileBean();
            mSelectPosition = -1;
            mCachePosition = -1;
            mCacheStartTime = 0;
            mCacheMusic = null;
            musicSelectListener.onMusicSelect(mSelectMusic, 0);
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                //如果正在播放音乐，停止播放
                mMediaPlayer.stop();
            }
            mAliyunMusicResetBtn.setEnabled(false);
            mMusicAdapter.notifySelectPosition(0, -1);
        }
    }

    private void selectOnlineTab(int index) {
        mMusicAdapter.setData(mOnlineMusicList, index);
        mAlivcMusicCopyrightTV.setVisibility(View.GONE);
    }

    private void selectLocalTab(int index) {
        mMusicAdapter.setData(mLocalMusicList, index);
        mAlivcMusicCopyrightTV.setVisibility(View.GONE);
    }

    public void setMusicSelectListener(MusicSelectListener musicSelectListener) {
        this.musicSelectListener = musicSelectListener;
    }

    public void setStreamDuration(long streamTime) {
        this.mRecordTime = streamTime;
        if (mMusicAdapter != null) {
            mMusicAdapter.setStreamDuration(streamTime);
        }
    }

    Runnable mStartRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (MusicChooseView.class) {
                if (isVisible) {
                    mMediaPlayer.start();
                }
            }
        }
    };

    /**
     * 设置view的可见状态, 如果不可见, 则停止音乐播放, 如果可见,开始播放
     *
     * @param visibleStatus true: 可见, false: 不可见
     */
    public void setVisibleStatus(final boolean visibleStatus) {
        if (isViewAttached) {
            if (visibleStatus) {
                //当重进view时，判断网络列表是否为空，如果为空则加载网络音乐
                if (mOnlineMusicList.isEmpty()) {
                    musicLoader.loadMoreOnlineMusic();
                }
                //在部分手机上锁屏亮屏会调用onResume和onPause，这里处理一下
                mPlayHandler.removeCallbacks(mStartRunnable);
                mPlayHandler.removeCallbacks(mMusciRunnable);
                mPlayHandler.postDelayed(mStartRunnable, 500);
                mPlayHandler.postDelayed(mMusciRunnable, mLoopTime - playedTime);
                isShowed = true;

            } else {
                mPlayHandler.removeCallbacks(mStartRunnable);
                mPlayHandler.removeCallbacks(mMusciRunnable);
                if (mMediaPlayer.isPlaying()) {
                    playedTime = mMediaPlayer.getCurrentPosition() - mStartTime;
                    synchronized (MusicChooseView.class) {
                        mMediaPlayer.pause();
                        isVisible = false;
                    }
                }
            }
        }
        isVisible = visibleStatus;
    }

    /**
     * 获取跳转到版权页面的字符串
     * @return
     */
    private SpannableString getClickalbeSpan() {
        String copyright = getContext().getResources().getString(R.string.alivc_music_copyright);
        String copyrightLink = getContext().getResources().getString(R.string.alivc_music_copyright_link);
        final int start = copyright.length();
        int end = copyright.length() + copyrightLink.length();
        SpannableString spannableString = new SpannableString(copyright + copyrightLink);
        spannableString.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(getContext(), CopyrightWebActivity.class);
                getContext().startActivity(intent);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.alivc_common_cyan_light)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

}
