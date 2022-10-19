package com.aliyun.player.alivcplayerexpand.view.dlna.callback;

import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IResponse;

/**
 * 说明：设备控制操作 回调
 * 作者：zhouzhan
 * 日期：17/7/4 10:56
 */

public interface ControlCallback<T> {

    void success(IResponse<T> response);

    void fail(IResponse<T> response);
}
