package com.aliyun.svideo.base;


/**
 * @author cross_ly
 * @date 2018/10/24
 * <p>描述:此类能够配置record、editor、crop完成之后的跳转
 */
public class AliyunSvideoActionConfig {

    private ActionInfo mAction;

    private AliyunSvideoActionConfig() {
        //设置默认参数
        mAction = new ActionInfo();
    }
    public static AliyunSvideoActionConfig getInstance(){
        return Instance.mActionConfig;
    }

    //可配置参数
    //1.目标activity name
    //2.返回是否销毁activity

    /**
     * 注册一个编辑合成后跳转的页面
     */
    public void registerEditFinishActivity(String className){
        mAction.setTagClassName(ActionInfo.SVideoAction.EDITOR_TARGET_CLASSNAME,className);
    }

    /**
     * 注册一个编辑合成后跳转的页面
     * @param className 合成之后跳转的actvity
     */
    public void registerRecordFinishActivity(String className){
        mAction.setTagClassName(ActionInfo.SVideoAction.RECORD_TARGET_CLASSNAME,className);
    }

    /**
     * 注册一个裁剪后跳转的页面
     */
    public void registerCropFinishActivity(String className){
        mAction.setTagClassName(ActionInfo.SVideoAction.CROP_TARGET_CLASSNAME,className);
    }
    /**
     * 注册一个发布界面的className，需要是个activity的全类名
     */
    public void registerPublishActivity(String className){
        mAction.setTagClassName(ActionInfo.SVideoAction.PUBLISH_TARGET_CLASSNAME,className);
    }
    /**
     * 获取action对象
     * @return ActionInfo
     */
    public ActionInfo getAction() {
        if (mAction == null){
            mAction = new ActionInfo();
        }
        return mAction;
    }

    /**
     * 静态内部类实现单例
     */
    private static class Instance{
        private static AliyunSvideoActionConfig mActionConfig = new AliyunSvideoActionConfig();
    }
}
