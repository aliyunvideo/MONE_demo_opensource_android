/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.downloader;

import android.content.Context;

import java.util.Map;

import okhttp3.Headers;

public class DownloaderManagerConfiguration {

    private Context mContext;
    private String mDownloadStorePath;
    private int mMaxDownloadingCount = 3;
    private Map<String, String> mDbExtFieldMap;
    private int mDbVersion;
    private boolean mDebug;
    private DbUpgradeListener mDbUpgradeListener;
    private int mAutoRetryTimes;
    private Headers mHeaders;
    private boolean mIsCipher;
    private String mCk;

    private DownloaderManagerConfiguration(final Builder builder) {
        this.mContext = builder.mContext;
        this.mDownloadStorePath = builder.mDownloadStorePath;
        this.mDbExtFieldMap = builder.mDbExtFieldMap;
        this.mDbVersion = builder.mDbVersion;
        this.mDbUpgradeListener = builder.mDbUpgradeListener;
        this.mDebug = builder.mDebug;
        this.mCk = builder.mCk;
        this.mIsCipher = builder.mIsCipher;

        if (builder.mMaxDownloadingCount > 0) {
            this.mMaxDownloadingCount = builder.mMaxDownloadingCount;
        }

        this.mAutoRetryTimes = builder.mAutoRetryTimes;
        this.mHeaders = builder.mHeaders.build();
    }

    public static class Builder {
        private Context mContext;
        private String mDownloadStorePath;
        private int mMaxDownloadingCount = 1;
        private Map<String, String> mDbExtFieldMap;
        private int mDbVersion = 1;
        private DbUpgradeListener mDbUpgradeListener;
        private boolean mDebug;
        private int mAutoRetryTimes = 3;
        private Headers.Builder mHeaders;
        private boolean mIsCipher;
        private String mCk;

        public Builder(Context context) {
            this.mContext = context;
            mHeaders = new Headers.Builder();
        }

        /**
         * 设置下载存储目录
         *
         * @param path
         * @return
         */
        public Builder setDownloadStorePath(String path) {
            this.mDownloadStorePath = path;
            return this;
        }

        /**
         * 设置最大并行下载数
         *
         * @param maxCount
         * @return
         */
        public Builder setMaxDownloadingCount(int maxCount) {
            this.mMaxDownloadingCount = maxCount;
            return this;
        }

        /**
         * 设置表扩展字段
         *
         * @param extFieldMap
         * @return
         */
        public Builder setDbExtField(Map<String, String> extFieldMap) {
            this.mDbExtFieldMap = extFieldMap;
            return this;
        }

        /**
         * 数据库版本号
         *
         * @param dbVersion
         * @return
         */
        public Builder setDbVersion(int dbVersion) {
            this.mDbVersion = dbVersion;
            return this;
        }

        /**
         * 自动重试次数
         *
         * @param autoRetryTimes
         * @return
         */
        public Builder setAutoRetryTimes(int autoRetryTimes) {
            this.mAutoRetryTimes = autoRetryTimes;
            return this;
        }

        /**
         * 数据库更新监听
         *
         * @param dbUpgradeListener
         * @return
         */
        public Builder setDbUpgradeListener(DbUpgradeListener dbUpgradeListener) {
            this.mDbUpgradeListener = dbUpgradeListener;
            return this;
        }

        /**
         * 添加header
         *
         * @param line
         * @return
         */
        public Builder addHeader(String line) {
            mHeaders.add(line);
            return this;
        }

        /**
         * 添加header
         *
         * @param name
         * @param value
         * @return
         */
        public Builder addHeader(String name, String value) {
            mHeaders.add(name, value);
            return this;
        }

        /**
         * 设置是否开启debug
         *
         * @param debug
         * @return
         */
        public Builder setDebug(boolean debug) {
            this.mDebug = debug;
            return this;
        }

        public Builder setCipher(boolean isCipher) {
            mIsCipher = isCipher;
            return this;
        }

        /**
         * 安全扫描中不允许存在密码关键词
         *
         * @return
         */
        public Builder setCK(String ck) {
            mCk = ck;
            return this;
        }

        public DownloaderManagerConfiguration build() {
            return new DownloaderManagerConfiguration(this);
        }

    }

    public Context getContext() {
        return mContext;
    }

    public String getDownloadStorePath() {
        return mDownloadStorePath;
    }

    public int getMaxDownloadingCount() {
        return mMaxDownloadingCount;
    }

    public Map<String, String> getDbExtField() {
        return mDbExtFieldMap;
    }

    public DbUpgradeListener getDbUpgradeListener() {
        return mDbUpgradeListener;
    }

    public int getDbVersion() {
        return mDbVersion;
    }

    public boolean isDebug() {
        return mDebug;
    }

    public int getAutoRetryTimes() {
        return mAutoRetryTimes;
    }

    public Headers getHeaders() {
        return mHeaders;
    }

    public boolean iCipher() {
        return mIsCipher;
    }

    public String getCk() {
        return mCk;
    }
}
