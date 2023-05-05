package com.alivc.player.videolist.auivideolistcommon;

import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.OnCompletionListener;
import com.alivc.player.videolist.auivideolistcommon.listener.OnNetWorkListener;
import com.alivc.player.videolist.auivideolistcommon.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class AUIVideoListController {

    private static final Gson gson = new Gson();
    private VideoInfo mLastVideoInfo;

    public void reloadData(String url, OnCompletionListener listener) {
        HttpUtils.get(url, new OnNetWorkListener() {

            @Override
            public void onSuccess(Object obj) {
                Response response = (Response) obj;
                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String string = body.string();
                        List<VideoInfo> videoInfoList = gson.fromJson(string, new TypeToken<List<VideoInfo>>() {
                        }.getType());
                        mLastVideoInfo = videoInfoList.get(videoInfoList.size() - 1);
                        listener.onCompletion(true, videoInfoList, "", mLastVideoInfo.getId());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    listener.onCompletion(false, null, msg, 0);
                }
            }

            @Override
            public void onError(String msg) {
                if (listener != null) {
                    listener.onCompletion(false, null, msg, 0);
                }
            }
        });
    }

    public void loadMoreData(String url, OnCompletionListener listener) {
        int id = mLastVideoInfo == null ? 0 : mLastVideoInfo.getId();
        url += "&lastIndex="+id;
        HttpUtils.get(url, new OnNetWorkListener() {
            @Override
            public void onSuccess(Object obj) {
                Response response = (Response) obj;
                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String string = body.string();
                        List<VideoInfo> videoInfoList = gson.fromJson(string, new TypeToken<List<VideoInfo>>() {
                        }.getType());
                        mLastVideoInfo = videoInfoList.get(videoInfoList.size() - 1);
                        listener.onCompletion(true, videoInfoList, "", mLastVideoInfo.getId());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    listener.onCompletion(false, null, msg, mLastVideoInfo == null ? 0 : mLastVideoInfo.getId());
                }
            }

            @Override
            public void onError(String msg) {
                if (listener != null) {
                    listener.onCompletion(false, null, msg, mLastVideoInfo == null ? 0 : mLastVideoInfo.getId());
                }
            }
        });
    }
}