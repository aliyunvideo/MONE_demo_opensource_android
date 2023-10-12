package com.alivc.player.videolist.auivideolistcommon;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AUIVideoListViewModelFactory<T> implements ViewModelProvider.Factory {

    private final AUIVideoListViewModel.DataProvider<T> mDataProvider;

    public AUIVideoListViewModelFactory(AUIVideoListViewModel.DataProvider<T> dataProvider) {
        this.mDataProvider = dataProvider;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AUIVideoListViewModel(mDataProvider);
    }
}
