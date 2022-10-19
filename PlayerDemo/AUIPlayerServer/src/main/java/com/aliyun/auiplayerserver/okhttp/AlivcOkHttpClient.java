package com.aliyun.auiplayerserver.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * okhttp简易封装
 */
public class AlivcOkHttpClient {
    private static AlivcOkHttpClient alivcOkHttpClient;
    private OkHttpClient okHttpClient;
    private final OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder().addNetworkInterceptor(new LoggingIntcepetor());
    private Handler handler;

    private AlivcOkHttpClient() {
        handler = new Handler(Looper.getMainLooper());
        build();
    }

    public static AlivcOkHttpClient getInstance() {
        if (alivcOkHttpClient == null) {
            synchronized (AlivcOkHttpClient.class) {
                if (alivcOkHttpClient == null) {
                    alivcOkHttpClient = new AlivcOkHttpClient();
                }
            }
        }

        return alivcOkHttpClient;
    }

    private void build() {
        okHttpBuilder.connectTimeout(10, TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(10, TimeUnit.SECONDS);
        okHttpBuilder.readTimeout(10, TimeUnit.SECONDS);
        okHttpClient = okHttpBuilder.build();

    }

    class StringCallBack implements Callback {
        private HttpCallBack httpCallBack;
        private Request request;

        public StringCallBack(Request request, HttpCallBack httpCallBack) {
            this.request = request;
            this.httpCallBack = httpCallBack;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            final IOException fe = e;
            if (httpCallBack != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        httpCallBack.onError(request, fe);
                    }
                });
            }
        }

        @Override
        public void onResponse(final Call call, okhttp3.Response response) throws IOException {
            final String result = response.body().string();

            try {
                final JSONObject jsonObject = new JSONObject(result);
                if ("200".equals(jsonObject.getString("code")) || "20000".equals(jsonObject.getString("code"))) {
                    if (httpCallBack != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                httpCallBack.onSuccess(request, result);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                httpCallBack.onError(request, new IOException("json error"));
                            }
                        });
                    }
                } else {
                    if (httpCallBack != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    httpCallBack.onError(request, new IOException(jsonObject.getString("message")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    class StringCallBack2 implements Callback {
        private final HttpCallBack httpCallBack;
        private final Request request;

        public StringCallBack2(Request request, HttpCallBack httpCallBack) {
            this.request = request;
            this.httpCallBack = httpCallBack;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            final IOException fe = e;
            if (httpCallBack != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        httpCallBack.onError(request, fe);
                    }
                });
            }
        }

        @Override
        public void onResponse(final Call call, okhttp3.Response response) throws IOException {
            final String result = response.body().string();
            try {
                final JSONObject jsonObject = new JSONObject(result);
                final String data = jsonObject.getString("data");
                if (!TextUtils.isEmpty(data)) {
                    if (httpCallBack != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                httpCallBack.onSuccess(request, data);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                httpCallBack.onError(request, new IOException("json error"));
                            }
                        });
                    }
                } else {
                    if (httpCallBack != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    httpCallBack.onError(request, new IOException(jsonObject.getString("message")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 构建POS请求的参数体。
     *
     * @return 组装参数后的FormBody。
     */
    public FormBody formBody(Map<String, String> param) {
        FormBody.Builder builder = new FormBody.Builder();
        if (param != null) {
            Set<String> keys = param.keySet();
            if (!keys.isEmpty()) {
                for (String key : keys) {
                    String value = param.get(key);
                    if (value != null) {
                        builder.add(key, value);
                    }
                }
            }
        }
        return builder.build();
    }

    /**
     * 当GET请求携带参数的时候，将参数以key=value的形式拼装到GET请求URL的后面，并且中间以?符号隔开。
     *
     * @return 携带参数的URL请求地址。
     */
    public String urlWithParam(String url, Map<String, ? extends Object> params) {
        if (params != null) {
            Set<String> keys = params.keySet();
            if (!keys.isEmpty()) {
                StringBuilder paramsBuilder = new StringBuilder();
                boolean needAnd = false;
                for (String key : keys) {
                    if(params.get(key) == null){
                        continue;
                    }
                    if (needAnd) {
                        paramsBuilder.append("&");
                    }
                    paramsBuilder.append(key).append("=").append(params.get(key));
                    needAnd = true;
                }
                return url + "?" + paramsBuilder.toString();
            }
        }
        return url;
    }

    public void get(String url, HttpCallBack httpCallBack) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new StringCallBack(request, httpCallBack));
    }

    public void get2(String url, HttpCallBack httpCallBack) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new StringCallBack2(request, httpCallBack));
    }

    /**
     * 带参数带get请求
     */
    public void get(String url, HashMap<String, ? extends Object> params, HttpCallBack httpCallBack) {

        Request request = new Request.Builder().url(urlWithParam(url, params)).build();
        okHttpClient.newCall(request).enqueue(new StringCallBack(request, httpCallBack));
    }

    /**
     * post请求
     */
    public void post(String url, Map<String, String> params, HttpCallBack httpCallBack) {
        Request request = new Request.Builder().url(url).post(formBody(params)).build();
        okHttpClient.newCall(request).enqueue(new StringCallBack(request, httpCallBack));
    }

    public interface HttpCallBack {
        /**
         * 错误回调
         */
        void onError(Request request, IOException e);

        /**
         * 成功回调
         */
        void onSuccess(Request request, String result);
    }
}