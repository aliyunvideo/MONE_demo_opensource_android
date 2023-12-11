package com.aliyun.alivcsolution;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaPlayActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaCodec mediaCodec;
    private MediaExtractor mediaExtractor;
    private int videoTrackIndex;
    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private MediaFormat mediaFormat;
    private boolean sawInputEOS;
    private boolean sawOutputEOS;
    private HandlerThread mWorkThread;
    private Handler mPlayHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_play);

        surfaceView = findViewById(R.id.surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mediaExtractor = new MediaExtractor();
                    mediaExtractor.setDataSource("/sdcard/DCIM/Camera/IMG_0225.MOV");
                    for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
                        MediaFormat format = mediaExtractor.getTrackFormat(i);
                        String mime = format.getString(MediaFormat.KEY_MIME);
                        if (mime.equals("video/hevc")) {
                            videoTrackIndex = i;
                            mediaFormat = format;
                            break;
                        }
                    }
                    mediaExtractor.selectTrack(videoTrackIndex);
                    mediaCodec = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME));
                    mediaCodec.configure(mediaFormat, holder.getSurface(), null, 0);
                    mediaCodec.start();
                    inputBuffers = mediaCodec.getInputBuffers();
                    outputBuffers = mediaCodec.getOutputBuffers();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        mWorkThread = new HandlerThread("player_work");
        mWorkThread.start();
        mPlayHandler = new Handler(mWorkThread.getLooper());

        startPlay();
    }

    private void startPlay(){
        mPlayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                while (!sawOutputEOS) {
                    if (!sawInputEOS) {
                        int inputBufferIndex = mediaCodec.dequeueInputBuffer(10000);
                        if (inputBufferIndex >= 0) {
                            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                            int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);
                            if (sampleSize < 0) {
                                sawInputEOS = true;
                                mediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            } else {
                                long presentationTimeUs = mediaExtractor.getSampleTime();
                                mediaCodec.queueInputBuffer(inputBufferIndex, 0, sampleSize, presentationTimeUs, 0);
                                mediaExtractor.advance();
                            }
                        }
                    }

                    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                    int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000);
                    if (outputBufferIndex >= 0) {
                        ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                        outputBuffer.position(bufferInfo.offset);
                        outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            sawOutputEOS = true;
                        }
                        mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        Log.d("MainActivity", "output format changed");
                    }
                }
                mediaCodec.stop();
                mediaCodec.release();
                mediaExtractor.release();
            }
        }, 2000);
    }
}
