package com.aliyun.ugsv.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author cross_ly DATE 2019/12/20
 * <p>描述:
 */
public class BitmapUtils {
    private static String TAG = "BitmapUtils";

    /**
     * 扶正带角度的图片
     *
     * @param imgPath 原始图片路径
     * @return true 修正图片角度成功 or false 图片无角度或者不存在
     */
    public static boolean checkAndAmendImgOrientation(String imgPath) {
        if (TextUtils.isEmpty(imgPath)) {
            return false;
        }
        File file = new File(imgPath);
        if (!file.exists()) {
            return false;
        }

        int degree = getBitmapDegree(imgPath);
        if (degree == 0) {
            return false;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        Bitmap rotateBitmap = rotateBitmap(bitmap, degree);
        saveBitmap(rotateBitmap, imgPath);
        return true;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                              ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            default:
                degree = 0;
                break;
            }
        } catch (IOException e) {
            Log.e(TAG, "getBitmapDegree failure msg : " + e.getMessage());
        }
        return degree;
    }

    /**
     * 旋转图片，使图片保持正确的方向。
     *
     * @param bitmap  原始图片
     * @param degrees 原始图片的角度
     * @return Bitmap 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                                         matrix, true);
        bitmap.recycle();
        return bmp;
    }

    /**
     * 保存bitmap到本地
     *
     * @param bitmap Bitmap
     */
    public static void saveBitmap(Bitmap bitmap, String path) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
            savePath = path;
        } else {
            Log.e(TAG, "saveBitmap failure : sdcard not mounted");
            return;
        }
        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
            } else {
                filePic.delete();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "saveBitmap: " + e.getMessage());
            bitmap.recycle();
            return;
        }
        bitmap.recycle();
        Log.i(TAG, "saveBitmap success: " + filePic.getAbsolutePath());
    }

}
