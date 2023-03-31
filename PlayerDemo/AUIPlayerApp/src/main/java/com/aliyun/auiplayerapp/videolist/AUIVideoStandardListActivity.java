package com.aliyun.auiplayerapp.videolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.alivc.player.videolist.auivideolistcommon.listener.OnLoadDataListener;
import com.alivc.player.videolist.auivideostandradlist.AUIVideoStandardListView;
import com.aliyun.auiplayerapp.R;
import com.aliyun.auiplayerapp.utils.AUIVideoListViewModelFactory;

public class AUIVideoStandardListActivity extends AppCompatActivity {

    private AUIVideoListViewModel mVideoListViewModel;
    private AUIVideoStandardListView mVideoStandardListView;
    private Boolean mIsLoadMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auivideo_standard_list);

        mVideoListViewModel = new ViewModelProvider(this, new AUIVideoListViewModelFactory(this)).get(AUIVideoListViewModel.class);

        initView();
        initObserver();
    }

    private void initView() {
        mVideoStandardListView = findViewById(R.id.aui_video_list_view);
//        mVideoStandardListView.openLoopPlay(false);
//        mVideoStandardListView.autoPlayNext(true);
    }

    private void initObserver() {
        mVideoListViewModel.mVideoListLiveData.observe(this, videoBeanList -> {
            if (mIsLoadMore) {
                mVideoStandardListView.addSources(videoBeanList);
            } else {
                mVideoStandardListView.loadSources(videoBeanList);
            }
        });


        mVideoListViewModel.mIsLoadMore.observe(this, aBoolean -> {
            this.mIsLoadMore = aBoolean;
        });


        mVideoStandardListView.setOnRefreshListener(new OnLoadDataListener() {
            @Override
            public void onRefresh() {
                mVideoListViewModel.loadSource();
            }

            @Override
            public void onLoadMore() {
//                mVideoListViewModel.addSource();
            }
        });
    }
}