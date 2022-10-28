package com.aliyun.interactive_common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AliLiveUserSigGenerate {

    /**
     * APP_ID。在阿里云控制台应用管理页面创建和查看。
     */
    public static final String ALILIVE_APPID = "PLACEHOLDER";
    /**
     * APP_KEY。在阿里云控制台应用管理页面创建和查看。
     */
    public static final String ALILIVE_APPKEY = "PLACEHOLDER";

    /**
     * DOMAIN 固定字段。
     */
    public static final String ALILIVE_PUSH_DOMAIN = "live.aliyun.com";
    /**
     * CDN_DOMAIN。 在阿里云控制台应用管理页面创建和查看。
     */
    public static final String ALILIVE_PLAY_DOMAIN = "PLACEHOLDER";

    public static final String APP_NAME = "live";
    public static final String SOURCE_TYPE = "camera";

    /**
     * 过期时间
     * 时间单位秒，代表令牌有效时间。可设置最大范围是小于等于1天，建议不要设置的过短或超过1天，超过1天会不安全。
     * 默认时间1天。1天 = 60 x  60  x 24。
     */
    public static long getTimesTamp() {
        return System.currentTimeMillis() / 1000 + 60 * 60 * 24;
    }

    /**
     * 根据 appid，appkey，channelId，userId，nonc，timestamp 生层 token
     *
     * @param appid     应用ID。在控制台应用管理页面创建和查看。
     * @param appkey    在控制台应用管理页面创建和查看。
     * @param channelId 频道 ID
     * @param userId    用户 ID
     * @param timestamp 过期时间戳
     * @return token
     */
    public static String createToken(String appid, String appkey, String channelId, String userId, long timestamp) {
        String stringBuilder = appid +
                appkey +
                channelId +
                userId +
                timestamp;
        return getSHA256(stringBuilder);
    }

    /**
     * 字符串签名
     *
     * @param str 输入源
     * @return 返回签名
     */
    public static String getSHA256(String str) {
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        String temp = null;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

}
