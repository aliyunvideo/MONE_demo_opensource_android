# amdemos-android-live

A demo project for Apsara Video Live Push SDK.

## **一、Demo体验**

> **官网文档请参考**：[推流SDK · Demo体验](https://help.aliyun.com/zh/live/developer-reference/sdk-download-and-release-notes?#section-cra-t8n-o2q)

![DEMO QRCODE](https://alivc-demo-cms.alicdn.com/versionProduct/resources/livePush/demo_qrcode.png)

## **二、Demo编译**

> **官网文档请参考**：[Android推流SDK · Demo编译](https://help.aliyun.com/zh/live/developer-reference/push-sdk-for-android-demo-compilation)

### **1、IDE**

* Android Studio

> [Download Android Studio & App Tools - Android Developers](https://developer.android.com/studio?hl=zh-cn)

### **2、环境要求**

* Gradle 7.5-bin，插件版本7.1.2

* **JDK 11**

> JDK 11设置方法：Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JDK -> 选择 11（如果没有11，请升级你的Android Studio版本）

### **3、项目说明**

待补充。

## **三、SDK集成**

>  **官网文档请参考**：[Android推流SDK · SDK集成](https://help.aliyun.com/zh/live/developer-reference/integrate-push-sdk-for-android)

### **Android推荐使用maven集成：**

#### **1、在工程build.gradle配置脚本中的dependencies中添加如下代码**

- **基础版**

```groovy
implementation "com.alivc.pusher:AlivcLivePusher:${version}"
```

- **互动版**

```groovy
implementation "com.alivc.pusher:AlivcLivePusher_Interactive:${version}"
```

* **版本更新记录**
  * [SDK下载与发布记录](https://help.aliyun.com/zh/live/developer-reference/sdk-download-and-release-notes)

  * [SDK download and release notes](https://www.alibabacloud.com/help/en/live/developer-reference/sdk-download-and-release-notes)


#### **2、在项目build.gradle中增加Maven源**

```groovy
maven { url 'https://maven.aliyun.com/nexus/content/repositories/releases' }
```

#### **3、在 defaultConfig 中，指定App使用的CPU架构（目前SDK支持armeabi-v7a 和 arm64-v8a，不支持模拟器调试）**

```groovy
defaultConfig {
  ndk {
    abiFilters "armeabi-v7a", "arm64-v8a"
  }
}
```

#### **4、配置License**

推流SDK升级到4.4.2及以后版本，接入一体化License服务，您需要配置License文件。具体操作，请参见[推流SDK License集成指南](https://help.aliyun.com/zh/live/developer-reference/integrate-a-push-sdk-license)。

#### **5、其它工程配置**

* **权限配置**

```xml
<!--  Used for network features  -->
<uses-permission android:name="android.permission.INTERNET" />
<!--  To check the network connection state of the device, you'll need to add the ACCESS_NETWORK_STATE permission.  -->
<!--  This permission does not require user consent at runtime, but needs to be declared in the app's AndroidManifest.xml.  -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!--  If the app needs to record audio, you need to declare the RECORD_AUDIO permission and request this permission at runtime  -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />

<!--  The application needs to access the device's camera  -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- Request legacy Bluetooth permissions on older devices. -->
<uses-permission
  android:name="android.permission.BLUETOOTH"
  android:maxSdkVersion="30" />
<uses-permission
  android:name="android.permission.BLUETOOTH_ADMIN"
  android:maxSdkVersion="30" />
<!-- Needed only if your app communicates with already-paired Bluetooth devices. -->
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />

<!-- Only used by Demo for accessing storage files  -->
<!--  Storage permissions  -->
<uses-permission
  android:name="android.permission.READ_EXTERNAL_STORAGE"
  android:maxSdkVersion="32" />
<!-- Access image files -->
<uses-permission
  android:name="android.permission.READ_MEDIA_IMAGES"
  android:minSdkVersion="33" />
<!-- Write audio files -->
<uses-permission
  android:name="android.permission.READ_MEDIA_AUDIO"
  android:minSdkVersion="33" />
<!-- Write video files -->
<uses-permission
  android:name="android.permission.READ_MEDIA_VIDEO"
  android:minSdkVersion="33" />

<!-- Only used by Demo for screen recording and streaming  -->
<!-- Used for creating background tasks, not required by SDK -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<!-- Requesting permission to create system overlay windows -->
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

<!-- Only used by Demo for muting  -->
<!-- If this permission is not declared, the feature of automatically muting the stream during a phone call will not work -->
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

* **混淆配置**

```tex
-keep class org.webrtc.** { *; }
-keep class com.alivc.** { *; }
-keep class com.aliyun.** { *; }
-keep class com.cicada.** { *; }
```

#### **6、集成与编译问题指南**

> **官网文档请参考**：[Android推流SDK · 错误异常及特殊场景处理](https://help.aliyun.com/zh/live/developer-reference/handling-of-exceptions-and-special-scenarios-for-android)

* **SDK集成问题：集成多个SDK冲突**

如您同时集成[直播推流SDK](https://help.aliyun.com/zh/live/developer-reference/push-sdk-for-android/)和[播放器SDK](https://help.aliyun.com/zh/vod/developer-reference/apsaravideo-player-sdk-for-android/)，或其它阿里云音视频SDK；在组合编译时，部分SDK版本，可能会存在字节码重复（Duplicate class）冲突问题。请使用[音视频终端 SDK](https://help.aliyun.com/document_detail/2391316.html)避免冲突，集成方式请参考[标准集成·Android端](https://help.aliyun.com/document_detail/2412570.html)。

* **SDK集成方式**

如您希望以本地AAR形式集成，我们在官网文档[SDK下载与发布记录](https://help.aliyun.com/zh/live/developer-reference/sdk-download-and-release-notes)中，为您提供了SDK下载包。

如您希望以音视频终端SDK形式集成，请参考[音视频终端 SDK](https://help.aliyun.com/document_detail/2391316.html)，集成方式请参考[标准集成·Android端](https://help.aliyun.com/document_detail/2412570.html)。

* **Demo编译方式：基础版 与 互动版 切换**

将`gradle.properties`文件中`SDK_TYPE`的值，对应修改为AliVCSDK_BasicLive（基础版），或AliVCSDK_InteractiveLive（互动版）

* **Demo编译方式：直播推流SDK 与 音视频终端SDK 切换**

  * 修改Demo配置

    将`gradle.properties`文件中`allInOne`的值，改为true；

  * 修改SDK依赖

    将`AndroidThirdParty/config.gradle`文件中`externalAllInOne`对应SDK的名称，修改为AliVCSDK_BasicLive（基础版），或AliVCSDK_InteractiveLive（互动版）

* **Demo编译问题：Gradle Distribution无法访问**

由于近期Gradle将gradle distributions的后端存储服务，切换到了GitHub上，而GitHub在国内的访问存在不稳定因素，可能会导致访问失败，因此出现Demo编译失败的问题。解决gradle distribution的问题，即可完成Demo项目的跑通。

## **四、链接指引**

### **文档**

* [阿里云·视频直播](https://www.aliyun.com/product/live)
* [推流SDK](https://help.aliyun.com/zh/live/developer-reference/push-sdk)
* [推流SDK · API接口文档](https://help.aliyun.com/zh/live/developer-reference/integrate-push-sdk-for-android#section-d8t-6hq-n0d)
* [互动直播](https://help.aliyun.com/zh/live/user-guide/interactive-live)
* [音视频终端SDK](https://help.aliyun.com/product/261167.html)

### **Global**

* [ApsaraVideo Live](https://www.alibabacloud.com/zh/product/apsaravideo-for-live)
* [Push SDK](https://www.alibabacloud.com/product/apsaravideo-for-live/streaming-sdk)
* [Push SDK Doc](https://www.alibabacloud.com/help/en/live/developer-reference/push-sdk/)
* [Push SDK API Doc](https://www.alibabacloud.com/help/en/live/developer-reference/integrate-push-sdk-for-android#f0c462b07a1rh)
* [Interactive streaming](https://www.alibabacloud.com/help/en/live/user-guide/interactive-live/)

### **控制台**

* [直播控制台](https://live.console.aliyun.com/)
* [直播连麦控制台](https://live.console.aliyun.com/connect_microphone/demo#/connect_microphone/demo)
* [License控制台](https://live.console.aliyun.com/connect_microphone/demo#/sdks/license)

### **其它**

* **历史下载包追溯**

我们在官网文档[SDK下载与发布记录](https://help.aliyun.com/zh/live/developer-reference/sdk-download-and-release-notes)中，提供了最新的下载包。如：SDK（如：[v6.7.0](https://alivc-demo-cms.alicdn.com/versionProduct/installPackage/livePush/Interactive/v6.7.0/cn/SDK/AlivcLivePusher_Interactive_6.7.0_Android.zip)）、Demo源码（如：[v6.7.0](https://alivc-demo-cms.alicdn.com/versionProduct/installPackage/livePush/Interactive/v6.7.0/cn/Demo/AlivcLivePusherDemo_Interactive_6.7.0_Android.zip)）、API文档（如：[v6.7.0](https://alivc-demo-cms.alicdn.com/versionProduct/doc/live_pusher_interactive/6.7.0/Android/cn/annotated.html)）等。如需追溯历史版本的下载包，您可以自行改动下载地址里面的版本号，进行下载。

## **五、帮助**

如果您在使用推流SDK有任何问题或建议，欢迎通过钉钉搜索群号32825314或44911608加入推流SDK开发者生态群。

[视频直播-帮助中心](https://help.aliyun.com/product/29949.html)

[ApsaraVideo Live Help](https://www.alibabacloud.com/help/en/apsaravideo-live)
