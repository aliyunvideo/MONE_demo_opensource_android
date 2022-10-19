package com.aliyun.svideo.base;

import android.app.Activity;
import android.content.Intent;

import com.aliyun.svideo.media.MediaInfo;
import com.aliyun.svideosdk.common.struct.common.AliyunVideoParam;
import java.util.ArrayList;

/**
 * @author cross_ly
 * @date 2018/10/19
 * <p>描述:跳转相册、编辑页面的路由
 */
public class AlivcEditorRoute {
    public static final String KEY_INTENT_ISOPENCROP = "key_intent_isopencrop";//是否相册进入打开裁剪的入口的key
    public static final int KEY_REQUEST_EDITOR_CODE = 10001;//startActivityForResult的请求码，获取合成路径的
    public static final String KEY_INTENT_MEDIA_INFO = "key_media_info";

    /**
     * 打开编辑Activity
     *
     * @param activity   在该activity的onActivityResult接收到合成视频的路径
     * @param param      {@link AlivcSvideoEditParam}
     * @param mediaInfos 需要编辑的视频信息<>这里只保证我们自己录制的视频，对其他途径的视频有可能会出现无法编辑的问题</>
     */
    public static void startEditorActivity(Activity activity, AlivcSvideoEditParam param, ArrayList<MediaInfo> mediaInfos) {
        Intent intent = new Intent();
        intent.setClassName(activity, "com.aliyun.svideo.editor.editor.EditorActivity");
        param.setMediaInfo(mediaInfos.get(0));
        AliyunVideoParam mVideoParam = param.generateVideoParam();
        intent.putExtra("video_param", mVideoParam);
        intent.putParcelableArrayListExtra(KEY_INTENT_MEDIA_INFO, mediaInfos);
        intent.putExtra("hasTailAnimation", param.isHasTailAnimation());
        intent.putExtra(AlivcSvideoEditParam.INTENT_PARAM_KEY_ENTRANCE, param.getEntrance());
        activity.startActivity(intent);
    }

    /**
     * 打开相册Activity
     *
     * @param param 必要参数
     */
    public static void startMediaActivity(Activity activity, AlivcSvideoEditParam param) {

        Intent intent = new Intent();
        intent.setClassName(activity, "com.aliyun.svideo.media.MediaActivity");
        boolean isOpenCrop = param.isOpenCrop();
        intent.putExtra(KEY_INTENT_ISOPENCROP, isOpenCrop);
        if (isOpenCrop) {
            intent.putExtra(AlivcSvideoEditParam.VIDEO_FRAMERATE, param.getFrameRate());
            intent.putExtra(AlivcSvideoEditParam.VIDEO_GOP, param.getGop());
            intent.putExtra(AlivcSvideoEditParam.VIDEO_RATIO, param.getRatio());
            intent.putExtra(AlivcSvideoEditParam.VIDEO_QUALITY, param.getVideoQuality());
            intent.putExtra(AlivcSvideoEditParam.VIDEO_RESOLUTION, param.getResolutionMode());
            intent.putExtra(AlivcSvideoEditParam.VIDEO_CROP_MODE, param.getCropMode());
            intent.putExtra(AlivcSvideoEditParam.TAIL_ANIMATION, param.isHasTailAnimation());
            intent.putExtra(AlivcSvideoEditParam.INTENT_PARAM_KEY_ENTRANCE, "svideo");
        }
        activity.startActivityForResult(intent, KEY_REQUEST_EDITOR_CODE);
    }

}
