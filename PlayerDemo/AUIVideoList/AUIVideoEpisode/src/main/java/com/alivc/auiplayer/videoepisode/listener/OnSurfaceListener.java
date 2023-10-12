package com.alivc.auiplayer.videoepisode.listener;

import android.view.Surface;

/**
 * @author keria
 * @date 2023/9/26
 * @brief Surface状态回调
 */
public interface OnSurfaceListener {
    void onSurfaceCreate(int index, Surface surface);

    void onSurfaceChanged(int index, int width, int height);

    void onSurfaceDestroyed(int index);
}
