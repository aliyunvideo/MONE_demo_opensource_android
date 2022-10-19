package com.alivc.live.pusher.demo.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.alivc.live.pusher.demo.ContextUtils;
import com.alivc.live.pusher.demo.LiveApplication;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 对推流URL增加鉴权逻辑
 */
public class PushAuth {

    private final static String PRE_GROUP = "push_stream";
    private final static String PRE_NAME = "name";

    private final static String DEFAULT_STREAM_NAME = "stream";
    private final static String PUSH_URL = "artp://testdomain.com/app/name";
    private final static String APP_NAME = "test";

    public static String wrapAuthUrl() {
        String streamName = getStreamName();
        String uriPartStr = "/" + APP_NAME + "/" + streamName;

        long curTimemills = System.currentTimeMillis() / 1000 + 2 * 60 * 60;
        String concatString = uriPartStr + "-" + curTimemills + "-0-0-aaabbb";
        String md5Str = md5(concatString);
        String authKey = curTimemills + "-0-0-" + md5Str;
        return PUSH_URL + uriPartStr + "?&auth_key=" + authKey;
    }

    private static String getStreamName() {
        String streamName = null;
        SharedPreferences sharePref = ContextUtils.getContext().getSharedPreferences(PRE_GROUP, Context.MODE_PRIVATE);
        if (sharePref != null) {
            streamName = sharePref.getString(PRE_NAME, null);
        }
        if (streamName == null) {
            streamName = DEFAULT_STREAM_NAME + ((int) ((Math.random() * 9 + 1) * 1000));
            if (sharePref != null) {
                sharePref.edit().putString(PRE_NAME, streamName).apply();
            }
        }
        return streamName;
    }

    private static String md5(String originStr) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    originStr.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

}
