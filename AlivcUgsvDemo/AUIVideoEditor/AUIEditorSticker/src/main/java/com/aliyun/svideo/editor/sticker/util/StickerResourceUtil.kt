package com.aliyun.svideo.editor.sticker.util

import android.content.Context
import com.aliyun.svideo.editor.common.util.SdCardUtils
import com.aliyun.svideo.editor.sticker.model.StickerResource
import com.aliyun.svideosdk.common.struct.project.Source
import java.io.File

class StickerResourceUtil {
    companion object {
        private const val RESOURCE_STICKER = "ugsv_sticker"


        fun copyResource(context : Context) {
            SdCardUtils.copySelf(context, RESOURCE_STICKER)
        }


        /**
         * @return 文字气泡字体文件
         */
        fun getStickerFileList(): MutableList<StickerResource> {
            val list: MutableList<StickerResource> = ArrayList()
            val file: File = File(
                SdCardUtils.SD_DIR,
                RESOURCE_STICKER
            )
            if (file.exists() && file.isDirectory) {
                val files = file.listFiles()
                for (fileTemp in files) {
                    if (fileTemp.exists()) {
                        val filenameArray = fileTemp.name.split('-')
                        if(filenameArray.size != 2) {
                            continue
                        }
                        val captionResource = StickerResource(filenameArray[0].toInt(), Source(fileTemp.absolutePath), "${fileTemp.absolutePath}${File.separator}icon.png", filenameArray[1])
                        list.add(captionResource)
                    }
                }
            }
            return list
        }


    }
}