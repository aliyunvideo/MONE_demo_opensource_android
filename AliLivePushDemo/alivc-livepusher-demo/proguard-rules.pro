# This is a configuration file for ProGuard.
# http:#proguard.sourceforge.net/index.html#manual/usage.html

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
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

#保留注解参数
-keepattributes *Annotation*
#保留Google原生服务需要的类
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-keep class com.aliyun.clientinforeport.** {*;}
-keep class com.alivc.component.encoder.** { *;}
-keep class com.alivc.component.player.** { *; }
-keep class com.alivc.live.pusher.Alivc* { *;}
-keep class com.alivc.live.pusher.**  { *;}
-keep class com.alivc.live.utils.**  { *;}
-keep class com.alivc.live.pusher.LivePusherJNI { *;}
-keep class com.alivc.live.pusher.ILivePusher { *;}
-keep class com.alivc.component.capture.** { *;}
-keep class com.alivc.component.custom.** { *;}
-keep class com.alivc.live.pusher.WaterMarkInfo { *;}
-keep class com.alivc.live.pusher.LogUtil { *;}
-keep class com.alivc.live.pusher.SurfaceStatus { *; }
-keep class com.aliyun.rts.network.** { *; }
-keep class com.example.rtsnetsdkjar.** { *; }
-keep class com.uc.crashsdk.** { *; }
-keep class com.alivc.component.voice.** { *; }
-keep class com.aliyun.animoji.** { *; }
-keep class com.aliyun.player.** { *; }



-keep class android.** { *;}

# For native methods, see http:#proguard.sourceforge.net/manual/examples.html#native
#保留native方法的类名和方法名
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http:#proguard.sourceforge.net/manual/examples.html#beans
#保留自定义View,如"属性动画"中的set/get方法
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
#保留Activity中参数是View的方法，如XML中配置android:onClick=”buttonClick”属性，Activity中调用的buttonClick(View view)方法
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http:#proguard.sourceforge.net/manual/examples.html#enumerations
#保留混淆枚举中的values()和valueOf()方法
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#Parcelable实现类中的CREATOR字段是绝对不能改变的，包括大小写
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

#R文件中的所有记录资源id的静态字段
-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Dont warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
#忽略support包因为版本兼容产生的警告
-dontwarn android.support.**