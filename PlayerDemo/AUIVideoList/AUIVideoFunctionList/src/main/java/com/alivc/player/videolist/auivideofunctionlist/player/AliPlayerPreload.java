package com.alivc.player.videolist.auivideofunctionlist.player;

import android.text.TextUtils;
import android.util.Log;

import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.aliyun.loader.MediaLoader;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author baorunchen
 * @date 2024/4/19
 * @brief By using the idea of sliding windows, handle the logic of MediaLoader preloading to ensure the effect of full screen second opening
 * @note Need to be used in conjunction with local caching functionality: {@link com.aliyun.player.AliPlayerGlobalSettings#enableLocalCache(boolean, int, String)}  }
 */
public class AliPlayerPreload {
    private static final String TAG = "[AUI]AliPlayerPreload";

    private static final int PRELOAD_BUFFER_DURATION = 3 * 1000;
    private static final int WINDOW_SIZE = 2;

    private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    private final List<VideoInfo> videoList = new LinkedList<>();
    private final Queue<String> loadQueue = new LinkedList<>();
    private final HashSet<String> loadedUrls = new HashSet<>();

    private int currentPosition = -1;

    private MediaLoader mMediaLoader;

    private AliPlayerPreload() {
    }

    private static class Inner {
        private static final AliPlayerPreload instance = new AliPlayerPreload();
    }

    public static AliPlayerPreload getInstance() {
        return Inner.instance;
    }

    /**
     * initialize the media loader
     */
    public void init() {
        Log.i(TAG, "[API][INIT]");
        release();
        initMediaLoader();
    }

    /**
     * release the media loader
     */
    public void release() {
        Log.i(TAG, "[API][RELEASE]");
        cancelAll();
        releaseMediaLoader();

        currentPosition = -1;
        videoList.clear();
        loadQueue.clear();
    }

    /**
     * force update the video list URLs
     *
     * @param videoBeanList video list URLs
     */
    public synchronized void setUrls(List<VideoInfo> videoBeanList) {
        cancelAll();
        videoList.addAll(videoBeanList);
        Log.i(TAG, "[API][SET][" + videoList + "]");
    }

    /**
     * append the video list URLs
     *
     * @param videoBeanList video list URLs
     */
    public synchronized void addUrls(List<VideoInfo> videoBeanList) {
        videoList.addAll(videoBeanList);
        Log.i(TAG, "[API][ADD][" + videoList + "]");
    }

    /**
     * move to specific position
     *
     * @param position the position of video list
     */
    public synchronized void moveTo(int position) {
        Log.i(TAG, "[API][MOVE][" + currentPosition + "->" + position + "]");
        if (position < 0 || position >= videoList.size()) {
            return;
        }

        if (position == currentPosition) {
            return;
        }

        List<Integer> newWindow = getWindowIndices(position);
        List<Integer> toLoad = new LinkedList<>(newWindow);
        if (currentPosition < 0) {
            loadUrlsInWindow(newWindow);
        } else {
            List<Integer> prevWindow = getWindowIndices(currentPosition);
            List<Integer> toCancel = new LinkedList<>(prevWindow);
            toCancel.removeAll(newWindow);
            cancelUrlsInWindow(toCancel);

            toLoad.removeAll(prevWindow);
        }
        loadUrlsInWindow(toLoad);

        currentPosition = position;
    }

    private void cancelUrlsInWindow(List<Integer> indices) {
        for (Integer index : indices) {
            String urlToCancel = videoList.get(index).getUrl();
            cancel(urlToCancel);
        }
    }

    private synchronized void loadUrlsInWindow(List<Integer> indices) {
        for (Integer index : indices) {
            String urlToLoad = videoList.get(index).getUrl();
            if (!loadedUrls.contains(urlToLoad)) {
                loadedUrls.add(urlToLoad);
                loadQueue.offer(urlToLoad);
            }
        }
        loadNextInQueue();
    }

    private List<Integer> getWindowIndices(int position) {
        List<Integer> windowIndices = new LinkedList<>();
        for (int i = -WINDOW_SIZE; i <= WINDOW_SIZE; i++) {
            int index = position + i;
            if (index >= 0 && index < videoList.size()) {
                windowIndices.add(index);
            }
        }
        return windowIndices;
    }

    private synchronized void loadNextInQueue() {
        String urlToLoad = loadQueue.poll();
        if (urlToLoad != null) {
            load(urlToLoad);
        }
    }

    private void load(String videoUrl) {
        mExecutorService.execute(() -> {
            Log.i(TAG, "[LOAD][" + queryVideoInfoByUrl(videoUrl) + "]");
            if (mMediaLoader != null && !TextUtils.isEmpty(videoUrl)) {
                mMediaLoader.load(videoUrl, PRELOAD_BUFFER_DURATION);
                synchronized (this) {
                    loadedUrls.add(videoUrl);
                }
            }
        });
    }

    private void cancel(String videoUrl) {
        mExecutorService.execute(() -> {
            Log.i(TAG, "[CANCEL][" + queryVideoInfoByUrl(videoUrl) + "]");
            if (mMediaLoader != null && !TextUtils.isEmpty(videoUrl)) {
                mMediaLoader.cancel(videoUrl);
                synchronized (this) {
                    loadedUrls.remove(videoUrl);
                    loadQueue.remove(videoUrl);
                }
            }
        });
    }

    private synchronized void cancelAll() {
        HashSet<String> urlsToCancel = new HashSet<>(loadedUrls);
        for (String url : urlsToCancel) {
            cancel(url);
        }
        loadedUrls.clear();
        loadQueue.clear();
        videoList.clear();
    }

    private void initMediaLoader() {
        mMediaLoader = MediaLoader.getInstance();
        mMediaLoader.setOnLoadStatusListener(new MediaLoader.OnLoadStatusListener() {
            @Override
            public void onError(String s, int i, String s1) {
                Log.e(TAG, "[CBK][ERROR][" + queryVideoInfoByUrl(s) + "][" + i + "][" + s1 + "]");
                loadNextInQueue();
            }

            @Override
            public void onCompleted(String s) {
                Log.i(TAG, "[CBK][COMPLETE][" + queryVideoInfoByUrl(s) + "]");
                loadNextInQueue();
            }

            @Override
            public void onCanceled(String s) {
                Log.w(TAG, "[CBK][CANCEL][" + queryVideoInfoByUrl(s) + "]");
                loadNextInQueue();
            }
        });
    }

    private void releaseMediaLoader() {
        if (mMediaLoader != null) {
            mMediaLoader.setOnLoadStatusListener(null);
            mMediaLoader = null;
        }
    }

    private String queryVideoInfoByUrl(String url) {
        for (VideoInfo videoInfo : videoList) {
            if (videoInfo != null && TextUtils.equals(url, videoInfo.getUrl())) {
                return videoInfo.toString();
            }
        }
        return null;
    }
}
