/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.media;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.aliyun.svideo.media.MediaStorage.TYPE_PHOTO;


public class ThumbnailGenerator {

    public interface OnThumbnailGenerateListener {

        void onThumbnailGenerate(int key, Bitmap thumbnail);

    }


    private Executor executor;
    private ContentResolver resolver;
    private Handler handler = new Handler();
//    private MemoryCache cache;

    public ThumbnailGenerator(Context context) {
        executor = new ThreadPoolExecutor(3, 3,
                                          0L, TimeUnit.MILLISECONDS,
                                          new LruLinkedBlockingDeque<Runnable>(), new ThumbnailGeneratorFactory());
        resolver = context.getApplicationContext().getContentResolver();

    }

    public static class LruLinkedBlockingDeque<E> extends LinkedBlockingDeque<E> {
        @NonNull
        @Override
        public E take() throws InterruptedException {
            return super.takeLast();
        }

        @Override
        public E poll(long timeout, TimeUnit unit) throws InterruptedException {
            return super.pollLast(timeout, unit);
        }
    }

    public static class ThumbnailGeneratorFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("ThumbnailGenerator #");
            return thread;
        }
    }

    public void generateThumbnail(int type, int id, int resId, OnThumbnailGenerateListener listener) {

        ThumbnailTask task = new ThumbnailTask(type, id, resId, listener);

        executor.execute(task);
    }

    public void cancelAllTask() {
        ((ExecutorService)executor).shutdown();
    }

    private class ThumbnailTask implements Runnable {

        private int type;
        private int id;
        private int resId;
        private OnThumbnailGenerateListener listener;

        public ThumbnailTask(int type, int id, int resId, OnThumbnailGenerateListener listener) {
            this.type = type;
            this.id = id;
            this.resId = resId;
            this.listener = listener;
        }

        @Override
        public void run() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            final Bitmap bitmap ;
            if (type == TYPE_PHOTO) {
                bitmap = MediaStore.Images.Thumbnails.getThumbnail(resolver,
                         id == -1 ? resId : id, MediaStore.Images.Thumbnails.MINI_KIND, options);
            } else {
                bitmap = MediaStore.Video.Thumbnails.getThumbnail(resolver,
                         id == -1 ? resId : id, MediaStore.Video.Thumbnails.MINI_KIND, options);
            }
            final int key = generateKey(type, id);
            if (bitmap == null) {

                return ;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onThumbnailGenerate(key, bitmap);
                        listener = null;
                    }
                }
            });
        }

    }

    public static String generateKey(String imageUri, int width, int height) {
        return imageUri + "_" + width + "x" + height;
    }

    public static int generateKey(int type, int id) {
        return type << 16 | id;
    }

    class MemoryCache {
        private final Map<String, Reference<Bitmap>> softMap = new HashMap<>();

        public Bitmap get(String key) {
            Bitmap result = null;
            Reference reference = (Reference)this.softMap.get(key);
            if (reference != null) {
                result = (Bitmap)reference.get();
            }

            return result;
        }

        public boolean put(String key, Bitmap value) {
            this.softMap.put(key, this.createReference(value));
            return true;
        }

        public Bitmap remove(String key) {
            Reference bmpRef = (Reference)this.softMap.remove(key);
            return bmpRef == null ? null : (Bitmap)bmpRef.get();
        }

        public Collection<String> keys() {
            Map var1 = this.softMap;
            synchronized (this.softMap) {
                return new HashSet(this.softMap.keySet());
            }
        }

        public void clear() {
            for (Reference<Bitmap> ref : softMap.values()) {
                Bitmap bmp = ref.get();
                if (bmp != null) {
                    bmp.recycle();
                }
            }
            this.softMap.clear();
        }

        protected Reference<Bitmap> createReference(Bitmap var1) {
            return new SoftReference<>(var1);
        }

    }

}
