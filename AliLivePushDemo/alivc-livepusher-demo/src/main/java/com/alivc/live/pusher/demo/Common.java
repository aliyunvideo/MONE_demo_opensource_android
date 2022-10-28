package com.alivc.live.pusher.demo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;

import com.alivc.live.pusher.demo.bean.MusicInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class Common {

    private static String SD_DIR = "";
    private static String RESOURCE_DIR = "alivc_resource";
    private static String filename = RESOURCE_DIR + File.separator + "watermark.png";
    public  static String waterMark = "";

    public static void copyAsset(Context context) {
        if(context == null) {
            return;
        }
        if(SD_DIR == null || SD_DIR.isEmpty()){
            SD_DIR = context.getFilesDir().getPath() + File.separator;
            waterMark = SD_DIR + filename;
        }
        if(new File(SD_DIR + filename).exists()) {
            return;
        }
        if(SD_DIR != null && new File(SD_DIR).exists()) {

            Boolean isSuccess = true;
            AssetManager assetManager = context.getAssets();

            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                String newFileName = SD_DIR + filename;
                out = new FileOutputStream(newFileName);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (Exception e) {
                e.printStackTrace();
                isSuccess = false;
            }
        }
    }

    public static void showDialog(final Context context, final String message) {
        if(context == null || message == null) {
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(context != null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle(context.getString(R.string.dialog_title));
                    dialog.setMessage(message);
                    dialog.setNegativeButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    public static void copyAll(Context cxt) {
        if(SD_DIR == null || SD_DIR.isEmpty()){
            SD_DIR = cxt.getFilesDir().getPath() + File.separator;
            waterMark = SD_DIR + filename;
        }
        File dir = new File(Common.SD_DIR);
        copySelf(cxt,"alivc_resource");
        dir.mkdirs();
    }

    public static void copySelf(Context cxt, String root) {
        try {
            String[] files = cxt.getAssets().list(root);
            if(files != null && files.length > 0) {
                File subdir = new File(Common.SD_DIR + root);
                if (!subdir.exists()) {
                    subdir.mkdirs();
                }
                for (String fileName : files) {
                    File file = new File(Common.SD_DIR + root + File.separator + fileName);
                    if (file.exists() && !file.isDirectory()){
                        file = null;
                        continue;
                    }
                    copySelf(cxt,root + "/" + fileName);
                }
            }else{
                OutputStream myOutput = new FileOutputStream(Common.SD_DIR+root);
                InputStream myInput = cxt.getAssets().open(root);
                byte[] buffer = new byte[1024 * 8];
                int length = myInput.read(buffer);
                while(length > 0)
                {
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

    public static ArrayList<MusicInfo> getResource() {
        ArrayList<MusicInfo> list = new ArrayList<>();
        if(new File(SD_DIR + RESOURCE_DIR).exists()) {

            File[] files = new File(SD_DIR + RESOURCE_DIR).listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File file, String s) {
                    if(s.endsWith(".mp3")) {
                        return true;
                    }
                    return false;
                }
            });
            if(files != null && files.length > 0) {
                for(int i = 0; i < files.length; i++) {
                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.setMusicName(files[i].getName());
                    musicInfo.setPath(files[i].getAbsolutePath());
                    list.add(musicInfo);
                }
            }
        }
        return list;
    }

    public static String getTime(long ms) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(ms);
        return hms;
    }

    public static String getFileMD5(File file) {
        if (file == null) {
            return "";
        }
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
                    file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }
}
