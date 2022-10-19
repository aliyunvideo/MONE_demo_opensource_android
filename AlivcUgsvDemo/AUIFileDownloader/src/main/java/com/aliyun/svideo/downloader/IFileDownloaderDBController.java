package com.aliyun.svideo.downloader;

import android.database.Cursor;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.List;

public interface IFileDownloaderDBController {
    SparseArray<FileDownloaderModel> getAllTasks();

    FileDownloaderModel addTask(FileDownloaderModel downloaderModel);

    boolean insertDb(FileDownloaderModel model, HashMap<String, String> hashMap);

    boolean deleteTask(final int downloadId);

    boolean deleteTaskById(final int id);

    boolean checkExits(int id, int type);

    boolean checkExits(int taskId);

    String getPathByUrl(String url);

    List<FileDownloaderModel> getResourceByFiled(HashMap<String, String> hashMap);

    List<String> getPath(int id) ;

    Cursor getResourceColumns(HashMap<String, String> hashMap, List<String> colums) ;

    Cursor getResourceById(int id) ;

    List<FileDownloaderModel> getResourceByType(int type);


}
