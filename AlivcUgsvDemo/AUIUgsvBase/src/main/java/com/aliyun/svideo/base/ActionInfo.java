package com.aliyun.svideo.base;

import android.app.Activity;
import android.util.SparseArray;

import java.util.LinkedList;

/**
 * @author cross_ly
 * @date 2018/10/24
 * <p>描述:实现录制、裁剪、编辑完成的跳转设置和获取
 */
public class ActionInfo {
    /**
     * map存储对应页面{@link SVideoAction}的跳转Activity packageName
     */
    private SparseArray<String> mTagClassNames = new SparseArray<>();
    /**
     * 存储需要销毁的Activity的packageName，在判断是否销毁的时候会将目标移出list
     */
    private LinkedList<String> mDestroyClassNames;

    /**
     * 设置目标跳转页面
     *
     * @param key {@link SVideoAction}
     * @param className 目标Activity的name，示例com.aliyun.demo.editor.EditorActivity
     */
    public void setTagClassName(SVideoAction key, String className) {
        mTagClassNames.put(key.index(), className);
    }

    /**
     * 获取目标跳转页面，如果没有设置则使用默认的跳转，如果value为null则直接finish
     *
     * @param key {@link SVideoAction}
     * @return className 目标Activity的name
     */
    public String getTagClassName(SVideoAction key) {
        String className = mTagClassNames.get(key.index());
        if (className == null) {
            className = getDefaultTargetConfig(key);
        }
        return className;
    }

    /**
     * 清空记录的tag页面 --使用默认的页面
     */
    public void clearTagClass() {
        mTagClassNames.clear();
    }

    /**
     * 设置finish页面
     *
     * @param className 目标Activity的name，示例com.aliyun.demo.editor.EditorActivity
     */
    public void setDestroyClassName(String className) {
        mDestroyClassNames.add(className);
    }

    /**
     * 判断目标页面是否finish
     *
     * @param className 目标Activity的name，示例com.aliyun.demo.editor.EditorActivity
     * @return {@code true} if this Activity need finish
     */
    public boolean isDestroyClassName(String className) {

        return mDestroyClassNames.remove(className);
    }

    /**
     * 判断目标页面是否finish
     *
     * @param activity 目标Activity的name
     * @return {@code true} if this Activity need finish
     */
    public boolean isDestroyActivity(Activity activity) {

        return isDestroyClassName(activity.getPackageName());
    }


    /**
     * 获取默认的配置
     *
     * @param key {@link SVideoAction}
     */
    public static String getDefaultTargetConfig(SVideoAction key) {
        String tagClassName;
        switch (key) {
            case CROP_TARGET_CLASSNAME:
                //裁剪完成默认返回
                tagClassName = null;
                break;
            case RECORD_TARGET_CLASSNAME:
                //录制完成默认进入编辑页面
                tagClassName = "com.aliyun.svideo.editorold.editor.EditorActivity";
                break;
            case EDITOR_TARGET_CLASSNAME:
                //编辑合成默认进入发布页面
                tagClassName = "com.aliyun.svideo.editorold.publish.UploadActivity";
                break;
            case PUBLISH_TARGET_CLASSNAME:
                //完成编辑后，默认进去合成页面
                tagClassName = "com.aliyun.svideo.editorold.publish.PublishActivity";
                break;
            default:
                tagClassName = null;
                break;
        }
        return tagClassName;
    }

    /**
     * 枚举包含录制、编辑、裁剪三个可配置
     */
    public enum SVideoAction {
        /**
         * 录制
         */
        RECORD_TARGET_CLASSNAME,
        /**
         * 编辑
         */
        EDITOR_TARGET_CLASSNAME,
        /**
         * 裁剪
         */
        CROP_TARGET_CLASSNAME,
        /**
         * 发布页面
         */
        PUBLISH_TARGET_CLASSNAME;
        public int index() {
            return ordinal();
        }
    }
}
