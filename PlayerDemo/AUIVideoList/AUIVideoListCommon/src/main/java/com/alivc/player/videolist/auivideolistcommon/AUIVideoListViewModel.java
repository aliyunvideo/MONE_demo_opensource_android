package com.alivc.player.videolist.auivideolistcommon;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AUIVideoListViewModel<T> extends ViewModel {

    private final DataProvider<T> mDataProvider;

    private final MutableLiveData<T> _mLoadMoreVideoListLiveData = new MutableLiveData<>();
    public LiveData<T> mLoadMoreVideoListLiveData = _mLoadMoreVideoListLiveData;

    private final MutableLiveData<T> _mRefreshVideoListLiveData = new MutableLiveData<>();
    public LiveData<T> mRefreshVideoListLiveData = _mRefreshVideoListLiveData;

    public AUIVideoListViewModel(DataProvider<T> dataProvider) {
        mDataProvider = dataProvider;
        loadData(false);
    }

    public void loadData(boolean isRefresh) {
        mDataProvider.onLoadData(new DataCallback<T>() {
            @Override
            public void onData(T data) {
                if (isRefresh) {
                    _mRefreshVideoListLiveData.postValue(data);
                } else {
                    _mLoadMoreVideoListLiveData.postValue(data);
                }
            }
        });
    }

    public interface DataCallback<T> {
        void onData(T data);
    }

    public interface DataProvider<T> {
        void onLoadData(DataCallback<T> callback);
    }
}
