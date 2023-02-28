package com.alivc.live.baselive_push.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.live.baselive_push.R;
import com.alivc.live.baselive_push.adapter.SoundEffectRecyclerViewAdapter;
import com.alivc.live.baselive_push.bean.SoundEffectBean;

import java.util.HashMap;

/**
 * 音效设置 Fragment
 */
public class SoundEffectFragment extends Fragment {

    private View inflate;
    private HashMap<Integer, SoundEffectBean> mDataMap;
    private SoundEffectRecyclerViewAdapter.OnSoundEffectItemClickListener mOnSoundEffectItemClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mDataMap = (HashMap<Integer, SoundEffectBean>) arguments.getSerializable("args");
        }
        inflate = inflater.inflate(R.layout.fragment_sound_effect, container, false);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = inflate.findViewById(R.id.item_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        SoundEffectRecyclerViewAdapter soundEffectRecyclerViewAdapter = new SoundEffectRecyclerViewAdapter(view.getContext(), mDataMap);
        mRecyclerView.setAdapter(soundEffectRecyclerViewAdapter);

        soundEffectRecyclerViewAdapter.setOnSoundEffectItemClickListener(position -> {
            soundEffectRecyclerViewAdapter.setSelectIndex(position);
            if (mOnSoundEffectItemClickListener != null) {
                mOnSoundEffectItemClickListener.onSoundEffectItemClick(position);
            }
        });
    }

    public void setOnSoundEffectItemClickListener(SoundEffectRecyclerViewAdapter.OnSoundEffectItemClickListener listener) {
        this.mOnSoundEffectItemClickListener = listener;
    }
}
