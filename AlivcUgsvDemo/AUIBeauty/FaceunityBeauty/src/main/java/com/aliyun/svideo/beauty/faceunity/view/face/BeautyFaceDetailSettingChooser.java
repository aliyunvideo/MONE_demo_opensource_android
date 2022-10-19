package com.aliyun.svideo.beauty.faceunity.view.face;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.aliyun.svideo.base.BaseChooser;
import com.aliyun.svideo.beauty.faceunity.R;
import com.aliyun.svideo.beauty.faceunity.bean.FaceUnityBeautyParams;
import com.aliyun.svideo.beauty.faceunity.inteface.OnBeautyChooserCallback;
import com.aliyun.svideo.beauty.faceunity.inteface.OnBeautyParamsChangeListener;
import com.aliyun.svideo.beauty.faceunity.inteface.OnBlankViewClickListener;


/**
 * 美颜微调
 *
 * @author xlx
 */
public class BeautyFaceDetailSettingChooser extends BaseChooser implements OnBlankViewClickListener, OnBeautyParamsChangeListener {

    private AlivcBeautyDetailSettingView beautyDetailSettingView;
    private FaceUnityBeautyParams mBeautyParams;
    private OnBeautyChooserCallback mOnBeautyChooserCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.QUDemoFullStyle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return new AlivcBeautyDetailSettingView(getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        beautyDetailSettingView = (AlivcBeautyDetailSettingView) view;
        beautyDetailSettingView.setBeautyParamsChangeListener(this);
        beautyDetailSettingView.setOnBlankViewClickListener(this);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    if (mOnBeautyChooserCallback != null){
                        mOnBeautyChooserCallback.onChooserKeyBackClick();
                    }
                }
                return false;
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        beautyDetailSettingView.setParams(mBeautyParams);
    }

    /**
     * dialog空白区域点击事件
     */
    @Override
    public void onBlankClick() {
        if (mOnBeautyChooserCallback != null){
            mOnBeautyChooserCallback.onChooserBlankClick();
        }
    }

    @Override
    public void onBackClick() {
        if (mOnBeautyChooserCallback != null) {
            mOnBeautyChooserCallback.onChooserBackClick();
        }
    }



    public void setBeautyParams(FaceUnityBeautyParams mBeautyParams) {
        this.mBeautyParams = mBeautyParams;
    }

    @Override
    public void onBeautyFaceChange(FaceUnityBeautyParams param) {
        if (mOnBeautyChooserCallback != null){
            mOnBeautyChooserCallback.onBeautyFaceChange(param);
        }
    }

    @Override
    public void onBeautyShapeChange(FaceUnityBeautyParams param) {
        if (mOnBeautyChooserCallback != null){
            mOnBeautyChooserCallback.onBeautyShapeChange(param);
        }
    }

    public void setOnBeautyChooserCallback(OnBeautyChooserCallback mOnBeautyChooserCallback) {
        this.mOnBeautyChooserCallback = mOnBeautyChooserCallback;
    }

    public void show(FragmentManager manager, String tag, int currentLevel) {
        super.show(manager, tag);
    }
}
