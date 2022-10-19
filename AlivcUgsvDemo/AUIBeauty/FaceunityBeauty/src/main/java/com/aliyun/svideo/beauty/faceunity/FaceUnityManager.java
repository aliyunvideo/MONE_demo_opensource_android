package com.aliyun.svideo.beauty.faceunity;

import android.content.Context;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Keep;
import androidx.fragment.app.FragmentManager;

import com.aliyun.svideo.base.BaseChooser;
import com.aliyun.ugsv.auibeauty.BeautyInterface;
import com.aliyun.ugsv.auibeauty.IAliyunBeautyInitCallback;
import com.aliyun.ugsv.auibeauty.OnBeautyLayoutChangeListener;
import com.aliyun.ugsv.auibeauty.OnDefaultBeautyLevelChangeListener;
import com.aliyun.ugsv.auibeauty.api.constant.BeautyConstant;
import com.aliyun.ugsv.auibeauty.api.constant.BeautySDKType;
import com.aliyun.svideo.beauty.faceunity.bean.FaceUnityBeautyParams;
import com.aliyun.svideo.beauty.faceunity.constant.FaceUnityBeautyConstants;
import com.aliyun.svideo.beauty.faceunity.inteface.OnBeautyChooserCallback;
import com.aliyun.svideo.beauty.faceunity.util.SharedPreferenceUtils;
import com.aliyun.svideo.beauty.faceunity.view.FaceUnityBeautyChooser;
import com.aliyun.svideo.beauty.faceunity.view.face.BeautyFaceDetailSettingChooser;
import com.aliyun.svideo.beauty.faceunity.view.skin.BeautyShapeDetailChooser;
import com.aliyun.ugsv.common.utils.ThreadUtils;
import com.faceunity.wrapper.faceunity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * FaceUnify管理类，支持高级美颜(美白，磨皮，红润调整)，美型(脸型，大眼，瘦脸)
 *
 * @author Mulberry
 * create on 2018/7/11.
 */
@Keep
public class FaceUnityManager implements BeautyInterface, OnBeautyChooserCallback {
    private static final String TAG = "FaceUnityManager";
    private Object obj = new Object();
    /**
     * 美颜道具
     */
    private int mFaceBeautyItem = 0;


    /**
     * 美白
     */
    private float mFaceBeautyColorLevel = 0.2f;
    /**
     * 磨皮
     */
    private float mFaceBeautyBlurLevel = 6.0f;
    /**
     * 精准磨皮,传0和1的值是指是否开启精准磨皮
     */
    private float mFaceBeautyALLBlurLevel = 0.0f;
    /**
     * 红润
     */
    private float mFaceBeautyRedLevel = 0.5f;
    /**
     * 瘦脸
     */
    private float mFaceBeautyCheekThin = 1.0f;
    /**
     * 大眼
     */
    private float mFaceBeautyEnlargeEye = 0.5f;
    /**
     * 美型，脸型选择，有0，1，2，3, 4值可选
     * 默认（3）、女神（0）、网红（1）、自然（2）、自定义（4）。
     */
    private int mFaceShape = 4;
    /**
     * 美型程度 0-1的值
     */
    private float mFaceShapeLevel = 0.5f;
    private boolean isInit;//是否初始化
    private IAliyunBeautyInitCallback mIAliyunBeautyInitCallback;
    private WeakReference<Context> mContextWeakReference;
    private FaceUnityBeautyChooser mFaceUnityBeautyChooser;
    private OnBeautyLayoutChangeListener mOnBeautyLayoutChangeListener;
    private BeautyFaceDetailSettingChooser mBeautyFaceDetailSettingChooser;
    private FragmentManager mFragmentManager;
    private BeautyShapeDetailChooser mBeautyShapeDetailChooser;
    //用户微调后的参数
    private Map<Integer, FaceUnityBeautyParams> mCustomParams = null;
    /**
     * 美肌美颜微调dialog是否正在显示
     */
    private boolean isBeautyDetailShowing = false;
    private int mBeautyFaceLevel;
    private int mBeautySkinLevel;
    //为当前帧数序号，重新初始化后需要从0开始
    private int frameId = 0;
    /**
     * 相机的原始NV21数据
     */
    private byte[] frameBytes;
    /**
     * 原始数据宽
     */
    private int frameWidth;
    /**
     * 原始数据高
     */
    private int frameHeight;

    public FaceUnityManager() {
    }

