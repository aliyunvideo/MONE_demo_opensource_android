package com.aliyun.player.alivcplayerexpand.view.dlna.domain;

/**
 * 设备控制 返回结果
 */

public interface IResponse<T> {

    T getResponse();

    void setResponse(T response);
}
