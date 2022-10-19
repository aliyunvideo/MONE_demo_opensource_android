/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.base.widget.control;

import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;

public class TabGroup implements OnClickListener {

    public interface OnCheckedChangeListener {
        void onCheckedChanged(TabGroup control, int checkedIndex);
    }

    private OnCheckedChangeListener _Listener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        _Listener = listener;
    }

    private final ArrayList<View> _ViewList = new ArrayList<>();

    public void addView(View view) {
        view.setOnClickListener(this);
        _ViewList.add(view);
    }

    private int _CheckedIndex = -1;

    public View getCheckedView() {
        return _CheckedIndex < 0 ? null : _ViewList.get(_CheckedIndex);
    }

    public View getView(int index) {
        return (index < 0 || index >= _ViewList.size()) ? null : _ViewList.get(index);
    }

    public int getCheckedIndex() {
        return _CheckedIndex;
    }

    public int getCheckedID() {
        return _CheckedIndex < 0 ? -1 : _ViewList.get(_CheckedIndex).getId();
    }

    public void setCheckedId(int id) {

        for (int i = 0, count = _ViewList.size(); i < count; i ++) {
            if (_ViewList.get(i).getId() == id) {
                setCheckedIndex(i);
                return;
            }
        }

        setCheckedIndex(-1);
    }

    public void setCheckedView(View item) {
        setCheckedIndex(_ViewList.indexOf(item));
    }

    public void setCheckedIndex(int index) {

        if (index == _CheckedIndex) {
            return;
        }

        if (_CheckedIndex >= 0) {
            _ViewList.get(_CheckedIndex).setActivated(false);
        }
        _CheckedIndex = index;
        if (_CheckedIndex >= 0) {
            _ViewList.get(_CheckedIndex).setActivated(true);
        }

        if (_Listener != null) {
            _Listener.onCheckedChanged(this, _CheckedIndex);
        }
    }

    @Override
    public void onClick(View v) {
        setCheckedView(v);
    }

}
