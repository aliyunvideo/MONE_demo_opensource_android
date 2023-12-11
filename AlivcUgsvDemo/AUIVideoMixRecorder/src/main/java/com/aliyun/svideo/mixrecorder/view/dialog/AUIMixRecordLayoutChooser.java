package com.aliyun.svideo.mixrecorder.view.dialog;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.aliyun.svideo.mixrecorder.view.layout.AUIMixRecordLayoutChooseFragment;
import com.aliyun.svideo.record.R;

import java.util.ArrayList;
import java.util.List;

public class AUIMixRecordLayoutChooser extends AUIBaseChooser{
    private AUIMixRecordLayoutChooseFragment mFragment;
    private Context mContext;
    private View.OnClickListener mListener;

    public AUIMixRecordLayoutChooser(Context ctx){
        mContext = ctx;
    }
    @Override
    public List<Fragment> createPagerFragmentList() {
        mFragment = new AUIMixRecordLayoutChooseFragment();
        mFragment.setTabTitle(mContext.getString(R.string.ugsv_mix_recorder_control_layout));
        mFragment.setOnClickListener(mListener);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(mFragment);
        return fragments;
    }

    public void setOnClickListener(View.OnClickListener listener){
        mListener = listener;
    }
}
