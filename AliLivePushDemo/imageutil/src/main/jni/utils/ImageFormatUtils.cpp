//
// Created by lifujun on 2020/7/21.
//

#include <libyuv.h>
#include "ImageFormatUtils.h"


static char *ImageFormatUtilsPath = (char *) ("com/alilive/imageutil/ImageFormatUtils");

static JNINativeMethod imageformat_method_table[] = {
        {"nativeGetNV21", "(JJJIIIII)[B", (void *) ImageFormatUtils::java_GetNV21},
};

jclass gj_ImageFormatUtils_Class = nullptr;

void ImageFormatUtils::init(JNIEnv *pEnv) {
    if (gj_ImageFormatUtils_Class == nullptr) {
        jclass pJclass = pEnv->FindClass(ImageFormatUtilsPath);
        gj_ImageFormatUtils_Class = (jclass) pEnv->NewGlobalRef(
                pJclass);
        pEnv->DeleteLocalRef(pJclass);
    }
}

void ImageFormatUtils::unInit(JNIEnv *pEnv) {
    if (gj_ImageFormatUtils_Class != nullptr) {
        pEnv->DeleteGlobalRef(gj_ImageFormatUtils_Class);
        gj_ImageFormatUtils_Class = nullptr;
    }
}

int ImageFormatUtils::registerMethod(JNIEnv *pEnv) {
    if (gj_ImageFormatUtils_Class == nullptr) {
        return JNI_FALSE;
    }
    if (pEnv->RegisterNatives(gj_ImageFormatUtils_Class, imageformat_method_table,
                              sizeof(imageformat_method_table) / sizeof(JNINativeMethod)) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

jbyteArray
ImageFormatUtils::java_GetNV21(JNIEnv *env, jclass jclazz, jlong y, jlong u, jlong v, jint width,
                               jint height,
                               jint strideY, jint strideU, jint strideV) {


    int dataLen = strideY * height * 3 / 2;
    uint8* nv21_data = static_cast<uint8 *>(malloc(dataLen));


    uint8 *nv21_y_data = nv21_data;
    uint8 *nv21_uv_data = nv21_data + strideY * height;

    libyuv::I420ToNV21(
            (const uint8 *) y, strideY,
            (const uint8 *) u, strideU,
            (const uint8 *) v, strideV,
            (uint8 *) nv21_y_data, strideY,
            (uint8 *) nv21_uv_data, strideY,
            width, height);

    jbyteArray bufArray = env->NewByteArray(dataLen);
    env->SetByteArrayRegion(bufArray, 0, dataLen, (jbyte *) (nv21_data));

    free(nv21_data);

    return bufArray;
}
