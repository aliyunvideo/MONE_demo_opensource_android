/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.music.music;

public class EffectBody<T> {

    private T mData;
    private boolean isLocal = false;
    private boolean isLoading = false;
    private boolean loadingError = false;
    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public EffectBody(T data, boolean isLocal) {
        mData = data;
        this.isLocal = isLocal;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isLoadingError() {
        return loadingError;
    }

    public void setLoadingError(boolean loadingError) {
        this.loadingError = loadingError;
    }

    @Override
    public int hashCode() {
        int result = mData != null ? mData.hashCode() : 0;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EffectBody<?> that = (EffectBody<?>) o;
        if (mData != null) {
            return mData.equals(that.mData);
        } else {
            return super.equals(o);
        }
    }
}
