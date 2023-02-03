package com.aliyun.svideo.template.sample;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetsUtils {
    public static void copyDirFromAssets(Context context, String src, String dest) {
        try {
            String[] names = context.getAssets().list(src);
            if (names.length > 0) {
                File file = new File(dest);
                if (!file.exists()) {
                    file.mkdirs();
                }

                for (String name : names) {
                    copyDirFromAssets(context,
                            src.equals("") ? name : src + File.separator + name,
                            dest + File.separator + name);
                }
            } else {
                copyFileFromAssets(context, src, dest);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFileFromAssets(Context context, String src, String dest) {
        try {
            InputStream is = context.getAssets().open(src);

            FileOutputStream fos = new FileOutputStream(dest);

            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();

            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
