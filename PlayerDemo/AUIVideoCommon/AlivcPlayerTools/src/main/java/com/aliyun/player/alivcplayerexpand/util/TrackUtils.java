package com.aliyun.player.alivcplayerexpand.util;

import android.text.TextUtils;

import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.TrackInfo;

import java.util.ArrayList;
import java.util.List;

public class TrackUtils {
    public static List<TrackInfo> getTrackInfoListWithTrackInfoType(TrackInfo.Type trackInfoType, MediaInfo aliyunMediaInfo) {
        List<TrackInfo> trackInfoList = new ArrayList<>();
        if (aliyunMediaInfo != null && aliyunMediaInfo.getTrackInfos() != null) {
            for (TrackInfo trackInfo : aliyunMediaInfo.getTrackInfos()) {
                TrackInfo.Type type = trackInfo.getType();
                if (type == trackInfoType) {

                    if (trackInfoType == TrackInfo.Type.TYPE_SUBTITLE) {
                        //字幕
                        if (!TextUtils.isEmpty(trackInfo.getSubtitleLang())) {
                            trackInfoList.add(trackInfo);
                        }
                    } else if (trackInfoType == TrackInfo.Type.TYPE_AUDIO) {
                        //音轨
                        if (!TextUtils.isEmpty(trackInfo.getAudioLang())) {
                            trackInfoList.add(trackInfo);
                        }
                    } else if (trackInfoType == TrackInfo.Type.TYPE_VIDEO) {
                        //码率
                        if (trackInfo.getVideoBitrate() > 0) {
                            if (trackInfoList.size() == 0) {
                                //添加自动码率
                                trackInfoList.add(trackInfo);
                            }
                            trackInfoList.add(trackInfo);
                        }
                    } else if (trackInfoType == TrackInfo.Type.TYPE_VOD) {
                        //清晰度
                        if (!TextUtils.isEmpty(trackInfo.getVodDefinition())) {
                            trackInfoList.add(trackInfo);
                        }
                    }

                }
            }
        }
        return trackInfoList;
    }
}
