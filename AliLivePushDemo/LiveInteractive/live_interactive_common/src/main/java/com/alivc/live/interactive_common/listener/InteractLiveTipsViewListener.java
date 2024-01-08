package com.alivc.live.interactive_common.listener;

import com.alivc.live.interactive_common.bean.InteractiveUserData;

public abstract class InteractLiveTipsViewListener {
    public void onCancel() {}
    public void onConfirm() {}
    public void onInputConfirm(InteractiveUserData userData) {}
    public void onQrClick() {}
}