    @Override
    public void init(Context context, IAliyunBeautyInitCallback iAliyunBeautyInitCallback) {
        mIAliyunBeautyInitCallback = iAliyunBeautyInitCallback;
        mContextWeakReference = new WeakReference<>(context);
        frameId = 0;
        if (isInit) {
            if (iAliyunBeautyInitCallback != null) {
                iAliyunBeautyInitCallback.onInit(BeautyConstant.BEAUTY_INIT_SUCCEED);
            }
        } else {
            ThreadUtils.runOnSubThread(initRunnable);
        }

    }

    private final Runnable initRunnable  = new Runnable() {
        @Override
        public  void run() {
            InputStream inputStream = null;
            try {
                Context context = mContextWeakReference.get();
                if (context == null) {
                    return;
                }
                synchronized (obj) {
                    if (!FaceUnityBeautyConstants.isInit) {
                        inputStream = context.getAssets().open("v3.bundle");
                        byte[] v3data = new byte[inputStream.available()];
                        int len = inputStream.read(v3data);
                        inputStream.close();

                        /**
                         * SDK初始化
                         */
                        faceunity.fuSetup(v3data, null, authpack.A());
                        Log.e(TAG, "fuSetup v3 len " + len);
                        Log.e(TAG, "fuGetVersion:" + faceunity.fuGetVersion());
                        FaceUnityBeautyConstants.isInit = true;
                    }
                }
                isInit = createBeautyItem(context);
            } catch (IOException e) {
                Log.e(TAG, "run: ", e);
                isInit = false;
            }
            ThreadUtils.runOnUiThread(initCallBackRunnable);
        }
    };
    private final Runnable initCallBackRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIAliyunBeautyInitCallback != null) {
                mIAliyunBeautyInitCallback.onInit(isInit ? BeautyConstant.BEAUTY_INIT_SUCCEED : BeautyConstant.BEAUTY_INIT_ERROR);
            }
        }
    };

    /**
     * 创建美颜相关
     *
     * @param context
     * @return
     */
    public boolean createBeautyItem(Context context) {
        InputStream inputStream = null;
        /**
         * 美颜初始化
         */
        try {
            inputStream = context.getAssets().open("face_beautification.bundle");
            byte[] itemData = new byte[inputStream.available()];
            int len = inputStream.read(itemData);
            Log.e(TAG, "beautification len " + len);
            inputStream.close();
            mFaceBeautyItem = faceunity.fuCreateItemFromPackage(itemData);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void initParams() {
        Context context = mContextWeakReference.get();
        if (context == null) {
            return;
        }
        mBeautyFaceLevel = SharedPreferenceUtils.getFaceUnityBeautyLevel(context);
        mBeautySkinLevel = SharedPreferenceUtils.getFaceUnityBeautySkinLevel(context);
        mCustomParams = getBeautyParams(context);
        changeFaceBeautyLevel(mBeautyFaceLevel);
        changeSkinLevel(mBeautySkinLevel);
    }

    /**
     * @return 用户自定义参数
     */
    private Map<Integer, FaceUnityBeautyParams> getBeautyParams(Context context) {
        Map<Integer, FaceUnityBeautyParams> customParams = null;
        try {
            //获取用户微调参数
            String faceUnityBeautyCustomParams = SharedPreferenceUtils.getFaceUnityBeautyCustomParams(context);
            if (!TextUtils.isEmpty(faceUnityBeautyCustomParams)) {
                Gson gson = new GsonBuilder().create();
                customParams = gson.fromJson(faceUnityBeautyCustomParams, new TypeToken<HashMap<Integer, FaceUnityBeautyParams>>() {
                }.getType());
            }
        } catch (Exception e) {
            SharedPreferenceUtils.setFaceUnityBeautyCustomParams(context, "");
        }
        if (customParams == null) {
            customParams = new HashMap<>();
            for (Map.Entry<Integer, FaceUnityBeautyParams> key : FaceUnityBeautyConstants.BEAUTY_MAP.entrySet()) {
                FaceUnityBeautyParams value = key.getValue();
                FaceUnityBeautyParams cloneParams = value.clone();
                customParams.put(cloneParams.beautyLevel, cloneParams);
            }
        }
        return customParams;
    }


    @Override
    public void showControllerView(FragmentManager fragmentManager, final OnBeautyLayoutChangeListener onBeautyLayoutChangeListener) {
        final Context context = mContextWeakReference.get();
        mFragmentManager = fragmentManager;
        mOnBeautyLayoutChangeListener = onBeautyLayoutChangeListener;
        if (context == null || fragmentManager == null) {
            return;
        }
        if (mFaceUnityBeautyChooser == null) {
            mFaceUnityBeautyChooser = new FaceUnityBeautyChooser();
            mFaceUnityBeautyChooser.setOnBeautyChooserCallback(this);
            mFaceUnityBeautyChooser.setDismissListener(new BaseChooser.DialogVisibleListener() {
                @Override
                public void onDialogDismiss() {
                    if (onBeautyLayoutChangeListener != null && !isBeautyDetailShowing) {
                        onBeautyLayoutChangeListener.onLayoutChange(View.GONE);
                        saveBeautyParams(context);
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

        mFaceUnityBeautyChooser.show(fragmentManager, "faceUnity", mBeautyFaceLevel, mBeautySkinLevel);

    }


    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public BeautySDKType getSdkType() {
        return BeautySDKType.FACEUNITY;
    }

    @Override
    public void setDebug(boolean isDebug) {

    }


    @Override
    public void onShowFaceDetailView(int level) {
        if (mCustomParams == null) {
            return;
        }
        isBeautyDetailShowing = true;
        if (mFaceUnityBeautyChooser != null && mFaceUnityBeautyChooser.isVisible()) {
            mFaceUnityBeautyChooser.dismiss();
        }
        if (mBeautyFaceDetailSettingChooser == null) {
            mBeautyFaceDetailSettingChooser = new BeautyFaceDetailSettingChooser();
        }
        mBeautyFaceDetailSettingChooser.setOnBeautyChooserCallback(this);
        mBeautyFaceDetailSettingChooser.setBeautyParams(mCustomParams.get(level));
        mBeautyFaceDetailSettingChooser.show(mFragmentManager, "faceDetail");

    }

    @Override
    public void onShowSkinDetailView(int type) {
        isBeautyDetailShowing = true;
        if (mFaceUnityBeautyChooser != null && mFaceUnityBeautyChooser.isVisible()) {
            mFaceUnityBeautyChooser.dismiss();
        }
        if (mBeautyShapeDetailChooser == null) {
            mBeautyShapeDetailChooser = new BeautyShapeDetailChooser();
        }
        mBeautyShapeDetailChooser.setOnBeautyChooserCallback(this);
        mBeautyShapeDetailChooser.setBeautyShapeParams(mCustomParams.get(type));
        mBeautyShapeDetailChooser.show(mFragmentManager, "shapeDetail");
    }

    @Override
    public void onChooserBlankClick() {
        //点击空白部分，隐藏美颜编辑界面
        isBeautyDetailShowing = false;
        if (mBeautyShapeDetailChooser != null && mBeautyShapeDetailChooser.isVisible()) {
            mBeautyShapeDetailChooser.dismiss();
        }
        if (mBeautyFaceDetailSettingChooser != null && mBeautyFaceDetailSettingChooser.isVisible()) {
            mBeautyFaceDetailSettingChooser.dismiss();
        }
        if (mFaceUnityBeautyChooser != null && mFaceUnityBeautyChooser.isVisible()) {
            mFaceUnityBeautyChooser.dismiss();
        } else {
            if (mOnBeautyLayoutChangeListener != null) {
                mOnBeautyLayoutChangeListener.onLayoutChange(View.GONE);
            }
        }

    }

    @Override
    public void onChooserBackClick() {
        if (mBeautyShapeDetailChooser != null && mBeautyShapeDetailChooser.isVisible()) {
            mBeautyShapeDetailChooser.dismiss();
        }
        if (mBeautyFaceDetailSettingChooser != null && mBeautyFaceDetailSettingChooser.isVisible()) {
            mBeautyFaceDetailSettingChooser.dismiss();
        }
        if (mFaceUnityBeautyChooser != null && !mFaceUnityBeautyChooser.isVisible()) {
            mFaceUnityBeautyChooser.show(mFragmentManager, "faceUnity", mBeautyFaceLevel, mBeautySkinLevel);
        }

    }

    @Override
    public void onChooserKeyBackClick() {
        isBeautyDetailShowing = false;
        if (mBeautyShapeDetailChooser != null && mBeautyShapeDetailChooser.isVisible()) {
            mBeautyShapeDetailChooser.dismiss();
        }
        if (mBeautyFaceDetailSettingChooser != null && mBeautyFaceDetailSettingChooser.isVisible()) {
            mBeautyFaceDetailSettingChooser.dismiss();
        }
        if (mFaceUnityBeautyChooser != null && mFaceUnityBeautyChooser.isVisible()) {
            mFaceUnityBeautyChooser.dismiss();
        }
        if (mOnBeautyLayoutChangeListener != null) {
            mOnBeautyLayoutChangeListener.onLayoutChange(View.GONE);
        }
    }

    @Override
    public void onFaceLevelChanged(int level) {
        mBeautyFaceLevel = level;
        changeFaceBeautyLevel(level);

    }

    @Override
    public void onShapeTypeChange(int level) {
        mBeautySkinLevel = level;
        changeSkinLevel(level);
    }

    @Override
    public void onBeautyFaceChange(FaceUnityBeautyParams param) {
        if (param != null) {
            changeFaceBeautyParams(param);
            if (mCustomParams != null) {
                mCustomParams.put(param.beautyLevel, param);
            }
        }


    }

    @Override
    public void onBeautyShapeChange(FaceUnityBeautyParams param) {
        if (param != null) {
            changeSkinParams(param);
            if (mCustomParams != null) {
                mCustomParams.put(param.beautyLevel, param);
            }
        }
    }

    @Override
    public void addDefaultBeautyLevelChangeListener(OnDefaultBeautyLevelChangeListener onDefaultBeautyLevelChangeListener) {

    }

    @Override
    public void onFrameBack(byte[] bytes, int width, int height, Camera.CameraInfo info) {
        frameBytes = bytes;
        frameWidth = width;
        frameHeight = height;
    }

    @Override
    public int onTextureIdBack(int textureId, int textureWidth, int textureHeight, float[] matrix, int currentCameraType) {
        if (!isInit && mFaceBeautyColorLevel == 0 &&
                mFaceBeautyBlurLevel == 0 &&
                mFaceBeautyRedLevel == 0 &&
                mFaceBeautyCheekThin == 0 &&
                mFaceBeautyEnlargeEye == 0) {
            return textureId;
        }

        faceunity.fuItemSetParam(mFaceBeautyItem, "color_level", mFaceBeautyColorLevel);
        faceunity.fuItemSetParam(mFaceBeautyItem, "blur_level", mFaceBeautyBlurLevel);
        faceunity.fuItemSetParam(mFaceBeautyItem, "skin_detect", mFaceBeautyALLBlurLevel);
        faceunity.fuItemSetParam(mFaceBeautyItem, "cheek_thinning", mFaceBeautyCheekThin);
        faceunity.fuItemSetParam(mFaceBeautyItem, "eye_enlarging", mFaceBeautyEnlargeEye);
        faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape", mFaceShape);
        faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape_level", mFaceShapeLevel);
        faceunity.fuItemSetParam(mFaceBeautyItem, "red_level", mFaceBeautyRedLevel);
        faceunity.fuItemSetParam(mFaceBeautyItem, "eye_bright", 0f);
        faceunity.fuItemSetParam(mFaceBeautyItem, "heavy_blur", 0f);

        boolean isOESTexture = true; //Tip: camera texture类型是默认的是OES的，和texture 2D不同
        int flags = isOESTexture ? faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE : 0;
        boolean isNeedReadBack = false; //是否需要写回，如果是，则入参的byte[]会被修改为带有fu特效的；支持写回自定义大小的内存数组中，即readback custom img
        flags = isNeedReadBack ? flags | faceunity.FU_ADM_FLAG_ENABLE_READBACK : flags;
        byte[] fuImgNV21Bytes = null;
        if (isNeedReadBack) {
            fuImgNV21Bytes = new byte[frameBytes.length];
            System.arraycopy(frameBytes, 0, fuImgNV21Bytes, 0, frameBytes.length);
        } else {
            fuImgNV21Bytes = frameBytes;
        }
        flags |= currentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? 0 : faceunity.FU_ADM_FLAG_FLIP_X;

        /*
         * 这里拿到fu处理过后的texture，可以对这个texture做后续操作，如硬编、预览。
         */
        return faceunity.fuDualInputToTexture(fuImgNV21Bytes, textureId, flags,
                frameWidth, frameHeight, frameId++, new int[]{mFaceBeautyItem});
    }


    @Override
    public void setDeviceOrientation(int deviceOrientation, int displayOrientation) {

    }

    private void changeFaceBeautyLevel(int beautyFaceLevel) {
        if (mCustomParams == null || mCustomParams.size() == 0 || beautyFaceLevel < 0) {
            return;
        }
        FaceUnityBeautyParams faceUnityBeautyParams = mCustomParams.get(beautyFaceLevel);
        changeFaceBeautyParams(faceUnityBeautyParams);
    }

    private void changeFaceBeautyParams(FaceUnityBeautyParams faceUnityBeautyParams) {
        setFaceBeautyWhite(faceUnityBeautyParams.beautyWhite / 100);
        setFaceBeautyRuddy(faceUnityBeautyParams.beautyRuddy / 100);
        setFaceBeautyBuffing(faceUnityBeautyParams.beautyBuffing / 100 * 10 * 0.6f);
    }

    private void changeSkinLevel(int beautySkinLevel) {
        if (mCustomParams == null || mCustomParams.size() == 0 || beautySkinLevel < 0) {
            return;
        }
        FaceUnityBeautyParams faceUnityBeautyParams = mCustomParams.get(beautySkinLevel);
        changeSkinParams(faceUnityBeautyParams);
    }

    private void changeSkinParams(FaceUnityBeautyParams faceUnityBeautyParams) {
        setFaceBeautyBigEye(faceUnityBeautyParams.beautyBigEye / 100 * 1.5f);
        setFaceBeautySlimFace(faceUnityBeautyParams.beautySlimFace / 100 * 1.5f);
    }

    /**
     * 美白
     *
     * @param mFaceBeautyColorLevel
     */
    public FaceUnityManager setFaceBeautyWhite(float mFaceBeautyColorLevel) {
        this.mFaceBeautyColorLevel = mFaceBeautyColorLevel;
        Log.e(TAG, "setFaceBeautyWhite: " + mFaceBeautyColorLevel);

        return this;
    }

    /**
     * 磨皮
     *
     * @param mFaceBeautyBlurLevel
     */
    public FaceUnityManager setFaceBeautyBuffing(float mFaceBeautyBlurLevel) {
        this.mFaceBeautyBlurLevel = mFaceBeautyBlurLevel;
        Log.e(TAG, "setFaceBeautyBuffing: " + mFaceBeautyBlurLevel);
        return this;
    }

    /**
     * 精准磨皮
     *
     * @param mFaceBeautyALLBlurLevel
     */
    public FaceUnityManager setFaceBeautyALLBlurLevel(float mFaceBeautyALLBlurLevel) {
        this.mFaceBeautyALLBlurLevel = mFaceBeautyALLBlurLevel;
        return this;
    }


    /**
     * 瘦脸
     *
     * @return
     */
    public FaceUnityManager setFaceBeautySlimFace(float mFaceBeautyCheekThin) {
        this.mFaceBeautyCheekThin = mFaceBeautyCheekThin;
        Log.e(TAG, "setFaceBeautySlimFace: " + mFaceBeautyCheekThin);

        return this;
    }

    /**
     * 大眼
     *
     * @param mFaceBeautyEnlargeEye
     */
    public FaceUnityManager setFaceBeautyBigEye(float mFaceBeautyEnlargeEye) {
        this.mFaceBeautyEnlargeEye = mFaceBeautyEnlargeEye;
        Log.e(TAG, "setFaceBeautyBigEye: " + mFaceBeautyEnlargeEye);

        return this;
    }

    /**
     * 红润
     *
     * @return
     */
    public FaceUnityManager setFaceBeautyRuddy(float mFaceBeautyRedLevel) {
        this.mFaceBeautyRedLevel = mFaceBeautyRedLevel;
        Log.e(TAG, "setFaceBeautySharpLevel: " + mFaceBeautyRedLevel);

        return this;
    }

    /**
     * 美型，脸型选择
     *
     * @param mFaceShape
     */
    public void setFaceShape(int mFaceShape) {
        this.mFaceShape = mFaceShape;
    }

    /**
     * 美型程度
     *
     * @param mFaceShapeLevel 0-1的值
     */
    public void setFaceShapeLevel(float mFaceShapeLevel) {
        this.mFaceShapeLevel = mFaceShapeLevel;
    }


    /**
     * 保存用户自定义参数
     *
     * @param context
     */
    private void saveBeautyParams(Context context) {
        String gsonString = "";
        if (context != null && mCustomParams != null) {
            try {
                Gson gson = new GsonBuilder().create();
                Type gsonType = new TypeToken<HashMap<Integer, FaceUnityBeautyParams>>() {
                }.getType();
                gsonString = gson.toJson(mCustomParams, gsonType);
            } catch (Exception e) {
                Log.e(TAG, "saveBeautyParams: ", e);
            }

            SharedPreferenceUtils.setFaceUnityBeautyCustomParams(context, gsonString);
            SharedPreferenceUtils.setBeautyFaceLevel(context, mBeautyFaceLevel);
            SharedPreferenceUtils.setBeautySkinLevel(context, mBeautySkinLevel);
        }
    }


    @Override
    public void release() {
        Log.e(TAG, "release: ");
        frameId = 0;
        mIAliyunBeautyInitCallback = null;
        ThreadUtils.removeCallbacks(initCallBackRunnable);
        faceunity.fuDestroyAllItems();
        faceunity.fuDone();
        faceunity.fuOnDeviceLost();
    }
}
