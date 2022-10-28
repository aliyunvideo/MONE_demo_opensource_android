package com.aliyun.interactive_common;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.alivc.live.commonbiz.SharedPreferenceUtils;

/**
 * AppInfo 相关信息录入 Fragment
 */
public class InteractLiveAppInfoFragment extends Fragment {

    private ImageView mArrowImageView;
    private ConstraintLayout mAppInfoConstraintLayout;
    private ImageView mEditImageView;
    private TextView mAppIdTextView;
    private TextView mAppKeyTextView;
    private TextView mPlayDomainTextView;
    private OnEditClickListener mOnEditClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_interact_live_appinfo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mArrowImageView = view.findViewById(R.id.iv_arrow);
        mEditImageView = view.findViewById(R.id.iv_edit);
        mAppInfoConstraintLayout = view.findViewById(R.id.cl_app_info);

        mAppIdTextView = view.findViewById(R.id.tv_app_id);
        mAppKeyTextView = view.findViewById(R.id.tv_app_key);
        mPlayDomainTextView = view.findViewById(R.id.tv_play_domain);

        mArrowImageView.setColorFilter(Color.WHITE);

        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAppIdTextView.setText(SharedPreferenceUtils.getAppId(getContext().getApplicationContext()));
        mAppKeyTextView.setText(SharedPreferenceUtils.getAppKey(getContext().getApplicationContext()));
        mPlayDomainTextView.setText(SharedPreferenceUtils.getPlayDomain(getContext().getApplicationContext()));
    }

    private void initListener(){
        mArrowImageView.setOnClickListener((view) -> {
            float mArrowImageViewRotation;
            if (mAppInfoConstraintLayout.isShown()) {
                mAppInfoConstraintLayout.setVisibility(View.GONE);
                mArrowImageViewRotation = 0;
            } else {
                mAppInfoConstraintLayout.setVisibility(View.VISIBLE);
                mArrowImageViewRotation = 180;
            }
            mArrowImageView.setRotation(mArrowImageViewRotation);
        });

        mEditImageView.setOnClickListener((view) -> {
            if(mOnEditClickListener != null){
                mOnEditClickListener.onEditClickListener();
            }
        });
    }

    public interface OnEditClickListener{
        void onEditClickListener();
    }
    
    public void setOnEditClickListener(OnEditClickListener listener){
        this.mOnEditClickListener = listener;
    }
}
