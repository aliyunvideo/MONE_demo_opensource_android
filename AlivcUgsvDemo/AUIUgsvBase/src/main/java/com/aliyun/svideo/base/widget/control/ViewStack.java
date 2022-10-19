/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.base.widget.control;

import android.view.View;

import java.util.ArrayList;

public class ViewStack {

    private final int _Invisible;

    public ViewStack(int invisible_value) {
        _Invisible = invisible_value;
    }

    public ViewStack() {
        this(View.GONE);
    }

    private final ArrayList<View> _ViewList = new ArrayList<>();

    public void addView(View view) {
        _ViewList.add(view);
        view.setVisibility(_Invisible);
    }

    private int _ActiveIndex = -1;

    public void setActiveIndex(int value) {

        if (_ActiveIndex >= 0) {
            _ViewList.get(_ActiveIndex).setVisibility(_Invisible);
        }
        _ActiveIndex = value;
        if (_ActiveIndex >= 0) {
            _ViewList.get(_ActiveIndex).setVisibility(View.VISIBLE);
        }
    }

    public int getActiveIndex() {
        return _ActiveIndex;
    }

    public void setActiveViewID(int id) {
        for (int i = 0, count = _ViewList.size(); i < count; i ++) {
            if (_ViewList.get(i).getId() == id) {
                setActiveIndex(i);
                return;
            }
        }
        setActiveIndex(-1);
    }

}
