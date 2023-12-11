package com.aliyun.svideo.mixrecorder.view.music;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliyun.svideo.base.BaseChooser;
import com.aliyun.svideo.music.music.MusicChooseView;
import com.aliyun.svideo.music.music.MusicFileBean;
import com.aliyun.svideo.record.R;

/**
 * @author zsy_18 data:2018/8/29
 */
public class MusicChooser extends BaseChooser {
    private MusicSelectListener musicSelectListener;
    //视频录制时长
    private int mRecordTime = 10 * 1000;
    private MusicChooseView musicChooseView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.QUDemoFullFitStyle);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (musicChooseView == null) {
            musicChooseView = new MusicChooseView(getContext());

            musicChooseView.setStreamDuration(mRecordTime);
            musicChooseView.setMusicSelectListener(new com.aliyun.svideo.music.music.MusicSelectListener() {
                @Override
                public void onMusicSelect(MusicFileBean musicFileBean, long startTime) {
                    if (musicSelectListener != null) {
                        musicSelectListener.onMusicSelect(musicFileBean, startTime);
                    }
                    dismiss();
                }

                @Override
                public void onCancel() {
                    dismiss();
                }
            });
        } else {
            if (musicChooseView.getParent() != null) {
                ((ViewGroup)musicChooseView.getParent()).removeView(musicChooseView);
            }
        }
        return musicChooseView;
    }

    public void setMusicSelectListener(MusicSelectListener musicSelectListener) {

        this.musicSelectListener = musicSelectListener;
    }

    public void setRecordTime(int mRecordTime) {
        this.mRecordTime = mRecordTime;
        if (musicChooseView != null) {
            musicChooseView.setStreamDuration(mRecordTime);
        }
    }
    /**
     * 设置view的可见状态, 会在activity的onStart和onStop中调用
     * @param visibleStatus true: 可见, false: 不可见
     */
    public void setVisibleStatus(boolean visibleStatus) {
        if (musicChooseView != null) {
            musicChooseView.setVisibleStatus(visibleStatus);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //处理部分手机在锁屏的状态下会调用onResume
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        if (pm != null && pm.isScreenOn()) {
            setVisibleStatus(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        setVisibleStatus(false);
    }
}
