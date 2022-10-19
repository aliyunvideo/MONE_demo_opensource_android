package com.alivc.live.beauty.constant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

import static com.alivc.live.beauty.constant.BeautyImageFormat.kDefault;
import static com.alivc.live.beauty.constant.BeautyImageFormat.kNV21;
import static com.alivc.live.beauty.constant.BeautyImageFormat.kRGB;
import static com.alivc.live.beauty.constant.BeautyImageFormat.kRGBA;

@Retention(RetentionPolicy.SOURCE)
@IntDef({kDefault, kRGB, kNV21, kRGBA})
public @interface BeautyImageFormat {
    int kDefault = -1;
    int kRGB = 0;
    int kNV21 = 1;
    int kRGBA = 2;
}
