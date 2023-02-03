package com.aliyun.svideo.template.sample.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aliyun.aio.avbaseui.AVBaseDialogFragment;
import com.aliyun.svideo.music.music.MusicFileBean;
import com.aliyun.svideo.template.sample.ui.callback.MusicSelectListener;

public class MusicSelectPanel extends AVBaseDialogFragment {
    private MusicSelectListener musicSelectListener;
    private long mStreamDuration = 0;
    private MusicChooseView musicChooseView;
    private MusicFileBean mCacheMusicBean;
    private DialogVisibleListener mVisibleListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, com.aliyun.svideo.music.R.style.MusicPanelStyle);
        if(mVisibleListener != null){
            setDismissListener(mVisibleListener);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (musicChooseView == null) {
            musicChooseView = new MusicChooseView(getContext());

            musicChooseView.setStreamDuration(mStreamDuration);
            musicChooseView.setMusicSelectListener(new MusicSelectListener() {
                @Override
                public void onMusicSelect(MusicFileBean musicFileBean) {
                    mCacheMusicBean = musicFileBean;
                }

                @Override
                public void onMusicPanelClose(boolean confirm) {
                    if (confirm && musicSelectListener != null) {
                        musicSelectListener.onMusicSelect(mCacheMusicBean);
                    }
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

    public void setVisibleListener(DialogVisibleListener listener){
        this.mVisibleListener = listener;
    }

    public void setStreamDuration(long duration) {
        this.mStreamDuration = duration;
        if (musicChooseView != null) {
            musicChooseView.setStreamDuration(duration);
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
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int)(getResources().getDisplayMetrics().density * 465);
        params.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        super.onStart();
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
