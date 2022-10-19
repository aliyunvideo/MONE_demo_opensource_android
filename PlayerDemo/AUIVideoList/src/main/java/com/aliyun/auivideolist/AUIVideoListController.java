package com.aliyun.auivideolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Surface;

import com.aliyun.auivideolist.bean.ListVideoBean;
import com.aliyun.auivideolist.player.AliPlayerManager;
import com.aliyun.auivideolist.player.AliPlayerPreload;
import com.aliyun.auivideolist.utils.GlobalSettings;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;

import java.io.File;
import java.util.LinkedList;


public class AUIVideoListController {

    private static final AUIVideoListModel M_VIDEO_LIST_MODEL = new AUIVideoListModel();
    private static final String AUI_VIDEO_LIST_GESTURE = "aui_video_list_gesture_show";
    private AliPlayerManager mAliPlayerManager;
    private AliPlayerPreload mAliPlayerPreload;
    private boolean mIsPlay = true;

    private final Context mContext;
    private int mOldPosition = 0;
    private boolean mIsFirstPlay = true;
    private final SharedPreferences mSharedPreferences;

    public AUIVideoListController(Context context){
        this.mContext = context;

        mSharedPreferences = context.getSharedPreferences("aui_video_list",Context.MODE_PRIVATE);
        initDir();
        initPlayer();
    }

    private void initDir(){
        GlobalSettings.CACHE_DIR = mContext.getExternalCacheDir().getAbsolutePath() + File.separator + "Preload";
        M_VIDEO_LIST_MODEL.loadData(mContext.getApplicationContext());
    }

    private void initPlayer(){
        mAliPlayerPreload = AliPlayerPreload.getInstance();
        mAliPlayerManager = new AliPlayerManager(mContext);

        mAliPlayerPreload.setUrls(M_VIDEO_LIST_MODEL.getData());
        mAliPlayerManager.setUrls(M_VIDEO_LIST_MODEL.getData());

        mAliPlayerManager.setOnPlayerListener(new AliPlayerManager.OnPlayerListener() {

            @Override
            public void onInfo(InfoBean infoBean) {
                if(mContext instanceof AUIVideoListActivity){
                    if(infoBean.getCode() == InfoCode.CurrentPosition){
                        ((AUIVideoListActivity)mContext).updateCurrentPosition(infoBean.getExtraValue());
                    }
                }
            }
            @Override
            public void onRenderingStart(long duration) {
                if(mContext instanceof AUIVideoListActivity){
                    ((AUIVideoListActivity)mContext).onVideoFrameShow(duration);
                    ((AUIVideoListActivity)mContext).hideCoverView();
                }
                mIsPlay = true;
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                if(mContext instanceof AUIVideoListActivity){
                    ((AUIVideoListActivity)mContext).showError(errorInfo);
                }
            }

            @Override
            public void onPlayerStateChanged(int newState) {
                if(mContext instanceof AUIVideoListActivity){
                    ((AUIVideoListActivity)mContext).showPlayIcon(newState == IPlayer.paused);
                }
            }
        });
    }

    public void initSP(){
        boolean mHasShowGesture = mSharedPreferences.getBoolean(AUI_VIDEO_LIST_GESTURE, false);
        if(!mHasShowGesture){
            ((AUIVideoListActivity)mContext).showInteractiveGuidance();
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.putBoolean(AUI_VIDEO_LIST_GESTURE,true);
            edit.apply();
        }
    }

    public LinkedList<ListVideoBean> getData(){
        return M_VIDEO_LIST_MODEL.getData();
    }

    public void setRenderView(Surface surface) {
        mAliPlayerManager.setSurface(surface);
    }

    public void surfacChanged() {
        mAliPlayerManager.surfaceChanged();
    }

    public void start(int position) {
        if(position == mOldPosition && !mIsFirstPlay){
            return ;
        }
        int flag = position - mOldPosition;
        if (Math.abs(flag) == 1) {
            if (flag < 0) {
                mAliPlayerManager.moveToPre();
            } else {
                mAliPlayerManager.moveToNext();
            }
        } else {
            mAliPlayerManager.moveTo(position);
            mIsFirstPlay = false;
        }
        mAliPlayerPreload.moveTo(position);
        mOldPosition = position;
        if(position == M_VIDEO_LIST_MODEL.getData().size() - 1){
            ((AUIVideoListActivity)mContext).loadMore();
        }
    }

    public void destroy() {
        mAliPlayerManager.release();
    }

    public void startPlay(int position) {
        LinkedList<ListVideoBean> data = M_VIDEO_LIST_MODEL.getData();
        ListVideoBean listVideoBean = data.get(position);
        mAliPlayerManager.startPlay(listVideoBean.getUrl());
    }

    public void changePlayState(){
        if(mIsPlay){
            mAliPlayerManager.pause();
        }else{
            mAliPlayerManager.start();
        }
        mIsPlay = !mIsPlay;
    }
}
