package com.aliyun.alivcsolution;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.aliyun.aio.avbaseui.AVBaseListActivity;
import com.aliyun.alivcsolution.setting.MoreSettingActivity;
import com.aliyun.svideo.base.utils.FastClickUtil;
import com.aliyun.svideo.mixrecorder.activity.AUIVideoMixRecordActivity;
import com.aliyun.svideo.template.sample.ui.TemplateListActivity;
import com.aliyun.ugsv.common.utils.PermissionUtils;
import com.aliyun.ugsv.common.utils.ToastUtils;
import com.aliyun.svideo.crop.util.AUICropHelper;
import com.aliyun.svideo.editor.AUIVideoEditor;
import com.aliyun.svideo.recorder.AUIVideoRecorderEntrance;
import com.zhihu.matisse.AVMatisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.SelectionCreator;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/ugsv/MainActivity")
public class MainActivity extends AVBaseListActivity {

    private static final int EDITOR_REQUEST_CODE_CHOOSE = 102;
    private static final int CROP_REQUEST_CODE_CHOOSE = 103;

    private static final int MIX_RECORDER_REQUEST_CODE_CHOOSE = 104;
    private static final int EDITOR_PERMISSION_REQUEST_CODE = 1001;
    private static final int CROP_PERMISSION_REQUEST_CODE = 1002;

    private static final int MIX_RECORDER_PERMISSION_REQUEST_CODE = 1003;

    private static final int INDEX_RECORDER = 0;
    private static final int INDEX_VIDEO_TRANSCODE = 1;
    private static final int INDEX_EDITOR = 2;
    private static final int INDEX_TEMPLATE = 3;
    private static final int INDEX_MORE = 4;

    private static final int INDEX_MIX_RECORDER = 5;

    private boolean showMoreSetting() {
        return BuildConfig.APK_TYPE == 0;
    }

    @Override
    public int getTitleResId() {
        return R.string.solution_svideo;
    }

    @Override
    public boolean showBackBtn() {
        return !isTaskRoot();
    }

    @Override
    public List<ListModel> createListData() {
        List<ListModel> menu = new ArrayList<>();
        menu.add(new ListModel(INDEX_RECORDER, R.drawable.ic_ugsv_recorder, getResources().getString(R.string.solution_recorder), null));
        menu.add(new ListModel(INDEX_MIX_RECORDER, R.drawable.ic_ugsv_mix_recorder, getResources().getString(R.string.solution_mix_recorder), null));
        menu.add(new ListModel(INDEX_VIDEO_TRANSCODE, R.drawable.ic_ugsv_videocrop, getResources().getString(R.string.solution_crop), null));
        menu.add(new ListModel(INDEX_EDITOR, R.drawable.ic_ugsv_editor, getResources().getString(R.string.solution_edit), null));
        menu.add(new ListModel(INDEX_TEMPLATE, R.drawable.ic_ugsv_videocrop, getResources().getString(R.string.solution_template), null));
        if (showMoreSetting()) {
            menu.add(new ListModel(INDEX_MORE, R.drawable.ic_ugsv_more, getResources().getString(R.string.ugsv_setting_more), null));
        }
        return menu;
    }

