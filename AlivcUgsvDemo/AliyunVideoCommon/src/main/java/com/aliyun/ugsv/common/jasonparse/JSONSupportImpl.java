/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.ugsv.common.jasonparse;

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

/**
 *
 */
public class JSONSupportImpl extends JSONSupport {

    private final Gson mGson = new Gson();

    @Override
    public <T> T readListValue(String content, Type klass) throws Exception {
        return mGson.fromJson(content, klass);
    }

    @Override
    public <T> T readValue(InputStream istream, Class<? extends T> klass) throws Exception {
        JsonReader reader = new JsonReader(new InputStreamReader(istream, "UTF-8"));
        reader.setLenient(true);
        T t = null;
        try {
            t = mGson.fromJson(reader, klass);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
        return t;
    }

    @Override
    public <T> T readValue(File fin, Class<? extends T> klass) throws Exception {
        return readValue(new FileInputStream(fin), klass);
    }

    @Override
    public <T> T readValue(String content, Class<? extends T> klass) throws Exception {
        return mGson.fromJson(content, klass);
    }

    @Override
    public <T> void writeValue(OutputStream ostream, T instance) throws Exception {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(ostream, "UTF-8"));
        try {
            mGson.toJson(instance, instance.getClass(), writer);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                writer.flush();
            } catch (IOException e) {
            }
            try {
                writer.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public <T> void writeValue(File fout, T instance) throws Exception {
        writeValue(new FileOutputStream(fout), instance);
    }

    @Override
    public <T> String writeValue(T instance) throws Exception {
        return mGson.toJson(instance);
    }

}
