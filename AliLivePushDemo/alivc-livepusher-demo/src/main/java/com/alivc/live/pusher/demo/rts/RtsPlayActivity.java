package com.alivc.live.pusher.demo.rts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alivc.live.pusher.demo.R;

import java.util.Locale;

/**
 * 播放界面
 */
public class RtsPlayActivity extends AppCompatActivity {

    private static final String TAG = "RtsPlayActivity";

    private RtsPlayer mRtsPlayer;
    private RadioGroup mRadioGroup;
    private TextView mTraceIdTextView;
    private ImageView mBackImageView;
    private SurfaceView mSurfaceView;
    private TextView mErrorTextView;
    private LinearLayout mMaskLinearLayout;
    private TextView mDemoteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_rts_play);

        mRtsPlayer = new RtsPlayer(this);
        String mRtsUrl = getIntent().getStringExtra("rts_url");

        initView();
        initListener();

        mRtsPlayer.setDataSource(mRtsUrl);
        mRtsPlayer.prepare();
        setRadioGroupEnabled(false);
    }

    private void initView() {
        mBackImageView = findViewById(R.id.iv_back);
        mErrorTextView = findViewById(R.id.tv_error);
        mRadioGroup = findViewById(R.id.radio_group);
        mDemoteTextView = findViewById(R.id.tv_demote);
        mMaskLinearLayout = findViewById(R.id.ll_mask);
        mSurfaceView = findViewById(R.id.surface_view);
        mTraceIdTextView = findViewById(R.id.tv_trace_id);

    }

    private void initListener() {
        mBackImageView.setOnClickListener(view -> finish());
        mRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId == R.id.rb_play) {
                mRtsPlayer.prepare();
                setRadioGroupEnabled(false);
            } else {
                mRtsPlayer.stop();
            }
        });

        //show traceId
        mTraceIdTextView.setOnClickListener(view -> showTraceIdInfo());

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                mRtsPlayer.setSurface(surfaceHolder);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                mRtsPlayer.surfaceChanged();
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                mRtsPlayer.setSurface(null);
            }
        });

        mRtsPlayer.setRtsPlayerListener(new RtsPlayer.RtsPlayerListener() {
            @Override
            public void onFirstFrameRender() {
                mErrorTextView.setText("");
                mMaskLinearLayout.setVisibility(View.GONE);
                setRadioGroupEnabled(true);
            }

            @Override
            public void onPlayerError(String msg, int code) {
                Log.e(TAG, "onPlayerError: " + msg + " --- " + code);
                setRadioGroupEnabled(true);
            }

            @Override
            public void onRtsMsg(String msg, int code, boolean showDemoted) {
                showRtsMsg(msg, code, showDemoted);
                setRadioGroupEnabled(true);
            }
        });
    }

    private void showTraceIdInfo() {
        RtsTraceIdInfoView rtsTraceIdInfoView = new RtsTraceIdInfoView(this);
        if (mRtsPlayer != null) {
            rtsTraceIdInfoView.setTraceId(mRtsPlayer.getTraceId());
            rtsTraceIdInfoView.setUrl(mRtsPlayer.getUrl());
        }
    }

    //Rts 事件通知
    private void showRtsMsg(String msg, int code, boolean showDemoted) {
        mRadioGroup.check(R.id.rb_stop);
        if (mMaskLinearLayout.getVisibility() == View.GONE) {
            mMaskLinearLayout.setVisibility(View.VISIBLE);
        }
        //降级文案显示、隐藏
        mDemoteTextView.setVisibility(showDemoted ? View.VISIBLE : View.GONE);
        mErrorTextView.setText(String.format(Locale.getDefault(), "%d,%s", code, msg));
    }

    private void setRadioGroupEnabled(boolean enable) {
        for (int i = 0; i < mRadioGroup.getChildCount(); i++) {
            mRadioGroup.getChildAt(i).setEnabled(enable);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRtsPlayer != null) {
            mRtsPlayer.stop();
            mRtsPlayer.release();
        }
    }
}