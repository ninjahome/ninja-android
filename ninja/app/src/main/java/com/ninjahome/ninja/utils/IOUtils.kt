package com.ninjahome.ninja.utils

import com.orhanobut.logger.Logger
import java.io.Closeable
import java.io.IOException

object IOUtils {
    /**
     * 关闭流
     */
    fun close(io: Closeable?): Boolean {
        if (io != null) {
            try {
                io.close()
            } catch (e: IOException) {
                Logger.e(e.message!!)
            }
        }
        return true
    }
}