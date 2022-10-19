package com.aliyun.svideo.editor.controller;

import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.aliyun.aio.avbaseui.AVBaseDialogFragment;
import com.aliyun.svideo.music.MusicSelectListener;
import com.aliyun.svideo.music.MusicSelectPanel;
import com.aliyun.svideo.music.music.MusicFileBean;
import com.aliyun.svideosdk.common.struct.effect.TrackAudioStream;
import com.aliyun.svideosdk.common.struct.project.Source;
import com.aliyun.svideosdk.editor.AliyunIEditor;

/**
 * @author geekeraven
 */
public class MusicSelectController implements AVBaseDialogFragment.DialogVisibleListener, MusicSelectListener {

    private MusicSelectPanel mMusicSelectPanel;
    private AliyunIEditor mAliyunIEditor;
    private TrackAudioStream mTrackAudioStream;
    private final String TAG = "MusicSelectController";

    public MusicSelectController(Context context, AliyunIEditor aliyunIEditor) {
        mMusicSelectPanel = new MusicSelectPanel();
        mAliyunIEditor = aliyunIEditor;
        mMusicSelectPanel.setMusicSelectListener(this);
    }

    public void showMusicSelectPanel(FragmentManager manager) {
        Fragment fragment = manager.findFragmentByTag(TAG);
        if(fragment != null) {
            manager.beginTransaction().remove(fragment).commitNowAllowingStateLoss();
        }
        mMusicSelectPanel.setStreamDuration(mAliyunIEditor.getDuration() / 1000);
        mMusicSelectPanel.show(manager, TAG);
    }

    @Override
    public void onDialogDismiss() {

    }

    @Override
    public void onDialogShow() {

    }

    @Override
    public void onMusicSelect(MusicFileBean musicFileBean, long startTime) {
        if (musicFileBean == null || TextUtils.isEmpty(musicFileBean.path)) {
            if (mTrackAudioStream != null) {
                mAliyunIEditor.removeMusic(mTrackAudioStream);
            }
        } else {
            if (mTrackAudioStream != null) {
                mAliyunIEditor.removeMusic(mTrackAudioStream);
            }
            Source source = new Source();
            source.setPath(musicFileBean.path);
            mTrackAudioStream = new TrackAudioStream.Builder()
                .source(source)
                .audioWeight(100)
                .streamStartTime(startTime, TimeUnit.MILLISECONDS)
                .streamDuration(musicFileBean.duration, TimeUnit.MILLISECONDS).build();
            mAliyunIEditor.applyMusic(mTrackAudioStream);
        }
    }

    @Override
    public void onCancel() {

    }
}
