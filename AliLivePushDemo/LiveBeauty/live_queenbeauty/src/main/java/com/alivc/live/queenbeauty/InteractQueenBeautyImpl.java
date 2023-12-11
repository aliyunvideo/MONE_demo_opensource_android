package com.alivc.live.queenbeauty;

import static android.opengl.GLES20.GL_FRAMEBUFFER;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.alivc.live.beauty.BeautyInterface;
import com.alivc.live.beauty.constant.BeautyImageFormat;
import com.aliyun.android.libqueen.aio.IBeautyParamsHolder;
import com.aliyun.android.libqueen.aio.QueenBeautyInterface;
import com.aliyun.android.libqueen.aio.QueenBeautyWrapper;
import com.aliyun.android.libqueen.aio.QueenFlip;
import com.aliyunsdk.queen.param.QueenParamHolder;

import java.nio.IntBuffer;

/**
 * 互动模式下，美颜实现相关类
 *
 * @note v4.4.4版本-v6.1.0版本，互动模式下的美颜，处理逻辑参考BeautySDKType.INTERACT_QUEEN，即：InteractQueenBeautyImpl；
 * @note v6.1.0以后的版本（从v6.2.0开始），基础模式下的美颜，和互动模式下的美颜，处理逻辑保持一致，即：QueenBeautyImpl；
 */
@Keep
public class InteractQueenBeautyImpl implements BeautyInterface {
    private static final String TAG = "InteractQueenBeautyImpl";

    private static final boolean FLAG_ENABLE_DEBUG_LOG = false;

    private final Context mContext;

    private QueenBeautyInterface mBeautyImpl;
    private int lastTextureWidth = 0;
    private int lastTextureHeight = 0;
    private boolean mLastTextureIsOes = false;
    private int mLastTextureId = -1;
    private float[] mLastMatrix = null;

    private int mDeviceOrientation = 0;
    private OrientationEventListener mOrientationListener;

    private volatile boolean isBeautyEnable = false;
    private volatile boolean isAlgDataRendered = false;
    private boolean isFrameSync = true;

    private long mShareGlContext;

    public InteractQueenBeautyImpl(Context context) {
        Log.i(TAG, "construct");
        mContext = context;
        initOrientationListener(context);
    }

    private void resetQueenEngine() {
        Log.i(TAG, "resetQueenEngine");
        destroyQueenEngine();

        initQueenEngine(mShareGlContext);
    }

    private void destroyQueenEngine() {
        if (mBeautyImpl != null) {
            mBeautyImpl.release();
            mBeautyImpl = null;
        }
    }

