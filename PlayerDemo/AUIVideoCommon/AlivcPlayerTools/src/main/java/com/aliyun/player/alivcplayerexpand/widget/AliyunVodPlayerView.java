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
 * UI?????????????????????????????? ??????ITheme????????????????????????????????? ????????????view???????????????UI??????????????????view????????? ?????????????????????{@link GestureView} ??????????????????????????????{@link
 * ControlView} ????????????????????????{@link QualityView} ????????????????????????{@link GuideView} ??????????????????{@link TipsView}
 * ?????????????????? view ??????????????????{@link #initVideoView}????????????????????? ??????????????????view????????????????????????????????????????????????????????????????????????????????????
 */
public class AliyunVodPlayerView extends RelativeLayout implements ITheme {

    private final SharedPreferences mLongPressSP = getContext().getSharedPreferences("long_press", Context.MODE_PRIVATE);

    /**
     * ????????????
     */
    private static final String ADV_VIDEO_URL = "https://alivc-demo-cms.alicdn.com/video/videoAD.mp4";
    /**
     * ????????????
     */
    private static final String ADV_URL = "https://www.aliyun.com/product/vod?spm=5176.10695662.782639.1.4ac218e2p7BEEf";

    /**
     * ??????????????????
     */
    private static final WaterMarkRegion WATER_MARK_REGION = WaterMarkRegion.RIGHT_TOP;

    /**
     * ?????????????????????
     */
    private static final MarqueeView.MarqueeRegion MARQUEE_REGION = MarqueeView.MarqueeRegion.TOP;

    private static final String TAG = AliyunVodPlayerView.class.getSimpleName();

    /**
     * ????????????prepared??????
     */
    private static final int ADV_VIDEO_PREPARED = 0;

    /**
     * ?????????preapred??????
     */
    private static final int SOURCE_VIDEO_PREPARED = 1;

    /**
     * ??????VodePlayer ??????????????????
     */
    private final Map<MediaInfo, Boolean> hasLoadEnd = new HashMap<>();

    //????????????
    private SurfaceView mSurfaceView;
    private AudioModeView mAudioModeView;
    //????????????view
    private GestureView mGestureView;
    //??????view
    private ControlView mControlView;
    private View mLongPressVideoSpeedView;
    private int mDanmakuLocation = 2;
    private boolean mDanmakuOpen = true;
    //?????????view
    private QualityView mQualityView;
    //?????????view
    private GuideView mGuideView;
    //??????view
    private ImageView mCoverView;
    //?????????????????????
    private GestureDialogManager mGestureDialogManager;
    //??????????????????
    private NetWatchdog mNetWatchdog;
    //??????????????????
    private OrientationWatchDog mOrientationWatchDog;
    //Tips view
    private TipsView mTipsView;
    //????????????
    private LockPortraitListener mLockPortraitListener = null;
    //??????????????????
    private boolean mIsFullScreenLocked = false;
    //??????????????????
    private AliyunScreenMode mCurrentScreenMode = AliyunScreenMode.Small;
    //????????????seek???
    private boolean inSeek = false;
    //??????????????????
    private boolean isCompleted = false;
    //????????????
    private MediaInfo mAliyunMediaInfo;
    //????????????????????????
    private MediaInfo mAdvVideoMediaInfo;
    //?????????
    private MarqueeView mMarqueeView;
    /**
     * ?????????View
     */
    private ThumbnailView mThumbnailView;
    /**
     * ??????????????????
     */
    private ThumbnailHelper mThumbnailHelper;
    //???????????????????????????
    private boolean mThumbnailPrepareSuccess = false;

    //?????????handler
    private VodPlayerHandler mVodPlayerHandler;
    //??????bug,?????????????????????????????????????????????,?????????????????????????????????
    private final VodPlayerLoadEndHandler vodPlayerLoadEndHandler = new VodPlayerLoadEndHandler(this);
    //????????????buffered
    private long mVideoBufferedPosition = 0;
    //????????????currentPosition
    private long mCurrentPosition = 0;
    //??????????????? currentPosition
    private long mAdvCurrentPosition;
    //?????????????????? position
    private long mAdvTotalPosition = 0;
    //????????????????????????
    private int mPlayerState = IPlayer.idle;
    private boolean mPlayingBeforePause = true;
    //???????????????????????????,???????????????--??????--???????????????????????????   todo   ??????????????????????????????
    private final MutiSeekBarView.AdvPosition mAdvPosition = MutiSeekBarView.AdvPosition.ALL;
    //???????????????
    private long mSourceDuration;
    //??????????????????
    private long mAdvDuration;
    //????????????View
    private AdvVideoView mAdvVideoView;
    //??????view
    private PlayerDanmakuView mDanmakuView;
    //??????
    private ImageView mWaterMark;
    //??????View
    private TrailersView mTrailersView;
    //????????????View
    private ScreenCostingView mScreenCostingView;
    //?????????????????????
    private boolean mIsScreenCosting = false;
    private boolean mIsAudioMode = false;
    //??????
    private SubtitleView mSubtitleView;
    private View mContrastPlayTipView;

    //?????????????????????????????????
    private VidAuth mAliyunPlayAuth;
    private VidMps mAliyunVidMps;
    private UrlSource mAliyunLocalSource;
    private VidSts mAliyunVidSts;
    private LiveSts mAliyunLiveSts;

    //???????????????????????????
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
    // ??????????????????
    private NetConnectedListener mNetConnectedListener = null;
    // ????????????????????????
    private ControlView.OnShowMoreClickListener mOutOnShowMoreClickListener;
    private ControlView.OnVideoSpeedClickListener mOnVideoSpeedClickListener;
    //????????????????????????
    private OnPlayStateBtnClickListener onPlayStateBtnClickListener;
    //??????????????????
    private OnStoppedListener mOnStoppedListener;
    //ControView????????????
    private ControlView.OnControlViewHideListener mOnControlViewHideListener;
    private ControlView.OnFloatPlayViewClickListener mOnFloatPlayViewClickListener;
    private ControlView.OnCastScreenListener onCastScreenListener;
    private ControlView.OnDamkuOpenListener onDamkuOpenListener;
    private ControlView.OnSelectSeriesListener onSelectSeriesListener;
    private ControlView.OnNextSeriesClick onNextSeriesClick;
    //?????????,??????????????????????????????
    private OnScreenCostingVideoCompletionListener mOnScreenCostingVideoCompletionListener;
    //????????????????????????????????????????????????
    private ControlView.OnTrackInfoClickListener mOutOnTrackInfoClickListener;
    private ControlView.OnBackClickListener mOutOnBackClickListener;
    //??????????????????????????????
    private int mAdvVideoPlayerState;
    //seekTo?????????
    private int mSeekToPosition;
    //?????????seekTo?????????
    private int mSourceSeekToPosition;
    //seekTo????????????position????????????
    private int mSeekToCurrentPlayerPosition;
    //??????????????????????????????
    private int mAdvVideoCount = 0;
    //????????????????????????MIDDLE?????????????????????,??????????????????seek
    private boolean needToSeek = false;
    //??????????????????,??????seekTo?????????????????????????????????
    private AdvVideoView.IntentPlayVideo mCurrentIntentPlayVideo;
    //??????????????????
    private int mScreenBrightness;
    //???????????????????????????
    private boolean mIsOperatorPlay;
    private boolean mLongPressSpeed = false;
    //?????????MediaInfo
    private MediaInfo mSourceVideoMediaInfo;

    private float currentVolume;
    //??????????????????
    private int mScreenCostingVolume;
    //?????????????????????5??????
    public static int TRAILER = 300;
    //????????????????????????
    public static String PLAY_DOMAIN = "alivc-demo-vod-player.aliyuncs.com";
    private boolean mIsVipRetry;
    //?????????,??????????????????
    private TransportState mCurrentTransportState;
    //????????????????????????????????????
    private boolean mIsInMultiWindow;
    //???????????????,???????????????
    private int mStartScreenCostingPosition;
    //???????????????????????????
    private boolean mIsNeedOnlyFullScreen;
    //??????????????????????????????
    private boolean initNetWatch;
    //?????????+?????????View
    private AliyunRenderView mAliyunRenderView;
    private int mFullScreenType;
    private boolean mIsContinuedPlay;
    private boolean isFromRecommendList = false;
    private String mUUID = "";
    private boolean isFirstLand;
    private String mAuthorName = "";
    //??????????????????????????????????????????????????????????????????
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
     * ?????????view
     */
    public void initVideoView(IRenderView renderView, ListPlayManager listPlayer, int fullScreen) {
        mFullScreenType = fullScreen;
        isFirstLand = mFullScreenType == 1 || mFullScreenType == 2;
        //?????????handler
        mVodPlayerHandler = new AliyunVodPlayerView.VodPlayerHandler(this);
        //??????????????????
        initAliVcPlayer(renderView, listPlayer);
        //???????????????
        initCoverView();
        //???????????????view
        initGestureView();
        //?????????????????????
        initWaterMark();
        //??????????????????
        initMarquee();
        //???????????????View
        initTrailersView();
        //??????????????????view
        initQualityView();
        //??????????????????
        initControlView();
        //?????????????????????
        initAudioModeView();
        //??????????????????
        initThumbnailView();
        //???????????????view
        initTipsView();
        //????????????????????????
        initNetWatchdog();
        //???????????????????????????
        initOrientationWatchdog();
        //??????????????????????????????
        initGestureDialogManager();
        //?????????????????????
        setTheme(Theme.Blue);
        //?????????????????????????????????????????????prepare?????????????????????
        hideGestureAndControlViews();
        //???????????????
        initDanmaku();
        //??????
        initScreenCost();
        //???????????????
        initSubtitleView();
        initContrastTipLayout();
        initLongPressVideoSpeedView();
    }

    /**
     * ??????UI??????????????????
     *
     * @param theme ???????????????
     */
    @Override
    public void setTheme(Theme theme) {
        //???????????????View???????????????ITheme???????????????????????????
        int childCounts = getChildCount();
        for (int i = 0; i < childCounts; i++) {
            View view = getChildAt(i);
            if (view instanceof ITheme) {
                ((ITheme) view).setTheme(theme);
            }
        }
    }

    /**
     * ??????????????????
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
            AVToast.show(getContext(), true, getContext().getString(R.string.alivc_player_not_check_any_browser));
            return;
        }
        Intent intent = new Intent();
        intent.setData(Uri.parse(ADV_URL));
        intent.setAction(Intent.ACTION_VIEW);
        getContext().startActivity(intent);
    }

    /**
     * ????????????????????????
     */
    private void initMarquee() {
        mMarqueeView = new MarqueeView(getContext());
        addSubViewHeightWrap(mMarqueeView);
    }

    /**
     * ???????????????View
     */
    private void initTrailersView() {
        mTrailersView = new TrailersView(getContext());
        addSubView(mTrailersView);

        mTrailersView.hideAll();

        mTrailersView.setOnTrailerViewClickListener(mOnTrailerViewClickListener);

        mTrailersView.setOnTrailerViewClickListener(new TrailersView.OnTrailerViewClickListener() {
            //????????????
            @Override
            public void onTrailerPlayAgainClick() {
                if (mOnTrailerViewClickListener != null) {
                    mOnTrailerViewClickListener.onTrailerPlayAgainClick();
                }
                //???????????????????????????????????????view
                mTrailersView.hideAll();
            }

            //??????vip
            @Override
            public void onOpenVipClick() {
                if (mOnTrailerViewClickListener != null) {
                    mOnTrailerViewClickListener.onOpenVipClick();
                }
            }
        });
    }


    /**
     * ???????????????
     */
    private void initDanmaku() {
        mDanmakuView = new PlayerDanmakuView(getContext());
        addSubViewExactlyHeight(mDanmakuView, (int) (ScreenUtils.getHeight(getContext()) * 0.75), RelativeLayout.ALIGN_PARENT_TOP);
        updateDanmakuLocation(mDanmakuLocation);
    }

    /**
     * @param location ???????????????0 ?????????(???????????????25%),1 ?????????(???????????????25%),2 ????????????
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
     * ???????????????
     */
    private void initWaterMark() {
        mWaterMark = new ImageView(getContext());
        mWaterMark.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //??????????????????????????????icon????????????????????????
        mWaterMark.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.alivc_watermark_icon));
        mWaterMark.setVisibility(View.GONE);
        addSubViewByWrap(mWaterMark);
    }

    /**
     * ??????
     */
    private void initScreenCost() {
        mScreenCostingView = new ScreenCostingView(getContext());
        mScreenCostingView.setVisibility(View.GONE);
        addSubView(mScreenCostingView);

        //????????????
        mScreenCostingView.setOnOutDLNAPlayerList(new DLNAOptionListener() {
            @Override
            public void play() {
                Log.e(TAG, "????????????play");
            }

            @Override
            public void playSuccess() {
                //???????????????,??????????????????
                mScreenCostingView.startScheduledTask();
            }

            @Override
            public void playFailed() {
                AVToast.show(getContext(), true, getResources().getString(R.string.alivc_player_play_screening_fail));
                mIsScreenCosting = false;
                if (mControlView != null) {
                    mControlView.setInScreenCosting(mIsScreenCosting);
                }
            }
        });

        //??????????????????
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

        //????????????????????????
        mScreenCostingView.setOnGetTransportInfoListener(new ScreenCostingView.OnGetTransportInfoListener() {
            @Override
            public void onGetTransportInfo(TransportInfo transportInfo) {
                if (transportInfo != null) {
                    mCurrentTransportState = transportInfo.getCurrentTransportState();
                    String currentSpeed = transportInfo.getCurrentSpeed();
                    TransportStatus currentTransportStatus = transportInfo.getCurrentTransportStatus();
                    if (mCurrentPosition == 0 && mCurrentTransportState.equals(TransportState.STOPPED)) {
                        //??????????????????
                        //1.???????????????????????????,????????????????????????
                        mScreenCostingView.stopScheduledTask();
                        //2.?????????AlivcPlayerActivity????????????
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
     * ????????????????????????
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
     * ?????????????????????
     */
    private void initNetWatchdog() {
        Context context = getContext();
        mNetWatchdog = new NetWatchdog(context);
        mNetWatchdog.setNetChangeListener(new MyNetChangeListener(this));
        mNetWatchdog.setNetConnectedListener(new MyNetConnectedListener(this));
    }

    private void onWifiTo4G() {
        //??????????????????????????????????????????????????????????????????????????????
        if (mTipsView == null || mTipsView.isErrorShow() || (GlobalPlayerConfig.IS_VIDEO && (mAdvVideoPlayerState == IPlayer.started
                || mAdvVideoPlayerState == IPlayer.paused))) {
            return;
        }

        //wifi??????4G??????????????????????????????????????????
        if (!isLocalSource()) {
            if (mIsOperatorPlay) {
                AVToast.show(getContext(), true, R.string.alivc_operator_play);
            } else {
                pause();
            }
        }

        if (!initNetWatch) {
            reload();
        }

        //???????????????????????????
        if (!isLocalSource() && mTipsView != null) {
            if (mIsOperatorPlay) {
                AVToast.show(getContext(), true, R.string.alivc_operator_play);
            } else {
                mTipsView.hideAll();
                mTipsView.showNetChangeTipView();
                //?????????????????????,???????????????????????????????????????
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
        //??????????????????????????????????????????????????????????????????????????????
        if (mTipsView == null || mTipsView.isErrorShow()) {
            return;
        }

        //???????????????????????????
        if (mTipsView != null) {
            mTipsView.hideNetErrorTipView();
        }
        if (!initNetWatch) {
            reload();
        }
        initNetWatch = false;
    }

    private void onNetDisconnected() {
        //???????????????
        // NOTE??? ????????????????????????????????????????????????????????????????????????????????????????????????????????????
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
     * ?????????????????????????????????????????????????????????????????????OrientationListener???????????????
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
     * ???????????????????????????
     *
     * @param fromPort ????????????????????????
     */
    private void changedToLandForwardScape(boolean fromPort) {
        //???????????????????????????????????????????????????????????????????????????????????????
        if (!fromPort) return;
        changeScreenMode(AliyunScreenMode.Full, false);
        if (orientationChangeListener != null) {
            orientationChangeListener.orientationChange(fromPort, mCurrentScreenMode);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param fromPort ????????????????????????
     */
    private void changedToLandReverseScape(boolean fromPort) {
        //???????????????????????????????????????????????????????????????????????????????????????
        if (!fromPort) return;
        changeScreenMode(AliyunScreenMode.Full, true);
        if (orientationChangeListener != null) {
            orientationChangeListener.orientationChange(fromPort, mCurrentScreenMode);
        }
    }

    /**
     * ????????????????????????
     *
     * @param fromLand ????????????????????????
     */
    public void changedToPortrait(boolean fromLand) {
        //??????????????????
        if (mIsFullScreenLocked) return;

        if (mCurrentScreenMode == AliyunScreenMode.Full) {
            //???????????????????????????
            if (getLockPortraitMode() == null) {
                //??????????????????????????????mode
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
     * ???????????????????????????
     */
    private void initGestureDialogManager() {
        Context context = getContext();
        if (context instanceof Activity) {
            mGestureDialogManager = new GestureDialogManager((Activity) context);
        }
    }

    /**
     * ???????????????view
     */
    private void initTipsView() {
        mTipsView = new TipsView(getContext());
        //??????tip????????????????????????
        mTipsView.setOnTipClickListener(new TipsView.OnTipClickListener() {
            @Override
            public void onContinuePlay() {
                mIsOperatorPlay = true;
                //???????????????????????????prepare??????stop??????????????????prepare
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
                // ????????????
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
                //??????
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
     * ?????????????????????????????????view????????????
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
     * ???????????????
     */
    private void initCoverView() {
        mCoverView = new ImageView(getContext());
        //???????????????????????????????????????id
        mCoverView.setId(R.id.custom_id_min);
        addSubView(mCoverView);
    }

    /**
     * ??????????????????view
     */
    private void initControlView() {
        mControlView = new ControlView(getContext());
        addSubView(mControlView);

        //????????????????????????
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
        //??????????????????seek??????
        mControlView.setOnSeekListener(new ControlView.OnSeekListener() {
            @Override
            public void onSeekEnd(int position) {
                if (mControlView != null) {
                    mControlView.setVideoPosition(position);
                }
                if (isCompleted) {
                    //????????????????????????seek???
                    inSeek = false;
                } else {

                    //????????????????????????seek
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
                //????????????
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
        //?????????????????????
        mControlView.setOnQualityBtnClickListener(new ControlView.OnQualityBtnClickListener() {

            @Override
            public void onQualityBtnClick(View v, List<TrackInfo> qualities, String currentQuality) {
                //?????????????????????
                mQualityView.setQuality(qualities, currentQuality);
                mQualityView.showAtTop(v);
            }

            @Override
            public void onHideQualityView() {
                mQualityView.hide();
            }
        });

        //??????????????????????????????????????????????????????
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
        //?????????????????????
        mControlView.setOnScreenLockClickListener(new ControlView.OnScreenLockClickListener() {
            @Override
            public void onClick() {
                lockScreen(!mIsFullScreenLocked);
            }
        });
        //????????????/????????????
        mControlView.setOnScreenModeClickListener(new ControlView.OnScreenModeClickListener() {
            @Override
            public void onClick() {
                onScreenModeClick();
            }
        });
        //?????????????????????????????????
        mControlView.setOnBackClickListener(new ControlView.OnBackClickListener() {
            @Override
            public void onClick() {
                if (mOutOnBackClickListener != null) {
                    mOutOnBackClickListener.onClick();
                } else {
                    if (mCurrentScreenMode == AliyunScreenMode.Small) {
                        //?????????????????????????????????
                        if (mOnFinishListener != null) {
                            mOnFinishListener.onFinishClick();
                        }
                    } else if (mCurrentScreenMode == AliyunScreenMode.Full) {
                        if (isLocalSource()) {
                            //??????????????????????????????,??????????????????????????????????????????,???????????????Activity
                            if (orientationChangeListener != null) {
                                orientationChangeListener.orientationChange(false, AliyunScreenMode.Small);
                            }
                        } else {
                            //?????????????????????
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

        // ?????????????????????
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

        // ??????
        mControlView.setOnScreenShotClickListener(new ControlView.OnScreenShotClickListener() {
            @Override
            public void onScreenShotClick() {
                if (!mIsFullScreenLocked) {
                    snapShot();
                }
            }
        });

        // ??????
        mControlView.setOnScreenRecoderClickListener(new ControlView.OnScreenRecoderClickListener() {
            @Override
            public void onScreenRecoderClick() {
            }
        });

        //??????????????????????????????
        mControlView.setOnInputDanmakuClickListener(new ControlView.OnInputDanmakuClickListener() {
            @Override
            public void onInputDanmakuClick() {
                showInputDanmakuClick();
                pause();
            }
        });

        //????????????
        mControlView.setOnFloatPlayViewClickListener(new ControlView.OnFloatPlayViewClickListener() {
            @Override
            public void onFloatViewPlayClick() {
                if (mOnFloatPlayViewClickListener != null) {
                    mOnFloatPlayViewClickListener.onFloatViewPlayClick();
                }
            }
        });

        //??????
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
        //????????????????????????
        mControlView.setOnDamkuOpenListener(new ControlView.OnDamkuOpenListener() {
            @Override
            public void onDamkuOpen(boolean open) {
                //????????????????????????
                if (open) {
                    showDanmakuAndMarquee();
                } else {
                    hideDanmakuAndMarquee();
                }
                mDanmakuOpen = open;
                //????????????
                if (onDamkuOpenListener != null) {
                    onDamkuOpenListener.onDamkuOpen(open);
                }
            }
        });
        //???????????????????????????
        mControlView.setOnAudioModeChangeListener(new ControlView.OnAudioModeChangeListener() {
            @Override
            public void onAudioMode(boolean audioMode) {
                onChangeMediaMode(audioMode);
            }
        });
        //????????????
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

        //ControlView????????????
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
                //??????
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
            //???????????????
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
     * ???????????????????????? View
     */
    public void showLongPressView(String key) {
        if (enableShowLongPressVideoSpeedView(key)) {
            mLongPressVideoSpeedView.setVisibility(View.VISIBLE);
            mLongPressVideoSpeedView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLongPressVideoSpeedView.setVisibility(GONE);
                }
            }, 3000L);
            mLongPressSP.edit().putBoolean(key, false).apply();
        }
    }

    //????????????/????????????
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
     * ????????????
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
     * ??????????????????????????? View
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
                //????????????????????????
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
            //???????????????
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
     * ????????????????????????
     */
    private void initQualityView() {
        mQualityView = new QualityView(getContext());
        addSubView(mQualityView);
        //?????????????????????
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
     * ???????????????view
     */
    private void initGuideView() {
        mGuideView = new GuideView(getContext());
        addSubView(mGuideView);
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    private void switchPlayerState() {
        //????????????????????????
        if (mIsScreenCosting && mScreenCostingView != null && mControlView != null) {
            if (mCurrentTransportState == TransportState.PLAYING) {
                mScreenCostingView.pause();
                mControlView.updateScreenCostPlayStateBtn(true);
            } else {
                mScreenCostingView.play((int) mCurrentPosition);
                mControlView.updateScreenCostPlayStateBtn(false);
            }
        } else {
            //???????????????????????????
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
     * ???????????????view
     */
    private void initGestureView() {
        mGestureView = new GestureView(getContext());
        addSubView(mGestureView);
        mGestureView.setMultiWindow(mIsInMultiWindow);

        //??????????????????
        mGestureView.setOnGestureListener(new GestureView.GestureListener() {

            @Override
            public void onHorizontalDistance(float downX, float nowX) {
            }

            @Override
            public void onLeftVerticalDistance(float downY, float nowY) {
                //??????????????????????????????
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
                //??????????????????????????????
                float volume = mAliyunRenderView.getVolume();
                int changePercent = (int) ((nowY - downY) * 100 / getHeight());
                if (mGestureDialogManager != null) {
                    mGestureDialogManager.showVolumeDialog(AliyunVodPlayerView.this, volume * 100);
                    float targetVolume = mGestureDialogManager.updateVolumeDialog(changePercent);
                    currentVolume = targetVolume;
                    //????????????????????????,?????????????????????????????????,???????????????????????????
                    mAliyunRenderView.setVolume((targetVolume / 100.00f));//???????????????????????????
                }
            }

            @Override
            public void onGestureEnd() {
                Log.i(TAG, "onGestureEnd");
                //???????????????
                //seek???????????????????????????
                if (mGestureDialogManager != null) {
                    int seekPosition = mControlView.getVideoPosition();
                    if (seekPosition >= mAliyunRenderView.getDuration()) {
                        seekPosition = (int) (mAliyunRenderView.getDuration() - 1000);
                    }
                    if (seekPosition <= 0) {
                        seekPosition = 0;
                    }
                    //???????????????????????????
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
                        //????????????????????????????????????
                        mControlView.openAutoHide();
                    }

                    mGestureDialogManager.dismissBrightnessDialog();
                    mGestureDialogManager.dismissVolumeDialog();
                }
                if (mLongPressSpeed) {
                    mAliyunRenderView.setSpeed(mRecordPlaySpeed);
                    mLongPressSpeed = false;
                    if (mControlView != null) {
                        mControlView.setSpeedViewText(mRecordPlaySpeed);
                        mControlView.showVideoSpeedTipLayout(false);
                    }
                }
            }

            @Override
            public void onSingleTap() {
                //??????????????????????????????
                if (mControlView != null) {
                    //????????????????????????????????????
                    if (mAdvVideoPlayerState == IPlayer.started && (GlobalPlayerConfig.IS_VIDEO)) {
                        openAdvertisement();
                    } else if (mIsScreenCosting) {
                        //??????????????????ControlView???????????????
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
                //?????????????????????????????????
                if (mIsScreenCosting || (GlobalPlayerConfig.IS_TRAILER && mCurrentPosition >= TRAILER)) {
                    //????????????????????????????????????????????????????????????
                } else if (GlobalPlayerConfig.IS_TRAILER && mAdvVideoPlayerState == IPlayer.started) {
                    //?????????????????????,??????????????????????????????,????????????????????????
                } else {
                    switchPlayerState();
                }
            }

            @Override
            public void onLongPress() {
                //??????????????????
                handleLongPress();
            }
        });
    }

    /**
     * ????????????????????????
     */
    private void initAdvVideoView() {
        mAdvVideoView = new AdvVideoView(getContext());
        addSubView(mAdvVideoView);

        //??????????????????
        mAdvVideoView.setOutPreparedListener(new VideoPlayerPreparedListener(this, true));
        //loading??????????????????
        mAdvVideoView.setOutOnLoadingStatusListener(new VideoPlayerLoadingStatusListener(this, true));
        //??????????????????
        mAdvVideoView.setOutOnStateChangedListener(new VideoPlayerStateChangedListener(this, true));
        //????????????
        mAdvVideoView.setOutOnCompletionListener(new VideoPlayerCompletionListener(this, true));
        //??????info????????????
        mAdvVideoView.setOutOnInfoListener(new VideoPlayerInfoListener(this, true));
        //??????????????????
        mAdvVideoView.setOutOnErrorListener(new VideoPlayerErrorListener(this, true));
        //??????renderingStart??????
        mAdvVideoView.setOutOnRenderingStartListener(new VideoPlayerRenderingStartListener(this, true));
        //????????????????????????????????????
        mAdvVideoView.setOnBackImageViewClickListener(new VideoPlayerAdvBackImageViewListener(this));

    }

    /**
     * ??????????????????
     */
    private void initAliVcPlayer(IRenderView renderView, ListPlayManager listPlayer) {
        mAliyunRenderView = new AliyunRenderView(getContext());
        addSubView(mAliyunRenderView);
        mAliyunRenderView.setSurfaceView(renderView);
        mAliyunRenderView.setListPlayer(listPlayer);
        //??????????????????
        mAliyunRenderView.setOnPreparedListener(new VideoPlayerPreparedListener(this, false));
        //?????????????????????
        mAliyunRenderView.setOnErrorListener(new VideoPlayerErrorListener(this, false));
        //?????????????????????
        mAliyunRenderView.setOnLoadingStatusListener(new VideoPlayerLoadingStatusListener(this, false));
        //???????????????
        mAliyunRenderView.setOnStateChangedListener(new VideoPlayerStateChangedListener(this, false));
        //????????????
        mAliyunRenderView.setOnCompletionListener(new VideoPlayerCompletionListener(this, false));
        //??????????????????
        mAliyunRenderView.setOnInfoListener(new VideoPlayerInfoListener(this, false));
        //???????????????
        mAliyunRenderView.setOnRenderingStartListener(new VideoPlayerRenderingStartListener(this, false));
        //trackChange??????
        mAliyunRenderView.setOnTrackChangedListener(new VideoPlayerTrackChangedListener(this));
        //?????????????????????
        mAliyunRenderView.setOnSubtitleDisplayListener(new VideoPlayerSubtitleDeisplayListener(this));
        //seek????????????
        mAliyunRenderView.setOnSeekCompleteListener(new VideoPlayerOnSeekCompleteListener(this));
        //??????????????????
        mAliyunRenderView.setOnSnapShotListener(new VideoPlayerOnSnapShotListener(this));
        //sei????????????
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
        //?????????????????????
        mAliyunRenderView.setOnErrorListener(null);
        //?????????????????????
        mAliyunRenderView.setOnLoadingStatusListener(null);
        //???????????????
        mAliyunRenderView.setOnStateChangedListener(null);
        //????????????
        mAliyunRenderView.setOnCompletionListener(null);
        //??????????????????
        mAliyunRenderView.setOnInfoListener(null);
        //???????????????
        mAliyunRenderView.setOnRenderingStartListener(null);
        //trackChange??????
        mAliyunRenderView.setOnTrackChangedListener(null);
        //?????????????????????
        mAliyunRenderView.setOnSubtitleDisplayListener(null);
        //seek????????????
        mAliyunRenderView.setOnSeekCompleteListener(null);
        //??????????????????
        mAliyunRenderView.setOnSnapShotListener(null);
        //sei????????????
        mAliyunRenderView.setOnSeiDataListener(null);
        mAliyunRenderView.setOnVerifyTimeExpireCallback(null);
    }

    /**
     * ???????????????
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
        //????????????
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
     * ????????????????????????????????????
     */
    private void startAdvVideo() {
        if (GlobalPlayerConfig.IS_TRAILER) {
            //???????????????,??????seek?????????????????????????????????,????????????????????????
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
     * ?????????????????????????????? ??? ?????????????????????????????????????????????????????????????????? ????????????????????????????????????????????????
     *
     * @param title ????????????????????????
     * @return ???????????????
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
     * ?????????????????????????????? ??? ?????????????????????????????????????????????????????????????????? ????????????????????????????????????????????????
     *
     * @param postUrl ????????????????????????
     * @return ???????????????
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
     * ??????????????????????????????
     *
     * @param isInMultiWindow true,??????????????????,false?????????????????????
     */
    public void setMultiWindow(boolean isInMultiWindow) {
        this.mIsInMultiWindow = isInMultiWindow;
        if (mGestureView != null) {
            mGestureView.setMultiWindow(mIsInMultiWindow);
        }
    }

    /**
     * ????????????????????????seek
     */
    private void isAutoAccurate(long position) {
        if (GlobalPlayerConfig.PlayConfig.mEnableAccurateSeekModule) {
            mAliyunRenderView.seekTo(position, IPlayer.SeekMode.Accurate);
        } else {
            mAliyunRenderView.seekTo(position, IPlayer.SeekMode.Inaccurate);
        }
    }

    /**
     * ???????????????????????????
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
     * ??????????????????
     *
     * @return ????????????
     */
    public int getDuration() {
        if (mAliyunRenderView != null) {
            return (int) mAliyunRenderView.getDuration();
        }

        return 0;
    }

    /**
     * ??????????????????
     *
     * @param errorCode  ?????????
     * @param errorEvent ????????????
     * @param errorMsg   ????????????
     */
    public void showErrorTipView(int errorCode, String errorEvent, String errorMsg) {
        stop();
        if (mControlView != null) {
            mControlView.setPlayState(ControlView.PlayState.NotPlaying);
        }

        if (mTipsView != null) {
            //?????????????????????,???????????????????????????????????????
            mGestureView.hide(ViewAction.HideType.End);
            mControlView.hide(ViewAction.HideType.End);
            mCoverView.setVisibility(GONE);
            mTipsView.showErrorTipView(errorCode, errorEvent, errorMsg);
            mTrailersView.hideAll();
        }
    }

    public void hideErrorTipView() {

        if (mTipsView != null) {
            //?????????????????????,???????????????????????????????????????
            mTipsView.hideErrorTipView();
        }
    }

    /**
     * ???????????????????????????
     */
    private void requestBitmapByPosition(int targetPosition) {
        if (mThumbnailHelper != null && mThumbnailPrepareSuccess) {
            mThumbnailHelper.requestBitmapAtPosition(targetPosition);
        }
    }

    /**
     * ???????????????
     */
    private void hideThumbnailView() {
        if (mThumbnailView != null) {
            mThumbnailView.hideThumbnailView();
        }
    }

    /**
     * ???????????????
     */
    private void showThumbnailView() {
        if (mThumbnailView != null && !mIsAudioMode) {
            mThumbnailView.showThumbnailView();
            //??????????????????????????????????????????
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
     * ????????????????????????
     *
     * @param duration        ???????????????
     * @param currentPosition ??????????????????
     * @param deltaPosition   ??????????????????????????????
     */
    public int getTargetPosition(long duration, long currentPosition, long deltaPosition) {
        // seek??????
        long finalDeltaPosition;
        // ???????????????????????????seek??????
        long totalMinutes = duration / 1000 / 60;
        int hours = (int) (totalMinutes / 60);
        int minutes = (int) (totalMinutes % 60);

        // ???????????????1?????????????????????????????????????????????????????????????????????????????????
        if (hours >= 1) {
            finalDeltaPosition = deltaPosition / 10;
        }// ???????????????31?????????60???????????????????????????????????????????????????????????????????????????
        else if (minutes > 30) {
            finalDeltaPosition = deltaPosition / 5;
        }// ???????????????11?????????30???????????????????????????????????????????????????????????????????????????
        else if (minutes > 10) {
            finalDeltaPosition = deltaPosition / 3;
        }// ???????????????4-10???????????????????????????????????????????????????????????????????????????
        else if (minutes > 3) {
            finalDeltaPosition = deltaPosition / 2;
        }// ???????????????1?????????3???????????????????????????????????????????????????????????????
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
     * addSubView ?????????view????????????
     *
     * @param view ???view
     */
    private void addSubView(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //??????????????????
        addView(view, params);
    }

    /**
     * ?????????View????????????,?????????View?????????
     *
     * @param view            ???????????????View
     * @param belowTargetView ?????????View?????????
     */
    private void addSubViewBelow(final View view, final View belowTargetView) {
        belowTargetView.post(new Runnable() {
            @Override
            public void run() {
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                params.topMargin = belowTargetView.getMeasuredHeight();
                //??????????????????
                addView(view, params);
            }
        });
    }

    /**
     * ?????????View???????????????
     */
    private void addSubViewByCenter(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(view, params);
    }

    /**
     * ?????????View????????????,??????????????? WRAP_CONTENT
     *
     * @param view ???view
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
        //??????????????????
        addView(view, params);
    }

    /**
     * @param view
     * @param height
     */
    private void addSubViewExactlyHeight(View view, int height, int verb) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        params.addRule(verb);
        //??????????????????
        addView(view, params);
    }

    /**
     * ???????????????View
     *
     * @param view ???view
     */
    private void addSubViewByBottom(View view) {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //??????????????????
        addView(view, params);
    }

    /**
     * ?????????View
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
     * ??????????????????????????????????????????
     *
     * @param targetMode {@link AliyunScreenMode}
     */
    public void changeScreenMode(AliyunScreenMode targetMode, boolean isReverse) {
        AliyunScreenMode finalScreenMode = targetMode;

        if (mIsFullScreenLocked) {
            finalScreenMode = AliyunScreenMode.Full;
        }

        //???????????????????????????????????????
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
                    //???????????????????????????
                    if (isReverse) {
                        ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    } else {
                        ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }

                    //SCREEN_ORIENTATION_LANDSCAPE??????????????????????????????
                } else {
                    //??????????????????????????????????????????view??????????????????
                    ViewGroup.LayoutParams aliVcVideoViewLayoutParams = getLayoutParams();
                    aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                showDanmakuAndMarquee();
            } else if (finalScreenMode == AliyunScreenMode.Small) {

                if (getLockPortraitMode() == null) {
                    //???????????????????????????
                    ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    //??????????????????????????????????????????view??????????????????
                    ViewGroup.LayoutParams aliVcVideoViewLayoutParams = getLayoutParams();
                    aliVcVideoViewLayoutParams.height = (int) (ScreenUtils.getWidth(context) * 9.0f / 16);
                    aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param screenMode ??????????????????
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
     * ??????????????????????????????????????????
     *
     * @return ??????????????????
     */
    public AliyunScreenMode getScreenMode() {
        return mCurrentScreenMode;
    }

    /**
     * ????????????????????????
     *
     * @param onPreparedListener ????????????
     */
    public void setOnPreparedListener(IPlayer.OnPreparedListener onPreparedListener) {
        mOutPreparedListener = onPreparedListener;
    }

    /**
     * ????????????????????????
     *
     * @param onErrorListener ??????????????????
     */
    public void setOnErrorListener(IPlayer.OnErrorListener onErrorListener) {
        mOutErrorListener = onErrorListener;
    }

    /**
     * ????????????????????????
     *
     * @param onInfoListener ??????????????????
     */
    public void setOnInfoListener(IPlayer.OnInfoListener onInfoListener) {
        mOutInfoListener = onInfoListener;
    }

    /**
     * ??????????????????????????????
     *
     * @param onCompletionListener ????????????????????????
     */
    public void setOnCompletionListener(IPlayer.OnCompletionListener onCompletionListener) {
        mOutCompletionListener = onCompletionListener;
    }

    /**
     * ??????????????????????????????
     */
    public void setOnScreenCostingSingleTagListener(OnScreenCostingSingleTagListener listener) {
        this.mOnScreenCostingSingleTagListener = listener;
    }

    /**
     * ??????????????????????????????
     *
     * @param l ????????????????????????
     */
    public void setOnAutoPlayListener(OnAutoPlayListener l) {
        mOutAutoPlayListener = l;
    }

    public interface OnTimeExpiredErrorListener {
        void onTimeExpiredError();
    }

    /**
     * ?????????????????????
     *
     * @param l ???????????????
     */
    public void setOnTimeExpiredErrorListener(OnTimeExpiredErrorListener l) {
        mOutTimeExpiredErrorListener = l;
    }

    /**
     * ?????????,????????????????????????
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
     * ??????????????????????????????
     *
     * @param onFirstFrameStartListener ????????????????????????
     */
    public void setOnFirstFrameStartListener(IPlayer.OnRenderingStartListener onFirstFrameStartListener) {
        mOutFirstFrameStartListener = onFirstFrameStartListener;
    }

    /**
     * ??????seek????????????
     *
     * @param onSeekCompleteListener seek????????????
     */
    public void setOnSeekCompleteListener(IPlayer.OnSeekCompleteListener onSeekCompleteListener) {
        mOuterSeekCompleteListener = onSeekCompleteListener;
    }

    /**
     * ?????????????????????
     */
    public void setOnTrackChangedListener(IPlayer.OnTrackChangedListener listener) {
        this.mOutOnTrackChangedListener = listener;
    }

    /**
     * ????????????????????????
     *
     * @param onStoppedListener ??????????????????
     */
    public void setOnStoppedListener(OnStoppedListener onStoppedListener) {
        this.mOnStoppedListener = onStoppedListener;
    }

    /**
     * ????????????????????????
     *
     * @param onLoadingListener ??????????????????
     */
    public void setOnLoadingListener(IPlayer.OnLoadingStatusListener onLoadingListener) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setOnLoadingStatusListener(onLoadingListener);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param onVideoSizeChangedListener ????????????????????????
     */
    public void setOnVideoSizeChangedListener(IPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        }
    }


    /**
     * ??????????????????????????????
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
     * ??????????????????????????????
     */
    private void showVideoFunction(boolean refresh) {
        //????????? refresh ??????????????????????????????
        if (mAliyunRenderView != null && refresh) {
            mPlayerState = IPlayer.stopped;
            mAliyunRenderView.stop();
        }
        if (mAdvVideoView != null && refresh) {
            mAdvVideoView.optionStop();
            mAdvVideoView.isShowAdvVideoBackIamgeView(false);
            mAdvVideoView.isShowAdvVideoTipsTextView(false);
        }
        //??????
        if (GlobalPlayerConfig.IS_WATERMARK) {
            mWaterMark.setVisibility(View.VISIBLE);
        } else {
            mWaterMark.setVisibility(View.GONE);
        }

        //??????????????????,???????????????????????????,????????????????????????,?????????????????????,????????????????????????
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
        //????????????
        if (!GlobalPlayerConfig.IS_VIDEO) {
            if (mControlView != null) {
                mControlView.showNativeSeekBar();
            }
        }

        //???????????????????????????,?????????4g??????
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
     * ????????????????????????
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
            //?????????????????????,????????????????????????
            mAdvVideoView.setAutoPlay(!mIsScreenCosting);
            mAdvVideoView.optionPrepare();
        }
    }

    public void showNetLoadingTipView() {
        if (mTipsView != null && !mIsAudioMode) {
            mTipsView.showNetLoadingTipView();
        }
    }

    public void hideNetLoadingTipView() {
        if (mTipsView != null) {
            mTipsView.hideNetLoadingTipView();
        }
    }


    /**
     * ???VodPlayer ???????????????????????????,??????onStop ???????????????,
     * ?????????????????????????????????
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
     * ???????????????????????????
     *
     * @param isOperatorPlay true???????????????,false??????tips
     */
    public void setOperatorPlay(boolean isOperatorPlay) {
        this.mIsOperatorPlay = isOperatorPlay;
    }

    /**
     * ???activity??????onResume?????????????????? ??????home???????????????????????????????????????
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

        //onStop????????????????????????????????????????????????
        if (!mIsScreenCosting) {
            //??????????????????????????????
            resumePlayerState();
        }

        if (PlayServiceHelper.mServiceStart && activityResume) {
            PlayServiceHelper.stopService(getContext());
        }
    }


    /**
     * ??????????????????
     */
    public void startNetWatch() {
        initNetWatch = true;
        if (mNetWatchdog != null) {
            mNetWatchdog.startWatch();
        }
    }

    /**
     * ??????????????????
     */
    public void stopNetWatch() {
        if (mNetWatchdog != null) {
            mNetWatchdog.stopWatch();
        }
    }


    /**
     * ????????????????????????
     */
    public void onStop() {
        clearAllListener();
        if (mOrientationWatchDog != null) {
            mOrientationWatchDog.stopWatch();
        }
        //??????????????????????????????resume???????????????
        savePlayerState();
    }

    /**
     * ????????????????????????
     */
    public void startOrientationWatchDog() {
        if (mOrientationWatchDog != null) {
            mOrientationWatchDog.startWatch();
        }
    }

    /**
     * ????????????????????????
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
        //?????????????????????????????????,?????? Activity ??? fragment ??? pause ????????????????????????
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
     * Activity?????????????????????????????????
     */
    private void resumePlayerState() {
        if (mAliyunRenderView == null || !mPlayingBeforePause) {
            return;
        }

        //?????????????????????,???????????????????????????????????????????????????,?????????????????????????????????????????????
        if (mAdvVideoView != null && GlobalPlayerConfig.IS_VIDEO) {
            /** ????????????mCurrentPosition == 0 ?????????????????????prepare????????????????????????,???????????????????????????????????????prepare??????,??????paused??????,
             *  ???????????????????????????????????????*/
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
            //?????????????????????????????????,????????????????????????????????????
            if (!isLocalSource() && NetWatchdog.is4GConnected(getContext()) && !mIsOperatorPlay && isPlaying()) {
                pause();
            } else {
                if (mSourceDuration <= 0 && mPlayerState == IPlayer.stopped) {
                    //??????
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
     * ???????????????????????????????????????
     */
    private void savePlayerState() {
        if (mAliyunRenderView == null) {
            return;
        }
        if (mSourceDuration <= 0) {
            //????????????stop
            mPlayerState = IPlayer.stopped;
            mAliyunRenderView.stop();
        } else {
            pause();
        }
    }

    /**
     * ???????????????????????????????????????????????????
     */
    private boolean advStyleIsIncludeEnd() {
        return mAdvPosition == MutiSeekBarView.AdvPosition.ALL || mAdvPosition == MutiSeekBarView.AdvPosition.ONLY_END
                || mAdvPosition == MutiSeekBarView.AdvPosition.START_END || mAdvPosition == MutiSeekBarView.AdvPosition.MIDDLE_END;
    }

    /**
     * ??????????????????
     *
     * @return ????????????
     */
    public MediaInfo getMediaInfo() {
        if (mAliyunRenderView != null) {
            return mAliyunRenderView.getMediaInfo();
        }

        return null;
    }

    /**
     * ??????????????????4g??????,?????????????????????
     * 1.??????????????????
     * 2.???????????????4G
     * ??????????????????????????????4G??????
     *
     * @return true:????????????,false?????????
     */
    private boolean show4gTips() {
        //?????????????????????????????????
        if (isLocalSource()) {
            return false;
        } else {
            //??????????????????
            if (NetWatchdog.is4GConnected(getContext())) {
                if (mIsOperatorPlay) {
                    //?????????????????????,???Toast?????????,????????????
                    AVToast.show(getContext(), true, R.string.alivc_operator_play);
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
     * ???????????????4g????????????
     */
    private void advVideoPlayer4gTips() {
        if (!show4gTips()) {
            mAliyunRenderView.start();
            //???????????????????????????,??????prepare????????????,??????????????????
            mAdvVideoView.setAutoPlay(false);
            mAdvVideoView.optionPrepare();
        }

    }

    /**
     * ?????????????????????????????????
     */
    private void afterAdvVideoPlayerComplete() {
        //???????????????????????????????????????????????????
        mAdvTotalPosition += mAdvCurrentPosition;
        if (mAliyunRenderView != null && mSurfaceView != null) {
            mSurfaceView.setVisibility(View.VISIBLE);
            if (mAdvVideoView != null) {
                mAdvVideoView.setSurfaceViewVisibility(View.GONE);
            }
            if (needToSeek) {
                //?????????MIDDLE???????????????seekTo?????????
                if (mAdvVideoCount < 3) {
                    isAutoAccurate(mSeekToPosition - mAdvDuration * 2);
                    advVideoPlayer4gTips();
                }
            } else {
                if (mCurrentIntentPlayVideo == AdvVideoView.IntentPlayVideo.MIDDLE_END_ADV_SEEK && mAdvVideoCount < 3) {
                    //?????????MIDDLE?????????????????????END??????
                    if (mAliyunRenderView != null) {
                        isAutoAccurate(mSourceDuration);
                        mAliyunRenderView.pause();
                    }
                    if (mControlView != null) {
                        /*
                            ????????????????????????,seek???sourceDuration / 2??????????????????????????????????????????????????????,????????????????????????????????????????????????????????????????????????
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

        //????????????????????????????????????????????????????????????????????????,???????????????????????????????????????????????????????????????????????????????????????????????????,???????????????????????????
        if (advStyleIsIncludeEnd() && (mAdvVideoCount == 3)) {
            //??????????????????
            if (mOutCompletionListener != null) {
                mOutCompletionListener.onCompletion();
            }
        }
    }


    /**
     * ?????????????????????
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
     * ????????????????????????
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
     * ????????????????????????
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
     * ???????????????????????????,???????????????????????????URL?????????????????????
     */
    public void needOnlyFullScreenPlay(boolean isNeed) {
        mIsNeedOnlyFullScreen = isNeed;
    }

    /**
     * ????????????
     */
    public void hideDanmakuView() {
        if (mDanmakuView != null) {
            mDanmakuView.hideAndPauseDrawTask();
            mDanmakuView.setVisibility(View.GONE);
        }
    }

    /**
     * ???????????????????????????start??????pause???
     *
     * @return ????????????????????????
     */
    public boolean isPlaying() {
        return mPlayerState == IPlayer.started;
    }

    /**
     * ?????????????????????
     *
     * @return ???????????????
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
     * ????????????
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
            //??????
            mAliyunRenderView.prepare();
        } else {
            if (mPlayerState == IPlayer.paused) {
                mAliyunRenderView.start();
            } else if (mIsContinuedPlay && resume) {
                //???????????????,????????? resume ?????????????????? prepare ??????
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
     * ????????????
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
                //????????????stop
                mPlayerState = IPlayer.stopped;
                mAliyunRenderView.stop();
            } else {
                mAliyunRenderView.pause();
                mPlayerState = IPlayer.paused;
            }


            //???vip????????????vip???????????????????????????????????????????????????????????????????????????????????????????????????????????????

            if (mMarqueeView != null) {
                mMarqueeView.pause();
            }
            if (mDanmakuOpen && mDanmakuView != null) {
                mDanmakuView.pause();
            }
        }
    }

    /**
     * ????????????
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
     * ?????????????????????????????????
     */
    public void clearFrameWhenStop(boolean clearFrameWhenStop) {
        if (mAliyunRenderView != null) {
            PlayerConfig config = mAliyunRenderView.getPlayerConfig();
            config.mClearFrameWhenStop = clearFrameWhenStop;
            mAliyunRenderView.setPlayerConfig(config);
        }
    }

    /**
     * ??????replay
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
                //?????????????????????,???????????????????????????????????????
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
     * seek??????
     *
     * @param position ????????????
     */
    public void seekTo(int position) {
        mSeekToPosition = position;
        if (mAliyunRenderView == null) {
            return;
        }
        inSeek = true;
        //?????????????????????????????????????????????????????????????????????????????????view???????????????????????????

        if (GlobalPlayerConfig.IS_VIDEO) {
            //????????????
            checkAdvVideoSeek(position);
        } else {
            mSourceSeekToPosition = position;
            realySeekToFunction(position);
        }
    }

    private void realySeekToFunction(int position) {
        /** ?????????????????????????????????seekEnd?????????progress?????????????????????????????????,????????????seek,????????????????????????seek??????,??????????????????????????????????????? */
        if (GlobalPlayerConfig.IS_VIDEO) {
            isAutoAccurate(position - mAdvVideoCount * mAdvDuration);
        } else {
            isAutoAccurate(position);
        }
    }

    /**
     * ??????????????????seek?????????
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
                        //??????mControlView???????????????
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
                        //??????mControlView???????????????
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
     * ????????????????????????
     *
     * @param listener ?????????
     */
    public void setLockPortraitMode(LockPortraitListener listener) {
        mLockPortraitListener = listener;
    }

    /**
     * ????????????
     *
     * @return ???????????????
     */
    public LockPortraitListener getLockPortraitMode() {
        return mLockPortraitListener;
    }

    /**
     * ???home?????????
     *
     * @param keyCode ??????
     * @param event   ??????
     * @return ???????????????
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
     * ??????????????????listener
     */
    public interface OnPlayStateBtnClickListener {
        void onPlayBtnClick(int playerState);
    }

    /**
     * ??????????????????????????????
     */
    public void setOnPlayStateBtnClickListener(OnPlayStateBtnClickListener listener) {
        this.onPlayStateBtnClickListener = listener;
    }

    private OnSeekStartListener onSeekStartListener;

    /**
     * seek????????????
     */

    public interface OnSeekStartListener {
        void onSeekStart(int position);
    }

    public void setOnSeekStartListener(OnSeekStartListener listener) {
        this.onSeekStartListener = listener;
    }

    /**
     * ??????????????????????????????
     */
    public interface OnOrientationChangeListener {
        /**
         * ??????????????????
         *
         * @param from        ????????????????????????, ????????????????????????
         * @param currentMode ??????????????????
         */
        void orientationChange(boolean from, AliyunScreenMode currentMode);
    }

    private OnOrientationChangeListener orientationChangeListener;

    public void setOrientationChangeListener(
            OnOrientationChangeListener listener) {
        this.orientationChangeListener = listener;
    }

    /**
     * ??????/????????????
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
     * ??????????????????????????????
     */
    public interface NetConnectedListener {
        /**
         * ???????????????
         */
        void onReNetConnected(boolean isReconnect);

        /**
         * ???????????????
         */
        void onNetUnConnected();
    }

    public interface OnFinishListener {
        void onFinishClick();
    }

    /**
     * ????????????
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
     * ????????????????????????????????????????????????????????????
     */
    public void setOnTrackInfoClickListener(ControlView.OnTrackInfoClickListener listener) {
        this.mOutOnTrackInfoClickListener = listener;
    }

    public void setOnBackClickListener(ControlView.OnBackClickListener listener) {
        this.mOutOnBackClickListener = listener;
    }

    /**
     * ?????????????????????
     */
    public interface OnSoftKeyHideListener {
        void softKeyHide();

        //????????????
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


    /** ------------------- ??????????????? --------------------------- */

    /**
     * ?????????????????????????????????????????????
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
                    //????????????????????????
                    aliyunVodPlayerView.advVideoPlayerPrepared();
                } else {
                    //?????????????????????
                    aliyunVodPlayerView.sourceVideoPlayerPrepared();
                }
            }
        }
    }

    /**
     * ????????????????????????
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

        /** ????????????????????????handler,???????????????????????????????????????handler???????????????,
         * ?????????????????????,?????????????????????????????????????????????,?????????????????????????????? */
        if (mAdvVideoCount == 0) {
            Message msg = Message.obtain();
            msg.what = ADV_VIDEO_PREPARED;
            msg.obj = mAdvVideoMediaInfo;
            mVodPlayerHandler.sendMessage(msg);
        }
    }

    /**
     * ?????????????????????
     */
    private void sourceVideoPlayerPrepared() {
        //?????????mThumbnailPrepareSuccess??????,???????????????????????????????????????
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
        //???????????????????????????????????????
        mSourceDuration = mAliyunRenderView.getDuration();
        mAliyunMediaInfo.setDuration((int) mSourceDuration);
        //?????????
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

        //???????????????????????????
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
        //????????????????????????????????????????????????????????????view,????????????????????????????????????????????????????????????
        if (GlobalPlayerConfig.IS_VIDEO) {
            //?????????????????????
            if (!mIsVipRetry) {
                mSurfaceView.setVisibility(View.GONE);
            }

            Message msg = Message.obtain();
            msg.what = SOURCE_VIDEO_PREPARED;
            msg.obj = mAliyunMediaInfo;
            mVodPlayerHandler.sendMessage(msg);

            return;
        } else if (GlobalPlayerConfig.IS_TRAILER) {
            //??????
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

        //??????????????????????????????start??????????????????
        if (mOutPreparedListener != null) {
            mOutPreparedListener.onPrepared();
        }
        mIsVipRetry = false;
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????
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
     * ????????????????????????
     */
    private void advVideoPlayerError(ErrorInfo errorInfo) {
        if (mTipsView != null) {
            mTipsView.hideAll();
        }
        //????????????????????????????????????????????????????????????????????????
        lockScreen(false);

        showErrorTipView(errorInfo.getCode().getValue(), Integer.toHexString(errorInfo.getCode().getValue()), errorInfo.getMsg());

        if (mOutErrorListener != null) {
            mOutErrorListener.onError(errorInfo);
        }
    }

    /**
     * ?????????????????????
     */
    private void sourceVideoPlayerError(ErrorInfo errorInfo) {
        if (mTipsView != null) {
            mTipsView.hideAll();
        }
        //????????????????????????????????????????????????????????????????????????
        lockScreen(false);

        //errorInfo.getExtra()?????????null,???????????????errorInfo.getCode?????????????????????
        showErrorTipView(errorInfo.getCode().getValue(), Integer.toHexString(errorInfo.getCode().getValue()), errorInfo.getMsg());


        if (mOutErrorListener != null) {
            mOutErrorListener.onError(errorInfo);
        }
    }

    /**
     * ???????????????????????????
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
     * ????????????????????????
     */
    private void advVideoPlayerLoadingBegin() {
        if (mTipsView != null) {
            mTipsView.showBufferLoadingTipView();
        }
    }

    /**
     * ?????????????????????
     */
    private void sourceVideoPlayerLoadingBegin() {
        if (mIsAudioMode)
            return;
        if (mTipsView != null) {
            //????????????,?????????????????????????????????,????????????loading
            if (GlobalPlayerConfig.IS_VIDEO && mAdvVideoPlayerState == IPlayer.started) {
            } else {
                mTipsView.hideNetLoadingTipView();
                mTipsView.showBufferLoadingTipView();
            }
        }
    }

    /**
     * ??????????????????????????????
     */
    private void advVideoPlayerLoadingProgress(int percent) {
        if (mTipsView != null) {
            mTipsView.updateLoadingPercent(percent);
        }
    }

    /**
     * ???????????????????????????
     */
    private void sourceVideoPlayerLoadingProgress(int percent) {

        if (mIsAudioMode)
            return;
        if (mTipsView != null) {
            //????????????,?????????????????????????????????,????????????loading
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
     * ????????????????????????
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
     * ?????????????????????
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
     * ???????????????????????????
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
     * ????????????????????????
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
            //????????????????????????,??????????????????
            if (mAliyunRenderView != null) {
                mAliyunRenderView.pause();
            }
        }
    }

    /**
     * ???????????????????????????
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
     * ???????????????????????????
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
     * ????????????????????????
     */
    private void advVideoPlayerCompletion() {
        //?????????????????????????????????????????????,????????????5?????????view
        if (GlobalPlayerConfig.IS_TRAILER && mTrailersView != null) {
            mTrailersView.trailerPlayTipsIsShow(true);
        }

        showDanmakuAndMarquee();
        mAdvVideoCount++;
        inSeek = false;

        //???????????????????????????????????????????????????
        afterAdvVideoPlayerComplete();
    }

    /**
     * ?????????????????????
     */
    private void sourceVideoPlayerCompletion() {
        inSeek = false;

        if (mOutCompletionListener != null) {
            //?????????????????????,?????????????????????????????????????????????
            if (GlobalPlayerConfig.IS_VIDEO && advStyleIsIncludeEnd()) {
                //?????????????????????????????????,????????????????????????????????????????????????
                //?????????????????????,???????????????????????????????????????????????????,?????????????????????
                if (GlobalPlayerConfig.IS_TRAILER && mCurrentPosition < TRAILER * 1000) {
                    startAdvVideo();
                } else {
                    //??????,???????????????,?????????????????????,????????????,???????????????
                    if (GlobalPlayerConfig.IS_TRAILER) {
                        if (mTrailersView != null && mCurrentPosition >= (TRAILER * 1000)) {
                            mTrailersView.trailerPlayTipsIsShow(false);
                        }
                    } else {
                        mOutCompletionListener.onCompletion();
                    }
                }
            } else {
                //??????????????????,?????????,??????????????????????????????????????????????????????,?????????VIP??????View
                if (GlobalPlayerConfig.IS_TRAILER && mTrailersView != null && mCurrentPosition >= (TRAILER * 1000)) {
                    mTrailersView.trailerPlayTipsIsShow(false);
                } else {
                    //??????????????????
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
     * ?????????Info??????
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
     * ????????????Info
     */
    private void advVideoPlayerInfo(InfoBean infoBean) {
        //?????????????????????
        if (infoBean.getCode().getValue() == TrackInfo.Type.TYPE_VOD.ordinal()) {
            //??????????????????????????????
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
     * ?????????Info
     */
    private void sourceVideoPlayerInfo(InfoBean infoBean) {
        if (infoBean.getCode() == InfoCode.AutoPlayStart) {
            //??????????????????,????????????????????????
            if (mControlView != null) {
                mControlView.setPlayState(ControlView.PlayState.Playing);
            }
            if (mOutAutoPlayListener != null) {
                mOutAutoPlayListener.onAutoPlayStarted();
            }
        } else if (infoBean.getCode() == InfoCode.BufferedPosition) {
            //??????bufferedPosition
            mVideoBufferedPosition = infoBean.getExtraValue();
            mControlView.setVideoBufferPosition((int) mVideoBufferedPosition);
        } else if (infoBean.getCode() == InfoCode.CurrentPosition) {
            //??????currentPosition
            mCurrentPosition = infoBean.getExtraValue();
            if (mDanmakuView != null && mDanmakuOpen && !mIsAudioMode) {
                mDanmakuView.setCurrentPosition((int) mCurrentPosition);
            }
            if (mControlView != null) {
                //?????????????????????,???????????????????????????,??????????????????????????????
                mControlView.setOtherEnable(true);
            }
            if (GlobalPlayerConfig.IS_VIDEO) {
                //??????,???????????????????????????,??????????????????
                if (mControlView != null && mControlView.isNeedToPause((int) infoBean.getExtraValue(), mAdvVideoCount)) {
                    if (infoBean.getExtraValue() < TRAILER * 1000) {
                        startAdvVideo();
                    }
                }
                if (mControlView != null && !inSeek && mPlayerState == IPlayer.started) {
                    /*
                        ????????????????????????,seek???sourceDuration / 2??????????????????????????????????????????????????????,????????????????????????????????????????????????????????????????????????
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
     * ?????????Render??????
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
     * ???????????????????????????????????????
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
     * ????????????onVideoRenderingStart
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
     * ?????????onVideoRenderingStart
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
     * ???????????????????????????
     */
    private static class VideoPlayerSubtitleDeisplayListener implements IPlayer.OnSubtitleDisplayListener {

        private final WeakReference<AliyunVodPlayerView> weakReference;

        public VideoPlayerSubtitleDeisplayListener(AliyunVodPlayerView aliyunVodPlayerView) {
            weakReference = new WeakReference<>(aliyunVodPlayerView);
        }

        @Override
        public void onSubtitleExtAdded(int trackIndex, String url) {
        }

        @Override
        public void onSubtitleHeader(int i, String s) {
        }

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
     * ????????????
     *
     * @param id ??????
     */
    private void onSubtitleHide(long id) {
        mSubtitleView.dismiss(id + "");
    }

    /**
     * ????????????
     *
     * @param id   ??????
     * @param data ??????
     */
    private void onSubtitleShow(long id, String data) {
        SubtitleView.Subtitle subtitle = new SubtitleView.Subtitle();
        subtitle.id = id + "";
        subtitle.content = data;
        mSubtitleView.show(subtitle);
    }

    /**
     * ?????????TrackChanged??????
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
     * ????????? trackInfoChangedSuccess
     */
    private void sourceVideoPlayerTrackInfoChangedSuccess(TrackInfo trackInfo) {
        //?????????????????????
        if (trackInfo.getType() == TrackInfo.Type.TYPE_VOD) {
            //??????????????????????????????
            mControlView.setCurrentQuality(trackInfo.getVodDefinition());
            if (mIsScreenCosting) {
                //????????????
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
     * ????????? trackInfochangedFail
     */
    private void sourceVideoPlayerTrackInfoChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {
        //??????????????????????????????????????????
        if (mTipsView != null) {
            mTipsView.hideNetLoadingTipView();
        }
        stop();
        if (mOutOnTrackChangedListener != null) {
            mOutOnTrackChangedListener.onChangedFail(trackInfo, errorInfo);
        }
    }

    /**
     * ?????????seek????????????
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
     * ?????????seek??????
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
     * ???????????????????????????
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
     * ?????????????????????
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
                        AVToast.show(getContext(), true, R.string.alivc_player_snap_shot_save_success);
                    }
                });
            }
        });
    }

    /** ------------------- ??????????????? end--------------------------- */

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

                    //???????????????????????????????????????
                    if (aliyunVodPlayerView.mSourceVideoMediaInfo != null && aliyunVodPlayerView.mAdvVideoMediaInfo != null) {
                        MediaInfo mediaInfo = new MediaInfo();
                        //????????????????????????MediaInfo,??????????????????duration
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
                        //?????????????????????,???????????????????????????seekBar
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
     * ---------------------------------????????????-------------------------------------
     */

    /**
     * ??????????????????
     */
    public void setDotInfo(List<DotBean> dotBean) {
        if (mControlView != null) {
            mControlView.setDotInfo(dotBean);
        }
    }

    /**
     * ?????????????????????
     */
    public void setEnableHardwareDecoder(boolean enableHardwareDecoder) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.enableHardwareDecoder(enableHardwareDecoder);
        }
    }

    /**
     * ??????????????????,??????5??????
     */
    public void setTrailerTime(int trailerTime) {
        TRAILER = trailerTime;
    }

    /**
     * ????????????
     */
    public void setPlayDomain(String domain) {
        PLAY_DOMAIN = domain;
    }

    /**
     * ????????????????????????????????????????????????
     */
    public MediaInfo getCurrentMediaInfo() {
        return mAliyunMediaInfo;
    }

    /**
     * ????????????????????????
     */
    public void setScreenBrightness(int screenBrightness) {
        this.mScreenBrightness = screenBrightness;
    }

    /**
     * ????????????????????????
     */
    public int getScreenBrightness() {
        return this.mScreenBrightness;
    }

    /**
     * ????????????
     */
    public void snapShot() {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.snapshot();
        }
    }

    /**
     * ??????????????????
     *
     * @param circlePlay true:????????????
     */
    public void setCirclePlay(boolean circlePlay) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setLoop(circlePlay);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param mode ????????????
     */
    public void setRenderMirrorMode(IPlayer.MirrorMode mode) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setMirrorMode(mode);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param rotate ????????????
     */
    public void setRenderRotate(IPlayer.RotateMode rotate) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setRotateModel(rotate);
        }
    }

    /**
     * ????????????????????????
     */
    public boolean getIsCreenCosting() {
        return mIsScreenCosting;
    }

    /**
     * ???????????????????????????
     *
     * @param show true:???
     */
    public void setTitleBarCanShow(boolean show) {
        if (mControlView != null) {
            mControlView.setTitleBarCanShow(show);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param show true:???
     */
    public void setControlBarCanShow(boolean show) {
        if (mControlView != null) {
            mControlView.setControlBarCanShow(show);
        }

    }

    /**
     * ??????????????????
     */
    public void enableNativeLog() {
        Logger.getInstance(getContext()).enableConsoleLog(true);
        Logger.getInstance(getContext()).setLogLevel(Logger.LogLevel.AF_LOG_LEVEL_DEBUG);
    }

    /**
     * ??????????????????
     */
    public void disableNativeLog() {
        Logger.getInstance(getContext()).enableConsoleLog(false);
    }

    /**
     * ??????????????????
     */
    public void setCacheConfig(CacheConfig cacheConfig) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setCacheConfig(cacheConfig);
        }
    }

    /**
     * ??????Config
     */
    public PlayerConfig getPlayerConfig() {
        if (mAliyunRenderView != null) {
            return mAliyunRenderView.getPlayerConfig();
        }
        return null;
    }

    /**
     * ??????Config
     */
    public void setPlayerConfig(PlayerConfig playerConfig) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setPlayerConfig(playerConfig);
        }
    }

    /**
     * ??????SDK?????????
     *
     * @return SDK?????????
     */
    public String getSDKVersion() {
        return AliPlayerFactory.getSdkVersion();
    }

    /**
     * ????????????surfaceView
     *
     * @return ??????surfaceView
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
     * ??????????????????
     *
     * @param auto true ????????????
     */
    public void setAutoPlay(boolean auto) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setAutoPlay(auto);
        }
    }

    /**
     * ????????????
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
     * ????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param lockScreen ????????????
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
     * ?????????????????????????????????????????????
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
            //?????????reset???????????????????????????????????????
            mControlView.setVideoPosition(currentPosition);
        }
        if (mGestureView != null) {
            mGestureView.reset();
        }

        if (mAliyunRenderView != null) {

            //?????????????????????loading??????
            if (mTipsView != null) {
                mTipsView.showNetLoadingTipView();
            }
            //seek???????????????????????????
            if (GlobalPlayerConfig.IS_VIDEO) {
                //????????????
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
     * ?????????????????????????????????
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
            //?????????????????????loading??????
            if (mTipsView != null && !mIsAudioMode) {
                mTipsView.showNetLoadingTipView();
            }
            if (mOutOnTipClickListener != null) {
                mOutOnTipClickListener.onReplay();
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param speedValue ????????????
     */
    public void changeSpeed(float speedValue) {
        mAliyunRenderView.setSpeed(speedValue);
        mControlView.setSpeedViewText(speedValue);
    }

    /**
     * ??????????????????
     */
    public float getCurrentSpeed() {
        return mAliyunRenderView.getSpeed();
    }

    /**
     * ??????????????????
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
     * ?????? fps
     */
    public float getOption(IPlayer.Option renderFPS) {
        if (mAliyunRenderView != null) {
            return mAliyunRenderView.getOption(renderFPS);
        }
        return 0f;
    }

    /**
     * ??????????????????
     */
    public float getCurrentVolume() {
        if (mAliyunRenderView != null) {
            return mAliyunRenderView.getVolume();
        }
        return 0;
    }

    /**
     * ????????????,?????????false
     */
    public void setMute(boolean isMute) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setMute(isMute);
        }
    }

    /**
     * ??????????????????
     */
    public void setScreenCostingVolume(int volume) {
        if (volume <= 0) {
            volume = 0;
        }
        if (volume >= 100) {
            volume = 100;
        }
        this.mScreenCostingVolume = volume;
        //???????????????????????????
        if (mScreenCostingView != null && mIsScreenCosting) {
            mScreenCostingView.setVolume(mScreenCostingVolume);
        }
    }

    /**
     * ????????????????????????
     */
    public int getScreenCostingVolume() {
        return mScreenCostingVolume;
    }

    /**
     * ?????????????????????
     * 0?????????---100?????????
     */
    public void setDanmakuAlpha(int progress) {
        if (mDanmakuView != null) {
            mDanmakuView.setAlpha((float) (1 - progress / 100.0 * 1.0));
        }
    }

    /**
     * ??????????????????
     */
    public void setDanmakuSpeed(int progress) {
        if (mDanmakuView != null) {
            mDanmakuView.setDanmakuSpeed((float) (2.5 - (100 + progress) / 100.0 * 1.0));
        }
    }

    /**
     * ????????????????????????
     */
    public void setDanmakuRegion(int progress) {
        if (mDanmakuView != null) {
            mDanmakuView.setDanmakuRegion(progress);
        }
    }

    /**
     * ????????????
     */
    public void screenCostPlay() {
        mIsScreenCosting = true;
        if (mAliyunRenderView != null) {
            mAliyunRenderView.pause();
        }
        // ?????????????????????????????????????????????????????????????????????DLNA?????????????????????????????????????????????????????? */
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
     * ????????????
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
     * ??????????????????
     */
    public void setDanmakuDefault() {
        if (mDanmakuView != null) {
            setDanmakuAlpha(DanmakuSettingView.ALPHA_PROGRESS_DEFAULT);
            setDanmakuSpeed(DanmakuSettingView.SPEED_PROGRESS_DEFAULT);
            setDanmakuRegion(DanmakuSettingView.REGION_PROGRESS_DEFAULT);
        }
    }

    /**
     * ??????????????????
     *
     * @param uri url??????
     */
    public void setCoverUri(String uri) {
        if (mCoverView != null && !TextUtils.isEmpty(uri)) {
            ImageLoader.loadImg(uri, mCoverView);
            mCoverView.setVisibility(isPlaying() ? GONE : VISIBLE);
        }
    }

    /**
     * ????????????id
     *
     * @param resId ??????id
     */
    public void setCoverResource(int resId) {
        if (mCoverView != null) {
            mCoverView.setImageResource(resId);
            mCoverView.setVisibility(isPlaying() ? GONE : VISIBLE);
        }
    }

    /**
     * ??????????????????
     *
     * @param scaleMode ????????????
     */
    public void setScaleMode(IPlayer.ScaleMode scaleMode) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setScaleModel(scaleMode);
        }
    }

    /**
     * ??????????????????
     *
     * @return ??????????????????
     */
    public IPlayer.ScaleMode getScaleMode() {
        IPlayer.ScaleMode scaleMode = IPlayer.ScaleMode.SCALE_ASPECT_FIT;
        if (mAliyunRenderView != null) {
            scaleMode = mAliyunRenderView.getScaleModel();
        }
        return scaleMode;
    }

    /**
     * ????????????????????????
     */
    public void setLoop(boolean isLoop) {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setLoop(isLoop);
        }
    }

    /**
     * ????????????????????????
     */
    public boolean isLoop() {
        if (mAliyunRenderView != null) {
            return mAliyunRenderView.isLoop();
        }
        return false;
    }

    /**
     * ?????????????????????
     *
     * @param trackInfo ?????????
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
     * ????????????????????????????????????Track????????????????????????????????????Track?????????????????????????????????????????????????????????
     */
    public void selectAutoBitrateTrack() {
        if (mAliyunRenderView != null) {
            mAliyunRenderView.selectTrack(TrackInfo.AUTO_SELECT_INDEX);
        }
    }

    /**
     * ??????TrackInfo.Type?????????????????????
     *
     * @param type ?????????
     * @return ?????????
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

    private void beforeSetDataSource(boolean refresh, boolean qualityForce) {
        if (mAliyunRenderView == null) return;

        if (refresh) {
            clearAllSource();
            reset();
        }

        if (mControlView != null) {
            mControlView.setForceQuality(qualityForce);
        }
    }

    /**
     * ??????LiveSts
     */
    public void setLiveStsDataSource(LiveSts liveSts) {
        beforeSetDataSource(false, liveSts.isForceQuality());

        mAliyunLiveSts = liveSts;

        if (!show4gTips()) {
            innerPrepareLiveSts(liveSts);
        }
    }


    /**
     * ??????UrlSource
     */
    public void setLocalSource(UrlSource aliyunLocalSource) {
        beforeSetDataSource(false, aliyunLocalSource.isForceQuality());

        mAliyunLocalSource = aliyunLocalSource;

        if (!show4gTips()) {
            innerPrepareUrl(aliyunLocalSource);
        }
    }

    /**
     * ??????VidSts
     */
    public void setVidSts(VidSts vidSts, boolean refresh) {
        beforeSetDataSource(refresh, vidSts.isForceQuality());
        mAliyunVidSts = vidSts;

        showVideoFunction(refresh);
    }

    /**
     * ??????VidAuth
     */
    public void setVidAuth(VidAuth aliyunPlayAuth) {
        beforeSetDataSource(false, aliyunPlayAuth.isForceQuality());

        mAliyunPlayAuth = aliyunPlayAuth;

        //4G???????????????
        if (!show4gTips()) {
            //?????????????????????
            innerPrepareAuth(aliyunPlayAuth);
        }
    }

    /**
     * ??????Mps
     */
    public void setVidMps(VidMps vidMps) {
        beforeSetDataSource(false, vidMps.isForceQuality());

        mAliyunVidMps = vidMps;

        //4G???????????????
        if (!show4gTips()) {
            //?????????????????????
            innerPrepareMps(vidMps);
        }
    }

//-------------------------------- outter method -------------------------------------

//-------------------------------- inner method --------------------------------------

    private void innerBeforePrepare() {
        if (mAliyunRenderView != null) {
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
     * ??????playAuth prepare
     */
    private void innerPrepareAuth(VidAuth aliyunPlayAuth) {
        innerBeforePrepare();
        mAliyunRenderView.setDataSource(aliyunPlayAuth);
        mAliyunRenderView.prepare();
    }

    /**
     * ??????Mps prepare
     */
    private void innerPrepareMps(VidMps vidMps) {
        innerBeforePrepare();
        mAliyunRenderView.setDataSource(vidMps);
        mAliyunRenderView.prepare();
    }

    /**
     * ??????vidsts ???
     */
    private void innerPrepareSts(VidSts vidSts) {
        innerBeforePrepare();
        if (GlobalPlayerConfig.IS_TRAILER) {
            VidPlayerConfigGen vidPlayerConfigGen = new VidPlayerConfigGen();
            //??????,??????5??????
            vidPlayerConfigGen.addPlayerConfig("PlayDomain", PLAY_DOMAIN);
            vidPlayerConfigGen.setPreviewTime(TRAILER);
            vidSts.setPlayConfig(vidPlayerConfigGen);
        }
        if (mAliyunRenderView != null) {
            mAliyunRenderView.setDataSource(vidSts);
            mAliyunRenderView.prepare();
        }
    }

    /**
     * prepare???????????????
     */
    private void innerPrepareUrl(UrlSource aliyunLocalSource) {
        innerBeforePrepare();
        //???????????????????????????????????????
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
     * ??????LiveSts ???
     */
    private void innerPrepareLiveSts(LiveSts mAliyunLiveSts) {
        innerBeforePrepare();
        mAliyunRenderView.setDataSource(mAliyunLiveSts);
        mAliyunRenderView.prepare();
    }

//-------------------------------- inner method --------------------------------------
}
