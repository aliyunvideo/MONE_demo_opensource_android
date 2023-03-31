package com.aliyun.auiplayerapp.videolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.alivc.player.videolist.auivideofunctionlist.AUIVideoFunctionListView;
import com.alivc.player.videolist.auivideolistcommon.listener.OnLoadDataListener;
import com.aliyun.auiplayerapp.R;
import com.aliyun.auiplayerapp.utils.AUIVideoListViewModelFactory;

public class AUIVideoFunctionListActivity extends AppCompatActivity {

    private AUIVideoListViewModel mVideoListViewModel;
    private AUIVideoFunctionListView mVideoFunctionListView;
    private boolean mIsLoadMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auivideo_function_list);

        mVideoListViewModel = new ViewModelProvider(this,new AUIVideoListViewModelFactory(this)).get(AUIVideoListViewModel.class);

        initView();
        initObserver();
    }

    private void initView() {
        mVideoFunctionListView = findViewById(R.id.aui_video_list_view);

//        mVideoFunctionListView.openLoopPlay(false);
//        mVideoFunctionListView.autoPlayNext(true);
    }

    private void initObserver() {
        mVideoListViewModel.mVideoListLiveData.observe(this, videoBeanList -> {
            if (mIsLoadMore) {
                mVideoFunctionListView.addSources(videoBeanList);
            } else {
                mVideoFunctionListView.loadSources(videoBeanList);
            }
        });


        mVideoListViewModel.mIsLoadMore.observe(this, aBoolean -> {
            this.mIsLoadMore = aBoolean;
        });

        mVideoFunctionListView.setOnRefreshListener(new OnLoadDataListener() {
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