# **LiveBeauty**

## **一、模块介绍**

### **模块职责**

**LiveBeauty**模块，为**美颜前处理**模块，主要负责对Queen SDK的封装，一站式使用美颜处理。

## **二、前置条件**

**1、申请License**

参考文档：[获取美颜特效SDK License](https://help.aliyun.com/zh/live/user-guide/obtain-a-license-of-queen-sdk/)

**2、配置License**

参考项目README.md文档里面的**配置License**环节。

## **三、模块实现**

### **模块介绍**

LiveBeauty，负责美颜处理的基础模块，分为live_beauty和live_queenbeauty两个模块。

live_beauty模块，主要负责对于直播场景下美颜接口的抽象；

live_queenbeauty是基于live_beauty抽象接口，对Queen美颜SDK的封装与具体实现。

### **实现逻辑**

* 对外接口：BeautyInterface
* 核心实现：QueenBeautyImpl
* 创建实例：BeautyFactory

由于模块实现了插件化，因此beauty实例是通过反射进行实例化，即：

BeautyInterface类，负责抽象出一套统一的对外接口；

BeautyFactory类，通过反射创建实例；

QueenBeautyImpl类，为Queen SDK美颜实现；**（核心逻辑）**

**注意：如果QueenBeautyImpl的包名被修改，请注意同步在代码中修改包名，否则在实例化失败，导致美颜调用无效！**

```java
public class BeautyConstant {
    // 由于beauty模块是插件化，因此beauty实例是通过反射进行实例化，请注意修改美颜具体实现（impl）类名，以免出现美颜初始化失败导致美颜失效的问题
    public static final String BEAUTY_QUEEN_MANAGER_CLASS_NAME = "com.alivc.live.queenbeauty.QueenBeautyImpl";
}
```

### **接入流程**

* **引入模块依赖**

如果使用LiveBeauty功能，请注意引入live_queenbeauty模块：

```groovy
implementation project(':LiveBeauty:live_queenbeauty')
```

* **美颜处理逻辑**

```java
private BeautyInterface mBeautyManager;

mALivcLivePusher.setCustomFilter(new AlivcLivePushCustomFilter() {
    @Override
    public void customFilterCreate() {
        initBeautyManager();
    }

    @Override
    public int customFilterProcess(int inputTexture, int textureWidth, int textureHeight, long extra) {
        if (mBeautyManager == null) {
            return inputTexture;
        }

        return mBeautyManager.onTextureInput(inputTexture, textureWidth, textureHeight);
    }

    @Override
    public void customFilterDestroy() {
        destroyBeautyManager();
        Log.d(TAG, "customFilterDestroy---> thread_id: " + Thread.currentThread().getId());
    }
});

private void initBeautyManager() {
    if (mBeautyManager == null) {
        Log.d(TAG, "initBeautyManager start");
        // 从v6.2.0开始，基础模式下的美颜，和互动模式下的美颜，处理逻辑保持一致，即：QueenBeautyImpl；
        mBeautyManager = BeautyFactory.createBeauty(BeautySDKType.QUEEN, mContext);
        // initialize in texture thread.
        mBeautyManager.init();
        mBeautyManager.setBeautyEnable(isBeautyEnable);
        mBeautyManager.switchCameraId(mCameraId);
        Log.d(TAG, "initBeautyManager end");
    }
}

private void destroyBeautyManager() {
    if (mBeautyManager != null) {
        mBeautyManager.release();
        mBeautyManager = null;
    }
}
```

* **美颜UI面板逻辑**

  * **UI布局**

  ```xml
  <com.aliyunsdk.queen.menu.QueenBeautyMenu
      android:id="@+id/beauty_beauty_menuPanel"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      android:layout_alignParentBottom="true"
      android:layout_centerHorizontal="true" />
  ```

  * **UI声明**

  ```java
  QueenMenuPanel beautyMenuPanel = QueenBeautyMenu.getPanel(context);
  beautyMenuPanel.onHideMenu();
  beautyMenuPanel.onHideValidFeatures();
  beautyMenuPanel.onHideCopyright();
  
  QueenBeautyMenu beautyBeautyContainerView = findViewById(R.id.beauty_beauty_menuPanel);
  beautyBeautyContainerView.addView(beautyMenuPanel);
  ```

### **依赖关系**

```groovy
dependencies {
    api project(':LiveBeauty:live_beauty')

    // 美颜UI面板
    api "com.aliyun.maliang.android:queen_menu:6.7.0-official-pro-tiny"

    // 一体化SDK，包含基础美颜功能
    implementation "com.aliyun.aio:AliVCSDK_InteractiveLive:6.7.0"

    // 此处引用外部独立版本高级功能Queen
    implementation "com.aliyun.maliang.android:queen:6.7.0-official-pro"
}
```

**注明：**[Queen SDK](https://www.aliyun.com/activity/cdn/video/rtc_race)基础版和高级版区别，详见：[Android端集成美颜特效SDK](https://help.aliyun.com/zh/live/user-guide/integrate-queen-sdk-for-android)

* **queen_menu**

Queen SDK官网提供的UI库，美颜UI面板及美颜资源加载库

### **可扩展**

BeautyInterface为抽象化的美颜接口类，客户可以基于该接口类，对接其它美颜SDK，实现一套基于其它美颜SDK的实现（参考QueenBeautyImpl）。

在BeautyConstant里面定义实现类的包路径，在BeautySDKType里面定义美颜SDK类型，通过BeautyFactory指定美颜SDK类型，完成反射实例化。

## 四、重要更新

* v4.4.4~v6.1.0：基础直播下的美颜，处理逻辑参考BeautySDKType.QUEEN，即：QueenBeautyImpl；互动直播下的美颜，处理逻辑参考BeautySDKType.INTERACT_QUEEN，即：InteractQueenBeautyImpl；

* v6.2.0~v6.6.0：互动直播下的美颜，与基础直播下的美颜，完成统一，处理逻辑保持一致，即：QueenBeautyImpl；

* v6.7.0开始，一体化SDK只包含基础美颜功能，高级美颜功能需要单独集成美颜SDK，详见模块文档；

## 五、用户指引

### **文档**

[推流SDK](https://help.aliyun.com/zh/live/developer-reference/push-sdk)

[音视频终端SDK](https://help.aliyun.com/product/261167.html)

[美颜特效SDK](https://help.aliyun.com/document_detail/2392303.html)

[美颜特效SDK通用问题](https://help.aliyun.com/document_detail/2400372.html)

### **FAQ**

如果您在使用推流SDK有任何问题或建议，欢迎通过钉钉搜索群号32825314或44911608加入推流SDK开发者生态群。

您在美颜特效SDK使用过程中有任何问题或建议，请通过开发者支持群联系我们，钉钉搜索群号34197869加入。