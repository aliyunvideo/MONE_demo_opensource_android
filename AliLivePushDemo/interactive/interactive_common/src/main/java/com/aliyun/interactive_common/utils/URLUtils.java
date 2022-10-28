package com.aliyun.interactive_common.utils;

public class URLUtils {

    public static final String RTMP = "rtmp://";
    public static final String HTTP = "http://";
    public static final String ARTC = "artc://";
    public static final String PULL_FLAG = "play";
    public static final String PUSH_FLAG = "push";
    public static final String SDK_APP_ID = "sdkAppId";
    public static final String USER_ID = "userId";
    public static final String TIMESTAMP = "timestamp";
    public static final String TOKEN = "token";
    public static final String NONCE = "nonce";

    private static StringBuilder generateProtocol(int type) {
        StringBuilder stringBuilder = new StringBuilder();
        if (type == 0) {
            stringBuilder.append(RTMP);
        } else if (type == 1) {
            stringBuilder.append(ARTC);
        } else if (type == 2) {
            stringBuilder.append(HTTP);
        }
        return stringBuilder;
    }

    /**
     * 根据 {@link AliLiveUserSigGenerate#ALILIVE_APPID}，{@link AliLiveUserSigGenerate#ALILIVE_APPKEY}，${@link AliLiveUserSigGenerate#getTimesTamp()},
     * USER_ID，CHANNEL_ID 等信息生成推流地址
     *
     * @param channelId 频道 ID
     * @param userId    用户 ID
     * @param type      协议类型，0:rtmp  1：artc  2：http
     * @return 生成的推流地址
     */
    public static String generatePushUrl(String channelId, String userId, int type) {
        String appid = PushDemoTestConstants.getTestInteractiveAppID();
        String appkey = PushDemoTestConstants.getTestInteractiveAppKey();
        String playDomain = PushDemoTestConstants.getTestInteractivePlayDomain();
        long timestamp = AliLiveUserSigGenerate.getTimesTamp();

        String token = AliLiveUserSigGenerate.createToken(appid,
                appkey, channelId, userId, timestamp);
        StringBuilder stringBuilder = generateProtocol(type);
        stringBuilder.append(AliLiveUserSigGenerate.ALILIVE_PUSH_DOMAIN).append("/")
                .append(PUSH_FLAG).append("/")
                .append(channelId)
                .append("?")
                .append(SDK_APP_ID).append("=").append(appid).append("&")
                .append(USER_ID).append("=").append(userId).append("&")
                .append(TIMESTAMP).append("=").append(timestamp).append("&")
                .append(TOKEN).append("=").append(token);
        return stringBuilder.toString();
    }

    /**
     * 根据 {@link AliLiveUserSigGenerate#ALILIVE_APPID}，{@link AliLiveUserSigGenerate#ALILIVE_APPKEY}，${@link AliLiveUserSigGenerate#getTimesTamp()},
     * USER_ID，CHANNEL_ID 等信息生成拉流地址
     *
     * @param channelId 频道 ID
     * @param userId    用户 ID
     * @param type      协议类型，0:rtmp  1：artc  2：http
     * @return 生成的拉流地址
     */
    public static String generatePullUrl(String channelId, String userId, int type) {
        String appid = PushDemoTestConstants.getTestInteractiveAppID();
        String appkey = PushDemoTestConstants.getTestInteractiveAppKey();
        long timestamp = AliLiveUserSigGenerate.getTimesTamp();

        String token = AliLiveUserSigGenerate.createToken(appid,
                appkey, channelId, userId, timestamp);
        StringBuilder stringBuilder = generateProtocol(type);
        stringBuilder.append(AliLiveUserSigGenerate.ALILIVE_PUSH_DOMAIN).append("/")
                .append(PULL_FLAG).append("/")
                .append(channelId)
                .append("?")
                .append(SDK_APP_ID).append("=").append(appid).append("&")
                .append(USER_ID).append("=").append(userId).append("&")
                .append(TIMESTAMP).append("=").append(timestamp).append("&")
                .append(TOKEN).append("=").append(token);
        return stringBuilder.toString();
    }

    /**
     * 根据 {@link AliLiveUserSigGenerate#ALILIVE_APPID}，{@link AliLiveUserSigGenerate#ALILIVE_APPKEY}，${@link AliLiveUserSigGenerate#getTimesTamp()},
     * SER_ID，CHANNEL_ID 等信息生成 CDN 拉流地址
     *
     * @param channelId 频道 ID
     * @param userId    用户 ID
     * @param type      协议类型，0:rtmp  1：artc  2：http
     * @return 生成的拉流地址
     */
    public static String generateCDNUrl(String channelId, String userId, int type) {
        String appid = PushDemoTestConstants.getTestInteractiveAppID();
        String playDomain = PushDemoTestConstants.getTestInteractivePlayDomain();

        StringBuilder stringBuilder = generateProtocol(type);
        stringBuilder.append(playDomain).append("/")
                .append(AliLiveUserSigGenerate.APP_NAME).append("/")
                .append(appid).append("_")
                .append(channelId).append("_")
                .append(userId).append("_")
                .append(AliLiveUserSigGenerate.SOURCE_TYPE);
        return stringBuilder.toString();
    }
}
