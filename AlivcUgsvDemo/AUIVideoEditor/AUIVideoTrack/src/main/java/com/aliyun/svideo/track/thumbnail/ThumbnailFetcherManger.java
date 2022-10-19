package com.aliyun.svideo.track.thumbnail;


import android.util.Log;

import com.aliyun.svideo.track.api.TrackConfig;
import com.aliyun.svideosdk.common.AliyunIThumbnailFetcher;
import com.aliyun.svideosdk.common.impl.AliyunThumbnailFetcherFactory;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ThumbnailFetcherManger {
    private static ThumbnailFetcherManger instance;

    private final HashMap<String, AliyunIThumbnailFetcher> mFetcherMap = new HashMap<>();
    private final HashMap<String, HashMap<Long, List<ThumbnailRequestListener>>> mCallbacks = new HashMap<>();
    private AliyunIThumbnailFetcher mCurFetcher;
    private int mCurTaskImageSize = 0;
    /**
     * 当前播放时间
     */
    private long mCurPlayTime;
    private long mStartTime;
    private long mStartTimeItem;

    private final ThumbnailRequestListener mThumbnailRequestListener = new ThumbnailRequestListener() {

        @Override
        public String getPath() {
            return null;
        }

        @Override
        public long getTimelineIn() {
            return 0;
        }

        @Override
        public long getTimelineOut() {
            return 0;
        }

        @Override
        public void onThumbnailReady(String path, List<ThumbnailBitmap> list) {

        }

        @Override
        public void onThumbnailReady(String path, ThumbnailBitmap thumbnailBitmap) {
            HashMap<Long, List<ThumbnailRequestListener>> mListenerMap = mCallbacks.get(path);
            if (mListenerMap == null) {
                //已移除，则取消切换到下个任务
                mCurFetcher = null;
                requestNextTask();
                return;
            }
            List<ThumbnailRequestListener> listeners = mListenerMap.get(thumbnailBitmap.getTime());
            if (listeners != null) {
                for (ThumbnailRequestListener item : listeners) {
                    item.onThumbnailReady(path, thumbnailBitmap);
                }
            }
            mListenerMap.remove(thumbnailBitmap.getTime());
            if (mListenerMap.isEmpty()) {
                mCallbacks.remove(path);
            }
            mCurTaskImageSize = mCurTaskImageSize - 1;
            if (mCurTaskImageSize <= 0) {
                Log.d("tantai", "ThumbnailFetcherManger>finish>item>" + (System.currentTimeMillis() - mStartTimeItem)+">"+path);
                mCurFetcher = null;
                requestNextTask();
                if (mCurFetcher == null) {
                    Log.d("tantai", "ThumbnailFetcherManger>finish>all>" + (System.currentTimeMillis() - mStartTime));
                }
            }
        }

        @Override
        public void onError(String path, int errorCode) {
            Set<ThumbnailRequestListener> hashSet = new HashSet<>();
            HashMap<Long, List<ThumbnailRequestListener>> mListenerMap = mCallbacks.get(path);
            if (mListenerMap != null) {
                for (Map.Entry<Long, List<ThumbnailRequestListener>> entry : mListenerMap.entrySet()) {
                    List<ThumbnailRequestListener> list = entry.getValue();
                    hashSet.addAll(list);
                }
                for (ThumbnailRequestListener listener : hashSet) {
                    listener.onError(path, errorCode);
                }
                mCallbacks.remove(path);
            }
            mCurFetcher = null;
            requestNextTask();
        }
    };

    public static ThumbnailFetcherManger getInstance() {
        if (instance == null) {
            instance = new ThumbnailFetcherManger();
        }
        return instance;
    }

    private AliyunIThumbnailFetcher getThumbnailFetcher(String path) {
        AliyunIThumbnailFetcher thumbnailFetcher = mFetcherMap.get(path);
        if (thumbnailFetcher == null) {
            thumbnailFetcher = AliyunThumbnailFetcherFactory.createThumbnailFetcher();
            thumbnailFetcher.addVideoSource(path);
            thumbnailFetcher.setParameters(TrackConfig.FRAME_WIDTH, TrackConfig.FRAME_WIDTH,
                    AliyunIThumbnailFetcher.CropMode.Mediate, VideoDisplayMode.SCALE, 1);
            thumbnailFetcher.setFastMode(true);
            mFetcherMap.put(path, thumbnailFetcher);
        }
        return thumbnailFetcher;
    }

    public void requestThumbnailImage(String path, long[] times, ThumbnailRequestListener listener) {
        Log.d("tantai","onUpdatePlayTime>requestThumbnailImage>"+listener.getTimelineIn()+">"+path+">"+mCurPlayTime);
        if (mStartTime == 0) {
            mStartTime = System.currentTimeMillis();
        }
        HashMap<Long, List<ThumbnailRequestListener>> listenerMap = mCallbacks.get(path);

        if (listenerMap == null) {
            listenerMap = new HashMap<>();
            mCallbacks.put(path, listenerMap);
        }
        for (long item : times) {
            List<ThumbnailRequestListener> list = listenerMap.get(item);
            if (list == null) {
                list = new ArrayList<>();
                listenerMap.put(item, list);
            }
            list.add(listener);
        }
        requestNextTask();
    }

    private void requestNextTask() {
        if (mCurFetcher == null && !mCallbacks.isEmpty()) {
            mStartTimeItem = System.currentTimeMillis();
            Set<ThumbnailRequestListener> set = new HashSet<>();
            for (HashMap<Long, List<ThumbnailRequestListener>> item : mCallbacks.values()) {
                for (List<ThumbnailRequestListener> list : item.values()) {
                    set.addAll(list);
                }
            }
            List<ThumbnailRequestListener> listeners = new ArrayList<>(set);
            //优先加载靠近当前播放时间缩略图
            Collections.sort(listeners,new Comparator<ThumbnailRequestListener>() {
                @Override
                public int compare(ThumbnailRequestListener o1, ThumbnailRequestListener o2) {
                    long d1 = getDistance(o1);
                    long d2 = getDistance(o2);
                    Log.d("tantai","onUpdatePlayTime>compare>o1>"+o1.getTimelineIn()+">o2>"+o2.getTimelineIn()+">d1>"+d1+">d2>"+d2+">compare>"+Long.compare(d1,d2));
                    return Long.compare(d1,d2);
                }
            });
            String path = listeners.get(0).getPath();
            Log.d("tantai","onUpdatePlayTime>requestNextTask>compare>"+listeners.get(0).getTimelineIn()+">"+path+">"+mCurPlayTime);
            HashMap<Long, List<ThumbnailRequestListener>> item = mCallbacks.get(path);
            if (item == null) {
                return;
            }
            List<Long> list = new ArrayList<>(item.keySet());
            Collections.sort(list, new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    return (int) (o1-o2);
                }
            });
            long[] times = new long[list.size()];
            for (int i = 0; i < list.size(); i++) {
                times[i] = list.get(i);
            }
            mCurFetcher = getThumbnailFetcher(path);
            mCurTaskImageSize = times.length;
            mCurFetcher.requestThumbnailImage(times, new ThumbnailFetcherCallback(path, mThumbnailRequestListener));
        }
    }

    private long getDistance(ThumbnailRequestListener listener) {
        if (listener.getTimelineIn() > mCurPlayTime) {
            return listener.getTimelineIn() - mCurPlayTime;
        } else if (listener.getTimelineOut() < mCurPlayTime) {
            return mCurPlayTime - listener.getTimelineOut();
        } else {
            return 0;
        }
    }

    public void removeThumbnailRequestListener(String path, ThumbnailRequestListener listener) {
        Log.d("tantai","onUpdatePlayTime>removeThumbnailRequestListener>"+listener.getTimelineIn()+">"+path+">"+mCurPlayTime);
        HashMap<Long, List<ThumbnailRequestListener>> mListenerMap = mCallbacks.get(path);
        if (mListenerMap == null) {
            return;
        }
        List<Long> removeKeyList = new ArrayList<>();
        for (Map.Entry<Long, List<ThumbnailRequestListener>> entry : mListenerMap.entrySet()) {
            List<ThumbnailRequestListener> list = entry.getValue();
            list.remove(listener);
            if (list.isEmpty()) {
                removeKeyList.add(entry.getKey());
            }
        }
        for (Long item : removeKeyList) {
            mListenerMap.remove(item);
        }
        if (mListenerMap.isEmpty()) {
            mCallbacks.remove(path);
        }
    }

    public void onUpdatePlayTime(long playTime) {
        mCurPlayTime = playTime;
    }

    public void release(String path) {
        mCallbacks.remove(path);
        AliyunIThumbnailFetcher fetcher = mFetcherMap.get(path);
        if (fetcher != null) {
            fetcher.release();
            mFetcherMap.remove(path);
        }
    }

    public void releaseAll() {
        mStartTime = 0;
        for (AliyunIThumbnailFetcher fetcher : mFetcherMap.values()) {
            fetcher.release();
        }
        mCallbacks.clear();
        mFetcherMap.clear();
    }


}
