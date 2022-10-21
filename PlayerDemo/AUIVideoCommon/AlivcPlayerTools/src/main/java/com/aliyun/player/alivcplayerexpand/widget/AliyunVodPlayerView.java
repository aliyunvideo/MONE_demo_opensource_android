package com.aliyun.player.alivcplayerexpand.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.aio.utils.DensityUtil;
import com.aliyun.aio.utils.ThreadUtils;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.VidPlayerConfigGen;
import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.background.PlayServiceHelper;
import com.aliyun.player.alivcplayerexpand.bean.DotBean;
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.alivcplayerexpand.listplay.IListPlayManager;
import com.aliyun.player.alivcplayerexpand.listplay.IPlayManagerScene;
import com.aliyun.player.alivcplayerexpand.listplay.ListPlayManager;
import com.aliyun.player.alivcplayerexpand.theme.Theme;
import com.aliyun.player.alivcplayerexpand.listener.LockPortraitListener;
import com.aliyun.player.alivcplayerexpand.listener.OnAutoPlayListener;
import com.aliyun.player.alivcplayerexpand.listener.OnScreenCostingSingleTagListener;
import com.aliyun.player.alivcplayerexpand.listener.OnStoppedListener;
import com.aliyun.player.alivcplayerexpand.theme.ITheme;
import com.aliyun.player.alivcplayerexpand.util.BrowserCheckUtil;
import com.aliyun.player.alivcplayerexpand.util.TimeFormater;
import com.aliyun.player.alivcplayerexpand.view.control.ControlView;
import com.aliyun.player.alivcplayerexpand.view.dlna.callback.DLNAOptionListener;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.Config;
import com.aliyun.player.alivcplayerexpand.view.function.AdvVideoView;
import com.aliyun.player.alivcplayerexpand.view.function.MarqueeView;
import com.aliyun.player.alivcplayerexpand.view.function.MutiSeekBarView;
import com.aliyun.player.alivcplayerexpand.view.function.PlayerDanmakuView;
import com.aliyun.player.alivcplayerexpand.view.function.WaterMarkRegion;
import com.aliyun.player.alivcplayerexpand.view.gesture.GestureDialogManager;
import com.aliyun.player.alivcplayerexpand.view.gesture.GestureView;
import com.aliyun.player.alivcplayerexpand.view.guide.GuideView;
import com.aliyun.player.alivcplayerexpand.view.interfaces.ViewAction;
import com.aliyun.player.alivcplayerexpand.view.more.DanmakuSettingView;
import com.aliyun.player.alivcplayerexpand.view.more.ScreenCostingView;
import com.aliyun.player.alivcplayerexpand.view.quality.QualityView;
import com.aliyun.player.alivcplayerexpand.view.thumbnail.ThumbnailView;
import com.aliyun.player.alivcplayerexpand.view.trailers.TrailersView;
import com.aliyun.player.alivcplayerexpand.view.voice.AudioModeView;
import com.aliyun.player.aliyunplayerbase.util.AliyunScreenMode;
import com.aliyun.player.aliyunplayerbase.util.FileUtils;
import com.aliyun.player.aliyunplayerbase.util.ImageLoader;
import com.aliyun.player.aliyunplayerbase.util.NetWatchdog;
import com.aliyun.player.aliyunplayerbase.util.OrientationWatchDog;
import com.aliyun.player.aliyunplayerbase.util.ScreenUtils;
import com.aliyun.player.aliyunplayerbase.view.tipsview.OnTipsViewBackClickListener;
import com.aliyun.player.aliyunplayerbase.view.tipsview.TipsView;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.CacheConfig;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.nativeclass.Thumbnail;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.LiveSts;
import com.aliyun.player.source.StsInfo;
import com.aliyun.player.source.UrlSource;
import com.aliyun.player.source.VidAuth;
import com.aliyun.player.source.VidMps;
import com.aliyun.player.source.VidSts;
import com.aliyun.subtitle.LocationStyle;
import com.aliyun.subtitle.SubtitleView;
import com.aliyun.thumbnail.ThumbnailBitmapInfo;
import com.aliyun.thumbnail.ThumbnailHelper;
import com.cicada.player.utils.Logger;

