package com.ninjahome.ninja.utils

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object UnitConversionUtils {

    fun m2Km(m: Float): String {
        if (m >= 1000) {
            return (m / 1000).toString().plus("km")
        }
        return m.toString().plus("m")
    }


}