/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.base.widget.control;

public class TabbedViewStackBinding implements TabGroup.OnCheckedChangeListener {

    private ViewStack _ViewStack;

    public void setViewStack(ViewStack vs) {
        _ViewStack = vs;
    }

    public ViewStack getViewStack() {
        return _ViewStack;
    }

    @Override
    public void onCheckedChanged(TabGroup control, int checkedIndex) {
        _ViewStack.setActiveIndex(checkedIndex);
    }
}
