package com.aliyun.svideo.crop.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.aliyun.svideo.crop.CropMediaActivity;
import com.aliyun.svideo.crop.AliyunVideoCropActivity;
import com.aliyun.svideo.crop.bean.AlivcCropInputParam;
import com.aliyun.svideosdk.common.struct.common.AliyunSnapVideoParam;
import com.aliyun.svideosdk.common.struct.common.CropKey;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;

import java.util.List;

public class AUICropHelper {
    public static final int REQUEST_CODE_EDITOR_VIDEO_CROP = 1;
    public static final int REQUEST_CODE_CROP_VIDEO_CROP = 2;

    public static void startCropForResult(Activity activity, int requestCode, AliyunSnapVideoParam param) {
        Intent intent = new Intent(activity, CropMediaActivity.class);
        intent.putExtra(AliyunSnapVideoParam.SORT_MODE, param.getSortMode());
        intent.putExtra(AliyunSnapVideoParam.VIDEO_RESOLUTION, param.getResolutionMode());
        intent.putExtra(AliyunSnapVideoParam.VIDEO_RATIO, param.getRatioMode());
        intent.putExtra(AliyunSnapVideoParam.NEED_RECORD, param.isNeedRecord());
        intent.putExtra(AliyunSnapVideoParam.VIDEO_QUALITY, param.getVideoQuality());
        intent.putExtra(AliyunSnapVideoParam.VIDEO_CODEC, param.getVideoCodec());
        intent.putExtra(AliyunSnapVideoParam.VIDEO_GOP, param.getGop());
        intent.putExtra(AliyunSnapVideoParam.VIDEO_FRAMERATE, param.getFrameRate());
        intent.putExtra(AliyunSnapVideoParam.CROP_MODE, param.getScaleMode());
        intent.putExtra(AliyunSnapVideoParam.MIN_CROP_DURATION, param.getMinCropDuration());
        intent.putExtra(AliyunSnapVideoParam.MIN_VIDEO_DURATION, param.getMinVideoDuration());
        intent.putExtra(AliyunSnapVideoParam.MAX_VIDEO_DURATION, param.getMaxVideoDuration());
        intent.putExtra(AliyunSnapVideoParam.RECORD_MODE, param.getRecordMode());
        intent.putExtra(AliyunSnapVideoParam.FILTER_LIST, param.getFilterList());
        intent.putExtra(AliyunSnapVideoParam.BEAUTY_LEVEL, param.getBeautyLevel());
        intent.putExtra(AliyunSnapVideoParam.BEAUTY_STATUS, param.getBeautyStatus());
        intent.putExtra(AliyunSnapVideoParam.CAMERA_TYPE, param.getCameraType());
        intent.putExtra(AliyunSnapVideoParam.FLASH_TYPE, param.getFlashType());
        intent.putExtra(AliyunSnapVideoParam.NEED_CLIP, param.isNeedClip());
        intent.putExtra(AliyunSnapVideoParam.MAX_DURATION, param.getMaxDuration());
        intent.putExtra(AliyunSnapVideoParam.MIN_DURATION, param.getMinDuration());
        intent.putExtra(AliyunSnapVideoParam.CROP_USE_GPU, param.isCropUseGPU());
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startVideoCropForResult(Activity context, AlivcCropInputParam param, int requestCode) {
        if (param == null || TextUtils.isEmpty(param.getPath())) {
            return;
        }
        Intent intent = new Intent(context, AliyunVideoCropActivity.class);
        intent.putExtra(AlivcCropInputParam.INTENT_KEY_PATH, param.getPath());
        intent.putExtra(AlivcCropInputParam.INTENT_KEY_RESOLUTION_MODE, param.getResolutionMode());
        intent.putExtra(AlivcCropInputParam.INTENT_KEY_CROP_MODE, param.getCropMode());
        intent.putExtra(AlivcCropInputParam.INTENT_KEY_QUALITY, param.getQuality());
        intent.putExtra(AlivcCropInputParam.INTENT_KEY_CODECS, param.getVideoCodecs());
        int gop;
        if (requestCode == AUICropHelper.REQUEST_CODE_EDITOR_VIDEO_CROP) {
            //editor 裁剪预览转码时设置gop 5 ，进入编辑不需要二次裁剪
            gop = 5;
        } else {
            gop = param.getGop();
        }
        intent.putExtra(AlivcCropInputParam.INTENT_KEY_GOP, gop);
        intent.putExtra(AlivcCropInputParam.INTENT_KEY_RATIO_MODE, param.getRatioMode());
        intent.putExtra(AlivcCropInputParam.INTENT_KEY_FRAME_RATE, param.getFrameRate());
        intent.putExtra(AlivcCropInputParam.INTENT_KEY_ACTION, param.getAction());
        intent.putExtra(AlivcCropInputParam.INTENT_KEY_MIN_DURATION, param.getMinCropDuration());
        intent.putExtra(AlivcCropInputParam.INTENT_KEY_USE_GPU, param.isUseGPU());
        context.startActivityForResult(intent, requestCode);
    }

    public static void startVideoCrop(Activity context, List<String> paths) {
        if(paths.isEmpty() || TextUtils.isEmpty(paths.get(0))){
            return;
        }
        AlivcCropInputParam param = new AlivcCropInputParam.Builder()
                                    .setPath(paths.get(0))
                                    .setResolutionMode(CropKey.RESOLUTION_720P)
                                    .setCropMode(VideoDisplayMode.FILL)
                                    .setQuality(VideoQuality.HD)
                                    .setVideoCodecs(VideoCodecs.H264_HARDWARE)
                                    .setUseGPU(true)
                                    .setGop(250)
                                    .setFrameRate(30)
                                    .setRatioMode(CropKey.RATIO_MODE_9_16)
                                    .build();

       startVideoCropForResult(context, param, -1);
    }
}
