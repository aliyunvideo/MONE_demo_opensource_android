# amdemos-android-player

A demo project for Apsara Video Player SDK.

## **开发环境**

* **系统环境**

  * Android SDK

  * JDK 8


* **IDE**
  * Android Studio


## **项目配置**

当前播放器Demo项目已完成工程架构改造，支持一体化编译和单独编译；实现工程解构，高内聚低耦合，提升开发效率。

* **独立编译（推荐）**

```shell
# 拷贝播放器Demo代码
git clone git@gitlab.alibaba-inc.com:AlivcSolution_Android/AlivcPlayerSolution.git
cd AlivcPlayerSolution
# 切换到对应的一体化分支
git checkout release/aio/v6.4.0
# 拉取项目初始配置
./build_mtl.sh -i
# 切换submodule分支
...
```

* **一体化编译**

```shell
# 拷贝一体化Demo代码
git clone git@gitlab.alibaba-inc.com:AlivcSolution_Android/AlivcAIODemo.git
cd AlivcAIODemo
# 切换到对应的一体化分支
git checkout release/aio/v6.4.0
git submodule init && git submodule update --init --recursive
# 切换submodule分支
...
```

* **备注**

  无论是一体化工程，还是独立工程，都依赖了其它子代码仓库。**切换submodule分支**为必要步骤，各子仓库默认约定的分支如下表。

| 项目名称          | 默认分支（主分支） | 项目地址                                                     |
| ----------------- | ------------------ | ------------------------------------------------------------ |
| AUIFoundation     | develop            | https://code.alibaba-inc.com/AlivcSolution_Android/AUIFoundation |
| AndroidThirdParty | 一体化分支         | https://code.alibaba-inc.com/AlivcSolution_Android/AndroidThirdParty |
| AliLivePushDemo   | 一体化分支         | https://code.alibaba-inc.com/apsara-media-demo/amdemos-android-live |
| AlivcUgsvDemo     | 一体化分支         | https://code.alibaba-inc.com/apsara-media-demo/amdemos-android-ugsv |
| PlayerDemo        | 一体化分支         | https://code.alibaba-inc.com/AlivcSolution_Android/AlivcPlayerSolution |

*如果当前一体化版本在v6.5.0，对应一体化分支为：release/aio/v6.5.0。*

## **研发支撑**

* **MTL**

[AlivcPlayerSolution_Android](https://mtl4.alibaba-inc.com/#/collaborationSpace/detail/1054808/cr/1066739?step=BUILD)

