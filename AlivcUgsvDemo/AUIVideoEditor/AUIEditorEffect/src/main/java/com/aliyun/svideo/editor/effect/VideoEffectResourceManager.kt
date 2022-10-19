package com.aliyun.svideo.editor.effect

import android.content.Context
import com.aliyun.ugsv.common.utils.FileCopyUtils
import com.aliyun.ugsv.common.utils.FileUtils
import com.aliyun.ugsv.common.utils.StringUtils
import com.aliyun.ugsv.common.utils.ThreadUtils
import java.io.File
import java.io.FilenameFilter

class VideoEffectResourceManager {
    companion object {
        var PATH:String ?= null
        val DIR = "video_eff"
        val EFFECT_DIR = "aliyun_svideo_animation_filter"
        val NINE_EFFECT_DIR = "aliyun_svideo_split_screen_filters"

        var sResourceReady = false

        fun getVideoEffectPath(context: Context): String {
            if (StringUtils.isEmpty(PATH)) {
                PATH = FileCopyUtils.getExtFileDir(context) + DIR + File.separator
            }
            return PATH!!
        }

        fun copyAnimationEffect(context: Context) {
            sResourceReady = false
            var destPath = getVideoEffectPath(context)
            ThreadUtils.runOnSubThread { ->
                FileCopyUtils.copyAssetsFile(context, DIR, FileCopyUtils.getExtFileDir(context), null)
                val files: Array<File> = File(destPath).listFiles(
                    FilenameFilter { dir, name ->
                        if (name != null && name.endsWith(".zip")) {
                            true
                        } else false
                    })
                for (file in files) {
                    val len = file.absolutePath.length
                    if (!FileUtils.isFileExists(file.absolutePath.substring(0, len - 4))) {
                        FileCopyUtils.unZipFolder(file.absolutePath, destPath)
                    }
                }
                sResourceReady = true
            }
        }

        open fun loadVideoEffects(context: Context): ArrayList<VideoEffectItemViewModel> {
            var arrayList = ArrayList<VideoEffectItemViewModel>()
            var destPath = getVideoEffectPath(context)
            val files: Array<File> = File(destPath+EFFECT_DIR).listFiles({ dir, name ->
                if (name != null && !name.startsWith(".")) {
                    true
                } else false
            })
            for (file in files) {
                arrayList.add(VideoEffectItemViewModel(file.absolutePath))
            }
            var nine_files: Array<File> = File(destPath + NINE_EFFECT_DIR).listFiles({ dir, name ->
                if (name != null && !name.startsWith(".")) {
                    true
                } else false
            })
            for (file in nine_files) {
                arrayList.add(VideoEffectItemViewModel(file.absolutePath))
            }
            return arrayList
        }

        fun isResourceReady(): Boolean {
            return sResourceReady
        }
    }
}