package com.aliyun.svideo.recorder.views.dialog;

public interface AUIIPageTab {
    /**
     *设置Tab名称
     * @return tab名称
     */
    void setTabTitle(String tabTitle);
    /**
     * 获取Tab名称
     *
     * @return tab名称
     */
    String getTabTitle();

    /**
     * 获取Tab图标
     *
     * @return tab icon, type: resource
     */
    int getTabIcon();
}