import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.model.TransportStatus;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * UI播放器的主要实现类。 通过ITheme控制各个界面的主题色。 通过各种view的组合实现UI的界面。这些view包括： 用户手势操作的{@link GestureView} 控制播放，显示信息的{@link
 * ControlView} 显示清晰度列表的{@link QualityView} 用户使用引导页面{@link GuideView} 用户提示页面{@link TipsView}
 * 以及封面等。 view 的初始化是在{@link #initVideoView}方法中实现的。 然后是对各个view添加监听方法，处理对应的操作，从而实现与播放器的共同操作
 */
public class AliyunVodPlayerView extends RelativeLayout implements ITheme {

    private final SharedPreferences mLongPressSP = getContext().getSharedPreferences("long_press", Context.MODE_PRIVATE);

    /**
     * 视频广告
     */
    private static final String ADV_VIDEO_URL = "https://alivc-demo-cms.alicdn.com/video/videoAD.mp4";
    /**
     * 广告链接
     */
    private static final String ADV_URL = "https://www.aliyun.com/product/vod?spm=5176.10695662.782639.1.4ac218e2p7BEEf";

    /**
     * 水印展示位置
     */
    private static final WaterMarkRegion WATER_MARK_REGION = WaterMarkRegion.RIGHT_TOP;

    /**
     * 跑马灯显示区域
     */
    private static final MarqueeView.MarqueeRegion MARQUEE_REGION = MarqueeView.MarqueeRegion.TOP;

    private static final String TAG = AliyunVodPlayerView.class.getSimpleName();

    /**
     * 视频广告prepared完成
     */
    private static final int ADV_VIDEO_PREPARED = 0;

    /**
     * 原视频preapred完成
     */
    private static final int SOURCE_VIDEO_PREPARED = 1;

    /**
     * 判断VodePlayer 是否加载完成
     */
    private final Map<MediaInfo, Boolean> hasLoadEnd = new HashMap<>();

    //视频画面
    private SurfaceView mSurfaceView;
    private AudioModeView mAudioModeView;
    //手势操作view
    private GestureView mGestureView;
    //皮肤view
    private ControlView mControlView;
    private View mLongPressVideoSpeedView;
    private int mDanmakuLocation = 2;
    private boolean mDanmakuOpen = true;
    //清晰度view
    private QualityView mQualityView;
    //引导页view
    private GuideView mGuideView;
    //封面view
    private ImageView mCoverView;
    //手势对话框控制
    private GestureDialogManager mGestureDialogManager;
    //网络状态监听
    private NetWatchdog mNetWatchdog;
    //屏幕方向监听
    private OrientationWatchDog mOrientationWatchDog;
    //Tips view
    private TipsView mTipsView;
    //锁定竖屏
    private LockPortraitListener mLockPortraitListener = null;
    //是否锁定全屏
    private boolean mIsFullScreenLocked = false;
    //当前屏幕模式
    private AliyunScreenMode mCurrentScreenMode = AliyunScreenMode.Small;
    //是不是在seek中
    private boolean inSeek = false;
    //播放是否完成
    private boolean isCompleted = false;
    //媒体信息
    private MediaInfo mAliyunMediaInfo;
    //视频广告媒体信息
    private MediaInfo mAdvVideoMediaInfo;
    //跑马灯
    private MarqueeView mMarqueeView;
    /**
     * 缩略图View
     */
    private ThumbnailView mThumbnailView;
    /**
     * 缩略图帮助类
     */
    private ThumbnailHelper mThumbnailHelper;
    //获取缩略图是否成功
    private boolean mThumbnailPrepareSuccess = false;

    //初始化handler
    private VodPlayerHandler mVodPlayerHandler;
    //解决bug,进入播放界面快速切换到其他界面,播放器仍然播放视频问题
    private final VodPlayerLoadEndHandler vodPlayerLoadEndHandler = new VodPlayerLoadEndHandler(this);
    //原视频的buffered
    private long mVideoBufferedPosition = 0;
    //原视频的currentPosition
    private long mCurrentPosition = 0;
    //视频广告的 currentPosition
    private long mAdvCurrentPosition;
    //视频广告的总 position
    private long mAdvTotalPosition = 0;
    //当前播放器的状态
    private int mPlayerState = IPlayer.idle;
    private boolean mPlayingBeforePause = true;
    //广告视频的展示位置,默认是开始--中间--末尾都添加广告视频   todo   还未提供接口可以修改
    private final MutiSeekBarView.AdvPosition mAdvPosition = MutiSeekBarView.AdvPosition.ALL;
    //原视频时长
    private long mSourceDuration;
    //广告视频时长
    private long mAdvDuration;
    //视频广告View
    private AdvVideoView mAdvVideoView;
    //弹幕view
    private PlayerDanmakuView mDanmakuView;
    //水印
    private ImageView mWaterMark;
    //试看View
    private TrailersView mTrailersView;
    //投屏中的View
    private ScreenCostingView mScreenCostingView;
    //是否是在投屏中
    private boolean mIsScreenCosting = false;
    private boolean mIsAudioMode = false;
    //字幕
    private SubtitleView mSubtitleView;
    private View mContrastPlayTipView;

    //目前支持的几种播放方式
    private VidAuth mAliyunPlayAuth;
    private VidMps mAliyunVidMps;
    private UrlSource mAliyunLocalSource;
    private VidSts mAliyunVidSts;
    private LiveSts mAliyunLiveSts;

    //对外的各种事件监听
    private OnFinishListener mOnFinishListener = null;
    private IPlayer.OnInfoListener mOutInfoListener = null;
    private IPlayer.OnErrorListener mOutErrorListener = null;
    private OnAutoPlayListener mOutAutoPlayListener = null;
    private IPlayer.OnPreparedListener mOutPreparedListener = null;
    private IPlayer.OnCompletionListener mOutCompletionListener = null;
    private IPlayer.OnSeekCompleteListener mOuterSeekCompleteListener = null;
    private IPlayer.OnTrackChangedListener mOutOnTrackChangedListener = null;
    private IPlayer.OnRenderingStartListener mOutFirstFrameStartListener = null;
    private OnScreenCostingSingleTagListener mOnScreenCostingSingleTagListener = null;
    private OnScreenBrightnessListener mOnScreenBrightnessListener = null;
    private OnTimeExpiredErrorListener mOutTimeExpiredErrorListener = null;
    private OnTipsViewBackClickListener mOutOnTipsViewBackClickListener = null;
    private OnSoftKeyHideListener mOnSoftKeyHideListener = null;
    private TrailersView.OnTrailerViewClickListener mOnTrailerViewClickListener = null;
    private IPlayer.OnSeiDataListener mOutOnSeiDataListener = null;
    private AliPlayer.OnVerifyTimeExpireCallback mOutOnVerifyTimeExpireCallback = null;
    private TipsView.OnTipClickListener mOutOnTipClickListener = null;
    // 连网断网监听
    private NetConnectedListener mNetConnectedListener = null;
    // 横屏状态点击更多
    private ControlView.OnShowMoreClickListener mOutOnShowMoreClickListener;
    private ControlView.OnVideoSpeedClickListener mOnVideoSpeedClickListener;
    //播放按钮点击监听
    private OnPlayStateBtnClickListener onPlayStateBtnClickListener;
    //停止按钮监听
    private OnStoppedListener mOnStoppedListener;
    //ControView隐藏事件
    private ControlView.OnControlViewHideListener mOnControlViewHideListener;
    private ControlView.OnFloatPlayViewClickListener mOnFloatPlayViewClickListener;
    private ControlView.OnCastScreenListener onCastScreenListener;
    private ControlView.OnDamkuOpenListener onDamkuOpenListener;
    private ControlView.OnSelectSeriesListener onSelectSeriesListener;
    private ControlView.OnNextSeriesClick onNextSeriesClick;
    //投屏时,视频播放完成回调事件
    private OnScreenCostingVideoCompletionListener mOnScreenCostingVideoCompletionListener;
    //字幕、清晰度、音轨、码率点击事件
    private ControlView.OnTrackInfoClickListener mOutOnTrackInfoClickListener;
    private ControlView.OnBackClickListener mOutOnBackClickListener;
    //广告播放器的当前状态
    private int mAdvVideoPlayerState;
    //seekTo的位置
    private int mSeekToPosition;
    //原视频seekTo的位置
    private int mSourceSeekToPosition;
    //seekTo时视频的position播放位置
    private int mSeekToCurrentPlayerPosition;
    //广告视频播放次数计数
    private int mAdvVideoCount = 0;
    //用于视频广告判断MIDDLE广告播放完成后,是否需要继续seek
    private boolean needToSeek = false;
    //用于视频广告,当前seekTo后意图去播放哪一个视频
    private AdvVideoView.IntentPlayVideo mCurrentIntentPlayVideo;
    //当前屏幕亮度
    private int mScreenBrightness;
    //运营商是否自动播放
    private boolean mIsOperatorPlay;
    private boolean mLongPressSpeed = false;
    //原视频MediaInfo
    private MediaInfo mSourceVideoMediaInfo;

    private float currentVolume;
    //投屏当前声音
    private int mScreenCostingVolume;
    //试看时长，默认5分钟
    public static int TRAILER = 300;
    //试看所使用的域名
    public static String PLAY_DOMAIN = "alivc-demo-vod-player.aliyuncs.com";
    private boolean mIsVipRetry;
    //投屏时,当前播放状态
    private TransportState mCurrentTransportState;
    //判断当前是否处于分屏模式
    private boolean mIsInMultiWindow;
    //开始投屏时,当前的进度
    private int mStartScreenCostingPosition;
    //是否只需要全屏播放
    private boolean mIsNeedOnlyFullScreen;
    //第一次初始化网络监听
    private boolean initNetWatch;
    //播放器+渲染的View
    private AliyunRenderView mAliyunRenderView;
    private int mFullScreenType;
    private boolean mIsContinuedPlay;
    private boolean isFromRecommendList = false;
    private String mUUID = "";
    private boolean isFirstLand;
    private String mAuthorName = "";
    //记录播放速度，长按加速结束后，恢复到该速度。
    private float mRecordPlaySpeed;

    public AliyunVodPlayerView(Context context) {
        super(context);
    }

    public AliyunVodPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AliyunVodPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化view
     */
    public void initVideoView(IRenderView renderView, ListPlayManager listPlayer, int fullScreen) {
        mFullScreenType = fullScreen;
        isFirstLand = mFullScreenType == 1 || mFullScreenType == 2;
        //初始化handler
        mVodPlayerHandler = new AliyunVodPlayerView.VodPlayerHandler(this);
        //初始化播放器
        initAliVcPlayer(renderView, listPlayer);
        //初始化封面
        initCoverView();
        //初始化手势view
        initGestureView();
        //初始化水印图片
        initWaterMark();
        //初始化跑马灯
        initMarquee();
        //初始化试看View
        initTrailersView();
        //初始化清晰度view
        initQualityView();
        //初始化控制栏
        initControlView();
        //初始化音频模式
        initAudioModeView();
        //初始化缩略图
        initThumbnailView();
        //初始化提示view
        initTipsView();
        //初始化网络监听器
        initNetWatchdog();
        //初始化屏幕方向监听
        initOrientationWatchdog();
        //初始化手势对话框控制
        initGestureDialogManager();
        //默认为蓝色主题
        setTheme(Theme.Blue);
        //先隐藏手势和控制栏，防止在没有prepare的时候做操作。
        hideGestureAndControlViews();
        //初始化弹幕
        initDanmaku();
        //投屏
        initScreenCost();
        //初始化字幕
        initSubtitleView();
        initContrastTipLayout();
        initLongPressVideoSpeedView();
    }

    /**
     * 更新UI播放器的主题
     *
     * @param theme 支持的主题
     */
    @Override
    public void setTheme(Theme theme) {
        //通过判断子View是否实现了ITheme的接口，去更新主题
        int childCounts = getChildCount();
        for (int i = 0; i < childCounts; i++) {
            View view = getChildAt(i);
            if (view instanceof ITheme) {
                ((ITheme) view).setTheme(theme);
            }
        }
    }

    /**
     * 初始化缩略图
     */
    private void initThumbnailView() {
        mThumbnailView = new ThumbnailView(getContext());
        mThumbnailView.setVisibility(View.GONE);
        addSubViewByCenter(mThumbnailView);

        hideThumbnailView();
    }

    public void openAdvertisement() {
        List<ResolveInfo> resolveInfos = BrowserCheckUtil.checkBrowserList(getContext());
        if (resolveInfos == null || resolveInfos.size() <= 0) {
            AVToast.show(getContext(),true,getContext().getString(R.string.alivc_player_not_check_any_browser));
            return;
        }
        Intent intent = new Intent();
        intent.setData(Uri.parse(ADV_URL));
        intent.setAction(Intent.ACTION_VIEW);
        getContext().startActivity(intent);
    }

    /**
     * 初始化跑马灯控件
     */
    private void initMarquee() {
        mMarqueeView = new MarqueeView(getContext());
        addSubViewHeightWrap(mMarqueeView);
    }

    /**
     * 初始化试看View
     */
    private void initTrailersView() {
        mTrailersView = new TrailersView(getContext());
        addSubView(mTrailersView);

        mTrailersView.hideAll();

        mTrailersView.setOnTrailerViewClickListener(mOnTrailerViewClickListener);

        mTrailersView.setOnTrailerViewClickListener(new TrailersView.OnTrailerViewClickListener() {
            //重新观看
            @Override
            public void onTrailerPlayAgainClick() {
                if (mOnTrailerViewClickListener != null) {
                    mOnTrailerViewClickListener.onTrailerPlayAgainClick();
                }
                //如果是试看状态下，隐藏试看view
                mTrailersView.hideAll();
            }

            //开通vip
            @Override
            public void onOpenVipClick() {
                if (mOnTrailerViewClickListener != null) {
                    mOnTrailerViewClickListener.onOpenVipClick();
                }
            }
        });
    }


    /**
     * 初始化弹幕
     */
    private void initDanmaku() {
        mDanmakuView = new PlayerDanmakuView(getContext());
        addSubViewExactlyHeight(mDanmakuView, (int) (ScreenUtils.getHeight(getContext()) * 0.75), RelativeLayout.ALIGN_PARENT_TOP);
        updateDanmakuLocation(mDanmakuLocation);
    }

    /**
     * @param location 弹幕位置，0 为顶部(画面的顶部25%),1 为底部(画面的底部25%),2 为不限制
     */
    public void updateDanmakuLocation(int location) {
        if (mDanmakuView == null)
            return;
        LayoutParams layoutParams = null;
        if (location == 0) {
            layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (ScreenUtils.getHeight(getContext()) * 0.25));
            mDanmakuView.setDanmakuRegion(0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else if (location == 1) {
            layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (ScreenUtils.getHeight(getContext()) * 0.25));
            mDanmakuView.setDanmakuRegion(0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (ScreenUtils.getHeight(getContext()) * 0.75));
            mDanmakuView.setDanmakuRegion(3);
        }
        mDanmakuView.setLayoutParams(layoutParams);
    }

    /**
     * 初始化水印
     */
    private void initWaterMark() {
        mWaterMark = new ImageView(getContext());
        mWaterMark.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //目前暂时只使用阿里云icon，直接使用就可以
        mWaterMark.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.alivc_watermark_icon));
        mWaterMark.setVisibility(View.GONE);
        addSubViewByWrap(mWaterMark);
    }

    /**
     * 投屏
     */
    private void initScreenCost() {
        mScreenCostingView = new ScreenCostingView(getContext());
        mScreenCostingView.setVisibility(View.GONE);
        addSubView(mScreenCostingView);

        //投屏监听
        mScreenCostingView.setOnOutDLNAPlayerList(new DLNAOptionListener() {
            @Override
            public void play() {
                Log.e(TAG, "开始投屏play");
            }

            @Override
            public void playSuccess() {
                //投屏成功后,获取进度回调
                mScreenCostingView.startScheduledTask();
            }

            @Override
            public void playFailed() {
                AVToast.show(getContext(),true,getResources().getString(R.string.alivc_player_play_screening_fail));
                mIsScreenCosting = false;
                if (mControlView != null) {
                    mControlView.setInScreenCosting(mIsScreenCosting);
                }
            }
        });

        //获取播放进度
        mScreenCostingView.setOnGetPositionInfoListener(new ScreenCostingView.OnGetPositionInfoListener() {

            @Override
            public void onGetPositionInfo(int currentPosition, int duration) {
                mCurrentPosition = currentPosition;
                if (mControlView != null) {
                    mControlView.setMediaDuration(duration);
                    mControlView.setVideoPosition(currentPosition);
                }
                if (mStartScreenCostingPosition > 0 && duration > 0) {
                    mScreenCostingView.seek(mStartScreenCostingPosition);
                    mStartScreenCostingPosition = 0;
                }
            }
        });

        //播放状态回调监听
        mScreenCostingView.setOnGetTransportInfoListener(new ScreenCostingView.OnGetTransportInfoListener() {
            @Override
            public void onGetTransportInfo(TransportInfo transportInfo) {
                if (transportInfo != null) {
                    mCurrentTransportState = transportInfo.getCurrentTransportState();
                    String currentSpeed = transportInfo.getCurrentSpeed();
                    TransportStatus currentTransportStatus = transportInfo.getCurrentTransportStatus();
                    if (mCurrentPosition == 0 && mCurrentTransportState.equals(TransportState.STOPPED)) {
                        //播放完成状态
                        //1.先停止获取播放状态,播放进度定时任务
                        mScreenCostingView.stopScheduledTask();
                        //2.回调给AlivcPlayerActivity进行处理
                        if (mOnScreenCostingVideoCompletionListener != null) {
                            mOnScreenCostingVideoCompletionListener.onScreenCostingVideoCompletion();
                        }
                        mControlView.onScreenCostingVideoCompletion();
                    }
                }
            }
        });
    }


    /**
     * 隐藏手势和控制栏
     */
    private void hideGestureAndControlViews() {
        if (mGestureView != null) {
            mGestureView.hide(ViewAction.HideType.Normal);
        }
        if (mControlView != null && !mIsAudioMode) {
            mControlView.hide(ViewAction.HideType.Normal);
        }
    }

    /**
     * 初始化网络监听
     */
    private void initNetWatchdog() {
        Context context = getContext();
        mNetWatchdog = new NetWatchdog(context);
        mNetWatchdog.setNetChangeListener(new MyNetChangeListener(this));
        mNetWatchdog.setNetConnectedListener(new MyNetConnectedListener(this));
    }

    private void onWifiTo4G() {
        //如果已经显示错误了，那么就不用显示网络变化的提示了。
        if (mTipsView == null || mTipsView.isErrorShow() || (GlobalPlayerConfig.IS_VIDEO && (mAdvVideoPlayerState == IPlayer.started
                || mAdvVideoPlayerState == IPlayer.paused))) {
            return;
        }

        //wifi变成4G，如果不是本地视频先暂停播放
        if (!isLocalSource()) {
            if (mIsOperatorPlay) {
                AVToast.show(getContext(),true,R.string.alivc_operator_play);
            } else {
                pause();
            }
        }

        if (!initNetWatch) {
            reload();
        }

        //显示网络变化的提示
        if (!isLocalSource() && mTipsView != null) {
            if (mIsOperatorPlay) {
                AVToast.show(getContext(),true,R.string.alivc_operator_play);
            } else {
                mTipsView.hideAll();
                mTipsView.showNetChangeTipView();
                //隐藏其他的动作,防止点击界面去进行其他操作
                if (mControlView != null) {
                    mControlView.setHideType(ViewAction.HideType.Normal);
                    mControlView.hide(ControlView.HideType.Normal);
                }
                if (mGestureView != null) {
                    mGestureView.setHideType(ViewAction.HideType.Normal);
                    mGestureView.hide(ControlView.HideType.Normal);
                }
            }
        }
        initNetWatch = false;
    }

    private void on4GToWifi() {
        //如果已经显示错误了，那么就不用显示网络变化的提示了。
        if (mTipsView == null || mTipsView.isErrorShow()) {
            return;
        }

        //隐藏网络变化的提示
        if (mTipsView != null) {
            mTipsView.hideNetErrorTipView();
        }
        if (!initNetWatch) {
            reload();
        }
        initNetWatch = false;
    }

    private void onNetDisconnected() {
        //网络断开。
        // NOTE： 由于安卓这块网络切换的时候，有时候也会先报断开。所以这个回调是不准确的。
    }


    private static class MyNetChangeListener implements NetWatchdog.NetChangeListener {

        private final WeakReference<AliyunVodPlayerView> viewWeakReference;

        public MyNetChangeListener(AliyunVodPlayerView aliyunVodPlayerView) {
            viewWeakReference = new WeakReference<AliyunVodPlayerView>(aliyunVodPlayerView);
        }

        @Override
        public void onWifiTo4G() {
            AliyunVodPlayerView aliyunVodPlayerView = viewWeakReference.get();
            if (aliyunVodPlayerView != null) {
                aliyunVodPlayerView.onWifiTo4G();
            }
        }

        @Override
        public void on4GToWifi() {
            AliyunVodPlayerView aliyunVodPlayerView = viewWeakReference.get();
            if (aliyunVodPlayerView != null) {
                aliyunVodPlayerView.on4GToWifi();
            }
        }

        @Override
        public void onNetDisconnected() {
            AliyunVodPlayerView aliyunVodPlayerView = viewWeakReference.get();
            if (aliyunVodPlayerView != null) {
                aliyunVodPlayerView.onNetDisconnected();
            }
        }
    }

    /**
     * 初始化屏幕方向旋转。用来监听屏幕方向。结果通过OrientationListener回调出去。
     */
    private void initOrientationWatchdog() {
        final Context context = getContext();
        mOrientationWatchDog = new OrientationWatchDog(context);
        mOrientationWatchDog.setOnOrientationListener(new InnerOrientationListener(this));
    }

    private static class InnerOrientationListener implements OrientationWatchDog.OnOrientationListener {

        private final WeakReference<AliyunVodPlayerView> playerViewWeakReference;

        public InnerOrientationListener(AliyunVodPlayerView playerView) {
            playerViewWeakReference = new WeakReference<AliyunVodPlayerView>(playerView);
        }

        @Override
        public void changedToLandForwardScape(boolean fromPort) {
            AliyunVodPlayerView playerView = playerViewWeakReference.get();
            if (playerView != null) {
                playerView.changedToLandForwardScape(fromPort);
                if (playerView.isFirstLand) {
                    playerView.isFirstLand = false;
                }
            }
        }

        @Override
        public void changedToLandReverseScape(boolean fromPort) {
            AliyunVodPlayerView playerView = playerViewWeakReference.get();
            if (playerView != null) {
                playerView.changedToLandReverseScape(fromPort);
                if (playerView.isFirstLand) {
                    playerView.isFirstLand = false;
                }
            }
        }

        @Override
        public void changedToPortrait(boolean fromLand) {
            AliyunVodPlayerView playerView = playerViewWeakReference.get();
            if (playerView != null) {
                if (playerView.mFullScreenType == 1 || playerView.mFullScreenType == 2) {
                    if (playerView.isFirstLand) {
                        return;
                    }
                    if (playerView.mOnFinishListener != null) {
                        playerView.changeScreenMode(AliyunScreenMode.Small, false);
                        playerView.mOnFinishListener.onFinishClick();
                    }
                } else {
                    playerView.changedToPortrait(fromLand);
                }
            }
        }
    }

    /**
     * 屏幕方向变为横屏。
     *
     * @param fromPort 是否从竖屏变过来
     */
    private void changedToLandForwardScape(boolean fromPort) {
        //如果不是从竖屏变过来，也就是一直是横屏的时候，就不用操作了
        if (!fromPort) return;
        changeScreenMode(AliyunScreenMode.Full, false);
        if (orientationChangeListener != null) {
            orientationChangeListener.orientationChange(fromPort, mCurrentScreenMode);
        }
    }

    /**
     * 屏幕方向变为横屏。
     *
     * @param fromPort 是否从竖屏变过来
     */
    private void changedToLandReverseScape(boolean fromPort) {
        //如果不是从竖屏变过来，也就是一直是横屏的时候，就不用操作了
        if (!fromPort) return;
        changeScreenMode(AliyunScreenMode.Full, true);
        if (orientationChangeListener != null) {
            orientationChangeListener.orientationChange(fromPort, mCurrentScreenMode);
        }
    }

    /**
     * 屏幕方向变为竖屏
     *
     * @param fromLand 是否从横屏转过来
     */
    public void changedToPortrait(boolean fromLand) {
        //屏幕转为竖屏
        if (mIsFullScreenLocked) return;

        if (mCurrentScreenMode == AliyunScreenMode.Full) {
            //全屏情况转到了竖屏
            if (getLockPortraitMode() == null) {
                //没有固定竖屏，就变化mode
                if (fromLand) {
                    changeScreenMode(AliyunScreenMode.Small, false);
                }
            }
        }
        if (orientationChangeListener != null) {
            orientationChangeListener.orientationChange(fromLand, mCurrentScreenMode);
        }
    }

    /**
     * 初始化手势的控制类
     */
    private void initGestureDialogManager() {
        Context context = getContext();
        if (context instanceof Activity) {
            mGestureDialogManager = new GestureDialogManager((Activity) context);
        }
    }

    /**
     * 初始化提示view
     */
    private void initTipsView() {
        mTipsView = new TipsView(getContext());
        //设置tip中的点击监听事件
        mTipsView.setOnTipClickListener(new TipsView.OnTipClickListener() {
            @Override
            public void onContinuePlay() {
                mIsOperatorPlay = true;
                //继续播放。如果没有prepare或者stop了，需要重新prepare
                mTipsView.hideAll();
                if (GlobalPlayerConfig.IS_VIDEO) {
                    if (mAliyunRenderView != null) {
                        mAliyunRenderView.start();
                    }
                    if (mControlView != null) {
                        mControlView.setHideType(ViewAction.HideType.Normal);
                    }
                    if (mGestureView != null) {
                        mGestureView.setVisibility(VISIBLE);
                        mGestureView.setHideType(ViewAction.HideType.Normal);
                    }
                } else {
                    if (mPlayerState == IPlayer.idle || mPlayerState == IPlayer.stopped
                            || mPlayerState == IPlayer.error || mPlayerState == IPlayer.completion) {
                        mAliyunRenderView.setAutoPlay(true);
                        if (mAliyunPlayAuth != null) {
                            innerPrepareAuth(mAliyunPlayAuth);
                        } else if (mAliyunVidSts != null) {
                            innerPrepareSts(mAliyunVidSts);
                        } else if (mAliyunVidMps != null) {
                            innerPrepareMps(mAliyunVidMps);
                        } else if (mAliyunLocalSource != null) {
                            innerPrepareUrl(mAliyunLocalSource);
                        } else if (mAliyunLiveSts != null) {
                            innerPrepareLiveSts(mAliyunLiveSts);
                        }
                    } else {
                        start(false);
                    }
                }
            }

            @Override
            public void onStopPlay() {
                // 结束播放
                mTipsView.hideAll();
                stop();
                if (mOnFinishListener != null) {
                    mOnFinishListener.onFinishClick();
                }
            }

            @Override
            public void onRetryPlay(int errorCode) {
                if (mOutOnTipClickListener != null) {
                    mOutOnTipClickListener.onRetryPlay(errorCode);
                }
            }

            @Override
            public void onReplay() {
                //重播
                rePlay();
            }

            @Override
            public void onRefreshSts() {
                if (mOutTimeExpiredErrorListener != null) {
                    mOutTimeExpiredErrorListener.onTimeExpiredError();
                }
            }

            @Override
            public void onWait() {
                mTipsView.hideAll();
                mTipsView.showNetLoadingTipView();
            }

            @Override
            public void onExit() {
                mTipsView.hideAll();
                if (mOutOnTipClickListener != null) {
                    mOutOnTipClickListener.onExit();
                }
            }
        });

        mTipsView.setOnTipsViewBackClickListener(new OnTipsViewBackClickListener() {
            @Override
            public void onBackClick() {
                if (mOutOnTipsViewBackClickListener != null) {
                    mOutOnTipsViewBackClickListener.onBackClick();
                }
            }
        });
        addSubView(mTipsView);
    }

    /**
     * 重置。包括一些状态值，view的状态等
     */
    private void reset() {
        isCompleted = false;
        inSeek = false;
        mCurrentPosition = 0;
        mAdvTotalPosition = 0;
        mAdvCurrentPosition = 0;
        mVideoBufferedPosition = 0;
        needToSeek = false;
        mCurrentIntentPlayVideo = AdvVideoView.IntentPlayVideo.NORMAL;

        if (mTipsView != null) {
            mTipsView.hideAll();
        }
        if (mTrailersView != null) {
            mTrailersView.hideAll();
        }
        if (mControlView != null) {
            mControlView.reset();
        }
        if (mGestureView != null) {
            mGestureView.reset();
        }
        if (mDanmakuView != null) {
            mDanmakuView.clearDanmaList();
        }
        stop();
    }

    /**
     * 初始化封面
     */
    private void initCoverView() {
        mCoverView = new ImageView(getContext());
        //这个是为了给自动化测试用的id
        mCoverView.setId(R.id.custom_id_min);
        addSubView(mCoverView);
    }

    /**
     * 初始化控制栏view
     */
    private void initControlView() {
        mControlView = new ControlView(getContext());
        addSubView(mControlView);

        //设置播放按钮点击
        mControlView.setOnPlayStateClickListener(new ControlView.OnPlayStateClickListener() {
            @Override
            public void onPlayStateClick() {
                if (mPlayerState == IPlayer.stopped) {
                    rePlay();
                } else {
                    switchPlayerState();
                }
            }

            @Override
            public void onRePlayClick() {
                screenCostPlay();
            }
        });
        //设置进度条的seek监听
        mControlView.setOnSeekListener(new ControlView.OnSeekListener() {
            @Override
            public void onSeekEnd(int position) {
                if (mControlView != null) {
                    mControlView.setVideoPosition(position);
                }
                if (isCompleted) {
                    //播放完成了，不能seek了
                    inSeek = false;
                } else {

                    //拖动结束后，开始seek
                    if (!mIsScreenCosting) {
                        seekTo(position);
                    }

                    if (onSeekStartListener != null) {
                        onSeekStartListener.onSeekStart(position);
                    }
                    if (mScreenCostingView != null && mIsScreenCosting) {
                        mScreenCostingView.seek(position);
                        mControlView.setPlayState(ControlView.PlayState.Playing);
                    }
                    hideThumbnailView();

                }
            }

            @Override
            public void onSeekStart(int position) {
                //拖动开始
                inSeek = true;
                mSeekToCurrentPlayerPosition = position;
                if (mThumbnailPrepareSuccess) {
                    showThumbnailView();
                }

            }

            @Override
            public void onProgressChanged(int progress) {
                requestBitmapByPosition(progress);
            }
        });
        //清晰度按钮点击
        mControlView.setOnQualityBtnClickListener(new ControlView.OnQualityBtnClickListener() {

            @Override
            public void onQualityBtnClick(View v, List<TrackInfo> qualities, String currentQuality) {
                //显示清晰度列表
                mQualityView.setQuality(qualities, currentQuality);
                mQualityView.showAtTop(v);
            }

            @Override
            public void onHideQualityView() {
                mQualityView.hide();
            }
        });

        //清晰度、码率、字幕、音轨点击事件监听
        mControlView.setOnTrackInfoClickListener(new ControlView.OnTrackInfoClickListener() {
            @Override
            public void onSubtitleClick(List<TrackInfo> subtitleTrackInfoList) {
                if (mOutOnTrackInfoClickListener != null) {
                    mOutOnTrackInfoClickListener.onSubtitleClick(subtitleTrackInfoList);
                }
            }

            @Override
            public void onAudioClick(List<TrackInfo> audioTrackInfoList) {
                if (mOutOnTrackInfoClickListener != null) {
                    mOutOnTrackInfoClickListener.onAudioClick(audioTrackInfoList);
                }
            }

            @Override
            public void onBitrateClick(List<TrackInfo> bitrateTrackInfoList) {
                if (mOutOnTrackInfoClickListener != null) {
                    mOutOnTrackInfoClickListener.onBitrateClick(bitrateTrackInfoList);
                }
            }

            @Override
            public void onDefinitionClick(List<TrackInfo> definitionTrackInfoList) {
                if (mOutOnTrackInfoClickListener != null) {
                    mOutOnTrackInfoClickListener.onDefinitionClick(definitionTrackInfoList);
                }
            }
        });
        //点击锁屏的按钮
        mControlView.setOnScreenLockClickListener(new ControlView.OnScreenLockClickListener() {
            @Override
            public void onClick() {
                lockScreen(!mIsFullScreenLocked);
            }
        });
        //点击全屏/小屏按钮
        mControlView.setOnScreenModeClickListener(new ControlView.OnScreenModeClickListener() {
            @Override
            public void onClick() {
                onScreenModeClick();
            }
        });
        //点击了标题栏的返回按钮
        mControlView.setOnBackClickListener(new ControlView.OnBackClickListener() {
            @Override
            public void onClick() {
                if(mOutOnBackClickListener != null){
                    mOutOnBackClickListener.onClick();
                }else{
                    if (mCurrentScreenMode == AliyunScreenMode.Small) {
                        //小屏状态下，就结束活动
                        if (mOnFinishListener != null) {
                            mOnFinishListener.onFinishClick();
                        }
                    } else if (mCurrentScreenMode == AliyunScreenMode.Full) {
                        if (isLocalSource()) {
                            //如果播放的是本地视频,并且是全屏模式下点击返回按钮,则需要关闭Activity
                            if (orientationChangeListener != null) {
                                orientationChangeListener.orientationChange(false, AliyunScreenMode.Small);
                            }
                        } else {
                            //设置为小屏状态
                            changeScreenMode(AliyunScreenMode.Small, false);
                            if (mFullScreenType != 0) {
                                if (mOnFinishListener != null) {
                                    mOnFinishListener.onFinishClick();
                                }
                            }
                        }
                    }
                }
            }
        });

        // 横屏下显示更多
        mControlView.setOnShowMoreClickListener(new ControlView.OnShowMoreClickListener() {
            @Override
            public void showMore() {
                if (mOutOnShowMoreClickListener != null) {
                    mOutOnShowMoreClickListener.showMore();
                }
            }
        });
        mControlView.setOnVideoSpeedClickListener(new ControlView.OnVideoSpeedClickListener() {
            @Override
            public void onVideoSpeedClick() {
                if (mOnVideoSpeedClickListener != null) {
                    mOnVideoSpeedClickListener.onVideoSpeedClick();
                }
            }
        });

        // 截屏
        mControlView.setOnScreenShotClickListener(new ControlView.OnScreenShotClickListener() {
            @Override
            public void onScreenShotClick() {
                if (!mIsFullScreenLocked) {
                    snapShot();
                }
            }
        });

        // 录制
        mControlView.setOnScreenRecoderClickListener(new ControlView.OnScreenRecoderClickListener() {
            @Override
            public void onScreenRecoderClick() {
            }
        });

        //弹幕输入按钮点击监听
        mControlView.setOnInputDanmakuClickListener(new ControlView.OnInputDanmakuClickListener() {
            @Override
            public void onInputDanmakuClick() {
                showInputDanmakuClick();
                pause();
            }
        });

        //浮窗播放
        mControlView.setOnFloatPlayViewClickListener(new ControlView.OnFloatPlayViewClickListener() {
            @Override
            public void onFloatViewPlayClick() {
                if (mOnFloatPlayViewClickListener != null) {
                    mOnFloatPlayViewClickListener.onFloatViewPlayClick();
                }
            }
        });

        //投屏
        mControlView.setOnDLNAControlListener(new ControlView.OnDLNAControlListener() {
            @Override
            public void onExit() {
                mIsScreenCosting = false;
                if (mScreenCostingView != null) {
                    mScreenCostingView.exit();
                }
                if (mControlView != null) {
                    mControlView.exitScreenCost();
                    mControlView.setInScreenCosting(mIsScreenCosting);
                    if (GlobalPlayerConfig.IS_VIDEO) {
                        mControlView.hideNativeSeekBar();
                    } else {
                        mControlView.showNativeSeekBar();
                    }
                }
                seekTo((int) mCurrentPosition);
            }

            @Override
            public void onChangeQuality() {

            }

            @Override
            public void onChangeSeries(int series) {
            }
        });
        //弹幕打开或者关闭
        mControlView.setOnDamkuOpenListener(new ControlView.OnDamkuOpenListener() {
            @Override
            public void onDamkuOpen(boolean open) {
                //弹幕打开或者关闭
                if (open) {
                    showDanmakuAndMarquee();
                } else {
                    hideDanmakuAndMarquee();
                }
                mDanmakuOpen = open;
                //全局保存
                if (onDamkuOpenListener != null) {
                    onDamkuOpenListener.onDamkuOpen(open);
                }
            }
        });
        //听音频关闭或者打开
        mControlView.setOnAudioModeChangeListener(new ControlView.OnAudioModeChangeListener() {
            @Override
            public void onAudioMode(boolean audioMode) {
                onChangeMediaMode(audioMode);
            }
        });
        //投屏打开
        mControlView.setOnCastScreenListener(new ControlView.OnCastScreenListener() {
            @Override
            public void onCastScreen(boolean openCastScreen) {
                if (onCastScreenListener != null) {
                    if (mCurrentScreenMode == AliyunScreenMode.Small) {
                        onScreenModeClick();
                    }
                    onCastScreenListener.onCastScreen(openCastScreen);
                }
            }
        });

        //ControlView隐藏监听
        mControlView.setOnControlViewHideListener(new ControlView.OnControlViewHideListener() {
            @Override
            public void onControlViewHide() {
                if (mOnControlViewHideListener != null) {
                    mOnControlViewHideListener.onControlViewHide();
                }
            }
        });
        mControlView.setOnSelectSeriesClickListener(new ControlView.OnSelectSeriesListener() {
            @Override
            public void onSelectVideo() {
                //选集
                if (onSelectSeriesListener != null) {
                    onSelectSeriesListener.onSelectVideo();
                }
            }
        });
        mControlView.setOnNextSeriesClickListener(new ControlView.OnNextSeriesClick() {
            @Override
            public void onNextSeries() {
                if (onNextSeriesClick != null) {
                    onNextSeriesClick.onNextSeries();
                }
            }
        });

        if (mAliyunRenderView.getListPlayer().getPlayerScene() == IPlayManagerScene.SCENE_ONLY_VOICE) {
            //状态栏常驻
            mControlView.show(ViewAction.ShowType.AudioMode);
        }
        mControlView.show();
    }

    public void initLongPressVideoSpeedView() {
        mLongPressVideoSpeedView = LayoutInflater.from(getContext()).inflate(R.layout.layout_long_press_speed_up_tip, this, false);
        mLongPressVideoSpeedView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLongPressVideoSpeedView.setVisibility(GONE);
            }
        });
        addView(mLongPressVideoSpeedView);
    }

    private boolean enableShowLongPressVideoSpeedView(String key) {
        return mLongPressSP.getBoolean(key, true) && !mIsAudioMode;
    }

    /**
     * 展示长按加速提示 View
     */
    public void showLongPressView(String key){
        if(enableShowLongPressVideoSpeedView(key)){
            mLongPressVideoSpeedView.setVisibility(View.VISIBLE);
            mLongPressVideoSpeedView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLongPressVideoSpeedView.setVisibility(GONE);
                }
            }, 3000L);
            mLongPressSP.edit().putBoolean(key,false).apply();
        }
    }

    //点击全屏/小屏按钮
    private void onScreenModeClick() {
        if (mCurrentScreenMode == AliyunScreenMode.Small) {
            changedToLandForwardScape(true);
        } else {
            changeScreenMode(AliyunScreenMode.Small, false);
            if (mFullScreenType != 0) {
                if (mOnFinishListener != null) {
                    mOnFinishListener.onFinishClick();
                }
            }
        }
        if (mCurrentScreenMode == AliyunScreenMode.Full && !mIsAudioMode) {
            mControlView.showMoreButton();
        }
    }

    public void monitorFullScreenClick(boolean reverse) {
        if (reverse) {
            changedToLandReverseScape(true);
        } else {
            changedToLandForwardScape(true);
        }
    }


    public void setOnControlViewHideListener(ControlView.OnControlViewHideListener listener) {
        this.mOnControlViewHideListener = listener;
    }

    public void setOnTipClickListener(TipsView.OnTipClickListener listener) {
        this.mOutOnTipClickListener = listener;
    }

    /**
     * 输入弹幕
     */
    private void showInputDanmakuClick() {
        if (mOnSoftKeyHideListener != null) {
            mOnSoftKeyHideListener.onClickPaint();
        }
    }

    private void onChangeMediaMode(boolean isAudioMode) {
        showAudioModeView(isAudioMode);
        mControlView.setAudioMode(isAudioMode);
        mAliyunRenderView.onAudioMode(isAudioMode);
    }


    /**
     * 初始化音频模式相关 View
     */
    private void initAudioModeView() {
        mAudioModeView = new AudioModeView(getContext());
        addSubView(mAudioModeView);
        mIsAudioMode = mAliyunRenderView.getListPlayer().getPlayerScene() == IPlayManagerScene.SCENE_ONLY_VOICE;
        if (mIsAudioMode) {
            mControlView.setMediaInfo(mAliyunRenderView.getListPlayer().getListPlayer().getMediaInfo());
        }
        showAudioModeView(mIsAudioMode);
        mAudioModeView.setMOnAudioModeListener(new AudioModeView.OnAudioModeListener() {
            @Override
            public void closeAudioMode() {
                showAudioModeView(false);
            }

            @Override
            public void clickPlayIcon() {
                if (mPlayerState == IPlayer.stopped) {
                    rePlay();
                } else {
                    if (mAliyunRenderView.getListPlayer().isPlaying()) {
                        pause();
                    } else {
                        mAliyunRenderView.getListPlayer().resumeListPlay();
                        mControlView.setPlayState(ControlView.PlayState.Playing);
                    }
                }
            }

            @Override
            public void onReplay() {
                //重播是从头开始播
                rePlay();
                showAudioModeView(true);
            }
        });
    }

    private void showAudioModeView(boolean show) {
        mIsAudioMode = show;
        if (mControlView != null) {
            mControlView.setAudioMode(mIsAudioMode);
        }
        if (show) {
            if (mAliyunRenderView.getListPlayer().getListPlayer().getMediaInfo() == null) {
                return;
            }
            boolean playComplete = mAliyunRenderView.getListPlayer().isPlayComplete();
            mAudioModeView.setUpData(mAliyunRenderView.getListPlayer().getListPlayer().getMediaInfo().getCoverUrl(),
                    mCurrentScreenMode, mAliyunRenderView.getListPlayer().isPlaying(), false,
                    playComplete);
            mAudioModeView.setVisibility(View.VISIBLE);
            //状态栏常驻
            mControlView.show(ViewAction.ShowType.AudioMode);
            mAliyunRenderView.getListPlayer().setPlayerScene(IPlayManagerScene.SCENE_ONLY_VOICE);
            hideDanmakuAndMarquee();
            if (playComplete) {
                seekTo(mAliyunRenderView.getListPlayer().getCurrentVideo().getDuration());
            }
        } else {
            mAudioModeView.setVisibility(View.GONE);
            mControlView.hide(ViewAction.HideType.Normal);
            PlayServiceHelper.stopService(getContext());
            mAliyunRenderView.getListPlayer().setPlayerScene(IPlayManagerScene.SCENE_NORMAL);
            showDanmakuAndMarquee();
        }
    }

    /**
     * 初始化清晰度列表
     */
    private void initQualityView() {
        mQualityView = new QualityView(getContext());
        addSubView(mQualityView);
        //清晰度点击事件
        mQualityView.setOnQualityClickListener(new QualityView.OnQualityClickListener() {
            @Override
            public void onQualityClick(TrackInfo qualityTrackInfo) {
                String dlnaUrl = qualityTrackInfo.getVodPlayUrl();
                if (TextUtils.isEmpty(dlnaUrl) || dlnaUrl.contains("encrypt")) {
                    Config.DLNA_URL = "";
                } else {
                    Config.DLNA_URL = qualityTrackInfo.getVodPlayUrl();
                }

                mAliyunRenderView.selectTrack(qualityTrackInfo.getIndex());
            }
        });
    }

    /**
     * 初始化引导view
     */
    private void initGuideView() {
        mGuideView = new GuideView(getContext());
        addSubView(mGuideView);
    }

    /**
     * 切换播放状态。点播播放按钮之后的操作
     */
    private void switchPlayerState() {
        //投屏状态下的处理
        if (mIsScreenCosting && mScreenCostingView != null && mControlView != null) {
            if (mCurrentTransportState == TransportState.PLAYING) {
                mScreenCostingView.pause();
                mControlView.updateScreenCostPlayStateBtn(true);
            } else {
                mScreenCostingView.play((int) mCurrentPosition);
                mControlView.updateScreenCostPlayStateBtn(false);
            }
        } else {
            //非投屏状态下的处理
            if (mPlayerState == IPlayer.started) {
                pause();
            } else if (mPlayerState == IPlayer.paused || mPlayerState == IPlayer.prepared || mPlayerState == IPlayer.stopped) {
                start(false);
                if (mDanmakuOpen && mDanmakuView != null) {
                    mDanmakuView.resume();
                }
            }
        }

        if (onPlayStateBtnClickListener != null) {
            onPlayStateBtnClickListener.onPlayBtnClick(mPlayerState);
        }
    }

    /**
     * 初始化手势view
     */
    private void initGestureView() {
        mGestureView = new GestureView(getContext());
        addSubView(mGestureView);
        mGestureView.setMultiWindow(mIsInMultiWindow);

        //设置手势监听
        mGestureView.setOnGestureListener(new GestureView.GestureListener() {

            @Override
            public void onHorizontalDistance(float downX, float nowX) {
            }

            @Override
            public void onLeftVerticalDistance(float downY, float nowY) {
                //左侧上下滑动调节亮度
                int changePercent = (int) ((nowY - downY) * 100 / getHeight());

                if (mGestureDialogManager != null) {
                    mGestureDialogManager.showBrightnessDialog(AliyunVodPlayerView.this, mScreenBrightness);
                    int brightness = mGestureDialogManager.updateBrightnessDialog(changePercent);
                    if (mOnScreenBrightnessListener != null) {
                        mOnScreenBrightnessListener.onScreenBrightness(brightness);
                    }
                    mScreenBrightness = brightness;
                }
            }

            @Override
            public void onRightVerticalDistance(float downY, float nowY) {
                //右侧上下滑动调节音量
                float volume = mAliyunRenderView.getVolume();
                int changePercent = (int) ((nowY - downY) * 100 / getHeight());
                if (mGestureDialogManager != null) {
                    mGestureDialogManager.showVolumeDialog(AliyunVodPlayerView.this, volume * 100);
                    float targetVolume = mGestureDialogManager.updateVolumeDialog(changePercent);
                    currentVolume = targetVolume;
                    //不管是否投屏状态,都设置音量改变给播放器,用于保存当前的音量
                    mAliyunRenderView.setVolume((targetVolume / 100.00f));//通过返回值改变音量
                }
            }

            @Override
            public void onGestureEnd() {
                Log.i(TAG, "onGestureEnd");
                //手势结束。
                //seek需要在结束时操作。
                if (mGestureDialogManager != null) {
                    int seekPosition = mControlView.getVideoPosition();
                    if (seekPosition >= mAliyunRenderView.getDuration()) {
                        seekPosition = (int) (mAliyunRenderView.getDuration() - 1000);
                    }
                    if (seekPosition <= 0) {
                        seekPosition = 0;
                    }
                    //如果是在投屏状态下
                    if (mScreenCostingView != null && mIsScreenCosting) {
                        if (mGestureDialogManager.isVolumeDialogIsShow()) {
                            mScreenCostingVolume = (int) currentVolume;
                            mScreenCostingView.setVolume(currentVolume);
                        }
                    }

                    if (mThumbnailView != null && inSeek) {
                        seekTo(seekPosition);
                        inSeek = false;
                        if (mThumbnailView.isShown()) {
                            hideThumbnailView();
                        }

                    }
                    if (mControlView != null) {
                        //开启状态栏自动隐藏的功能
                        mControlView.openAutoHide();
                    }

                    mGestureDialogManager.dismissBrightnessDialog();
                    mGestureDialogManager.dismissVolumeDialog();
                }
                if (mLongPressSpeed) {
                    mAliyunRenderView.setSpeed(mRecordPlaySpeed);
                    mLongPressSpeed = false;
                    if(mControlView != null){
                        mControlView.setSpeedViewText(mRecordPlaySpeed);
                        mControlView.showVideoSpeedTipLayout(false);
                    }
                }
            }

            @Override
            public void onSingleTap() {
                //单击事件，显示控制栏
                if (mControlView != null) {
                    //播放广告状态下的单击事件
                    if (mAdvVideoPlayerState == IPlayer.started && (GlobalPlayerConfig.IS_VIDEO)) {
                        openAdvertisement();
                    } else if (mIsScreenCosting) {
                        //投屏状态下的ControlView的单击事件
                        if (mOnScreenCostingSingleTagListener != null) {
                            mOnScreenCostingSingleTagListener.onScreenCostingSingleTag();
                        }
                    } else if (mIsAudioMode) {
                        //do nothing
                    } else {
                        if (mControlView.getVisibility() != VISIBLE) {
                            mControlView.show();
                        } else {
                            mControlView.hide(ControlView.HideType.Normal);
                        }
                    }

                }
            }

            @Override
            public void onDoubleTap() {
                //双击事件，控制暂停播放
                if (mIsScreenCosting || (GlobalPlayerConfig.IS_TRAILER && mCurrentPosition >= TRAILER)) {
                    //投屏状态下或者试看结束时双击不做任何操作
                } else if (GlobalPlayerConfig.IS_TRAILER && mAdvVideoPlayerState == IPlayer.started) {
                    //如果是视频广告,并且视频广告在播放中,那么不做任何操作
                } else {
                    switchPlayerState();
                }
            }

            @Override
            public void onLongPress() {
                //长按，则快进
                handleLongPress();
            }
        });
    }

    /**
     * 初始化广告播放器
     */
    private void initAdvVideoView() {
        mAdvVideoView = new AdvVideoView(getContext());
        addSubView(mAdvVideoView);

        //准备完成监听
        mAdvVideoView.setOutPreparedListener(new VideoPlayerPreparedListener(this, true));
        //loading状态改变监听
        mAdvVideoView.setOutOnLoadingStatusListener(new VideoPlayerLoadingStatusListener(this, true));
        //状态改变监听
        mAdvVideoView.setOutOnStateChangedListener(new VideoPlayerStateChangedListener(this, true));
        //完成监听
        mAdvVideoView.setOutOnCompletionListener(new VideoPlayerCompletionListener(this, true));
        //设置info改变监听
        mAdvVideoView.setOutOnInfoListener(new VideoPlayerInfoListener(this, true));
        //设置错误监听
        mAdvVideoView.setOutOnErrorListener(new VideoPlayerErrorListener(this, true));
        //设置renderingStart监听
        mAdvVideoView.setOutOnRenderingStartListener(new VideoPlayerRenderingStartListener(this, true));
        //设置广告返回按钮点击事件
        mAdvVideoView.setOnBackImageViewClickListener(new VideoPlayerAdvBackImageViewListener(this));

    }

    /**
     * 初始化播放器
     */
    private void initAliVcPlayer(IRenderView renderView, ListPlayManager listPlayer) {
        mAliyunRenderView = new AliyunRenderView(getContext());
        addSubView(mAliyunRenderView);
        mAliyunRenderView.setSurfaceView(renderView);
        mAliyunRenderView.setListPlayer(listPlayer);
        //设置准备回调
        mAliyunRenderView.setOnPreparedListener(new VideoPlayerPreparedListener(this, false));
        //播放器出错监听
        mAliyunRenderView.setOnErrorListener(new VideoPlayerErrorListener(this, false));
        //播放器加载回调
        mAliyunRenderView.setOnLoadingStatusListener(new VideoPlayerLoadingStatusListener(this, false));
        //播放器状态
        mAliyunRenderView.setOnStateChangedListener(new VideoPlayerStateChangedListener(this, false));
        //播放结束
        mAliyunRenderView.setOnCompletionListener(new VideoPlayerCompletionListener(this, false));
        //播放信息监听
        mAliyunRenderView.setOnInfoListener(new VideoPlayerInfoListener(this, false));
        //第一帧显示
        mAliyunRenderView.setOnRenderingStartListener(new VideoPlayerRenderingStartListener(this, false));
        //trackChange监听
        mAliyunRenderView.setOnTrackChangedListener(new VideoPlayerTrackChangedListener(this));
        //字幕显示和隐藏
        mAliyunRenderView.setOnSubtitleDisplayListener(new VideoPlayerSubtitleDeisplayListener(this));
        //seek结束事件
        mAliyunRenderView.setOnSeekCompleteListener(new VideoPlayerOnSeekCompleteListener(this));
        //截图监听事件
        mAliyunRenderView.setOnSnapShotListener(new VideoPlayerOnSnapShotListener(this));
        //sei监听事件
        mAliyunRenderView.setOnSeiDataListener(new VideoPlayerOnSeiDataListener(this));
        mAliyunRenderView.setOnVerifyTimeExpireCallback(new VideoPlayerOnVerifyStsCallback(this));
        mAliyunRenderView.setOnContrastPlay(new AliyunRenderView.OnContrastPlay() {
            @Override
            public void onContrastPlay(long durationMilllis) {
                onShowContrastPlayTip(true);
            }
        });
    }

    public void clearAllListener() {
        mAliyunRenderView.setOnPreparedListener(null);
        //播放器出错监听
        mAliyunRenderView.setOnErrorListener(null);
        //播放器加载回调
        mAliyunRenderView.setOnLoadingStatusListener(null);
        //播放器状态
        mAliyunRenderView.setOnStateChangedListener(null);
        //播放结束
        mAliyunRenderView.setOnCompletionListener(null);
        //播放信息监听
        mAliyunRenderView.setOnInfoListener(null);
        //第一帧显示
        mAliyunRenderView.setOnRenderingStartListener(null);
        //trackChange监听
        mAliyunRenderView.setOnTrackChangedListener(null);
        //字幕显示和隐藏
        mAliyunRenderView.setOnSubtitleDisplayListener(null);
        //seek结束事件
        mAliyunRenderView.setOnSeekCompleteListener(null);
        //截图监听事件
        mAliyunRenderView.setOnSnapShotListener(null);
        //sei监听事件
        mAliyunRenderView.setOnSeiDataListener(null);
        mAliyunRenderView.setOnVerifyTimeExpireCallback(null);
    }

    /**
     * 初始化字幕
     */
    private void initSubtitleView() {
        mSubtitleView = new SubtitleView(getContext());
        mSubtitleView.setId(R.id.alivc_player_subtitle);
        SubtitleView.DefaultValueBuilder defaultValueBuilder = new SubtitleView.DefaultValueBuilder();
        defaultValueBuilder.setLocation(LocationStyle.Location_Center);
        defaultValueBuilder.setColor("#e7e7e7");
        mSubtitleView.setDefaultValue(defaultValueBuilder);
        addSubView(mSubtitleView);
    }

    private void initContrastTipLayout() {
        mContrastPlayTipView = LayoutInflater.from(getContext()).inflate(R.layout.layout_contrast_play_tip, this, false);
        addView(mContrastPlayTipView);
        mContrastPlayTipView.setVisibility(GONE);
        //续播提示
        mContrastPlayTipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekTo(0);
                mContrastPlayTipView.setVisibility(View.GONE);
            }
        });
    }

    public void onShowContrastPlayTip(boolean show) {
        if (mContrastPlayTipView == null)
            return;
        if (show) {
            mContrastPlayTipView.setVisibility(View.VISIBLE);
            mContrastPlayTipView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mContrastPlayTipView.setVisibility(View.GONE);
                }
            }, 3000L);
        } else {
            mContrastPlayTipView.setVisibility(View.GONE);
        }
    }

    /**
     * 暂停原视频，播放广告视频
     */
    private void startAdvVideo() {
        if (GlobalPlayerConfig.IS_TRAILER) {
            //试看情况下,只有seek到的时长小于试看时长时,才会播放视频广告
            if (mSourceSeekToPosition < TRAILER * 1000) {
                playAdvVideo();
            }
        } else {
            playAdvVideo();
        }
    }

    private void playAdvVideo() {
        if (mAliyunRenderView != null && mAdvVideoView != null) {
            mAliyunRenderView.pause();
            int advPlayerState = mAdvVideoView.getAdvPlayerState();
            if (advPlayerState == IPlayer.paused || advPlayerState == IPlayer.prepared || advPlayerState == IPlayer.started) {
                mAdvVideoView.optionStart();
            } else {
                mAdvVideoView.optionPrepare();
            }
        }
    }

    /**
     * 获取从源中设置的标题 。 如果用户设置了标题，优先使用用户设置的标题。 如果没有，就使用服务器返回的标题
     *
     * @param title 服务器返回的标题
     * @return 最后的标题
     */
    private String getTitle(String title) {
        String finalTitle = title;
        if (mAliyunLocalSource != null) {
            finalTitle = mAliyunLocalSource.getTitle();
        } else if (mAliyunPlayAuth != null) {
            finalTitle = mAliyunPlayAuth.getTitle();
        } else if (mAliyunVidSts != null) {
            finalTitle = mAliyunVidSts.getTitle();
        }

        if (TextUtils.isEmpty(finalTitle)) {
            return title;
        } else {
            return finalTitle;
        }
    }

    /**
     * 获取从源中设置的封面 。 如果用户设置了封面，优先使用用户设置的封面。 如果没有，就使用服务器返回的封面
     *
     * @param postUrl 服务器返回的封面
     * @return 最后的封面
     */
    private String getPostUrl(String postUrl) {
        String finalPostUrl = postUrl;
        if (mAliyunLocalSource != null) {
            finalPostUrl = mAliyunLocalSource.getCoverPath();
        } else if (mAliyunPlayAuth != null) {

        }

        if (TextUtils.isEmpty(finalPostUrl)) {
            return postUrl;
        } else {
            return finalPostUrl;
        }
    }

    /**
     * 设置是否处于分屏模式
     *
     * @param isInMultiWindow true,处于分屏模式,false不处于分屏模式
     */
    public void setMultiWindow(boolean isInMultiWindow) {
        this.mIsInMultiWindow = isInMultiWindow;
        if (mGestureView != null) {
            mGestureView.setMultiWindow(mIsInMultiWindow);
        }
    }

    /**
     * 判断是否开启精准seek
     */
    private void isAutoAccurate(long position) {
        if (GlobalPlayerConfig.PlayConfig.mEnableAccurateSeekModule) {
            mAliyunRenderView.seekTo(position, IPlayer.SeekMode.Accurate);
        } else {
            mAliyunRenderView.seekTo(position, IPlayer.SeekMode.Inaccurate);
        }
    }

    /**
     * 判断是否是本地资源
     */
    private boolean isLocalSource() {
        String scheme = null;
        if (GlobalPlayerConfig.PLAYTYPE.STS.equals(GlobalPlayerConfig.mCurrentPlayType)
                || GlobalPlayerConfig.PLAYTYPE.MPS.equals(GlobalPlayerConfig.mCurrentPlayType)
                || GlobalPlayerConfig.PLAYTYPE.AUTH.equals(GlobalPlayerConfig.mCurrentPlayType)
                || GlobalPlayerConfig.PLAYTYPE.DEFAULT.equals(GlobalPlayerConfig.mCurrentPlayType)) {
            return false;
        }
        if (mAliyunLocalSource == null || TextUtils.isEmpty(mAliyunLocalSource.getUri())) {
            return false;
        }
        if (GlobalPlayerConfig.PLAYTYPE.URL.equals(GlobalPlayerConfig.mCurrentPlayType)) {
            Uri parse = Uri.parse(mAliyunLocalSource.getUri());
            scheme = parse.getScheme();
        }
        return scheme == null;
    }


    /**
     * 获取视频时长
     *
     * @return 视频时长
     */
    public int getDuration() {
        if (mAliyunRenderView != null) {
            return (int) mAliyunRenderView.getDuration();
        }

        return 0;
    }

    /**
     * 显示错误提示
     *
     * @param errorCode  错误码
     * @param errorEvent 错误事件
     * @param errorMsg   错误描述
     */
    public void showErrorTipView(int errorCode, String errorEvent, String errorMsg) {
        stop();
        if (mControlView != null) {
            mControlView.setPlayState(ControlView.PlayState.NotPlaying);
        }

        if (mTipsView != null) {
            //隐藏其他的动作,防止点击界面去进行其他操作
            mGestureView.hide(ViewAction.HideType.End);
            mControlView.hide(ViewAction.HideType.End);
            mCoverView.setVisibility(GONE);
            mTipsView.showErrorTipView(errorCode, errorEvent, errorMsg);
            mTrailersView.hideAll();
        }
    }

    public void hideErrorTipView() {

        if (mTipsView != null) {
            //隐藏其他的动作,防止点击界面去进行其他操作
            mTipsView.hideErrorTipView();
        }
    }

    /**
     * 根据位置请求缩略图
     */
    private void requestBitmapByPosition(int targetPosition) {
        if (mThumbnailHelper != null && mThumbnailPrepareSuccess) {
            mThumbnailHelper.requestBitmapAtPosition(targetPosition);
        }
    }

    /**
     * 隐藏缩略图
     */
    private void hideThumbnailView() {
        if (mThumbnailView != null) {
            mThumbnailView.hideThumbnailView();
        }
    }

    /**
     * 显示缩略图
     */
    private void showThumbnailView() {
        if (mThumbnailView != null && !mIsAudioMode) {
            mThumbnailView.showThumbnailView();
            //根据屏幕大小调整缩略图的大小
            ImageView thumbnailImageView = mThumbnailView.getThumbnailImageView();
            if (thumbnailImageView != null) {
                ViewGroup.LayoutParams layoutParams = thumbnailImageView.getLayoutParams();
                layoutParams.width = (int) (ScreenUtils.getWidth(getContext()) / 3);
                layoutParams.height = layoutParams.width / 2 - DensityUtil.px2dip(getContext(), 10);
                thumbnailImageView.setLayoutParams(layoutParams);
            }
        }
    }

    /**
     * 目标位置计算算法
     *
     * @param duration        视频总时长
     * @param currentPosition 当前播放位置
     * @param deltaPosition   与当前位置相差的时长
     */
    public int getTargetPosition(long duration, long currentPosition, long deltaPosition) {
        // seek步长
        long finalDeltaPosition;
        // 根据视频时长，决定seek步长
        long totalMinutes = duration / 1000 / 60;
        int hours = (int) (totalMinutes / 60);
        int minutes = (int) (totalMinutes % 60);

        // 视频时长为1小时以上，小屏和全屏的手势滑动最长为视频时长的十分之一
        if (hours >= 1) {
            finalDeltaPosition = deltaPosition / 10;
        }// 视频时长为31分钟－60分钟时，小屏和全屏的手势滑动最长为视频时长五分之一
        else if (minutes > 30) {
            finalDeltaPosition = deltaPosition / 5;
        }// 视频时长为11分钟－30分钟时，小屏和全屏的手势滑动最长为视频时长三分之一
        else if (minutes > 10) {
            finalDeltaPosition = deltaPosition / 3;
        }// 视频时长为4-10分钟时，小屏和全屏的手势滑动最长为视频时长二分之一
        else if (minutes > 3) {
            finalDeltaPosition = deltaPosition / 2;
        }// 视频时长为1秒钟至3分钟时，小屏和全屏的手势滑动最长为视频结束
        else {
            finalDeltaPosition = deltaPosition;
        }

        long targetPosition = finalDeltaPosition + currentPosition;
        if (targetPosition < 0) {
            targetPosition = 0;
        }
        if (targetPosition > duration) {
            targetPosition = duration;
        }
        return (int) targetPosition;
    }

    /**
     * addSubView 添加子view到布局中
     *
     * @param view 子view
     */
    private void addSubView(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //添加到布局中
        addView(view, params);
    }

    /**
     * 添加子View到布局中,在某个View的下方
     *
     * @param view            需要添加的View
     * @param belowTargetView 在这个View的下方
     */
    private void addSubViewBelow(final View view, final View belowTargetView) {
        belowTargetView.post(new Runnable() {
            @Override
            public void run() {
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                params.topMargin = belowTargetView.getMeasuredHeight();
                //添加到布局中
                addView(view, params);
            }
        });
    }

    /**
     * 添加子View到布局中央
     */
    private void addSubViewByCenter(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(view, params);
    }

    /**
     * 添加子View到布局中,高度设置为 WRAP_CONTENT
     *
     * @param view 子view
     */
    private void addSubViewHeightWrap(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        switch (MARQUEE_REGION) {
            case TOP:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case MIDDLE:
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                break;
            case BOTTOM:
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            default:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
        }
        //添加到布局中
        addView(view, params);
    }

    /**
     * @param view
     * @param height
     */
    private void addSubViewExactlyHeight(View view, int height, int verb) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        params.addRule(verb);
        //添加到布局中
        addView(view, params);
    }

    /**
     * 在底部添加View
     *
     * @param view 子view
     */
    private void addSubViewByBottom(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //添加到布局中
        addView(view, params);
    }

    /**
     * 添加子View
     */
    private void addSubViewByWrap(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        switch (WATER_MARK_REGION) {
            case LEFT_TOP:
                params.leftMargin = DensityUtil.dip2px(getContext(), 20);
                params.topMargin = DensityUtil.dip2px(getContext(), 10);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case LEFT_BOTTOM:
                params.leftMargin = DensityUtil.dip2px(getContext(), 20);
                params.bottomMargin = DensityUtil.dip2px(getContext(), 10);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case RIGHT_TOP:
                params.rightMargin = DensityUtil.dip2px(getContext(), 20);
                params.topMargin = DensityUtil.dip2px(getContext(), 10);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case RIGHT_BOTTOM:
                params.rightMargin = DensityUtil.dip2px(getContext(), 20);
                params.bottomMargin = DensityUtil.dip2px(getContext(), 10);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            default:
                params.rightMargin = DensityUtil.dip2px(getContext(), 20);
                params.topMargin = DensityUtil.dip2px(getContext(), 10);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
        }
        addView(view, params);
    }

    /**
     * 改变屏幕模式：小屏或者全屏。
     *
     * @param targetMode {@link AliyunScreenMode}
     */
    public void changeScreenMode(AliyunScreenMode targetMode, boolean isReverse) {
        AliyunScreenMode finalScreenMode = targetMode;

        if (mIsFullScreenLocked) {
            finalScreenMode = AliyunScreenMode.Full;
        }

        //这里可能会对模式做一些修改
        if (targetMode != mCurrentScreenMode) {
            mCurrentScreenMode = finalScreenMode;
        }

        if (mGestureDialogManager != null) {
            mGestureDialogManager.setCurrentScreenMode(mCurrentScreenMode);
        }

        if (mControlView != null) {
            mControlView.setScreenModeStatus(finalScreenMode);
        }

        if (mMarqueeView != null) {
            mMarqueeView.setScreenMode(finalScreenMode);
        }

        if (mGuideView != null) {
            mGuideView.setScreenMode(finalScreenMode);
        }

        if (mAudioModeView != null) {
            mAudioModeView.setScreenMode(finalScreenMode);
        }

        setWaterMarkPosition(finalScreenMode);

        Context context = getContext();
        if (context instanceof Activity) {
            if (finalScreenMode == AliyunScreenMode.Full) {
                if (getLockPortraitMode() == null) {
                    //不是固定竖屏播放。
                    if (isReverse) {
                        ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    } else {
                        ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }

                    //SCREEN_ORIENTATION_LANDSCAPE只能固定一个横屏方向
                } else {
                    //如果是固定全屏，那么直接设置view的布局，宽高
                    ViewGroup.LayoutParams aliVcVideoViewLayoutParams = getLayoutParams();
                    aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                showDanmakuAndMarquee();
            } else if (finalScreenMode == AliyunScreenMode.Small) {

                if (getLockPortraitMode() == null) {
                    //不是固定竖屏播放。
                    ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    //如果是固定全屏，那么直接设置view的布局，宽高
                    ViewGroup.LayoutParams aliVcVideoViewLayoutParams = getLayoutParams();
                    aliVcVideoViewLayoutParams.height = (int) (ScreenUtils.getWidth(context) * 9.0f / 16);
                    aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
            }
        }
    }

    /**
     * 设置水印位置
     *
     * @param screenMode 当前屏幕模式
     */
    private void setWaterMarkPosition(AliyunScreenMode screenMode) {
        if (mWaterMark == null) {
            return;
        }
        int navigationBarHeight = ScreenUtils.getNavigationBarHeight(getContext());
        MarginLayoutParams params = (MarginLayoutParams) mWaterMark.getLayoutParams();
        switch (WATER_MARK_REGION) {
            case LEFT_TOP:
                params.leftMargin = DensityUtil.dip2px(getContext(), screenMode == AliyunScreenMode.Full ? navigationBarHeight / 2 : 20);
                params.topMargin = DensityUtil.dip2px(getContext(), screenMode == AliyunScreenMode.Full ? 20 : 10);
                break;
            case LEFT_BOTTOM:
                params.leftMargin = DensityUtil.dip2px(getContext(), screenMode == AliyunScreenMode.Full ? navigationBarHeight / 2 : 20);
                params.bottomMargin = DensityUtil.dip2px(getContext(), screenMode == AliyunScreenMode.Full ? 20 : 10);
                break;
            case RIGHT_TOP:
                params.rightMargin = DensityUtil.dip2px(getContext(), screenMode == AliyunScreenMode.Full ? navigationBarHeight / 2 : 20);
                params.topMargin = DensityUtil.dip2px(getContext(), screenMode == AliyunScreenMode.Full ? 20 : 10);
                break;
            case RIGHT_BOTTOM:
                params.rightMargin = DensityUtil.dip2px(getContext(), screenMode == AliyunScreenMode.Full ? navigationBarHeight / 2 : 20);
                params.bottomMargin = DensityUtil.dip2px(getContext(), screenMode == AliyunScreenMode.Full ? 20 : 10);
                break;
            default:
                params.rightMargin = DensityUtil.dip2px(getContext(), screenMode == AliyunScreenMode.Full ? navigationBarHeight / 2 : 20);
                params.topMargin = DensityUtil.dip2px(getContext(), screenMode == AliyunScreenMode.Full ? 20 : 10);
                break;
        }
        mWaterMark.setLayoutParams(params);
    }

    /**
     * 获取当前屏幕模式：小屏、全屏
     *
     * @return 当前屏幕模式
     */
    public AliyunScreenMode getScreenMode() {
        return mCurrentScreenMode;
    }

    /**
     * 设置准备事件监听
     *
     * @param onPreparedListener 准备事件
     */
    public void setOnPreparedListener(IPlayer.OnPreparedListener onPreparedListener) {
        mOutPreparedListener = onPreparedListener;
    }

    /**
     * 设置错误事件监听
     *
     * @param onErrorListener 错误事件监听
     */
    public void setOnErrorListener(IPlayer.OnErrorListener onErrorListener) {
        mOutErrorListener = onErrorListener;
    }

    /**
     * 设置信息事件监听
     *
     * @param onInfoListener 信息事件监听
     */
    public void setOnInfoListener(IPlayer.OnInfoListener onInfoListener) {
        mOutInfoListener = onInfoListener;
    }

    /**
     * 设置播放完成事件监听
     *
     * @param onCompletionListener 播放完成事件监听
     */
    public void setOnCompletionListener(IPlayer.OnCompletionListener onCompletionListener) {
        mOutCompletionListener = onCompletionListener;
    }

    /**
     * 投屏状态下的单击事件
     */
    public void setOnScreenCostingSingleTagListener(OnScreenCostingSingleTagListener listener) {
        this.mOnScreenCostingSingleTagListener = listener;
    }

    /**
     * 设置自动播放事件监听
     *
     * @param l 自动播放事件监听
     */
    public void setOnAutoPlayListener(OnAutoPlayListener l) {
        mOutAutoPlayListener = l;
    }

    public interface OnTimeExpiredErrorListener {
        void onTimeExpiredError();
    }

    /**
     * 设置源超时监听
     *
     * @param l 源超时监听
     */
    public void setOnTimeExpiredErrorListener(OnTimeExpiredErrorListener l) {
        mOutTimeExpiredErrorListener = l;
    }

    /**
     * 投屏时,视频播放完成监听
     */
    public interface OnScreenCostingVideoCompletionListener {
        void onScreenCostingVideoCompletion();
    }

    public void setOnScreenCostingVideoCompletionListener(OnScreenCostingVideoCompletionListener listener) {
        this.mOnScreenCostingVideoCompletionListener = listener;
    }

    public void setOnTipsViewBackClickListener(OnTipsViewBackClickListener listener) {
        this.mOutOnTipsViewBackClickListener = listener;
    }

    /**
     * 设置首帧显示事件监听
     *
     * @param onFirstFrameStartListener 首帧显示事件监听
     */
    public void setOnFirstFrameStartListener(IPlayer.OnRenderingStartListener onFirstFrameStartListener) {
        mOutFirstFrameStartListener = onFirstFrameStartListener;
    }

    /**
     * 设置seek结束监听
     *
     * @param onSeekCompleteListener seek结束监听
     */
    public void setOnSeekCompleteListener(IPlayer.OnSeekCompleteListener onSeekCompleteListener) {
        mOuterSeekCompleteListener = onSeekCompleteListener;
    }

    /**
     * 流切换监听事件
     */
    public void setOnTrackChangedListener(IPlayer.OnTrackChangedListener listener) {
        this.mOutOnTrackChangedListener = listener;
    }

    /**
     * 设置停止播放监听
     *
     * @param onStoppedListener 停止播放监听
     */
    public void setOnStoppedListener(OnStoppedListener onStoppedListener) {
        this.mOnStoppedListener = onStoppedListener;
    }

    /**
     * 设置加载状态监听
     *
     * @param onLoadingListener 加载状态监听
     */
    public void setOnLoadingListener(IPlayer.OnLoadingStatusListener onLoadingListener) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setOnLoadingStatusListener(onLoadingListener);
        }
    }

    /**
     * 设置视频宽高变化监听
     *
     * @param onVideoSizeChangedListener 视频宽高变化监听
     */
    public void setOnVideoSizeChangedListener(IPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        }
    }


    /**
     * 清空之前设置的播放源
     */
    private void clearAllSource() {
        mAliyunPlayAuth = null;
        mAliyunVidSts = null;
        mAliyunLocalSource = null;
        mAliyunVidMps = null;
        mAliyunLiveSts = null;
    }

    public void playPosition(String uuid) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.playPosition(uuid);
        }
    }

    /**
     * 展示播放器不同的功能
     */
    private void showVideoFunction(boolean refresh) {
        //如果是 refresh 才需要进行停止等操作
        if (mAliyunRenderView != null && refresh) {
            mPlayerState = IPlayer.stopped;
            mAliyunRenderView.stop();
        }
        if (mAdvVideoView != null && refresh) {
            mAdvVideoView.optionStop();
            mAdvVideoView.isShowAdvVideoBackIamgeView(false);
            mAdvVideoView.isShowAdvVideoTipsTextView(false);
        }
        //水印
        if (GlobalPlayerConfig.IS_WATERMARK) {
            mWaterMark.setVisibility(View.VISIBLE);
        } else {
            mWaterMark.setVisibility(View.GONE);
        }

        //图片广告功能,如果是图片广告视频,并且不是视频广告,并且不是投屏中,才会显示投屏广告
        if (GlobalPlayerConfig.IS_PICTRUE && !GlobalPlayerConfig.IS_VIDEO && !mIsScreenCosting) {
            if (mAliyunRenderView != null) {
                mAliyunRenderView.setAutoPlay(false);
            }
            if (mControlView != null) {
                mControlView.hide(ViewAction.HideType.Normal);

            }
            if (mControlView != null) {
                mControlView.showNativeSeekBar();
            }
            innerPrepareSts(mAliyunVidSts);
            return;
        }
        //视频广告
        if (!GlobalPlayerConfig.IS_VIDEO) {
            if (mControlView != null) {
                mControlView.showNativeSeekBar();
            }
        }

        //如果要显示视频广告,并且是4g网络
        if (!GlobalPlayerConfig.IS_VIDEO) {
            if (!show4gTips()) {
                innerPrepareSts(mAliyunVidSts);
            }
        }
    }

    public void updateStsInfo(StsInfo stsInfo) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.updateStsInfo(stsInfo);
        }
    }

    public void updateAuthInfo(VidAuth vidAuth) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.updateAuthInfo(vidAuth);
        }
    }


    public void updateListPosition(String uuid) {
        this.mUUID = uuid;
    }

    public void setAuthorName(String authorName) {
        this.mAuthorName = authorName;
    }

    public void updateContinuePlay(boolean continuePlay, boolean isFromList) {
        this.isFromRecommendList = isFromList;
    }

    /**
     * 准备广告播放资源
     */
    private void preapreAdvVidSts(VidSts vidSts) {
        if (mTipsView != null) {
            mTipsView.showNetLoadingTipView();
        }
        if (mControlView != null) {
            mControlView.setIsMtsSource(false);
        }

        if (mQualityView != null) {
            mQualityView.setIsMtsSource(false);
        }
        if (mAdvVideoView != null) {
            UrlSource urlSource = new UrlSource();
            urlSource.setUri(ADV_VIDEO_URL);
            mAdvVideoView.optionSetUrlSource(urlSource);
            //如果是投屏状态,则不播放视频广告
            mAdvVideoView.setAutoPlay(!mIsScreenCosting);
            mAdvVideoView.optionPrepare();
        }
    }

    public void showNetLoadingTipView() {
        if (mTipsView != null && !mIsAudioMode) {
            mTipsView.showNetLoadingTipView();
        }
    }

    public void hideNetLoadingTipView(){
        if (mTipsView != null) {
            mTipsView.hideNetLoadingTipView();
        }
    }


    /**
     * 当VodPlayer 没有加载完成的时候,调用onStop 去暂停视频,
     * 会出现暂停失败的问题。
     */
    private static class VodPlayerLoadEndHandler extends Handler {

        private final WeakReference<AliyunVodPlayerView> weakReference;

        private boolean intentPause;

        public VodPlayerLoadEndHandler(AliyunVodPlayerView aliyunVodPlayerView) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                intentPause = true;
            }
            if (msg.what == 1) {
                AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
                if (aliyunVodPlayerView != null && intentPause) {
                    aliyunVodPlayerView.onStop();
                    intentPause = false;
                }
            }
        }
    }

    /**
     * 运营商是否自动播放
     *
     * @param isOperatorPlay true为自动播放,false会有tips
     */
    public void setOperatorPlay(boolean isOperatorPlay) {
        this.mIsOperatorPlay = isOperatorPlay;
    }

    /**
     * 在activity调用onResume的时候调用。 解决home回来后，画面方向不对的问题
     */
    public void onResume(boolean activityResume) {
        if (mIsFullScreenLocked) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                changeScreenMode(AliyunScreenMode.Small, false);
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                changeScreenMode(AliyunScreenMode.Full, false);
            }
        }

        if (mOrientationWatchDog != null) {
            mOrientationWatchDog.startWatch();
        }

        //onStop中记录下来的状态，在这里恢复使用
        if (!mIsScreenCosting) {
            //不是投屏中再回复播放
            resumePlayerState();
        }

        if (PlayServiceHelper.mServiceStart && activityResume) {
            PlayServiceHelper.stopService(getContext());
        }
    }


    /**
     * 开启网络监听
     */
    public void startNetWatch() {
        initNetWatch = true;
        if (mNetWatchdog != null) {
            mNetWatchdog.startWatch();
        }
    }

    /**
     * 关闭网络监听
     */
    public void stopNetWatch() {
        if (mNetWatchdog != null) {
            mNetWatchdog.stopWatch();
        }
    }


    /**
     * 暂停播放器的操作
     */
    public void onStop() {
        clearAllListener();
        if (mOrientationWatchDog != null) {
            mOrientationWatchDog.stopWatch();
        }
        //保存播放器的状态，供resume恢复使用。
        savePlayerState();
    }

    /**
     * 开启屏幕旋转监听
     */
    public void startOrientationWatchDog() {
        if (mOrientationWatchDog != null) {
            mOrientationWatchDog.startWatch();
        }
    }

    /**
     * 关闭屏幕旋转监听
     */
    public void stopOrientationWatchDog() {
        if (mOrientationWatchDog != null) {
            mOrientationWatchDog.stopWatch();
        }
    }


    public void setIsContinuedPlay(boolean isContinuedPlay, boolean fromList) {
        this.mIsContinuedPlay = isContinuedPlay;
        this.isFromRecommendList = fromList;
    }

    public void onPause(boolean activityPause) {
        mPlayingBeforePause = isPlaying();
        //显示通知栏显示后台播放,只有 Activity 和 fragment 都 pause 的时候，才会执行
        if (ListPlayManager.GlobalPlayer.getMGlobalPlayEnable() && activityPause) {
            PlayServiceHelper.startPlayService(getContext(), mAuthorName);
        }
        if (activityPause && !ListPlayManager.GlobalPlayer.getMGlobalPlayEnable()) {
            pause();
        }
    }

    public void updatePlayStateIcon(boolean playing) {
        mPlayingBeforePause = playing;
        if (playing) {
            mPlayerState = IPlayer.started;
        } else {
            mPlayerState = IPlayer.paused;
        }
        if (mIsAudioMode) {
            if (mAudioModeView != null) {
                mAudioModeView.updatePlayIcon(playing);
            }
        } else {
            if (playing) {
                mControlView.setPlayState(ControlView.PlayState.Playing);
            } else {
                mControlView.setPlayState(ControlView.PlayState.NotPlaying);
            }
        }
    }

    public void set(boolean playingBeforePause) {
        this.mPlayingBeforePause = playingBeforePause;
    }

    /**
     * Activity回来后，恢复之前的状态
     */
    private void resumePlayerState() {
        if (mAliyunRenderView == null || !mPlayingBeforePause) {
            return;
        }

        //从后台返回前台,不管是播放状态还是暂停状态都要播放,但是需要对视频广告单独处理一下
        if (mAdvVideoView != null && GlobalPlayerConfig.IS_VIDEO) {
            /** 添加判断mCurrentPosition == 0 如果在视频广告prepare阶段快速退到后台,此时视频广告播放器的状态是prepare状态,不是paused状态,
             *  所以恢复到前台后会无法播放*/
            if (mAdvVideoPlayerState == IPlayer.paused || mCurrentPosition == 0) {
                mAdvVideoView.optionStart();
            } else {
                if (isLocalSource()) {
                    reTry();
                } else {
                    start(true);
                }
            }
        } else {
            //恢复前台后需要继续播放,包括本地资源也要继续播放
            if (!isLocalSource() && NetWatchdog.is4GConnected(getContext()) && !mIsOperatorPlay && isPlaying()) {
                pause();
            } else {
                if (mSourceDuration <= 0 && mPlayerState == IPlayer.stopped) {
                    //直播
                    mAliyunRenderView.prepare();
                } else {
                    start(true);
                }
                if (mAliyunRenderView.getListPlayer().isPlaying()) {
                    showDanmakuAndMarquee();
                    if (mDanmakuView != null) {
                        mDanmakuView.resume();
                    }
                } else {
                    hideDanmakuAndMarquee();
                }
            }
        }
    }

    /**
     * 保存当前的状态，供恢复使用
     */
    private void savePlayerState() {
        if (mAliyunRenderView == null) {
            return;
        }
        if (mSourceDuration <= 0) {
            //直播调用stop
            mPlayerState = IPlayer.stopped;
            mAliyunRenderView.stop();
        } else {
            pause();
        }
    }

    /**
     * 判断视频广告位置是否包含末尾的位置
     */
    private boolean advStyleIsIncludeEnd() {
        return mAdvPosition == MutiSeekBarView.AdvPosition.ALL || mAdvPosition == MutiSeekBarView.AdvPosition.ONLY_END
                || mAdvPosition == MutiSeekBarView.AdvPosition.START_END || mAdvPosition == MutiSeekBarView.AdvPosition.MIDDLE_END;
    }

    /**
     * 获取媒体信息
     *
     * @return 媒体信息
     */
    public MediaInfo getMediaInfo() {
        if (mAliyunRenderView != null) {
            return mAliyunRenderView.getMediaInfo();
        }

        return null;
    }

    /**
     * 判断是否展示4g提示,内部进行了判断
     * 1.不是本地视频
     * 2.当前网络是4G
     * 满足上述条件才会展示4G提示
     *
     * @return true:需要展示,false不需要
     */
    private boolean show4gTips() {
        //播放本地文件不需要提示
        if (isLocalSource()) {
            return false;
        } else {
            //不是本地文件
            if (NetWatchdog.is4GConnected(getContext())) {
                if (mIsOperatorPlay) {
                    //运营商自动播放,则Toast提示后,继续播放
                    AVToast.show(getContext(),true,R.string.alivc_operator_play);
                    return false;
                } else {
                    if (mTipsView != null) {
                        mTipsView.showNetChangeTipView();
                    }
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * 广告播放器4g提示处理
     */
    private void advVideoPlayer4gTips() {
        if (!show4gTips()) {
            mAliyunRenderView.start();
            //切换到原视频播放后,就去prepare广告视频,用于无缝衔接
            mAdvVideoView.setAutoPlay(false);
            mAdvVideoView.optionPrepare();
        }

    }

    /**
     * 视频广告播放完成的处理
     */
    private void afterAdvVideoPlayerComplete() {
        //播放完成后，保存当前播放的进度时长
        mAdvTotalPosition += mAdvCurrentPosition;
        if (mAliyunRenderView != null && mSurfaceView != null) {
            mSurfaceView.setVisibility(View.VISIBLE);
            if (mAdvVideoView != null) {
                mAdvVideoView.setSurfaceViewVisibility(View.GONE);
            }
            if (needToSeek) {
                //播放完MIDDLE视频后需要seekTo原视频
                if (mAdvVideoCount < 3) {
                    isAutoAccurate(mSeekToPosition - mAdvDuration * 2);
                    advVideoPlayer4gTips();
                }
            } else {
                if (mCurrentIntentPlayVideo == AdvVideoView.IntentPlayVideo.MIDDLE_END_ADV_SEEK && mAdvVideoCount < 3) {
                    //播放完MIDDLE视频后需要播放END视频
                    if (mAliyunRenderView != null) {
                        isAutoAccurate(mSourceDuration);
                        mAliyunRenderView.pause();
                    }
                    if (mControlView != null) {
                        /*
                            由于关键帧的问题,seek到sourceDuration / 2时间点会导致进度条和广告时间对应不上,导致在播放原视频的时候进度条还在广告进度条范围内
                        */
                        mControlView.setAdvVideoPosition((int) (mSourceDuration + mAdvDuration * 2), (int) mCurrentPosition);
                    }
                    if (mAdvVideoView != null) {
                        mAdvVideoView.setAutoPlay(!mIsScreenCosting);
                        mAdvVideoView.optionPrepare();
                    }
                }
                if (mAdvVideoCount < 3) {
                    advVideoPlayer4gTips();
                }
            }

        }
        if (mControlView != null) {
            mControlView.setTotalPosition(mAdvTotalPosition);
        }

        //如果是广告视频并且广告视频的位置包含了末尾的位置,并且已经展示过视频广告了，说明当前视频广告结束的是最后一条视频广告,需要播放下一个视频
        if (advStyleIsIncludeEnd() && (mAdvVideoCount == 3)) {
            //获取当前位置
            if (mOutCompletionListener != null) {
                mOutCompletionListener.onCompletion();
            }
        }
    }


    /**
     * 活动销毁，释放
     */
    public void onDestroy() {
        stop();
        if (mAliyunRenderView != null) {
            mAliyunRenderView.release(isFromRecommendList || mIsContinuedPlay);
            mAliyunRenderView = null;
        }
        mSurfaceView = null;
        mGestureView = null;
        mControlView = null;
        mCoverView = null;
        mGestureDialogManager = null;
        mNetWatchdog = null;
        mTipsView = null;
        mAliyunMediaInfo = null;
        if (mOrientationWatchDog != null) {
            mOrientationWatchDog.destroy();
        }
        mOrientationWatchDog = null;
        hasLoadEnd.clear();
        if (mScreenCostingView != null) {
            mScreenCostingView.destroy();
        }
        stopNetWatch();
    }

    /**
     * 显示弹幕和跑马灯
     */
    private void showDanmakuAndMarquee() {
        if (mIsAudioMode) {
            return;
        }
        if (GlobalPlayerConfig.IS_BARRAGE && mDanmakuView != null) {
            mDanmakuView.show();
        }
        if (GlobalPlayerConfig.IS_MARQUEE && mMarqueeView != null) {
            mMarqueeView.createAnimation();
            mMarqueeView.startFlip();
        }
    }

    /**
     * 隐藏弹幕和跑马灯
     */
    private void hideDanmakuAndMarquee() {
        if (mDanmakuView != null && mDanmakuView.isShown()) {
            mDanmakuView.hide();
        }
        if (mMarqueeView != null && mMarqueeView.isStart()) {
            mMarqueeView.stopFlip();
        }
    }

    /**
     * 是否只需要全屏播放,只在播放本地视频和URL播放方式下生效
     */
    public void needOnlyFullScreenPlay(boolean isNeed) {
        mIsNeedOnlyFullScreen = isNeed;
    }

    /**
     * 隐藏弹幕
     */
    public void hideDanmakuView() {
        if (mDanmakuView != null) {
            mDanmakuView.hideAndPauseDrawTask();
            mDanmakuView.setVisibility(View.GONE);
        }
    }

    /**
     * 是否处于播放状态：start或者pause了
     *
     * @return 是否处于播放状态
     */
    public boolean isPlaying() {
        return mPlayerState == IPlayer.started;
    }

    /**
     * 获取播放器状态
     *
     * @return 播放器状态
     */
    public int getPlayerState() {
        return mPlayerState;
    }

    /**
     * reload
     */
    public void reload() {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.reload();
        }
    }

    /**
     * 开始播放
     */
    public void start(boolean resume) {
        if (mControlView != null) {
            mControlView.setPlayState(ControlView.PlayState.Playing);
        }

        if (mAliyunRenderView == null) {
            return;
        }

        if (mAdvVideoPlayerState == IPlayer.started && GlobalPlayerConfig.IS_VIDEO) {
            mControlView.setHideType(ViewAction.HideType.Normal);
            mGestureView.setHideType(ViewAction.HideType.Normal);

        } else {
            mGestureView.show();
        }
        if (mSourceDuration <= 0 && mPlayerState == IPlayer.stopped) {
            //直播
            mAliyunRenderView.prepare();
        } else {
            if (mPlayerState == IPlayer.paused) {
                mAliyunRenderView.start();
            } else if (mIsContinuedPlay && resume) {
                //如果是续播,并且从 resume 过来的，直接 prepare 成功
                onContinuePlayStart();
            } else if (!mIsContinuedPlay && isFromRecommendList && !mAliyunRenderView.getListPlayer().isPlaying() && !mAliyunRenderView.getListPlayer().isPlayComplete()) {
                mAliyunRenderView.playPosition(mUUID);
            } else if (!mAliyunRenderView.getListPlayer().isPlayComplete()) {
                mAliyunRenderView.start();
            }

        }

        if (mMarqueeView != null && mMarqueeView.isStart() && mCurrentScreenMode == AliyunScreenMode.Full) {
            mMarqueeView.startFlip();
        }
        mPlayerState = IPlayer.started;
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mControlView != null && !mIsScreenCosting) {
            mControlView.setPlayState(ControlView.PlayState.NotPlaying);
        }
        if (mAliyunRenderView == null) {
            return;
        }
        if (mAdvVideoView != null) {
            mAdvVideoView.optionPause();
        }
        if (mPlayerState == IPlayer.started || mPlayerState == IPlayer.prepared) {
            if (mSourceDuration <= 0) {
                //直播调用stop
                mPlayerState = IPlayer.stopped;
                mAliyunRenderView.stop();
            } else {
                mAliyunRenderView.pause();
                mPlayerState = IPlayer.paused;
            }


            //非vip用户观看vip视频，先展示视频广告，展示之后展示试看功能，暂停的时候展示图片广告    

            if (mMarqueeView != null) {
                mMarqueeView.pause();
            }
            if (mDanmakuOpen && mDanmakuView != null) {
                mDanmakuView.pause();
            }
        }
    }

    /**
     * 停止播放
     */
    private void stop() {
        Boolean hasLoadedEnd = null;
        MediaInfo mediaInfo = null;
        if (mAliyunRenderView != null) {
            mediaInfo = mAliyunRenderView.getMediaInfo();
            hasLoadedEnd = hasLoadEnd.get(mediaInfo);
        }

        if (mAliyunRenderView != null && hasLoadedEnd != null && !isFromRecommendList && !mIsContinuedPlay) {
            mPlayerState = IPlayer.stopped;
            mAliyunRenderView.stop();
        }
        if (mAdvVideoView != null) {
            mAdvVideoView.optionStop();
        }
        if (mControlView != null) {
            mControlView.setPlayState(ControlView.PlayState.NotPlaying);
        }
        hasLoadEnd.remove(mediaInfo);
    }

    public void onPlayComplete() {
        if (isFromRecommendList) {
            mPlayerState = IPlayer.stopped;
            mAliyunRenderView.stop();
        }
    }

    /**
     * 停止后是否显示最后一帧
     */
    public void clearFrameWhenStop(boolean clearFrameWhenStop) {
        if (mAliyunRenderView != null) {
            PlayerConfig config = mAliyunRenderView.getPlayerConfig();
            config.mClearFrameWhenStop = clearFrameWhenStop;
            mAliyunRenderView.setPlayerConfig(config);
        }
    }

    /**
     * 展示replay
     */
    public void showReplay() {
        if (isFromRecommendList) {
            onPlayComplete();
        }
        if (!mIsAudioMode) {
            if (mAliyunMediaInfo == null) {
                mAliyunMediaInfo = getMediaInfo();
            }
            mControlView.hide(ViewAction.HideType.Normal);
            if (mTipsView != null && mAliyunMediaInfo != null) {
                //隐藏其他的动作,防止点击界面去进行其他操作
                mTipsView.showReplayTipView(mAliyunMediaInfo.getCoverUrl());
            }
        } else {
            if (mAudioModeView != null) {
                mAudioModeView.onPlayEnd();
            }
        }
    }

    public void hideReplay() {
        if (mTipsView != null) {
            mTipsView.hideAll();
        }
    }

    /**
     * seek操作
     *
     * @param position 目标位置
     */
    public void seekTo(int position) {
        mSeekToPosition = position;
        if (mAliyunRenderView == null) {
            return;
        }
        inSeek = true;
        //如果是视频广告跟试看同时存在，第一段广告播放完毕，试看view显示之后不播放广告

        if (GlobalPlayerConfig.IS_VIDEO) {
            //视频广告
            checkAdvVideoSeek(position);
        } else {
            mSourceSeekToPosition = position;
            realySeekToFunction(position);
        }
    }

    private void realySeekToFunction(int position) {
        /** 这里由于如果是视频广告seekEnd返回的progress是包含了视频广告的时间,而这里的seek,需要的是原视频的seek时间,所以需要减去视频广告的时间 */
        if (GlobalPlayerConfig.IS_VIDEO) {
            isAutoAccurate(position - mAdvVideoCount * mAdvDuration);
        } else {
            isAutoAccurate(position);
        }
    }

    /**
     * 检查视频广告seek的位置
     */
    private void checkAdvVideoSeek(int position) {
        needToSeek = false;
        if (mControlView != null) {
            AdvVideoView.IntentPlayVideo intentPlayVideo = mControlView.getIntentPlayVideo(mControlView.getMutiSeekBarCurrentProgress(), position);
            mCurrentIntentPlayVideo = intentPlayVideo;
            switch (intentPlayVideo) {
                case START_ADV:
                    if (mAliyunRenderView != null) {
                        mSourceSeekToPosition = 0;
                        isAutoAccurate(mSourceSeekToPosition);
                    }
                    if (mControlView != null) {
                        mControlView.setAdvVideoPosition(0, 0);
                    }
                    mAdvTotalPosition = 0;
                    mAdvVideoCount = 0;
                    startAdvVideo();
                    break;
                case MIDDLE_ADV:
                    if (mAliyunRenderView != null) {
                        mSourceSeekToPosition = (int) (mSourceDuration / 2);
                        isAutoAccurate(mSourceSeekToPosition);
                    }
                    if (mControlView != null) {
                        mControlView.setAdvVideoPosition((int) (mAdvDuration + mSourceDuration / 2), (int) mSourceSeekToPosition);
                    }
                    mAdvTotalPosition = mAdvDuration;
                    mAdvVideoCount = 1;
                    startAdvVideo();
                    break;
                case END_ADV:
                    if (mControlView != null) {
                        mSourceSeekToPosition = (int) (mSourceDuration + mAdvDuration * 2);
                        mControlView.setAdvVideoPosition((int) (mSourceDuration + mAdvDuration * 2), (int) mSourceSeekToPosition);
                    }
                    mAdvTotalPosition = mAdvDuration * 2;
                    mAdvVideoCount = 2;
                    startAdvVideo();
                    break;
                case MIDDLE_ADV_SEEK:
                    needToSeek = true;
                    if (mAliyunRenderView != null) {
                        //保证mControlView的位置正确
                        mSourceSeekToPosition = (int) (mSourceDuration / 2);
                        isAutoAccurate(mSourceSeekToPosition);
                    }
                    if (mControlView != null) {
                        mControlView.setAdvVideoPosition((int) (mAdvDuration + mSourceDuration / 2), (int) mSourceSeekToPosition);
                    }
                    mAdvTotalPosition = mAdvDuration;
                    mAdvVideoCount = 1;
                    startAdvVideo();
                    break;
                case MIDDLE_END_ADV_SEEK:
                    needToSeek = false;
                    if (mAliyunRenderView != null) {
                        //保证mControlView的位置正确
                        mSourceSeekToPosition = (int) (mSourceDuration / 2);
                        isAutoAccurate(mSourceSeekToPosition);
                    }
                    if (mControlView != null) {
                        mControlView.setAdvVideoPosition((int) (mAdvDuration + mSourceDuration / 2), (int) mSourceSeekToPosition);
                    }
                    mAdvTotalPosition = mAdvDuration;
                    mAdvVideoCount = 1;
                    startAdvVideo();
                    break;
                case REVERSE_SOURCE:
                    if (mAliyunRenderView != null) {
                        mSourceSeekToPosition = (int) (position - mAdvDuration);
                        isAutoAccurate(position - mAdvDuration);
                    }
                    if (mControlView != null) {
                        mControlView.setAdvVideoPosition(position, (int) mSourceSeekToPosition);
                    }
                    mAdvTotalPosition = mAdvDuration;
                    mAdvVideoCount = 1;
                    break;
                case NORMAL:
                    realySeekToFunction(position);
                    break;
                default:
                    realySeekToFunction(position);
                    break;
            }
        }
    }

    /**
     * 设置锁定竖屏监听
     *
     * @param listener 监听器
     */
    public void setLockPortraitMode(LockPortraitListener listener) {
        mLockPortraitListener = listener;
    }

    /**
     * 锁定竖屏
     *
     * @return 竖屏监听器
     */
    public LockPortraitListener getLockPortraitMode() {
        return mLockPortraitListener;
    }

    /**
     * 让home键无效
     *
     * @param keyCode 按键
     * @param event   事件
     * @return 是否处理。
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (((mCurrentScreenMode == AliyunScreenMode.Full)) && (keyCode != KeyEvent.KEYCODE_HOME)
                && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN) {
            changedToPortrait(true);
            return false;
        }
        return !mIsFullScreenLocked || (keyCode == KeyEvent.KEYCODE_HOME);
    }

    /**
     * 播放按钮点击listener
     */
    public interface OnPlayStateBtnClickListener {
        void onPlayBtnClick(int playerState);
    }

    /**
     * 设置播放状态点击监听
     */
    public void setOnPlayStateBtnClickListener(OnPlayStateBtnClickListener listener) {
        this.onPlayStateBtnClickListener = listener;
    }

    private OnSeekStartListener onSeekStartListener;

    /**
     * seek开始监听
     */

    public interface OnSeekStartListener {
        void onSeekStart(int position);
    }

    public void setOnSeekStartListener(OnSeekStartListener listener) {
        this.onSeekStartListener = listener;
    }

    /**
     * 屏幕方向改变监听接口
     */
    public interface OnOrientationChangeListener {
        /**
         * 屏幕方向改变
         *
         * @param from        从横屏切换为竖屏, 从竖屏切换为横屏
         * @param currentMode 当前屏幕类型
         */
        void orientationChange(boolean from, AliyunScreenMode currentMode);
    }

    private OnOrientationChangeListener orientationChangeListener;

    public void setOrientationChangeListener(
            OnOrientationChangeListener listener) {
        this.orientationChangeListener = listener;
    }

    /**
     * 断网/连网监听
     */
    private class MyNetConnectedListener implements NetWatchdog.NetConnectedListener {
        public MyNetConnectedListener(AliyunVodPlayerView aliyunVodPlayerView) {
        }

        @Override
        public void onReNetConnected(boolean isReconnect) {
            if (mNetConnectedListener != null) {
                mNetConnectedListener.onReNetConnected(isReconnect);
            }
        }

        @Override
        public void onNetUnConnected() {
            if (mNetConnectedListener != null) {
                mNetConnectedListener.onNetUnConnected();
            }
        }
    }

    public void setNetConnectedListener(NetConnectedListener listener) {
        this.mNetConnectedListener = listener;
    }

    /**
     * 判断是否有网络的监听
     */
    public interface NetConnectedListener {
        /**
         * 网络已连接
         */
        void onReNetConnected(boolean isReconnect);

        /**
         * 网络未连接
         */
        void onNetUnConnected();
    }

    public interface OnFinishListener {
        void onFinishClick();
    }

    /**
     * 设置监听
     */
    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.mOnFinishListener = onFinishListener;
    }

    public void setOnShowMoreClickListener(
            ControlView.OnShowMoreClickListener listener) {
        this.mOutOnShowMoreClickListener = listener;
    }

    public void setOnVideoSpeedClickListener(
            ControlView.OnVideoSpeedClickListener listener) {
        this.mOnVideoSpeedClickListener = listener;
    }

    /**
     * 设置码率，字幕、音轨、清晰度点击事件监听
     */
    public void setOnTrackInfoClickListener(ControlView.OnTrackInfoClickListener listener) {
        this.mOutOnTrackInfoClickListener = listener;
    }

    public void setOnBackClickListener(ControlView.OnBackClickListener listener){
        this.mOutOnBackClickListener = listener;
    }

    /**
     * 软键盘隐藏监听
     */
    public interface OnSoftKeyHideListener {
        void softKeyHide();

        //点击画笔
        void onClickPaint();
    }

    public void setSoftKeyHideListener(OnSoftKeyHideListener listener) {
        this.mOnSoftKeyHideListener = listener;
    }

    public void setOnTrailerViewClickListener(TrailersView.OnTrailerViewClickListener listener) {
        this.mOnTrailerViewClickListener = listener;
    }

    public interface OnScreenBrightnessListener {
        void onScreenBrightness(int brightness);
    }

    public void setOnScreenBrightness(OnScreenBrightnessListener listener) {
        this.mOnScreenBrightnessListener = listener;
    }


    /** ------------------- 播放器回调 --------------------------- */

    /**
     * 广告视频播放器准备对外接口监听
     */
    public static class VideoPlayerPreparedListener implements IPlayer.OnPreparedListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;
        private final boolean isAdvPlayer;

        public VideoPlayerPreparedListener(AliyunVodPlayerView aliyunVodPlayerView, boolean isAdvPlayer) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
            this.isAdvPlayer = isAdvPlayer;
        }

        @Override
        public void onPrepared() {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                if (isAdvPlayer) {
                    //视频广告的播放器
                    aliyunVodPlayerView.advVideoPlayerPrepared();
                } else {
                    //原视频的播放器
                    aliyunVodPlayerView.sourceVideoPlayerPrepared();
                }
            }
        }
    }

    /**
     * 视频广告准备完成
     */
    private void advVideoPlayerPrepared() {
        if (mAdvVideoView == null) {
            return;
        }
        if (mTipsView != null) {
            mTipsView.hideNetLoadingTipView();
        }
        AliPlayer mAdvVideoAliyunVodPlayer = mAdvVideoView.getAdvVideoAliyunVodPlayer();
        if (mAdvVideoAliyunVodPlayer == null) {
            return;
        }
        MediaInfo mMediaInfo = mAdvVideoAliyunVodPlayer.getMediaInfo();
        if (mMediaInfo == null) {
            return;
        }
        if (mSurfaceView != null && mPlayerState == IPlayer.prepared) {
            mSurfaceView.setVisibility(View.GONE);
        }

        if (mAdvVideoView != null && mPlayerState == IPlayer.prepared) {
            mAdvVideoView.setSurfaceViewVisibility(View.VISIBLE);
        }

        if (mControlView != null) {
            mControlView.hide(ViewAction.HideType.Normal);
        }

        mAdvVideoMediaInfo = mMediaInfo;
        mAdvDuration = mAdvVideoMediaInfo.getDuration();

        /** 判断是否需要发送handler,如果是第一次，那么需要发送handler进行初始化,
         * 否则不需要发送,否则视频广告的进度条初始化多次,导致进度显示会有异常 */
        if (mAdvVideoCount == 0) {
            Message msg = Message.obtain();
            msg.what = ADV_VIDEO_PREPARED;
            msg.obj = mAdvVideoMediaInfo;
            mVodPlayerHandler.sendMessage(msg);
        }
    }

    /**
     * 原视频准备完成
     */
    private void sourceVideoPlayerPrepared() {
        //需要将mThumbnailPrepareSuccess重置,否则会出现缩略图错乱的问题
        mThumbnailPrepareSuccess = false;
        if (mThumbnailView != null) {
            mThumbnailView.setThumbnailPicture(null);
        }
        if (mAliyunRenderView == null) {
            return;
        }
        mAliyunMediaInfo = mAliyunRenderView.getMediaInfo();
        if (mAliyunMediaInfo == null) {
            return;
        }

        List<Thumbnail> thumbnailList = mAliyunMediaInfo.getThumbnailList();
        if (thumbnailList != null && thumbnailList.size() > 0) {

            mThumbnailHelper = new ThumbnailHelper(thumbnailList.get(0).mURL);

            mThumbnailHelper.setOnPrepareListener(new ThumbnailHelper.OnPrepareListener() {
                @Override
                public void onPrepareSuccess() {
                    mThumbnailPrepareSuccess = true;
                }

                @Override
                public void onPrepareFail() {
                    mThumbnailPrepareSuccess = false;
                }
            });

            mThumbnailHelper.prepare();

            mThumbnailHelper.setOnThumbnailGetListener(new ThumbnailHelper.OnThumbnailGetListener() {
                @Override
                public void onThumbnailGetSuccess(long l, ThumbnailBitmapInfo thumbnailBitmapInfo) {
                    if (thumbnailBitmapInfo != null && thumbnailBitmapInfo.getThumbnailBitmap() != null) {
                        Bitmap thumbnailBitmap = thumbnailBitmapInfo.getThumbnailBitmap();
                        mThumbnailView.setTime(TimeFormater.formatMs(l), "/" + TimeFormater.formatMs(getDuration()));
                        mThumbnailView.setThumbnailPicture(thumbnailBitmap);
                    }
                }

                @Override
                public void onThumbnailGetFail(long l, String s) {
                }
            });
        }
        //防止服务器信息和实际不一致
        mSourceDuration = mAliyunRenderView.getDuration();
        mAliyunMediaInfo.setDuration((int) mSourceDuration);
        //直播流
        if (mSourceDuration <= 0) {
            TrackInfo trackVideo = mAliyunRenderView.currentTrack(TrackInfo.Type.TYPE_VIDEO);
            TrackInfo trackAudio = mAliyunRenderView.currentTrack(TrackInfo.Type.TYPE_AUDIO);
            if (trackVideo == null && trackAudio != null) {
                Toast.makeText(getContext(), getContext().getString(R.string.alivc_player_audio_stream), Toast.LENGTH_SHORT).show();
            } else if (trackVideo != null && trackAudio == null) {
                Toast.makeText(getContext(), getContext().getString(R.string.alivc_player_video_stream), Toast.LENGTH_SHORT).show();
            }

        }
        List<TrackInfo> trackInfos = mAliyunMediaInfo.getTrackInfos();
        if (trackInfos != null) {
            for (TrackInfo trackInfo : trackInfos) {
                if (trackInfo.getType() == TrackInfo.Type.TYPE_VOD) {
                    String vodPlayUrl = trackInfo.getVodPlayUrl();
                    if (TextUtils.isEmpty(vodPlayUrl) || vodPlayUrl.contains("encrypt")) {
                        Config.DLNA_URL = "";
                    } else {
                        Config.DLNA_URL = trackInfo.getVodPlayUrl();
                    }
                    break;
                }
            }
        }

        //使用用户设置的标题
        if (!GlobalPlayerConfig.IS_VIDEO) {
            TrackInfo trackInfo = mAliyunRenderView.currentTrack(TrackInfo.Type.TYPE_VOD.ordinal());
            if (trackInfo != null) {
                mControlView.setMediaInfo(mAliyunMediaInfo, mAliyunRenderView.currentTrack(TrackInfo.Type.TYPE_VOD.ordinal()).getVodDefinition());
            } else {
                mControlView.setMediaInfo(mAliyunMediaInfo, "FD");
            }

            mControlView.setScreenModeStatus(mCurrentScreenMode);
            mGestureView.show();
        }

        mControlView.setHideType(ViewAction.HideType.Normal);
        mGestureView.setHideType(ViewAction.HideType.Normal);
        if (mTipsView != null) {
            mTipsView.hideNetLoadingTipView();
            mTipsView.hideBufferLoadingTipView();
        }
        //如果是视频广告，那么走这里并不会显示试看view,试看功能放在视频广告或者图片广告结束之后
        if (GlobalPlayerConfig.IS_VIDEO) {
            //如果是视频广告
            if (!mIsVipRetry) {
                mSurfaceView.setVisibility(View.GONE);
            }

            Message msg = Message.obtain();
            msg.what = SOURCE_VIDEO_PREPARED;
            msg.obj = mAliyunMediaInfo;
            mVodPlayerHandler.sendMessage(msg);

            return;
        } else if (GlobalPlayerConfig.IS_TRAILER) {
            //试看
            if (mTrailersView != null) {
                mTrailersView.trailerPlayTipsIsShow(true);
            }
        } else {
            if (mSurfaceView != null) {
                mSurfaceView.setVisibility(View.VISIBLE);
            }

            if (mAdvVideoView != null) {
                mAdvVideoView.setSurfaceViewVisibility(View.GONE);
            }
            setCoverUri(mAliyunMediaInfo.getCoverUrl());
        }

        //准备成功之后可以调用start方法开始播放
        if (mOutPreparedListener != null) {
            mOutPreparedListener.onPrepared();
        }
        mIsVipRetry = false;
    }

    /**
     * 如果是续播，则直接回调视频缓冲成功，和首帧渲染回调，并且更新进度
     */
    private void onContinuePlayStart() {
        Log.i(TAG, "onContinuePlayStart");
        sourceVideoPlayerPrepared();
        sourceVideoPlayerOnVideoRenderingStart();
        sourceVideoPlayerStateChanged(GlobalPlayerConfig.PlayState.playState);
    }


    private static class VideoPlayerErrorListener implements IPlayer.OnErrorListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;
        private final boolean isAdvPlayer;

        public VideoPlayerErrorListener(AliyunVodPlayerView aliyunVodPlayerView, boolean isAdvPlayer) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
            this.isAdvPlayer = isAdvPlayer;
        }

        @Override
        public void onError(ErrorInfo errorInfo) {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                if (isAdvPlayer) {
                    aliyunVodPlayerView.advVideoPlayerError(errorInfo);
                } else {
                    aliyunVodPlayerView.sourceVideoPlayerError(errorInfo);
                }
            }
        }
    }

    /**
     * 视频广告错误监听
     */
    private void advVideoPlayerError(ErrorInfo errorInfo) {
        if (mTipsView != null) {
            mTipsView.hideAll();
        }
        //出错之后解锁屏幕，防止不能做其他操作，比如返回。
        lockScreen(false);

        showErrorTipView(errorInfo.getCode().getValue(), Integer.toHexString(errorInfo.getCode().getValue()), errorInfo.getMsg());

        if (mOutErrorListener != null) {
            mOutErrorListener.onError(errorInfo);
        }
    }

    /**
     * 原视频错误监听
     */
    private void sourceVideoPlayerError(ErrorInfo errorInfo) {
        if (mTipsView != null) {
            mTipsView.hideAll();
        }
        //出错之后解锁屏幕，防止不能做其他操作，比如返回。
        lockScreen(false);

        //errorInfo.getExtra()展示为null,修改为显示errorInfo.getCode的十六进制的值
        showErrorTipView(errorInfo.getCode().getValue(), Integer.toHexString(errorInfo.getCode().getValue()), errorInfo.getMsg());


        if (mOutErrorListener != null) {
            mOutErrorListener.onError(errorInfo);
        }
    }

    /**
     * 播放器加载状态监听
     */
    private static class VideoPlayerLoadingStatusListener implements IPlayer.OnLoadingStatusListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;
        private final boolean isAdvPlayer;

        public VideoPlayerLoadingStatusListener(AliyunVodPlayerView aliyunVodPlayerView, boolean isAdvPlayer) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
            this.isAdvPlayer = isAdvPlayer;
        }

        @Override
        public void onLoadingBegin() {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                if (isAdvPlayer) {
                    aliyunVodPlayerView.advVideoPlayerLoadingBegin();
                } else {
                    aliyunVodPlayerView.sourceVideoPlayerLoadingBegin();
                }
            }
        }

        @Override
        public void onLoadingProgress(int percent, float v) {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                if (isAdvPlayer) {
                    aliyunVodPlayerView.advVideoPlayerLoadingProgress(percent);
                } else {
                    aliyunVodPlayerView.sourceVideoPlayerLoadingProgress(percent);
                }
            }
        }

        @Override
        public void onLoadingEnd() {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                if (isAdvPlayer) {
                    aliyunVodPlayerView.advVideoPlayerLoadingEnd();
                } else {
                    aliyunVodPlayerView.sourceVideoPlayerLoadingEnd();
                }
            }
        }
    }

    /**
     * 广告视频开始加载
     */
    private void advVideoPlayerLoadingBegin() {
        if (mTipsView != null) {
            mTipsView.showBufferLoadingTipView();
        }
    }

    /**
     * 原视频开始加载
     */
    private void sourceVideoPlayerLoadingBegin() {
        if (mIsAudioMode)
            return;
        if (mTipsView != null) {
            //视频广告,并且广告视频在播放状态,不要展示loading
            if (GlobalPlayerConfig.IS_VIDEO && mAdvVideoPlayerState == IPlayer.started) {
            } else {
                mTipsView.hideNetLoadingTipView();
                mTipsView.showBufferLoadingTipView();
            }
        }
    }

    /**
     * 广告视频开始加载进度
     */
    private void advVideoPlayerLoadingProgress(int percent) {
        if (mTipsView != null) {
            mTipsView.updateLoadingPercent(percent);
        }
    }

    /**
     * 原视频开始加载进度
     */
    private void sourceVideoPlayerLoadingProgress(int percent) {

        if (mIsAudioMode)
            return;
        if (mTipsView != null) {
            //视频广告,并且广告视频在播放状态,不要展示loading
            if (GlobalPlayerConfig.IS_VIDEO && mAdvVideoPlayerState == IPlayer.started) {

            } else {
                mTipsView.updateLoadingPercent(percent);
            }
            if (percent == 100) {
                mTipsView.hideBufferLoadingTipView();
            }
        }
    }

    /**
     * 广告视频加载结束
     */
    private void advVideoPlayerLoadingEnd() {
        if (mTipsView != null) {
            mTipsView.hideBufferLoadingTipView();
            mTipsView.hideErrorTipView();
        }

        if (isPlaying()) {
            mTipsView.hideErrorTipView();
        }

        hasLoadEnd.put(mAdvVideoMediaInfo, true);
        vodPlayerLoadEndHandler.sendEmptyMessage(1);
    }

    /**
     * 原视频加载结束
     */
    private void sourceVideoPlayerLoadingEnd() {

        if (mTipsView != null) {
            if (isPlaying()) {
                mTipsView.hideErrorTipView();
            }
            mTipsView.hideBufferLoadingTipView();
        }
        if (mControlView != null) {
            mControlView.setHideType(ViewAction.HideType.Normal);
        }
        if (mGestureView != null) {
            mGestureView.setHideType(ViewAction.HideType.Normal);
            mGestureView.show();
        }
        hasLoadEnd.put(mAliyunMediaInfo, true);
        vodPlayerLoadEndHandler.sendEmptyMessage(1);
    }


    /**
     * 播放器状态改变监听
     */
    private static class VideoPlayerStateChangedListener implements IPlayer.OnStateChangedListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;
        private final boolean isAdvPlayer;

        public VideoPlayerStateChangedListener(AliyunVodPlayerView aliyunVodPlayerView, boolean isAdvPlayer) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
            this.isAdvPlayer = isAdvPlayer;
        }


        @Override
        public void onStateChanged(int newState) {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                if (isAdvPlayer) {
                    aliyunVodPlayerView.advVideoPlayerStateChanged(newState);
                } else {
                    aliyunVodPlayerView.sourceVideoPlayerStateChanged(newState);
                }
            }
        }
    }

    /**
     * 广告视频状态改变
     */
    private void advVideoPlayerStateChanged(int newState) {
        mAdvVideoPlayerState = newState;
        if (newState == IPlayer.started) {
            if (mControlView != null) {
                mControlView.setVisibility(View.GONE);
            }
            if (mMarqueeView != null) {
                mMarqueeView.stopFlip();
            }
            if (mDanmakuView != null) {
                mDanmakuView.hide();
            }
            if (mSurfaceView != null) {
                mSurfaceView.setVisibility(View.GONE);
            }

            if (mAdvVideoView != null) {
                mAdvVideoView.setSurfaceViewVisibility(View.VISIBLE);
            }
            //如果广告正在播放,暂停视频播放
            if (mAliyunRenderView != null) {
                mAliyunRenderView.pause();
            }
        }
    }

    /**
     * 原视频状态改变监听
     */
    private void sourceVideoPlayerStateChanged(int newState) {
        mPlayerState = newState;
        if (newState == IPlayer.stopped
                || newState == IPlayer.paused) {
            if (mOnStoppedListener != null) {
                mOnStoppedListener.onStop();
            }
            if (mControlView != null) {
                mControlView.setPlayState(ControlView.PlayState.NotPlaying);
            }
        } else if (newState == IPlayer.started) {
            if (mControlView != null) {
                mControlView.setPlayState(ControlView.PlayState.Playing);
            }
        }
    }

    /**
     * 播放器播放完成监听
     */
    private static class VideoPlayerCompletionListener implements IPlayer.OnCompletionListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;
        private final boolean isAdvPlayer;

        public VideoPlayerCompletionListener(AliyunVodPlayerView aliyunVodPlayerView, boolean isAdvPlayer) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
            this.isAdvPlayer = isAdvPlayer;
        }

        @Override
        public void onCompletion() {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                if (isAdvPlayer) {
                    aliyunVodPlayerView.advVideoPlayerCompletion();
                } else {
                    aliyunVodPlayerView.sourceVideoPlayerCompletion();
                }
            }
        }
    }

    /**
     * 广告视频播放完成
     */
    private void advVideoPlayerCompletion() {
        //如果是同时有广告视频并且有试看,展示试看5分钟的view
        if (GlobalPlayerConfig.IS_TRAILER && mTrailersView != null) {
            mTrailersView.trailerPlayTipsIsShow(true);
        }

        showDanmakuAndMarquee();
        mAdvVideoCount++;
        inSeek = false;

        //视频广告播放完成，则开始播放原视频
        afterAdvVideoPlayerComplete();
    }

    /**
     * 原视频播放完成
     */
    private void sourceVideoPlayerCompletion() {
        inSeek = false;

        if (mOutCompletionListener != null) {
            //如果有视频广告,则需要判断是否包含末尾视频广告
            if (GlobalPlayerConfig.IS_VIDEO && advStyleIsIncludeEnd()) {
                //如果包含了末尾视频广告,需要判断是否需要播放末尾视频广告
                //如果是试看视频,并且当前结束时的时长为达到试看时长,则播放视频广告
                if (GlobalPlayerConfig.IS_TRAILER && mCurrentPosition < TRAILER * 1000) {
                    startAdvVideo();
                } else {
                    //否则,如果是试看,则显示试看结束,不是试看,则播放结束
                    if (GlobalPlayerConfig.IS_TRAILER) {
                        if (mTrailersView != null && mCurrentPosition >= (TRAILER * 1000)) {
                            mTrailersView.trailerPlayTipsIsShow(false);
                        }
                    } else {
                        mOutCompletionListener.onCompletion();
                    }
                }
            } else {
                //没有视频广告,则判断,如果是试看且结束时的时长达到试看时长,则显示VIP开通View
                if (GlobalPlayerConfig.IS_TRAILER && mTrailersView != null && mCurrentPosition >= (TRAILER * 1000)) {
                    mTrailersView.trailerPlayTipsIsShow(false);
                } else {
                    //否则播放结束
                    mOutCompletionListener.onCompletion();
                }
            }
        }
        updateWhenPlayComplete();
    }

    private void updateWhenPlayComplete() {
        if (mIsAudioMode) {
            if (mAudioModeView != null) {
                mAudioModeView.onPlayEnd();
            }
        }
    }

    /**
     * 播放器Info监听
     */
    private static class VideoPlayerInfoListener implements IPlayer.OnInfoListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;
        private final boolean isAdvPlayer;

        public VideoPlayerInfoListener(AliyunVodPlayerView aliyunVodPlayerView, boolean isAdvPlayer) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
            this.isAdvPlayer = isAdvPlayer;
        }

        @Override
        public void onInfo(InfoBean infoBean) {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                if (isAdvPlayer) {
                    aliyunVodPlayerView.advVideoPlayerInfo(infoBean);
                } else {
                    aliyunVodPlayerView.sourceVideoPlayerInfo(infoBean);
                }
            }
        }
    }

    /**
     * 广告视频Info
     */
    private void advVideoPlayerInfo(InfoBean infoBean) {
        //清晰度切换监听
        if (infoBean.getCode().getValue() == TrackInfo.Type.TYPE_VOD.ordinal()) {
            //切换成功后就开始播放
            mControlView.setCurrentQuality(TrackInfo.Type.TYPE_VOD.name());

            if (mTipsView != null) {
                mTipsView.hideNetLoadingTipView();
            }
        } else if (infoBean.getCode() == InfoCode.BufferedPosition) {

        } else if (infoBean.getCode() == InfoCode.CurrentPosition) {
            //currentPosition
            hideDanmakuAndMarquee();
            mAdvCurrentPosition = infoBean.getExtraValue();

            if (mControlView != null) {
                mControlView.setAdvVideoPosition((int) (mAdvCurrentPosition + mCurrentPosition + mAdvTotalPosition), (int) mCurrentPosition);
            }
        } else {
            if (mOutInfoListener != null) {
                mOutInfoListener.onInfo(infoBean);
            }
        }
    }

    /**
     * 原视频Info
     */
    private void sourceVideoPlayerInfo(InfoBean infoBean) {
        if (infoBean.getCode() == InfoCode.AutoPlayStart) {
            //自动播放开始,需要设置播放状态
            if (mControlView != null) {
                mControlView.setPlayState(ControlView.PlayState.Playing);
            }
            if (mOutAutoPlayListener != null) {
                mOutAutoPlayListener.onAutoPlayStarted();
            }
        } else if (infoBean.getCode() == InfoCode.BufferedPosition) {
            //更新bufferedPosition
            mVideoBufferedPosition = infoBean.getExtraValue();
            mControlView.setVideoBufferPosition((int) mVideoBufferedPosition);
        } else if (infoBean.getCode() == InfoCode.CurrentPosition) {
            //更新currentPosition
            mCurrentPosition = infoBean.getExtraValue();
            if (mDanmakuView != null && mDanmakuOpen && !mIsAudioMode) {
                mDanmakuView.setCurrentPosition((int) mCurrentPosition);
            }
            if (mControlView != null) {
                //如果是试看视频,并且试看已经结束了,要屏蔽其他按钮的操作
                mControlView.setOtherEnable(true);
            }
            if (GlobalPlayerConfig.IS_VIDEO) {
                //判断,是否需要暂停原视频,播放广告视频
                if (mControlView != null && mControlView.isNeedToPause((int) infoBean.getExtraValue(), mAdvVideoCount)) {
                    if (infoBean.getExtraValue() < TRAILER * 1000) {
                        startAdvVideo();
                    }
                }
                if (mControlView != null && !inSeek && mPlayerState == IPlayer.started) {
                    /*
                        由于关键帧的问题,seek到sourceDuration / 2时间点会导致进度条和广告时间对应不上,导致在播放原视频的时候进度条还在广告进度条范围内
                     */
                    if (mAdvVideoCount == 2 && ((mAdvTotalPosition + mCurrentPosition) < (mAdvTotalPosition + mSourceDuration / 2))) {
                        mControlView.setAdvVideoPosition((int) (mAdvTotalPosition + mSourceDuration / 2), (int) mCurrentPosition);
                    } else {
                        mControlView.setAdvVideoPosition((int) (mAdvTotalPosition + mCurrentPosition), (int) mCurrentPosition);
                    }
                }
            } else {
                if (mControlView != null && !inSeek && mPlayerState == IPlayer.started) {
                    mControlView.setVideoPosition((int) mCurrentPosition);
                }
            }
        }
        if (mOutInfoListener != null) {
            mOutInfoListener.onInfo(infoBean);
        }
    }

    /**
     * 播放器Render监听
     */
    private static class VideoPlayerRenderingStartListener implements IPlayer.OnRenderingStartListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;
        private final boolean isAdvPlayer;

        public VideoPlayerRenderingStartListener(AliyunVodPlayerView aliyunVodPlayerView, boolean isAdvPlayer) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
            this.isAdvPlayer = isAdvPlayer;
        }

        @Override
        public void onRenderingStart() {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                if (isAdvPlayer) {
                    aliyunVodPlayerView.advVideoPlayerOnVideoRenderingStart();
                } else {
                    aliyunVodPlayerView.sourceVideoPlayerOnVideoRenderingStart();
                }
            }
        }
    }

    /**
     * 视频广告播放器返回按钮监听
     */
    private static class VideoPlayerAdvBackImageViewListener implements AdvVideoView.OnBackImageViewClickListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;

        public VideoPlayerAdvBackImageViewListener(AliyunVodPlayerView aliyunVodPlayerView) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
        }

        @Override
        public void onBackImageViewClick() {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                aliyunVodPlayerView.onAdvBackImageViewClickListener();
            }
        }
    }

    private void onAdvBackImageViewClickListener() {
        if (mOnFinishListener != null) {
            mOnFinishListener.onFinishClick();
        }
    }

    /**
     * 广告视频onVideoRenderingStart
     */
    private void advVideoPlayerOnVideoRenderingStart() {
        if (mCoverView != null) {
            mCoverView.setVisibility(GONE);
        }
        if (mOutFirstFrameStartListener != null) {
            mOutFirstFrameStartListener.onRenderingStart();
        }
    }

    /**
     * 原视频onVideoRenderingStart
     */
    private void sourceVideoPlayerOnVideoRenderingStart() {
        mCoverView.setVisibility(GONE);
        if (!mAliyunRenderView.getListPlayer().isPlayComplete()) {
            hideReplay();
        }
        if (mOutFirstFrameStartListener != null) {
            mOutFirstFrameStartListener.onRenderingStart();
        }
        if (mIsAudioMode && mAudioModeView != null && mAliyunRenderView != null) {
            mAudioModeView.setUpData(getMediaInfo().getCoverUrl(), mCurrentScreenMode, mAliyunRenderView.getListPlayer().isPlaying(), false,
                    mAliyunRenderView.getListPlayer().isPlayComplete());
            if (mAliyunRenderView.getListPlayer().isPlaying()) {
                mPlayerState = IPlayer.started;
            } else if (mAliyunRenderView.getListPlayer().isPlayComplete()) {
                mPlayerState = IPlayer.stopped;
            } else {
                mPlayerState = IPlayer.paused;
            }
        }
    }

    /**
     * 字幕显示和隐藏监听
     */
    private static class VideoPlayerSubtitleDeisplayListener implements IPlayer.OnSubtitleDisplayListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;

        public VideoPlayerSubtitleDeisplayListener(AliyunVodPlayerView aliyunVodPlayerView) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
        }

        @Override
        public void onSubtitleExtAdded(int trackIndex, String url) { }

        @Override
        public void onSubtitleHeader(int i, String s) { }

        @Override
        public void onSubtitleShow(int trackIndex, long id, String data) {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                aliyunVodPlayerView.onSubtitleShow(id, data);
            }
        }

        @Override
        public void onSubtitleHide(int trackIndex, long id) {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                aliyunVodPlayerView.onSubtitleHide(id);
            }
        }
    }

    /**
     * 字幕隐藏
     *
     * @param id 索引
     */
    private void onSubtitleHide(long id) {
        mSubtitleView.dismiss(id + "");
    }

    /**
     * 字幕显示
     *
     * @param id   索引
     * @param data 数据
     */
    private void onSubtitleShow(long id, String data) {
        SubtitleView.Subtitle subtitle = new SubtitleView.Subtitle();
        subtitle.id = id + "";
        subtitle.content = data;
        mSubtitleView.show(subtitle);
    }

    /**
     * 播放器TrackChanged监听
     */
    private static class VideoPlayerTrackChangedListener implements IPlayer.OnTrackChangedListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;

        public VideoPlayerTrackChangedListener(AliyunVodPlayerView aliyunVodPlayerView) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
        }

        @Override
        public void onChangedSuccess(TrackInfo trackInfo) {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                aliyunVodPlayerView.sourceVideoPlayerTrackInfoChangedSuccess(trackInfo);
            }
        }

        @Override
        public void onChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                aliyunVodPlayerView.sourceVideoPlayerTrackInfoChangedFail(trackInfo, errorInfo);
            }
        }
    }

    /**
     * 原视频 trackInfoChangedSuccess
     */
    private void sourceVideoPlayerTrackInfoChangedSuccess(TrackInfo trackInfo) {
        //清晰度切换监听
        if (trackInfo.getType() == TrackInfo.Type.TYPE_VOD) {
            //切换成功后就开始播放
            mControlView.setCurrentQuality(trackInfo.getVodDefinition());
            if (mIsScreenCosting) {
                //在投屏中
                Config.DLNA_URL = trackInfo.getVodPlayUrl();
                if (mScreenCostingView != null) {
                    mScreenCostingView.play((int) mCurrentPosition);
                }
                if (mControlView != null) {
                    mControlView.setVideoPosition((int) mCurrentPosition);
                }
            } else {
                start(false);
            }

            if (mTipsView != null) {
                mTipsView.hideNetLoadingTipView();
            }
        }
        if (mOutOnTrackChangedListener != null) {
            mOutOnTrackChangedListener.onChangedSuccess(trackInfo);
        }
    }

    /**
     * 原视频 trackInfochangedFail
     */
    private void sourceVideoPlayerTrackInfoChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {
        //失败的话，停止播放，通知上层
        if (mTipsView != null) {
            mTipsView.hideNetLoadingTipView();
        }
        stop();
        if (mOutOnTrackChangedListener != null) {
            mOutOnTrackChangedListener.onChangedFail(trackInfo, errorInfo);
        }
    }

    /**
     * 播放器seek完成监听
     */
    private static class VideoPlayerOnSeekCompleteListener implements IPlayer.OnSeekCompleteListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;

        public VideoPlayerOnSeekCompleteListener(AliyunVodPlayerView aliyunVodPlayerView) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
        }

        @Override
        public void onSeekComplete() {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                aliyunVodPlayerView.sourceVideoPlayerSeekComplete();
            }
        }
    }

    /**
     * 原视频seek完成
     */
    private void sourceVideoPlayerSeekComplete() {
        inSeek = false;

        if (mOuterSeekCompleteListener != null) {
            mOuterSeekCompleteListener.onSeekComplete();
        }
    }

    /**
     * sei
     */
    private static class VideoPlayerOnSeiDataListener implements IPlayer.OnSeiDataListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;

        public VideoPlayerOnSeiDataListener(AliyunVodPlayerView aliyunVodPlayerView) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
        }

        @Override
        public void onSeiData(int type, byte[] bytes) {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                aliyunVodPlayerView.onSeiData(type, bytes);
            }
        }
    }

    private void onSeiData(int type, byte[] bytes) {
        if (mOutOnSeiDataListener != null) {
            mOutOnSeiDataListener.onSeiData(type, bytes);
        }
    }

    public void setOutOnSeiDataListener(IPlayer.OnSeiDataListener listener) {
        this.mOutOnSeiDataListener = listener;
    }

    private static class VideoPlayerOnVerifyStsCallback implements AliPlayer.OnVerifyTimeExpireCallback {

        private final WeakReference<AliyunVodPlayerView> weakReference;

        public VideoPlayerOnVerifyStsCallback(AliyunVodPlayerView aliyunVodPlayerView) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
        }

        @Override
        public AliPlayer.Status onVerifySts(StsInfo stsInfo) {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                return aliyunVodPlayerView.onVerifySts(stsInfo);
            }
            return AliPlayer.Status.Valid;
        }

        @Override
        public AliPlayer.Status onVerifyAuth(VidAuth vidAuth) {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                return aliyunVodPlayerView.onVerifyAuth(vidAuth);
            }
            return AliPlayer.Status.Valid;
        }
    }

    public void setOutOnVerifyTimeExpireCallback(AliPlayer.OnVerifyTimeExpireCallback listener) {
        this.mOutOnVerifyTimeExpireCallback = listener;
    }

    private AliPlayer.Status onVerifyAuth(VidAuth vidAuth) {
        if (mOutOnVerifyTimeExpireCallback != null) {
            return mOutOnVerifyTimeExpireCallback.onVerifyAuth(vidAuth);
        }
        return AliPlayer.Status.Valid;
    }

    private AliPlayer.Status onVerifySts(StsInfo stsInfo) {
        if (mOutOnVerifyTimeExpireCallback != null) {
            return mOutOnVerifyTimeExpireCallback.onVerifySts(stsInfo);
        }
        return AliPlayer.Status.Valid;
    }

    /**
     * 播放器截图事件监听
     */
    private static class VideoPlayerOnSnapShotListener implements IPlayer.OnSnapShotListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;

        public VideoPlayerOnSnapShotListener(AliyunVodPlayerView aliyunVodPlayerView) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
        }

        @Override
        public void onSnapShot(Bitmap bitmap, int width, int height) {
            AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
            if (aliyunVodPlayerView != null) {
                aliyunVodPlayerView.sourceVideoSnapShot(bitmap, width, height);
            }
        }
    }

    /**
     * 原视频截图完成
     */
    private void sourceVideoSnapShot(final Bitmap bitmap, int width, int height) {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                String videoPath = FileUtils.getDir(getContext()) + GlobalPlayerConfig.SNAP_SHOT_PATH;
                String bitmapPath = FileUtils.saveBitmap(bitmap, videoPath);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    FileUtils.saveImgToMediaStore(getContext().getApplicationContext(), bitmapPath, "image/png");
                } else {
                    MediaScannerConnection.scanFile(getContext().getApplicationContext(),
                            new String[]{bitmapPath},
                            new String[]{"image/png"}, null);
                }
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AVToast.show(getContext(),true,R.string.alivc_player_snap_shot_save_success);
                    }
                });
            }
        });
    }

    /** ------------------- 播放器回调 end--------------------------- */

    /**
     * Handler
     */
    private static class VodPlayerHandler extends Handler {

        private final WeakReference<AliyunVodPlayerView> weakReference;

        public VodPlayerHandler(AliyunVodPlayerView aliyunVodPlayerView) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ADV_VIDEO_PREPARED:
                case SOURCE_VIDEO_PREPARED:

                    AliyunVodPlayerView aliyunVodPlayerView = weakReference.get();
                    if (aliyunVodPlayerView == null) {
                        return;
                    }
                    if (msg.what == ADV_VIDEO_PREPARED) {
                        aliyunVodPlayerView.mAdvVideoMediaInfo = (MediaInfo) msg.obj;
                    }
                    if (msg.what == SOURCE_VIDEO_PREPARED) {
                        aliyunVodPlayerView.mSourceVideoMediaInfo = (MediaInfo) msg.obj;
                    }

                    //视频广告和原视频都准备完成
                    if (aliyunVodPlayerView.mSourceVideoMediaInfo != null && aliyunVodPlayerView.mAdvVideoMediaInfo != null) {
                        MediaInfo mediaInfo = new MediaInfo();
                        //重新创建一个新的MediaInfo,并且重新计算duration
                        mediaInfo.setDuration(aliyunVodPlayerView.mAdvVideoMediaInfo.getDuration()
                                + aliyunVodPlayerView.mSourceVideoMediaInfo.getDuration());

                        if (aliyunVodPlayerView.mAliyunRenderView != null) {
                            TrackInfo trackInfo = aliyunVodPlayerView.mAliyunRenderView.currentTrack(TrackInfo.Type.TYPE_VOD.ordinal());
                            if (trackInfo != null) {
                                aliyunVodPlayerView.mControlView.setMediaInfo(aliyunVodPlayerView.mSourceVideoMediaInfo,
                                        trackInfo.getVodDefinition());
                            }
                        }

                        aliyunVodPlayerView.mControlView.setHideType(ViewAction.HideType.Normal);
                        aliyunVodPlayerView.mGestureView.setHideType(ViewAction.HideType.Normal);
                        aliyunVodPlayerView.mControlView.setPlayState(ControlView.PlayState.Playing);
                        //如果是投屏状态,则不显示视频广告的seekBar
                        aliyunVodPlayerView.mControlView.setMutiSeekBarInfo(aliyunVodPlayerView.mAdvVideoMediaInfo.getDuration(),
                                aliyunVodPlayerView.mSourceVideoMediaInfo.getDuration(), aliyunVodPlayerView.mAdvPosition);
                        aliyunVodPlayerView.mControlView.hideNativeSeekBar();
                        aliyunVodPlayerView.mGestureView.show();


                        if (aliyunVodPlayerView.mTipsView != null) {
                            aliyunVodPlayerView.mTipsView.hideNetLoadingTipView();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void hideSystemUI() {
        AliyunVodPlayerView.this.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * ---------------------------------对外接口-------------------------------------
     */

    /**
     * 设置打点信息
     */
    public void setDotInfo(List<DotBean> dotBean) {
        if (mControlView != null) {
            mControlView.setDotInfo(dotBean);
        }
    }

    /**
     * 设置硬解码开关
     */
    public void setEnableHardwareDecoder(boolean enableHardwareDecoder) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.enableHardwareDecoder(enableHardwareDecoder);
        }
    }

    /**
     * 设置试看时长,默认5分钟
     */
    public void setTrailerTime(int trailerTime) {
        TRAILER = trailerTime;
    }

    /**
     * 设置域名
     */
    public void setPlayDomain(String domain) {
        PLAY_DOMAIN = domain;
    }

    /**
     * 获取当前播放器正在播放的媒体信息
     */
    public MediaInfo getCurrentMediaInfo() {
        return mAliyunMediaInfo;
    }

    /**
     * 设置当前屏幕亮度
     */
    public void setScreenBrightness(int screenBrightness) {
        this.mScreenBrightness = screenBrightness;
    }

    /**
     * 获取当前屏幕亮度
     */
    public int getScreenBrightness() {
        return this.mScreenBrightness;
    }

    /**
     * 截图功能
     */
    public void snapShot() {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.snapshot();
        }
    }

    /**
     * 设置循环播放
     *
     * @param circlePlay true:循环播放
     */
    public void setCirclePlay(boolean circlePlay) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setLoop(circlePlay);
        }
    }

    /**
     * 设置播放时的镜像模式
     *
     * @param mode 镜像模式
     */
    public void setRenderMirrorMode(IPlayer.MirrorMode mode) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setMirrorMode(mode);
        }
    }

    /**
     * 设置播放时的旋转方向
     *
     * @param rotate 旋转角度
     */
    public void setRenderRotate(IPlayer.RotateMode rotate) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setRotateModel(rotate);
        }
    }

    /**
     * 获取是否在投屏中
     */
    public boolean getIsCreenCosting() {
        return mIsScreenCosting;
    }

    /**
     * 设置是否显示标题栏
     *
     * @param show true:是
     */
    public void setTitleBarCanShow(boolean show) {
        if (mControlView != null) {
            mControlView.setTitleBarCanShow(show);
        }
    }

    /**
     * 设置是否显示控制栏
     *
     * @param show true:是
     */
    public void setControlBarCanShow(boolean show) {
        if (mControlView != null) {
            mControlView.setControlBarCanShow(show);
        }

    }

    /**
     * 开启底层日志
     */
    public void enableNativeLog() {
        Logger.getInstance(getContext()).enableConsoleLog(true);
        Logger.getInstance(getContext()).setLogLevel(Logger.LogLevel.AF_LOG_LEVEL_DEBUG);
    }

    /**
     * 关闭底层日志
     */
    public void disableNativeLog() {
        Logger.getInstance(getContext()).enableConsoleLog(false);
    }

    /**
     * 设置缓存配置
     */
    public void setCacheConfig(CacheConfig cacheConfig) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setCacheConfig(cacheConfig);
        }
    }

    /**
     * 获取Config
     */
    public PlayerConfig getPlayerConfig() {
        if (mAliyunRenderView != null) {
            return mAliyunRenderView.getPlayerConfig();
        }
        return null;
    }

    /**
     * 设置Config
     */
    public void setPlayerConfig(PlayerConfig playerConfig) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setPlayerConfig(playerConfig);
        }
    }

    /**
     * 获取SDK版本号
     *
     * @return SDK版本号
     */
    public String getSDKVersion() {
        return AliPlayerFactory.getSdkVersion();
    }

    /**
     * 获取播放surfaceView
     *
     * @return 播放surfaceView
     */
    public SurfaceView getVideoPlayerView() {
        return mSurfaceView;
    }

    public View getPlayerView() {
        return mAliyunRenderView.getSurfaceView();
    }

    public AliPlayer getPlayer() {
        return mAliyunRenderView.getPlayer();
    }

    public IListPlayManager getListPlayer() {
        return mAliyunRenderView.getListPlayer();
    }

    /**
     * 设置自动播放
     *
     * @param auto true 自动播放
     */
    public void setAutoPlay(boolean auto) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setAutoPlay(auto);
        }
    }

    /**
     * 更新弹幕
     */
    public void setmDanmaku(String danmu) {
        if (mDanmakuView != null) {
            mDanmakuView.resume();
            mDanmakuView.addDanmaku(danmu, mCurrentPosition);
        }
        if (mAliyunRenderView != null) {
            mAliyunRenderView.start();
        }
        hideSystemUI();
    }

    /**
     * 锁定屏幕。锁定屏幕后，只有锁会显示，其他都不会显示。手势也不可用
     *
     * @param lockScreen 是否锁住
     */
    public void lockScreen(boolean lockScreen) {
        mIsFullScreenLocked = lockScreen;
        if (mControlView != null) {
            mControlView.setScreenLockStatus(mIsFullScreenLocked);
        }
        if (mGestureView != null) {
            mGestureView.setScreenLockStatus(mIsFullScreenLocked);
        }
    }

    /**
     * 重试播放，会从当前位置开始播放
     */
    public void reTry() {

        isCompleted = false;
        inSeek = false;

        int currentPosition = mControlView.getVideoPosition();

        if (mTipsView != null) {
            mTipsView.hideAll();
        }
        if (mControlView != null) {
            mControlView.reset();
            //防止被reset掉，下次还可以获取到这些值
            mControlView.setVideoPosition(currentPosition);
        }
        if (mGestureView != null) {
            mGestureView.reset();
        }

        if (mAliyunRenderView != null) {

            //显示网络加载的loading。。
            if (mTipsView != null) {
                mTipsView.showNetLoadingTipView();
            }
            //seek到当前的位置再播放
            if (GlobalPlayerConfig.IS_VIDEO) {
                //视频广告
                if (mAliyunRenderView != null) {
                    mIsVipRetry = true;
                    mAliyunRenderView.prepare();
                }
            } else {
                mAliyunRenderView.prepare();
                isAutoAccurate(currentPosition);
            }
        }

    }

    /**
     * 重播，将会从头开始播放
     */
    public void rePlay() {

        isCompleted = false;
        inSeek = false;

        if (mTipsView != null) {
            mTipsView.hideAll();
        }
        if (mControlView != null) {
            mControlView.reset();
        }
        if (mGestureView != null) {
            mGestureView.reset();
        }
        if (mAliyunRenderView != null) {
            //显示网络加载的loading。。
            if (mTipsView != null && !mIsAudioMode) {
                mTipsView.showNetLoadingTipView();
            }
            if (mOutOnTipClickListener != null) {
                mOutOnTipClickListener.onReplay();
            }
        }
    }

    /**
     * 切换播放速度
     *
     * @param speedValue 播放速度
     */
    public void changeSpeed(float speedValue) {
        mAliyunRenderView.setSpeed(speedValue);
        mControlView.setSpeedViewText(speedValue);
    }

    /**
     * 获取当前速率
     */
    public float getCurrentSpeed() {
        return mAliyunRenderView.getSpeed();
    }

    /**
     * 设置当前速率
     */
    public void setCurrentVolume(float progress) {
        if (progress <= 0) {
            progress = 0;
        }
        if (progress >= 1) {
            progress = 1;
        }
        this.currentVolume = progress;
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setVolume(progress);
        }
    }

    /**
     * 获取 fps
     */
    public float getOption(IPlayer.Option renderFPS) {
        if (mAliyunRenderView != null) {
            return mAliyunRenderView.getOption(renderFPS);
        }
        return 0f;
    }

    /**
     * 获取当前音量
     */
    public float getCurrentVolume() {
        if (mAliyunRenderView != null) {
            return mAliyunRenderView.getVolume();
        }
        return 0;
    }

    /**
     * 设置静音,默认是false
     */
    public void setMute(boolean isMute) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setMute(isMute);
        }
    }

    /**
     * 设置投屏音量
     */
    public void setScreenCostingVolume(int volume) {
        if (volume <= 0) {
            volume = 0;
        }
        if (volume >= 100) {
            volume = 100;
        }
        this.mScreenCostingVolume = volume;
        //如果是在投屏状态下
        if (mScreenCostingView != null && mIsScreenCosting) {
            mScreenCostingView.setVolume(mScreenCostingVolume);
        }
    }

    /**
     * 获取当前投屏音量
     */
    public int getScreenCostingVolume() {
        return mScreenCostingVolume;
    }

    /**
     * 设置弹幕透明度
     * 0无透明---100全透明
     */
    public void setDanmakuAlpha(int progress) {
        if (mDanmakuView != null) {
            mDanmakuView.setAlpha((float) (1 - progress / 100.0 * 1.0));
        }
    }

    /**
     * 设置弹幕速率
     */
    public void setDanmakuSpeed(int progress) {
        if (mDanmakuView != null) {
            mDanmakuView.setDanmakuSpeed((float) (2.5 - (100 + progress) / 100.0 * 1.0));
        }
    }

    /**
     * 设置弹幕显示区域
     */
    public void setDanmakuRegion(int progress) {
        if (mDanmakuView != null) {
            mDanmakuView.setDanmakuRegion(progress);
        }
    }

    /**
     * 投屏播放
     */
    public void screenCostPlay() {
        mIsScreenCosting = true;
        if (mAliyunRenderView != null) {
            mAliyunRenderView.pause();
        }
        // 这里需要提前进行界面隐藏和展示的操作，否则会被DLNA一直抢占资源，无法展示预期的界面效果 */
        if (mControlView != null) {
            mControlView.setInScreenCosting(mIsScreenCosting);
            mControlView.show(ViewAction.ShowType.ScreenCast);
            mControlView.startScreenCost();
        }

        if (mScreenCostingView != null) {
            mStartScreenCostingPosition = (int) mCurrentPosition;
            mScreenCostingView.play(0);
        }
    }

    /**
     * 停止投屏
     */
    public void screenCostStop() {
        mIsScreenCosting = false;
        if (mScreenCostingView != null) {
            mScreenCostingView.exit();
        }
        if (mControlView != null) {
            mControlView.setInScreenCosting(mIsScreenCosting);
        }
    }

    /**
     * 恢复弹幕设置
     */
    public void setDanmakuDefault() {
        if (mDanmakuView != null) {
            setDanmakuAlpha(DanmakuSettingView.ALPHA_PROGRESS_DEFAULT);
            setDanmakuSpeed(DanmakuSettingView.SPEED_PROGRESS_DEFAULT);
            setDanmakuRegion(DanmakuSettingView.REGION_PROGRESS_DEFAULT);
        }
    }

    /**
     * 设置封面信息
     *
     * @param uri url地址
     */
    public void setCoverUri(String uri) {
        if (mCoverView != null && !TextUtils.isEmpty(uri)) {
            ImageLoader.loadImg(uri, mCoverView);
            mCoverView.setVisibility(isPlaying() ? GONE : VISIBLE);
        }
    }

    /**
     * 设置封面id
     *
     * @param resId 资源id
     */
    public void setCoverResource(int resId) {
        if (mCoverView != null) {
            mCoverView.setImageResource(resId);
            mCoverView.setVisibility(isPlaying() ? GONE : VISIBLE);
        }
    }

    /**
     * 设置缩放模式
     *
     * @param scaleMode 缩放模式
     */
    public void setScaleMode(IPlayer.ScaleMode scaleMode) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setScaleModel(scaleMode);
        }
    }

    /**
     * 获取缩放模式
     *
     * @return 当前缩放模式
     */
    public IPlayer.ScaleMode getScaleMode() {
        IPlayer.ScaleMode scaleMode = IPlayer.ScaleMode.SCALE_ASPECT_FIT;
        if (mAliyunRenderView != null) {
            scaleMode = mAliyunRenderView.getScaleModel();
        }
        return scaleMode;
    }

    /**
     * 设置是否循环播放
     */
    public void setLoop(boolean isLoop) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setLoop(isLoop);
        }
    }

    /**
     * 是否开启循环播放
     */
    public boolean isLoop() {
        if (mAliyunRenderView != null) {
            return mAliyunRenderView.isLoop();
        }
        return false;
    }

    /**
     * 选择播放的流。
     *
     * @param trackInfo 流信息
     */
    public void selectTrack(TrackInfo trackInfo) {
        if (mAliyunRenderView != null && trackInfo != null) {
            int trackIndex = trackInfo.getIndex();
            mAliyunRenderView.selectTrack(trackIndex);
        }
    }

    public void selectTrackIndex(int index) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.selectTrack(index);
        }
    }

    /**
     * 注意：自动切换码率与其他Track的选择是互斥的。选择其他Track之后，自动切换码率就失效，不起作用了。
     */
    public void selectAutoBitrateTrack() {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.selectTrack(TrackInfo.AUTO_SELECT_INDEX);
        }
    }

    /**
     * 根据TrackInfo.Type获取当前流信息
     *
     * @param type 流类型
     * @return 流信息
     */
    public TrackInfo currentTrack(TrackInfo.Type type) {
        if (mAliyunRenderView == null) {
            return null;
        } else {
            return mAliyunRenderView.currentTrack(type);
        }
    }

    public void setOnFloatPlayViewClickListener(ControlView.OnFloatPlayViewClickListener onFloatPlayViewClickListener) {
        this.mOnFloatPlayViewClickListener = onFloatPlayViewClickListener;
    }

    public void setOnCastScreenListener(ControlView.OnCastScreenListener onCastScreenListener) {
        this.onCastScreenListener = onCastScreenListener;
    }

    public void setOnSelectSeriesListener(ControlView.OnSelectSeriesListener onSelectSeriesListener) {
        this.onSelectSeriesListener = onSelectSeriesListener;
    }

    public void setOnNextSeriesClickListener(ControlView.OnNextSeriesClick onNextSeriesClick) {
        this.onNextSeriesClick = onNextSeriesClick;
    }


    public void setOnDamkuOpenListener(ControlView.OnDamkuOpenListener listener) {
        this.onDamkuOpenListener = listener;
    }

    public void setUpConfig(boolean danmkuShow, int danmkuLocation, boolean seriesPlay) {
        Log.i(TAG, "setUpConfig danmkuShow:" + danmkuShow + " danmkuLocation:" + danmkuLocation);
        this.mDanmakuLocation = danmkuLocation;
        this.mDanmakuOpen = danmkuShow;
        if (mDanmakuOpen) {
            showDanmakuAndMarquee();
        } else {
            hideDanmakuAndMarquee();
        }
        updateDanmakuLocation(danmkuLocation);
        if (mControlView != null) {
            mControlView.setUpConfig(danmkuShow);
        }
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setListPlayOpen(seriesPlay);
        }
    }


    public void setListPlayOpen(boolean open) {
        if (mAliyunRenderView == null) {
            mAliyunRenderView.setListPlayOpen(open);
        }
    }


    private void handleLongPress() {
        mLongPressSpeed = true;
        mRecordPlaySpeed = mAliyunRenderView.getSpeed();
        mAliyunRenderView.setSpeed(2.0f);
        if (mControlView != null) {
            mControlView.showVideoSpeedTipLayout(true);
        }
    }

