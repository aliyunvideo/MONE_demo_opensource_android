package com.aliyun.svideo.recorder;

import android.content.Context;
import android.content.Intent;

import com.aliyun.svideosdk.common.struct.common.AliyunVideoClip;
import com.aliyun.svideosdk.common.struct.common.AliyunVideoParam;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.importer.AliyunIImport;
import com.aliyun.svideosdk.importer.impl.AliyunImportCreator;

public class AUIVideoRecorderRouter {

    public static void jumpEditor(Context context, String path, long duration) {
        AliyunVideoParam.Builder builder = new AliyunVideoParam.Builder();
        builder.gop(RecorderConfig.Companion.getInstance().getGop());
        builder.frameRate(RecorderConfig.Companion.getInstance().getFps());
        builder.videoQuality(RecorderConfig.Companion.getInstance().getVideoQuality());
        builder.videoCodec(RecorderConfig.Companion.getInstance().getCodec());
        builder.scaleMode(VideoDisplayMode.FILL);
        int width = RecorderConfig.Companion.getInstance().getResolution();
        int height = (int) (width / RecorderConfig.Companion.getInstance().getRatio());
        builder.outputWidth(width);
        builder.outputHeight(height);

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
