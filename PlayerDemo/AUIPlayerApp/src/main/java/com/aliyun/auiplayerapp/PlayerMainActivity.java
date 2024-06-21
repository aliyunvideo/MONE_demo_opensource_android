package com.aliyun.auiplayerapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.alivc.auiplayer.videoepisode.AUIEpisodePlayerActivity;
import com.alivc.player.videolist.auivideofunctionlist.AUIVideoFunctionListActivity;
import com.alivc.player.videolist.auivideostandradlist.AUIVideoStandardListActivity;
import com.aliyun.auifullscreen.AUIFullScreenActivity;
import com.aliyun.auiplayerapp.utils.PermissionUtils;
import com.aliyun.auiplayerapp.view.AUIPlayerBaseListActivity;
import com.aliyun.video.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerMainActivity extends AUIPlayerBaseListActivity {

    private static final int REQUEST_PERMISSION_STORAGE = 0x0001;

    private static final int INDEX_FEED_FLOW = 0;
    private static final int INDEX_VIDEO_LIST_FUNCTION = 1;
    private static final int INDEX_VIDEO_LIST_STANDARD = 2;
    private static final int INDEX_VIDEO_LIST_SHORT = 3;
    private static final int INDEX_FULL_SCREEN = 4;
    private static final int INDEX_CUSTOM = 5;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemStatusBar();
    }

    @Override
    public List<ListModel> createListData() {
        List<ListModel> menu = new ArrayList<>();
        menu.add(new ListModel(INDEX_FEED_FLOW, R.drawable.ic_player_xinxi, getResources().getString(R.string.player_feed_flow), getResources().getString(R.string.player_feed_flow_msg)));
        menu.add(new ListModel(INDEX_VIDEO_LIST_FUNCTION, R.drawable.ic_player_chenjin, getResources().getString(R.string.player_video_list), getResources().getString(R.string.player_feed_flow_function_msg)));
        menu.add(new ListModel(INDEX_VIDEO_LIST_STANDARD, R.drawable.ic_player_quanping, getResources().getString(R.string.player_video_list), getResources().getString(R.string.player_feed_flow_standard_msg)));
        menu.add(new ListModel(INDEX_VIDEO_LIST_SHORT, R.drawable.ic_player_chenjin, getResources().getString(R.string.player_episode), getResources().getString(R.string.player_video_episode_msg)));
        menu.add(new ListModel(INDEX_FULL_SCREEN, R.drawable.ic_player_zidingyi, getResources().getString(R.string.player_full_screen), getResources().getString(R.string.player_video_full_screen_msg)));
//        menu.add(new ListModel(INDEX_CUSTOM, R.drawable.ic_player_zidingyi, getResources().getString(R.string.player_custom), null));
        return menu;
    }

    @Override
    public void onListItemClick(ListModel model) {
        mListModel = model;
        String[] per = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ?
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE} :
                new String[] {Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO};
        if (PermissionUtils.checkPermissionsGroup(this, per)) {
            ActivityCompat.requestPermissions((Activity) this, per, REQUEST_PERMISSION_STORAGE);
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
            Toast.makeText(this, getString(R.string.alivc_recorder_camera_permission_tip), Toast.LENGTH_SHORT).show();
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
            case INDEX_VIDEO_LIST_FUNCTION: {
                Intent videoListIntent = new Intent(this, AUIVideoFunctionListActivity.class);
                startActivity(videoListIntent);
                break;
            }
            case INDEX_VIDEO_LIST_STANDARD: {
                Intent videoListIntent = new Intent(this, AUIVideoStandardListActivity.class);
                startActivity(videoListIntent);
                break;
            }
            case INDEX_VIDEO_LIST_SHORT: {
                Intent videoListIntent = new Intent(this, AUIEpisodePlayerActivity.class);
                startActivity(videoListIntent);
                break;
            }
            case INDEX_FULL_SCREEN: {
                Intent fullscreenIntent = new Intent(this, AUIFullScreenActivity.class);
                startActivity(fullscreenIntent);
                break;
            }
            case INDEX_CUSTOM:
//                    Intent videoDetailIntent = new Intent(this, AUIVideoConfigActivity.class);
//                    startActivity(videoDetailIntent);
                break;
        }
    }

    private void hideSystemStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
