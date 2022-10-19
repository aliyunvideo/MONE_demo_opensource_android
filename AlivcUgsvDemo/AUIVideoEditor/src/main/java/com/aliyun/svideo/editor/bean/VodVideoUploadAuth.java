package com.aliyun.svideo.editor.bean;

import android.util.Log;

import com.aliyun.common.global.AliyunTag;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by macpro on 2017/11/8.
 */

public class VodVideoUploadAuth {

    private String videoId;
    private String uploadAddress;
    private String uploadAuth;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getUploadAddress() {
        return uploadAddress;
    }

    public void setUploadAddress(String uploadAddress) {
        this.uploadAddress = uploadAddress;
    }

    public String getUploadAuth() {
        return uploadAuth;
    }

    public void setUploadAuth(String uploadAuth) {
        this.uploadAuth = uploadAuth;
    }

    /**
     * 富血模式
     * 获取video上传的token信息
     * @param json String
     * @return VodVideoUploadAuth
     */
    public static VodVideoUploadAuth getVideoTokenInfo(String json) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        try {
            JsonElement jsonElement = parser.parse(json);
            JsonObject obj = jsonElement.getAsJsonObject();
            VodVideoUploadAuth tokenInfo = gson.fromJson(obj.get("data"), VodVideoUploadAuth.class);
            Log.d(AliyunTag.TAG, tokenInfo.toString());
            return tokenInfo;
        } catch (Exception e) {
            Log.e(AliyunTag.TAG, "Get TOKEN info failed, json :" + json, e);
            return null;
        }
    }

    @Override
    public String toString() {
        return "VodVideoUploadAuth{" +
               "videoId='" + videoId + '\'' +
               ", uploadAddress='" + uploadAddress + '\'' +
               ", uploadAuth='" + uploadAuth + '\'' +
               '}';
    }
}
