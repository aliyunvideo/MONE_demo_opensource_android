/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.mixrecorder.view.effects.filter;

import com.aliyun.svideosdk.common.struct.project.Source;
import com.aliyun.svideosdk.editor.TimeEffectType;
import com.aliyun.svideosdk.common.struct.form.AspectForm;

import java.util.List;

/**
 * 特效javaBean
 * @author xlx
 */
public class EffectInfo {
    public UIEditorPage type;

    public TimeEffectType timeEffectType;

    public float timeParam;

    public boolean isMoment;

    public boolean isCategory;

    public boolean isAudioMixBar;

    public boolean isLocalMusic;

    public String fontPath;

    public int id;

    public int mixId;

    public List<AspectForm> list;

    public long startTime;

    public long endTime;

    public long streamStartTime;

    public long streamEndTime;

    Source mSource;

    public int musicWeight;

    /**
     * 获取资源文件路径
     * @deprecated 使用 {@link #getSource()}替代
     * @return 资源文件路径
     */
    /****
     * Gets the file of a resource.
     * @deprecated Replaced by {@link ##getSource()}.
     * @return path
     */
    @Deprecated
    public String getPath() {
        if (mSource != null) {
            return mSource.getPath();
        }
        return null;
    }

    /**
     * 设置资源文件路径
     * @param path
     * @deprecated 使用 {@link #setSource(Source)}替代
     */
    /****
     * Sets the file of a resource.
     * @param path
     * @deprecated Replaced by {@link #setSource(Source)}.
     */
    @Deprecated
    public void setPath(String path) {
        mSource = new Source(path);
    }

    /**
     * 获取资源
     *
     * @return 资源
     */
    /****
     * Gets the file of a resource.
     * @return Source
     */
    public Source getSource() {
        return mSource;
    }

    /**
     * 设置资源
     * @param source 资源
     */
    /****
     * Sets the file of a resource.
     * @param source Source
     */
    public void setSource(final Source source) {
        mSource = source;
    }
}
