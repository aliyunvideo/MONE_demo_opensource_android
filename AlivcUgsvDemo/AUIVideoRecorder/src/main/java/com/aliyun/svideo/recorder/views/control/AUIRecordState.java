package com.aliyun.svideo.recorder.views.control;

public enum AUIRecordState {
    /**
     * 等待录制
     */
    WAITING,
    /**
     * 等待进入录制（倒计时）
     * 说明：倒计时录制，需等倒计时结束才进入录制
     */
    RECORD_PENDING,
    /**
     * 录制中
     */
    RECORDING,
    /**
     * 停止录制中
     * 说明：触发停止录制后，需等待SDK返回录制结束
     */
    RECORD_STOPPING,
    /**
     * 视频合成中
     */
    FINISHING,
}
