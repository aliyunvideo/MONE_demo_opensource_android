package com.ethanhua.skeleton;

import android.view.View;

import androidx.annotation.LayoutRes;

public class SkeletonManager {
    private SkeletonScreen skeletonScreen;

    public void show(View view, @LayoutRes int layoutId) {
        skeletonScreen = Skeleton.bind(view)
                .shimmer(false)
                .angle(20)
                .load(layoutId)
                .duration(1200)
                .show();

    }

    public void hide() {
        if (skeletonScreen != null) {
            skeletonScreen.hide();
        }
    }
}
