package com.aliyun.svideo.beautyeffect.queen;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.aliyun.android.libqueen.aio.QueenImageFormat;
import com.aliyun.android.libqueen.aio.QueenConfig;
import com.aliyun.svideo.base.BaseChooser;
import com.aliyun.ugsv.auibeauty.BeautyInterface;
import com.aliyun.ugsv.auibeauty.IAliyunBeautyInitCallback;
import com.aliyun.ugsv.auibeauty.OnBeautyLayoutChangeListener;
import com.aliyun.ugsv.auibeauty.OnDefaultBeautyLevelChangeListener;
import com.aliyun.ugsv.auibeauty.api.constant.BeautyConstant;
import com.aliyun.ugsv.auibeauty.api.constant.BeautySDKType;

import com.aliyun.android.libqueen.aio.IBeautyParamsHolder;
import com.aliyun.android.libqueen.aio.QueenBeautyWrapper;
import com.aliyun.android.libqueen.aio.QueenBeautyInterface;
import com.aliyun.android.libqueen.aio.QueenFlip;
import com.aliyunsdk.queen.param.QueenParamHolder;


import java.lang.ref.WeakReference;

@Keep
public class QueenManager implements BeautyInterface {
    private static final String TAG = "QueenManager";
    private SimpleBytesBufPool mBytesBufPool;
    private int mDeviceOrientation;
    private int mDisplayOrientation;
    private WeakReference<Context> mContextWeakReference;
    private QueenBeautyMenu mQueenBeautyMenu;
    private Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
    /**
     * 相机的原始NV21数据
     */
    private byte[] frameBytes;

    private QueenBeautyInterface mBeautyImpl;

    public QueenManager() {
    }


    @Override
    public void init(final Context context, final IAliyunBeautyInitCallback iAliyunBeautyInitCallback) {
        mContextWeakReference = new WeakReference<>(context);
        if (iAliyunBeautyInitCallback != null) {
            iAliyunBeautyInitCallback.onInit(BeautyConstant.BEAUTY_INIT_SUCCEED);
        }
    }

    /**
     * SDK初始化,
     */
    public void initEngine() {
        if (mBeautyImpl == null) {
            mBeautyImpl = new QueenBeautyWrapper();
            QueenConfig queenConfig = new QueenConfig();
//            queenConfig.enableDebugLog = true;
            mBeautyImpl.init(getContext(), queenConfig);
//            mBeautyImpl.init(getContext());
            mBeautyImpl.setBeautyParams(new IBeautyParamsHolder() {
                @Override
                public void onWriteParamsToBeauty(Object o) {
                    QueenParamHolder.writeParamToEngine(o);
                }
            });
        }
    }

    @Nullable
    private Context getContext() {
        return mContextWeakReference != null?mContextWeakReference.get():null;
    }

    @Override
    public void initParams() {
        // 开启显示手势点位、美体点位
//        QueenRuntime.sHandDetectDebug = true;
//        QueenRuntime.sBodyDetectDebug = true;
        // 开启手势识别的回调
//        QueenParamHolder.getQueenParam().gestureRecord.setAlgListener(new Algorithm.OnAlgDetectListener() {
//            @Override
//            public int onAlgDetectFinish(int algId, Object algResult) {
//                final StringBuilder builder = new StringBuilder();
//                if (algResult instanceof com.aliyun.android.libqueen.algorithm.GestureData) {
//                    com.aliyun.android.libqueen.algorithm.GestureData gestureData = (com.aliyun.android.libqueen.algorithm.GestureData) algResult;
//                    for (int i = 0; i < gestureData.getGestures().length; i++) {
//                        int type = gestureData.getGestures()[i];
//                        String name = com.aliyun.android.libqueen.models.HandGestureType.getName(type);
//                        builder.append(name).append("-");
//                    }
//                    android.util.Log.d("Queen_DEBUG", "===gesture=" + builder.toString());
//                }
//                return 0;
//            }
//        });
    }

    @Override
    public void onFrameBack(byte[] bytes, int width, int height, Camera.CameraInfo info) {
        frameBytes = bytes;
        mCameraInfo = info;
        updateBytesBufPool(width, height, bytes);
    }


