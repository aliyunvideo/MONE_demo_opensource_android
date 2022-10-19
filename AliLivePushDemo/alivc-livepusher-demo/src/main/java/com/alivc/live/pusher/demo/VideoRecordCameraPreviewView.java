package com.alivc.live.pusher.demo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.alivc.live.pusher.*;
import com.aliyun.aio.utils.DensityUtil;

import java.lang.reflect.Field;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class VideoRecordCameraPreviewView extends LinearLayout {

    public static int viewWidth;

    public static int viewHeight;

    private static int statusBarHeight;

    private WindowManager windowManager;

    private WindowManager.LayoutParams mParams;

    private AlivcLivePusher mLivePusher = null;

    private float xInScreen;

    private float yInScreen;

    private float xDownInScreen;

    private float yDownInScreen;

    private float xInView;

    private float yInView;

    private Activity mActivity;

    private SurfaceView cameraPreview = null;


    public VideoRecordCameraPreviewView(Activity activity, Context context) {
        super(context);
        this.mActivity = activity;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.record_camera_preview, this);
        View view = findViewById(R.id.record_camera_layout);
        viewWidth = DensityUtil.dip2px(activity,90);
        viewHeight = DensityUtil.dip2px(activity,160);
        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        cameraPreview.getHolder().addCallback(mCallback);
    }

    public void setVisible(boolean visible) {
        if(visible) {
            cameraPreview.setVisibility(VISIBLE);
        } else {
            cameraPreview.setVisibility(INVISIBLE);
        }
    }

    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            if(mLivePusher != null ) {
                AlivcLivePushStats currentStatus = mLivePusher.getCurrentStatus();
                mLivePusher.startCamera(cameraPreview);
                int rotation = getDisplayRotation();
                mLivePusher.setScreenOrientation(rotation);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if(mLivePusher != null) {
                try {
                    mLivePusher.stopCamera();
                } catch (Exception e) {
                }
            }
        }
    };

    public void refreshPosition()
    {
        updateViewPosition();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void setParams(AlivcLivePusher pusher, WindowManager.LayoutParams params) {
        mParams = params;
        mLivePusher = pusher;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    public SurfaceView getSurfaceView()
    {
        return cameraPreview;
    }

    private int getDisplayRotation() {
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                break;
        }
        return 0;
    }

}
