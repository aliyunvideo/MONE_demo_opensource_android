/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.downloader;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDownloaderDBController {
    protected static final String TABLE_NAME = "FileDownloader";

    private FileDownloaderDBGeneralController mFileDownloaderDBGeneralController = null;
    private FileDownloaderDBCipherController mFileDownloaderDBCipherController = null;

    public FileDownloaderDBController(Context context, int dbVersion, Map<String, String> dbExtFieldMap, DbUpgradeListener dbUpgradeListener, boolean isCipher, String ck) {
        if (isCipher && !TextUtils.isEmpty(ck)) {
            mFileDownloaderDBCipherController = new FileDownloaderDBCipherController(new FileDownloaderDBCipherOpenHelper(context, dbVersion, dbExtFieldMap, dbUpgradeListener), TABLE_NAME, ck);
        } else {
            mFileDownloaderDBGeneralController = new FileDownloaderDBGeneralController(new FileDownloaderDBOpenHelper(context, dbVersion, dbExtFieldMap, dbUpgradeListener),
                    TABLE_NAME);
            Log.d(TABLE_NAME, "FileDownloaderDBController: " + isCipher + TextUtils.isEmpty(ck));
        }
    }

    /**
     * 从数据库中读取所有下载任务
     *
     * @return
     */
    public SparseArray<FileDownloaderModel> getAllTasks() {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.getAllTasks();
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.getAllTasks();
        }
        return null;

    }

    /**
     * 添加一个任务，保存到数据库
     *
     * @param downloaderModel
     * @return
     */
    public synchronized FileDownloaderModel addTask(FileDownloaderModel downloaderModel) {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.addTask(downloaderModel);
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.addTask(downloaderModel);
        }
        return null;

    }

    public synchronized boolean insertDb(FileDownloaderModel model, HashMap<String, String> hashMap) {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.insertDb(model, hashMap);
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.insertDb(model, hashMap);
        }
        return false;
    }

    /**
     * 删除数据库中的一条任务信息
     *
     * @param downloadId
     * @return
     */
    public synchronized boolean deleteTask(final int downloadId) {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.deleteTask(downloadId);
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.deleteTask(downloadId);
        }
        return false;
    }

    /**
     * 删除数据库中的一条任务信息
     *
     * @param id
     * @return
     */
    public synchronized boolean deleteTaskById(final int id) {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.deleteTaskById(id);
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.deleteTaskById(id);
        }
        return false;

    }

    public synchronized boolean deleteTaskById(final int id, boolean isParent) {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.deleteTaskById(id, isParent);
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.deleteTaskById(id, isParent);
        }
        return false;
    }

    public synchronized boolean checkExits(int id, int type) {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.checkExits(id, type);
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.checkExits(id, type);
        }
        return false;
    }

    public synchronized boolean checkExits(int taskId) {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.checkExits(taskId);
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.checkExits(taskId);
        }
        return false;
    }

    public synchronized String getPathByUrl(String url) {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.getPathByUrl(url);
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.getPathByUrl(url);
        }
        return null;
    }

    /**
     * 查询
     *
     * @param hashMap key - value
     * @return List<FileDownloaderModel>
     */
    public synchronized List<FileDownloaderModel> getResourceByFiled(HashMap<String, String> hashMap) {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.getResourceByFiled(hashMap);
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.getResourceByFiled(hashMap);
        }
        return null;
    }

    public synchronized List<String> getPath(int id) {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.getPath(id);
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.getPath(id);
        }
        return null;
    }

    public synchronized Cursor getResourceColumns(HashMap<String, String> hashMap, List<String> colums) {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.getResourceColumns(hashMap, colums);
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.getResourceColumns(hashMap, colums);
        }
        return null;
    }

    public synchronized Cursor getResourceById(int id) {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.getResourceById(id);
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.getResourceById(id);
        }
        return null;
    }

    /**
     * 通过type来获取数据库资源
     *
     * @param type { EffectService }
     * @return List<FileDownloaderModel>
     */
    public synchronized List<FileDownloaderModel> getResourceByType(int type) {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.getResourceByType(type);
        } else if (mFileDownloaderDBGeneralController != null) {
            return mFileDownloaderDBGeneralController.getResourceByType(type);
        }
        return null;
    }

    private FileDownloaderModel addCursor2Data(Cursor c) {
        String url = c.getString(c.getColumnIndex(FileDownloaderModel.URL));
        int taskId = c.getInt(c.getColumnIndex(FileDownloaderModel.TASK_ID));
        int id = c.getInt(c.getColumnIndex(FileDownloaderModel.ID));
        String name = c.getString(c.getColumnIndex(FileDownloaderModel.NAME));
        String nameEn = c.getString(c.getColumnIndex(FileDownloaderModel.NAME_EN));
        String path = c.getString(c.getColumnIndex(FileDownloaderModel.PATH));
        int isunzip = c.getInt(c.getColumnIndex(FileDownloaderModel.ISUNZIP));
        int effectType = c.getInt(c.getColumnIndex(FileDownloaderModel.EFFECTTYPE));
//        int subqffectype = c.getInt(c.getColumnIndex(FileDownloaderModel.SUBEFFECTYPT));
        String key = c.getString(c.getColumnIndex(FileDownloaderModel.KEY));
        int level = c.getInt(c.getColumnIndex(FileDownloaderModel.LEVEL));
        String tag = c.getString(c.getColumnIndex(FileDownloaderModel.TAG));
        int cat = c.getInt(c.getColumnIndex(FileDownloaderModel.CAT));
        String previewpic = c.getString(c.getColumnIndex(FileDownloaderModel.PREVIEWPIC));
        String previewmp4 = c.getString(c.getColumnIndex(FileDownloaderModel.PREVIEWMP4));
        long duration = c.getLong(c.getColumnIndex(FileDownloaderModel.DURATION));
//        int type = c.getInt(c.getColumnIndex(FileDownloaderModel.TYPE));
        int sort = c.getInt(c.getColumnIndex(FileDownloaderModel.SORT));
        int aspect = c.getInt(c.getColumnIndex(FileDownloaderModel.ASPECT));
        String download = c.getString(c.getColumnIndex(FileDownloaderModel.DOWNLOAD));
        String md5 = c.getString(c.getColumnIndex(FileDownloaderModel.MD5));
        String cnname = c.getString(c.getColumnIndex(FileDownloaderModel.CNNAME));
        int category = c.getInt(c.getColumnIndex(FileDownloaderModel.CATEGORY));
        String banner = c.getString(c.getColumnIndex(FileDownloaderModel.BANNER));
        String icon = c.getString(c.getColumnIndex(FileDownloaderModel.ICON));
        String description = c.getString(c.getColumnIndex(FileDownloaderModel.DESCRIPTION));
        int isnew = c.getInt(c.getColumnIndex(FileDownloaderModel.ISNEW));
        String preview = c.getString(c.getColumnIndex(FileDownloaderModel.PREVIEW));
        int subid = c.getInt(c.getColumnIndex(FileDownloaderModel.SUBID));
        int fontid = c.getInt(c.getColumnIndex(FileDownloaderModel.FONTID));
        String subicon = c.getString(c.getColumnIndex(FileDownloaderModel.SUBICON));
        String subname = c.getString(c.getColumnIndex(FileDownloaderModel.SUBNAME));
        int priority = c.getInt(c.getColumnIndex(FileDownloaderModel.PRIORITY));
        String subpreview = c.getString(c.getColumnIndex(FileDownloaderModel.SUBPREVIEW));
        int subsort = c.getInt(c.getColumnIndex(FileDownloaderModel.SUBSORT));
        int subtype = c.getInt(c.getColumnIndex(FileDownloaderModel.SUBTYPE));

        FileDownloaderModel model = new FileDownloaderModel();

        model.setTaskId(taskId);
        model.setId(id);
        model.setName(name);
        model.setNameEn(nameEn);
        model.setUrl(url);
        model.setPath(path);
        model.setIsunzip(isunzip);
        model.setEffectType(effectType);
        model.setKey(key);
        model.setLevel(level);
        model.setTag(tag);
        model.setCat(cat);
        model.setPreviewpic(previewpic);
        model.setPreviewmp4(previewmp4);
        model.setDuration(duration);
        model.setSubtype(subtype);
        model.setSort(sort);
        model.setAspect(aspect);
        model.setDownload(download);
        model.setMd5(md5);
        model.setCnname(cnname);
        model.setCategory(category);
        model.setBanner(banner);
        model.setIcon(icon);
        model.setDescription(description);
        model.setIsnew(isnew);
        model.setPreview(preview);
        model.setSubid(subid);
        model.setFontid(fontid);
        model.setSubicon(subicon);
        model.setSubname(subname);
        model.setPriority(priority);
        model.setSubpreview(subpreview);
        model.setSubsort(subsort);
        model.parseExtField(c);
        return model;
    }

    private String listToString(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                stringBuilder.append(list.get(i));
                break;
            }
            stringBuilder.append(list.get(i) + ",");
        }
        return stringBuilder.toString();
    }

    public FileDownloaderDBCipherOpenHelper getDBCipherOpenHelper() {
        if (mFileDownloaderDBCipherController != null) {
            return mFileDownloaderDBCipherController.getDBHelper();
        }
        return null;
    }
}
