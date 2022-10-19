package com.aliyun.auiplayerserver.flowfeed

import com.aliyun.auiplayerserver.bean.VideoDetailInfo
import com.aliyun.auiplayerserver.flowfeed.http.HttpWrapBean
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoDetailApiService {

    @GET("/api/vod/getVodDetail")
    suspend fun getVideoDetailInfo(@Query("vodId") vodId: Long): HttpWrapBean<VideoDetailInfo>
}