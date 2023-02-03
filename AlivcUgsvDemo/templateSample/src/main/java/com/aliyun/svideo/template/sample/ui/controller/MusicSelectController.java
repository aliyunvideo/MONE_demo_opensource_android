package com.aliyun.svideo.template.sample.ui.controller;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.aliyun.aio.avbaseui.AVBaseDialogFragment;
import com.aliyun.svideo.music.music.MusicFileBean;
import com.aliyun.svideo.template.sample.ui.callback.MusicSelectListener;
import com.aliyun.svideo.template.sample.ui.view.MusicSelectPanel;
import com.aliyun.svideosdk.template.AliyunAETemplateIEditor;

/**
 * @author geekeraven
 */
public class MusicSelectController implements AVBaseDialogFragment.DialogVisibleListener, MusicSelectListener {

    private MusicSelectPanel mMusicSelectPanel;
    private AliyunAETemplateIEditor mAliyunIEditor;
    private final String TAG = "MusicSelectController";
    private MusicInnerListener mInnerListener;

    public MusicSelectController(AliyunAETemplateIEditor aliyunIEditor, MusicInnerListener listener) {
        mMusicSelectPanel = new MusicSelectPanel();
        mMusicSelectPanel.setVisibleListener(this);
        mAliyunIEditor = aliyunIEditor;
        mMusicSelectPanel.setMusicSelectListener(this);
        mInnerListener = listener;
    }

    public void showMusicSelectPanel(FragmentManager manager) {
        Fragment fragment = manager.findFragmentByTag(TAG);
        if(fragment != null) {
            manager.beginTransaction().remove(fragment).commitNowAllowingStateLoss();
        }
        mMusicSelectPanel.setStreamDuration((long)mAliyunIEditor.getTemplate().getDuration() * 1000);
        mMusicSelectPanel.show(manager, TAG);
    }

    @Override
    public void onDialogDismiss() {
        if(mInnerListener != null){
            mInnerListener.onDialogDismiss();
        }
    }

    @Override
    public void onDialogShow() {
        if(mInnerListener != null){
            mInnerListener.onDialogShow();
        }
    }

    @Override
    public void onMusicSelect(MusicFileBean musicFileBean) {
        if(mInnerListener != null){
            mInnerListener.onMusicSelect(musicFileBean);
        }
    }

    @Override
    public void onMusicPanelClose(boolean confirm) {
    }

    public interface MusicInnerListener{
        void onMusicSelect(MusicFileBean musicFileBean);
        void onDialogDismiss();
        void onDialogShow();
    }
}
