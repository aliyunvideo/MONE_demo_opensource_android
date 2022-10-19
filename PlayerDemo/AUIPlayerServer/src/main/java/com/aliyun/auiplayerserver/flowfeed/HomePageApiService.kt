package com.aliyun.auiplayerserver.flowfeed

import com.aliyun.auiplayerserver.bean.AliyunVideoInfoList
import com.aliyun.auiplayerserver.flowfeed.http.HttpWrapBean
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 首页接口 api
 */
interface HomePageApiService {

    @GET("/api/vod/getVodRecommendVideoList")
    suspend fun getRecommendVideoList(
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int
    ): HttpWrapBean<AliyunVideoInfoList>

}