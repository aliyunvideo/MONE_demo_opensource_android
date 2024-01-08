package com.alivc.live.queenbeauty;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.alivc.live.beauty.BeautyInterface;
import com.aliyun.android.libqueen.aio.IBeautyParamsHolder;
import com.aliyun.android.libqueen.aio.QueenBeautyInterface;
import com.aliyun.android.libqueen.aio.QueenBeautyWrapper;
import com.aliyun.android.libqueen.aio.QueenConfig;
import com.aliyun.android.libqueen.aio.QueenFlip;
import com.aliyunsdk.queen.param.QueenParam;
import com.aliyunsdk.queen.param.QueenParamFactory;
import com.aliyunsdk.queen.param.QueenParamHolder;

import java.util.LinkedList;
import java.util.List;

/**
 * 美颜实现相关类
 *
 * @note v4.4.4~v6.1.0：基础直播下的美颜，处理逻辑参考BeautySDKType.QUEEN，即：QueenBeautyImpl；互动直播下的美颜，处理逻辑参考BeautySDKType.INTERACT_QUEEN，即：InteractQueenBeautyImpl；
 * @note v6.2.0~v6.6.0：互动直播下的美颜，与基础直播下的美颜，完成统一，处理逻辑保持一致，即：QueenBeautyImpl；
 * @note v6.7.0开始，一体化SDK只包含基础美颜功能，高级美颜功能需要单独集成美颜SDK，详见模块文档；
 */
@Keep
public class QueenBeautyImpl implements BeautyInterface {
    private static final String TAG = "QueenBeautyImpl";

    private static final boolean FLAG_ENABLE_DEBUG_LOG = false;

    private final Context mContext;

    private final Object mCmdLock = new Object();
    private final List<Object> mCmdList = new LinkedList<>();

    private long glThreadId = -1;

    private QueenBeautyInterface mBeautyImpl;
    private boolean mLastTextureIsOes = false;
    private int mLastTextureId = -1;
    private float[] mLastMatrix = null;

    private int lastTextureWidth = 0;
    private int lastTextureHeight = 0;

    private int mDeviceOrientation = 0;
    private OrientationEventListener mOrientationListener;

    private volatile boolean isBeautyEnable = false;
    private volatile boolean isAlgDataRendered = false;
    private boolean isFrameSync = true;
    private boolean isLicenseValid = true;

    public QueenBeautyImpl(Context context) {
        mContext = context;
        initOrientationListener(context);
    }

    @Override
    public void init() {
        if (mBeautyImpl == null) {
            mBeautyImpl = new QueenBeautyWrapper();
            QueenConfig queenConfig = new QueenConfig();
//            queenConfig.enableDebugLog = true;
            mBeautyImpl.init(mContext, queenConfig);
//            mBeautyImpl.init(mContext);
            mBeautyImpl.setBeautyParams(new IBeautyParamsHolder() {
                @Override
                public void onWriteParamsToBeauty(Object o) {
                    QueenParamHolder.writeParamToEngine(o);
                }
            });
            isBeautyEnable = true;
        }

        // 是否开启美颜SDK的debug日志
        if (mBeautyImpl != null && FLAG_ENABLE_DEBUG_LOG) {
            mBeautyImpl.enableDebugMode();
        }

        // engine初始化后，更新当前默认的美颜效果
        refreshDefaultQueenParam();
    }

    /**
     * engine初始化后，更新当前默认的美颜效果
     *
     * @note 请自行调整直播状态下的初始美颜效果
     */
    private void refreshDefaultQueenParam() {
        QueenParam defaultQueenParam = QueenParamHolder.getQueenParam();
        // 更新默认美颜为流行风格
        defaultQueenParam.basicBeautyRecord = QueenParamFactory.BeautyParams.getParams(QueenParamFactory.BeautyParams.ID_SIMPLE);
        // 更新默认美型为可爱风格
        defaultQueenParam.faceShapeRecord = QueenParamFactory.FaceShapeParams.getParams(QueenParamFactory.FaceShapeParams.TAG_SHAPE_AUTO);
        // 若需要微调细节参数-磨皮
        defaultQueenParam.basicBeautyRecord.skinBuffingParam = 0.4f;
        // 若需要微调细节参数-美白
        defaultQueenParam.basicBeautyRecord.skinWhitingParam = 0.6f;
    }

