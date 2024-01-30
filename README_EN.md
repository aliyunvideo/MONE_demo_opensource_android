# MediaBox SDK of Alibaba Cloud

## Project Introduction
This project consists of the AIOApp video cloud terminal SDK and three sub-business solution demos. The three project business solution demos consist of short video & & beauty effects, player, and live stream ingest. Developers can load the entire project or separately load a business solution as needed.All three business solutions can run independently.

## 运行环境
Android Studio

Gradle 7.5, 插件版本7.1.2

Java Android Studio with jdk11

Jdk11 Settings：Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JDK -> Choose 11

##  Configure the DEMO signature
>  for security risks, Alibaba Cloud * video cloud terminal SDK DEMO does not open the test License, and you need to configure your own signature.
1. Go to AIOApp-build. gradle, find the applicationId, and change it to your package name. For example, com.xxx.yyy. Do not use the original package name.
2. Create a signature file for the DEMO, please[Google](https://www.google.com.hk/search?q=android%20sign)
3. Go to AIOApp-build. gradle, find the signingConfigs, and configure your signature information.


## Apply for License authorization
1. Log on to the Apsaravideo in Alibaba Cloud console.
2. Click Create application and bind the License, and create an authorization certificate based on the package name of the application
3. Obtain your authorized public key and certificate file and download them to your local computer. Open AIOApp-src-main-AndroidManifest. xml
4. Find com.aliyun.alivc_license.licensekey and replace it with your public key.
5. Find com.aliyun.alivc_license.licensefile and replace it with your certificate File (no configuration is required, and the certificate is updated online by default).

## 项目结构展示
```
project
    | ----- AIOApp                          integrated DEMO main portal
    | ----- AliLivePushDEMO                 live stream ingest solution DEMO (live stream ingest, video stream ingest)
                | ------ LiveApp            the main portal of the live streaming solution
                | ------ LiveBasic          basic live broadcast
                | ------ LiveInteractive    interactive live broadcast
                | ------ LiveBeauty         live beauty module
                | ------ LiveCommon         Basic module of live broadcast
    
    | ----- AlivcUgsvDEMO                   short video production solution DEMO (video shooting, video cropping, and video editing)
                | ------ AUIUgsvBase        short video solution base Library
                | ------ AUIVideoEditor     video editing module
                | ------ AUIVideoRecorder   video shooting capability module
                | ------ AUICrop            video cropping module
                | ------ UGSVAPP            the main entry for short videos
    
    | ----- PlayerDEMO                      Player solution DEMO (information stream playback, full-screen playback, immersive playback)
                | ------ AUIFlowFeed        player information stream playback
                | ------ AUIFullScreen      player to play in full screen
                | ------ AUIVideoList       player list
                | ------ AUIPlayerApp       main player entry
    
    | ----- AndroidThirdParty               three-party library unified dependency definition module
    
    | ----- AUIFoundation                   AUI basic capabilities module
    | ------ AUIBaseUI                      UI base class and basic UI components
    | ------ AVTheme                        UI the theme base class, which provides switching between daytime mode and nighttime mode. Currently, only dark mode is supported.
    | ------ AVMatisse                      the album component, which provides album selection and is optimized according to the Matisse of the open source library.
    | ------ AVUtils                        Basic Tool Library
    
    | ----- SDKs                            the aar package of the current SDK
```
## Modify the running mode gradle.properties
allInOne: indicates whether to use an integrated package. true indicates that the integrated package is used. false indicates that the integrated package is used. Corresponding to the configuration in AndroidThirdParty/config.gradle

SDK_TYPE: the integrated package type. The default value is AliVCSDK_Premium, which corresponds to the AndroidThirdParty package type in externalAllInOne/config.gradle:

The specific values are as follows:

#SDK type

#AliVCSDK_Standard: Standard Integrated SDK.

#AliVCSDK_UGC: short video scenario SDK.

#AliVCSDK_BasicLive: SDK for basic live streaming scenarios.

#AliVCSDK_InteractiveLive: the SDK for interactive live streaming scenarios.

## SDK library and version
| Version | Dependency                                    | Capability item |
|-------------|-----------------------------------------------|----------------------------------------|
| Standard Integrated SDK | com.aliyun.aio:AliVCSDK_Standard:6.9.0        | Player + ultra-low latency live broadcast + live broadcast + short video + RTC-connected microphone + basic beauty |
| Short video scenario SDK | com.aliyun.aio:AliVCSDK_UGC:6.9.0             | Player + short video + basic beauty |
| Basic live streaming SDK | com.aliyun.aio:AliVCSDK_BasicLive:6.9.0       | Player + live streaming + basic beauty + ultra-low latency live streaming |
| Interactive live streaming SDK | com.aliyun.aio:AliVCSDK_InteractiveLive:6.9.0 | Player + ultra-low latency live streaming + live streaming + RTC link + basic beauty |