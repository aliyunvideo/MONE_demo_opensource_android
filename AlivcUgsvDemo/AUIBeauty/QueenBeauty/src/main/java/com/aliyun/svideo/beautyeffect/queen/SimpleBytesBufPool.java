package com.aliyun.svideo.beautyeffect.queen;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleBytesBufPool {
    // 缓存大小
    private final SimpleQueuePool<byte[]> mBytesBufQueuePool;
    private int mByteLength;
    private FrameBuffer mLastFrameBuffer;
    private FrameBuffer mOldFrameBuffer;

    public SimpleBytesBufPool(int capacity, int byteLength) {
        mBytesBufQueuePool = new SimpleQueuePool<>(capacity);
        mByteLength = byteLength;
        mLastFrameBuffer = new FrameBuffer();
        mOldFrameBuffer = new FrameBuffer();
    }

    // 生产数据
    public void updateBuffer(byte[] data) {
        // 引入两个FrameBuffer,更新赋值不用进行同步
        FrameBuffer oldFrameBuffer = mLastFrameBuffer;

        mOldFrameBuffer.resetData(data);
        mLastFrameBuffer = mOldFrameBuffer;

        oldFrameBuffer.releaseBuf2Pool(mBytesBufQueuePool);
        mOldFrameBuffer = oldFrameBuffer;
    }

    public byte[] getLastUpdateBuffer() {
        return isNewBufferUpdate() ? getLastBuffer() : null;
    }

    // 取用最后数据,只在gl线程
    public byte[] getLastBuffer() {
        mLastGetFrameBuf = mLastFrameBuffer;
        return mLastFrameBuffer.getBuffer();
    }

    private FrameBuffer mLastGetFrameBuf;
    public boolean isNewBufferUpdate() {
        return mLastFrameBuffer != mLastGetFrameBuf;
    }

    public void releaseBuffer(byte[] buffer) {
        mBytesBufQueuePool.release(buffer);
    }

    // 生产复用数据
    public byte[] reusedBuffer() {
        byte[] descData = mBytesBufQueuePool.acquire();
        if (descData == null) {
            descData = new byte[mByteLength];
        }
        return descData;
    }

    public void clear() {
        mBytesBufQueuePool.clear();
    }

    class FrameBuffer {
        int getCnt;
        byte[] buffer;

        public Lock lockObj = new ReentrantLock(false);

        public byte[] getBuffer() {
            lockObj.lock();
                getCnt = 1;
                byte[] data = buffer;
            lockObj.unlock();
            return data;
        }

        public boolean isNotGetUsed() {
            return getCnt < 1 && buffer != null;
        }

        public void releaseBuf2Pool(SimpleQueuePool<byte[]> bytesBufQueuePool) {
            lockObj.lock();
                if (isNotGetUsed()) {
                    // 上一帧数据尚未使用,则释放掉数据
                    bytesBufQueuePool.release(buffer);
                }
            lockObj.unlock();
        }

        public void resetData(byte[] data) {
            getCnt = 0;
            buffer = data;
        }
    }
}
