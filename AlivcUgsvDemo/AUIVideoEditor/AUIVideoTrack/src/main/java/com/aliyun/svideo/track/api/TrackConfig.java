package com.aliyun.svideo.track.api;


import com.aliyun.svideo.track.util.Util;

public class TrackConfig {
    /**
     * 轨道顶部间距
     */
    public static int TRACK_LAYOUT_MARGIN_START;
    /**
     * 一张缩略图宽度
     */
    public static int FRAME_WIDTH;
    /**
     * 一张缩略图默认时长
     */
    public static final int DEFAULT_FRAME_DURATION_MS = 1000;
    /**
     * 自动滚动宽度
     */
    public static int AUTO_SCROLL_WIDTH;
    /**
     * 拖动按钮宽度
     */
    public static int MOVE_BTN_WIDTH;
    /**
     * 最短时长1S
     */
    public static int MIN_DURATION = 1000;
    /**
     * 主轨轨道间距
     */
    public static int FRAME_MID_MARGIN;
    /**
     * 音频轨道高度
     */
    public static int TRACK_AUDIO_HEIGHT;
    /**
     * 非主轨道片段高度
     */
    public static int SUB_CLIP_HEIGHT;
    public static int ERROR = -1;
    public static int draggedId = -1;

    /**
     * 时间刻度左内边距
     */
    public static int TIMELINE_PADDING_LEFT;
    /**
     * 时间戳单位，10s、5S、3S、2S、1S、15F、10F、5F、3F
     */
    public static float[] sTimeUnit = new float[]{10 * 1000, 5 * 1000, 3 * 1000, 2 * 1000, 1000, 500, 1000 / 3f, 1000 / 6f, 100};

    static {
        TIMELINE_PADDING_LEFT = Util.dp2px(81);
        FRAME_WIDTH = Util.dp2px(44);
        TRACK_AUDIO_HEIGHT = Util.dp2px(32);
        SUB_CLIP_HEIGHT = Util.dp2px(24);
        MOVE_BTN_WIDTH = Util.dp2px(15);
        AUTO_SCROLL_WIDTH = (int) (FRAME_WIDTH * 1.5f);
        TRACK_LAYOUT_MARGIN_START = Util.dp2px(4);
        FRAME_MID_MARGIN = Util.dp2px(3);
    }

    /**
     * 获取单位时间像素长度
     *
     * @return
     */
    public static float getPX_US() {
        return FRAME_WIDTH / DEFAULT_FRAME_DURATION_MS;
    }

    /**
     * 获取单位时间对应的像素长度
     * @param timelineScale 缩放值
     * @return 单位时间的像素长度
     */
    public static float getPxUnit(float timelineScale, long duration) {
        return FRAME_WIDTH * timelineScale / duration;
    }

    /**
     * 获取1S时长单位像素长度
     * @param timelineScale 缩放值
     * @return 单位像素长度
     */
    public static float getPxUnit(float timelineScale) {
        return getPxUnit(timelineScale, DEFAULT_FRAME_DURATION_MS);
    }

    /**
     * 获取最小单位像素
     *
     * @return 最小单位像素
     */
    public static float getMinPx(float timelineScale) {
        return (TrackConfig.MIN_DURATION * TrackConfig.getPxUnit(timelineScale));
    }

    public static float autoScrollSpeed(float touchRawX, int screenWidth) {
        if (touchRawX >= screenWidth >> 1) {
            touchRawX = screenWidth - touchRawX;
        }
        if (touchRawX >= AUTO_SCROLL_WIDTH) {
            return 1.0f;
        }
        return 1.0f + (((AUTO_SCROLL_WIDTH - touchRawX) / AUTO_SCROLL_WIDTH) * 4);
    }

}
