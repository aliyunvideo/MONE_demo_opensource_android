package com.aliyun.svideo.track.view;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

/**
 * 自定义移除拖拽控件阴影
 */
public class DragShadowBuilder extends View.DragShadowBuilder {
    public DragShadowBuilder(View view) {
        super(view);
    }

    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        //修改宽高,太宽会报错
        outShadowSize.set(100, 100);
        outShadowTouchPoint.set(outShadowSize.x / 2, outShadowSize.y / 2);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {

    }
}