//-------------------------------- outter method -------------------------------------

    private void beforeSetDataSource(boolean refresh,boolean qualityForce){
        if (mAliyunRenderView == null)  return;

        if (refresh) {
            clearAllSource();
            reset();
        }

        if (mControlView != null) {
            mControlView.setForceQuality(qualityForce);
        }
    }

    /**
     * 设置LiveSts
     */
    public void setLiveStsDataSource(LiveSts liveSts) {
        beforeSetDataSource(false,liveSts.isForceQuality());

        mAliyunLiveSts = liveSts;

        if (!show4gTips()) {
            innerPrepareLiveSts(liveSts);
        }
    }


    /**
     * 设置UrlSource
     */
    public void setLocalSource(UrlSource aliyunLocalSource) {
        beforeSetDataSource(false,aliyunLocalSource.isForceQuality());

        mAliyunLocalSource = aliyunLocalSource;

        if (!show4gTips()) {
            innerPrepareUrl(aliyunLocalSource);
        }
    }

    /**
     * 设置VidSts
     */
    public void setVidSts(VidSts vidSts, boolean refresh) {
        beforeSetDataSource(refresh,vidSts.isForceQuality());
        mAliyunVidSts = vidSts;

        showVideoFunction(refresh);
    }

    /**
     * 设置VidAuth
     */
    public void setVidAuth(VidAuth aliyunPlayAuth) {
        beforeSetDataSource(false,aliyunPlayAuth.isForceQuality());

        mAliyunPlayAuth = aliyunPlayAuth;

        //4G的话先提示
        if (!show4gTips()) {
            //具体的准备操作
            innerPrepareAuth(aliyunPlayAuth);
        }
    }

    /**
     * 设置Mps
     */
    public void setVidMps(VidMps vidMps) {
        beforeSetDataSource(false,vidMps.isForceQuality());

        mAliyunVidMps = vidMps;

        //4G的话先提示
        if (!show4gTips()) {
            //具体的准备操作
            innerPrepareMps(vidMps);
        }
    }

