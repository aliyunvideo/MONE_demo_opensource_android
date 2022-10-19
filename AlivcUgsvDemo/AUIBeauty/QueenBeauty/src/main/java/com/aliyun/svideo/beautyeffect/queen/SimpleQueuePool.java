package com.aliyun.svideo.beautyeffect.queen;


import androidx.core.util.Pools;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SimpleQueuePool<T> implements Pools.Pool<T> {

    private final LinkedBlockingQueue<T> mPool;
    private int mAcquireTimeout;
    private int mReleaseTimeout;

    private final static int DEFAULT_ACQUIRE_TIMEOUT = 3;       // 3ms的申请等待,避免卡住main线程
    private final static int DEFAULT_RELEASE_TIMEOUT = 2;       // 2ms的释放等待,避免卡住gl线程

    public SimpleQueuePool(int size) {
        this(size, DEFAULT_ACQUIRE_TIMEOUT, DEFAULT_RELEASE_TIMEOUT);
    }

    public SimpleQueuePool(int size, int acquireTimeout, int releaseTimeout) {
        mPool = new LinkedBlockingQueue<>(size);
        mAcquireTimeout = acquireTimeout;
        mReleaseTimeout = releaseTimeout;
    }

    public void clear() {
        mPool.clear();
    }

    @Override
    public T acquire() {
        T ref = null;
        if (mPool.size() > 0) {
            try {
                ref = mPool.poll(mAcquireTimeout, TimeUnit.MICROSECONDS);
            } catch (InterruptedException e) {

            }
        }
        return ref;
    }

    @Override
    public boolean release(T instance) {
        if (isInPool(instance)) {
            return false;
        }

        try {
            mPool.offer(instance, mReleaseTimeout, TimeUnit.MILLISECONDS);
            return true;
        } catch (InterruptedException e) {

        }
        return false;
    }

    private boolean isInPool(T instance) {
        for (T obj:mPool) {
            if (obj == instance) {
                return true;
            }
        }
        return false;
    }
}
