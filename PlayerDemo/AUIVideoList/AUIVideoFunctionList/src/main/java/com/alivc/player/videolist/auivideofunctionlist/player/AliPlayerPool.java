package com.alivc.player.videolist.auivideofunctionlist.player;

import android.content.Context;


import java.util.ArrayDeque;

/**
 * Save {@link AliyunRenderView} with Queue
 */
public class AliPlayerPool {
    public static final int TOTAL_SIZE = 3;

    private static final ArrayDeque<AliyunRenderView> mDequeue = new ArrayDeque<>(TOTAL_SIZE);

    private AliPlayerPool() {
    }

    public static void init(Context context) {
        if (mDequeue.size() != TOTAL_SIZE) {
            mDequeue.clear();
            mDequeue.add(new AliyunRenderView(context));
            mDequeue.add(new AliyunRenderView(context));
            mDequeue.add(new AliyunRenderView(context));
        }
    }

    /**
     * get {@link AliyunRenderView}
     * pollFirst and then addLast
     */
    public static AliyunRenderView getPlayer() {
        AliyunRenderView aliyunRenderView = mDequeue.pollFirst();
        mDequeue.addLast(aliyunRenderView);
        return aliyunRenderView;
    }

    public static void release() {
        for (AliyunRenderView aliyunRenderView : mDequeue) {
            aliyunRenderView.release();
        }
        mDequeue.clear();
    }

    public static void openLoopPlay(boolean openLoopPlay) {
        for (AliyunRenderView aliyunRenderView : mDequeue) {
            aliyunRenderView.openLoopPlay(openLoopPlay);
        }
    }
}
