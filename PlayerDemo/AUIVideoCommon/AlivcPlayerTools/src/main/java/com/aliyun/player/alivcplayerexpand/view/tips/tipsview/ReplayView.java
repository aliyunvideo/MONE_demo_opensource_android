package com.aliyun.player.alivcplayerexpand.view.tips.tipsview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.alivcplayerexpand.view.tips.OnTipsViewBackClickListener;
import com.aliyun.player.alivcplayerexpand.util.ImageLoader;

import jp.wasabeef.blurry.Blurry;

/**
 * 重播提示对话框。播放结束的时候会显示这个界面
 */
public class ReplayView extends RelativeLayout {
    private int count_down = 3;
    //重播按钮
    private View mReplayBtn;
    //重播事件监听
    private OnReplayClickListener mOnReplayClickListener = null;
    private OnTipsViewBackClickListener mOnTipsViewBackClickListener = null;
    private ImageView bg;
    private TextView mTvReplay;

    public ReplayView(Context context) {
        super(context);
        init();
    }

    public ReplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReplayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                mTvReplay.setText(getResources().getString(R.string.alivc_replay) + (--count_down));
                if(count_down > 0){
                    countDown();
                }else{
                    invokeReplay();
                }
            }
        }
    };

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        View view = inflater.inflate(R.layout.alivc_dialog_replay, this);
        View view = inflater.inflate(R.layout.aui_video_flow_dialog_replay, this);

        //设置监听
//        mReplayBtn = view.findViewById(R.id.replay_layout);
//        view.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnReplayClickListener != null) {
//                    mOnReplayClickListener.onReplay();
//                }
//            }
//        });

        mTvReplay = view.findViewById(R.id.mTvReplay);
        mTvReplay.setText(getResources().getString(R.string.alivc_replay) + count_down);
        countDown();
        mTvReplay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                invokeReplay();
            }
        });
        bg = view.findViewById(R.id.video_play_complete_bg);

    }

    private void countDown(){
        mHandler.sendEmptyMessageDelayed(0,1000);
    }

    private void invokeReplay(){
        count_down = 3;
        mHandler.removeCallbacksAndMessages(null);
        if (mOnReplayClickListener != null) {
            mOnReplayClickListener.onReplay();
        }
    }

    public void setData(String coverUrl) {
        ImageLoader.loadAsBitmap(
                getContext(),
                bg,
                coverUrl,
                new ImageLoader.OnLoadBitmapCallback() {
                    @Override
                    public void onBitmapBack(Bitmap bitmap) {
                        if (bitmap != null) {
                            Blurry.with(getContext())
                                    .radius(10)
                                    .sampling(8)
                                    .from(bitmap)
                                    .into(bg);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    /**
     * 重播点击事件
     */
    public interface OnReplayClickListener {
        /**
         * 重播事件
         */
        void onReplay();
    }

    /**
     * 设置重播事件监听
     *
     * @param l 重播事件
     */
    public void setOnReplayClickListener(OnReplayClickListener l) {
        mOnReplayClickListener = l;
    }

    /**
     * 设置返回按钮监听
     */
    public void setOnBackClickListener(OnTipsViewBackClickListener listener) {
        this.mOnTipsViewBackClickListener = listener;
    }
}
