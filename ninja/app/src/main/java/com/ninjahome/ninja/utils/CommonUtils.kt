package com.ninjahome.ninja.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object CommonUtils {
    fun copyToMemory(context: Context, data: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Ninja memory string", data)
        clipboard.setPrimaryClip(clip)
    }
}