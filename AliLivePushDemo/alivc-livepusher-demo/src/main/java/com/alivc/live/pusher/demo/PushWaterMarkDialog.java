package com.alivc.live.pusher.demo;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.alivc.live.pusher.WaterMarkInfo;

import java.util.ArrayList;

public class PushWaterMarkDialog extends DialogFragment{

    private Switch mSwitch;
    private EditText mX;
    private EditText mY;
    private EditText mW;
    private Switch mSwitch1;
    private EditText mX1;
    private EditText mY1;
    private EditText mW1;
    private Switch mSwitch2;
    private EditText mX2;
    private EditText mY2;
    private EditText mW2;
    private ArrayList<WaterMarkInfo> mWaterMarkInfos;
    private WaterMarkInfo mWaterMarkInfo = new WaterMarkInfo();
    private WaterMarkInfo mWaterMarkInfo1 = new WaterMarkInfo();
    private WaterMarkInfo mWaterMarkInfo2 = new WaterMarkInfo();
    private boolean mWater = false;
    private boolean mWater1 = false;
    private boolean mWater2 = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        getDialog().setCanceledOnTouchOutside(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.push_watermark, container);
        mX = (EditText) view.findViewById(R.id.x);
        mY = (EditText) view.findViewById(R.id.y);
        mW = (EditText) view.findViewById(R.id.w);
        mX1 = (EditText) view.findViewById(R.id.x1);
        mY1 = (EditText) view.findViewById(R.id.y1);
        mW1 = (EditText) view.findViewById(R.id.w1);
        mX2 = (EditText) view.findViewById(R.id.x2);
        mY2 = (EditText) view.findViewById(R.id.y2);
        mW2 = (EditText) view.findViewById(R.id.w2);
        mSwitch = (Switch) view.findViewById(R.id.watermark);
        mSwitch1 = (Switch) view.findViewById(R.id.watermark1);
        mSwitch2 = (Switch) view.findViewById(R.id.watermark2);
        mSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        mSwitch1.setOnCheckedChangeListener(onCheckedChangeListener);
        mSwitch2.setOnCheckedChangeListener(onCheckedChangeListener);
        return view;
    }

    @Override
    public void onResume() {
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        super.onResume();

        DisplayMetrics dpMetrics = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();

        p.width = dpMetrics.widthPixels;
        p.height = dpMetrics.heightPixels * 2/3;
        getDialog().getWindow().setAttributes(p);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mWaterMarkInfos == null){
            return;
        }
        try {

            if (!mX.getText().toString().isEmpty() && !mY.getText().toString().isEmpty() && !mW.getText().toString().isEmpty()) {
                mWaterMarkInfo.mWaterMarkCoordX = Float.valueOf(mX.getText().toString());
                mWaterMarkInfo.mWaterMarkCoordY = Float.valueOf(mY.getText().toString());
                mWaterMarkInfo.mWaterMarkWidth = Float.valueOf(mW.getText().toString());
                mWaterMarkInfo.mWaterMarkPath = Common.waterMark;
                mWater = true;
            }

            if (!mX1.getText().toString().isEmpty() && !mY1.getText().toString().isEmpty() && !mW1.getText().toString().isEmpty()) {
                mWaterMarkInfo1.mWaterMarkCoordX = Float.valueOf(mX1.getText().toString());
                mWaterMarkInfo1.mWaterMarkCoordY = Float.valueOf(mY1.getText().toString());
                mWaterMarkInfo1.mWaterMarkWidth = Float.valueOf(mW1.getText().toString());
                mWaterMarkInfo1.mWaterMarkPath = Common.waterMark;
                mWater1 = true;
            }

            if (!mX2.getText().toString().isEmpty() && !mY2.getText().toString().isEmpty() && !mW2.getText().toString().isEmpty()) {
                mWaterMarkInfo2.mWaterMarkCoordX = Float.valueOf(mX2.getText().toString());
                mWaterMarkInfo2.mWaterMarkCoordY = Float.valueOf(mY2.getText().toString());
                mWaterMarkInfo2.mWaterMarkWidth = Float.valueOf(mW2.getText().toString());
                mWaterMarkInfo2.mWaterMarkPath = Common.waterMark;
                mWater2 = true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(mWaterMarkInfos.size() > 0) {
            mWaterMarkInfos.clear();
        }

        if (mWater) {
            mWaterMarkInfos.add(mWaterMarkInfo);
        }
        if (mWater1)  {
            mWaterMarkInfos.add(mWaterMarkInfo1);
        }
        if (mWater2) {
            mWaterMarkInfos.add(mWaterMarkInfo2);
        }
    }

    public void setWaterMarkInfo(ArrayList<WaterMarkInfo> waterMarkInfos) {
        this.mWaterMarkInfos = waterMarkInfos;
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int id = compoundButton.getId();
            if(id == R.id.watermark) {
                mWater = b;
            } else if (id == R.id.watermark1) {
                mWater1 = b;
            } else if (id == R.id.watermark2) {
                mWater2 = b;
            }
        }
    };
}
