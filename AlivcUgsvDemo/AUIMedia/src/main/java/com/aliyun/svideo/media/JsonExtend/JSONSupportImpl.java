package com.aliyun.svideo.media.JsonExtend;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

public class JSONSupportImpl extends JSONSupport {
    private final Gson mGson = new Gson();

    public JSONSupportImpl() {
    }

    public <T> T readListValue(String content, Type klass) throws Exception {
        return this.mGson.fromJson(content, klass);
    }

    public <T> T readValue(InputStream istream, Class<? extends T> klass) throws Exception {
        JsonReader var3 = new JsonReader(new InputStreamReader(istream, "UTF-8"));
        var3.setLenient(true);
        T var4 = null;

        try {
            var4 = this.mGson.fromJson(var3, klass);
        } catch (Exception var13) {
            throw var13;
        } finally {
            try {
                var3.close();
            } catch (IOException var12) {
            }

        }

        return var4;
    }

    public <T> T readValue(File fin, Class<? extends T> klass) throws Exception {
        return this.readValue((InputStream)(new FileInputStream(fin)), klass);
    }

    public <T> T readValue(String content, Class<? extends T> klass) throws Exception {
        return this.mGson.fromJson(content, klass);
    }

    public <T> void writeValue(OutputStream ostream, T instance) throws Exception {
        JsonWriter var3 = new JsonWriter(new OutputStreamWriter(ostream, "UTF-8"));

        try {
            this.mGson.toJson(instance, instance.getClass(), var3);
        } catch (Exception var15) {
            throw var15;
        } finally {
            try {
                var3.flush();
            } catch (IOException var14) {
            }

            try {
                var3.close();
            } catch (IOException var13) {
            }

        }

    }

    public <T> void writeValue(File fout, T instance) throws Exception {
        this.writeValue((OutputStream)(new FileOutputStream(fout)), instance);
    }

    public <T> String writeValue(T instance) throws Exception {
        return this.mGson.toJson(instance);
    }
}

