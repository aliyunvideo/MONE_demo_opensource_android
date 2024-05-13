package com.alivc.auiplayer.videoepisode;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.alivc.auiplayer.videoepisode.data.AUIEpisodeConstants;
import com.alivc.auiplayer.videoepisode.data.AUIEpisodeData;
import com.alivc.auiplayer.videoepisode.view.AUIVideoEpisodeListView;
import com.alivc.player.videolist.auivideolistcommon.AUIVideoListViewModel;
import com.alivc.player.videolist.auivideolistcommon.AUIVideoListViewModelFactory;
import com.alivc.player.videolist.auivideolistcommon.listener.OnLoadDataListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AUIEpisodePlayerActivity extends AppCompatActivity {

    private AUIVideoListViewModel<AUIEpisodeData> mVideoListViewModel;

    private final AUIVideoListViewModel.DataProvider<AUIEpisodeData> dataProvider = new AUIVideoListViewModel.DataProvider<AUIEpisodeData>() {
        @Override
        public void onLoadData(AUIVideoListViewModel.DataCallback<AUIEpisodeData> callback) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (callback != null) {
                        callback.onData(loadDataFromJson());
                    }
                }
            }).start();
        }
    };

    private AUIVideoEpisodeListView mVideoEpisodeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideSystemStatusBar();

        // Android特有功能，禁止app录屏和截屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_episode_player);

        mVideoListViewModel = new ViewModelProvider(this, new AUIVideoListViewModelFactory(dataProvider)).get(AUIVideoListViewModel.class);

        initViews();
        initObserver();
    }

    private void initViews() {
        mVideoEpisodeView = findViewById(R.id.aui_episode_view);
        mVideoEpisodeView.showPlayTitleContent(false);

        // 设置短剧初始集数，默认从第1集开始
//        mVideoEpisodeView.setInitialEpisodeIndex(3);
    }

    private void initObserver() {
        mVideoListViewModel.mLoadMoreVideoListLiveData.observe(this, new Observer<AUIEpisodeData>() {
            @Override
            public void onChanged(AUIEpisodeData episodeData) {
                mVideoEpisodeView.updateEpisodeData(episodeData, false);
            }
        });

        mVideoListViewModel.mRefreshVideoListLiveData.observe(this, new Observer<AUIEpisodeData>() {
            @Override
            public void onChanged(AUIEpisodeData episodeData) {
                mVideoEpisodeView.updateEpisodeData(episodeData, true);
            }
        });

        mVideoEpisodeView.setOnRefreshListener(new OnLoadDataListener() {
            @Override
            public void onRefresh() {
                mVideoListViewModel.loadData(true);
            }

            @Override
            public void onLoadMore() {
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        mVideoEpisodeView.setOnBackground(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mVideoEpisodeView.setOnBackground(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void hideSystemStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * 网络请求
     */
    private static AUIEpisodeData loadDataFromJson() {
        Gson gson = new Gson();
        return gson.fromJson(getHtmlContent(AUIEpisodeConstants.EPISODE_JSON_URL), new TypeToken<AUIEpisodeData>() {
        }.getType());
    }

    private static String getHtmlContent(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                return convertStreamToString(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String convertStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}

