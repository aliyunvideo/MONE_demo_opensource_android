# 阿里云*短视频SDK DEMO

## 运行环境
Android Studio

Gradle 6.5, 插件版本4.1.2

Java Android Studio自带 jdk11

Jdk11设置方法：Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle -> Gradle JDK -> 选择 11（如果没有11，请升级你的Android Studio版本）

## 配置DEMO签名
> 出于安全风险考虑，阿里云*短视频SDK DEMO不对外开放测试License，需要客户自己配置签名。
1. 进入UGSVApp - build.gradle 中，找到applicationId，修改为您的包名。如com.xxx.yyy。请勿使用原有包名。
2. 为DEMO创建签名文件，请自己[百度](https://www.baidu.com/s?wd=android%E7%AD%BE%E5%90%8D%E6%96%87%E4%BB%B6)
3. 进入UGSVApp - build.gradle 中，找到signingConfigs， 配置您的签名信息

## 获取签名MD5 (v3.36.0后非必须)
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
2. 点击创建应用并绑定License，并根据应用的包名和签名(v3.36.0之后不需要签名，v3.36.0之前需要填写签名)，创建授权证书
3. 获取您的授权公钥和证书文件，下载到本地。打开UGSVApp - src - main - AndroidManifest.xml
4. 找到 com.aliyun.alivc_license.licensekey，替换为您的公钥
5. 找到 com.aliyun.alivc_license.licensefile，替换为您的证书文件(可不配置，默认联网更新证书)