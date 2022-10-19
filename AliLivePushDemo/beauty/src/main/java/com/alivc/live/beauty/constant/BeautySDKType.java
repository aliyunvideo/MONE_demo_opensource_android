package com.alivc.live.beauty.constant;

import static com.alivc.live.beauty.constant.BeautyConstant.BEAUTY_INTERACT_QUEEN_MANAGER_CLASS_NAME;
import static com.alivc.live.beauty.constant.BeautyConstant.BEAUTY_QUEEN_DATA_INJECTOR_CLASS_NAME;
import static com.alivc.live.beauty.constant.BeautyConstant.BEAUTY_QUEEN_MANAGER_CLASS_NAME;

public enum BeautySDKType {
    // should be kept!
    QUEEN(BEAUTY_QUEEN_MANAGER_CLASS_NAME, BEAUTY_QUEEN_DATA_INJECTOR_CLASS_NAME),
    INTERACT_QUEEN(BEAUTY_INTERACT_QUEEN_MANAGER_CLASS_NAME, BEAUTY_QUEEN_DATA_INJECTOR_CLASS_NAME);

    private final String managerClassName;
    private final String dataInjectorClassName;

    BeautySDKType(String managerClassName, String dataInjectorClassName) {
        this.managerClassName = managerClassName;
        this.dataInjectorClassName = dataInjectorClassName;
    }

    public String getManagerClassName() {
        return managerClassName;
    }

    public String getDataInjectorClassName() {
        return dataInjectorClassName;
    }
}
