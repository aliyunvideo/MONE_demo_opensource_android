package com.alivc.live.commonutils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

/**
 * @author keria
 * @date 2021/7/11
 */
public class FileUtil {

    private FileUtil() {
    }

    /**
     * Check if file exists
     *
     * @param path file path
     * @return true->exists, false->not exist
     */
    public static boolean fileExists(@Nullable String path) {
        if (path == null) {
            return false;
        }
        if (path.isEmpty()) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    /**
     * Check if folder exists
     *
     * @param path folder path
     * @return true->exists, false->not exist
     */
    public static boolean folderExists(@Nullable String path) {
        if (path == null) {
            return false;
        }
        if (path.isEmpty()) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }

    /**
     * Get parent folder absolute path
     *
     * @param path file path
     * @return parent folder absolute path
     */
    @NonNull
    public static String getFolderPath(@Nullable String path) {
        if (path == null) {
            return "";
        }
        if (path.isEmpty()) {
            return "";
        }
        File file = new File(path);
        String parent = file.getAbsoluteFile().getParent();
        return parent != null ? parent : "";
    }

    /**
     * Create folder if not exists
     * <p>
     * If the target is a file, remove it and then create folder.
     *
     * @param path folder path
     */
    public static void safeCreateFolder(@Nullable String path) {
        if (path == null || path.isEmpty()) {
            return;
        }
        File file = new File(path);
        if (file.isFile()) {
            file.delete();
        }
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * Remove file if exists
     * <p>
     * If the target exists, remove it
     *
     * @param path file path
     */
    public static void safeDeleteFile(@Nullable String path) {
        if (path == null || path.isEmpty()) {
            return;
        }

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Get internal cache folder from sd card
     *
     * @param context android context
     * @return internal cache folder from sdcard
     */
    @NonNull
    public static String getInternalCacheFolder(@Nullable Context context) {
        if (context == null) {
            return "";
        }
        return context.getCacheDir().getAbsolutePath();
    }

    /**
     * Get external cache folder from sd card
     *
     * @param context android context
     * @return external cache folder from sdcard
     */
    @NonNull
    public static String getExternalCacheFolder(@Nullable Context context) {
        if (context == null) return "";
        return context.getExternalCacheDir().getAbsolutePath();
    }

    /**
     * Combine paths
     *
     * @param varargs path list
     * @return combined absolute path
     */
    @NonNull
    public static String combinePaths(String... varargs) {
        if (varargs.length == 0) {
            return "";
        }

        File parent = new File(varargs[0]);
        int i = 1;
        while (i < varargs.length) {
            parent = new File(parent, varargs[i++]);
        }
        return parent.getAbsolutePath();
    }

    /**
     * see this How-to for a faster way to convert
     * a byte array to a HEX string
     *
     * @param filename file name
     * @return md5 byte buffer
     */
    @Nullable
    public static byte[] createChecksum(@Nullable String filename) throws Exception {
        if (!fileExists(filename)) {
            return null;
        }

        InputStream fis = new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    /**
     * see this How-to for a faster way to convert
     * a byte array to a HEX string
     *
     * @param filename file name
     * @return md5 string
     */
    @NonNull
    public static String getMD5Checksum(@NonNull String filename) {
        if (!fileExists(filename)) {
            return "";
        }

        try {
            byte[] b = createChecksum(filename);
            if (b == null) {
                return "";
            }

            StringBuilder result = new StringBuilder();
            for (byte value : b) {
                result.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * list all files
     *
     * @param file          file folder
     * @param includeFolder includeFolder
     * @return all files
     */
    @Nullable
    public static ArrayList<File> listAllFiles(@Nullable File file, boolean includeFolder) {
        if (file == null || !file.exists()) {
            return null;
        }

        ArrayList<File> files = new ArrayList<>();

        if (file.isFile()) {
            files.add(file);
        } else if (file.isDirectory()) {
            if (includeFolder) {
                files.add(file);
            }

            File[] filesArray = file.listFiles();
            if (filesArray == null || file.length() == 0) {
                return null;
            }

            for (File fileFolder : filesArray) {
                ArrayList<File> subFiles = listAllFiles(fileFolder, includeFolder);
                if (subFiles == null || subFiles.isEmpty()) {
                    continue;
                }
                files.addAll(subFiles);
            }
        }

        return files;
    }

    /**
     * read lines from file
     *
     * @param file file
     * @return file lines
     */
    @Nullable
    public static ArrayList<String> readLinesFromFile(@Nullable File file) {
        if (file == null || !file.exists() || !file.canRead()) {
            return null;
        }

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (fileReader == null) {
            return null;
        }

        ArrayList<String> lines = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(fileReader, 1000 * 8192);
        try {
            String st;
            while ((st = bufferedReader.readLine()) != null) {
                lines.add(st);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return lines;
    }

    @Nullable
    public static String readTextFromFile(@Nullable File file) {
        if (file == null || !file.exists() || !file.canRead()) {
            return null;
        }

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (fileReader == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(fileReader, 1000 * 8192);
        try {
            String st;
            while ((st = bufferedReader.readLine()) != null) {
                sb.append(st).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
