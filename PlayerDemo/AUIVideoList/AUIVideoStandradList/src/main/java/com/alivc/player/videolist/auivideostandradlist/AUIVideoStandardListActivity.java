package com.alivc.player.videolist.auivideostandradlist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alivc.player.videolist.auivideolistcommon.AUIVideoListViewModel;
import com.alivc.player.videolist.auivideolistcommon.AUIVideoListViewModelFactory;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.OnLoadDataListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class AUIVideoStandardListActivity extends AppCompatActivity {

    private AUIVideoListViewModel<List<VideoInfo>> mVideoListViewModel;

    private final AUIVideoListViewModel.DataProvider<List<VideoInfo>> dataProvider = new AUIVideoListViewModel.DataProvider<List<VideoInfo>>() {
        @Override
        public void onLoadData(AUIVideoListViewModel.DataCallback<List<VideoInfo>> callback) {
            callback.onData(loadDataFromLocalJson(getBaseContext()));
        }
    };

    private AUIVideoStandardListView mVideoStandardListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auivideo_standard_list);

        mVideoListViewModel = new ViewModelProvider(this, new AUIVideoListViewModelFactory<>(dataProvider)).get(AUIVideoListViewModel.class);

        initView();
        initObserver();
    }

    private void initView() {
        mVideoStandardListView = findViewById(R.id.aui_video_list_view);
//        mVideoStandardListView.openLoopPlay(false);
//        mVideoStandardListView.autoPlayNext(true);
        mVideoStandardListView.showPlayTitleContent(true);
    }

    private void initObserver() {
        mVideoListViewModel.mLoadMoreVideoListLiveData.observe(this, new Observer<List<VideoInfo>>() {
            @Override
            public void onChanged(List<VideoInfo> videoInfos) {
                mVideoStandardListView.addSources(videoInfos);
            }
        });

        mVideoListViewModel.mRefreshVideoListLiveData.observe(this, new Observer<List<VideoInfo>>() {
            @Override
            public void onChanged(List<VideoInfo> videoInfos) {
                mVideoStandardListView.loadSources(videoInfos);
            }
        });

        mVideoStandardListView.setOnRefreshListener(new OnLoadDataListener() {
            @Override
            public void onRefresh() {
                mVideoListViewModel.loadData(true);
            }

            @Override
            public void onLoadMore() {
                mVideoListViewModel.loadData(false);
            }
        });
    }

    private static List<VideoInfo> loadDataFromLocalJson(@NonNull Context context) {
        Gson gson = new Gson();

        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream assetsInputStream = context.getAssets().open("videolist.json");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetsInputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gson.fromJson(stringBuilder.toString(), new TypeToken<List<VideoInfo>>() {
        }.getType());
    }
}