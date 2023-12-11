package com.aliyun.svideo.mixrecorder.mixrecorder;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.aliyun.svideo.mixrecorder.bean.AlivcMixBorderParam;
import com.aliyun.svideo.mixrecorder.bean.VideoDisplayParam;
import com.aliyun.svideosdk.common.NativeAdaptiveUtil;
import com.aliyun.svideosdk.common.callback.recorder.OnFrameCallback;
import com.aliyun.svideosdk.common.callback.recorder.OnPictureCallback;
import com.aliyun.svideosdk.common.callback.recorder.OnRecordCallback;
import com.aliyun.svideosdk.common.callback.recorder.OnTextureIdCallback;
import com.aliyun.svideosdk.common.struct.effect.EffectFilter;
import com.aliyun.svideosdk.common.struct.effect.EffectStream;
import com.aliyun.svideosdk.common.struct.recorder.CameraType;
import com.aliyun.svideosdk.common.struct.recorder.FlashType;
import com.aliyun.svideosdk.common.struct.recorder.MediaInfo;
import com.aliyun.svideosdk.mixrecorder.AliyunIMixRecorder;
import com.aliyun.svideosdk.mixrecorder.AliyunMixBorderParam;
import com.aliyun.svideosdk.mixrecorder.AliyunMixMediaInfoParam;
import com.aliyun.svideosdk.mixrecorder.AliyunMixRecorderDisplayParam;
import com.aliyun.svideosdk.mixrecorder.AliyunMixTrackLayoutParam;
import com.aliyun.svideosdk.mixrecorder.MixAudioAecType;
import com.aliyun.svideosdk.mixrecorder.MixAudioSourceType;
import com.aliyun.svideosdk.mixrecorder.MixVideoSourceType;
import com.aliyun.svideosdk.mixrecorder.impl.AliyunMixRecorderCreator;
import com.aliyun.svideosdk.recorder.AliyunIClipManager;
import com.aliyun.svideosdk.recorder.AliyunIRecordPasterManager;
import com.aliyun.ugsv.common.utils.ScreenUtils;

/**
 * 包含合拍功能
 */
public class AlivcMixRecorder {

    private Context mContext;
    private AliyunIMixRecorder mRecorder;
    private VideoDisplayParam mPlayDisplayParam;
    private VideoDisplayParam mRecordDisplayParam;

    public AlivcMixRecorder(Context context) {
        this.mContext = context;
        initRecorder(mContext);
    }

    /**
     * 初始化recorder
     */
    private void initRecorder(Context context) {

        mRecorder = AliyunMixRecorderCreator.createAlivcMixRecorderInstance(context);

    }


    public void applyAnimationFilter(EffectFilter effectFilter) {
        mRecorder.applyAnimationFilter(effectFilter);
    }


    public void updateAnimationFilter(EffectFilter effectFilter) {
        mRecorder.updateAnimationFilter(effectFilter);
    }


    public void removeAnimationFilter(EffectFilter effectFilter) {
        mRecorder.removeAnimationFilter(effectFilter);
    }


    public void useFlip(boolean isUseFlip) {
        //nothing to do
    }


    public FrameLayout.LayoutParams getLayoutParams() {

        int screenWidth = ScreenUtils.getRealWidth(mContext);
        int height = 0;
        int width = screenWidth / 2;
        height = width * 16 / 9;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);

