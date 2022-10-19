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

public class VodImageUploadAuth {

    private String uploadAddress;
    private String uploadAuth;
    private String imageId;
    private String imageURL;

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

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * 富血模式
     * 获取image上传的token信息
     * @param json String
     * @return VodImageUploadAuth
     */
    public static VodImageUploadAuth getImageTokenInfo(String json) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        try {
            JsonElement jsonElement = parser.parse(json);
            JsonObject obj = jsonElement.getAsJsonObject();
            VodImageUploadAuth tokenInfo = gson.fromJson(obj.get("data"), VodImageUploadAuth.class);
            Log.d(AliyunTag.TAG, tokenInfo.toString());
            return tokenInfo;
        } catch (Exception e) {
            Log.e(AliyunTag.TAG, "Get TOKEN info failed, json :" + json, e);
            return null;
        }
    }

    @Override
    public String toString() {
        return "VodImageUploadAuth{" +
               "uploadAddress='" + uploadAddress + '\'' +
               ", uploadAuth='" + uploadAuth + '\'' +
               ", imageId='" + imageId + '\'' +
               ", imageURL='" + imageURL + '\'' +
               '}';
    }
}
