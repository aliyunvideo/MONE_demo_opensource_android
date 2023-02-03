package com.alivc.live.commonutils;

public class FastClickUtil {
    private static long lastClickTime;
    //两次点击按钮之间的点击间隔不能少于2500毫秒
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
