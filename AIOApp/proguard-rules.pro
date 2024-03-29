# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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
# 指定代码的压缩级别
-optimizationpasses 5
#混淆时不使用大小写混合类名
-dontusemixedcaseclassnames
#不跳过library中的非public的类
-dontskipnonpubliclibraryclasses
#打印混淆的详细信息
-verbose
#忽略警告
-ignorewarnings
# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
#不进行优化，建议使用此选项，理由见上
-dontoptimize
#不进行预校验，预校验是作用在Java平台上的，Android平台上不需要这项功能，去掉之后还可以加快混淆速度
-dontpreverify
-keep class com.aliyun.alivcsolution.MutiApplication
-keep class com.aliyun.player.demo.PlayerApplication
-keep class com.alivc.live.pusher.demo.LiveApplication
-keep class com.umeng.** {*;}

-keep class org.repackage.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider

-keep class com.alivc.live.** {
   *;
}

-keep class com.alivc.component.** {
   *;
}

-keep class org.webrtc.** {
   *;
}

-keep class com.huawei.multimedia.alivc.** {
    *;
}

-keep class com.alivc.rtc.** {
    *;
}

-keep class com.alivc.component.** {
    *;
}


-keep class com.cicada.player.utils.NativeUsed { *; }
-keep,allowobfuscation interface com.cicada.player.utils.NativeUsed

-keep @com.cicada.player.utils.NativeUsed class *
-keepclassmembers class * {
    @com.cicada.player.utils.NativeUsed *;
}


-keep class com.aliyun.player.nativeclass.** { *;}
#-keep class com.aliyun.player.externalplayer.** { *;}
-keep class com.aliyun.player.videoview.AliDisplayView { *;}
-keep class com.aliyun.player.videoview.AliDisplayView$* { *;}
-keep class com.aliyun.player.source.** { *;}
-keep class com.aliyun.player.bean.** { *;}
-keep class com.aliyun.player.* { *;}

-keep class com.aliyun.downloader.** { *;}
-keep class com.aliyun.thumbnail.** { *;}
-keep class com.aliyun.liveshift.** { *;}
-keep class com.aliyun.loader.** { *;}
-keep class com.aliyun.subtitle.** { *;}
-keep class com.aliyun.private_service.** { *;}

-keep class com.aliyun.utils.DeviceInfoUtils { *;}
-keep class com.cicada.player.utils.** { *;}

######################短视频混淆配置#########################
-keep class com.aliyun.svideosdk.** {
    *;
}
-keep class com.aliyun.common.network.** {
    *;
}

-keep class com.aliyun.common.log.struct.AliyunLogInfo {*;}


-keep class **.CalledByNative { *; }
-keep,allowobfuscation interface **.CalledByNative

-keep @**.CalledByNative class *
-keepclassmembers class * {
    @**.CalledByNative *;
}

-keep class com.aliyun.rts.network.* { *; }
-keep class com.alibaba.dingpaas.** { *; }
-keep class com.dingtalk.mars.** { *; }
-keep class com.dingtalk.bifrost.** { *; }
-keep class com.dingtalk.mobile.** { *; }
-keep class org.android.spdy.** { *; }
-keep class com.alibaba.dingpaas.interaction.** { *; }