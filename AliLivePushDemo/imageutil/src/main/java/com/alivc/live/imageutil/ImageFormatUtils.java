package com.alivc.live.imageutil;

public class ImageFormatUtils {

    static {
        try {
            System.loadLibrary("imageutil");
        } catch (Throwable throwable) {
        }
    }

    public static byte[] getNV21(long dataFrameY, long dataFrameU, long dataFrameV, int width, int height, int strideY, int strideU, int strideV) {
        return nativeGetNV21(dataFrameY, dataFrameU, dataFrameV, width, height, strideY, strideU, strideV);
    }

    private static native byte[] nativeGetNV21(long dataFrameY, long dataFrameU, long dataFrameV, int width, int height, int strideY, int strideU, int strideV);
}
