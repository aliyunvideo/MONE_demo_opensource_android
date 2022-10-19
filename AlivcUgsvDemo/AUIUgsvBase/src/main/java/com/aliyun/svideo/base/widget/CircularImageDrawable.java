/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.base.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

public
class CircularImageDrawable extends Drawable {

    public CircularImageDrawable() {
        _Paint.setAntiAlias(true);
    }

    private Bitmap _Bitmap;

    public
    void setBitmap(Bitmap bitmap) {
        _Bitmap = bitmap;

        if (_Bitmap == null) {
            _Paint.setShader(null);
        } else {
            createShader(_Bitmap);
        }

        invalidateSelf();
    }

    private final Paint _Paint = new Paint();


    @Override
    public
    void draw(Canvas canvas) {

        if (_Bitmap == null) {
            return;
        }

        Rect bounds = getBounds();
        float w = bounds.width();
        float h = bounds.height();

        canvas.drawCircle(w / 2, h / 2, Math.min(w / 2, h / 2), _Paint);
    }

    @Override
    public
    void setAlpha(int alpha) {

    }

    @Override
    public
    void setColorFilter(ColorFilter cf) {

    }

    @Override
    public
    int getOpacity() {
        return PixelFormat.OPAQUE ;
    }

    private final Matrix _Matrix = new Matrix();

    @Override
    protected
    void onBoundsChange(Rect bounds) {
        updateShaderMatrix(bounds);
    }

    @Override
    public
    int getIntrinsicHeight() {
        return _Bitmap == null ? -1 : _Bitmap.getHeight();
    }

    @Override
    public
    int getIntrinsicWidth() {
        return _Bitmap == null ? -1 : _Bitmap.getWidth();
    }

    private
    void createShader(Bitmap bitmap) {
        _Paint.setShader(new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP));
        updateShaderMatrix(getBounds());
    }

    private
    void updateShaderMatrix(Rect bounds) {
        BitmapShader shader = (BitmapShader) _Paint.getShader();
        if (shader == null) {
            return;
        }

        int w = bounds.width();
        int h = bounds.height();


        float sx = (float) w / _Bitmap.getWidth();
        float sy = (float) h / _Bitmap.getHeight();
        float s = Math.min(sx, sy);

        float tx = (float) w / 2 - _Bitmap.getWidth() * s / 2;
        float ty = (float) h / 2 - _Bitmap.getHeight() * s / 2;

        _Matrix.setScale(s, s);
        _Matrix.postTranslate(tx, ty);

        shader.setLocalMatrix(_Matrix);

        _Paint.setShader(shader);
    }

}
