package com.ninjahome.ninja.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.ninja.android.lib.utils.dp
import com.orhanobut.logger.Logger
import java.io.*


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object BitmapUtils {
    private const val DCIM = "/DCIM/Camera/"

    fun stringToQRBitmap(data: String): Bitmap {
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400.dp.toInt(), 400.dp.toInt())
    }

    fun saveBitmapToAlbum(context: Context, bitmap: Bitmap, bitName: String): Uri? {
        val fileName: String
        var file: File
        val brand = Build.BRAND


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return saveSignImage(context, bitmap, bitName)
        }

        Logger.v("saveBitmap brand", "" + brand)
        val dirPath = Environment.getExternalStorageDirectory().path.toString() + DCIM
        file = File(dirPath)

        if (!file.exists()) {
            val mkdirs = file.mkdirs()
            if (!mkdirs) {
                Logger.e("makeFire", "file.mkdirs error")
                return null
            }
        }

        fileName = dirPath + bitName
        file = File(fileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        val out: FileOutputStream
        try {
            out = FileOutputStream(file)
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush()
                out.close()
                // 插入图库
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                } else {
                    MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, bitName, null)
                }
            }
        } catch (e: FileNotFoundException) {
            Logger.e("FileNotFoundException", "FileNotFoundException:" + e.message.toString())
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            Logger.e("IOException", "IOException:" + e.message.toString())
            e.printStackTrace()
            return null
        } catch (e: Exception) {
            Logger.e("IOException", "IOException:" + e.message.toString())
            e.printStackTrace()
            return null

        }
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$fileName")))

        return Uri.parse("file://$fileName")
    }

    private fun saveSignImage(context: Context, bitmap: Bitmap, fileName: String?): Uri? {
        try {
            //设置保存参数到ContentValues中
            val contentValues = ContentValues()
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            //兼容Android Q和以下版本
            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //RELATIVE_PATH是相对路径不是绝对路径
            //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)

            //设置文件类型
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG")
            //执行insert操作，向系统文件夹中添加文件
            //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                //若生成了uri，则表示该文件添加成功
                //使用流将内容写入该uri中即可
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    outputStream.flush()
                    outputStream.close()
                }
            }
            return uri
        } catch (e: Exception) {
            Logger.e(e.message.toString())
            return null
        }
    }
}