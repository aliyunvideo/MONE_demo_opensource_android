package com.aliyun.svideo.base.utils;

/**
 * @author zsy_18 data:2018/9/29
 */
public class FastClickUtil {
    public static final int MIN_DELAY_TIME= 500;  // 两次点击间隔不能少于500ms
    private static long lastClickTime;
    private static long lastNoRecordClickTime;

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        lastNoRecordClickTime = currentClickTime;
        return flag;
    }

    /**
     * 用于判断录制按钮和其他按钮同时按下
     * @return
     */
    public static boolean isRecordWithOtherClick() {
//        boolean flag = true;
//        long currentClickTime = System.currentTimeMillis();
//        if ((currentClickTime - lastNoRecordClickTime) >= MIN_DELAY_TIME) {
//            flag = false;
//        }
//        lastClickTime = currentClickTime;
        return false;
    }
}
