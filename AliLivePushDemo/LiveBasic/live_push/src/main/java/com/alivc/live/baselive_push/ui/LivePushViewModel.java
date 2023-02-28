package com.alivc.live.baselive_push.ui;

import android.app.Application;
import android.content.Context;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.alivc.live.commonbiz.ReadFileData;
import com.alivc.live.commonbiz.ResourcesDownload;
import com.alivc.live.pusher.AlivcLivePusher;

import java.io.File;

public class LivePushViewModel extends AndroidViewModel {

    private final Context mApplicationContext;
    private final ReadFileData mReadFileData;

    public LivePushViewModel(@NonNull Application application) {
        super(application);
        mApplicationContext = application;
        mReadFileData = new ReadFileData();
    }

    private void startYUV(final Context context, AlivcLivePusher mAlivcLivePusher) {
        File f = ResourcesDownload.localCaptureYUVFilePath(context);
        mReadFileData.readYUVData(f, (buffer, pts) -> mAlivcLivePusher.inputStreamVideoData(buffer, 720, 1280, 720, 1280 * 720 * 3 / 2, pts, 0));
    }

    public void stopYUV() {
        mReadFileData.stopYUV();
    }

    public void stopPcm() {
        mReadFileData.stopPcm();
    }

    private void startPCM(final Context context, AlivcLivePusher mAlivcLivePusher) {
        File f = new File(context.getFilesDir().getPath() + File.separator + "alivc_resource/441.pcm");
        mReadFileData.readPCMData(f, (buffer, length, pts) -> mAlivcLivePusher.inputStreamAudioData(buffer, length, 44100, 1, pts));
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
