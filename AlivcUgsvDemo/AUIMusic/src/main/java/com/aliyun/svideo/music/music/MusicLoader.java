package com.aliyun.svideo.music.music;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aliyun.svideo.music.R;
import com.aliyun.ugsv.common.utils.CommonUtil;
import com.aliyun.ugsv.common.utils.StorageUtils;
import com.aliyun.ugsv.common.utils.ToastUtil;
import com.aliyun.svideo.downloader.DownloaderManager;
import com.aliyun.svideo.downloader.FileDownloaderCallback;
import com.aliyun.svideo.downloader.FileDownloaderModel;
import com.aliyun.svideo.base.http.EffectService;
import com.aliyun.svideo.base.http.HttpCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liulishuo.filedownloader.BaseDownloadTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MusicLoader {
    public static final int EFFECT_MUSIC = 5;       //音乐

    private final MusicQuery mMusicQuery;
    private LoadCallback callback;
    private final Context mContext;
    public EffectService mService = new EffectService();
    private Gson mGson = new GsonBuilder().disableHtmlEscaping().create();

    public void loadAllMusic() {
        loadLocalMusic();
        loadMoreOnlineMusic();

    }

    public MusicLoader(Context context) {
        mContext = context;
        mMusicQuery = new MusicQuery(context);
    }

    /**
     * 加载本地音乐
     */
    public void loadLocalMusic() {
        mMusicQuery.setOnResProgressListener(new MusicQuery.OnResProgressListener() {
            @Override
            public void onResProgress(ArrayList<MusicFileBean> musics) {
                List<EffectBody<MusicFileBean>> effectBodyList = new ArrayList<>();
                for (MusicFileBean musicFileBean : musics) {
                    effectBodyList.add(new EffectBody<MusicFileBean>(musicFileBean, true));
                }
                if (callback != null) {
                    callback.onLoadLocalMusicCompleted(effectBodyList);
                }
            }
        });
        mMusicQuery.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
    private int pageSize = 25;
    private int pageNo = 1;
    /**
     * 是否正在加载网络音乐
     */
    private boolean isLoadingMusic;
    private boolean isMusicEnd;
    /**
     * 搜索音乐
     */
    public void searchOnlineMusic(String keyWord) {
        loadOnlineMusic(1, 25, keyWord);

    }
    public void loadMoreOnlineMusic() {
        if (isLoadingMusic || isMusicEnd) {
            return;
        }
        loadOnlineMusic(pageNo, pageSize, "");
    }
    /**
     *加载网络音乐
     */
    private void loadOnlineMusic(int pageNo, final int pageSize, final String keyWord) {
        if (!CommonUtil.hasNetwork(mContext)) {
            ToastUtil.showToast(mContext, R.string.alivc_music_network_not_connect);
            return;
        }
        isLoadingMusic = true;
        loadingMusicData(mContext, mContext.getPackageName(), pageNo, pageSize, keyWord, new HttpCallback<List<MusicFileBean>>() {
            @Override
            public void onSuccess(List<MusicFileBean> result) {
                isLoadingMusic = false;
                if (TextUtils.isEmpty(keyWord) && result.size() < pageSize) {
                    isMusicEnd = true;
                }
                List<EffectBody<MusicFileBean>> effectBodyList = new ArrayList<>();
                List<FileDownloaderModel> modelsTemp = DownloaderManager.getInstance().getDbController().getResourceByType(EFFECT_MUSIC);
                for (MusicFileBean musicFileBean : result) {
                    EffectBody<MusicFileBean> effectBody = new EffectBody<>(musicFileBean, false);
                    for (FileDownloaderModel fileDownloaderModel : modelsTemp) {
                        if (musicFileBean.getMusicId().equals(fileDownloaderModel.getDownload()) && new File(fileDownloaderModel.getPath()).exists()) {
                            musicFileBean.setPath(fileDownloaderModel.getPath());
                            effectBody.setLocal(true);
                        }
                    }
                    effectBodyList.add(effectBody);
                }
                if (callback != null) {
                    if (TextUtils.isEmpty(keyWord)) {
                        callback.onLoadNetMusicCompleted(effectBodyList);
                    } else {
                        callback.onSearchNetMusicCompleted(effectBodyList);
                    }

                }
                MusicLoader.this.pageNo++;
                MusicLoader.this.pageSize++;
            }

            @Override
            public void onFailure(Throwable e) {
                isLoadingMusic = false;
            }
        });
    }

    /**
     * 素材分发服务为官方demo演示使用，无法达到商业化使用程度。请自行搭建相关的服务
     */
    public void loadingMusicData(Context context, String packageName, int pageNo, int pageSize, String keyWord, final HttpCallback<List<MusicFileBean>> callback) {
        try {
            InputStreamReader is = new InputStreamReader(context.getAssets().open("musicList"));
            BufferedReader bufferedReader = new BufferedReader(is);
            String line = "";
            StringBuilder result = new StringBuilder();
            while((line = bufferedReader.readLine()) != null){
                result.append(line);
            }
            JSONObject jsonObject = new JSONObject(result.toString());
            JSONObject dataObject = jsonObject.getJSONObject("data");
            JSONArray jsonArray = dataObject.getJSONArray("musicList");
            List<MusicBean> resourceList = mGson.fromJson(jsonArray.toString(), new TypeToken<List<MusicBean>>() {
            } .getType());
            if (callback != null) {
                List<MusicFileBean> musicFileBeanList = new ArrayList<>();
                for (MusicBean musicBean : resourceList) {
                    MusicFileBean fileBean = new MusicFileBean(
                        musicBean.getTitle(),
                        musicBean.getArtistName(),
                        musicBean.getMusicId(),
                        musicBean.getImage()
                    );
                    fileBean.setDuration(Integer.valueOf(musicBean.getDuration()));
                    fileBean.setPath(musicBean.getSource());
                    musicFileBeanList.add(fileBean);
                }
                callback.onSuccess(musicFileBeanList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(e);
            }
        }
    }


    /**
     * 下载音乐
     * @param musicFileBean
     * @param callback
     */
    public void downloadMusic(final MusicFileBean musicFileBean, final FileDownloaderCallback callback) {

        if (!CommonUtil.hasNetwork(mContext)) {
            ToastUtil.showToast(mContext, R.string.alivc_music_network_not_connect);
            return;
        }
        if (CommonUtil.SDFreeSize() < 10 * 1000 * 1000) {
            Toast.makeText(mContext, R.string.alivc_music_no_free_memory, Toast.LENGTH_SHORT).show();
            return;
        }
        mService.getMusicDownloadUrlById(mContext.getPackageName(), musicFileBean.musicId, new HttpCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String url = result;
                if (TextUtils.isEmpty(url)) {
                    ToastUtil.showToast(mContext,  mContext.getResources().getString(R.string.alivc_music_play_url_null));
                    return;
                }
                final FileDownloaderModel downloaderModel = new FileDownloaderModel();
                downloaderModel.setUrl(url);
                downloaderModel.setDownload(musicFileBean.getMusicId());
                downloaderModel.setName(musicFileBean.title);
                downloaderModel.setIsunzip(0);
                downloaderModel.setDuration(musicFileBean.duration);
                downloaderModel.setPath(StorageUtils.getFilesDirectory(mContext) + "/svideo_res/cloud/music/" + musicFileBean.title);
                downloaderModel.setDescription(musicFileBean.artist);
                downloaderModel.setEffectType(EFFECT_MUSIC);
                if (downloaderModel == null) {
                    Log.e("MusicLoader", "downloaderModel is null" );
                }
                final FileDownloaderModel model = DownloaderManager.getInstance().addTask(downloaderModel, url);
                if (model == null) {
                    Log.e("MusicLoader", "model is null" );
                }
                if (DownloaderManager.getInstance().isDownloading(model.getTaskId(), model.getPath())) {
                    return;
                }
                DownloaderManager.getInstance().startTask(model.getTaskId(), new FileDownloaderCallback() {
                    @Override
                    public void onFinish(int downloadId, String path) {
                        callback.onFinish(downloadId, path);
                    }

                    @Override
                    public void onStart(int downloadId, long soFarBytes, long totalBytes, int preProgress) {
                        callback.onStart(downloadId, soFarBytes, totalBytes, preProgress);
                    }

                    @Override
                    public void onProgress(int downloadId, long soFarBytes, long totalBytes, long speed,
                                           int progress) {
                        callback.onProgress(downloadId, soFarBytes, totalBytes, speed, progress);
                    }

                    @Override
                    public void onError(BaseDownloadTask task, Throwable e) {
                        ToastUtil.showToast(mContext, e.getMessage());
                        DownloaderManager.getInstance().deleteTaskByTaskId(model.getTaskId());
                        DownloaderManager.getInstance().getDbController().deleteTask(model.getTaskId());
                        callback.onError(task, e );
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {

            }
        });

    }

    public interface LoadCallback {
        void onLoadLocalMusicCompleted(List<EffectBody<MusicFileBean>> loacalMusic);
        void onLoadNetMusicCompleted(List<EffectBody<MusicFileBean>> netMusic);
        void onSearchNetMusicCompleted(List<EffectBody<MusicFileBean>> result);

    }

    public void setCallback(LoadCallback callback) {
        this.callback = callback;
    }
}
