package com.alivc.player.videolist.auivideostandradlist;

import android.content.Context;
import android.util.SparseArray;
import android.view.Surface;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;
import com.aliyun.player.AliListPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;

import java.util.List;
import java.util.UUID;

public class AUIVideoStandardListController {

    private final AliListPlayer aliListPlayer;
    private int mOldPosition = 0;
    private final SparseArray<String> mIndexWithUUID = new SparseArray<>();
    private PlayerListener mPlayerListener;


    public AUIVideoStandardListController(Context context) {
        aliListPlayer = AliPlayerFactory.createAliListPlayer(context);
        aliListPlayer.setLoop(true);

        aliListPlayer.setOnCompletionListener(() -> {
            if (mPlayerListener != null) {
                mPlayerListener.onCompletion(-1);
            }
        });
    }

    public void loadSource(List<VideoInfo> listVideo) {
        aliListPlayer.clear();
        mIndexWithUUID.clear();
        for (int i = 0; i < listVideo.size(); i++) {
            String randomUUID = UUID.randomUUID().toString();
            mIndexWithUUID.put(i,randomUUID);
            aliListPlayer.addUrl(listVideo.get(i).getUrl(),randomUUID);
        }
    }

    public void addSource(List<VideoInfo> videoBeanList) {
        for (int i = 0; i < videoBeanList.size(); i++) {
            String randomUUID = UUID.randomUUID().toString();
            mIndexWithUUID.put(i + mIndexWithUUID.size(),randomUUID);
            aliListPlayer.addUrl(videoBeanList.get(i).getUrl(),randomUUID);
        }
    }

    public void openLoopPlay(boolean openLoopPlay) {
        aliListPlayer.setLoop(openLoopPlay);
    }

    public void setPlayerListener (PlayerListener listener) {
        this.mPlayerListener = listener;
    }

    public void onPageSelected(int position) {
        if (position == 0) {
            aliListPlayer.moveTo(mIndexWithUUID.get(position));
        } else {
            if (mOldPosition < position) {
                aliListPlayer.moveToNext();
            } else {
                aliListPlayer.moveToPrev();
            }
        }
        this.mOldPosition = position;
    }

    public void setSurface(Surface surface) {
        aliListPlayer.setSurface(surface);
    }

    public void surfaceChanged() {
        aliListPlayer.surfaceChanged();
    }

    public void destroy() {
        aliListPlayer.clear();
        aliListPlayer.stop();
        aliListPlayer.release();
    }
}
