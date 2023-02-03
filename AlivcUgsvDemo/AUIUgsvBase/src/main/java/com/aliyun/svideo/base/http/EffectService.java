/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.base.http;

import com.aliyun.ugsv.common.jasonparse.JSONSupportImpl;
import com.aliyun.ugsv.common.qupaiokhttp.BaseHttpRequestCallback;
import com.aliyun.ugsv.common.qupaiokhttp.HttpRequest;
import com.aliyun.ugsv.common.qupaiokhttp.RequestParams;
import com.aliyun.ugsv.common.qupaiokhttp.StringHttpRequestCallback;
import com.aliyun.svideo.base.Form.AnimationEffectForm;
import com.aliyun.svideo.base.Form.FontForm;
import com.aliyun.svideo.base.Form.IMVForm;
import com.aliyun.svideo.base.Form.PasterForm;
import com.aliyun.svideo.base.Form.ResourceForm;
import com.aliyun.svideosdk.common.struct.form.PreviewPasterForm;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 尊敬的客户，此Server服务只用于demo演示使用，无法作为商用服务上线使用，我们不能保证这个服务的稳定性，高并发性，请自行搭建自己的Server服务，
 * 如何集成自己的Server服务详见文档：https://help.aliyun.com/document_detail/108783.html?spm=a2c4g.11186623.6.1075.a70a3a4895Qysq。
 */
public class EffectService {
    private Gson mGson = new GsonBuilder().disableHtmlEscaping().create();
    private static final String KEY_PACKAGE_NAME = "PACKAGE_NAME";

    public static final int EFFECT_TEXT = 1;        //字体
    public static final int EFFECT_PASTER = 2;      //动图
    public static final int EFFECT_MV = 3;          //MV
    public static final int EFFECT_FILTER = 4;      //滤镜
    public static final int EFFECT_MUSIC = 5;       //音乐
    public static final int EFFECT_CAPTION = 6;     //字幕
    public static final int EFFECT_FACE_PASTER = 7; //人脸动图
    public static final int EFFECT_IMG = 8;         //静态贴纸
    public static final int ANIMATION_FILTER = 9;   //动态滤镜
    public static final int EFFECT_TRANSITION = 10;   //转场特效
    /**
     * 素材分发服务为官方demo演示使用，无法达到商业化使用程度。请自行搭建相关的服务
     */
    public static final String BASE_URL = "https://alivc-demo.aliyuncs.com";

