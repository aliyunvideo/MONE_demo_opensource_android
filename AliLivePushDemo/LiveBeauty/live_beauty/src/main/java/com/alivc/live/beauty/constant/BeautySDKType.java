package com.alivc.live.beauty.constant;

public enum BeautySDKType {
    // should be kept!
    QUEEN(BeautyConstant.BEAUTY_QUEEN_MANAGER_CLASS_NAME),

    ;

    private final String managerClassName;

    BeautySDKType(String managerClassName) {
        this.managerClassName = managerClassName;
    }

    public String getManagerClassName() {
        return managerClassName;
    }
}
