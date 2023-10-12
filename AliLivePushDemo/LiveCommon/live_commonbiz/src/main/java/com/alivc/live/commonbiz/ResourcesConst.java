package com.alivc.live.commonbiz;

import android.content.Context;

import java.io.File;

public class ResourcesConst {

    private ResourcesConst() {
    }

    /**
     * capture0.yuv 文件本地保存路径
     */
    public static File localCaptureYUVFilePath(Context context) {
        return new File(context.getApplicationContext().getExternalFilesDir(null), "capture0.yuv");
    }

    /**
     * 441.pcm 文件本地保存路径
     */
    public static File localCapturePCMFilePath(Context context) {
//        return new File(context.getExternalFilesDir(null).getPath() + File.separator + "441.pcm");
        return new File(context.getFilesDir().getPath() + File.separator + "alivc_resource/441.pcm");
    }

}
