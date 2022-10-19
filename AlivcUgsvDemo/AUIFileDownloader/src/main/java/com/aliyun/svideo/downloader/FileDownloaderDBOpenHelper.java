/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.downloader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aliyun.common.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

class FileDownloaderDBOpenHelper extends SQLiteOpenHelper {
    final static String DATABASE_NAME = "filedownloaderfinal.db";
    final Map<String, String> mDbExtFieldMap;
    private DbUpgradeListener mDbUpgradeListener;

    public FileDownloaderDBOpenHelper(Context context, final int dbVersion,
                                      Map<String, String> dbExtFieldMap,
                                      DbUpgradeListener dbUpgradeListener) {
        super(context, DATABASE_NAME, null, dbVersion);
        this.mDbExtFieldMap = dbExtFieldMap;
        this.mDbUpgradeListener = dbUpgradeListener;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Map<String, String> allFieldMap = new HashMap<>();
        allFieldMap.put(FileDownloaderModel.TASK_ID, "INTEGER PRIMARY KEY"); //主键
        allFieldMap.put(FileDownloaderModel.ID, "INTEGER");
        allFieldMap.put(FileDownloaderModel.NAME, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.NAME_EN, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.URL, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.PATH, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.ISUNZIP, "INTEGER");

        allFieldMap.put(FileDownloaderModel.EFFECTTYPE, "INTEGER");
        allFieldMap.put(FileDownloaderModel.KEY, "VARCHAR");
//        allFieldMap.put(FileDownloaderModel.SUBEFFECTYPT, "INTEGER");
        allFieldMap.put(FileDownloaderModel.LEVEL, "INTEGER");
        allFieldMap.put(FileDownloaderModel.TAG, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.CAT, "INTEGER");
        allFieldMap.put(FileDownloaderModel.PREVIEWPIC, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.PREVIEWMP4, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.DURATION, "INTEGER");
//        allFieldMap.put(FileDownloaderModel.TYPE, "INTEGER");
        allFieldMap.put(FileDownloaderModel.SORT, "INTEGER");
        allFieldMap.put(FileDownloaderModel.ASPECT, "INTEGER");
        allFieldMap.put(FileDownloaderModel.DOWNLOAD, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.MD5, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.CNNAME, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.CATEGORY, "INTEGER");
        allFieldMap.put(FileDownloaderModel.BANNER, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.ICON, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.DESCRIPTION, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.DESCRIPTION_EN, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.ISNEW, "INTEGER");
        allFieldMap.put(FileDownloaderModel.PREVIEW, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.SUBID, "INTEGER");
        allFieldMap.put(FileDownloaderModel.FONTID, "INTEGER");
        allFieldMap.put(FileDownloaderModel.SUBICON, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.SUBNAME, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.PRIORITY, "INTEGER");
        allFieldMap.put(FileDownloaderModel.SUBPREVIEW, "VARCHAR");
        allFieldMap.put(FileDownloaderModel.SUBSORT, "INTEGER");
        allFieldMap.put(FileDownloaderModel.SUBTYPE, "INTEGER");

        if (mDbExtFieldMap != null) {
            allFieldMap.putAll(mDbExtFieldMap);
        }

        StringBuffer sqlSb = new StringBuffer();
        sqlSb.append("CREATE TABLE IF NOT EXISTS " + FileDownloaderDBController.TABLE_NAME);
        sqlSb.append("(");

        int index = 0;
        int max = allFieldMap.size();
        for (Map.Entry<String, String> entry : allFieldMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            index ++;
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
                continue;
            }
            sqlSb.append(key);
            sqlSb.append(" ");
            sqlSb.append(value);
            if ( index != max ) {
                sqlSb.append(",");
            }
        }
        sqlSb.append(")");
        db.execSQL(sqlSb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //使用case穿透实现跨版本升级
        switch (oldVersion) {
        case 1:
            upgrade2(db);
            break;
        default:
            break;
        }
        if (mDbUpgradeListener != null) {
            mDbUpgradeListener.onUpgrade(db, oldVersion, newVersion);
        }
    }

    /**
     * 版本升级 1 > 2
     * @param db SQLiteDatabase
     */
    private void upgrade2(SQLiteDatabase db) {
        //添加字段 nameEn,descriptionEn
        db.execSQL("alter table " + FileDownloaderDBController.TABLE_NAME + " add " + FileDownloaderModel.NAME_EN + " VARCHAR");
        db.execSQL("alter table " + FileDownloaderDBController.TABLE_NAME + " add " + FileDownloaderModel.DESCRIPTION_EN + " VARCHAR");
    }

}
