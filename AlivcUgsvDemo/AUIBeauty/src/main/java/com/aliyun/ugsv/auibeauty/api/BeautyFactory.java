package com.aliyun.ugsv.auibeauty.api;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.ugsv.auibeauty.BeautyInterface;
import com.aliyun.ugsv.auibeauty.BeautyManager;
import com.aliyun.ugsv.auibeauty.api.constant.BeautyConstant;
import com.aliyun.ugsv.auibeauty.api.constant.BeautySDKType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class BeautyFactory {

    @Nullable
    public static BeautyInterface createBeauty(BeautySDKType type, @NonNull Context context) {
        BeautyInterface itf = null;
        if (type == BeautySDKType.QUEEN) {
            itf = reflectInitBeauty(BeautyConstant.beautyManagerImplMap.get(BeautySDKType.QUEEN), null, null);
        }else if (type == BeautySDKType.FACEUNITY){
            itf = reflectInitBeauty(BeautyConstant.beautyManagerImplMap.get(BeautySDKType.FACEUNITY), null, null);
        }else if (type == BeautySDKType.RACE){
            itf = reflectInitBeauty(BeautyConstant.beautyManagerImplMap.get(BeautySDKType.RACE), null, null);
        }else if (type == BeautySDKType.DEFAULT){
            itf = new BeautyManager();
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
