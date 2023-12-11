package com.aliyun.svideo.mixrecorder.view.effects.paster;

import com.aliyun.svideosdk.common.struct.form.PreviewPasterForm;

public interface PasterSelectListener {
    void onPasterSelected(PreviewPasterForm imvForm);

    /**
     * 选择的贴图下载完成
     * @param path paster path
     */
    void onSelectPasterDownloadFinish(String path);
}
