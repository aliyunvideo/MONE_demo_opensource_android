package com.alivc.live.pusher.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alivc.live.annotations.AlivcLiveMode;
import com.alivc.live.pusher.AlivcLiveBase;
import com.alivc.live.pusher.demo.test.PushDemoTestConstants;
import com.aliyun.interactive_common.InteractAppInfoActivity;
import com.aliyun.interactive_live.InteractLiveInputActivity;
import com.aliyun.interactive_pk.PKLiveInputActivity;
import com.alivc.live.commonutils.ContextUtils;
import com.alivc.live.commonutils.FastClickUtil;
import com.alivc.live.commonbiz.SharedPreferenceUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout mLivePushLayout;
    private LinearLayout mLivePullCommonPullLayout;
    private LinearLayout mLivePullRtcLayout;
    private LinearLayout mLiveInteractLayout;
    private LinearLayout mPKLiveInteractLayout;
    private TextView mVersion;//推流sdk版本号
    private String mPushUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        check();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.push_activity_main);
        ARouter.init(getApplication());
        initView();
        if (!permissionCheck()) {
            if (Build.VERSION.SDK_INT >= 23) {
                ActivityCompat.requestPermissions(this, permissionManifest, PERMISSION_REQUEST_CODE);
            } else {
                showNoPermissionTip(getString(noPermissionTip[mNoPermissionIndex]));
                finish();
            }
        }
        Intent intent = getIntent();
        mPushUrl = intent.getStringExtra("pushUrl");
    }

    private void check(){
        if (!this.isTaskRoot()) { // 当前类不是该Task的根部，那么之前启动
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) { // 当前类是从桌面启动的
                    finish(); // finish掉该类，直接打开该Task中现存的Activity
                }
            }
        }
    }

    private void initView() {
        mLivePushLayout = (LinearLayout) findViewById(R.id.push_enter_layout);
        mLivePushLayout.setOnClickListener(this);
        mLivePullCommonPullLayout = (LinearLayout) findViewById(R.id.pull_common_enter_layout);
        mLivePullCommonPullLayout.setOnClickListener(this);
        mLivePullRtcLayout = (LinearLayout) findViewById(R.id.pull_enter_layout);
        mLivePullRtcLayout.setOnClickListener(this);
        mLiveInteractLayout = (LinearLayout) findViewById(R.id.pull_interact_layout);
        mPKLiveInteractLayout = (LinearLayout) findViewById(R.id.pull_pk_layout);
        mLiveInteractLayout.setOnClickListener(this);
        mPKLiveInteractLayout.setOnClickListener(this);
        mVersion = (TextView) findViewById(R.id.push_version);
        mVersion.setText(getString(R.string.version_desc) + AlivcLiveBase.getSDKVersion());

        mLiveInteractLayout.setVisibility(AlivcLiveBase.isSupportLiveMode(AlivcLiveMode.AlivcLiveInteractiveMode) ? View.VISIBLE : View.GONE);
        mPKLiveInteractLayout.setVisibility(AlivcLiveBase.isSupportLiveMode(AlivcLiveMode.AlivcLiveInteractiveMode) ? View.VISIBLE : View.GONE);

    }

    private int mNoPermissionIndex = 0;
    private final int PERMISSION_REQUEST_CODE = 1;
    // todo keria: 动态权限申请这块需要梳理一下~~~
    private final String[] permissionManifest = {
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.BLUETOOTH_CONNECT,
    };

    private final int[] noPermissionTip = {
            R.string.no_camera_permission,
            R.string.no_record_bluetooth_permission,
            R.string.no_record_audio_permission,
            R.string.no_read_phone_state_permission,
            R.string.no_write_external_storage_permission,
            R.string.no_read_external_storage_permission,
            R.string.no_internet_permission,
            R.string.no_record_bluetooth_permission,
    };

    @SuppressLint("WrongConstant")
    private boolean permissionCheck() {
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        String permission;
        for (int i = 0; i < permissionManifest.length; i++) {
            permission = permissionManifest[i];
            mNoPermissionIndex = i;
            if (PermissionChecker.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionCheck = PackageManager.PERMISSION_DENIED;
            }
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onClick(View v) {
        Intent intent;
        int id = v.getId();
        if (id == R.id.push_enter_layout) {
            if (FastClickUtil.isFastClick()) {
                return;//点击间隔 至少1秒
            }
            intent = new Intent(MainActivity.this, PushConfigActivity.class);
            if (!TextUtils.isEmpty(mPushUrl)) {
                intent.putExtra("pushUrl", mPushUrl);
            }
            startActivity(intent);
        } else if (id == R.id.pull_common_enter_layout) {
            intent = new Intent(MainActivity.this, VideoRecordConfigActivity.class);
            if (!TextUtils.isEmpty(mPushUrl)) {
                intent.putExtra("pushUrl", mPushUrl);
            }
            startActivity(intent);
        } else if (id == R.id.pull_enter_layout) {
            intent = new Intent(MainActivity.this, PlayerActivity.class);
            startActivity(intent);
        } else if(id == R.id.pull_interact_layout){
            if (checkInteractiveAPPInfoIfNeed()) {
                intent = new Intent(MainActivity.this, InteractAppInfoActivity.class);
            }else{
                intent = new Intent(MainActivity.this, InteractLiveInputActivity.class);
            }
            startActivity(intent);
        } else if(id == R.id.pull_pk_layout){
            if (checkInteractiveAPPInfoIfNeed()) {
                intent = new Intent(MainActivity.this, InteractAppInfoActivity.class);
                intent.putExtra(InteractAppInfoActivity.INTENT_FROM_PK,true);
            }else{
                intent = new Intent(MainActivity.this, PKLiveInputActivity.class);
            }
            startActivity(intent);
        }
    }

    private void showNoPermissionTip(String tip) {
        Toast.makeText(this, tip, Toast.LENGTH_LONG).show();
    }

    private boolean checkInteractiveAPPInfoIfNeed() {
        Context context = ContextUtils.getApplicationContext();

        // 如果sp里面appInfo都有，返回false，不需要填写
        if (!(TextUtils.isEmpty(SharedPreferenceUtils.getAppId(context))
                || TextUtils.isEmpty(SharedPreferenceUtils.getAppKey(context))
                || TextUtils.isEmpty(SharedPreferenceUtils.getPlayDomain(context)))) {
            return false;
        }

        // 如果开发调试用的appInfo都有，返回false，不需要填写
        String testAppID = PushDemoTestConstants.getTestInteractiveAppID();
        String testAppKey = PushDemoTestConstants.getTestInteractiveAppKey();
        String testPlayDomain = PushDemoTestConstants.getTestInteractivePlayDomain();
        if (!(PushDemoTestConstants.checkIsPlaceholder(testAppID)
                || PushDemoTestConstants.checkIsPlaceholder(testAppKey)
                || PushDemoTestConstants.checkIsPlaceholder(testPlayDomain))) {
            // 将开发调试用的appInfo写入到sp里面
            SharedPreferenceUtils.setAppId(context, testAppID);
            SharedPreferenceUtils.setAppKey(context, testAppKey);
            SharedPreferenceUtils.setPlayDomain(context, testPlayDomain);
            return false;
        }

        return true;
    }

}