    @Override
    public int onTextureIdBack(int textureId, int textureWidth, int textureHeight, float[] matrix, int currentCameraType) {
        int resultTextureId = textureId;
        initEngine();

        if (mBeautyImpl == null) {
            Log.e("QueenBeautyImpl", "mMediaChainEngine == null || !isBeautyEnable");
            return resultTextureId;
        }

        byte[] lastBuffer = null;
        if (mBytesBufPool != null) {
            updateBytesBufPool(frameBytes);
            lastBuffer = mBytesBufPool.getLastBuffer();
        }
        if (lastBuffer != null) {
            int inputAngle = getInputAngle(mCameraInfo);
            int outputAngle = getOutputAngle(mCameraInfo);
            int flip = getFlipAxis(mCameraInfo);
            boolean isOesTexture = true;
            resultTextureId = mBeautyImpl.onProcessTextureAndBuffer(textureId, isOesTexture, matrix, textureWidth, textureHeight,
                    inputAngle, outputAngle, flip, lastBuffer, QueenImageFormat.NV21);

            mBytesBufPool.releaseBuffer(lastBuffer);
        }

        return resultTextureId;
    }

    private void updateBytesBufPool(int width, int height, byte[] bytes) {
        if (mBytesBufPool == null) {
            int byteSize = width * height * android.graphics.ImageFormat.getBitsPerPixel(android.graphics.ImageFormat.NV21) / 8;
            mBytesBufPool = new SimpleBytesBufPool(3, byteSize);
        }
        updateBytesBufPool(bytes);
    }

    private void updateBytesBufPool(byte[] bytes) {
        mBytesBufPool.updateBuffer(bytes);
        mBytesBufPool.reusedBuffer();
    }

    @Override
    public void setDeviceOrientation(int deviceOrientation, int displayOrientation) {
        if (deviceOrientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return;
        }
        Camera.getCameraInfo(mCameraInfo.facing, mCameraInfo);
        deviceOrientation = (deviceOrientation + 45) / 90 * 90;

        this.mDeviceOrientation = deviceOrientation;
        this.mDisplayOrientation = displayOrientation;
    }

    @Override
    public void showControllerView(FragmentManager fragmentManager, final OnBeautyLayoutChangeListener onBeautyLayoutChangeListener) {
        final Context context = mContextWeakReference.get();
        if (context == null || fragmentManager == null) {
            return;
        }
        if (mQueenBeautyMenu == null) {
            mQueenBeautyMenu = new QueenBeautyMenu();
            mQueenBeautyMenu.setDismissListener(new BaseChooser.DialogVisibleListener() {
                @Override
                public void onDialogDismiss() {
                    if (onBeautyLayoutChangeListener != null) {
                        onBeautyLayoutChangeListener.onLayoutChange(View.GONE);
                    }
                }

                @Override
                public void onDialogShow() {
                    if (onBeautyLayoutChangeListener != null) {
                        onBeautyLayoutChangeListener.onLayoutChange(View.VISIBLE);
                    }
                }
            });
        }
        mQueenBeautyMenu.show(fragmentManager, "queen");
    }

    @Override
    public void addDefaultBeautyLevelChangeListener(OnDefaultBeautyLevelChangeListener onDefaultBeautyLevelChangeListener) {

    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public BeautySDKType getSdkType() {
        return BeautySDKType.QUEEN;
    }

    private int getOutputAngle(Camera.CameraInfo cameraInfo) {
        boolean isFont = cameraInfo.facing != Camera.CameraInfo.CAMERA_FACING_BACK;
        int angle = isFont ? (360 - mDeviceOrientation) % 360 : mDeviceOrientation % 360;
        return (angle - mDisplayOrientation + 360) % 360;
    }


    private int getInputAngle(Camera.CameraInfo cameraInfo) {
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (360 + cameraInfo.orientation - mDeviceOrientation) % 360;
        } else {
            return (cameraInfo.orientation + mDeviceOrientation) % 360;
        }
    }

    private int getFlipAxis(Camera.CameraInfo cameraInfo) {
        int mFlipAxis;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mFlipAxis = QueenFlip.kNone;
        } else {
            mFlipAxis = QueenFlip.kFlipY;
        }
        return mFlipAxis;
    }

    @Override
    public void setDebug(boolean isDebug) {
        if (mBeautyImpl != null) {
            mBeautyImpl.enableDebugMode();
        }
    }

    @Override
    public void release() {
        if (mBeautyImpl != null) {
            mBeautyImpl.release();
            mBeautyImpl = null;
        }
        // 还原所有美颜相关参数
        QueenParamHolder.relaseQueenParams();
    }
}
