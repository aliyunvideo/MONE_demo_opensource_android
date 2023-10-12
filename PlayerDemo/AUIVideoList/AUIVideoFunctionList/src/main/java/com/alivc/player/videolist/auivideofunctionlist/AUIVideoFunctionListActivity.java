package com.alivc.player.videolist.auivideofunctionlist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alivc.player.videolist.auivdieofunctionlist.R;
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

public class AUIVideoFunctionListActivity extends AppCompatActivity {

    private AUIVideoListViewModel<List<VideoInfo>> mVideoListViewModel;

    private final AUIVideoListViewModel.DataProvider<List<VideoInfo>> dataProvider = new AUIVideoListViewModel.DataProvider<List<VideoInfo>>() {
        @Override
        public void onLoadData(AUIVideoListViewModel.DataCallback<List<VideoInfo>> callback) {
            callback.onData(loadDataFromLocalJson(getBaseContext()));
        }
    };

    private AUIVideoFunctionListView mVideoFunctionListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auivideo_function_list);

        mVideoListViewModel = new ViewModelProvider(this, new AUIVideoListViewModelFactory<>(dataProvider)).get(AUIVideoListViewModel.class);

        initView();
        initObserver();
    }

    private void initView() {
        mVideoFunctionListView = findViewById(R.id.aui_video_list_view);
        mVideoFunctionListView.showPlayTitleContent(true);
//        mVideoFunctionListView.openLoopPlay(false);
//        mVideoFunctionListView.autoPlayNext(true);
    }

    private void initObserver() {
        mVideoListViewModel.mLoadMoreVideoListLiveData.observe(this, new Observer<List<VideoInfo>>() {
            @Override
            public void onChanged(List<VideoInfo> videoInfos) {
                mVideoFunctionListView.addSources(videoInfos);
            }
        });

        mVideoListViewModel.mRefreshVideoListLiveData.observe(this, new Observer<List<VideoInfo>>() {
            @Override
            public void onChanged(List<VideoInfo> videoInfos) {
                mVideoFunctionListView.loadSources(videoInfos);
            }
        });

        mVideoFunctionListView.setOnRefreshListener(new OnLoadDataListener() {
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