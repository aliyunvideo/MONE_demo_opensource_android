/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.alivcsolution;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * @author Mulberry
 */
public class SplashActivity extends Activity {
    /**
     * 动画时间 2000ms
     */
    private  final int ANIMATOR_DURATION = 2000;

    /**
     * 动画样式-- 透明度动画
     */
    private  final String ANIMATOR_STYLE = "alpha";

    /**
     * 动画起始值
     */
    private  final float ANIMATOR_VALUE_START = 0f;

    /**
     * 动画结束值
     */
    private  final float ANIMATOR_VALUE_END = 1f;
    private ObjectAnimator alphaAnimIn;
    private LinearLayout splashView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo 排查错误
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (strangeError()) {
            return;
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_spalash);
        splashView = findViewById(R.id.splash_view);

        alphaAnimIn = ObjectAnimator.ofFloat(splashView, ANIMATOR_STYLE, ANIMATOR_VALUE_START, ANIMATOR_VALUE_END);

        alphaAnimIn.setDuration(ANIMATOR_DURATION);

        alphaAnimIn.start();
        alphaAnimIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setJumpToMain();
            }
        });
    }

    /**
     * 处理手机在第一次安装时, 退后台再次启动app, 会重新走splash页面
     * 目前只针对已知存在该问题的机型做处理
     * @return
     */
    private boolean strangeError() {
        if (isStrangeErrorBrand()) {
            Intent intent = getIntent();
            if (!isTaskRoot()
                    && intent != null
                    && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                    && intent.getAction() != null
                    && intent.getAction().equals(Intent.ACTION_MAIN)) {
                finish();
                return true;
            }
        }
        return false;
    }

    /**
     * 判断手机品牌
     * GIONEE: 金立
     * OPPO: oppo
     * samsung: 三星
     * LeEco: 乐视
     * @return true:Oppo ,  flase:其它
     */
    private boolean isStrangeErrorBrand() {
        return "GIONEE".equalsIgnoreCase(Build.BRAND)
               || "OPPO".equalsIgnoreCase(Build.BRAND)
               || "samsung".equalsIgnoreCase(Build.BRAND)
               || "Meitu".equalsIgnoreCase(Build.BRAND)
               || "LeEco".equalsIgnoreCase(Build.BRAND);
    }

    private void setJumpToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (alphaAnimIn != null) {
            alphaAnimIn.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alphaAnimIn != null) {
            alphaAnimIn.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (splashView != null) {
            splashView.clearAnimation();
        }
        if (alphaAnimIn != null) {
            alphaAnimIn.cancel();
            alphaAnimIn.removeAllListeners();
            alphaAnimIn = null;
        }
    }
}
