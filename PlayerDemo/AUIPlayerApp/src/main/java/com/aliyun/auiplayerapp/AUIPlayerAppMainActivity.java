package com.aliyun.auiplayerapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alivc.auiplayer.videoepisode.AUIEpisodePlayerActivity;
import com.alivc.player.videolist.auivideofunctionlist.AUIVideoFunctionListActivity;
import com.alivc.player.videolist.auivideostandradlist.AUIVideoStandardListActivity;
import com.aliyun.aio.avbaseui.AVBaseListActivity;
import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.auifullscreen.AUIFullScreenActivity;
import com.aliyun.video.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Route(path = "/player/MainActivity")
public class AUIPlayerAppMainActivity extends AVBaseListActivity {

    private static final int REQUEST_PERMISSION_STORAGE = 0x0001;

    private static final int INDEX_FEED_FLOW = 0;
    private static final int INDEX_VIDEO_LIST_FUNCTION = 1;
    private static final int INDEX_VIDEO_LIST_STANDARD = 2;
    private static final int INDEX_FULL_SCREEN = 3;
    private static final int INDEX_CUSTOM = 4;

    private static final int INDEX_VIDEO_LIST_Episode = 5;

    private ListModel mListModel;

    @Override
    public int getTitleResId() {
        return R.string.player_title;
    }

    @Override
    public boolean showBackBtn() {
        return !isTaskRoot();
    }

    @Override
    public List<ListModel> createListData() {
        List<ListModel> menu = new ArrayList<>();
        menu.add(new ListModel(INDEX_FEED_FLOW, R.drawable.ic_player_xinxi, getResources().getString(R.string.player_feed_flow), getResources().getString(R.string.player_feed_flow_msg)));
        menu.add(new ListModel(INDEX_VIDEO_LIST_FUNCTION, R.drawable.ic_player_chenjin, getResources().getString(R.string.player_video_list), getResources().getString(R.string.player_feed_flow_function_msg)));
        menu.add(new ListModel(INDEX_VIDEO_LIST_STANDARD, R.drawable.ic_player_quanping, getResources().getString(R.string.player_video_list), getResources().getString(R.string.player_feed_flow_standard_msg)));
        menu.add(new ListModel(INDEX_FULL_SCREEN, R.drawable.ic_player_zidingyi, getResources().getString(R.string.player_full_screen), getResources().getString(R.string.player_video_full_screen_msg)));
        //menu.add(new ListModel(INDEX_VIDEO_LIST_Episode, R.drawable.ic_player_quanping, getResources().getString(R.string.player_episode), getResources().getString(R.string.player_video_episode_msg)));
//        menu.add(new ListModel(INDEX_CUSTOM, R.drawable.ic_player_zidingyi, getResources().getString(R.string.player_custom), null));
        return menu;
    }

    @Override
    public void onListItemClick(ListModel model) {
        mListModel = model;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        } else {
            onModelItemClick(model);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;

        // 判断是否所有的权限都已经授予了
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                break;
            }
        }

        if (!isAllGranted) {
            // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
            AVToast.show(this, true, R.string.alivc_recorder_camera_permission_tip);
        } else {
            onModelItemClick(mListModel);
        }
    }

    private void onModelItemClick(ListModel model) {
        switch (model.index) {
            case INDEX_FEED_FLOW:
                Intent feedFlow = new Intent(this, MainActivity.class);
                startActivity(feedFlow);
                break;
            case INDEX_VIDEO_LIST_FUNCTION:
                Intent videoListIntent = new Intent(this, AUIVideoFunctionListActivity.class);
                startActivity(videoListIntent);
                break;
            case INDEX_VIDEO_LIST_STANDARD:
                Intent videoListIntent1 = new Intent(this, AUIVideoStandardListActivity.class);
                startActivity(videoListIntent1);
                break;
            case INDEX_FULL_SCREEN:
                Intent fullscreenIntent = new Intent(this, AUIFullScreenActivity.class);
                startActivity(fullscreenIntent);
                break;
            case INDEX_VIDEO_LIST_Episode:
                Intent videoListEpisodeIntent = new Intent(this, AUIEpisodePlayerActivity.class);
                startActivity(videoListEpisodeIntent);
                break;
            case INDEX_CUSTOM:
//                    Intent videoDetailIntent = new Intent(this, AUIVideoConfigActivity.class);
//                    startActivity(videoDetailIntent);
                break;
        }
    }

}
