package com.alivc.live.baselive_push.adapter;

import android.view.SurfaceView;

import com.alivc.live.baselive_push.ui.LivePushActivity;
import com.alivc.live.pusher.AlivcLivePusher;

public interface IPushController {
   public AlivcLivePusher getLivePusher();
   public LivePushActivity.PauseState getPauseStateListener();
   public SurfaceView getPreviewView();
}