    @Override
    public void release() {
        Log.d(TAG, "release");

        destroyOrientationListener();

        if (mBeautyImpl != null) {
            mBeautyImpl.release();
            mBeautyImpl = null;
        }

        // 重置美颜设置，美颜参数效果在内部以静态变量形式保存
        QueenParamHolder.relaseQueenParams();

        isBeautyEnable = false;
    }

    @Override
    public void setBeautyEnable(boolean enable) {
        Log.d(TAG, "setBeautyEnable: " + enable);
        isBeautyEnable = enable;
    }

    @Override
    public int onTextureInput(int inputTexture, int textureWidth, int textureHeight) {
        // 判断美颜license是否生效，如果不生效，不做美颜处理
        if (!isLicenseValid) {
            Log.e(TAG, "license for queen sdk is invalid!");
            return inputTexture;
        }

        glThreadId = Thread.currentThread().getId();

        if (mBeautyImpl == null || !isBeautyEnable) {
            return inputTexture;
        }

        mLastTextureId = inputTexture;
        mLastTextureIsOes = false;

        // 刷新当前镜头角度
        refreshCameraAngles();

        long now = SystemClock.uptimeMillis();
        if (outAngle == 90 || outAngle == 270) {// 右 out = 90 / 左 out = 270
            // 推流的输入纹理经过处理，非原始摄像头采集纹理，这里单独针对角度适配: 右 out = 90 / 左 out = 270
            inputAngle = outAngle;
            outAngle = (outAngle + 180) % 360;
        } else { // 正 out = 180 / 倒立 out = 0
            // 解决抠图和美发头像上下翻转的问题
            // 推流的输入纹理经过处理，非原始摄像头采集纹理，这里单独针对角度适配: 正 out = 180 : 倒立 out = 0
            inputAngle = outAngle;
            outAngle = 180 - outAngle;
        }

        isAlgDataRendered = true;

        int result = mBeautyImpl.onProcessTexture(inputTexture, false, null, textureWidth, textureHeight, inputAngle, outAngle, 2);

        if (FLAG_ENABLE_DEBUG_LOG) {
            Log.i(TAG, Thread.currentThread().getId() + " - " + "render : " + (SystemClock.uptimeMillis() - now) + "ms"
                    + ", textureW: " + textureWidth + ", textureH: " + textureHeight + ", outAngle: " + outAngle);
        }
        return result;
    }

    @Override
    public void onDrawFrame(byte[] image, int format, int width, int height, int stride) {
        if (mBeautyImpl != null && isBeautyEnable) {
            // @keria, reference: https://www.atatech.org/articles/123323
            // inputAngle, outputAngle, flipAxis，太TM绕了...(〒︿〒)
            int displayOrientation = getDisplayOrientation();

            int inputAngle = 0;
            int outputAngle = 0;

            if (displayOrientation == 90 || displayOrientation == 270) { //横屏
                inputAngle = (270 - mDeviceOrientation + 360) % 360;
                outputAngle = (displayOrientation - mDeviceOrientation + 360) % 360;
            } else if (displayOrientation == 0 || displayOrientation == 180) { //竖屏
                inputAngle = (270 - mDeviceOrientation + 360) % 360;
                outputAngle = (180 + displayOrientation - mDeviceOrientation + 360) % 360;
            }

            Log.d(TAG, "inputAngle=" + inputAngle + ", outputAngle=" + outputAngle);

//            mMediaChainEngine.updateInputDataAndRunAlg(image, format, width, height, stride, inputAngle, outputAngle, 0);
            mBeautyImpl.onProcessTextureAndBuffer(mLastTextureId, mLastTextureIsOes, mLastMatrix, width, height, inputAngle, outputAngle, 0, image, format);
        }
    }

    @Override
    public String getVersion() {
        return "";
    }

