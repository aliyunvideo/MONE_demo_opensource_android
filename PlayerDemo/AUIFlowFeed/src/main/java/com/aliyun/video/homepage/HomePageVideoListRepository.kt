package com.aliyun.video.homepage

import com.aliyun.auiplayerserver.bean.VideoInfo
import com.aliyun.auiplayerserver.bean.VideoStsInfo
import com.aliyun.auiplayerserver.flowfeed.HomePageFetcher
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig
import com.aliyun.player.alivcplayerexpand.listplay.ListPlayManager
import com.aliyun.player.source.StsInfo
import com.google.gson.Gson
import java.util.*

/**
 * 测试：http://vpdemo-proxy.aliyun.test
 * 预发：https://pre-vpdemo-proxy.aliyuncs.com
 * 正式：https://vpdemo-proxy.aliyuncs.com
 * 1.先请求 UserToken
 * 2.请求播放列表
 * 3.请求 sts
 */
open class HomePageVideoListRepository{
    var mStsInfo: StsInfo? = null
    private var mStsInit = false

    /**
     * 如果刷新需要传递上一次最后一个视频id，保证获取到的视频是没有播放过的
     */
    protected var mLastVideoId: Long? = null

    private val mFlowFeedServer by lazy {
        HomePageFetcher()
    }

    fun requestVideoList(isLoadMore: Boolean, callback: HomePageFetcher.VideoListDataBack) {
        if (!mStsInit) {
            requestStsAndVideoListData(callback)
        } else {
            mFlowFeedServer.initPlayerListDatas(mLastVideoId,isLoadMore, object: HomePageFetcher.VideoListDataBack{
                override fun onResult(list: MutableList<VideoInfo>) {
                    initVideoListRandomUUID(list)
                    callback.onResult(list)
                }

                override fun onError(msg: String?) {}
            })
        }
    }

    private fun requestStsAndVideoListData(callback: HomePageFetcher.VideoListDataBack){

        mFlowFeedServer.requestVideoStsInfo(object: HomePageFetcher.VideoStsInfoCallback{
            override fun onResult(result: String) {
                val gson = Gson()
                val videoStsInfo = gson.fromJson(result, VideoStsInfo::class.java)
                val stsInfo = StsInfo()
                stsInfo.accessKeyId = videoStsInfo.accessKeyId
                stsInfo.securityToken = videoStsInfo.securityToken
                stsInfo.accessKeySecret = videoStsInfo.accessKeySecret

                ListPlayManager.mStsInfo = stsInfo
                mStsInfo = stsInfo
                mStsInit = true
                saveGlobalPlayConfig()

                mFlowFeedServer.initPlayerListDatas(mLastVideoId,false,object: HomePageFetcher.VideoListDataBack{
                    override fun onResult(list: MutableList<VideoInfo>) {
                        initVideoListRandomUUID(list)
                        callback.onResult(list)
                    }

                    override fun onError(msg: String?) {}
                })
            }

            override fun onError(msg: String?) {}
        })
    }

    private fun initVideoListRandomUUID(list: MutableList<VideoInfo>){
        for (i in list.indices) {
            if (i == list.size - 1) {
                mLastVideoId = list[i].weight
            }
            list[i].randomUUID = UUID.randomUUID().toString()
        }
    }


    private fun saveGlobalPlayConfig() {
        GlobalPlayerConfig.mStsAccessKeyId = mStsInfo!!.accessKeyId
        GlobalPlayerConfig.mStsSecurityToken = mStsInfo!!.securityToken
        GlobalPlayerConfig.mStsAccessKeySecret = mStsInfo!!.accessKeySecret
    }
}