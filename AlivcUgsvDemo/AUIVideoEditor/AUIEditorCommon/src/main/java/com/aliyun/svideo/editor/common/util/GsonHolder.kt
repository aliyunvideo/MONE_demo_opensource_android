package com.aliyun.svideo.editor.common.util

import com.google.gson.Gson
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * ****************************************************************************
 * Copyright (C) 2005-2022 Alibaba Corporation. All rights reserved
 * <p>
 * Creation : 2022/6/8
 * Author   : zhihong.lanzh
 * ****************************************************************************
 */
class GsonHolder {
    companion object {
        @JvmField
        val gson = Gson()

        /**
         * 可以将json array数组解析成 List<T> 集合对象
         * 另一种方法，new TypeToken<List></List><T>>(){}.getType()；也可以实现
         * @param json
         * @param clazz
         * @param <T>
         * @return
        </T></T></T> */
        @JvmStatic
        fun <T> parseJson2List(json: String, clazz: Class<T>): List<T> {
            val type = ParameterizedTypeImpl(clazz)
            return gson.fromJson<List<T>>(json, type)
        }

    }
}

private class ParameterizedTypeImpl internal constructor(private val mClazz: Class<*>) : ParameterizedType {


    override fun getActualTypeArguments(): Array<Type> {
        return arrayOf(mClazz)
    }

    override fun getRawType(): Type {
        return List::class.java
    }

    override fun getOwnerType(): Type? {
        return null
    }
}