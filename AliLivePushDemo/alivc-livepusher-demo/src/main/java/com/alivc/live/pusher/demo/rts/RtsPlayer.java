package com.alivc.live.pusher.demo.rts;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;
import com.cicada.player.utils.Logger;

/**
 * RTS 播放
 */
public class RtsPlayer {

    static {
        System.loadLibrary("RtsSDK");
    }

    private static final int TRACE_ID_CODE = 104;
    private final AliPlayer mRtsAliPlayer;
    private String mUrl;
    private String mTraceId;
    private RtsPlayerListener mRtsPlayerListener;
    private int mCurrentPlayerState;
    private boolean mEnableRetry = true;

    public RtsPlayer(Context context) {
        //开启日志
        Logger.getInstance(context).enableConsoleLog(true);
        Logger.getInstance(context).setLogLevel(Logger.LogLevel.AF_LOG_LEVEL_TRACE);

        mRtsAliPlayer = AliPlayerFactory.createAliPlayer(context);
        //自动播放
        mRtsAliPlayer.setAutoPlay(true);
        mRtsAliPlayer.setOnInfoListener(infoBean -> {
            if (infoBean.getCode() == InfoCode.DirectComponentMSG) {
                String extraMsg = infoBean.getExtraMsg();
                parseDirectComponentMSG(extraMsg);
            }
        });

        mRtsAliPlayer.setOnStateChangedListener((newState) -> {
            mCurrentPlayerState = newState;
        });

        mRtsAliPlayer.setOnRenderingStartListener(() -> {
            if (mRtsPlayerListener != null) {
                mRtsPlayerListener.onFirstFrameRender();
            }
        });

        mRtsAliPlayer.setOnErrorListener(errorInfo -> {
            if (mRtsPlayerListener != null) {
                mRtsPlayerListener.onPlayerError(errorInfo.getMsg(), errorInfo.getCode().getValue());
            }
        });
    }

    /**
     * 给播放器设置 Surface
     */
    public void setSurface(SurfaceHolder surface) {
        mRtsAliPlayer.setDisplay(surface);
    }

    /**
     * 设置播放源
     */
    public void setDataSource(String url) {
        this.mUrl = url;
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(mUrl);
        PlayerConfig config = mRtsAliPlayer.getConfig();
        //播放器配置
        if (mUrl.startsWith("artc://")) {
            config.mMaxDelayTime = 1000;
            config.mHighBufferDuration = 10;
            config.mStartBufferDuration = 10;
        } else {
            config.mMaxDelayTime = 10000;
            config.mHighBufferDuration = 100;
            config.mStartBufferDuration = 100;
        }
        mRtsAliPlayer.setConfig(config);
        mRtsAliPlayer.setDataSource(urlSource);
    }

    /**
     * 准备播放
     */
    public void prepare() {
        mRtsAliPlayer.prepare();
    }

    public void surfaceChanged() {
        mRtsAliPlayer.surfaceChanged();
    }

    public void stop() {
        mRtsAliPlayer.stop();
    }

    public void release() {
        mUrl = null;
        mRtsAliPlayer.release();
    }

    /**
     * 解析 Rts 事件
     */
    private void parseDirectComponentMSG(String msg) {
        if (msg.contains("code=" + RtsError.E_DNS_FAIL.getCode()) || msg.contains("code=" + RtsError.E_AUTH_FAIL.getCode())
                || msg.contains("code=" + RtsError.E_CONN_TIMEOUT.getCode()) || msg.contains("code=" + RtsError.E_SUB_TIMEOUT.getCode())
                || msg.contains("code=" + RtsError.E_SUB_NO_STREAM.getCode()) || msg.contains("code=" + RtsError.E_STREAM_BROKEN.getCode())
                || msg.contains("code=" + RtsError.E_RECV_STOP_SIGNAL.getCode())) {

            //不是播放状态，降级
            if (mCurrentPlayerState != IPlayer.started) {
                willBeDemoted();
            } else {
                //播放状态，收到 STREAM_BROKEN 事件并且未重试，则重试
                if (msg.contains("code=" + RtsError.E_STREAM_BROKEN.getCode()) && mEnableRetry) {
                    retry();
                } else {
                    parseError(msg);
                    //除 stop 信令以外的其他失败消息，才会降级
                    if (!msg.contains("code=" + RtsError.E_RECV_STOP_SIGNAL.getCode())) {
                        willBeDemoted();
                    }
                }
            }
        } else if (msg.contains("code=" + TRACE_ID_CODE)) {
            parseTraceId(msg);
        }
    }

