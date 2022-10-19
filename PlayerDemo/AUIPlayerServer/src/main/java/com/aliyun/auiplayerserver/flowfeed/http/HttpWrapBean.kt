package com.aliyun.auiplayerserver.flowfeed.http

import com.google.gson.annotations.SerializedName
import github.leavesc.reactivehttp.bean.IHttpWrapBean

const val CODE_SERVER_SUCCESS = 200
const val CODE_SUCCESS = 20000
const val CODE_PARAM_ERROR = 40000
const val RAM_AUTH_ERROR = 40301
const val STS_AUTH_ERROR = 40302
const val UID_ERROR = 40303
const val SERVER_ERROR = 50000
const val VOD_NOT_EXIST_ERROR = 50001

//IHttpWrapBean
class HttpWrapBean<T>(
    @SerializedName("code") var code: Int = 0,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data", alternate = ["forecasts"]) var data: T
) : IHttpWrapBean<T> {

    companion object {

        fun <T> success(data: T): HttpWrapBean<T> {
            return HttpWrapBean(CODE_SERVER_SUCCESS, "success", data)
        }

        fun <T> failed(data: T): HttpWrapBean<T> {
            return HttpWrapBean(-200, "服务器停止维护了~~", data)
        }

    }

    override val httpCode: Int
        get() = code

    override val httpMsg: String
        get() = message ?: ""

    override val httpData: T
        get() = data

    override val httpIsSuccess: Boolean
        get() = code == CODE_SERVER_SUCCESS || message == "OK" || code == CODE_SUCCESS

    override fun toString(): String {
        return "HttpResBean(code=$code, message=$message, data=$data)"
    }

}