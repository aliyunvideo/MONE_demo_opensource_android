package com.aliyun.player.alivcplayerexpand.view.dlna.domain;

/**
 * 控制点
 */

public interface IControlPoint<T> {

    /**
     * @return  返回控制点
     */
    T getControlPoint();

    /**
     * 设置控制点
     * @param controlPoint  控制点
     */
    void setControlPoint(T controlPoint);

    /**
     * 销毁 清空缓存
     */
    void destroy();
}
