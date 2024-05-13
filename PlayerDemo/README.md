# amdemos-android-player

A demo project for Apsara Video Player SDK.

## **Demo编译**

### **Environment**

* Gradle 7.5-bin，插件版本7.1.2

* **JDK 11**

> JDK 11设置方法：Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JDK -> 选择 11（如果没有11，请升级你的Android Studio版本）

### **IDE**

* Android Studio

### **编译方式**

当前播放器Demo项目已完成工程架构改造，支持一体化编译和单独编译；实现工程解构，提升开发效率。

- **独立编译（推荐）**

1. 您需要将`AlivcAIODemo`工程中的基础模块`AndroidThirdParty`拷贝到当前目录下
2. 以当前目录为目标文件夹，使用Android Studio打开并运行，实现仅播放器模块的独立编译。

- **一体化编译**

1. 当前工程已内嵌到`AlivcAIODemo`工程。
2. 您可以直接使用`AlivcAIODemo`作为目标文件夹，使用Android Studio打开并运行，实现多模块编译。

### **编译配置**

1. 如您使用**一体化编译**方式进行编译，底层SDK采用音视频终端SDK（AliVCSDK）
2. 如您使用**独立编译**方式进行编译，底层SDK可以配置使用播放器SDK（AliyunPlayer）或音视频终端SDK
   1. `gradle.properties`文件中`allInOne`编译配置，决定使用的SDK类型
   2. true，使用音视频终端SDK；false，使用播放器SDK

## **SDK集成**

* **版本更新记录**

[SDK下载](https://help.aliyun.com/zh/vod/developer-reference/sdk-download)

[SDK download](https://www.alibabacloud.com/help/en/vod/developer-reference/sdk-download)

* **快速集成**

[快速集成](https://help.aliyun.com/zh/vod/developer-reference/quick-integration-1)

[Quick integration](https://www.alibabacloud.com/help/en/vod/developer-reference/quick-integration-1)

**注意：如果同时集成直播推流SDK和播放器SDK，会存在冲突问题。可以使用[音视频终端 SDK](https://help.aliyun.com/document_detail/2391304.html)避免冲突**

## **Reference**

### **产品官网**

* [播放器SDK](https://help.aliyun.com/zh/vod/developer-reference/apsaravideo-player-sdk/)
* [ApsaraVideo Player SDK](https://www.alibabacloud.com/help/en/vod/developer-reference/apsaravideo-player-sdk/)
* [阿里云·视频点播](https://www.aliyun.com/product/vod)
* [ApsaraVideo VOD](https://www.alibabacloud.com/zh/product/apsaravideo-for-vod)

### **接口文档**

* [接口说明](https://help.aliyun.com/zh/vod/developer-reference/api-reference-android-player)
* [API reference](https://www.alibabacloud.com/help/en/vod/developer-reference/api-reference-android-player)

### **控制台**

* [视频点播控制台](https://vod.console.aliyun.com)
* [License控制台](https://live.console.aliyun.com/connect_microphone/demo#/sdks/license)

## **Help**

如果您在使用播放器SDK有任何问题或建议，欢迎通过钉钉搜索群号31882553加入阿里云播放器SDK开发者群。

[播放器常见问题](https://help.aliyun.com/zh/vod/support/faq-about-apsaravideo-player/)

[播放器单点追查](https://help.aliyun.com/zh/vod/user-guide/single-point-tracing)

