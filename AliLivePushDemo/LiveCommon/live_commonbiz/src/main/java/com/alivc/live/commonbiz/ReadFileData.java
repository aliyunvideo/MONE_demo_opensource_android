package com.alivc.live.commonbiz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ReadFileData {

    private boolean videoThreadOn = false;
    private boolean audioThreadOn = false;

    public void stopYUV() {
        videoThreadOn = false;
    }

    public void stopPcm() {
        audioThreadOn = false;
    }

    public void readYUVData(File f,ReadYUVFileListener listener){
        new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            private AtomicInteger atoInteger = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("LivePushActivity-readYUV-Thread" + atoInteger.getAndIncrement());
                return t;
            }
        }).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                videoThreadOn = true;
                byte[] yuv;
                InputStream myInput = null;
                try {
                    myInput = new FileInputStream(f);
                    byte[] buffer = new byte[1280 * 720 * 3 / 2];
                    int length = myInput.read(buffer);
                    //发数据
                    while (length > 0 && videoThreadOn) {
                        long pts = System.currentTimeMillis() * 1000;
                        listener.onVideoStreamData(buffer,pts);
                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //发数据
                        length = myInput.read(buffer);
                        if (length <= 0) {
                            myInput.close();
                            myInput = new FileInputStream(f);
                            length = myInput.read(buffer);
                        }
                    }
                    myInput.close();
                    videoThreadOn = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void readPCMData(File f,ReadPCMFileListener listener) {
        new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            private AtomicInteger atoInteger = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("LivePushActivity-readPCM-Thread" + atoInteger.getAndIncrement());
                return t;
            }
        }).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                audioThreadOn = true;
                byte[] pcm;
                int allSended = 0;
                int sizePerSecond = 44100 * 2;
                InputStream myInput = null;
                OutputStream myOutput = null;
                boolean reUse = false;
                long startPts = System.nanoTime() / 1000;
                try {
                    myInput = new FileInputStream(f);
                    // File f = new File("/sdcard/alivc_resource/441.pcm");
                    byte[] buffer = new byte[2048];
                    int length = myInput.read(buffer, 0, 2048);
                    double sleep_time = 1000 / 44.1;
                    while (length > 0 && audioThreadOn) {
                        long start = System.nanoTime();
                        long pts = System.currentTimeMillis() * 1000;
                        listener.onAudioStreamData(buffer,length,pts);
                        allSended += length;
                        if ((allSended * 1000000L / sizePerSecond - 50000) > (pts - startPts)) {
                            try {
                                Thread.sleep(45);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        length = myInput.read(buffer);
                        if (length < 2048) {
                            myInput.close();
                            myInput = new FileInputStream(f);
                            length = myInput.read(buffer);
                        }
                        long end = System.nanoTime();
                        try {
                            long real_sleep_time = (long) (sleep_time - (end - start) / 1000 / 1000);
                            if(real_sleep_time > 0){
                                Thread.sleep(real_sleep_time);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    myInput.close();
                    audioThreadOn = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface ReadYUVFileListener{
        void onVideoStreamData(byte[] buffer,long pts);
    }

    public interface ReadPCMFileListener{
        void onAudioStreamData(byte[] buffer,int length,long pts);
    }
}