    private void initQueenEngine(long glContext) {
        if (mBeautyImpl == null) {
            mBeautyImpl = new QueenBeautyWrapper();
            mBeautyImpl.init(mContext, glContext);
            mBeautyImpl.setBeautyParams(new IBeautyParamsHolder() {
                @Override
                public void onWriteParamsToBeauty(Object o) {
                    QueenParamHolder.writeParamToEngine(o);
                }
            });
        }

        // 是否开启美颜SDK的debug日志
        if (mBeautyImpl != null && FLAG_ENABLE_DEBUG_LOG) {
            mBeautyImpl.enableDebugMode();
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void init(long glContext) {
        Log.i(TAG, "init: " + glContext);
        mShareGlContext = glContext;

        initQueenEngine(mShareGlContext);
    }

    @Override
    public void release() {
        Log.d(TAG, "release");

        destroyOrientationListener();

        destroyQueenEngine();

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
    public void setBeautyType(int type, boolean enable) {

    }

    @Override
    public void setBeautyParams(int type, float value) {

    }

    @Override
    public void setFaceShapeParams(int type, float value) {

    }

    @Override
    public void setMakeupParams(int type, String path) {

    }

    @Override
    public void setFilterParams(String path) {

    }

    @Override
    public void setMaterialParams(String path) {

    }

    @Override
    public void removeMaterialParams(String path) {

    }

    @Override
    public int onTextureInput(int inputTexture, int textureWidth, int textureHeight) {
        return inputTexture;
    }

    @Override
    public int onTextureInput(int inputTexture, int textureWidth, int textureHeight, float[] matrix, boolean isOES) {
        mLastTextureId = inputTexture;
        mLastMatrix = matrix;
        mLastTextureIsOes = isOES;

        Log.i(TAG, "onTextureInput [inputTexture: " + inputTexture + ", isOES: " + isOES);

        if (mBeautyImpl == null || !isBeautyEnable) {
            Log.w(TAG, "mMediaChainEngine == null || !isBeautyEnable");
            return inputTexture;
        }

        int width = textureWidth;
        int height = textureHeight;
        // 刷新当前镜头角度
        refreshCameraAngles();

        long now = SystemClock.uptimeMillis();
        if (!isOES) {
            if (outAngle == 90 || outAngle == 270) {// 右 out = 90 / 左 out = 270
                // 推流的输入纹理经过处理，非原始摄像头采集纹理，这里单独针对角度适配: 右 out = 90 / 左 out = 270
                inputAngle = outAngle;
                outAngle = (outAngle + 180) % 360;
                flipAxis = QueenFlip.kFlipY;
            } else { // 正 out = 180 / 倒立 out = 0
                inputAngle = outAngle;
                outAngle = 180-outAngle;
                flipAxis = QueenFlip.kFlipY;
            }
        }

        isAlgDataRendered = true;

        int result = mBeautyImpl.onProcessTexture(inputTexture, isOES, matrix,width, height, inputAngle, outAngle, flipAxis);

        if (FLAG_ENABLE_DEBUG_LOG) {
            Log.i(TAG, Thread.currentThread().getId() + " - " + "render : " + (SystemClock.uptimeMillis() - now) + "ms" +
                    ", textureW: " + width + ", textureH: " + height +
                    ", inputAngle: " + inputAngle + ", outAngle: " + outAngle +
                    ", cameraId: " + mCurCameraId);
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

            Log.d(TAG, "inputAngle" + inputAngle + ",outputAngle" + outputAngle);
//            mMediaChainEngine.updateInputDataAndRunAlg(image, format, width, height, stride, inputAngle, outputAngle, 0);

            mBeautyImpl.onProcessTextureAndBuffer(mLastTextureId, mLastTextureIsOes, mLastMatrix, width, height, inputAngle, outputAngle, 0, image, format);
        }
    }

    @Override
    public void onDrawFrame(long imageNativeBufferPtr, int format, int width, int height, int stride, int cameraId) {
//        if (mMediaChainEngine != null) {
//
//            if (cameraId != mCurCameraId) {
//                Camera.getCameraInfo(cameraId, mCameraInfo);
//                mCurCameraId = cameraId;
//            }
//
//            boolean isCameraFront = cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
//            if (isCameraFront) {
//                setCameraAngles4Front();
//            } else {
//                setCameraAngles4Back();
//            }
//
            Log.d(TAG, "onDrawFrame()-imgNativeBuffer，inputAngle=" + inputAngle + " ,outputAngle=" + outAngle + ", flipAxis=" + flipAxis + ", cameraId=" + cameraId);
//
//            if (isAlgDataRendered && !isFrameSync) {
//                long now = SystemClock.uptimeMillis();
//                // 1、测试发现，onDrawFrame接口和onTextureInput接口虽然是不同的线程回调，但会顺序执行;
//                //    onDrawFrame回调频率高于onTextureInput，所以保证算法结果被用于渲染之后，才会再次执行，否则会有一次冗余的算法过程，也会导致卡顿
//                // 2、新版本的queen不支持在非渲染线程执行，所以先在native侧将buffer拷贝存储起来，在渲染线程执行算法
//                long bufferSize = 0;
//                switch (format) {
//                    case BeautyImageFormat.kNV21:
//                        bufferSize = (long) (width * height * 1.5);
//                        break;
//                    case BeautyImageFormat.kRGB:
//                        bufferSize = width * height * 3;
//                        break;
//                    case BeautyImageFormat.kRGBA:
//                        bufferSize = width * height * 4;
//                        break;
//                    default:
//                        break;
//                }
//                if (bufferSize > 0) {
//                    mAlgNativeBufferPtr = mMediaChainEngine.copyNativeBuffer(imageNativeBufferPtr, bufferSize);
//                    mAlgDataFormat = format;
//                    mAlgDataWidth = width;
//                    mAlgDataHeight = height;
//                    nAlgDataStride = stride;
//                }
//            }
//        }
    }

    @Override
    public String getVersion() {
        return "";
    }


    @Override
    public void switchCameraId(int cameraId) {
        if (cameraId != mCurCameraId) {
            Camera.getCameraInfo(cameraId, mCameraInfo);
            mCurCameraId = cameraId;
        }
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

}