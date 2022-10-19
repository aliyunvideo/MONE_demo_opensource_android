package com.aliyun.svideo.base.utils;

import android.util.Log;

import com.aliyun.svideosdk.transcode.NativeParser;

import java.util.LinkedHashMap;

/**
 * @author cross_ly DATE 2019/08/16
 * <p>描述:视频信息工具类，基于{@link com.aliyun.svideosdk.transcode.NativeParser}
 */
public class VideoInfoUtils {

    /**
     * 打印视频信息
     * @param outputPath path
     */
    public static void printVideoInfo(String outputPath) {

        NativeParser nativeParser = new NativeParser();
        try {
            nativeParser.init(outputPath);
            LinkedHashMap<String, Object> infoMap = new LinkedHashMap<>();
            int height = Integer.parseInt(nativeParser.getValue(NativeParser.VIDEO_HEIGHT));
            int width = Integer.parseInt(nativeParser.getValue(NativeParser.VIDEO_WIDTH));
            int gop = Integer.parseInt(nativeParser.getValue(NativeParser.VIDEO_GOP));
            int frameCount = Integer.parseInt(nativeParser.getValue(NativeParser.VIDEO_FRAME_COUNT));
            long videoDuration = Long.parseLong(nativeParser.getValue(NativeParser.VIDEO_DURATION));
            long bitrate = Long.parseLong(nativeParser.getValue(NativeParser.VIDEO_BIT_RATE));
            long audioDuration = Long.parseLong(nativeParser.getValue(NativeParser.AUDIO_DURATION));
            infoMap.put("path", outputPath);
            infoMap.put("width", width);
            infoMap.put("height", height);
            infoMap.put("videoDuration", videoDuration);
            infoMap.put("audioDuration", audioDuration);
            infoMap.put("gop", gop);
            infoMap.put("frameRate", (float)frameCount / videoDuration * 1000 * 1000);
            infoMap.put("bitrate", bitrate);
            Log.i("printVideoInfo", infoMap.toString());
        } catch (Exception ex) {
            Log.e("printVideoInfo", ex.getMessage());
        } finally {
            nativeParser.release();
            nativeParser.dispose();
        }
    }

}
