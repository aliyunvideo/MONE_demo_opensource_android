package com.aliyun.svideo.downloader;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import com.aliyun.ugsv.common.utils.FileUtils;
import com.aliyun.ugsv.common.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FileDownloaderDBGeneralController implements   IFileDownloaderDBController{
    private FileDownloaderDBOpenHelper mDBHelper;
    private String mTableName ;

    public FileDownloaderDBGeneralController(FileDownloaderDBOpenHelper dBHelper, String tableName) {
        mDBHelper = dBHelper;
        mTableName = tableName;
    }

    /**
     * 从数据库中读取所有下载任务
     *
     * @return
     */
    public SparseArray<FileDownloaderModel> getAllTasks() {
        SQLiteDatabase sqliteDatabase = mDBHelper.getReadableDatabase();
        final Cursor c = sqliteDatabase.rawQuery("SELECT * FROM " + mTableName, null);

        final SparseArray<FileDownloaderModel> tasksMap = new SparseArray<>();
        try {
            while (c.moveToNext()) {
                int taskId = c.getInt(c.getColumnIndex(FileDownloaderModel.TASK_ID));
                FileDownloaderModel model = addCursor2Data(c);
                tasksMap.put(taskId, model);
            }
        } catch (Exception e) {

        } finally {
            if (c != null) {
                c.close();
            }

            if (sqliteDatabase.isOpen()) {
                sqliteDatabase.close();
            }
        }

        return tasksMap;
    }

    /**
     * 添加一个任务，保存到数据库
     *
     * @param downloaderModel
     * @return
     */
    public synchronized FileDownloaderModel addTask(FileDownloaderModel downloaderModel) {
        String path = downloaderModel.getPath();
        if (StringUtils.isEmpty(path)) {
            return null;
        }

        final int taskId = downloaderModel.getTaskId();
        SQLiteDatabase sqliteDatabase = mDBHelper.getWritableDatabase();
        boolean succeed = false;
        if (sqliteDatabase.isOpen()) {
            try {
                if (!checkExits(taskId)) {
                    succeed = sqliteDatabase.insert(mTableName, null, downloaderModel.toContentValues()) != -1;
                } else {
                    succeed = sqliteDatabase.update(mTableName, downloaderModel.toContentValues(), "task_id = ?", new String[]{String.valueOf(taskId)}) != -1;
                }

            } catch (Exception e) {
            }
        }
        try {
            sqliteDatabase.close();
        } catch (SQLException e) {
        }

        return succeed ? downloaderModel : null;
    }

    public synchronized boolean insertDb(FileDownloaderModel model, HashMap<String, String> hashMap) {
        SQLiteDatabase sqliteDatabase = mDBHelper.getWritableDatabase();
        boolean succeed = false;
        //查询是否已经存在
        List<FileDownloaderModel> list = getResourceByFiled(hashMap);
        if (sqliteDatabase.isOpen()) {
            try {
                if (list.size() == 0) {
                    succeed = sqliteDatabase.insert(mTableName, null, model.toContentValues()) != -1;
                }
            } catch (Exception e) {
            }
        }
        try {
            sqliteDatabase.close();
        } catch (SQLException e) {
        }
        return succeed;
    }

    /**
     * 删除数据库中的一条任务信息
     *
     * @param downloadId
     * @return
     */
    public synchronized boolean deleteTask(final int downloadId) {
        String[] args = {String.valueOf(downloadId)};
        SQLiteDatabase sqliteDatabase = mDBHelper.getWritableDatabase();
        boolean succeed = false;
        if (sqliteDatabase.isOpen()) {
            try {
                succeed = sqliteDatabase.delete(mTableName, "task_id=?", args) != -1;
            } catch (Exception e) {
            }
        }

        try {
            sqliteDatabase.close();
        } catch (SQLException e) {
        }

        return succeed;
    }

    /**
     * 删除数据库中的一条任务信息
     *
     * @param id
     * @return
     */
    public synchronized boolean deleteTaskById(final int id) {
        boolean succeed = deleteTaskById(id, false);
        return succeed;
    }

    public synchronized boolean deleteTaskById(final int id, boolean isParent) {
        String[] args = {String.valueOf(id)};
        SQLiteDatabase sqliteDatabase = mDBHelper.getWritableDatabase();
        boolean succeed = false;
        if (sqliteDatabase.isOpen()) {
            try {
                List<String> list = getPath(id);
                succeed = sqliteDatabase.delete(mTableName, "id=?", args) != -1;
                if (succeed) {
                    for (int i = 0; i < list.size(); i++) {
                        if (isParent) {
                            FileUtils.deleteFD(new File(list.get(i)).getParent());
                        } else {
                            FileUtils.deleteFD(new File(list.get(i)));
                        }
                    }
                }
            } catch (Exception e) {
            } finally {
            }
        }

        try {
            sqliteDatabase.close();
        } catch (SQLException e) {
        }

        return succeed;
    }

    public synchronized boolean checkExits(int id, int type) {
        boolean exits = false;
        String selection = " where id = ? and type = ?";
        String[] selectionArgs = new String[]{String.valueOf(id), String.valueOf(type)};
        SQLiteDatabase sqliteDatabase = mDBHelper.getReadableDatabase();
        final Cursor c = sqliteDatabase.rawQuery("SELECT * FROM " + mTableName + selection, selectionArgs);
        if (c.getCount() > 0) {
            exits = true;
        }
        c.close();
        return exits;
    }

    public synchronized boolean checkExits(int taskId) {
        boolean exits = false;
        String selection = " where task_id = ?";
        String[] selectionArgs = new String[]{String.valueOf(taskId)};
        SQLiteDatabase sqliteDatabase = mDBHelper.getReadableDatabase();
        final Cursor c = sqliteDatabase.rawQuery("SELECT * FROM " + mTableName + selection, selectionArgs);
        if (c.getCount() > 0) {
            exits = true;
        }
        c.close();
        return exits;
    }

    public synchronized String getPathByUrl(String url) {
        String path = null;
        if (url == null || url.isEmpty()) {
            return null;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(FileDownloaderModel.URL, url);
        List<FileDownloaderModel> list = getResourceByFiled(hashMap);
        if (list.size() > 0) {
            String pathTemp = list.get(0).getPath();
            if (new File(pathTemp).exists()) {
                path = pathTemp;
            }
        }

        return path;
    }

    /**
     * 查询
     *
     * @param hashMap key - value
     * @return List<FileDownloaderModel>
     */
    public synchronized List<FileDownloaderModel> getResourceByFiled(HashMap<String, String> hashMap) {
        String select = "SELECT * FROM ";
        StringBuffer sqlselection = new StringBuffer();
        sqlselection.append(" where ");
        List<String> list = new ArrayList<>();
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            sqlselection.append(entry.getKey().toString() + " = ?");
            list.add(entry.getValue().toString());
            if (iterator.hasNext()) {
                sqlselection.append(" and ");
            }
        }
        String[] selectionArgs = list.toArray(new String[list.size()]);
        List<FileDownloaderModel> modelList = new ArrayList<>();
        SQLiteDatabase sqliteDatabase = mDBHelper.getReadableDatabase();
        Cursor c = sqliteDatabase.rawQuery(select + mTableName + sqlselection, selectionArgs);
        while (c.moveToNext()) {
            FileDownloaderModel model = addCursor2Data(c);
            modelList.add(model);
        }
        c.close();
        return modelList;
    }

    public synchronized List<String> getPath(int id) {
        List<String> list = new ArrayList<>();
        HashMap hashMap = new HashMap();
        hashMap.put(FileDownloaderModel.ID, String.valueOf(id));
        List<String> colums = new ArrayList<>();
        colums.add(FileDownloaderModel.PATH);
        Cursor cursor = getResourceColumns(hashMap, colums);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(FileDownloaderModel.PATH));
                list.add(path);
            }
        }
        cursor.close();
        return list;
    }

    public synchronized Cursor getResourceColumns(HashMap<String, String> hashMap, List<String> colums) {
        Cursor c;
        String select = "SELECT DISTINCT * FROM ";
        if (colums.size() > 0) {
            select = "SELECT DISTINCT " + listToString(colums) + " FROM ";
        }
        StringBuffer sqlSelection = new StringBuffer();
        sqlSelection.append(" where ");
        List<String> list = new ArrayList<>();
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            sqlSelection.append(entry.getKey().toString() + " = ?");
            list.add(entry.getValue().toString());
            if (iterator.hasNext()) {
                sqlSelection.append(" and ");
            }
        }
        String[] selectionArgs = list.toArray(new String[list.size()]);
        SQLiteDatabase sqliteDatabase = mDBHelper.getReadableDatabase();
        c = sqliteDatabase.rawQuery(select + mTableName + sqlSelection, selectionArgs);

        return c;
    }

    public synchronized Cursor getResourceById(int id) {
        Cursor c;
        String selection = " where id = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        SQLiteDatabase sqliteDatabase = mDBHelper.getReadableDatabase();
        c = sqliteDatabase.rawQuery("SELECT * FROM " + mTableName + selection, selectionArgs);
        if (c.getCount() > 0) {
            return c;
        }
        return c;
    }

    /**
     * 通过type来获取数据库资源
     *
     * @param type { EffectService }
     * @return List<FileDownloaderModel>
     */
    public synchronized List<FileDownloaderModel> getResourceByType(int type) {
        List<FileDownloaderModel> list = new ArrayList<>();
        Cursor c;
        String selection = " where effecttype = ? order by id";
        String[] selectionArgs = new String[]{String.valueOf(type)};
        SQLiteDatabase sqliteDatabase = mDBHelper.getReadableDatabase();
        c = sqliteDatabase.rawQuery("SELECT * FROM " + mTableName + selection, selectionArgs);
        while (c.moveToNext()) {
            FileDownloaderModel model = addCursor2Data(c);
            list.add(model);
        }
        c.close();
        return list;
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
}
