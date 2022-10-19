package com.alivc.live.beautyui;

import androidx.annotation.NonNull;

import com.alivc.live.beauty.constant.BeautySDKType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class BeautyUIDataInjectorFactory {

    public static BeautyUIDataInjectorInterface generateDataInjector(BeautySDKType type) {
        BeautyUIDataInjectorInterface itf = null;
        //非互动模式
        if (type == BeautySDKType.QUEEN) {
            itf = reflectInit(BeautySDKType.QUEEN.getDataInjectorClassName());
        }
        //互动模式
        if (type == BeautySDKType.INTERACT_QUEEN) {
            itf = reflectInit(BeautySDKType.INTERACT_QUEEN.getDataInjectorClassName());
        }
        return itf;
    }

    private static BeautyUIDataInjectorInterface reflectInit(@NonNull String className) {
        Object obj = null;
        try {
            Class<?> cls = Class.forName(className);
            Object[] values = {};
            Class<?>[] params = {};
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

        return (BeautyUIDataInjectorInterface) obj;
    }

}
