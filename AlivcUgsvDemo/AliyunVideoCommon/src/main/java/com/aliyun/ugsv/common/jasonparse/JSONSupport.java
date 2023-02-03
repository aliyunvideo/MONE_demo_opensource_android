/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.ugsv.common.jasonparse;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 */
public abstract class JSONSupport {

    public abstract <T> T readListValue(String content, Type klass) throws Exception;
    public abstract <T> T readValue(InputStream istream, Class<? extends T> klass) throws Exception;
    public abstract <T> T readValue(File fin, Class<? extends T> klass) throws Exception;
    public abstract <T> T readValue(String content, Class<? extends T> klass) throws Exception;

    public abstract <T> void writeValue(OutputStream ostream, T instance) throws Exception;
    public abstract <T> void writeValue(File fout, T instance) throws Exception;
    public abstract <T> String writeValue(T instance) throws Exception;
}
