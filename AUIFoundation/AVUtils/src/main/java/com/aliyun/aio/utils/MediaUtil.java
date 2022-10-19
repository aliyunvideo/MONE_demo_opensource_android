package com.aliyun.aio.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;

/**
 * Created by apple on 2017/10/21.
 */

public class MediaUtil {
    public static int getFrameRate(String output) {
        if (Build.VERSION.SDK_INT >= 16) {
            MediaExtractor mediaExtractor = new MediaExtractor();
            int frameRate = 0; //may be default
            FileInputStream fis = null;
            try {
                File file = new File(output);
                fis = new FileInputStream(file);
                FileDescriptor fd = fis.getFD();
                //Adjust data source as per the requirement if file, URI, etc.
                mediaExtractor.setDataSource(fd);
                int numTracks = mediaExtractor.getTrackCount();
                for (int i = 0; i < numTracks; ++i) {
                    MediaFormat format = mediaExtractor.getTrackFormat(i);
                    String mime = format.getString(MediaFormat.KEY_MIME);
                    if (mime.startsWith("video/")) {
                        if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                            frameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mediaExtractor.release();
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return frameRate;
        } else {
            return 0;
        }
    }

    /**
     * 获取视频的时长
     * @param source
     * @return
     */
    public static long getVideoDuration(String source) {
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(source);
            return Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            return 0;
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
    }
}