    /**
     * 素材分发服务为官方demo演示使用，无法达到商业化使用程度。请自行搭建相关的服务
     */
    public void loadEffectPaster(String packageName, final HttpCallback<List<ResourceForm>> callback) {
        String url = new StringBuilder(BASE_URL).append("/resource/getPasterInfo").toString();
        RequestParams params = new RequestParams();
        params.addFormDataPart("type", 2);
        params.addFormDataPart(KEY_PACKAGE_NAME, packageName );
        get(url, params, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    List<ResourceForm> resourceList = mGson.fromJson(jsonArray.toString(), new TypeToken<List<ResourceForm>>() {
                    } .getType());

                    if (callback != null) {
                        callback.onSuccess(resourceList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }

            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                if (callback != null) {
                    callback.onFailure(new Throwable(msg));
                }
            }
        });

    }
    /**
     * 素材分发服务为官方demo演示使用，无法达到商业化使用程度。请自行搭建相关的服务
     */
    public void getPasterListById(String packageName, int id, final HttpCallback<List<PasterForm>> callback) {
        StringBuilder resUrl = new StringBuilder();
        resUrl.append(BASE_URL).append("/resource/getPasterList");
        RequestParams params = new RequestParams();
        params.addFormDataPart("type", 2);
        params.addFormDataPart(KEY_PACKAGE_NAME, packageName );
        params.addFormDataPart("pasterId", id);
        get(resUrl.toString(), params, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    List<PasterForm> resourceList = mGson.fromJson(jsonArray.toString(), new TypeToken<List<PasterForm>>() {
                    } .getType());
                    if (callback != null) {
                        callback.onSuccess(resourceList);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }

            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                if (callback != null) {
                    callback.onFailure(new Throwable(msg));
                }
            }
        } );
    }
    /**
     * 素材分发服务为官方demo演示使用，无法达到商业化使用程度。请自行搭建相关的服务
     */
    public void getCaptionListById(String packageName, int id, final HttpCallback<List<PasterForm>> callback) {
        StringBuilder resUrl = new StringBuilder();
        resUrl.append(BASE_URL).append("/resource/getTextPasterList");
        RequestParams params = new RequestParams();
        params.addFormDataPart("textPasterId", id);
        params.addFormDataPart(KEY_PACKAGE_NAME, packageName );
        get(resUrl.toString(), params, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    List<PasterForm> resourceList = mGson.fromJson(jsonArray.toString(), new TypeToken<List<PasterForm>>() {
                    } .getType());

                    if (callback != null) {
                        callback.onSuccess(resourceList);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }

            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                if (callback != null) {
                    callback.onFailure(new Throwable(msg));
                }
            }
        } );
    }
    /**
     * 素材分发服务为官方demo演示使用，无法达到商业化使用程度。请自行搭建相关的服务
     * 通过字体id获取字体信息
     */
    public void getFontById(String packageName, int id, final HttpCallback<FontForm> callback) {
        StringBuilder resUrl = new StringBuilder();
        resUrl.append(BASE_URL).append("/resource/getFont");
        RequestParams params = new RequestParams();
        params.addFormDataPart("fontId", id);
        params.addFormDataPart(KEY_PACKAGE_NAME, packageName );
        get(resUrl.toString(), params, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                super.onSuccess(s);

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONSupportImpl jsonSupport = new JSONSupportImpl();
                    FontForm fontForm = jsonSupport.readValue(data.toString(), FontForm.class);
                    if (callback != null) {
                        callback.onSuccess(fontForm);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }

            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                if (callback != null) {
                    callback.onFailure(new Throwable(msg));
                }
            }
        });
    }
    /**
     * 素材分发服务为官方demo演示使用，无法达到商业化使用程度。请自行搭建相关的服务
     * 获取前置动图
     */
    public void loadFrontEffectPaster(String packageName, final HttpCallback<List<PreviewPasterForm>> callback) {
        StringBuilder resUrl = new StringBuilder();
        resUrl.append(BASE_URL).append("/resource/getFrontPasterList");
        RequestParams params = new RequestParams();
        params.addFormDataPart(KEY_PACKAGE_NAME, packageName );
        get(resUrl.toString(), params, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                super.onSuccess(s);
                JSONSupportImpl jsonSupport = new JSONSupportImpl();
                List<PreviewPasterForm> resources;
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    resources = jsonSupport.readListValue(jsonArray.toString(),
                    new TypeToken<List<PreviewPasterForm>>() {
                    } .getType());

                    if (callback != null) {
                        callback.onSuccess(resources);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                if (callback != null) {
                    callback.onFailure(new Throwable(msg));
                }
            }
        } );

    }
    /**
     * 素材分发服务为官方demo演示使用，无法达到商业化使用程度。请自行搭建相关的服务
     */
    public void loadEffectMv(String packageName,
                             final HttpCallback<List<IMVForm>> callback) {
        RequestParams params = new RequestParams();
        params.addFormDataPart(KEY_PACKAGE_NAME, packageName );
        String url = new StringBuilder(BASE_URL).append("/resource/getMv").toString();
        get(url, params, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                super.onSuccess(s);

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    List<IMVForm> resourceList = mGson.fromJson(jsonArray.toString(), new TypeToken<List<IMVForm>>() {
                    } .getType());
                    if (callback != null) {
                        callback.onSuccess(resourceList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                if (callback != null) {
                    callback.onFailure(new Throwable(msg));
                }
            }
        });


    }

    /**
     * 素材分发服务为官方demo演示使用，无法达到商业化使用程度。请自行搭建相关的服务
     */
    public void loadAnimationFilter(String packageName,
                                    final HttpCallback<List<AnimationEffectForm>> callback) {
        RequestParams params = new RequestParams();
        params.addFormDataPart(KEY_PACKAGE_NAME, packageName );
        String url = new StringBuilder(BASE_URL).append("/resource/getMotionFilter").toString();
        get(url, params, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                super.onSuccess(s);

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    List<AnimationEffectForm> resourceList = mGson.fromJson(jsonArray.toString(), new TypeToken<List<AnimationEffectForm>>() {
                    } .getType());
                    if (callback != null) {
                        callback.onSuccess(resourceList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                if (callback != null) {
                    callback.onFailure(new Throwable(msg));
                }
            }
        });


    }

    /**
     * 素材分发服务为官方demo演示使用，无法达到商业化使用程度。请自行搭建相关的服务
     */
    public void loadTransitonEffect(String packageName,
                                    final HttpCallback<List<AnimationEffectForm>> callback) {
        RequestParams params = new RequestParams();
        params.addFormDataPart(KEY_PACKAGE_NAME, packageName );
        String url = new StringBuilder(BASE_URL).append("/resource/getTransitions").toString();
        get(url, params, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                super.onSuccess(s);

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    List<AnimationEffectForm> resourceList = mGson.fromJson(jsonArray.toString(), new TypeToken<List<AnimationEffectForm>>() {
                    } .getType());
                    if (callback != null) {
                        callback.onSuccess(resourceList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                if (callback != null) {
                    callback.onFailure(new Throwable(msg));
                }
            }
        });


    }

    /**
     * 素材分发服务为官方demo演示使用，无法达到商业化使用程度。请自行搭建相关的服务
     */
    public void loadEffectCaption(String packageName, final HttpCallback<List<ResourceForm>> callback) {
        String url = new StringBuilder(BASE_URL).append("/resource/getTextPaster").toString();
        RequestParams params = new RequestParams();
        params.addFormDataPart(KEY_PACKAGE_NAME, packageName );
        get(url, params, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    List<ResourceForm> resourceList = mGson.fromJson(jsonArray.toString(), new TypeToken<List<ResourceForm>>() {
                    } .getType());
                    if (callback != null) {
                        callback.onSuccess(resourceList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }

            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                if (callback != null) {
                    callback.onFailure(new Throwable(msg));
                }
            }
        });

    }

    /**
     * 素材分发服务为官方demo演示使用，无法达到商业化使用程度。请自行搭建相关的服务
     */
    public void getMusicDownloadUrlById(String packageName, String musicId, final HttpCallback<String> callback) {
        String url = BASE_URL + "/music/getPlayPath";
        final RequestParams params = new RequestParams();
        params.addFormDataPart("musicId", musicId);
        //params.addFormDataPart(KEY_PACKAGE_NAME, packageName );
        get(url, params, new StringHttpRequestCallback() {
            @Override
            protected void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    String playPath = dataObject.getString("playPath");
                    if (callback != null) {
                        callback.onSuccess(playPath);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                if (callback != null) {
                    callback.onFailure(new Throwable(msg));
                }
            }
        });

    }

    private void get(String url, RequestParams params, BaseHttpRequestCallback callback) {
        if (sAppInfo != null) {
            Set<Map.Entry<String, String>> entries = sAppInfo.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                params.addHeader(entry.getKey(), entry.getValue());
            }

        }
        HttpRequest.get(url, params, callback);
    }

    private static Map<String, String> sAppInfo;

    /**
     * 设置app信息
     * @param appName 应用名
     * @param packageName 应用包名
     * @param versionName 版本名
     * @param versionCode 版本号
     */
    public static void setAppInfo(String appName, String packageName, String versionName, long versionCode) {
        if (sAppInfo == null) {
            sAppInfo = new HashMap<>();
            try {
                sAppInfo.put("appName", URLEncoder.encode(appName, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sAppInfo.put("packageName", packageName);
            sAppInfo.put("appVersionName", versionName);
            sAppInfo.put("appVersionCode", String.valueOf(versionCode));
        }
    }


}
