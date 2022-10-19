AlivcSolution---为了视频云解决方案而生,展现视频云端产品的所有能力，伴随一系列的视频云解决方案.

# 一、一些改变
    - 1.因为现在直播已经统一换成了Android studio 3.0+ ，所以需要将gradle plugin提高到3.0.1, gradle版本提高到4.1
    - 2.提高之后需要将所有的compile替换为api，未来将需要替换为implementation也要进行替换

# 二、如何打出不同版本的Demo包？

执行gradle -Psvt=xxx assembleRelease 可选值:svideo_base，svideo_standard，svideo_pro，publish，player，upload
执行完毕在根目录下会生成一个outDemo文件夹，即为各个产品的Demo
需要说明的是：目前打出来的包仅仅是和官网包同步，但是具体友好还有一段距离，需要一步步将代码和第三方库等做清理.

# 三、目录结构
    App-------示例Demo入口
        - AliyunPublish(推流模块)
        - Aliyunplayer(播放模块)
        - AliyunVodUpload(上传模块)
        - AliyunVideoSdk(短视频标准版和专业版SDK)
        - AliyunView(View组件模块)
        - AliyunResample(音频冲采样模块)
        - AliyunFileDownLoader:downloadermanager(文件下载模块)
        - AliyunRecorder:pro_record(录制模块)
        - AliyunImport:import_demo(导入模块)
        - AliyunCrop:pro_crop(裁剪模块)
        - AliyunEditor:editor_demo(编辑模块)
        - AliyunHelp(License声明模块)
        - AliyunSnap:snap_demo(基础版Demo模块)
        - AliyunSnap:snap_core(基础版SDK模块)
        - thirdparty-lib(以subModule的形式依赖的统一第三方依赖库)

# 四、脚本思路
App入口列表通过gradle脚本productFlavors来自定义不同的入口类，通过命令行接收自定义参数打包出不同版本包.根据业务不同打出不同的Demo代码

播放器:App + AliyunVodUpload + thirdparty-lib + setting.gradle + builid.gradle
直播推流:App + AliyunPublish + thirdparty-lib + setting.gradle + builid.gradle
上传:App + AliyunVodUpload + thirdparty-lib + setting.gradle + builid.gradle
短视频基础版:App+ AliyunSnap:snap_demo + thirdparty-lib + setting.gradle + builid.gradle
短视频标准版:App + AliyunRecorder:pro_record + AliyunEditor:editor_demo + AliyunCrop:pro_crop + AliyunImport:import_demo + AliyunFileDownLoader:downloadermanager + AliyunResample + AliyunView + AliyunVideoSdk + thirdparty-lib + AliyunHelp + setting.gradle + builid.gradle
短视频专业版:App + AliyunRecorder:pro_record + AliyunEditor:editor_demo + AliyunCrop:pro_crop + AliyunImport:import_demo + AliyunFileDownLoader:downloadermanager + AliyunResample + AliyunView + AliyunVideoSdk + thirdparty-lib + AliyunHelp + setting.gradle + builid.gradle

### 一些思路和做法
    - 基础版：
        - 1.替换SDK:AliyunSdk-RCE —> QuSdk-RC.aar(目前官网基础版都叫这个名字,考虑替换为AliyunSdk-RC ——>目前也没有在Demo的基础上打包基础版的SDK。。。
        - 2.Application删除loadLibrary代码段 'System.loadLibrary("FaceAREngine");' 'System.loadLibrary("AliFaceAREngine");’
        - 3.删除libAliFaceAREngine.so，libFaceAREngine.so,libaliresample.so
        - 4.拷贝snap模块，snap中包含SDK和snap的基础跳转代码
        - 5.因为有license这件事情所以需要将预制好的keystore签名文件拷贝到相应的Demo中
    - 标准版:
        - 1.CameraDemo删除 ———>魔法相机
            recorder.needFaceTrackInternal(true);
            String path = StorageUtils.getCacheDirectory(CameraDemo.this).getAbsolutePath() + File.separator + Common.QU_NAME + File.separator;'
             'recorder.setFaceTrackInternalModelPath(path + "/model");’
        - 2.删除libAliFaceAREngine.so，libFaceAREngine.so
        - 3.Application删除loadLibrary代码段
            'System.loadLibrary("FaceAREngine");'
            'System.loadLibrary("AliFaceAREngine");’
        - 4.因为有license这件事情所以需要将预制好的keystore签名文件拷贝到相应的Demo中
        — 5.待完成，applicationId目前官网的标准版和专业版一样，考虑区分。区分的主要原因是考虑license，标准版不应该有全量license功能体验
    - 上传SDK：
        - 上传Demo已经包含json库，编译时需要删除避免异常错误，脚本打包时需要重新引入