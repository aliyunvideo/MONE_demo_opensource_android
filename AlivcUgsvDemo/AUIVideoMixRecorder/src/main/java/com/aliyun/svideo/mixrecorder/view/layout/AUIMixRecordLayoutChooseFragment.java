package com.aliyun.svideo.mixrecorder.view.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aliyun.svideo.mixrecorder.view.dialog.AUIIPageTab;
import com.aliyun.svideo.record.R;

public class AUIMixRecordLayoutChooseFragment extends Fragment implements AUIIPageTab, View.OnClickListener {
    private String mTitle;
    private View[] mBtnArray = new View[3];
    private int mSelectedPos = 0;
    private View.OnClickListener mListener;
    @Override
    public void setTabTitle(String tabTitle) {
        mTitle = tabTitle;
    }

    @Override
    public String getTabTitle() {
        return mTitle;
    }

    @Override
    public int getTabIcon() {
        return 0;
    }

    public void setOnClickListener(View.OnClickListener listener){
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ugsv_mix_record_layout_options, container, false);
        mBtnArray[0] = rootView.findViewById(R.id.layout_horizontal);
        mBtnArray[1] = rootView.findViewById(R.id.layout_vertical);
        mBtnArray[2] = rootView.findViewById(R.id.layout_float);
        for(int i = 0; i < mBtnArray.length; ++i){
            mBtnArray[i].setOnClickListener(this);
            mBtnArray[i].setTag(i);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateLayout();
    }

    private void updateLayout(){
        for(int i = 0; i < mBtnArray.length; ++i){
            mBtnArray[i].setSelected(i == mSelectedPos);
        }
    }

    @Override
    public void onClick(View v) {
        for(int i = 0; i < mBtnArray.length; ++i){
            if(v == mBtnArray[i]){
                mBtnArray[i].setSelected(true);
                mSelectedPos = i;
            }else{
                mBtnArray[i].setSelected(false);
            }
        }
        if(mListener != null){
            mListener.onClick(v);
        }
    }
}
