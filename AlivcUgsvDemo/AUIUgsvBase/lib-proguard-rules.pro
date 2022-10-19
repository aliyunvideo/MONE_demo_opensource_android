# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Users\Administrator\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-ignorewarnings
-dontwarn okio.**
-dontwarn com.google.common.cache.**
-dontwarn java.nio.file.**
-dontwarn sun.misc.**
-keep class android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
-keep class okhttp3.** { *; }
-keep class com.bumptech.glide.integration.okhttp3.** { *; }
-keep class com.liulishuo.filedownloader.** { *; }
-keep class java.nio.file.** { *; }
-keep class sun.misc.** { *; }

-keep class com.qu.preview.** { *; }
-keep class com.qu.mp4saver.** { *; }
-keep class com.duanqu.transcode.** { *; }
-keep class com.duanqu.qupai.render.** { *; }
-keep class com.duanqu.qupai.player.** { *; }
-keep class com.duanqu.qupai.audio.** { *; }
-keep class com.aliyun.qupai.encoder.** { *; }
-keep class com.sensetime.stmobile.** { *; }
-keep class com.duanqu.qupai.yunos.** { *; }
-keep class com.aliyun.common.** { *; }
-keep class com.aliyun.jasonparse.** { *; }
-keep class com.aliyun.struct.** { *; }
-keep class com.aliyun.recorder.AliyunRecorderCreator { *; }
-keep class com.aliyun.recorder.supply.** { *; }
-keep class com.aliyun.querrorcode.** { *; }
-keep class com.qu.preview.callback.** { *; }
-keep class com.aliyun.qupaiokhttp.** { *; }
-keep class com.aliyun.crop.AliyunCropCreator { *; }
-keep class com.aliyun.crop.struct.CropParam { *; }
-keep class com.aliyun.crop.supply.** { *; }
-keep class com.aliyun.qupai.editor.pplayer.AnimPlayerView { *; }
-keep class com.aliyun.qupai.editor.impl.AliyunEditorFactory { *; }
-keep interface com.aliyun.qupai.editor.** { *; }
-keep interface com.aliyun.qupai.import_core.AliyunIImport { *; }
-keep class com.aliyun.qupai.import_core.AliyunImportCreator { *; }
-keep class com.aliyun.qupai.encoder.** { *; }
-keep class com.aliyun.leaktracer.** { *;}
-keep class com.duanqu.qupai.adaptive.** { *; }
-keep class com.aliyun.thumbnail.** { *;}
-keep class com.aliyun.svideo.media.MediaCache { *;}
-keep class com.aliyun.svideo.media.MediaDir { *;}
-keep class com.aliyun.svideo.media.MediaInfo { *;}
-keep class com.alivc.component.encoder.**{ *;}
-keep class com.alivc.component.decoder.**{ *;}
-keep class com.aliyun.log.core.AliyunLogCommon { *;}
-keep class com.aliyun.log.core.AliyunLogger { *;}
-keep class com.aliyun.log.core.AliyunLogParam { *;}
-keep class com.aliyun.log.core.LogService { *;}
-keep class com.aliyun.log.struct.** { *;}
-keep class com.aliyun.svideo.editor.publish.SecurityTokenInfo { *; }

-keep class com.aliyun.vod.common.** { *; }
-keep class com.aliyun.vod.jasonparse.** { *; }
-keep class com.aliyun.vod.qupaiokhttp.** { *; }
-keep class com.aliyun.vod.log.core.AliyunLogCommon { *;}
-keep class com.aliyun.vod.log.core.AliyunLogger { *;}
-keep class com.aliyun.vod.log.core.AliyunLogParam { *;}
-keep class com.aliyun.vod.log.core.LogService { *;}
-keep class com.aliyun.vod.log.struct.** { *;}
-keep class com.aliyun.auth.core.**{*;}
-keep class com.aliyun.auth.common.AliyunVodHttpCommon{*;}
-keep class com.alibaba.sdk.android.vod.upload.exception.**{*;}
-keep class com.alibaba.sdk.android.vod.upload.auth.**{*;}
-keep class com.aliyun.auth.model.**{*;}
-keep class component.alivc.com.facearengine.** {*;}
-keep class com.aliyun.editor.NativeEditor{*;}
-keep class com.aliyun.nativerender.BitmapGenerator{*;}
-keep class com.aliyun.editor.EditorCallBack{*;}
-keep enum com.aliyun.editor.TimeEffectType{*;}
-keep enum com.aliyun.editor.EffectType{*;}
-keep class **.R$* { *; }
-keep class com.aliyun.svideo.sdk.internal.common.project.**{*;}
-keep class com.aliyun.svideo.sdk.external.struct.**{*;}
-keep class com.aliyun.sys.AlivcSdkCore{*;}
## Event Bus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}