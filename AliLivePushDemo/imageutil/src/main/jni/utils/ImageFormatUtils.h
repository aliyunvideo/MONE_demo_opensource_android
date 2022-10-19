//
// Created by lifujun on 2020/7/21.
//

#ifndef NOVA_IMAGEFORMATUTILS_H
#define NOVA_IMAGEFORMATUTILS_H

#include <jni.h>

class ImageFormatUtils {

public:
    static void init(JNIEnv *pEnv);

    static void unInit(JNIEnv *pEnv);

    static int registerMethod(JNIEnv *pEnv);

    static jbyteArray java_GetNV21(JNIEnv *env, jclass jclazz, jlong y, jlong u, jlong v, jint width , jint height,jint strideY, jint strideU, jint strideV);
};


#endif //NOVA_IMAGEFORMATUTILS_H
