# 阿里云*视频云终端SDK DEMO

## 项目介绍
该项目由AIOApp视频云终端SDK + 三个子业务解决方案DEMO组成。三个项目业务解决方案DEMO分别为（短视频&&美颜特效、播放器、直播推流）组成。开发者可根据需要，加载整体工程，或者单独加载某一个业务解决方案。三个业务解决方案皆可以独立运行

## 运行环境
Android Studio

Gradle 7.5, 插件版本7.1.2

Java Android Studio自带 jdk11

Jdk11设置方法：Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JDK -> 选择 11（如果没有11，请升级你的Android Studio版本）

## 配置DEMO签名
> 出于安全风险考虑，阿里云*视频云终端SDK DEMO不对外开放测试License，需要客户自己配置签名。
1. 进入AIOApp - build.gradle 中，找到applicationId，修改为您的包名。如com.xxx.yyy。请勿使用原有包名。
2. 为DEMO创建签名文件，请自己[百度](https://www.baidu.com/s?wd=android%E7%AD%BE%E5%90%8D%E6%96%87%E4%BB%B6)
3. 进入AIOApp - build.gradle 中，找到signingConfigs， 配置您的签名信息

## 获取签名MD5 (v1.7.0后非必须)
> 出于安全考虑，阿里云License授权需要获取签名MD5。下面给出几种获取签名MD5的方法

方法一：

通过签名工具获取。[签名工具下载](https://docs-aliyun.cn-hangzhou.oss.aliyun-inc.com/assets/attach/57134/cn_zh/1500877517694/app_signatures%20%281%29.apk?spm=a2c4g.11186623.0.0.17a71a1aq0CnV6&file=app_signatures%20%281%29.apk)。
或者扫码安装：

![qrcode](./sign_tool_qrcode.jpg "二维码")

方法二：
1. 下载[SignatureUtils.java](https://github.com/aliyunvideo/MONE_demo_opensource_android/blob/master/AUIFoundation/AVUtils/src/main/java/com/aliyun/aio/utils/SignatureUtils.java)到本地
2. 调用SignatureUtils#getSingInfo(Context context)方法获取

## 申请License授权
1. 进入一体化SDK授权点播控制台。https://ice.console.aliyun.com/sdks/mine/list
2. 点击创建应用并绑定License，并根据应用的包名和签名(v1.7.0之后不需要签名，v1.7.0之前需要填写签名)，创建授权证书
3. 获取您的授权公钥和证书文件，下载到本地。打开AIOApp - src - main - AndroidManifest.xml
4. 找到 com.aliyun.alivc_license.licensekey，替换为您的公钥
5. 找到 com.aliyun.alivc_license.licensefile，替换为您的证书文件(可不配置，默认联网更新证书)

## 项目结构展示
```
project
    |-----AIOApp  一体化DEMO主入口
    |-----AliLivePushDEMO  直播推流解决方案DEMO （直播推流、录屏推流）
            |------ LiveApp 直播推流解决方案主入口
            |------ LiveBasic 基础直播
            |------ LiveInteractive 互动直播
            |------ LiveBeauty 直播美颜模块
            |------ LiveCommon 直播基础模块

    |-----AlivcUgsvDEMO    短视频生产解决方案DEMO （视频拍摄、视频裁剪、视频编辑）
            |------ AUIUgsvBase      短视频解决方案基础库
            |------ AUIVideoEditor   视频编辑能力模块
            |------ AUIVideoRecorder 视频拍摄能力模块
            |------ AUICrop          视频裁剪能力模块
            |------ UGSVAPP          短视频主入口

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

#AliVCSDK_Standard：标准一体化SDK。

#AliVCSDK_UGC：短视频场景SDK。

#AliVCSDK_BasicLive：基础直播场景SDK。

#AliVCSDK_InteractiveLive：互动直播场景SDK。

## SDK库及版本
| 版本          | 依赖                                            | 能力项                                    |
|-------------|-----------------------------------------------|----------------------------------------|
| 标准一体化SDK    | com.aliyun.aio:AliVCSDK_Standard:6.9.0        | 播放器 + 超低延迟直播 + 直播 + 短视频 + RTC连麦 + 基础美颜 |
| 短视频场景SDK    | com.aliyun.aio:AliVCSDK_UGC:6.9.0             | 播放器 + 短视频 + 基础美颜                             | 
| 基础直播SDK     | com.aliyun.aio:AliVCSDK_BasicLive:6.9.0       | 播放器  + 直播推流 + 基础美颜  + 超低延迟直播                        | 
| 互动直播SDK     | com.aliyun.aio:AliVCSDK_InteractiveLive:6.9.0 | 播放器 + 超低延迟直播 + 直播推流 + RTC连麦  + 基础美颜       | 