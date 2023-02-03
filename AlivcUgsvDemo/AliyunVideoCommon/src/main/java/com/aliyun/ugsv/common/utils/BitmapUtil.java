/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.ugsv.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public
class BitmapUtil {

    private static final String TAG = "BitmapUtil";

    public static
    boolean writeBitmap(String path, Bitmap bitmap, int w, int h, Bitmap.CompressFormat format, int quality) {
        int originalWidth = bitmap.getWidth(), originalHeight = bitmap.getHeight();
        Matrix m = new Matrix();
        m.setScale((float) w / originalWidth, (float) h / originalHeight);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, originalWidth, originalHeight, m, true);
        boolean succ = writeBitmap(path, scaledBitmap, format, quality);
        scaledBitmap.recycle();
        return succ;
    }

    public static
    boolean writeBitmap(String path, Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "unable to open output file", e);
            return false;
        }

        boolean succ = bitmap.compress(format, quality, fout);

        try {
            fout.close();
        } catch (IOException e) {
            return false;
        }

        return succ;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                               : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    public static boolean generateFileFromBitmap(Bitmap bmp, String outputPath, String srcMimeType) throws IOException {
        if (outputPath == null || bmp == null) {
            return false;
        }
        File outputFile = new File(outputPath);
        if (!outputFile.exists()) {
            File dir = outputFile.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            outputFile.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        srcMimeType = TextUtils.isEmpty(srcMimeType) ? "jpeg" : srcMimeType;
        if (outputPath.endsWith("jpg") || outputPath.endsWith("jpeg")
                || srcMimeType.endsWith("jpg") || srcMimeType.endsWith("jpeg")) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } else if (outputPath.endsWith("png") || srcMimeType.endsWith("png")) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } else if (outputPath.endsWith("webp") || srcMimeType.endsWith("webp")) {
            bmp.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
        } else {
            Log.e("AliYunLog", "not supported image format for '" + outputPath + "'");
            outputStream.flush();
            outputStream.close();
            if (outputFile.exists()) {
                outputFile.delete();
            }
            return false;
        }
        outputStream.flush();
        outputStream.close();
        return true;
    }

    public static final Bitmap safeDecodeFile(String path, BitmapFactory.Options options) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Bitmap bmp = null;
        try {
            if (options != null) {
                bmp = BitmapFactory.decodeFile(path, options);
            } else {
                bmp = BitmapFactory.decodeFile(path);
            }
        } catch (Throwable e) {
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
            Log.e("AliYunLog", "load bmp failed!path[" + path + "]", e);
            return null;
        }
        return bmp;
    }

    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

}
