package com.aliyun.svideo.editor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.aliyun.ugsv.common.utils.FileUtils;
import com.aliyun.svideo.editor.publish.AUIPublishActivity;
import com.aliyun.svideosdk.common.struct.common.AliyunImageClip;
import com.aliyun.svideosdk.common.struct.common.AliyunVideoClip;
import com.aliyun.svideosdk.common.struct.common.AliyunVideoParam;
import com.aliyun.svideosdk.importer.AliyunIImport;
import com.aliyun.svideosdk.importer.impl.AliyunImportCreator;
import com.aliyun.svideosdk.transcode.NativeParser;
import com.aliyun.ugsv.common.utils.ToastUtil;
import com.zhihu.matisse.MimeType;

import java.util.List;

public class AUIVideoEditor {
    /**
     * 启动编辑器
     */
    public static void startEditor(Context context,String projectJsonPath){
        Intent intent = new Intent(context, AUIVideoEditorActivity.class);
        intent.putExtra("projectJsonPath",projectJsonPath);
        context.startActivity(intent);
    }

    public static void startEditor(Context context, List<String> list) {
        AliyunVideoParam.Builder builder = new AliyunVideoParam.Builder();
        AliyunIImport aliyunIImport = AliyunImportCreator.getImportInstance(context);
        builder.gop(EditorConfig.Companion.getInstance().getGop());
        builder.frameRate(EditorConfig.Companion.getInstance().getFps());
        builder.videoCodec(EditorConfig.Companion.getInstance().getCodec());
        builder.videoQuality(EditorConfig.Companion.getInstance().getVideoQuality());
        builder.scaleMode(EditorConfig.Companion.getInstance().getVideoDisplayMode());
        if (EditorConfig.Companion.getInstance().getBitRate() != EditorConfig.DEFAULT_BITRATE) {
            builder.bitrate(EditorConfig.Companion.getInstance().getBitRate());
        }
        if (EditorConfig.Companion.getInstance().getRatio() != 0) {
            int outputWidth = EditorConfig.Companion.getInstance().getResolution();
            int outputHeight = (int) (EditorConfig.Companion.getInstance().getResolution() / EditorConfig.Companion.getInstance().getRatio());
            builder.outputWidth(outputWidth);
            builder.outputHeight(outputHeight);
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String path = list.get(i);
            NativeParser nativeParser = new NativeParser();
            nativeParser.init(path);
            if(EditorConfig.Companion.getInstance().getRatio() == 0 && i == 0){
                int originWidth = Integer.parseInt(nativeParser.getValue(NativeParser.VIDEO_WIDTH));
                int originHeight = Integer.parseInt(nativeParser.getValue(NativeParser.VIDEO_HEIGHT));
                if(originWidth == 0 || originHeight == 0){
                    ToastUtil.showToast(context, R.string.unsupport_video_format);
                    return;
                }
                int rotation = Integer.parseInt(nativeParser.getValue(NativeParser.VIDEO_ROTATION));
                int width = originWidth;
                int height = originHeight;
                if (rotation == 90 || rotation == 270) {
                    int temp = width;
                    width = height;
                    height = temp;
                }
                int outputWidth = EditorConfig.Companion.getInstance().getResolution();
                int outputHeight = (int) (EditorConfig.Companion.getInstance().getResolution() / (1.f * width / height));

                builder.outputWidth(outputWidth);
                builder.outputHeight(outputHeight);
            }
            String mimeType = FileUtils.getMimeType(path);
            if (MimeType.isVideo(mimeType)) {
                long duration = Long.parseLong(nativeParser.getValue(NativeParser.VIDEO_DURATION)) / 1000;
                aliyunIImport.addMediaClip(new AliyunVideoClip.Builder()
                    .source(path)
                    .startTime(0)
                    .endTime(duration)
                    .duration(duration)
                    .build());
                nativeParser.release();
                nativeParser.dispose();
            } else if (MimeType.isImage(mimeType)) {
                if(MimeType.isGif(mimeType)){
                    try {
                        int frameCount = Integer.parseInt(nativeParser.getValue(NativeParser.VIDEO_FRAME_COUNT));
                        if(frameCount>1){
                            long duration = Long.parseLong(nativeParser.getValue(NativeParser.VIDEO_DURATION))/1000;
                            aliyunIImport.addMediaClip(new AliyunVideoClip.Builder()
                                .source(path)
                                .startTime(0)
                                .endTime(duration)
                                .duration(duration)
                                .build());
                            nativeParser.release();
                            nativeParser.dispose();
                            continue;
                        }
                    } catch (Exception e) {
                        nativeParser.release();
                        nativeParser.dispose();
                        continue;
                    }
                }
                aliyunIImport.addMediaClip(new AliyunImageClip.Builder()
                    .source(path)
                    .duration(3000)
                    .build());
                nativeParser.release();
                nativeParser.dispose();
            }

        }
        aliyunIImport.setVideoParam(builder.build());
        String projectConfigure = aliyunIImport.generateProjectConfigure();
        aliyunIImport.release();
        startEditor(context,projectConfigure);
    }

    public static void startEditorCompose(Activity context, String projectJsonPath, String coverPath, String shareText, int requestCode) {
        Intent intent = new Intent(context, AUIVideoEditorComposeActivity.class);
        intent.putExtra(AUIVideoEditorComposeActivity.KEY_PARAM_CONFIG, projectJsonPath);
        intent.putExtra(AUIVideoEditorComposeActivity.KEY_COVER_PATH, coverPath);
        intent.putExtra(AUIVideoEditorComposeActivity.KEY_SHARE_TEXT, shareText);
        context.startActivityForResult(intent, requestCode);
    }

    public static void startPublish(Activity context, String projectJsonPath, String path, int requestCode) {
        Intent intent = new Intent(context, AUIPublishActivity.class);
        intent.putExtra(AUIPublishActivity.KEY_PARAM_CONFIG, projectJsonPath);
        intent.putExtra(AUIPublishActivity.KEY_COVER_PATH, path);
        context.startActivityForResult(intent, requestCode);
    }

}
