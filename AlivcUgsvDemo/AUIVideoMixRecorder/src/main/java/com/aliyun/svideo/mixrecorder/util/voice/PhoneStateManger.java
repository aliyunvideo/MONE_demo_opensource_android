package com.aliyun.svideo.mixrecorder.util.voice;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.lang.ref.WeakReference;

/**
 * 手机电话状态监听
 *
 * @author xlx
 */
public class PhoneStateManger {

    private MyPhoneStateListener myPhoneStateListener;
    private WeakReference<Context> weakReferenceContext;

    private OnPhoneStateChangeListener onPhoneStateChangeListener;

    public PhoneStateManger(Context context) {
        weakReferenceContext = new WeakReference<>(context);
    }

    /**
     * 注册电话监听
     */
    public void registPhoneStateListener() {
        Context context = weakReferenceContext.get();
        if (context != null) {
            TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                try {
                    // 注册来电监听
                    myPhoneStateListener = new MyPhoneStateListener(this);
                    telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
                } catch (Exception e) {
                    // 异常捕捉
                    e.printStackTrace();
                }
            }
        }
    }

    private static class MyPhoneStateListener extends PhoneStateListener {
        WeakReference<PhoneStateManger> weakReference;
        MyPhoneStateListener(PhoneStateManger phoneStateManger) {
            weakReference = new WeakReference<>(phoneStateManger);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (weakReference == null) {
                return;
            }
            PhoneStateManger phoneStateManger = weakReference.get();
            if (phoneStateManger != null) {
                switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (phoneStateManger.onPhoneStateChangeListener != null) {
                        phoneStateManger.onPhoneStateChangeListener.stateIdel();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (phoneStateManger.onPhoneStateChangeListener != null) {
                        phoneStateManger.onPhoneStateChangeListener.stateOff();
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    if (phoneStateManger.onPhoneStateChangeListener != null) {
                        phoneStateManger.onPhoneStateChangeListener.stateRinging();
                    }
                    break;
                default:
                    break;
                }
            }
        }

        void destroy() {
            if (weakReference != null) {
                weakReference.clear();
                weakReference = null;
            }
        }
    }

    /**
     * 手机电话状态改变listener
     */
    public interface OnPhoneStateChangeListener {
        /**
         * 空闲
         */
        void stateIdel();

        /**
         * 挂起
         */
        void stateOff();

        /**
         * 响铃
         */
        void stateRinging();
    }

    /**
     * 设置电话listener
     * @param listener OnPhoneStateChangeListener
     */
    public void setOnPhoneStateChangeListener(OnPhoneStateChangeListener listener) {
        this.onPhoneStateChangeListener = listener;
    }

    /**
     * 手机状态监听反注册
     */
    public void unRegistPhoneStateListener() {
        if (myPhoneStateListener != null) {
            myPhoneStateListener.destroy();
            myPhoneStateListener = null;
        }

        if (weakReferenceContext != null) {
            weakReferenceContext.clear();
            weakReferenceContext = null;
        }
        onPhoneStateChangeListener = null;
    }
}
