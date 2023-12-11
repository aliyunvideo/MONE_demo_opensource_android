# amdemos-android-live

A demo project for Apsara Video Live Push SDK.

## **Demo编译**

### **环境要求**

* Android SDK
* JDK 8

### **IDE**

* Android Studio

## **Demo体验**

![DEMO QRCODE](https://alivc-demo-cms.alicdn.com/versionProduct/resources/livePush/demo_qrcode.png)

## **快速集成**

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
* [SDK下载与历史记录](https://help.aliyun.com/zh/live/developer-reference/sdk-download-and-release-notes)
  
* [SDK download and release notes](https://www.alibabacloud.com/help/en/live/developer-reference/sdk-download-and-release-notes)

#### **2、在项目build.gradle中增加Maven源**

```groovy
maven { url 'https://maven.aliyun.com/nexus/content/repositories/releases' }
```

#### **3、在 defaultConfig 中，指定App使用的CPU架构（目前SDK支持armeabi-v7a 和 arm64-v8a）**

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
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.REORDER_TASKS" />
<uses-permission android:name="android.permission.VIBRATE" />
<!-- 添加录音权限 -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<!-- 添加相机权限 -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```

* **混淆配置**

```tex
-keep class org.webrtc.** { *; }
-keep class com.alivc.** { *; }
-keep class com.aliyun.** { *; }
-keep class com.cicada.** { *; }
```

#### **6、集成与编译问题指南**

* **SDK集成问题：集成多个SDK冲突**

如您同时集成[直播推流SDK](https://help.aliyun.com/zh/live/developer-reference/push-sdk-for-android/)和[播放器SDK](https://help.aliyun.com/zh/vod/developer-reference/apsaravideo-player-sdk-for-android/)，或其它阿里云音视频SDK；在组合编译时，部分SDK版本，可能会存在字节码重复（Duplicate class）冲突问题；请使用[音视频终端 SDK](https://help.aliyun.com/document_detail/2391316.html)避免冲突，集成方式请参考[标准集成·Android端](https://help.aliyun.com/document_detail/2412570.html)。

* **SDK集成方式**

如您希望以本地AAR形式集成，我们在官网文档[SDK下载与历史记录](https://help.aliyun.com/zh/live/developer-reference/sdk-download-and-release-notes)中，为您提供了SDK下载包。

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

## **链接指引**

### **文档**

* [阿里云·视频直播](https://www.aliyun.com/product/live)
* [推流SDK](https://help.aliyun.com/document_detail/97659.html)
* [接口文档](https://help.aliyun.com/zh/live/developer-reference/integrate-push-sdk-for-android#section-d8t-6hq-n0d)
* [互动直播](https://help.aliyun.com/zh/live/user-guide/interactive-live)
* [音视频终端SDK](https://help.aliyun.com/product/261167.html)

### **Global**

* [ApsaraVideo Live](https://www.alibabacloud.com/zh/product/apsaravideo-for-live)
* [Push SDK](https://www.alibabacloud.com/product/apsaravideo-for-live/streaming-sdk)
* [Push SDK Doc](https://www.alibabacloud.com/help/en/live/developer-reference/push-sdk/)
* [API Doc](https://www.alibabacloud.com/help/en/live/developer-reference/integrate-push-sdk-for-android#section-d8t-6hq-n0d)
* [Interactive streaming](https://www.alibabacloud.com/help/en/live/user-guide/interactive-live/)

### **控制台**

* [直播控制台](https://live.console.aliyun.com/)
* [直播连麦控制台](https://live.console.aliyun.com/connect_microphone/demo#/connect_microphone/demo)
* [License控制台](https://live.console.aliyun.com/connect_microphone/demo#/sdks/license)

## **帮助**

如果您在使用推流SDK有任何问题或建议，欢迎通过钉钉搜索群号32825314或44911608加入推流SDK开发者生态群。

[视频直播-帮助中心](https://help.aliyun.com/product/29949.html)

[ApsaraVideo Live Help](https://www.alibabacloud.com/help/en/apsaravideo-live)
