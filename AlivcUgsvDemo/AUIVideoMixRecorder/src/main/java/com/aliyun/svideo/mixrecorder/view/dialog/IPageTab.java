package com.aliyun.svideo.mixrecorder.view.dialog;

public interface IPageTab {


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
