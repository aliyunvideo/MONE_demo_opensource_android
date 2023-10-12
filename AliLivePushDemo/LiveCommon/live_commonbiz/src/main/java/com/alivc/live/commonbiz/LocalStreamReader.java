package com.alivc.live.commonbiz;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @implNote 该类为读取本地音视频流的实现类
 * 注意：为了适配本地不同格式的音视频流，需要传入对应的profiles才能正常播放
 * 如：当前项目中，capture0.yuv为720p，请注意调节demo配置页中的分辨率为720p
 */
public class LocalStreamReader {

    private static final String TAG = "LocalStreamReader";

    private boolean videoThreadOn = false;
    private boolean audioThreadOn = false;
    private int videoWidth;
    private int videoHeight;
    private int videoStride;
    private int videoSize;
    private int videoRotation;
    private int audioSampleRate;
    private int audioChannel;
    private int audioBufferSize;

    public static final class Builder {
        private final LocalStreamReader localStreamReader = new LocalStreamReader();

        public Builder setVideoWith(int videoWith) {
            localStreamReader.videoWidth = videoWith;
            return this;
        }

        public Builder setVideoHeight(int videoHeight) {
            localStreamReader.videoHeight = videoHeight;
            return this;
        }

        public Builder setVideoSize(int videoSize) {
            localStreamReader.videoSize = videoSize;
            return this;
        }

        public Builder setVideoStride(int videoStride) {
            localStreamReader.videoStride = videoStride;
            return this;
        }

        public Builder setVideoRotation(int videoRotation) {
            localStreamReader.videoRotation = videoRotation;
            return this;
        }

        public Builder setAudioSampleRate(int audioSampleRate) {
            localStreamReader.audioSampleRate = audioSampleRate;
            return this;
        }

        public Builder setAudioChannel(int audioChannel) {
            localStreamReader.audioChannel = audioChannel;
            return this;
        }

        public Builder setAudioBufferSize(int audioBufferSize) {
            localStreamReader.audioBufferSize = audioBufferSize;
            return this;
        }

        public LocalStreamReader build() {
            return localStreamReader;
        }

    }

    public void readYUVData(File f, ReadYUVFileListener listener) {
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
                InputStream myInput;
                try {
                    myInput = new FileInputStream(f);
                    byte[] buffer = new byte[videoSize];
                    int length = myInput.read(buffer);
                    //发数据
                    while (length > 0 && videoThreadOn) {
                        long pts = System.currentTimeMillis() * 1000;
                        listener.onVideoStreamData(buffer, pts, videoWidth, videoHeight, videoStride, videoSize, videoRotation);
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

    public void readPCMData(File f, ReadPCMFileListener listener) {
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
                int allSended = 0;
                int sizePerSecond = audioSampleRate * 2;
                InputStream myInput;
                long startPts = System.nanoTime() / 1000;
                try {
                    myInput = new FileInputStream(f);
                    byte[] buffer = new byte[audioBufferSize];
                    int length = myInput.read(buffer, 0, audioBufferSize);
                    if (audioSampleRate <= 0) {
                        Log.e(TAG, "audio sample rate error cause return: " + audioSampleRate);
                        return;
                    }
                    double sleep_time = 1000 / (audioSampleRate / 1000);
                    while (length > 0 && audioThreadOn) {
                        long start = System.nanoTime();
                        long pts = System.currentTimeMillis() * 1000;
                        listener.onAudioStreamData(buffer, length, pts, audioSampleRate, audioChannel);
                        allSended += length;
                        if ((allSended * 1000000L / sizePerSecond - 50000) > (pts - startPts)) {
                            try {
                                Thread.sleep(45);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        length = myInput.read(buffer);
                        if (length < audioBufferSize) {
                            myInput.close();
                            myInput = new FileInputStream(f);
                            length = myInput.read(buffer);
                        }
                        long end = System.nanoTime();
                        try {
                            long real_sleep_time = (long) (sleep_time - (end - start) / 1000 / 1000);
                            if (real_sleep_time > 0) {
                                Thread.sleep(real_sleep_time);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    myInput.close();
                    audioThreadOn = false;
                } catch (IOException e) {
                    Log.e(TAG, "audio IOException: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    public void stopYUV() {
        videoThreadOn = false;
    }

    public void stopPcm() {
        audioThreadOn = false;
    }

    public interface ReadYUVFileListener {
        void onVideoStreamData(byte[] buffer, long pts, int videoWidth, int videoHeight, int videoStride, int videoSize, int videoRotation);
    }

    public interface ReadPCMFileListener {
        void onAudioStreamData(byte[] buffer, int length, long pts, int audioSampleRate, int audioChannel);
    }
}
