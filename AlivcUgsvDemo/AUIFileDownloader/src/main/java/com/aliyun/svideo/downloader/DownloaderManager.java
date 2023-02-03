/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.downloader;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.aliyun.ugsv.common.utils.StorageUtils;
import com.aliyun.ugsv.common.utils.StringUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.Headers;
import okhttp3.OkHttpClient;

public class DownloaderManager {

    private final static String TAG = DownloaderManager.class.getSimpleName();
    private static DownloaderManager mDownloadManager;
    private static Context mContext;
    private static FileDownloaderDBController mDbController;
    private SparseArray<FileDownloaderModel> mAllTasks = new SparseArray<>();
    private List<FileDownloadConnectListener> mConnectListenerList;
    private ListenerManager mListenerManager;

    private Queue<FileDownloaderModel> mWaitQueue;
    private List<FileDownloaderModel> mDownloadingList;

    private DownloaderManagerConfiguration mConfiguration;
    private FileDownloaderCallback mGlobalDownloadCallback;

    private Map<String, String> mExtFieldMap;
    private int mAutoRetryTimes;
    private Headers mHeaders;

    /**
     * 获取DownloadManager实例
     *
     * @return
     */
    public static DownloaderManager getInstance() {
        if (mDownloadManager == null) {
            synchronized (DownloaderManager.class) {
                if (mDownloadManager == null || (mDbController == null && mContext != null)) {
                    mDownloadManager = new DownloaderManager();
                }
            }
        }
        return mDownloadManager;
    }

    private DownloaderManager() {
        if (mDbController == null && mContext != null) {
            initDownloaderConfiger(mContext);
        }
    }

