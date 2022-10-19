package com.aliyun.svideo.editor.filter

import android.content.Context
import android.widget.ImageView
import com.aliyun.svideo.editor.common.bean.I18nBean
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.ugsv.common.utils.LanguageUtils
import com.aliyun.ugsv.common.utils.StringUtils
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.vidshop.base.image.GlideApp
import org.json.JSONException
import org.json.JSONObject
import java.io.*

class VideoFilterItemViewModel(source: String) : BaseViewModel(){
    var sourcePath = source
    var title: String ?= null

    override fun equals(other: Any?): Boolean {
        if (other is VideoFilterItemViewModel) {
            var itemViewModel:VideoFilterItemViewModel = other
            return sourcePath.equals(itemViewModel.sourcePath)
        }
        return return false
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun loadIcon(icon: ImageView, context: Context) {
        GlideApp.with(context).load(sourcePath + "/icon.png").into(icon)
    }

    /**
     * 获取滤镜名称 适配系统语言/中文或其他
     * @param path 滤镜文件目录
     * @return name
     */
    fun getFilterName(context: Context): String? {
        var path = sourcePath
        var name = ""
        val effectI18n: I18nBean? = getCurrentEffectI18n(path, "name")
        if (effectI18n == null) {
            path = if (LanguageUtils.isCHEN(context)) {
                "$path/config.json"
            } else {
                val pathEn = "$path/configEn.json"
                if (File(pathEn).exists()) {
                    pathEn
                } else {
                    "$path/config.json"
                }
            }
            val var2 = StringBuilder()
            val var3 = File(path)
            try {
                val var4 = FileReader(var3)
                var var7: Int
                while (var4.read().also { var7 = it } != -1) {
                    var2.append(var7.toChar())
                }
                var4.close()
            } catch (var6: IOException) {
                var6.printStackTrace()
            }
            try {
                val var4 = JSONObject(var2.toString())
                name = var4.optString("name")
            } catch (var5: JSONException) {
                var5.printStackTrace()
            }
            return name
        }
        return if (LanguageUtils.isCHEN(context)) {
            effectI18n.getZh_cn()
        } else {
            effectI18n.getEn()
        }
    }

    private fun getCurrentEffectI18n(path: String, key: String?): I18nBean? {
        val file = File(path)
        val dir = file.parentFile
        val i18nFile = File(dir, "i18n.json")
        if (!file.exists() || dir == null || !dir.exists() || !i18nFile.exists()) {
            return null
        }
        val parse: JsonElement
        parse = try {
            JsonParser().parse(
                InputStreamReader(FileInputStream(i18nFile))
            )
        } catch (e: FileNotFoundException) {
            return null
        }
        val jsonElement: JsonElement =
            parse.asJsonObject.getAsJsonObject("children").getAsJsonObject(file.name).getAsJsonObject(key)
        return Gson().fromJson<I18nBean>(
            jsonElement,
            I18nBean::class.java
        )
    }

    override fun hashCode(): Int {
        return sourcePath.hashCode()
    }
}