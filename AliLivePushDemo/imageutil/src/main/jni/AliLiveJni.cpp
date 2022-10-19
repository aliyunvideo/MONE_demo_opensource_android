//
// Created by lifujun on 2020/7/21.
//
#include <jni.h>
#include "platformUtils/JniEnv.h"
#include "utils/ImageFormatUtils.h"

int initJavaInfo(JNIEnv *pEnv);

void unInitJavaInfo(JNIEnv *pEnv);

extern "C"
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JniEnv::init(vm);
    JniEnv Jenv;
    JNIEnv *mEnv = Jenv.getEnv();
#ifdef USE_ARES
    ares_library_init_jvm(vm);
#endif
    int result = initJavaInfo(mEnv);

    if (result == JNI_FALSE) {
        return JNI_FALSE;
    }

    return JNI_VERSION_1_4;
}

int initJavaInfo(JNIEnv *pEnv) {
    ImageFormatUtils::init(pEnv);

    if (ImageFormatUtils::registerMethod(pEnv) == JNI_FALSE) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}


extern "C"
JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    JniEnv Jenv;
    JNIEnv *env = Jenv.getEnv();
    unInitJavaInfo(env);
}

void unInitJavaInfo(JNIEnv *pEnv) {

    ImageFormatUtils::unInit(pEnv);
}
