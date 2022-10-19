package com.aliyun.alivcsolution.setting;

import android.content.Intent;
import android.widget.Toast;

import com.aliyun.aio.avbaseui.AVBaseListActivity;
import com.aliyun.alivcsolution.R;
import com.aliyun.svideo.base.utils.FastClickUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 更多设置页
 */
public class MoreSettingActivity extends AVBaseListActivity {

    private static final int INDEX_RECORD = 0;
    private static final int INDEX_CROP = 1;
    private static final int INDEX_EDIT = 2;

    @Override
    public int getTitleResId() {
        return R.string.ugsv_setting_more;
    }

    @Override
    public boolean showBackBtn() {
        return !isTaskRoot();
    }

    @Override
    public List<ListModel> createListData() {
        List<ListModel> menu = new ArrayList<>();
        String desc = getResources().getString(R.string.ugsv_setting_desc);
        menu.add(new ListModel(INDEX_RECORD, R.drawable.ic_ugsv_recorder, getResources().getString(R.string.solution_recorder), desc));
        menu.add(new ListModel(INDEX_CROP, R.drawable.ic_ugsv_videocrop, getResources().getString(R.string.solution_crop), desc));
        menu.add(new ListModel(INDEX_EDIT, R.drawable.ic_ugsv_editor, getResources().getString(R.string.solution_edit), desc));
        return menu;
    }

    @Override
    public void onListItemClick(ListModel model) {
        if (FastClickUtil.isFastClick()) {
            return;
        }
        switch (model.index) {
            case INDEX_RECORD:
                jumpSettingActivity(AlivcRecordSettingActivity.class);
                break;
            case INDEX_CROP:
                jumpSettingActivity(AlivcCropSettingActivity.class);
                break;
            case INDEX_EDIT:
                jumpSettingActivity(AlivcEditorSettingActivity.class);
                break;
            default:
                break;
        }
    }

    private void jumpSettingActivity(Class<?> cls) {
        Intent record = new Intent(this, cls);
        startActivity(record);
    }
}
