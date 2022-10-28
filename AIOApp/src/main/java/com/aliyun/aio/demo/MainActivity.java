package com.aliyun.aio.demo;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.GridView;

import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.aliyun.aio.avtheme.AVBaseThemeActivity;
import com.aliyun.aio.demo.ui.MainGridAdapter;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.android.arouter.launcher.ARouter;

public class MainActivity extends AVBaseThemeActivity {

    private GridView mGridView;
    private TextView mAliyunSdkPrivacy;
    private MainGridAdapter mMainGridAdapter;
    /**
     * 阿里云视频服务隐私权政策
     * 需要在启动的时候给用户弹窗，客户允许之后再注册SDK
     */
    private final static String PRIVACY_SDK = "https://terms.aliyun.com/legal-agreement/terms/suit_bu1_ali_cloud/suit_bu1_ali_cloud202112151438_20307.html?spm=a2c4g.11186623.0.0.10c739ebi5cK2M";

    private static final int INDEX_LIVE = 0;
    private static final int INDEX_UGSV = 1;
    private static final int INDEX_PLAYER = 2;
    private static final int INDEX_SOLUTION = 3;

    @Override
    protected int specifiedThemeMode() {
        return AppCompatDelegate.MODE_NIGHT_NO;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aio_activity_main);
        initView();
        initData();
    }

    private void initView() {
        mGridView = findViewById(R.id.aio_main_grid_view);
        mMainGridAdapter = new MainGridAdapter(this, mItemClickListener);
        mGridView.setAdapter(mMainGridAdapter);
        mAliyunSdkPrivacy = findViewById(R.id.aliyun_sdk_privacy);
        /**
         * 阿里云视频服务隐私权政策
         * 需要在启动的时候给用户弹窗，客户允许之后再注册SDK
         */
        mAliyunSdkPrivacy.setText(R.string.aio_aliyun_sdk_privacy);
        mAliyunSdkPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private MainGridAdapter.GridItemClickListener mItemClickListener = item -> {
        switch (item.index) {
            case INDEX_LIVE:
                jumpToLive();
                break;
            case INDEX_UGSV:
                jumpToUgsv();
                break;
            case INDEX_PLAYER:
                jumpToPlayer();
                break;
            case INDEX_SOLUTION:
                jumpToSolution();
                break;
        }
    };

    private void initData() {
        List<MainPageItem> list = new ArrayList<>();
        list.add(new MainPageItem(INDEX_LIVE, getResources().getString(R.string.aio_live_push),
                getResources().getString(R.string.aio_live_push_desc), R.drawable.ic_main_live, R.drawable.ic_main_grid_item_chakan));
        list.add(new MainPageItem(INDEX_UGSV, getResources().getString(R.string.aio_ugsv),
                getResources().getString(R.string.aio_ugsv_desc), R.drawable.ic_main_ugsv, R.drawable.ic_main_grid_item_chakan));
        list.add(new MainPageItem(INDEX_PLAYER, getResources().getString(R.string.aio_live_player),
                getResources().getString(R.string.aio_live_player_desc), R.drawable.ic_main_player, R.drawable.ic_main_grid_item_chakan));
        list.add(new MainPageItem(INDEX_SOLUTION, getResources().getString(R.string.aio_solution),
                getResources().getString(R.string.aio_solution_desc), R.drawable.ic_main_more, R.drawable.ic_main_grid_item_chakan2));
        mMainGridAdapter.setData(list);
    }

    private void jumpToUgsv() {
        ARouter.getInstance().build("/ugsv/MainActivity").navigation();
    }

    private void jumpToLive() {
        ARouter.getInstance().build("/live/MainActivity").navigation();
    }

    private void jumpToPlayer() {
        ARouter.getInstance().build("/player/MainActivity").navigation();
    }

    private void jumpToSolution() {
        //TODO:更多的跳转
        Toast.makeText(this, "精彩内容，即将开放", Toast.LENGTH_SHORT).show();
    }

}