package com.aliyun.svideo.downloader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.io.IOException;

public class DatabaseTransferHelper {
    private static final String TAG = "DatabaseTransferHelper";

    public static void convertNormalToSQLCipheredDB(Context context, String startingFileName, String ck)
            throws IOException {
        File mStartingFile = context.getDatabasePath(startingFileName);
        if (!mStartingFile.exists()) {
            return;
        }
        android.database.sqlite.SQLiteDatabase oldDatabase = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(mStartingFile, null);
        FileDownloaderDBController dbController = DownloaderManager.getInstance().getDbController();
        FileDownloaderDBCipherOpenHelper dbCipherOpenHelper = dbController.getDBCipherOpenHelper();
        if (dbCipherOpenHelper != null) {
            Cursor old_cursor = oldDatabase.rawQuery("select name from sqlite_master where type='table' order by name", null);
            if (old_cursor != null && old_cursor.getCount() > 0) {
                boolean hasAimTableName = false;
                while (old_cursor.moveToNext()) {
                    String name = old_cursor.getString(0);//name
                    if ("FileDownloader".equals(name)) {
                        hasAimTableName = true;
                        break;
                    }
                }
                old_cursor.close();
                if (!hasAimTableName) {
                    return;
                }
            }

            Cursor cursor = oldDatabase.rawQuery("select * from " + "FileDownloader", null);
            if (null == cursor || !cursor.moveToFirst()) {
                Log.e(TAG, "table FileDownloader has no data !");
                return;
            }
            while (cursor.moveToNext()) {
                ContentValues cv = new ContentValues();
                String[] columnNames = cursor.getColumnNames();
                for (int i = 0; i < columnNames.length; i++) {
                    int type = cursor.getType(i);
                    String columnName = cursor.getColumnName(i);

                    switch (type) {
                        case Cursor.FIELD_TYPE_BLOB:
                            cv.put(columnName, cursor.getBlob(i));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            cv.put(columnName, cursor.getFloat(i));
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            cv.put(columnName, cursor.getInt(i));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            cv.put(columnName, cursor.getString(i));
                            break;
                        default:
                            break;
                    }

                }
                SQLiteDatabase sqliteDatabase = dbCipherOpenHelper.getWritableDatabase(ck);
                if (sqliteDatabase.isOpen()) {
                    try {
                        sqliteDatabase.insert("FileDownloader", null, cv);
                    } catch (Exception e) {
                        Log.e(TAG, "convertNormalToSQLCipheredDB: ", e);
                    }
                }
                try {
                    sqliteDatabase.close();
                } catch (SQLException e) {
                    Log.e(TAG, "convertNormalToSQLCipheredDB: ", e);
                }

            }

            try {
                cursor.close();
            } catch (Exception e) {
                Log.e(TAG, "convertNormalToSQLCipheredDB: ", e);
            }
            mStartingFile.delete();
        }

    }


}
