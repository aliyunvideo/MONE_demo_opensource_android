package com.aliyun.player.alivcplayerexpand.view.more;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.aliyun.player.alivcplayerexpand.view.dlna.GetInfoTimerTask;
import com.aliyun.player.alivcplayerexpand.view.dlna.callback.ClingPlayControl;
import com.aliyun.player.alivcplayerexpand.view.dlna.callback.ControlCallback;
import com.aliyun.player.alivcplayerexpand.view.dlna.callback.ControlReceiveCallback;
import com.aliyun.player.alivcplayerexpand.view.dlna.callback.DLNAOptionListener;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingPositionResponse;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingTransportInfoResponse;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.Config;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.DLANPlayState;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IResponse;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.Intents;
import com.aliyun.player.alivcplayerexpand.view.dlna.manager.ClingManager;
import com.aliyun.player.alivcplayerexpand.util.Formatter;

import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;

import java.lang.ref.WeakReference;
import java.util.TimerTask;

/**
 * 投屏中 View
 */
public class ScreenCostingView extends FrameLayout {

    private static final String TAG = "ScreenCostingView";

    /**
     * 连接设备状态: 播放状态
     */
    public static final int PLAY_ACTION = 0xa1;
    /**
     * 连接设备状态: 暂停状态
     */
    public static final int PAUSE_ACTION = 0xa2;
    /**
     * 连接设备状态: 停止状态
     */
    public static final int STOP_ACTION = 0xa3;
    /**
     * 连接设备状态: 转菊花状态
     */
    public static final int TRANSITIONING_ACTION = 0xa4;
    /**
     * 获取进度
     */
    public static final int GET_POSITION_INFO_ACTION = 0xa5;
    /**
     * 获取播放状态
     */
    public static final int GET_CURRENT_TRANSPORT_INFO = 0xa6;
    /**
     * 投放失败
     */
    public static final int ERROR_ACTION = 0xa7;
    /**
     * 投屏获取播放进度定时器
     */
    private GetInfoTimerTask mGetPositionTimerTask;

    private DLNAOptionListener mOutDLNAOptionListener;

    private TransportStateBroadcastReceiver mTransportStateBroadcastReceiver;

    /**
     * 获取播放进度回调监听
     */
    private OnGetPositionInfoListener mOnGetPositionInfoListener;

    /**
     * 获取播放状态回调监听
     */
    private OnGetTransportInfoListener mOnGetTransportInfoListener;

    private Handler mHandler = new InnerHandler(this);
    /**
     * 投屏控制器
     */
    private static ClingPlayControl mClingPlayControl = new ClingPlayControl();

    public ScreenCostingView(Context context) {
        super(context);
        init(context);
    }

