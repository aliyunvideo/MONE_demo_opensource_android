package com.aliyun.svideo.editor.filter

import android.content.Context
import com.aliyun.svideo.editor.effect.R
import com.aliyun.ugsv.common.utils.FileCopyUtils
import com.aliyun.ugsv.common.utils.FileUtils
import com.aliyun.ugsv.common.utils.StringUtils
import com.aliyun.ugsv.common.utils.ThreadUtils
import java.io.File
import java.io.FilenameFilter

class VideoFilterResourceManager {
    companion object {
        var PATH:String ?= null
        val DIR = "video_filter"
        val EFFECT_DIR = "filter"

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

        open fun loadVideoFilters(context: Context): ArrayList<VideoFilterItemViewModel> {
            var arrayList = ArrayList<VideoFilterItemViewModel>()
            var destPath = getVideoEffectPath(context)
            val files: Array<File> = File(destPath+EFFECT_DIR).listFiles({ dir, name ->
                if (name != null && !name.startsWith(".")) {
                    true
                } else false
            })
            var empty = VideoFilterItemViewModel("")
            empty.title = context.getString(R.string.ugsv_editor_filter_origin)
            arrayList.add(empty)
            for (file in files) {
                arrayList.add(VideoFilterItemViewModel(file.absolutePath))
            }
            return arrayList
        }

        fun isResourceReady(): Boolean {
            return sResourceReady
        }
    }
}