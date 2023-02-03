# 阿里云*视频云终端SDK DEMO

## 项目介绍
该项目由AIOApp视频云终端SDK + 三个子业务解决方案DEMO组成。三个项目业务解决方案DEMO分别为（短视频&&美颜特效、播放器、直播推流）组成。开发者可根据需要，加载整体工程，或者单独加载某一个业务解决方案。三个业务解决方案皆可以独立运行

## 运行环境
Android Studio

Gradle 6.5, 插件版本4.1.2

Java Android Studio自带 jdk11

Jdk11设置方法：Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JDK -> 选择 11（如果没有11，请升级你的Android Studio版本）

## 申请License授权
1. 进入一体化SDK授权点播控制台。https://ice.console.aliyun.com/sdks/mine/list
2. 点击创建应用并绑定License，并根据应用的包名和签名(v1.7.0之后不需要签名，v1.7.0之前需要填写签名)，创建授权证书
3. 获取您的授权公钥和证书文件，下载到本地。打开AIOApp - src - main - AndroidManifest.xml
4. 找到 com.aliyun.alivc_license.licensekey，替换为您的公钥
5. 找到 com.aliyun.alivc_license.licensefile，替换为您的证书文件

## 项目结构展示
```
project
    |-----AIOApp  一体化DEMO主入口
    |-----AliLivePushDEMO  直播推流解决方案DEMO （直播推流、录屏推流）
            |------ alivc-livepusher-demo 直播推流解决方案主入口
            |------ beauty 美颜特效相关能力封装

    |-----AlivcUgsvDEMO    短视频生产解决方案DEMO （视频拍摄、视频裁剪、视频编辑）
            |------ AUIUgsvBase      短视频解决方案基础库
            |------ AUIVideoEditor   视频编辑能力模块
            |------ AUIVideoRecorder 视频拍摄能力模块
            |------ AUICrop          视频裁剪能力模块
            |------ UGSVAPP          短视频主入口
            |------ templateSample   剪同款入口

    |-----PlayerDEMO       播放器解决方案DEMO （信息流播放、全屏播放、沉浸式播放）
            |------ AUIFlowFeed      播放器信息流播放
            |------ AUIFullScreen    播放器全屏播放
            |------ AUIVideoList     播放器列表播放
            |------ AUIPlayerApp     播放器主入口

    |-----AndroidThirdParty       三方库统一依赖定义模块

    |-----AUIFoundation           AUI基础能力模块
            |------ AUIBaseUI     UI基类，基础UI组件
            |------ AVTheme       UI主题基类，提供切换日间模式/夜间模式切换，目前只支持黑暗模式
            |------ AVMatisse     相册组件，提供相册选择，根据开源库Matisse优化
            |------ AVUtils       基础工具库

    |-----SDKs                     当前SDK的aar包
```
## 修改运行模式 gradle.properties
allInOne: 是否使用一体化包，true为使用一体化包，false为独立包。对应在AndroidThirdParty/config.gradle中的配置

SDK_TYPE: 一体化包类型，默认为AliVCSDK_Premium，对应AndroidThirdParty/config.gradle中的externalAllInOne包类型：

具体值如下

#SDK类型

#AliVCSDK_Premium：全功能SDK。 

#AliVCSDK_Standard：标准一体化SDK。

#AliVCSDK_UGC：短视频场景SDK。

#AliVCSDK_UGCPro：短视频场景SDK增强版。

#AliVCSDK_BasicLive：基础直播SDK。

#AliVCSDK_InteractiveLive：互动直播SDK。 

#AliVCSDK_StandardLive：基础直播SDK增强版。 

#AliVCSDK_PremiumLive：互动直播SDK增强版。


## SDK库及版本
| 版本          | 依赖                                            | 能力项                                            |
|-------------|-----------------------------------------------|------------------------------------------------|
| 标准一体化SDK    | com.aliyun.aio:AliVCSDK_Standard:1.7.0        | 播放器 + 超低延迟直播 + 直播 + 短视频 + RTC连麦                |
| 全功能SDK      | com.aliyun.aio:AliVCSDK_Premium:1.7.0         | 播放器 + 超低延迟直播 + 直播 + 短视频 + RTC连麦 + 高级美颜 + 后处理模块 | 
| 短视频场景SDK    | com.aliyun.aio:AliVCSDK_UGC:1.7.0             | 播放器 + 短视频                                      | 
| 短视频场景SDK增强版 | com.aliyun.aio:AliVCSDK_UGCPro:1.7.0          | 单播放器 + 短视频 + 美颜特效                              | 
| 基础直播SDK     | com.aliyun.aio:AliVCSDK_BasicLive:1.7.0       | 播放器  + 直播推流                                    | 
| 互动直播SDK     | com.aliyun.aio:AliVCSDK_InteractiveLive:1.7.0 | 播放器 + 超低延迟直播 + 直播推流 + RTC连麦                    | 
| 基础直播SDK增强版  | com.aliyun.aio:AliVCSDK_StandardLive:1.7.0    | 播放器 + 直播推流 + 美颜                                | 
| 互动直播SDK增强版  | com.aliyun.aio:AliVCSDK_PremiumLive:1.7.0     | 播放器 + 超低延迟直播 + 直播推流 + RTC连麦 + 美颜               |