package com.aliyun.ugsv.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;

/**
 * @author geekeraven
 */
public class FileCopyUtils {

    public static String SD_DIR ;

    public interface CopyCallback {
        void onFileCopy(String filePath);
    }

    public static String getExtFileDir(Context cxt) {
        return cxt.getExternalFilesDir("") + File.separator;
    }

    public static void copyAssetsFile(Context cxt, String assetsName, String destPath, CopyCallback copyCallback) {
        try {
            if(StringUtils.isEmpty(destPath)){
                destPath = getExtFileDir(cxt);
            }
            if (!destPath.endsWith(File.separator)) {
                destPath += File.separator;
            }
            String[] files = cxt.getAssets().list(assetsName);
            if (files.length > 0) {
                File subdir = new File(destPath + assetsName);
                if (!subdir.exists()) {
                    subdir.mkdirs();
                }

                for (String fileName : files) {
                    File fileTemp = new File(assetsName + File.separator + fileName);
                    if (!fileTemp.isDirectory() && fileTemp.exists()) {
                        continue;
                    }
                    copyAssetsFile(cxt,  assetsName + File.separator + fileName, destPath, copyCallback);
                }
            } else {
                if (FileUtils.isFileExists(destPath + assetsName)) {
                    return;
                }
                OutputStream myOutput = new FileOutputStream(destPath + assetsName);
                InputStream myInput = cxt.getAssets().open(assetsName);
                byte[] buffer = new byte[1024 * 8];
                int length = myInput.read(buffer);
                while (length > 0) {
                    myOutput.write(buffer, 0, length);
                    length = myInput.read(buffer);
                }

                myOutput.flush();
                myInput.close();
                myOutput.close();

                if (copyCallback != null) {
                    copyCallback.onFileCopy(destPath + assetsName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (szName.contains("__MACOSX")) {
                continue;
            }
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {

                File file = new File(outPathString + File.separator + szName);
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = inZip.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }
}
