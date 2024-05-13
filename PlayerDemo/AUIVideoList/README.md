# **AUIVideoList**

## **一、模块介绍**

**AUIVideoList**模块，为**沉浸式短视频**模块，包含基于阿里云播放器SDK封装的UI组件，提供顺滑的短视频流体验业务场景，获得沉浸式播放体验。

## **二、模块组成**

| 模块名称             | 模块类别 | 实现方案              |
| -------------------- | -------- | --------------------- |
| AUIVideoEpisode      | 短剧模式 | 基于AliListPlayer     |
| AUIVideoFunctionList | 性能模式 | 基于AliPlayer，多实例 |
| AUIVideoStandradList | 标准模式 | 基于AliListPlayer     |

### **1.短剧模式**

短剧模式使用 AliListPlayer 的方式实现短视频列表播放，基于阿里云在微短剧场景的实践经验，沉淀了音视频终端SDK的本地缓存、智能预加载等最佳实践，提供低代码集成套件，帮助集成方快速搭建微短剧App并获得更好的视听体验。

更多详细资料，请参考 AUIVideoEpisode 模块下的`README.md`。

### **2.性能模式**

性能模式使用多个播放器实例（AliPlayer）+ 预加载（MediaLoader）+ 预渲染的方式实现短视频列表播放，结合本地缓存可以达到更好体验。相比标准模式，性能模式支持播放器所有功能，更为灵活，功能更为强大。

### **3.标准模式**

标准模式使用 AliListPlayer 的方式实现短视频列表播放，结合内置预加载和本地缓存机制可以达到更好的体验。

## **三、编译运行**

1. 接入已授权播放器的音视频终端SDK License。

   具体操作，请参见[Android端接入License](https://help.aliyun.com/zh/apsara-video-sdk/user-guide/access-to-license#58bdccc0537vx)。

2. 根据你所要集成的模式，将 AUIVideoList 目录下的对应的模块，和 AUIVideoListCommon 模块，拷贝到您项目工程中。

   **注意⚠️**：请修改模块 build.gradle 文件中的编译版本和 SDK 版本。编译版本以您项目工程的为准，SDK版本以 `AndroidThirdParty/config.gradle` 中的为准；请确认工程的 gradle repositories 配置中，已引入了阿里云 SDK 的 Maven 源：

   ```groovy
   maven { url "https://maven.aliyun.com/repository/releases" }
   ```
   
3. 修改您项目工程的引入方式。

   **注意⚠️**：请在当前项目的 build.gradle 和 settings.gradle 文件中，增加模块的引用方式和依赖方式，如下：

   ```groovy
   // settings.gradle
   include ':AUIVideoList:AUIVideoListCommon'
   include ':AUIVideoList:AUIVideoEpisode'
   include ':AUIVideoList:AUIVideoFunctionList'
   include ':AUIVideoList:AUIVideoStandradList'
   
   // build.gradle
   implementation project(':AUIVideoList:AUIVideoEpisode')
   implementation project(':AUIVideoList:AUIVideoFunctionList')
   implementation project(':AUIVideoList:AUIVideoStandradList')
   ```

4. 确认视频源地址。

   **注意⚠️**：如果视频源地址为模块提供的 MP4 私有加密地址，由于加密特性，集成到您项目工程中将会播放失效。请注意修改 AUIEpisodeConstants 文件下的 EPISODE_JSON_URL 的变量值，手动切换剧集地址。

5. 配置页面跳转，在当前页面中打开对应模块的主界面。

   ```java
   // 短剧模式
   Intent videoListEpisodeIntent = new Intent(this, AUIEpisodePlayerActivity.class);
   startActivity(videoListEpisodeIntent);
   
   // 性能模式
   Intent videoListFunctionIntent = new Intent(this, AUIVideoFunctionListActivity.class);
   startActivity(videoListFunctionIntent);
   
   // 标准模式
   Intent videoListStandardIntent = new Intent(this, AUIVideoStandardListActivity.class);
   startActivity(videoListStandardIntent);
   ```

## **四、用户指引**

### **文档**

* [沉浸式短视频](https://help.aliyun.com/zh/vod/developer-reference/short-video-list-player)

* [微短剧场景简介](https://help.aliyun.com/zh/apsara-video-sdk/use-cases/introduction-to-micro-drama-scenes)

* [播放器SDK](https://help.aliyun.com/zh/vod/developer-reference/apsaravideo-player-sdk/)

* [音视频终端SDK](https://help.aliyun.com/product/261167.html)

* [阿里云·视频点播](https://www.aliyun.com/product/vod)

* [视频点播控制台](https://vod.console.aliyun.com)

* [ApsaraVideo VOD](https://www.alibabacloud.com/zh/product/apsaravideo-for-vod)

### **FAQ**

如果您在使用播放器SDK有任何问题或建议，欢迎通过钉钉搜索群号31882553加入阿里云播放器SDK开发者群。