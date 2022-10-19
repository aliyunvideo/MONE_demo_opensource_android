package com.aliyun.player.alivcplayerexpand.view.gesture;

public abstract class SimplyTapGestureListener implements GestureView.GestureListener {
    @Override
    public void onHorizontalDistance(float downX, float nowX) {
    }

    @Override
    public void onLeftVerticalDistance(float downY, float nowY) {

    }

    @Override
    public void onRightVerticalDistance(float downY, float nowY) {

    }

    @Override
    public void onGestureEnd() {

    }

    @Override
    public void onSingleTap() {
        onSingleTapClick();
    }

    @Override
    public void onDoubleTap() {
    }

    @Override
    public void onLongPress() {
    }

    public abstract void onSingleTapClick();
}
