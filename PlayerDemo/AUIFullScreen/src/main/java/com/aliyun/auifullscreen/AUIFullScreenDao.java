package com.aliyun.auifullscreen;

import com.aliyun.auiplayerserver.bean.VideoInfo;
import com.aliyun.auiplayerserver.flowfeed.HomePageFetcher;
import com.aliyun.player.source.StsInfo;
import com.aliyun.player.source.VidSts;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AUIFullScreenDao {

    public void getStsDataSource(final OnGetStsDataSourceListener listener){
        final HomePageFetcher homePageFetcher = new HomePageFetcher();
        homePageFetcher.requestVideoStsInfo(new HomePageFetcher.VideoStsInfoCallback() {
            @Override
            public void onResult(@NotNull final StsInfo stsInfo) {
                
                homePageFetcher.initPlayerListDatas(0L, false, new HomePageFetcher.VideoListDataBack() {
                    @Override
                    public void onResult(@NotNull List<VideoInfo> list) {
                        if(list.size() > 0){
                            VideoInfo videoInfo = list.get(0);
                            String videoId = videoInfo.getVideoId();
                            VidSts vidSts = new VidSts();
                            vidSts.setVid(videoId);
                            vidSts.setAccessKeyId(stsInfo.getAccessKeyId());
                            vidSts.setAccessKeySecret(stsInfo.getAccessKeySecret());
                            vidSts.setSecurityToken(stsInfo.getSecurityToken());

                            if(listener != null){
                                listener.getStsDataSourceSuccess(vidSts);
                            }
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        if(listener != null){
                            listener.getStsDataSourceFailure(msg);
                        }
                    }
                });
            }

            @Override
            public void onError(String msg) {
                if(listener != null){
                    listener.getStsDataSourceFailure(msg);
                }
            }
        });
    }

    public interface OnGetStsDataSourceListener{
        void getStsDataSourceSuccess(VidSts vidSts);
        void getStsDataSourceFailure(String error);
    }
}
