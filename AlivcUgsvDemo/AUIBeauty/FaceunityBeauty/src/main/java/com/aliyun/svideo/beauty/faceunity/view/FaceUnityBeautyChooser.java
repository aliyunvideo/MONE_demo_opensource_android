package com.aliyun.svideo.beauty.faceunity.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.aliyun.svideo.base.BaseChooser;
import com.aliyun.svideo.base.widget.PagerSlidingTabStrip;
import com.aliyun.svideo.beauty.faceunity.R;
import com.aliyun.svideo.beauty.faceunity.adapter.FaceUnityViewPagerAdapter;
import com.aliyun.svideo.beauty.faceunity.adapter.holder.FaceUnityFaceViewHolder;
import com.aliyun.svideo.beauty.faceunity.adapter.holder.FaceUnitySkinViewHolder;
import com.aliyun.svideo.beauty.faceunity.inteface.OnBeautyChooserCallback;


public class FaceUnityBeautyChooser extends BaseChooser {
    private ViewPager mViewPager;
    private PagerSlidingTabStrip mTabPageIndicator;
    private LinearLayout llBlank;
    private OnBeautyChooserCallback mOnBeautyChooserCallback;
    private int mBeautyFaceLevel = 3;
    private int mBeautySkinLevel = 3;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.QUDemoFullStyle);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.alivc_faceunity_beauty_chooser_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTabPageIndicator = (PagerSlidingTabStrip) view.findViewById(R.id.alivc_dialog_indicator);
        mViewPager = (ViewPager) view.findViewById(R.id.alivc_dialog_container);
        llBlank = view.findViewById(R.id.ll_blank);
        mTabPageIndicator.setTextColorResource(R.color.aliyun_svideo_tab_text_color_selector);
        mTabPageIndicator.setTabViewId(R.layout.aliyun_svideo_layout_tab_top);
        FaceUnityViewPagerAdapter raceViewPagerAdapter = new FaceUnityViewPagerAdapter();
        FaceUnityFaceViewHolder raceFaceViewHolder = new FaceUnityFaceViewHolder(view.getContext(), mBeautyFaceLevel, mOnBeautyChooserCallback);
        FaceUnitySkinViewHolder raceSkinViewHolder = new FaceUnitySkinViewHolder(view.getContext(),mBeautySkinLevel, mOnBeautyChooserCallback);
        raceViewPagerAdapter.addViewHolder(raceFaceViewHolder);
        raceViewPagerAdapter.addViewHolder(raceSkinViewHolder);
        raceViewPagerAdapter.notifyDataSetChanged();
        mViewPager.setOffscreenPageLimit(raceViewPagerAdapter.size());
        mViewPager.setAdapter(raceViewPagerAdapter);
        mTabPageIndicator.setViewPager(mViewPager);
    }

    @Override
    public void onStart() {
        super.onStart();
        llBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    解决crash:java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
                    原因:after onSaveInstanceState invoke commit,而 show 会触发 commit 操作
                    fragment is added and its state has already been saved，
                    Any operations that would change saved state should not be performed if this method returns true
                */
                if (isStateSaved()) {
                    return;
                }
                if (mOnBeautyChooserCallback != null){
                    mOnBeautyChooserCallback.onChooserBlankClick();
                }
            }
        });
    }

    public void show(FragmentManager manager, String tag, int beautyFaceLevel, int beautySkinLevel) {
        mBeautyFaceLevel = beautyFaceLevel;
        mBeautySkinLevel = beautySkinLevel;
        super.show(manager, tag);
    }


    public void setOnBeautyChooserCallback(OnBeautyChooserCallback mOnBeautyChooserCallback) {
        this.mOnBeautyChooserCallback = mOnBeautyChooserCallback;
    }


}
