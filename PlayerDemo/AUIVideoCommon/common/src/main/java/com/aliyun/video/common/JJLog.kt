package com.aliyun.video.common

import android.util.Log

object JJLog {
    fun logi(tag: String, args: String) {
//        val content = StringBuilder()
//        for (arg in args) {
//            content.append(arg)
//        }
        Log.i(tag, args)
    }

    fun logi(vararg arg: String) {
    }

    fun d(tag: String, args: String) {
        Log.d(tag, args)
    }
}