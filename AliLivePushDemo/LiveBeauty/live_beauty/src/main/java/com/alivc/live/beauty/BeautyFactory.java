package com.alivc.live.beauty;

import android.content.Context;

import com.alivc.live.beauty.constant.BeautySDKType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import androidx.annotation.NonNull;

public class BeautyFactory {

    public static BeautyInterface createBeauty(BeautySDKType type, @NonNull Context context) {
        BeautyInterface itf = null;
        //非互动模式
        if (type == BeautySDKType.QUEEN) {
            Object[] values = {context};
            Class<?>[] params = {Context.class};
            itf = reflectInitBeauty(BeautySDKType.QUEEN.getManagerClassName(), values, params);
        }
        //互动模式
        if(type == BeautySDKType.INTERACT_QUEEN){
            Object[] values = {context};
            Class<?>[] params = {Context.class};
            itf = reflectInitBeauty(BeautySDKType.INTERACT_QUEEN.getManagerClassName(), values, params);
        }
        return itf;
    }

    private static BeautyInterface reflectInitBeauty(@NonNull String className, @NonNull Object[] values, @NonNull Class<?>[] params) {
        Object obj = null;
        try {
            Class<?> cls = Class.forName(className);
            Constructor<?> constructor = cls.getDeclaredConstructor(params);
            obj = constructor.newInstance(values);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (BeautyInterface) obj;
    }
}
