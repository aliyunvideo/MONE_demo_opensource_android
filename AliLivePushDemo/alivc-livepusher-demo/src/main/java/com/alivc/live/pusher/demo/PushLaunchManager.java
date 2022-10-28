package com.alivc.live.pusher.demo;

import static android.os.Environment.MEDIA_MOUNTED;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alivc.live.pusher.AlivcLiveBase;
import com.alivc.live.pusher.AlivcLiveBaseListener;
import com.alivc.live.pusher.AlivcLivePushConstants;
import com.alivc.live.pusher.AlivcLivePushInstance;
import com.alivc.live.pusher.AlivcLivePushLogLevel;
import com.aliyun.aio.utils.ThreadUtils;
import com.aliyun.animoji.AnimojiDataFactory;
import com.aliyun.animoji.utils.LogUtils;
import com.aliyunsdk.queen.menu.download.BeautyMenuMaterial;

import java.io.File;

/**
 * Created by keria on 2022/10/12.
 * <p>
 * 直播推流启动类
 *
 * @see <a href="https://www.aliyun.com/product/live">阿里云视频直播官网</a>
 * <p>
 * @see <a href="https://help.aliyun.com/document_detail/431730.html">推流SDK License文档</a>
 * @see <a href="https://help.aliyun.com/document_detail/449478.html">直播连麦文档</a>
 * @see <a href="https://help.aliyun.com/document_detail/97659.html">推流SDK文档</a>
 * <p>
 * @see <a href="https://www.aliyun.com/activity/cdn/video/rtc_race">美颜特效SDK官网</a>
 * @see <a href="https://help.aliyun.com/document_detail/211046.html">美颜特效SDK文档</a>
 */
public class PushLaunchManager {
    private static final String TAG = PushLaunchManager.class.getSimpleName();

    /**
     * 启动所有任务
     *
     * @param context android context
     */
    public static void launch4All(@NonNull Context context) {
        launch4LivePushDemo(context);
        registerPushSDKLicense(context);
        launch4Log(context);
        launch4Animoji(context);
        launch4QueenBeauty(context);
    }

    /**
     * Demo Asset加载等
     *
     * @param context android context
     */
    private static void launch4LivePushDemo(@NonNull Context context) {
        ThreadUtils.runOnSubThread(() -> {
            Common.copyAsset(context.getApplicationContext());
            Common.copyAll(context.getApplicationContext());
        });
    }

    /**
     * 推流SDK申请License
     *
     * @param context android context
     * @see <a href="https://help.aliyun.com/document_detail/431730.html">推流SDK License文档</a>
     */
    private static void registerPushSDKLicense(@NonNull Context context) {
        AlivcLivePushInstance.loadInstance(context);
        AlivcLiveBase.setListener(new AlivcLiveBaseListener() {
            @Override
            public void onLicenceCheck(AlivcLivePushConstants.AlivcLiveLicenseCheckResultCode alivcLiveLicenseCheckResultCode, String s) {
                Log.e(TAG, "onLicenceCheck: " + alivcLiveLicenseCheckResultCode + ", " + s);
            }
        });
        AlivcLiveBase.registerSDK();
    }

    /**
     * 日志配置
     * <p>
     * 在debug环境下，使用console日志；release环境下，使用file日志；
     *
     * @param context android context
     */
    private static void launch4Log(@NonNull Context context) {
        AlivcLiveBase.setLogLevel(AlivcLivePushLogLevel.AlivcLivePushLogLevelInfo);
        AlivcLiveBase.setConsoleEnabled(BuildConfig.DEBUG);
        if (!BuildConfig.DEBUG) {
            // adb pull /sdcard/Android/data/com.alivc.live.pusher.demo/files/Ali_RTS_Log/ ~/Downloads
            AlivcLiveBase.setConsoleEnabled(false);
            String logPath = getLogFilePath(context.getApplicationContext(), null);
            // full log file limited was kLogMaxFileSizeInKB * 5 (parts)
            int maxPartFileSizeInKB = 100 * 1024 * 1024; //100G
            AlivcLiveBase.setLogDirPath(logPath, maxPartFileSizeInKB);
        }
    }

    /**
     * Animoji资源加载等
     *
     * @param context android context
     * @see <a href="https://www.aliyun.com/activity/cdn/video/rtc_race">美颜特效SDK官网</a>
     * @see <a href="https://help.aliyun.com/document_detail/211046.html">美颜特效SDK文档</a>
     */
    private static void launch4Animoji(@NonNull Context context) {
        AnimojiDataFactory.INSTANCE.loadResources(context);
        LogUtils.init(true);
    }

    /**
     * Queen美颜资源加载等
     *
     * @param context android context
     * @see <a href="https://www.aliyun.com/activity/cdn/video/rtc_race">美颜特效SDK官网</a>
     * @see <a href="https://help.aliyun.com/document_detail/211046.html">美颜特效SDK文档</a>
     */
    private static void launch4QueenBeauty(@NonNull Context context) {
        BeautyMenuMaterial.getInstance().prepare(context);
    }

    private static String getLogFilePath(@NonNull Context context, String dir) {
        String logFilePath = "";
        //判断SD卡是否可用
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            logFilePath = context.getExternalFilesDir(dir).getAbsolutePath();
        } else {
            //没内存卡就存机身内存
            logFilePath = context.getFilesDir() + File.separator + dir;
        }
        File file = new File(logFilePath);
        if (!file.exists()) {//判断文件目录是否存在
            file.mkdirs();
        }

        Log.d(TAG, "log file path: " + logFilePath);
        return logFilePath;
    }
}
