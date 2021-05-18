package com.ninjahome.ninja.utils

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object StringUtils {
    fun bytesToHexString(bArray: ByteArray): String {
        bArray.toString(Charsets.UTF_8)
        val sb = StringBuffer(bArray.size)
        var sTemp: String
        for (i in bArray.indices) {
            sTemp = Integer.toHexString(0xFF and bArray[i].toInt())
            if (sTemp.length < 2) sb.append(0)
            sb.append(sTemp.toUpperCase())
        }
        return sb.toString()
    }

    fun hexStringToByte(hex: String): ByteArray {
        val len = hex.length / 2
        val result = ByteArray(len)
        val achar = hex.toCharArray()
        for (i in 0 until len) {
            val pos = i * 2
            result[i] = (toByte(achar[pos]) shl 4 or toByte(achar[pos + 1])).toByte()
        }
        return result
    }

    private fun toByte(c: Char): Int {
        val b = "0123456789ABCDEF".indexOf(c).toByte()
        return b.toInt()
    }
}