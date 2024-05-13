package com.aliyun.svideo.recorder.utils;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GLHelper {

    public static void dumpBitmap(int textureId, int textureWidth, int textureHeight) {
                        // 创建一个FBO
            int[] frameBuffer = new int[1];
            GLES20.glGenFramebuffers(1, frameBuffer, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);

            // 将纹理绑定到FBO
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId, 0);

            // 检查FBO绑定是否成功
            if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                // 处理绑定失败的情况
            }

            // 设置视口大小和位置
            GLES20.glViewport(0, 0, textureWidth, textureHeight);

            // 创建一个缓冲区来存储像素数据
            ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(textureWidth * textureHeight * 4);
            pixelBuffer.order(ByteOrder.nativeOrder());

            // 从FBO中读取像素数据
            GLES20.glReadPixels(0, 0, textureWidth, textureHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);

            // 创建Bitmap
            Bitmap bitmap = Bitmap.createBitmap(textureWidth, textureHeight, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(pixelBuffer);
            // 重置FBO和纹理绑定
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GLES20.glDeleteFramebuffers(1, frameBuffer, 0);
    }
}
