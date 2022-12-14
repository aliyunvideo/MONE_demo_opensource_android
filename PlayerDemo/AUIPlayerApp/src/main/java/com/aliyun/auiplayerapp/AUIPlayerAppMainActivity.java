package com.aliyun.auiplayerapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aliyun.aio.avbaseui.AVBaseListActivity;
import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.auifullscreen.AUIFullScreenActivity;
import com.aliyun.auivideolist.AUIVideoListActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.aliyun.video.MainActivity;

@Route(path = "/player/MainActivity")
public class AUIPlayerAppMainActivity extends AVBaseListActivity {

    private static final int REQUEST_PERMISSION_STORAGE = 0x0001;

    private static final int INDEX_FEED_FLOW = 0;
    private static final int INDEX_VIDEO_LIST = 1;
    private static final int INDEX_FULL_SCREEN = 2;
    private static final int INDEX_CUSTOM = 3;

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
        menu.add(new ListModel(INDEX_VIDEO_LIST, R.drawable.ic_player_chenjin, getResources().getString(R.string.player_video_list), getResources().getString(R.string.player_feed_flow_msg)));
        menu.add(new ListModel(INDEX_FULL_SCREEN, R.drawable.ic_player_quanping, getResources().getString(R.string.player_full_screen), getResources().getString(R.string.player_feed_flow_msg)));
//        menu.add(new ListModel(INDEX_CUSTOM, R.drawable.ic_player_zidingyi, getResources().getString(R.string.player_custom), null));
        return menu;
    }

    @Override
    public void onListItemClick(ListModel model) {
        mListModel = model;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
        }else{
            onModelItemClick(model);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;

        // ?????????????????????????????????????????????
        for (int grant : grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                break;
            }
        }

        if (!isAllGranted) {
            // ????????????????????????????????????????????????, ???????????????????????????????????????????????????????????????
            AVToast.show(this,true,R.string.alivc_recorder_camera_permission_tip);
        }else{
            onModelItemClick(mListModel);
        }
    }

    private void onModelItemClick(ListModel model){
        switch (model.index){
            case INDEX_FEED_FLOW:
                Intent feedFlow = new Intent(this, MainActivity.class);
                startActivity(feedFlow);
                break;
            case INDEX_VIDEO_LIST:
                Intent videoListIntent = new Intent(this, AUIVideoListActivity.class);
                startActivity(videoListIntent);
                break;
            case INDEX_FULL_SCREEN:
                Intent fullscreenIntent = new Intent(this, AUIFullScreenActivity.class);
                startActivity(fullscreenIntent);
                break;
            case INDEX_CUSTOM:
//                    Intent videoDetailIntent = new Intent(this, AUIVideoConfigActivity.class);
//                    startActivity(videoDetailIntent);
                break;
        }
    }

}
