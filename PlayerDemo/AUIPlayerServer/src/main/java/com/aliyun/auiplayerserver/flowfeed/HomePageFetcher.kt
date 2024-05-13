package com.aliyun.auiplayerserver.flowfeed

import com.aliyun.auiplayerserver.bean.AliyunVideoInfoList
import com.aliyun.auiplayerserver.bean.VideoInfo
import com.aliyun.auiplayerserver.okhttp.AlivcOkHttpClient
import com.google.gson.Gson
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class HomePageFetcher{

    private val STS_SERVER_URL = "https://vpdemo-proxy.aliyuncs.com/api/getSts"
    private val GET_VIDEO_LIST_URL = "https://vpdemo-proxy.aliyuncs.com/api/vod/getVodRecommendVideoList"

    fun requestVideoStsInfo(callback: VideoStsInfoCallback) {
        AlivcOkHttpClient.getInstance().get2(STS_SERVER_URL, object :
            AlivcOkHttpClient.HttpCallBack {
            override fun onError(
                request: Request,
                e: IOException
            ) {
                callback.onError(e.message)
            }

            override fun onSuccess(
                request: Request,
                result: String
            ) {
                callback.onResult(result)
            }
        })
    }

    fun initPlayerListDatas(
        mLastVideoId: Long?,
        isLoadMore: Boolean,
        callback: HomePageFetcher.VideoListDataBack
    ) {

        val mParameterMap = HashMap<String,Any?>()
        if (isLoadMore) {
            mParameterMap["cursor"] = mLastVideoId
        }else{
            mParameterMap["cursor"] = null
        }
        mParameterMap["size"] = 10
        AlivcOkHttpClient.getInstance().get(GET_VIDEO_LIST_URL,mParameterMap,object: AlivcOkHttpClient.HttpCallBack{
            override fun onError(request: Request?, e: IOException?) {
                callback.onError(e?.message)
            }

            override fun onSuccess(request: Request?, result: String?) {
                result?.let {
                    val jsonObject = JSONObject(result)
                    val dataJson = jsonObject.getString("data")
                    val gson = Gson()
                    val videoInfoList =
                        gson.fromJson(dataJson, AliyunVideoInfoList::class.java)
                    val list: MutableList<VideoInfo> = mutableListOf()
                    if (videoInfoList.videoList.isNotEmpty()) {
                        list.addAll(videoInfoList.videoList)
                    }
                    callback.onResult(list)
                }
            }
        })
    }

    interface VideoListDataBack {
        fun onResult(
            list: MutableList<VideoInfo>
        )

        fun onError(msg: String?)
    }

    interface VideoStsInfoCallback {
        fun onResult(result: String)
        fun onError(msg: String?)
    }
}