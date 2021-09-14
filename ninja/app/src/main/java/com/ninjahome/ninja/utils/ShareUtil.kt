package com.ninjahome.ninja.utils

import android.content.Context
import android.content.Intent
import android.net.Uri


/**
 *Author:Mr'x
 *Time:2021/9/14
 *Description:
 */
object ShareUtil {
    fun shareText(context: Context, text: String, title: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        context.startActivity(Intent.createChooser(intent, title))
    }

    fun shareImage(context: Context, uri: Uri, title: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(Intent.createChooser(intent, title))
    }

    fun sendEmail(context: Context, title: String, mail: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + mail))
        context.startActivity(Intent.createChooser(intent, title))
    }

    fun sendMoreImage(context: Context, imageUris: ArrayList<Uri>, title: String) {
        val mulIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        mulIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
        mulIntent.type = "image/jpeg"
        context.startActivity(Intent.createChooser(mulIntent, "多图文件分享"))
    }
}