        params.height = height;
        params.width = width;
        return params;
    }


    public void takePicture(boolean needBitmap, OnPictureCallback pictureCallback) {

    }


    public void takeSnapshot(boolean needBitmap, OnPictureCallback pictureCallback) {

    }

    public void setMediaInfo(String videoPath, VideoDisplayParam playDisplayParam, VideoDisplayParam recordDisplayParam, MediaInfo outputInfo) {
        // mMixInputInfo只对合拍有效，普通录制情况下，该参数将被忽略
        mPlayDisplayParam = playDisplayParam;
        mRecordDisplayParam = recordDisplayParam;
        AliyunMixMediaInfoParam inputInfo = new AliyunMixMediaInfoParam
        .Builder()
        .streamStartTimeMills(0L)
        .streamEndTimeMills(0L)
        .mixVideoFilePath(videoPath)
        .mixDisplayParam(mPlayDisplayParam.getAliDisplayParam())
        .recordDisplayParam(mRecordDisplayParam.getAliDisplayParam())
        .build();
        Log.e("czwxxx", "play centerx: " + mPlayDisplayParam.getCenterX() + ", centery: " + mPlayDisplayParam.getCenterY() + ", w: " + mPlayDisplayParam.getWidthRatio() + ", h: " + mPlayDisplayParam.getHeightRatio());
        Log.e("czwxxx", "record centerx: " + mRecordDisplayParam.getCenterX() + ", centery: " + mRecordDisplayParam.getCenterY() + ", w: " + mRecordDisplayParam.getWidthRatio() + ", h: " + mRecordDisplayParam.getHeightRatio());
        mRecorder.setMixMediaInfo(inputInfo, outputInfo);
    }

    public AliyunIClipManager getClipManager() {
        return mRecorder.getClipManager();
    }


    public void setOutputPath(String var1) {
        mRecorder.setOutputPath(var1);
    }


    public void setGop(int var1) {
        mRecorder.setGop(var1);
    }


    public void setCamera(CameraType var1) {
        mRecorder.setCamera(var1);
    }


    public int getCameraCount() {
        return mRecorder.getCameraCount();
    }


    public void setDisplayView(SurfaceView cameraPreviewView, SurfaceView playerView) {
        mRecorder.setDisplayView(cameraPreviewView, playerView);
    }


    public int applyBackgroundMusic(EffectStream effectStream) {
        //do nothing
        return 0;
    }


    public int removeBackgroundMusic() {
        //do nothing
        return 0;
    }


    public void startPreview() {
        mRecorder.startPreview();
    }


    public void stopPreview() {
        mRecorder.stopPreview();

    }


    public AliyunIRecordPasterManager getPasterManager() {
        return mRecorder.getPasterManager();
    }



    public void applyFilter(EffectFilter var1) {
        mRecorder.applyFilter(var1);
    }


    public void removeFilter() {
        mRecorder.removeFilter();
    }



    public int switchCamera() {
        return mRecorder.switchCamera();
    }


    public void setLight(FlashType var1) {
        mRecorder.setLight(var1);
    }


    public void setZoom(float var1) {
        mRecorder.setZoom(var1);
    }


    public void setFocusMode(int var1) {
        mRecorder.setFocusMode(var1);
    }


    public void setRate(float var1) {
        mRecorder.setRate(var1);
    }


    public void setFocus(float var1, float var2) {
        mRecorder.setFocus(var1, var2);
    }



    public void setBeautyLevel(int var1) {
        mRecorder.setBeautyLevel(var1);
    }


    public void startRecording() {
        mRecorder.startRecording();
    }


    public void stopRecording() {
        mRecorder.stopRecording();
    }


    public int finishRecording() {
        //由于Demo这里合拍视频都是经过降采样转码的，分辨率比较低，建议使用软解码，如果是高分辨率视频且不转码降采样，则不建议关闭硬解码
        NativeAdaptiveUtil.setHWDecoderEnable(false);
        int code = mRecorder.finishRecording();
        NativeAdaptiveUtil.setHWDecoderEnable(true);//重新开启硬解码
        return code;
    }


    public void setOnRecordCallback(OnRecordCallback var1) {
        mRecorder.setOnRecordCallback(var1);
    }



    public void setOnFrameCallback(OnFrameCallback var1) {
        mRecorder.setOnFrameCallback(var1);
    }

    /**
     * 横屏竖屏旋转多段录制视频,保证旋转的角度跟录制出来的保持一致
     */

    public void setRotation(int var1) {
        mRecorder.setRecordRotation(0);
        mRecorder.setFaceDetectRotation(var1);
    }


    public void setOnTextureIdCallback(OnTextureIdCallback var1) {
        mRecorder.setOnTextureIdCallback(var1);
    }


    public void needFaceTrackInternal(boolean var1) {
        mRecorder.needFaceTrackInternal(var1);
    }


    public void setFaceTrackInternalModelPath(String var1) {
        mRecorder.setFaceTrackInternalModelPath(var1);
    }


    public void setFaceTrackInternalMaxFaceCount(int var1) {
        mRecorder.setFaceTrackInternalMaxFaceCount(var1);
    }


    public void setMute(boolean var1) {
    }


    public void deleteLastPart() {
        mRecorder.deleteLastPart();
    }

    public boolean isMixRecorder() {
        return true;
    }


    public void setResolutionMode(int resolutionMode) {
    }


    public void setRatioMode(int ratioMode) {

    }


    public void release() {
        mRecorder.release();
    }


    public VideoDisplayParam getPlayDisplayParams() {
        return mPlayDisplayParam;
    }


    public VideoDisplayParam getRecordDisplayParam() {
        return mRecordDisplayParam;
    }

    public void setPlayDisplayParam(VideoDisplayParam playDisplayParam) {
        this.mPlayDisplayParam = playDisplayParam;
    }

    public void setRecordDisplayParam(VideoDisplayParam recordDisplayParam) {
        this.mRecordDisplayParam = recordDisplayParam;
    }

    public AlivcMixBorderParam getMixBorderParam() {
        return mMixBorderParam;
    }


    public void setMixBorderParam(AlivcMixBorderParam param) {
        mMixBorderParam = param;
        if (mMixBorderParam != null) {
            AliyunMixBorderParam mixBorderParam = new AliyunMixBorderParam.Builder()
            .borderColor(mMixBorderParam.getBorderColor())
            .cornerRadius(mMixBorderParam.getCornerRadius())
            .borderWidth(mMixBorderParam.getBorderWidth())
            .build();
            mRecorder.setRecordBorderParam(mixBorderParam);
        } else {
            mRecorder.setRecordBorderParam(null);
        }
    }

    public void setMixAudioSource(MixAudioSourceType mMixAudioSourceType) {
        mRecorder.setMixAudioSource(mMixAudioSourceType);
    }

    public void setMixAudioAecType(MixAudioAecType mMixAudioAecType) {
        mRecorder.setMixAudioAecType(mMixAudioAecType);
    }

    private int mBackgroundColor;
    /**
     * 设置合成窗口非填充模式下的背景颜色
     * v3.19.0 新增
     * @param color
     */
    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        mRecorder.setBackgroundColor(color);
    }
    private String mBackGroundImage;
    private int mDisplayMode;
    private AlivcMixBorderParam mMixBorderParam;
    /**
     * 设置合成窗口非填充模式下的背景图片路径
     * v3.19.0 新增
     * @param path
     * @param displayMode 0：裁切 1：填充 2：拉伸
     */
    public void setBackgroundImage(String path, int displayMode) {
        mBackGroundImage = path;
        mDisplayMode = displayMode;
        mRecorder.setBackgroundImage(path, displayMode);
    }


    public int getBackgroundColor() {
        return mBackgroundColor;
    }


    public String getBackgroundImage() {
        return mBackGroundImage;
    }


    public int getBackgroundImageDisplayMode() {
        return mDisplayMode;
    }


    public void setIsAutoClearClipVideos(boolean isAutoClear) {
        mRecorder.setIsAutoClearClipVideos(isAutoClear);
    }

    public void updateVideoSourceLayout(){
        mRecorder.updateVideoSourceLayout(mPlayDisplayParam.getAliDisplayParam(), MixVideoSourceType.PLAY);
        mRecorder.updateVideoSourceLayout(mRecordDisplayParam.getAliDisplayParam(), MixVideoSourceType.RECORD);
    }
}
