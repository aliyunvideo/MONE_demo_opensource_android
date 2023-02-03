package com.alivc.live.pusher.widget;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.alivc.live.pusher.demo.R;
import com.alivc.live.pusher.demo.adapter.OnSoundEffectChangedListener;
import com.alivc.live.pusher.demo.bean.SoundEffectBean;
import com.alivc.live.pusher.demo.fragment.SoundEffectFragment;
import com.google.android.material.tabs.TabLayout;

/**
 * 音效 View
 */
public class SoundEffectView extends FrameLayout {

    private SoundEffectFragment mSoundEffectChangeVoiceFragment;
    private SoundEffectFragment mSoundEffectReverbFragment;
    private Fragment mCurrentFragment;
    private OnSoundEffectChangedListener mOnSoundEffectChangedListener;

    public SoundEffectView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SoundEffectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SoundEffectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_sound_effect_view, this, true);
        initFragment();
        initTabLayout();
    }

    private void initFragment() {
        mSoundEffectChangeVoiceFragment = new SoundEffectFragment();
        Bundle changeVoiceBundle = new Bundle();
        changeVoiceBundle.putSerializable("args",SoundEffectBean.SoundEffectChangeVoiceBean.getLivePushSoundEffectChangeVoice());
        mSoundEffectChangeVoiceFragment.setArguments(changeVoiceBundle);

        mSoundEffectReverbFragment = new SoundEffectFragment();
        Bundle reverbBundle = new Bundle();
        reverbBundle.putSerializable("args",SoundEffectBean.SoundEffectReverb.getLivePushSoundEffectReverb());
        mSoundEffectReverbFragment.setArguments(reverbBundle);

        switchFragment(mSoundEffectChangeVoiceFragment);

        mSoundEffectChangeVoiceFragment.setOnSoundEffectItemClickListener(position -> {
            if (mOnSoundEffectChangedListener != null) {
                mOnSoundEffectChangedListener.onSoundEffectChangeVoiceModeSelected(position);
            }
        });

        mSoundEffectReverbFragment.setOnSoundEffectItemClickListener(position -> {
            if (mOnSoundEffectChangedListener != null) {
                mOnSoundEffectChangedListener.onSoundEffectRevertBSelected(position);
            }
        });
    }

    private void initTabLayout() {
        TabLayout mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0://变声
                        switchFragment(mSoundEffectChangeVoiceFragment);
                        break;
                    case 1://环境音
                        switchFragment(mSoundEffectReverbFragment);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void switchFragment(Fragment targetFragment) {
        if (mCurrentFragment == targetFragment) {
            return;
        }
        FragmentTransaction fragmentTransaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            if (mCurrentFragment != null) {
                fragmentTransaction.hide(mCurrentFragment);
            }
            fragmentTransaction
                    .add(R.id.frame_layout, targetFragment)
                    .commit();
        } else {
            if (mCurrentFragment != null) {
                fragmentTransaction.hide(mCurrentFragment);
            }
            fragmentTransaction
                    .show(targetFragment)
                    .commit();
        }
        mCurrentFragment = targetFragment;
    }

    public void setOnSoundEffectChangedListener(OnSoundEffectChangedListener listener) {
        this.mOnSoundEffectChangedListener = listener;
    }

}
