package com.aliyun.svideo.recorder.utils;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLRotationHandler {

    private static final String VERTEX_SHADER_SOURCE =
            "attribute vec4 a_Position;\n" +
                    "attribute vec2 a_TexCoord;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "void main() {\n" +
                    "   gl_Position = a_Position;\n" +
                    "   v_TexCoord = a_TexCoord;\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER_SOURCE =
            "precision mediump float;\n" +
                    "uniform sampler2D u_TextureUnit;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "void main() {\n" +
                    "   // 对纹理坐标进行X轴旋转180度\n" +
                    "   vec2 rotatedTexCoord = vec2(v_TexCoord.x, 1.0 - v_TexCoord.y);\n" +
                    "   gl_FragColor = texture2D(u_TextureUnit, rotatedTexCoord);\n" +
                    "}\n";

    private int mProgram;
    private int mTextureUniformHandle;
    private int mPositionHandle;
    private int mTexCoordHandle;

    private FloatBuffer mVertexBuffer;

    private FloatBuffer mTexCoordBuffer;

    int[] textures = new int[2];

    int[] frameBuffers = new int[2];

    int nextTextureId = 0;

    private void init(int width, int height) {
        // 创建新的纹理ID
        if (mProgram == 0) {
            GLES20.glGenTextures(2, textures, 0);
            // 创建FBO
            GLES20.glGenFramebuffers(2, frameBuffers, 0);

            // 配置新纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);

            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

            // 使用旋转180度的着色器程序
            setupShaderProgram();

            mVertexBuffer = ByteBuffer.allocateDirect(new float[]{ -1f, -1f, -1f, 1f, 1f, -1f, 1f, 1f }.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mVertexBuffer.put(new float[]{ -1f, -1f, -1f, 1f, 1f, -1f, 1f, 1f }).position(0);

            mTexCoordBuffer = ByteBuffer.allocateDirect(new float[]{ 0f, 0f, 0f, 1f, 1f, 0f, 1f, 1f }.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTexCoordBuffer.put(new float[]{ 0f, 0f, 0f, 1f, 1f, 0f, 1f, 1f }).position(0);
        }
    }

    public int rotateTextureAroundXAxis(int originalTextureId, int width, int height) {

        init(width, height);

        int[] old_fbo = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, old_fbo, 0);

        int newTextureId = textures[nextTextureId];
        int fbo = frameBuffers[nextTextureId];

        nextTextureId++;
        nextTextureId = nextTextureId % 2;


        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);

        // 将新纹理附加到FBO
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, newTextureId, 0);

        GLES20.glViewport(0, 0, width, height);

        // Clear the framebuffer.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);

        boolean[] result = new boolean[1];
        GLES20.glGetBooleanv(GLES20.GL_BLEND, result, 0);
        GLES20.glDisable(GLES20.GL_BLEND);

        GLES20.glUseProgram(mProgram);

        // 设置顶点和纹理坐标缓冲区
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mTexCoordBuffer);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);

        // 绘制
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, originalTextureId);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // 禁用顶点数组
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);

        GLES20.glUseProgram(0);
        // 解绑FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, old_fbo[0]);

        // 返回新的纹理ID
        return newTextureId;
    }

    public void release() {
        // 删除与FBO关联的纹理
        if (textures[0] != 0) {
            GLES20.glDeleteTextures(2, textures, 0);
            textures[0] = 0;
        }

        // 删除FBO
        if (frameBuffers[0] != 0) {
            GLES20.glDeleteFramebuffers(2, frameBuffers, 0);
            frameBuffers[0] = 0;
        }

        if (mProgram != 0) {
            GLES20.glDeleteProgram(mProgram);
            mProgram = 0;
        }
    }

    private void setupShaderProgram() {
        if (mProgram == 0) {
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_SOURCE);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_SOURCE);

            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);

            mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit");
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
            mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoord");
        }
    }

    private static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


}