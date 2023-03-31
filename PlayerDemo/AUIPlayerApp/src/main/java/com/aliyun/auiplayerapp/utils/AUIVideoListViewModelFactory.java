package com.aliyun.auiplayerapp.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.aliyun.auiplayerapp.videolist.AUIVideoListViewModel;
import com.aliyun.auiplayerapp.videolist.repository.local.AUIVideoListLocalDataSource;

public class AUIVideoListViewModelFactory implements ViewModelProvider.Factory {

    private final Context mContext;

    public AUIVideoListViewModelFactory(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        if (aClass == AUIVideoListViewModel.class) {
            AUIVideoListLocalDataSource auiVideoListLocalDataSource = new AUIVideoListLocalDataSource(mContext);
            return (T) new AUIVideoListViewModel(auiVideoListLocalDataSource);
        }
        return null;
    }
}
