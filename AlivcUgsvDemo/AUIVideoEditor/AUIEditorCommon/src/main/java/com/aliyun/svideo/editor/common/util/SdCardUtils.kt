package com.aliyun.svideo.editor.common.util

import android.content.Context
import android.text.TextUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

interface CopyCallback {
    fun onFileCopy(filePath: String?)
}

class SdCardUtils {

    companion object {
        var SD_DIR: String = ""

        private fun getExtFileDir(cxt: Context): String {
            return cxt.getExternalFilesDir("").toString() + File.separator
        }

        fun copySelf(cxt: Context, root: String) {
            copySelf(cxt, root, null)
        }

        fun copySelf(cxt: Context, root: String, copyCallback: CopyCallback?) {
            try {
                if (TextUtils.isEmpty(SD_DIR)) {
                    SD_DIR = getExtFileDir(cxt)
                }
                val files = cxt.assets.list(root)
                if (files!!.isNotEmpty()) {
                    val subdir = File(SD_DIR + root)
                    if (!subdir.exists()) {
                        subdir.mkdirs()
                    }
                    for (fileName in files) {
                        val fileTemp = File(SD_DIR + root + File.separator + fileName)
                        if (!fileTemp.isDirectory && fileTemp.exists()) {
                            continue
                        }
                        copySelf(cxt, root + File.separator + fileName, copyCallback)
                    }
                } else {

                    if (File(SD_DIR + root).exists()) {
                        return
                    }
                    val myOutput: OutputStream = FileOutputStream(SD_DIR + root)
                    val myInput = cxt.assets.open(root)
                    val buffer = ByteArray(1024 * 8)
                    var length = myInput.read(buffer)
                    while (length > 0) {
                        myOutput.write(buffer, 0, length)
                        length = myInput.read(buffer)
                    }
                    myOutput.flush()
                    myInput.close()
                    myOutput.close()
                    copyCallback?.onFileCopy(SD_DIR + root)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        
    }
}