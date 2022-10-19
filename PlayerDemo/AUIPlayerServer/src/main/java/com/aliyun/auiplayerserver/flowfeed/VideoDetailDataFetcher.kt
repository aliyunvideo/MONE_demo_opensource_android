package com.aliyun.auiplayerserver.flowfeed

import github.leavesc.reactivehttp.datasource.RemoteExtendDataSource
import github.leavesc.reactivehttp.viewmodel.IUIActionEvent

class VideoDetailDataFetcher(iActionEvent: IUIActionEvent?) :
    RemoteExtendDataSource<VideoDetailApiService>(iActionEvent, VideoDetailApiService::class.java) {

    override val baseUrl: String
        get() = "https://vpdemo-proxy.aliyuncs.com"

    override fun showToast(msg: String) {
    }
}