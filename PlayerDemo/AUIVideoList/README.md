# **AUIVideoList**

## **一、模块介绍**

**AUIVideoList**模块，为**沉浸式短视频**模块，包含基于阿里云播放器SDK封装的UI组件，提供顺滑的短视频流体验业务场景，获得沉浸式播放体验。

## **二、模块组成**

| 模块名称             | 模块类别 | 实现方案              |
| -------------------- | -------- | --------------------- |
| AUIVideoEpisode      | 短剧模式 | 基于AliListPlayer     |
| AUIVideoStandradList | 标准模式 | 基于AliListPlayer     |
| AUIVideoFunctionList | 性能模式 | 基于AliPlayer，多实例 |

### **1.短剧模式（推荐）**

短剧模式使用 AliListPlayer 的方式实现短视频列表播放，基于阿里云在微短剧场景的实践经验，沉淀了音视频终端SDK的本地缓存、智能预加载等最佳实践，提供低代码集成套件，帮助集成方快速搭建微短剧App并获得更好的视听体验。

更多详细资料，请参考 AUIVideoEpisode 模块下的`README.md`。

### **2.标准模式**

标准模式使用 AliListPlayer 的方式实现短视频列表播放，结合内置预加载和本地缓存机制可以达到更好的体验。

### **3.性能模式**

性能模式使用多个播放器实例（AliPlayer）+ 预加载（MediaLoader）+ 预渲染的方式实现短视频列表播放，结合本地缓存可以达到更好体验。相比标准模式，性能模式支持播放器所有功能，更为灵活，功能更为强大。

## **三、用户指引**

### **文档**

[微短剧场景](https://help.aliyun.com/zh/apsara-video-sdk/developer-reference/micro-drama-scene/)

[沉浸式短视频](https://help.aliyun.com/zh/vod/developer-reference/short-video-list-player)

[播放器SDK](https://help.aliyun.com/zh/vod/developer-reference/apsaravideo-player-sdk/)

[阿里云·视频点播](https://www.aliyun.com/product/vod)

[视频点播控制台](https://vod.console.aliyun.com)

[ApsaraVideo VOD](https://www.alibabacloud.com/zh/product/apsaravideo-for-vod)

[音视频终端SDK](https://help.aliyun.com/product/261167.html)

### **FAQ**

如果您在使用播放器SDK有任何问题或建议，欢迎通过钉钉搜索群号31882553加入阿里云播放器SDK开发者群。