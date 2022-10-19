package com.aliyun.player.alivcplayerexpand.view.dlna.domain;

import org.fourthline.cling.controlpoint.ControlPoint;

/**
 * cling 控制点
 */

public class ClingControlPoint implements IControlPoint<ControlPoint> {

    private static ClingControlPoint INSTANCE = null;
    private ControlPoint mControlPoint;

    private ClingControlPoint() {
    }

    public static ClingControlPoint getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClingControlPoint();
        }
        return INSTANCE;
    }

    @Override
    public ControlPoint getControlPoint() {
        return mControlPoint;
    }

    @Override
    public void setControlPoint(ControlPoint controlPoint) {
        mControlPoint = controlPoint;
    }

    @Override
    public void destroy() {
        mControlPoint = null;
        INSTANCE = null;
    }
}