    String[] per = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ?
            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE} :
            new String[] {Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO};
    @Override
    public void onListItemClick(ListModel model) {
        if (FastClickUtil.isFastClick()) {
            return;
        }
        switch (model.index) {
        case INDEX_RECORDER:
            AUIVideoRecorderEntrance.startRecord(MainActivity.this);

            break;
        case INDEX_EDITOR:
            // 新视频编辑

            if (!PermissionUtils.checkPermissionsGroup(this, per)) {
                ToastUtils.show(this, PermissionUtils.NO_PERMISSION_TIP[4]);
                PermissionUtils.requestPermissions(this, per, EDITOR_PERMISSION_REQUEST_CODE);
                return;
            }
            AVMatisse.from(MainActivity.this)
                    .choose(MimeType.ofAll(), false)
                    .countable(true)
                    .maxSelectable(20)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .showPreview(false) // Default is `true`
                    .forResult(EDITOR_REQUEST_CODE_CHOOSE);
            break;
        case INDEX_VIDEO_TRANSCODE:
            // 视频裁剪
            if (!PermissionUtils.checkPermissionsGroup(this, per)) {
                ToastUtils.show(this, PermissionUtils.NO_PERMISSION_TIP[4]);
                PermissionUtils.requestPermissions(this, per, CROP_PERMISSION_REQUEST_CODE);
                return;
            }
            AVMatisse.from(MainActivity.this)
                    .choose(MimeType.ofVideo(), true)
                    .showSingleMediaType(true)
                    .countable(true)
                    .maxSelectable(1)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .showPreview(false) // Default is `true`
                    .forResult(CROP_REQUEST_CODE_CHOOSE);
            break;
            case INDEX_TEMPLATE:
//                TemplatePreviewEditActivity.start(MainActivity.this);
                TemplateListActivity.start(MainActivity.this);
                break;
            case INDEX_MORE:
                Intent moreIntent = new Intent(MainActivity.this, MoreSettingActivity.class);
                startActivity(moreIntent);
                break;
            case INDEX_MIX_RECORDER:
                if (!PermissionUtils.checkPermissionsGroup(this, per)) {
                    ToastUtils.show(this, PermissionUtils.NO_PERMISSION_TIP[4]);
                    PermissionUtils.requestPermissions(this, per, MIX_RECORDER_PERMISSION_REQUEST_CODE);
                    return;
                }
                AVMatisse.from(MainActivity.this)
                    .choose(MimeType.ofVideo(), false)
                    .showSingleMediaType(true)
                    .countable(true)
                    .maxSelectable(1)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .showPreview(false) // Default is `true`
                    .forResult(MIX_RECORDER_REQUEST_CODE_CHOOSE);
                break;
        default:
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EDITOR_PERMISSION_REQUEST_CODE || requestCode == CROP_PERMISSION_REQUEST_CODE || requestCode == MIX_RECORDER_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                Set<MimeType> mimeType = null;
                int resultCode = -1;
                int maxSelect = 1;
                if(requestCode == MIX_RECORDER_PERMISSION_REQUEST_CODE){
                    mimeType = MimeType.ofVideo();
                    resultCode = MIX_RECORDER_REQUEST_CODE_CHOOSE;
                    maxSelect = 1;
                }else if(requestCode ==  EDITOR_PERMISSION_REQUEST_CODE){
                    mimeType = MimeType.ofAll();
                    resultCode = EDITOR_REQUEST_CODE_CHOOSE;
                    maxSelect = 20;
                }else if(requestCode == CROP_PERMISSION_REQUEST_CODE){
                    mimeType = MimeType.ofVideo();
                    resultCode = CROP_REQUEST_CODE_CHOOSE;
                    maxSelect = 1;
                }
                // 如果所有的权限都授予了
                SelectionCreator creator = AVMatisse.from(MainActivity.this)
                        .choose(mimeType, false)
                        .countable(true)
                        .maxSelectable(maxSelect)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .showPreview(false);
                if(requestCode == MIX_RECORDER_PERMISSION_REQUEST_CODE){
                    creator.showSingleMediaType(true);
                }
                creator.forResult(resultCode);
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                showPermissionDialog();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (EDITOR_REQUEST_CODE_CHOOSE == requestCode && resultCode == RESULT_OK) {
            AUIVideoEditor.startEditor(this,AVMatisse.obtainPathResult(data));
        } else if (CROP_REQUEST_CODE_CHOOSE == requestCode && resultCode == RESULT_OK) {
            AUICropHelper.startVideoCrop(this,AVMatisse.obtainPathResult(data));
        } else if(MIX_RECORDER_REQUEST_CODE_CHOOSE == requestCode && resultCode == RESULT_OK){
            AUIVideoMixRecordActivity.startMixRecord(this, AVMatisse.obtainPathResult(data));
        }
    }

    //系统授权设置的弹框
    AlertDialog openAppDetDialog = null;
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.ugc_app_name) + getResources().getString(R.string.alivc_recorder_record_dialog_permission_remind));
        builder.setPositiveButton(getResources().getString(R.string.alivc_record_request_permission_positive_btn_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(getResources().getString(R.string.alivc_recorder_record_dialog_not_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });
        if (null == openAppDetDialog) {
            openAppDetDialog = builder.create();
        }
        if (null != openAppDetDialog && !openAppDetDialog.isShowing()) {
            openAppDetDialog.show();
        }
    }
}
