package com.aliyun.svideo.editor.caption.util

import android.content.Context
import android.graphics.Color
import android.net.Uri
import com.aliyun.svideo.editor.caption.model.CaptionResource
import com.aliyun.svideo.editor.caption.model.CaptionResourceType
import com.aliyun.svideo.editor.common.util.SdCardUtils
import com.aliyun.svideosdk.common.AliyunColor
import com.aliyun.svideosdk.common.struct.project.Source
import java.io.File

class CaptionResourceUtil {
    companion object {
        private const val RESOURCE_CAPTION_FONT = "ugsv_caption_font"
        private const val RESOURCE_CAPTION_FONT_EFFECT = "ugsv_caption_font_effect"
        private const val RESOURCE_CAPTION_FONT_STYLE_TEMPLATE = "ugsv_caption_font_style_template"
        private const val RESOURCE_CAPTION_BUBBLE = "ugsv_caption_bubble"
        private const val RESOURCE_CAPTION_BUBBLE_FONT = "ugsv_caption_bubble_font"


        fun copyResource(context : Context) {
            SdCardUtils.copySelf(context, RESOURCE_CAPTION_FONT)
            SdCardUtils.copySelf(context, RESOURCE_CAPTION_FONT_EFFECT)
            SdCardUtils.copySelf(context, RESOURCE_CAPTION_BUBBLE)
            SdCardUtils.copySelf(context, RESOURCE_CAPTION_BUBBLE_FONT)
        }

        /**
         * @return 字体文件
         */
        fun getCaptionFontList(): List<CaptionResource> {
            val list: MutableList<CaptionResource> = ArrayList()
            val file = File(
                SdCardUtils.SD_DIR,
                RESOURCE_CAPTION_FONT
            )
            if (file.exists() && file.isDirectory) {
                val files = file.listFiles()
                var index = 2
                for (fileTemp in files) {
                    if (fileTemp.exists()) {
                        val fileStructure = fileTemp.name.split('.');
                        val filenameArray = fileStructure[0].split('-')
                        if(filenameArray.size != 2) {
                            continue
                        }
                        val captionResource = CaptionResource(CaptionResourceType.Font, index++, Source(fileTemp.absolutePath), "${fileTemp.absolutePath}${File.separator}icon.png", filenameArray[1])
                        list.add(captionResource)
                    }
                }
            }
            return list
        }

        /**
         * @return 花字文件
         */
        fun getCaptionFlowerTemplateList(): List<String> {
            val list: MutableList<String> = ArrayList()
            val file: File = File(
                SdCardUtils.SD_DIR,
                RESOURCE_CAPTION_FONT_EFFECT
            )
            if (file.exists() && file.isDirectory) {
                val files = file.listFiles()
                for (fileTemp in files) {
                    if (fileTemp.exists()) {
                        list.add(fileTemp.absolutePath)
                    }
                }
            }
            return list
        }

        /**
         * @return 文字气泡文件
         */
        fun getCaptionBubbleFileList(): List<String> {
            val list: MutableList<String> = ArrayList()
            val file: File = File(
                SdCardUtils.SD_DIR,
                RESOURCE_CAPTION_BUBBLE
            )
            if (file.exists() && file.isDirectory) {
                val files = file.listFiles()
                for (fileTemp in files) {
                    if (fileTemp.exists()) {
                        list.add(fileTemp.absolutePath)
                    }
                }
            }
            return list
        }

        /**
         * @return 文字气泡字体文件
         */
        fun getCaptionBubbleFontFileList(): List<CaptionResource> {
            val list: MutableList<CaptionResource> = ArrayList()
            val file: File = File(
                SdCardUtils.SD_DIR,
                RESOURCE_CAPTION_BUBBLE_FONT
            )
            if (file.exists() && file.isDirectory) {
                val files = file.listFiles()
                for (fileTemp in files) {
                    if (fileTemp.exists()) {
                        val filenameArray = fileTemp.name.split('-')
                        if(filenameArray.size != 2) {
                            continue
                        }
                        val captionResource = CaptionResource(CaptionResourceType.Font, filenameArray[0].toInt(), Source(fileTemp.absolutePath), "${fileTemp.absolutePath}${File.separator}icon.png", filenameArray[1])
                        list.add(captionResource)
                    }
                }
            }
            return list
        }

        fun getCaptionColorList() : List<AliyunColor> {
            val list: MutableList<AliyunColor> = ArrayList()

            list.add(AliyunColor(Color.parseColor("#FFFFFF")))
            list.add(AliyunColor(Color.parseColor("#FE891E")))
            list.add(AliyunColor(Color.parseColor("#D74E47")))
            list.add(AliyunColor(Color.parseColor("#F7F3E2")))
            list.add(AliyunColor(Color.parseColor("#FFE45C")))
            list.add(AliyunColor(Color.parseColor("#FEF49D")))
            list.add(AliyunColor(Color.parseColor("#DCFACF")))
            list.add(AliyunColor(Color.parseColor("#89BF6A")))
            list.add(AliyunColor(Color.parseColor("#2DDB9D")))
            list.add(AliyunColor(Color.parseColor("#8AFDFE")))
            list.add(AliyunColor(Color.parseColor("#9DD623")))
            list.add(AliyunColor(Color.parseColor("#47A0D7")))
            list.add(AliyunColor(Color.parseColor("#316DDB")))
            list.add(AliyunColor(Color.parseColor("#925CFF")))
            list.add(AliyunColor(Color.parseColor("#FE9DE7")))

            return list
        }

        fun getCaptionFontStyleTemplateList(context : Context) : List<CaptionResource> {
            val list: MutableList<CaptionResource> = ArrayList()

            val textStyleList = context.assets.list(RESOURCE_CAPTION_FONT_STYLE_TEMPLATE)
            textStyleList?.forEach {
                val iconFile = Uri.parse("file:///android_asset/${RESOURCE_CAPTION_FONT_STYLE_TEMPLATE}${File.separator}${it}${File.separator}icon.png")

                val captionResource = CaptionResource(CaptionResourceType.FontStyleTemplate, it.hashCode(), Source("${RESOURCE_CAPTION_FONT_STYLE_TEMPLATE}${File.separator}${it}${File.separator}config.json"), iconFile?.toString() ?: "", it)
                list.add(captionResource)
            }

            return list
        }

    }
}