//-------------------------------- outter method -------------------------------------

//-------------------------------- inner method --------------------------------------

    private void innerBeforePrepare(){
        if(mAliyunRenderView != null){
            mAliyunRenderView.setAutoPlay(true);
        }

        if (mTipsView != null && !mIsContinuedPlay) {
            mTipsView.showNetLoadingTipView();
        }
        if (mControlView != null) {
            mControlView.setForceQuality(true);
            mControlView.setIsMtsSource(false);
        }
        if (mQualityView != null) {
            mQualityView.setIsMtsSource(false);
        }
    }

    /**
     * 通过playAuth prepare
     */
    private void innerPrepareAuth(VidAuth aliyunPlayAuth) {
        innerBeforePrepare();
        mAliyunRenderView.setDataSource(aliyunPlayAuth);
        mAliyunRenderView.prepare();
    }

    /**
     * 通过Mps prepare
     */
    private void innerPrepareMps(VidMps vidMps) {
        innerBeforePrepare();
        mAliyunRenderView.setDataSource(vidMps);
        mAliyunRenderView.prepare();
    }

    /**
     * 准备vidsts 源
     */
    private void innerPrepareSts(VidSts vidSts) {
        innerBeforePrepare();
        if (GlobalPlayerConfig.IS_TRAILER) {
            VidPlayerConfigGen vidPlayerConfigGen = new VidPlayerConfigGen();
            //试看,默认5分钟
            vidPlayerConfigGen.addPlayerConfig("PlayDomain", PLAY_DOMAIN);
            vidPlayerConfigGen.setPreviewTime(TRAILER);
            vidSts.setPlayConfig(vidPlayerConfigGen);
        }
        mAliyunRenderView.setDataSource(vidSts);
        mAliyunRenderView.prepare();
    }

    /**
     * prepare本地播放源
     */
    private void innerPrepareUrl(UrlSource aliyunLocalSource) {
        innerBeforePrepare();
        //如果是本地视频，则全屏播放
        if (isLocalSource() && mIsNeedOnlyFullScreen) {
            changeScreenMode(AliyunScreenMode.Full, false);
        }

        if (aliyunLocalSource != null && aliyunLocalSource.getUri().startsWith("artc")) {
            PlayerConfig playerConfig = mAliyunRenderView.getPlayerConfig();
            playerConfig.mMaxDelayTime = 1000;
            playerConfig.mHighBufferDuration = 10;
            playerConfig.mStartBufferDuration = 10;
            mAliyunRenderView.setPlayerConfig(playerConfig);
        }

        mAliyunRenderView.setDataSource(aliyunLocalSource);
        mAliyunRenderView.prepare();
    }

    /**
     * 准备LiveSts 源
     */
    private void innerPrepareLiveSts(LiveSts mAliyunLiveSts) {
        innerBeforePrepare();
        mAliyunRenderView.setDataSource(mAliyunLiveSts);
        mAliyunRenderView.prepare();
    }

//-------------------------------- inner method --------------------------------------
}
