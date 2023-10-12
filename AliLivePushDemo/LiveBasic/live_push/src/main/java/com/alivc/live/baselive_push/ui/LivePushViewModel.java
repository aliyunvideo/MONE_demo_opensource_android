package com.alivc.live.baselive_push.ui;

import android.app.Application;
import android.content.Context;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.alivc.live.commonbiz.LocalStreamReader;
import com.alivc.live.commonbiz.ResourcesConst;
import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.AlivcLivePusher;
import com.alivc.live.pusher.AlivcResolutionEnum;

import java.io.File;

public class LivePushViewModel extends AndroidViewModel {

    private final Context mApplicationContext;
    private LocalStreamReader mLocalStreamReader;

    public LivePushViewModel(@NonNull Application application) {
        super(application);
        mApplicationContext = application;
    }

    public void initReadFile(AlivcLivePushConfig config) {
        AlivcResolutionEnum resolution = config.getResolution();
        int width = AlivcResolutionEnum.getResolutionWidth(resolution, config.getLivePushMode());
        int height = AlivcResolutionEnum.getResolutionHeight(resolution, config.getLivePushMode());
        mLocalStreamReader = new LocalStreamReader.Builder()
                .setVideoWith(width)
                .setVideoHeight(height)
                .setVideoStride(width)
                .setVideoSize(height * width * 3 / 2)
                .setVideoRotation(0)
                .setAudioSampleRate(44100)
                .setAudioChannel(1)
                .setAudioBufferSize(2048)
                .build();
    }

    private void startYUV(final Context context, AlivcLivePusher mAlivcLivePusher) {
        File f = ResourcesConst.localCaptureYUVFilePath(context);
        mLocalStreamReader.readYUVData(f, (buffer, pts, videoWidth, videoHeight, videoStride, videoSize, videoRotation) -> {
            mAlivcLivePusher.inputStreamVideoData(buffer, videoWidth, videoHeight, videoStride, videoSize, pts, videoRotation);
        });
    }

    public void stopYUV() {
        mLocalStreamReader.stopYUV();
    }

    public void stopPcm() {
        mLocalStreamReader.stopPcm();
    }

    private void startPCM(final Context context, AlivcLivePusher mAlivcLivePusher) {
        File f = ResourcesConst.localCapturePCMFilePath(context);
        mLocalStreamReader.readPCMData(f, (buffer, length, pts, audioSampleRate, audioChannel) -> {
            mAlivcLivePusher.inputStreamAudioData(buffer, length, audioSampleRate, audioChannel, pts);
        });
    }

    public void onSurfaceCreated(boolean mAsync, SurfaceView mPreviewView, AlivcLivePusher mAlivcLivePusher) {
        if (mAlivcLivePusher != null) {
            if (mAsync) {
                mAlivcLivePusher.startPreviewAsync(mPreviewView);
            } else {
                mAlivcLivePusher.startPreview(mPreviewView);
            }
        }
    }

    public void startYUV(AlivcLivePusher mAlivcLivePusher) {
        startYUV(mApplicationContext, mAlivcLivePusher);
    }

    public void startPCM(AlivcLivePusher mAlivcLivePusher) {
        startPCM(mApplicationContext, mAlivcLivePusher);
    }
}