    public static synchronized void init(Context context) {
        mContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            //解决适配性问题，在api16-20的手机上https下载失败的问题
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .readTimeout(15, TimeUnit.SECONDS)
                    .connectTimeout(15, TimeUnit.SECONDS);
            try {
                SSLSocketFactory factory = new SSLSocketFactoryCompat();
                builder.sslSocketFactory(factory);
            } catch (KeyManagementException e) {
                e.printStackTrace();
                Log.e(TAG, "KeyManagementException");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.e(TAG, "NoSuchAlgorithmException");
            }
            FileDownloader.init(context, new DownloadMgrInitialParams.InitCustomMaker()
                    .connectionCreator(new OkHttp3Connection.Creator(builder)));
        } else {
            FileDownloader.init(context, new DownloadMgrInitialParams.InitCustomMaker()
                    .connectionCreator(new FileDownloadUrlConnection
                            .Creator(new FileDownloadUrlConnection.Configuration()
                            .connectTimeout(15000) // set connection timeout.
                            .readTimeout(15000) // set read timeout.
                            .proxy(Proxy.NO_PROXY) // set proxy
                    )));
        }
    }

    /**
     * 初始化DownloadManager
     */
    public synchronized void init(DownloaderManagerConfiguration configuration) {
        if (!FileDownloader.getImpl().isServiceConnected()) {
            FileDownloader.getImpl().bindService();
        }

        FileDownloadLog.NEED_LOG = true;

        this.mConfiguration = configuration;
        this.mExtFieldMap = configuration.getDbExtField();
        mDbController = new FileDownloaderDBController(configuration.getContext(), configuration.getDbVersion(),
                mExtFieldMap, configuration.getDbUpgradeListener(),configuration.iCipher(),configuration.getCk());
//        mAllTasks = mDbController.getAllTasks();
        mConnectListenerList = new ArrayList<>();
        mListenerManager = new ListenerManager();
        mAutoRetryTimes = configuration.getAutoRetryTimes();
        mHeaders = configuration.getHeaders();

        //设置下载保存目录
        if (!StringUtils.isEmpty(configuration.getDownloadStorePath())) {
            FileDownloadUtils.setDefaultSaveRootPath(configuration.getDownloadStorePath());
        }

        mWaitQueue = new LinkedList<>();
        mDownloadingList = Collections.synchronizedList(new ArrayList<FileDownloaderModel>());
        mDownloadManager = this;

    }

    /**
     * 获取扩展字段map
     *
     * @return
     */
    Map<String, String> getDbExtFieldMap() {
        return mExtFieldMap;
    }

    /**
     * 开始下载任务
     *
     * @param downloadId
     */
    public void startTask(int downloadId) {
        startTask(downloadId, null);
    }

    /**
     * 开始下载任务
     *
     * @param downloadId
     * @param callback
     */
    public void startTask(int downloadId, FileDownloaderCallback callback) {
        FileDownloaderModel model = getFileDownloaderModelById(downloadId);
        if (model != null) {
            BridgeListener bridgeListener = mListenerManager.getBridgeListener(downloadId);
            bridgeListener.addDownloadListener(callback);
            if (mDownloadingList.size() >= mConfiguration.getMaxDownloadingCount()) { //下载中队列已满
                if (!mWaitQueue.contains(model)) {
                    mWaitQueue.offer(model);
                }
                bridgeListener.wait(downloadId);
            } else {
                mDownloadingList.add(model);
                final BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
                        .setPath(model.getPath())
                        .setCallbackProgressTimes(100)
                        .setAutoRetryTimes(mAutoRetryTimes)
                        .setListener(bridgeListener);
                for (int i = 0; i < mHeaders.size(); i++) {
                    task.addHeader(mHeaders.name(i), mHeaders.value(i));
                }
                bridgeListener.setDownloadTask(task);
                task.start();
            }
        } else {
            Log.e(TAG, "Task does not exist!");
        }
    }

    public BaseDownloadTask createTask(int downloadId, FileDownloaderCallback callback) {
        FileDownloaderModel model = getFileDownloaderModelById(downloadId);
        BaseDownloadTask task = null;
        if (model != null) {
            BridgeListener bridgeListener = mListenerManager.getBridgeListener(downloadId);
            bridgeListener.addDownloadListener(callback);
            if (mDownloadingList.size() >= mConfiguration.getMaxDownloadingCount()) { //下载中队列已满
                if (!mWaitQueue.contains(model)) {
                    mWaitQueue.offer(model);
                }
                bridgeListener.wait(downloadId);
            } else {
                mDownloadingList.add(model);
                task = FileDownloader.getImpl().create(model.getUrl())
                        .setPath(model.getPath())
                        .setCallbackProgressTimes(100)
                        .setCallbackProgressMinInterval(100)
                        .setAutoRetryTimes(mAutoRetryTimes)
                        .setListener(bridgeListener);
                for (int i = 0; i < mHeaders.size(); i++) {
                    task.addHeader(mHeaders.name(i), mHeaders.value(i));
                }
                bridgeListener.setDownloadTask(task);
            }
        } else {
            Log.e(TAG, "Task does not exist!");
        }
        return task;
    }

    public <T extends FileDownloadListener> void startTaskExtend(int downloadId, T listener) {
        FileDownloaderModel model = getFileDownloaderModelById(downloadId);
        if (model != null) {
            final BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
                    .setPath(model.getPath())
                    .setCallbackProgressTimes(100)
                    .setAutoRetryTimes(mAutoRetryTimes)
                    .setListener(listener);
            for (int i = 0; i < mHeaders.size(); i++) {
                task.addHeader(mHeaders.name(i), mHeaders.value(i));
            }

        } else {
            Log.e(TAG, "Task does not exist!");
        }

    }

    /**
     * 删除一个任务
     *
     * @param downloadId
     */
    public void deleteTaskByTaskId(int downloadId) {
        if (mDbController.deleteTask(downloadId)) {
//            FileDownloaderModel model = getFileDownloaderModelById(downloadId);
//            if (model != null) {//删除文件
//                new File(model.getPath()).delete();
//            }
            pauseTask(downloadId);
            removeDownloadingTask(downloadId);
            removeWaitQueueTask(downloadId);
            try {
                mAllTasks.remove(downloadId);
            } catch (Exception e) {
            }
        } else {
            Log.e(TAG, "delete failure");
        }
    }

    /**
     * 删除数据库及资源
     */
    public void deleteTask(int id) {
        mDbController.deleteTaskById(id, false);
    }

    /**
     * 添加下载监听
     *
     * @param downloadId
     * @param listener
     */
    public void addFileDownloadListener(int downloadId, FileDownloaderCallback listener) {
        BridgeListener bridgeListener = mListenerManager.getBridgeListener(downloadId);
        bridgeListener.addDownloadListener(listener);
    }

    /**
     * 下一个任务
     *
     * @return
     */
    protected synchronized FileDownloaderModel nextTask() {
        return mWaitQueue.poll();
    }

    /**
     * 将一个下载中的任务从下载中队列移除
     *
     * @param downloadId
     */
    protected synchronized void removeDownloadingTask(int downloadId) {
        Iterator<FileDownloaderModel> iterator = mDownloadingList.iterator();
        while (iterator.hasNext()) {
            FileDownloaderModel model = iterator.next();
            if (model != null && model.getTaskId() == downloadId) {
                try {
                    iterator.remove();
                } catch (Exception e) {
                }
                return;
            }
        }
    }

    /**
     * 将一个等待中的任务从下等待队列中移除
     *
     * @param downloadId
     */
    protected synchronized void removeWaitQueueTask(int downloadId) {
        Iterator<FileDownloaderModel> iterator = mWaitQueue.iterator();
        while (iterator.hasNext()) {
            FileDownloaderModel model = iterator.next();
            if (model != null && model.getTaskId() == downloadId) {
                try {
                    iterator.remove();
                } catch (Exception e) {
                }
                return;
            }
        }
    }

    /**
     * 暂停任务
     *
     * @param downloadId
     */
    public synchronized void pauseTask(int downloadId) {
        if (isWaiting(downloadId)) {
            BridgeListener bridgeListener = mListenerManager.getBridgeListener(downloadId);
            removeWaitQueueTask(downloadId);
            bridgeListener.stop(downloadId, getSoFar(downloadId), getTotal(downloadId));
        } else {
            FileDownloader.getImpl().pause(downloadId);
        }
    }

    /**
     * 停止所有任务
     */
    public void pauseAllTask() {
        FileDownloader.getImpl().pauseAll();
    }

    /**
     * 根据任务ID获取任务
     *
     * @param downloadId
     * @return
     */
    public BaseDownloadTask getDownloadTaskById(int downloadId) {
        BridgeListener bridgeListener = mListenerManager.getBridgeListener(downloadId);
        return bridgeListener.getDownloadTask();
    }

    /**
     * 添加service连接监听
     *
     * @param listener
     */
    public void addServiceConnectListener(FileDownloadConnectListener listener) {
        FileDownloader.getImpl().addServiceConnectListener(listener);
    }

    /**
     * 移除sevice连接监听
     *
     * @param listener
     */
    public void removeServiceConnectListener(FileDownloadConnectListener listener) {
        FileDownloader.getImpl().removeServiceConnectListener(listener);
    }

    /**
     * 释放下载管理器
     */
    public void onDestroy() {
        try {
            mConnectListenerList.clear();
            pauseAllTask();
            FileDownloader.getImpl().unBindServiceIfIdle();
        } catch (Exception e) {
        }
    }

    /**
     * 获取service是否已连接
     *
     * @return
     */
    public boolean isReady() {
        return FileDownloader.getImpl().isServiceConnected();
    }

    /**
     * 根据索引获取下载信息
     *
     * @param position
     * @return
     */
    public FileDownloaderModel getFileDownloaderModelByPostion(final int position) {
        int id = mAllTasks.keyAt(position);
        return getFileDownloaderModelById(id);
    }

    /**
     * 根据URL获取下载信息
     *
     * @param url
     * @return
     */
    public FileDownloaderModel getFileDownloaderModelByUrl(String url) {
        for (int i = 0; i < mAllTasks.size(); i++) {
            FileDownloaderModel model = getFileDownloaderModelByPostion(i);
            if (model != null && TextUtils.equals(model.getUrl(), url)) {
                return model;
            }
        }

        return null;
    }


    /**
     * 根据downloadId获取下载信息
     *
     * @param downloadId
     * @return
     */
    public FileDownloaderModel getFileDownloaderModelById(final int downloadId) {
        if (mAllTasks != null && mAllTasks.size() > 0) {
            return mAllTasks.get(downloadId);
        }
        return null;
    }

    /**
     * 是否下载完成
     *
     * @param downloadId
     * @return
     */
    public boolean isFinish(int downloadId, String path) {
        FileDownloaderModel model = getFileDownloaderModelById(downloadId);
        if (model != null) {
            if (getStatus(downloadId, path) == FileDownloadStatus.completed && new File(model.getPath()).exists()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否在等待队列
     *
     * @param downloadId
     * @return
     */
    public boolean isWaiting(int downloadId) {
        FileDownloaderModel model = new FileDownloaderModel();
        model.setTaskId(downloadId);
        return mWaitQueue.contains(model);
    }

    /**
     * 判断一个任务是否在下载中
     *
     * @param downloadId
     * @return
     */
    public boolean isDownloading(final int downloadId, String path) {
        int status = getStatus(downloadId, path);
        switch (status) {
            case FileDownloadStatus.pending:
            case FileDownloadStatus.connected:
            case FileDownloadStatus.progress:
                return true;
            default:
                return false;
        }
    }

    /**
     * 判断任务是否存在
     *
     * @param url
     * @return
     */
    public boolean exists(String url) {
        FileDownloaderModel model = getFileDownloaderModelByUrl(url);
        if (model != null) {
            return true;
        }

        return false;
    }

    /**
     * 获取FileDownloader FileDownloadStatus下载状态
     *
     * @param downloadId
     * @return
     */
    public int getStatus(final int downloadId, String path) {
        return FileDownloader.getImpl().getStatus(downloadId, path);
    }

    /**
     * 根据downloadId获取文件总大小
     *
     * @param downloadId
     * @return
     */
    public long getTotal(final int downloadId) {
        return FileDownloader.getImpl().getTotal(downloadId);
    }

    /**
     * 根据downloadId获取文件已下载大小
     *
     * @param downloadId
     * @return
     */
    public long getSoFar(final int downloadId) {
        return FileDownloader.getImpl().getSoFar(downloadId);
    }

    /**
     * 获取下载速度
     *
     * @param downloadId
     * @return
     */
    public long getSpeed(final int downloadId) {
        BridgeListener bridgeListener = mListenerManager.getBridgeListener(downloadId);
        return bridgeListener.getSpeed();
    }

    /**
     * 根据downloadId获取文件下载进度
     *
     * @param downloadId
     * @return
     */
    public int getProgress(int downloadId) {
        FileDownloaderModel model = getFileDownloaderModelById(downloadId);
        int progress = 0;
        if (model != null) {
            if (!new File(model.getPath()).exists()) {
                return progress;
            }
        }

        long totalBytes = getTotal(downloadId);
        long soFarBytes = getSoFar(downloadId);

        if (totalBytes != 0) {
            progress = (int) (soFarBytes / (float) totalBytes * 100);
        }

        return progress;
    }

    public List<FileDownloaderModel> getAllTask() {
        List<FileDownloaderModel> allTask = new ArrayList<>();
        for (int i = 0; i < mAllTasks.size(); i++) {
            allTask.add(mAllTasks.valueAt(i));
        }
        return allTask;
    }

    /**
     * 获取所任务数
     *
     * @return
     */
    public int getTaskCounts() {
        if (mAllTasks == null) {
            return 0;
        }
        return mAllTasks.size();
    }

    /**
     * 添加一个任务
     * 注：同样的URL，保存的目录不一样表示这两次addTask是不同的任务
     *
     * @param url
     * @return
     */
    private FileDownloaderModel addTask(final String url) {
        FileDownloaderModel downloaderModel = new FileDownloaderModel();
        downloaderModel.setUrl(url);
        downloaderModel.setPath(createPath(url));
        return addTask(downloaderModel, url);
    }

    /**
     * 添加一个任务
     * 注：同样的URL，保存的目录不一样表示这两次addTask是不同的任务
     *
     * @param url
     * @param path
     * @return
     */
    public FileDownloaderModel addTask(final String url, String path) {
        FileDownloaderModel downloaderModel = new FileDownloaderModel();
        downloaderModel.setUrl(url);
        downloaderModel.setPath(path);
        return addTask(downloaderModel, url);
    }


    /**
     * 添加一个任务
     * 注：同样的URL，保存的目录不一样表示这两次addTask是不同的任务
     *
     * @param downloaderModel
     * @return
     */
    public FileDownloaderModel addTask(FileDownloaderModel downloaderModel, String url) {
        String path = downloaderModel.getPath();

        if (TextUtils.isEmpty(url)) {
            return null;
        }
        if (TextUtils.isEmpty(path)) {
            String subDirPath = "";
            switch (downloaderModel.getEffectType()) {
                case FileDownloaderModel.EFFECT_MV:
                    subDirPath = FileDownloaderModel.MV_DIR;
                    break;
                case FileDownloaderModel.EFFECT_TEXT:
                    subDirPath = FileDownloaderModel.FONT_DIR;
                    break;
                case FileDownloaderModel.EFFECT_PASTER:
                    subDirPath = FileDownloaderModel.STICKER_DIR;
                    break;
                case FileDownloaderModel.EFFECT_CAPTION:
                    subDirPath = FileDownloaderModel.CAPTION_DIR;
                    break;
                case FileDownloaderModel.EFFECT_TRANSITION:
                    subDirPath = FileDownloaderModel.TRANSITION_DIR;
                    break;
                case FileDownloaderModel.EFFECT_ANIMATION_FILTER:
                    subDirPath = FileDownloaderModel.ANIMATION_EFFECTS_DIR;
                    break;
            }
            switch (downloaderModel.getEffectType()) {
                case FileDownloaderModel.EFFECT_MV:
                    subDirPath = subDirPath + File.separator + downloaderModel.getId() + "-" + downloaderModel.getName() + File.separator + downloaderModel.getAspect();
                    break;
                case FileDownloaderModel.EFFECT_PASTER:
                case FileDownloaderModel.EFFECT_CAPTION:
                    subDirPath = subDirPath + File.separator + downloaderModel.getId() + "-" + downloaderModel.getName()
                            + File.separator + downloaderModel.getSubid() + "-" + downloaderModel.getSubname();
                    break;
                case FileDownloaderModel.EFFECT_TEXT:
                case FileDownloaderModel.EFFECT_TRANSITION:
                case FileDownloaderModel.EFFECT_ANIMATION_FILTER:
                    subDirPath = subDirPath + File.separator + downloaderModel.getId() + "-" + downloaderModel.getName();
                    break;
                default:
                    path = createPath(url);
            }
            if (!StringUtils.isEmpty(subDirPath)) {
                path = FileDownloadUtils.generateFilePath(FileDownloadUtils.getDefaultSaveRootPath(), subDirPath);
            }
            downloaderModel.setPath(path);
        }

        final int id = FileDownloadUtils.generateId(url, path);
//        FileDownloaderModel model = getFileDownloaderModelById(id);
//        if (model != null) {
//            return model;
//        }
        downloaderModel.setTaskId(id);
        //去除添加数据库操作，等下载完成后插入数据库
//        model = mDbController.addTask(downloaderModel);
        mAllTasks.put(id, downloaderModel);

        return downloaderModel;
    }

    /**
     * 添加任务并启动
     *
     * @param url
     * @return
     */
    public FileDownloaderModel addTaskAndStart(String url) {
        FileDownloaderModel model = addTask(url);
        startTask(model.getTaskId());
        return model;
    }

    /**
     * 添加任务并启动
     *
     * @param url
     * @param path
     * @return
     */
    public FileDownloaderModel addTaskAndStart(final String url, String path) {
        FileDownloaderModel model = addTask(url, path);
        startTask(model.getTaskId());
        return model;
    }

    /**
     * 串行下载
     */
    public void startTask(List<FileDownloaderModel> models, FileDownloaderCallback callback) {

        BridgeListener bridgeListener = new BridgeListener();
        bridgeListener.addDownloadListener(callback);

        for (FileDownloaderModel model : models) {
            createTask(addTask(model, model.getUrl()), bridgeListener);
        }

        FileDownloader.getImpl().start(bridgeListener, true);
//        FileDownloadQueueSet fileDownloadQueueSet = new FileDownloadQueueSet();
//        fileDownloadQueueSet.downloadSequentially()
    }


    public int createTask(FileDownloaderModel model, BridgeListener bridgeListener) {

        final int task = FileDownloader.getImpl().create(model.getUrl())
                .setPath(model.getPath())
                .setCallbackProgressTimes(100)
                .setAutoRetryTimes(mAutoRetryTimes)
                .setListener(bridgeListener)
                .asInQueueTask()
                .enqueue();

        return task;
    }

//    /**
//     * 添加任务并启动
//     * @param downloaderModel
//     * @return
//     */
//    public FileDownloaderModel addTaskAndStart(FileDownloaderModel downloaderModel) {
//        FileDownloaderModel model = addTask(downloaderModel);
//        startTask(model.getTaskId());
//        return model;
//    }

    /**
     * 创建下载保存地址
     *
     * @param url
     * @return
     */
    private String createPath(final String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        return FileDownloadUtils.getDefaultSaveFilePath(url);
    }

    /**
     * 设置全局下载事件监听
     *
     * @param callback
     */
    public void setGlobalDownloadCallback(FileDownloaderCallback callback) {
        this.mGlobalDownloadCallback = callback;
    }

    /**
     * 获取全局下载事件监听
     *
     * @return
     */
    protected FileDownloaderCallback getGlobalDownloadCallback() {
        return this.mGlobalDownloadCallback;
    }

    public FileDownloaderDBController getDbController() {
        if (mDbController == null && mContext != null) {
            initDownloaderConfiger(mContext);
        }
        return mDbController;
    }

    private void initDownloaderConfiger(Context context) {
        File storeFile = StorageUtils.getFilesDirectory(context);
        if (!storeFile.exists()) {
            storeFile.mkdirs();
        }
        final DownloaderManagerConfiguration.Builder dmBulder = new DownloaderManagerConfiguration.Builder(context)
                .setMaxDownloadingCount(50) //配置最大并行下载任务数，配置范围[1-100]
                .setDbExtField(new HashMap<String, String>()) //配置数据库扩展字段
                .setDbVersion(DBFileDownloadConfig.DB_VERSION)//配置数据库版本
                .setDbUpgradeListener(null) //配置数据库更新回调
                .setDownloadStorePath(storeFile.getAbsolutePath()); //配置下载文件存储目录
//                .setCipher(true)//使用加密数据库
//                .setCK("6Zi/6YeM55+t6KeG6aKR");

        if (mDbController == null) {
            init(dmBulder.build());//必要语句
        }
    }

}
