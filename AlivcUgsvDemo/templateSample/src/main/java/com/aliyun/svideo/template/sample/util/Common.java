package com.aliyun.svideo.template.sample.util;

import android.text.TextUtils;
import android.util.Log;

import com.aliyun.svideo.template.sample.Template;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;


import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class Common {
    public static final String TEMPLATE_FOLDER = "template";
    public static final String KEY_FOLDER = "KEY_FOLDER";
    public static final String KEY_TEMPLATE = "KEY_TEMPLATE";
    public static final String KEY_TEMPLATE_LIST = "KEY_TEMPLATE_LIST";
    public static final String KEY_TITLE = "KEY_TITLE";
    public static final int REQUEST_SINGLE_MEDIA = 11;
    public static final int REQUEST_PERMISSION_SINGLE = 22;
    public static final int REQUEST_CLIP_VIDEO = 33;

    public static ArrayList<Template> parseTemplateArrayJson(String infoJson) {
        ArrayList<Template> templates = new ArrayList<>();
        try {

            JSONArray info = new JSONArray(infoJson);
            for (int i = 0; i < info.length(); i++) {
                JSONObject object = info.getJSONObject(i);
                Template template = new Template(
                        object.getString("id"),
                        object.getString("name"),
                        object.getString("info"),
                        object.optString("cover"),
                        object.optInt("duration"),
                        object.optString("zip")
                );
                templates.add(template);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return templates;
    }


    public static void unZip(String zipFilePath, String destDir) throws Exception {
        ZipFile zipFile = new ZipFile(zipFilePath, "GBK");
        Enumeration<?> emu = zipFile.getEntries();
        BufferedInputStream bis;
        FileOutputStream fos;
        BufferedOutputStream bos;
        File file, parentFile;
        ZipEntry entry;
        String entryName = null;
        byte[] cache = new byte[1024];
        StringBuffer sb = new StringBuffer();
        while (emu.hasMoreElements()) {
            entry = (ZipEntry) emu.nextElement();
            entryName = entry.getName();
            if (entry.isDirectory()) {
                new File(destDir + File.separator + entryName).mkdirs();
                continue;
            }
            bis = new BufferedInputStream(zipFile.getInputStream(entry));
            sb.delete(0, sb.length());
            sb.append(destDir).append(File.separator).append(entryName);
            file = new File(sb.toString());
            parentFile = file.getParentFile();
            if (parentFile != null && (!parentFile.exists())) {
                parentFile.mkdirs();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos, 1024);
            int nRead = 0;
            while ((nRead = bis.read(cache, 0, 1024)) != -1) {
                fos.write(cache, 0, nRead);
            }
            bos.flush();
            bos.close();
            fos.close();
            bis.close();
        }
        zipFile.close();
    }}
