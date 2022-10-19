/*
 * Copyright (C) 2010-2021 Alibaba Group Holding Limited.
 *
 */
package com.aliyun.svideo.editor.common.util

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class AssetUtil {
    companion object {
        fun readAssertResource(context: Context, strAssertFileName: String): String {
            val assetManager = context.assets
            var strResponse = ""
            try {
                val ims = assetManager.open(strAssertFileName)
                strResponse = getStringFromInputStream(ims)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return strResponse
        }

        private fun getStringFromInputStream(a_is: InputStream): String {
            var br: BufferedReader? = null
            val sb = StringBuilder()
            var line: String?
            try {
                br = BufferedReader(InputStreamReader(a_is))
                while (br.readLine().also { line = it } != null) {
                    sb.append(line).append("\n")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (br != null) {
                    try {
                        br.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return sb.toString()
        }
    }

}