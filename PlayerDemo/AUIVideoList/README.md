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

更多详细资料，请参考 AUIVideoFunctionList 模块下的`README.md`。

### **3.标准模式**

标准模式使用 AliListPlayer 的方式实现短视频列表播放，结合内置预加载和本地缓存机制可以达到更好的体验。

## **三、编译运行**

1. 接入已授权播放器的音视频终端SDK License。

   具体操作请参见[Android端接入License](https://help.aliyun.com/zh/apsara-video-sdk/user-guide/access-to-license#58bdccc0537vx)。
                             
2. 将 AUIVideoList 目录下对应的模块（AUIVideoEpisode/AUIVideoFunctionList/AUIVideoStandradList）和 AUIVideoListCommon 模块，拷贝到您项目工程中。

   请注意修改两个模块 build.gradle 文件中的编译版本（与您项目工程中设置保持一致）以及播放器SDK版本。
    
   播放器SDK版本配置在 AUIVideoListCommon/build.gradle 中修改（参考 AndroidThirdParty/config.gradle 中的 externalPlayerFull ）。

3. 在项目 gradle 文件的 repositories 配置中，引入阿里云SDK的 Maven 源：

   ```groovy
   maven { url "https://maven.aliyun.com/repository/releases" }
   ```
   
4. 根据您要配置的模块，增加其引用方式和依赖方式。

   在项目的 setting.gradle 中按需增加:
   ```groovy
   // 项目根目录下有一个 AUIVideoList 文件夹，包含其中的 AUIVideoListCommon 模块。如果此模块直接放在根目录下，则应为':AUIVideoListCommon'，其他模块以此类推
   include ':AUIVideoList:AUIVideoListCommon'// 三者都需要这个
   include ':AUIVideoList:AUIVideoEpisode' 
   include ':AUIVideoList:AUIVideoFunctionList'
   include ':AUIVideoList:AUIVideoStandradList'
   ```
   
   在 app 模块的 build.gradle 中按需增加:
   ```groovy
   // 将 AUIVideoList 文件夹下的 AUIVideoEpisode 模块作为 app 模块的编译时依赖。同上，如果此模块被放置在根目录下，直接写':AUIVideoEpisode'即可
   implementation project(':AUIVideoList:AUIVideoEpisode')
   implementation project(':AUIVideoList:AUIVideoFunctionList')
   implementation project(':AUIVideoList:AUIVideoStandradList')
   ```

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
### **集成FAQ**
  
1. 错误“Namespace not specified”

   请检查您的 AGP 版本。如果为较新版本（如8.3.2），需要手动在各模块 build.gradle 中添加 namespace 设置。旧版本 AGP 此配置位于模块 /src/main/res/AndroidManifest.xml 中的 package 属性。
    
2. Gradle 在处理 repository 的优先级时出现冲突
   
     请优先在 setting.gradle 中添加 repository。

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
