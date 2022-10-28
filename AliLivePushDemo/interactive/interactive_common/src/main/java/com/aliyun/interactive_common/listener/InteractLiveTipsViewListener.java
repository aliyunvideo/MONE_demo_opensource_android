package com.aliyun.interactive_common.listener;

public interface InteractLiveTipsViewListener {

    void onCancel();
    void onConfirm();
    void onInputConfirm(String content);
}
