package com.alivc.live.interactive_common.manager;

import android.graphics.Bitmap;
import android.os.Handler;

import com.alivc.live.commonutils.BitmapUtil;
import com.alivc.live.commonutils.ContextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author keria
 * @date 2023/9/15
 * @brief 时间戳文字水印管理类
 * @note 每秒生成一帧文字水印的bitmap，回调给外层进行添加水印处理
 */
public class TimestampWatermarkManager {

    private static final long TIME_INTERVAL = 1000L;

    private final Handler sHandler = new Handler();

    private final Runnable sRunnable = new Runnable() {
        @Override
        public void run() {
            processEvent();
            sHandler.postDelayed(this, TIME_INTERVAL);
        }
    };

    private OnWatermarkListener mOnWatermarkListener;

    public void init(OnWatermarkListener listener) {
        mOnWatermarkListener = listener;
        startHandler();
    }

    public void destroy() {
        removeHandler();
        mOnWatermarkListener = null;
    }

    public void processEvent() {
        if (mOnWatermarkListener != null) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            Bitmap bitmap = BitmapUtil.createTextBitmap(ContextUtils.getContext(), timestamp);
            mOnWatermarkListener.onWatermarkUpdate(bitmap);
        }
    }

    private void startHandler() {
        sHandler.postDelayed(sRunnable, TIME_INTERVAL);
    }

    private void removeHandler() {
        sHandler.removeCallbacksAndMessages(null);
    }

    public interface OnWatermarkListener {
        public void onWatermarkUpdate(Bitmap bitmap);
    }
}
