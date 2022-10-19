package com.aliyun.svideo.media.JsonExtend;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

public abstract class JSONSupport {
    public JSONSupport() {
    }

    public abstract <T> T readListValue(String var1, Type var2) throws Exception;

    public abstract <T> T readValue(InputStream var1, Class<? extends T> var2) throws Exception;

    public abstract <T> T readValue(File var1, Class<? extends T> var2) throws Exception;

    public abstract <T> T readValue(String var1, Class<? extends T> var2) throws Exception;

    public abstract <T> void writeValue(OutputStream var1, T var2) throws Exception;

    public abstract <T> void writeValue(File var1, T var2) throws Exception;

    public abstract <T> String writeValue(T var1) throws Exception;
}
