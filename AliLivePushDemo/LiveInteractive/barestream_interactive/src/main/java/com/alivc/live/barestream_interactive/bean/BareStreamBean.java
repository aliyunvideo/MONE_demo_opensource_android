package com.alivc.live.barestream_interactive.bean;

import com.alivc.live.interactive_common.bean.InteractiveUserData;

public class BareStreamBean {

    private InteractiveUserData userData;
    private boolean isConnected;

    public InteractiveUserData getUserData() {
        return userData;
    }

    public void setUserData(InteractiveUserData userData) {
        this.userData = userData;
    }

    public String getUrl() {
        return userData != null ? userData.url : null;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