    /**
     * 重试
     */
    private void retry() {
        prepare();
        mEnableRetry = false;
    }

    /**
     * 降级策略
     */
    private void willBeDemoted() {
        stop();
        if (mUrl.startsWith("artc://")) {
            setDataSource(mUrl.replace("artc://", "rtmp://"));
            prepare();
        }
    }

    /**
     * 解析 Rts 事件，并通知上层监听
     */
    private void parseError(String msg) {
        if (mRtsPlayerListener != null) {
            if (msg.contains("code=" + RtsError.E_DNS_FAIL.getCode())) {
                mRtsPlayerListener.onRtsMsg(RtsError.E_DNS_FAIL.name(), RtsError.E_DNS_FAIL.getCode(), true);
            } else if (msg.contains("code=" + RtsError.E_AUTH_FAIL.getCode())) {
                mRtsPlayerListener.onRtsMsg(RtsError.E_AUTH_FAIL.name(), RtsError.E_AUTH_FAIL.getCode(), true);
            } else if (msg.contains("code=" + RtsError.E_CONN_TIMEOUT.getCode())) {
                mRtsPlayerListener.onRtsMsg(RtsError.E_CONN_TIMEOUT.name(), RtsError.E_CONN_TIMEOUT.getCode(), true);
            } else if (msg.contains("code=" + RtsError.E_SUB_TIMEOUT.getCode())) {
                mRtsPlayerListener.onRtsMsg(RtsError.E_SUB_TIMEOUT.name(), RtsError.E_SUB_TIMEOUT.getCode(), true);
            } else if (msg.contains("code=" + RtsError.E_SUB_NO_STREAM.getCode())) {
                mRtsPlayerListener.onRtsMsg(RtsError.E_SUB_NO_STREAM.name(), RtsError.E_SUB_NO_STREAM.getCode(), true);
            } else if (msg.contains("code=" + RtsError.E_STREAM_BROKEN.getCode())) {
                mRtsPlayerListener.onRtsMsg(RtsError.E_STREAM_BROKEN.name(), RtsError.E_STREAM_BROKEN.getCode(), true);
            } else if (msg.contains("code=" + RtsError.E_RECV_STOP_SIGNAL.getCode())) {
                mRtsPlayerListener.onRtsMsg(RtsError.E_RECV_STOP_SIGNAL.name(), RtsError.E_RECV_STOP_SIGNAL.getCode(), false);
            }
        }
    }

    /**
     * 解析 TraceId
     */
    private void parseTraceId(String msg) {
        String[] split = msg.split("-sub-");
        if (split.length >= 1) {
            mTraceId = "RequestId:" + (split[1].substring(0, split[1].length() - 1));
            mTraceId = mTraceId.replace("\"", "").replace("\\", "");
        }
    }

    /**
     * 获取 TraceId
     */
    public String getTraceId() {
        return mTraceId;
    }

    /**
     * 获取 URL
     */
    public String getUrl() {
        return mUrl;
    }

    public void setRtsPlayerListener(RtsPlayerListener listener) {
        this.mRtsPlayerListener = listener;
    }

    public interface RtsPlayerListener {
        /**
         * 首帧显示回调
         */
        void onFirstFrameRender();

        /**
         * 播放器报错
         */
        void onPlayerError(String msg, int code);

        /**
         * Rts 事件
         */
        void onRtsMsg(String msg, int code, boolean showDemoted);
    }

    public enum RtsError {
        /**
         * DNS 解析失败
         */
        E_DNS_FAIL(20001),
        /**
         * 鉴权失败
         */
        E_AUTH_FAIL(20002),
        /**
         * 建联信令超时
         */
        E_CONN_TIMEOUT(20011),
        /**
         * 订阅信令返回错误，或者超时。
         */
        E_SUB_TIMEOUT(20012),
        /**
         * 订阅流不存在
         */
        E_SUB_NO_STREAM(20013),
        /**
         * 媒体超时，没有收到音频包和视频包
         */
        E_STREAM_BROKEN(20052),
        /**
         * 收到CDN的stop信令
         */
        E_RECV_STOP_SIGNAL(20061);

        private int code;

        RtsError(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

}