    public ScreenCostingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScreenCostingView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        initView();
        initListener();
        initService();

    }

    private void initView() {
    }

    private void initListener() {
    }

    private void initService() {
        mTransportStateBroadcastReceiver = new TransportStateBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intents.ACTION_PLAYING);
        filter.addAction(Intents.ACTION_PAUSED_PLAYBACK);
        filter.addAction(Intents.ACTION_STOPPED);
        filter.addAction(Intents.ACTION_TRANSITIONING);
        getContext().registerReceiver(mTransportStateBroadcastReceiver, filter);
    }

    public void play(final int position) {
        @DLANPlayState.DLANPlayStates
        int currentState = mClingPlayControl.getCurrentState();

        //通过判断状态 来决定 是继续播放 还是重新播放
        if (currentState == DLANPlayState.STOP) {
            Log.e("AliyunDLNA", "playNew : ");
            mClingPlayControl.playNew(Config.DLNA_URL, new ControlCallback() {

                @Override
                public void success(IResponse response) {
                    Log.e("AliyunDLNA", "playNew Success: ");
                    ClingManager.getInstance().onDeviceConnected();
                    ClingManager.getInstance().registerAVTransport(getContext());
                    ClingManager.getInstance().registerRenderingControl(getContext());
                    if (mOutDLNAOptionListener != null) {
                        mOutDLNAOptionListener.playSuccess();
                    }
                }

                @Override
                public void fail(IResponse response) {
                    Log.e("AliyunDLNA", "playNew fail: ");
                    mHandler.sendEmptyMessage(ERROR_ACTION);
                    if (mOutDLNAOptionListener != null) {
                        mOutDLNAOptionListener.playFailed();
                    }
                }
            });
        } else {
            Log.e("AliyunDLNA", "play ");
            mClingPlayControl.play(new ControlCallback() {
                @Override
                public void success(IResponse response) {
                    Log.e("AliyunDLNA", "play success");
                    if (mOutDLNAOptionListener != null) {
                        mOutDLNAOptionListener.playSuccess();
                    }
                }

                @Override
                public void fail(IResponse response) {
                    Log.e("AliyunDLNA", "play fail");
                    mHandler.sendEmptyMessage(ERROR_ACTION);
                    if (mOutDLNAOptionListener != null) {
                        mOutDLNAOptionListener.playFailed();
                    }
                }
            });
        }
    }

    /**
     * 退出投屏
     */
    public void exit() {
        if (mClingPlayControl == null) {
            return;
        }
        if(mGetPositionTimerTask != null){
            mGetPositionTimerTask.cancel();
        }
        Log.e("AliyunDLNA", "stop");
        mClingPlayControl.stop(new ControlCallback() {
            @Override
            public void success(IResponse response) {
                Log.e("AliyunDLNA", "stop success");
                mClingPlayControl.setCurrentState(DLANPlayState.STOP);
            }

            @Override
            public void fail(IResponse response) {
                Log.e("AliyunDLNA", "stop fail");
            }
        });
    }

    /**
     * 开始seek
     */
    public void seek(int position) {
        Log.e("AliyunDLNA", "seek " + position);
        mClingPlayControl.seek(position, new ControlCallback() {
            @Override
            public void success(IResponse response) {
                Log.e("AliyunDLNA", "seek success");
                int currentState = mClingPlayControl.getCurrentState();
                if (currentState == DLANPlayState.PAUSE) {
                    play(0);
                }
            }

            @Override
            public void fail(IResponse response) {
                Log.e("AliyunDLNA", "seek fail");
            }
        });
    }

    /**
     * 暂停
     */
    public void pause() {
        Log.e("AliyunDLNA", "pause");
        mClingPlayControl.pause(new ControlCallback() {
            @Override
            public void success(IResponse response) {
                Log.e("AliyunDLNA", "pause success");
                mClingPlayControl.setCurrentState(DLANPlayState.PAUSE);
            }

            @Override
            public void fail(IResponse response) {
                Log.e("AliyunDLNA", "pause fail");
            }
        });
    }

    /**
     * 调节音量
     */
    public void setVolume(float targetVolume) {
        Log.e("AliyunDLNA", "setVolume = " + targetVolume);
        mClingPlayControl.setVolume((int) targetVolume, new ControlCallback() {
            @Override
            public void success(IResponse response) {
                Log.e("AliyunDLNA", "setVolume success");
            }

            @Override
            public void fail(IResponse response) {
                Log.e("AliyunDLNA", "setVolume fail");
            }
        });
    }

    /**
     * 开启定时任务
     * 每隔2s获取一次播放进度和播放状态
     */
    public void startScheduledTask() {
        if (mGetPositionTimerTask != null) {
            mGetPositionTimerTask.cancel();
        }
        mGetPositionTimerTask = new GetInfoTimerTask();
        mGetPositionTimerTask.setTime(2000);
        mGetPositionTimerTask.setTimerTask(new TimerTask() {
            @Override
            public void run() {
                //获取当前播放进度
                mClingPlayControl.getPositionInfo(new ControlReceiveCallback() {

                    @Override
                    public void receive(IResponse response) {
                        if (response instanceof ClingPositionResponse) {
                            ClingPositionResponse clingPositionResponse = (ClingPositionResponse) response;
                            PositionInfo positionInfo = clingPositionResponse.getResponse();
                            if (positionInfo != null) {
                                String relTime = positionInfo.getRelTime();
                                String trackDuration = positionInfo.getTrackDuration();
                                int currentPosition = Formatter.getIntTime(relTime);
                                int duration = Formatter.getIntTime(trackDuration);
                                Message msg = Message.obtain();
                                msg.what = GET_POSITION_INFO_ACTION;
                                msg.arg1 = currentPosition;
                                msg.arg2 = duration;
                                mHandler.sendMessage(msg);
                                Log.e("AliyunDLNA", "获取播放进度 : " + currentPosition);
                            }
                        }
                    }

                    @Override
                    public void success(IResponse response) {
                    }

                    @Override
                    public void fail(IResponse response) {
                    }
                });

                mClingPlayControl.getTransportInfo(new ControlReceiveCallback() {
                    @Override
                    public void receive(IResponse response) {
                        if (response instanceof ClingTransportInfoResponse) {
                            ClingTransportInfoResponse clingTransportInfoResponse = (ClingTransportInfoResponse) response;
                            TransportInfo transportInfo = clingTransportInfoResponse.getResponse();
                            if (transportInfo != null) {
                                Message msg = Message.obtain();
                                msg.what = GET_CURRENT_TRANSPORT_INFO;
                                msg.obj = transportInfo;
                                mHandler.sendMessage(msg);
                                Log.e("AliyunDLNA", "获取播放状态 : " + transportInfo.getCurrentTransportState().name());
                            }
                        }
                    }

                    @Override
                    public void success(IResponse response) {

                    }

                    @Override
                    public void fail(IResponse response) {

                    }
                });
            }
        });

        mGetPositionTimerTask.start();
    }

    /**
     * 停止定时任务
     */
    public void stopScheduledTask() {
        if (mGetPositionTimerTask != null) {
            mGetPositionTimerTask.cancel();
        }
    }

    /**
     * 接收状态改变信息
     */
    private class TransportStateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "Receive playback intent:" + action);
            if (Intents.ACTION_PLAYING.equals(action)) {
                mHandler.sendEmptyMessage(PLAY_ACTION);

            } else if (Intents.ACTION_PAUSED_PLAYBACK.equals(action)) {
                mHandler.sendEmptyMessage(PAUSE_ACTION);

            } else if (Intents.ACTION_STOPPED.equals(action)) {
                mHandler.sendEmptyMessage(STOP_ACTION);

            } else if (Intents.ACTION_TRANSITIONING.equals(action)) {
                mHandler.sendEmptyMessage(TRANSITIONING_ACTION);
            }
        }
    }

    public void destroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        getContext().unregisterReceiver(mTransportStateBroadcastReceiver);

    }

    public void setOnOutDLNAPlayerList(DLNAOptionListener listener) {
        this.mOutDLNAOptionListener = listener;
    }

    public static class InnerHandler extends Handler {

        private WeakReference<ScreenCostingView> weakReference;

        public InnerHandler(ScreenCostingView screenCostingView) {
            weakReference = new WeakReference<>(screenCostingView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ScreenCostingView screenCostingView = weakReference.get();
            if (screenCostingView != null) {
                switch (msg.what) {
                    case PLAY_ACTION:
                        Log.i(TAG, "Execute PLAY_ACTION");
                        mClingPlayControl.setCurrentState(DLANPlayState.PLAY);
                        break;
                    case PAUSE_ACTION:
                        Log.i(TAG, "Execute PAUSE_ACTION");
                        mClingPlayControl.setCurrentState(DLANPlayState.PAUSE);
                        break;
                    case STOP_ACTION:
                        Log.i(TAG, "Execute STOP_ACTION");
                        mClingPlayControl.setCurrentState(DLANPlayState.STOP);

                        break;
                    case TRANSITIONING_ACTION:
                        Log.i(TAG, "Execute TRANSITIONING_ACTION");
                        break;
                    case GET_POSITION_INFO_ACTION:
                        Log.e(TAG, "Execute GET_POSITION_INFO_ACTION");
                        Log.e("AliyunDLNA", "获取播放进度handler : " + msg.arg1);
                        if (screenCostingView.mOnGetPositionInfoListener != null) {
                            screenCostingView.mOnGetPositionInfoListener.onGetPositionInfo(msg.arg1, msg.arg2);
                        }
                        break;
                    case GET_CURRENT_TRANSPORT_INFO:
                        Log.e(TAG, "Execute GET_CURRENT_TRANSPORT_INFO");
                        TransportInfo transportInfo = (TransportInfo) msg.obj;
                        Log.e("AliyunDLNA", "获取播放状态handler : " + transportInfo.getCurrentTransportState().name());
                        if (screenCostingView.mOnGetTransportInfoListener != null) {
                            screenCostingView.mOnGetTransportInfoListener.onGetTransportInfo(transportInfo);
                        }
                        break;
                    case ERROR_ACTION:
                        Log.e(TAG, "Execute ERROR_ACTION");
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 获取播放进度回调
     */
    public interface OnGetPositionInfoListener {
        void onGetPositionInfo(int currentPositiion, int duration);
    }

    /**
     * 设置播放进度监听
     */
    public void setOnGetPositionInfoListener(OnGetPositionInfoListener listener) {
        this.mOnGetPositionInfoListener = listener;
    }

    /**
     * 获取播放状态回调
     */
    public interface OnGetTransportInfoListener {
        void onGetTransportInfo(TransportInfo transportInfo);
    }

    public void setOnGetTransportInfoListener(OnGetTransportInfoListener listener) {
        this.mOnGetTransportInfoListener = listener;
    }
}
