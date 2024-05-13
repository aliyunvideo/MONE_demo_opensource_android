package com.alivc.live.commonbiz.test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @note 备注：该文件被直播连麦控制台的demo试用所链接，请谨慎改动此文件的目录位置。
 */
public class AliLiveUserSigGenerate {

    /**
     * APP_ID，在阿里云控制台应用管理页面创建和查看。
     */
    public static final String ALILIVE_APPID = "PLACEHOLDER";
    /**
     * APP_KEY，在阿里云控制台应用管理页面创建和查看。
     */
    public static final String ALILIVE_APPKEY = "PLACEHOLDER";

    /**
     * CDN_DOMAIN，在阿里云控制台应用管理页面创建和查看。
     */
    public static final String ALILIVE_PLAY_DOMAIN = "PLACEHOLDER";

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
        StringBuilder stringBuilder = new StringBuilder()
                .append(appid)
                .append(appkey)
                .append(channelId)
                .append(userId)
                .append(timestamp);
        return getSHA256(stringBuilder.toString());
    }

    /**
     * 字符串签名
     *
     * @param str 输入源
     * @return 返回签名
     */
    public static String getSHA256(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(str.getBytes(StandardCharsets.UTF_8));
            return byte2Hex(hash);
        } catch (NoSuchAlgorithmException e) {
            // Consider logging the exception and/or re-throwing as a RuntimeException
            e.printStackTrace();
        }
        return "";
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                // Use single quote for char
                stringBuilder.append('0');
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }
}
