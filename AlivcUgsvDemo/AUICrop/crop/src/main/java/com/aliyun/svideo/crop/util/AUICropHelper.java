package com.aliyun.svideo.crop.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.aliyun.svideo.crop.CropConfig;
import com.aliyun.svideo.crop.AliyunVideoCropActivity;

import java.util.List;

public class AUICropHelper {
    public static final int REQUEST_CODE_EDITOR_VIDEO_CROP = 1;
    public static final int REQUEST_CODE_CROP_VIDEO_CROP = 2;

    public static void startVideoCropForResult(Activity context, List<String> paths, int requestCode) {
        if(paths.isEmpty() || TextUtils.isEmpty(paths.get(0))){
            return;
        }
        CropConfig.Companion.getInstance().setInputPath("/sdcard/DCIM/45f16b1a-18acc5b36bf.mp4");
        Intent intent = new Intent(context, AliyunVideoCropActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    public static void startVideoCrop(Activity context, List<String> paths) {
       startVideoCropForResult(context, paths, -1);
    }
}
