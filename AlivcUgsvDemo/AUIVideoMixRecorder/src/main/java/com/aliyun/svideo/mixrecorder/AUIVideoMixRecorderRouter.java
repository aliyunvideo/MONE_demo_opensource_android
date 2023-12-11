package com.aliyun.svideo.mixrecorder;

import android.content.Context;
import android.content.Intent;

import com.aliyun.svideo.mixrecorder.bean.MixRecorderConfig;
import com.aliyun.svideosdk.common.struct.common.AliyunVideoClip;
import com.aliyun.svideosdk.common.struct.common.AliyunVideoParam;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.importer.AliyunIImport;
import com.aliyun.svideosdk.importer.impl.AliyunImportCreator;

public class AUIVideoMixRecorderRouter {

    public static void jumpEditor(Context context, String path, long duration) {
        AliyunVideoParam.Builder builder = new AliyunVideoParam.Builder();
        builder.gop(MixRecorderConfig.getInstance().getGop());
        builder.frameRate(MixRecorderConfig.getInstance().getFPS());
        builder.videoQuality(MixRecorderConfig.getInstance().getVideoQuality());
        builder.videoCodec(MixRecorderConfig.getInstance().getVideoCodec());
        builder.scaleMode(VideoDisplayMode.FILL);
        builder.outputWidth(MixRecorderConfig.getInstance().getVideoWidth());
        builder.outputHeight(MixRecorderConfig.getInstance().getVideoHeight());

        AliyunIImport aliyunIImport = AliyunImportCreator.getImportInstance(context);
        aliyunIImport.addMediaClip(new AliyunVideoClip.Builder()
                .source(path)
                .startTime(0)
                .endTime(duration)
                .duration(duration)
                .build());
        aliyunIImport.setVideoParam(builder.build());
        String projectConfigure = aliyunIImport.generateProjectConfigure();
        aliyunIImport.release();

        Intent intent = new Intent();
        intent.setClassName(context, "com.aliyun.svideo.editor.AUIVideoEditorActivity");
        intent.putExtra("projectJsonPath", projectConfigure);
        context.startActivity(intent);
    }
}
