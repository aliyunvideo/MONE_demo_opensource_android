package com.aliyun.ugsv.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

public class CertUtils {
    private static String SD_DIR;
    private static String TAG = "CertUtils";
    public final static String CERT_NAME = "cert";

    public static String copyCert(Context cxt) {
        SD_DIR = getExtFileDir(cxt);
        copySelf(cxt, CERT_NAME);
        return SD_DIR + CERT_NAME + File.separator + "AliVideoCert.crt";
    }

    private static String getExtFileDir(Context cxt) {
        return cxt.getExternalFilesDir("") + File.separator;
    }

    private static void copySelf(Context cxt, String root) {
        try {
            String[] files = cxt.getAssets().list(root);
            if (files.length > 0) {
                File subdir = new File(SD_DIR + root);
                if (!subdir.exists()) {
                    subdir.mkdirs();
                }
                for (String fileName : files) {
                    copySelf(cxt, root + "/" + fileName);
                }
            } else {
                Log.d(TAG, "copy...." + SD_DIR + root);
                OutputStream myOutput = new FileOutputStream(SD_DIR + root);
                InputStream myInput = cxt.getAssets().open(root);
                byte[] buffer = new byte[1024 * 8];
                int length = myInput.read(buffer);
                while (length > 0) {
                    myOutput.write(buffer, 0, length);
                    length = myInput.read(buffer);
                }

                myOutput.flush();
                myInput.close();
                myOutput.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
