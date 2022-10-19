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

import com.aliyun.android.libqueen.Algorithm;
import com.aliyun.svideo.base.BaseChooser;
import com.aliyun.ugsv.auibeauty.BeautyInterface;
import com.aliyun.ugsv.auibeauty.IAliyunBeautyInitCallback;
import com.aliyun.ugsv.auibeauty.OnBeautyLayoutChangeListener;
import com.aliyun.ugsv.auibeauty.OnDefaultBeautyLevelChangeListener;
import com.aliyun.ugsv.auibeauty.api.constant.BeautyConstant;
import com.aliyun.ugsv.auibeauty.api.constant.BeautySDKType;
import com.aliyunsdk.queen.param.QueenParamHolder;

import com.aliyun.android.libqueen.ImageFormat;
import com.aliyun.android.libqueen.QueenEngine;
import com.aliyun.android.libqueen.Texture2D;
import com.aliyun.android.libqueen.exception.InitializationException;
import com.aliyun.android.libqueen.models.Flip;
import com.aliyunsdk.queen.param.QueenRuntime;

import java.lang.ref.WeakReference;

@Keep
public class QueenManager implements BeautyInterface {
    private static final String TAG = "QueenManager";
    private QueenEngine mQueenEngine;
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

    private Texture2D mTexture2D;

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
    public Texture2D initEngine(boolean toScreen, int textureId, int textureWidth, int textureHeight, boolean isEOS) {
        Texture2D texture2D = null;
        try {
           Context context  = getContext();
            if (context == null){
                Log.e(TAG, "initEngine: context is null" );
                return null;
            }
            if (mQueenEngine == null) {
                mQueenEngine = new QueenEngine(context, toScreen);
                mQueenEngine.setPowerSaving(true);
                mQueenEngine.setInputTexture(textureId, textureHeight, textureWidth, isEOS);
                texture2D = updateOutTexture(textureHeight, textureWidth);
                mQueenEngine.setScreenViewport(0, 0, textureHeight, textureWidth);

                mQueenEngine.setSegmentInfoFlipY(true);
            }
        } catch (InitializationException e) {
            e.printStackTrace();
        }

        return texture2D;
    }

    @Nullable
    private Context getContext() {
        return mContextWeakReference != null?mContextWeakReference.get():null;
    }

    @Override
    public void initParams() {
        // 开启显示手势点位、美体点位
        QueenRuntime.sHandDetectDebug = true;
        QueenRuntime.sBodyDetectDebug = true;
        // 开启手势识别的回调
        QueenParamHolder.getQueenParam().gestureRecord.setAlgListener(new Algorithm.OnAlgDetectListener() {
            @Override
            public int onAlgDetectFinish(int algId, Object algResult) {
                final StringBuilder builder = new StringBuilder();
                if (algResult instanceof com.aliyun.android.libqueen.algorithm.GestureData) {
                    com.aliyun.android.libqueen.algorithm.GestureData gestureData = (com.aliyun.android.libqueen.algorithm.GestureData) algResult;
                    for (int i = 0; i < gestureData.getGestures().length; i++) {
                        int type = gestureData.getGestures()[i];
                        String name = com.aliyun.android.libqueen.models.HandGestureType.getName(type);
                        builder.append(name).append("-");
                    }
                    android.util.Log.d("Queen_DEBUG", "===gesture=" + builder.toString());
                }
                return 0;
            }
        });
    }

    @Override
    public void onFrameBack(byte[] bytes, int width, int height, Camera.CameraInfo info) {
        frameBytes = bytes;
        mCameraInfo = info;
        updateBytesBufPool(width, height, bytes);
    }


    @Override
    public int onTextureIdBack(int textureId, int textureWidth, int textureHeight, float[] matrix,int currentCameraType) {
        if(mTexture2D == null){
            mTexture2D = initEngine(false, textureId, textureWidth, textureHeight, true);
        }

        byte[] lastBuffer = null;
        if (mBytesBufPool != null) {
            updateBytesBufPool(frameBytes);
            lastBuffer = mBytesBufPool.getLastBuffer();
        }
        if (lastBuffer != null) {
            mQueenEngine.updateInputDataAndRunAlg(lastBuffer, ImageFormat.NV21,textureWidth,textureHeight,0,getInputAngle(mCameraInfo),getOutputAngle(mCameraInfo),
                   getFlipAxis(mCameraInfo));
            mBytesBufPool.releaseBuffer(lastBuffer);
        }
        QueenParamHolder.writeParamToEngine(mQueenEngine, false);

        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        int retCode = mQueenEngine.renderTexture(matrix);
        Log.d(TAG, "onTextureIdBack: retCode:" + retCode);
        // QUEEN_INVALID_LICENSE(-9)，表示证书校验失败
       // QUEEN_NO_EFFECT(-10)，表示全部特效功能关闭
        if (retCode != -9 && retCode != -10) {
            if (mTexture2D != null) {
                textureId = mTexture2D.getTextureId();
            }
        }

        return textureId;
    }

    private Texture2D updateOutTexture(int textureWidth, int textureHeight) {
        Texture2D texture2D = null;
        // 非必要步骤：获得美颜输出纹理，可以在用于其他扩展业务
        if (mQueenEngine != null) {
            texture2D = mQueenEngine.autoGenOutTexture(true);
            if (texture2D != null) {
                mQueenEngine.updateOutTexture(texture2D.getTextureId(), textureWidth, textureHeight, true);
            }
        }
        return texture2D;
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
            mFlipAxis = Flip.kNone;
        } else {
            mFlipAxis = Flip.kFlipY;
        }
        return mFlipAxis;
    }

    @Override
    public void setDebug(boolean isDebug) {
        if (mQueenEngine != null) {
            mQueenEngine.enableDebugLog();
        }
    }

    @Override
    public void release() {
        if (mQueenEngine != null) {
            mQueenEngine.release();
            mQueenEngine = null;
            mTexture2D = null;
        }
        // 还原所有美颜相关参数
        QueenParamHolder.relaseQueenParams();
    }
}
