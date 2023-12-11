package com.alivc.live.interactive_common.bean;

import android.text.TextUtils;
import android.util.Log;

import com.alivc.live.player.annotations.AlivcLivePlayVideoStreamType;

/**
 * @author keria
 * @date 2023/10/18
 * @note 直播连麦推拉流用户信息
 */
public class InteractiveUserData {

    private static final String TAG = InteractiveUserData.class.getSimpleName();

    public String channelId;
    public String userId;

    public AlivcLivePlayVideoStreamType videoStreamType;

    public String url;

    public String getKey() {
        if (!TextUtils.isEmpty(channelId) && !TextUtils.isEmpty(userId)) {
            return String.format("%s_%s_%s", channelId, userId, videoStreamType == AlivcLivePlayVideoStreamType.STREAM_CAMERA ? "camera" : "screen");
        } else if (!TextUtils.isEmpty(url)) {
            return url;
        }
        Log.e(TAG, "invalid interactive user data");
        return null;
    }

    @Override
    public String toString() {
        return getKey();
    }
}
