/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.downloader;

import android.util.SparseArray;

class ListenerManager {
    private SparseArray<BridgeListener> mListenerListArray;

    protected ListenerManager() {
        mListenerListArray = new SparseArray<>();
    }

    public BridgeListener getBridgeListener(int downloadId) {
        BridgeListener listener = mListenerListArray.get(downloadId);
        if ( listener == null ) {
            listener = new BridgeListener();
        }
        mListenerListArray.put(downloadId, listener);
        return listener;
    }

    public void removeDownloadListener(int downloadId, FileDownloaderCallback dlistener) {
        BridgeListener listener = mListenerListArray.get(downloadId);
        if ( listener != null ) {
            listener.removeDownloadListener(dlistener);
        }
    }

    public void removeAllDownloadListener(final int downloadId) {
        BridgeListener listener = mListenerListArray.get(downloadId);
        if ( listener != null ) {
            listener.removeAllDownloadListener();
        }
    }

    public void removeAllDownloadListener() {
        for (int i = 0; i < mListenerListArray.size(); i++ ) {
            int key = mListenerListArray.keyAt(i);
            BridgeListener listener = mListenerListArray.get(key);
            if (listener != null) {
                listener.removeAllDownloadListener();
            }
        }
        mListenerListArray.clear();
    }
}
