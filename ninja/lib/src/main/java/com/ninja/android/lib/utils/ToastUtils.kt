package com.ninja.android.lib.utils

import android.content.Context
import android.os.Looper
import android.widget.Toast
import com.ninja.android.lib.provider.context

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
private var toast: Toast? = null
fun toast(msg: String) {
    val context: Context = context()
    try {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(msg)
        }
        toast!!.show()
    } catch (e: Exception) {
        Looper.prepare()
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}