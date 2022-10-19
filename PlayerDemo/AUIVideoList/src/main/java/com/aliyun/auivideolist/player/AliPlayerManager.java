package com.aliyun.auivideolist.player;

import android.content.Context;
import android.view.Surface;

import com.aliyun.auivideolist.bean.ListVideoBean;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;

import java.util.ArrayDeque;
import java.util.LinkedList;

/**
 * movToNext:
 *      Pre-Prepare --> current URL and next URL
 * moveToPre and MoveTo
 *      prepare current URL and play, prepare next URL
 */
public class AliPlayerManager {

    private final ArrayDeque<AliPlayer> mPlayerPool = new ArrayDeque<>(2);
    private int mCurrentPlayingIndex;
    private AliPlayer mCurrentPlayer;
    private LinkedList<ListVideoBean> mUrlLinkedList;
    private int mUrlLinkedListSize = 0;
    private OnPlayerListener mListener;
    private final UrlSource mUrlSource;
    private boolean mIsPlaying = false;

    public AliPlayerManager(Context context){
        mUrlSource = new UrlSource();
        AliPlayer aliPlayer1 = AliPlayerFactory.createAliPlayer(context);
        AliPlayer aliPlayer2 = AliPlayerFactory.createAliPlayer(context);

        aliPlayer1.setFastStart(true);
        PlayerConfig config1 = aliPlayer1.getConfig();
        config1.mClearFrameWhenStop = true;
        aliPlayer1.setConfig(config1);

        aliPlayer2.setFastStart(true);
        PlayerConfig config2 = aliPlayer1.getConfig();
        config2.mClearFrameWhenStop = true;
        aliPlayer2.setConfig(config2);

        mPlayerPool.add(aliPlayer1);
        mPlayerPool.add(aliPlayer2);
    }

    private void initListener(){
        mCurrentPlayer.setOnPreparedListener(() -> mCurrentPlayer.start());

        mCurrentPlayer.setOnRenderingStartListener(() -> {
            mIsPlaying = true;
            if(mListener != null){
                mListener.onRenderingStart(mCurrentPlayer.getDuration());
            }
        });

        mCurrentPlayer.setOnInfoListener(infoBean -> {
            if(mListener != null){
                mListener.onInfo(infoBean);
            }
        });

        mCurrentPlayer.setOnErrorListener(errorInfo -> {
            if(mListener != null){
                mListener.onError(errorInfo);
            }
        });

        mCurrentPlayer.setOnStateChangedListener(newState -> {
            if(mListener != null){
                mListener.onPlayerStateChanged(newState);
            }
        });
    }

    /**
     *  Top Player prepare and start
     *  Bottom Player prepare
     */
    public void moveTo(int index){
        mCurrentPlayer = mPlayerPool.getFirst();

        if(index < 0 || index >= mUrlLinkedList.size()){
            return ;
        }

        ListVideoBean listVideoBean = mUrlLinkedList.get(index);

        initListener();
        prepare(mCurrentPlayer,listVideoBean.getUrl());

        //pre prepare next
        if(index + 1 < mUrlLinkedList.size()){
            AliPlayer last = mPlayerPool.getLast();
            prepare(last,mUrlLinkedList.get(index + 1).getUrl());
        }
        mCurrentPlayingIndex = index;
    }

    /**
     * 1.currentPlayer(Player A) pause
     * 2.exchange Player Dequeue
     * 3.currentPlayer(Player B) start
     * 5.currentPlayer(Player A) prepare next.next url after Player B start
     */
    public void moveToNext(){
        mCurrentPlayingIndex++;
        mCurrentPlayer.stop();
        exchange();
        mCurrentPlayer.start();

        if(mCurrentPlayingIndex >= 0 && mCurrentPlayingIndex + 1 < mUrlLinkedListSize){
            AliPlayer last = mPlayerPool.getLast();
            prepare(last,mUrlLinkedList.get(mCurrentPlayingIndex + 1).getUrl());
        }
    }


    public void moveToPre(){
        mCurrentPlayer.stop();
        moveTo(--mCurrentPlayingIndex);
    }

    /**
     * Top player change to bottom
     * Bottom player change to top
     * mCurrentPlayer always pointer to Top Player
     */
    public void exchange(){
        AliPlayer pop = mPlayerPool.pop();
        mPlayerPool.add(pop);
        mCurrentPlayer = mPlayerPool.getFirst();
        initListener();
    }

    private void prepare(AliPlayer aliPlayer, String url){
        mIsPlaying = false;
        mUrlSource.setUri(url);
        aliPlayer.setDataSource(mUrlSource);
        aliPlayer.prepare();
    }

    public void setSurface(Surface mSurface) {
        mCurrentPlayer.setSurface(mSurface);
    }

    public void surfaceChanged() {
        mCurrentPlayer.surfaceChanged();
    }

    public void release() {
        for (AliPlayer aliPlayer : mPlayerPool) {
            aliPlayer.release();
        }
    }

    public void setUrls(LinkedList<ListVideoBean> urlLinkedList) {
        this.mUrlLinkedList = urlLinkedList;
        this.mUrlLinkedListSize = mUrlLinkedList.size();
    }

    public void startPlay(String url) {
        mCurrentPlayer.pause();
        initListener();
        prepare(mCurrentPlayer,url);
    }

    public void pause(){
        if(mIsPlaying){
            mCurrentPlayer.pause();
        }
    }

    public void start(){
        mCurrentPlayer.start();
    }

    public interface OnPlayerListener{
        void onInfo(InfoBean infoBean);
        void onRenderingStart(long duration);
        void onError(ErrorInfo errorInfo);
        void onPlayerStateChanged(int newState);
    }

    public void setOnPlayerListener(OnPlayerListener listener){
        this.mListener = listener;
    }
}
