package com.ninjahome.ninja.utils

import com.squareup.moshi.Moshi

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object JsonUtils {
    private val moshi = Moshi.Builder().build()

    fun <T> json2Object(json: String, clazz: Class<T>): T? {
        return moshi.adapter(clazz).fromJson(json)
    }

    fun <T> object2Json(obj: T, clazz: Class<T>): String {
        return moshi.adapter(clazz).toJson(obj)
    }


}