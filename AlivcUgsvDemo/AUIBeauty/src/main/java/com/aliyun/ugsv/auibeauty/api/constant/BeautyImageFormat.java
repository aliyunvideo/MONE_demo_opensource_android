package com.aliyun.ugsv.auibeauty.api.constant;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.aliyun.ugsv.auibeauty.api.constant.BeautyImageFormat.kNV21;
import static com.aliyun.ugsv.auibeauty.api.constant.BeautyImageFormat.kRGBA;


@Retention(RetentionPolicy.SOURCE)
@IntDef({BeautyImageFormat.kDefault, BeautyImageFormat.kRGB, kNV21, kRGBA})
public @interface BeautyImageFormat {
    int kDefault = -1;
    int kRGB = 0;
    int kNV21 = 1;
    int kRGBA = 2;
}
