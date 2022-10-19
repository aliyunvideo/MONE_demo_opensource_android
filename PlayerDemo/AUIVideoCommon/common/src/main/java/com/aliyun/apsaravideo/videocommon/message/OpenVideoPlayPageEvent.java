package com.aliyun.apsaravideo.videocommon.message;

import android.os.Bundle;

import androidx.annotation.Keep;

@Keep
public class OpenVideoPlayPageEvent {
    public int tabIndex;
    public Bundle bundle;

    public OpenVideoPlayPageEvent(int tabIndex, Bundle bundle) {
        this.tabIndex = tabIndex;
        this.bundle = bundle;
    }
}
