package com.alivc.live.pusher.demo;

import android.view.SurfaceView;

import com.alivc.live.pusher.AlivcLivePusher;

public interface IPushController {
   public AlivcLivePusher getLivePusher();
   public LivePushActivity.PauseState getPauseStateListener();
   public SurfaceView getPreviewView();
}