    @Override
    public void switchCameraId(int cameraId) {
        mCurCameraId = cameraId;
        boolean isCameraFront = mCurCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
        if (isCameraFront) {
            setCameraAngles4Front();
        } else {
            setCameraAngles4Back();
        }
    }

    private Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
    private int mCurCameraId = -1;
    private int inputAngle;
    private int outAngle;
    private int flipAxis;
//    private long mAlgNativeBufferPtr;
//    private int mAlgDataFormat, mAlgDataWidth, mAlgDataHeight, nAlgDataStride;

    private void refreshCameraAngles() {
        switchCameraId(mCurCameraId);
    }

    private void setCameraAngles4Back() {
        int displayOrientation = getDisplayOrientation();
        inputAngle = (mCameraInfo.orientation + mDeviceOrientation) % 360;
        int angle = mDeviceOrientation % 360;
        outAngle = (angle - displayOrientation + 360) % 360;

        if (displayOrientation == 0 || displayOrientation == 180) { //竖屏
            outAngle = (180 + displayOrientation - mDeviceOrientation + 360) % 360;
            if (mDeviceOrientation % 180 == 90) {
                outAngle = (180 + mDeviceOrientation) % 360;
            }
        }
        flipAxis = QueenFlip.kFlipY;
    }

    private void setCameraAngles4Front() {
        // @keria, reference: https://www.atatech.org/articles/123323
        // inputAngle, outputAngle, flipAxis，太TM绕了...(〒︿〒)

        inputAngle = 0;
        outAngle = 0;

        int displayOrientation = getDisplayOrientation();
        if (displayOrientation == 90 || displayOrientation == 270) { //横屏
            inputAngle = (270 - mDeviceOrientation + 360) % 360;
            outAngle = (displayOrientation - mDeviceOrientation + 360) % 360;
        } else if (displayOrientation == 0 || displayOrientation == 180) { //竖屏
            inputAngle = (270 - mDeviceOrientation + 360) % 360;
            outAngle = (180 + displayOrientation - mDeviceOrientation + 360) % 360;
        }

        flipAxis = QueenFlip.kFlipY;
    }


    // TODO: Warning: patch code, need to be replaced next version. We should get camera orientation by texture callback.
    private void initOrientationListener(@NonNull Context context) {
        mOrientationListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                // This is a method called frequently, which is bad for performance.
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;
                }
                orientation = (orientation + 45) / 90 * 90;
                if (mDeviceOrientation != orientation) {
                    // 不能在此处更改mDisplayOrientation，屏幕旋转可能还未生效
                    Log.d(TAG, "Orientation Changed! displayOrientation: " + mDeviceOrientation + "->" + orientation);
                    mDeviceOrientation = orientation;
                }
            }
        };

        if (mOrientationListener.canDetectOrientation()) {
            Log.d(TAG, "Can detect orientation");
            mOrientationListener.enable();
        } else {
            Log.d(TAG, "Cannot detect orientation");
            mOrientationListener.disable();
        }
    }

    private void destroyOrientationListener() {
        if (mOrientationListener != null) {
            mOrientationListener.disable();
            mOrientationListener = null;
        }
    }

    private int getDisplayOrientation() {
        int displayOrientation = 0;
        if (mContext != null) {
            int angle = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            switch (angle) {
                case Surface.ROTATION_0:
                    displayOrientation = 0;
                    break;
                case Surface.ROTATION_90:
                    displayOrientation = 90;
                    break;
                case Surface.ROTATION_180:
                    displayOrientation = 180;
                    break;
                case Surface.ROTATION_270:
                    displayOrientation = 270;
                    break;
                default:
                    break;
            }
        }
        return displayOrientation;
    }

    // 如果引擎未创建或者不是texture线程，先缓存设置
    private boolean isCurrentTextureThread(Object cmd) {
        long currentThreadId = Thread.currentThread().getId();
        if (mBeautyImpl != null && glThreadId == currentThreadId) {
            return true;
        }

        Log.w(TAG, "now not in texture thread " + glThreadId + ", " + currentThreadId);
        synchronized (mCmdLock) {
            mCmdList.add(cmd);
        }
        return false;
    }

}