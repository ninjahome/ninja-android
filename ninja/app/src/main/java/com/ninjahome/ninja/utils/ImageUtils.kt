package com.ninjahome.ninja.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import com.ninjahome.ninja.utils.FileUtils.getFileNameFromPath
import java.io.*
import java.util.*

object ImageUtils {
    private val THUMB_IMG_DIR_PATH = UIUtils.context.cacheDir.absolutePath
    private const val IMG_WIDTH = 480 //超過此寬、高則會 resize圖片

    private const val IMG_HIGHT = 800
    private const val COMPRESS_QUALITY = 70 //壓縮 JPEG使用的品質(70代表壓縮率為 30%)


    fun genThumbImgFile(srcImgPath: String?): File? {
        val thumbImgDir = File(THUMB_IMG_DIR_PATH)
        if (!thumbImgDir.exists()) {
            thumbImgDir.mkdirs()
        }
        val thumbImgName: String = SystemClock.currentThreadTimeMillis().toString() + getFileNameFromPath(srcImgPath)
        var imageFileThumb: File? = null
        try {
            val `is`: InputStream = FileInputStream(srcImgPath)
            val bmpSource = BitmapFactory.decodeStream(`is`)
            val bmpTarget = ThumbnailUtils.extractThumbnail(bmpSource, 200, 200, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
                    ?: return null
            imageFileThumb = File(thumbImgDir, thumbImgName)
            imageFileThumb.createNewFile()
            val fosThumb = FileOutputStream(imageFileThumb)
            bmpTarget.compress(Bitmap.CompressFormat.JPEG, 100, fosThumb)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageFileThumb
    }

    fun compressImage(srcImgPath: String): File? {
        //先取得原始照片的旋轉角度
        var rotate = 0
        try {
            val exif = ExifInterface(srcImgPath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //計算取 Bitmap時的參數"inSampleSize"
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(srcImgPath, options)
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > IMG_HIGHT || width > IMG_WIDTH) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= IMG_HIGHT && halfWidth / inSampleSize >= IMG_WIDTH) {
                inSampleSize *= 2
            }
        }
        options.inJustDecodeBounds = false
        options.inSampleSize = inSampleSize

        //取出原檔的 Bitmap(若寬高超過會 resize)並設定原始的旋轉角度
        val srcBitmap = BitmapFactory.decodeFile(srcImgPath, options)
        if (srcBitmap == null) {
            Log.e("ImageUtils", "decode file error $srcImgPath")
            return null
        }
        val matrix = Matrix()
        matrix.postRotate(rotate.toFloat())
        val outBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.width, srcBitmap.height, matrix, false)

        //壓縮並存檔至 cache路徑下的 File
        val tempImgDir = File(THUMB_IMG_DIR_PATH)
        if (!tempImgDir.exists()) {
            tempImgDir.mkdirs()
        }
        val compressedImgName: String = SystemClock.currentThreadTimeMillis().toString() + getFileNameFromPath(srcImgPath)
        val compressedImgFile = File(tempImgDir, compressedImgName)
        var fos: FileOutputStream? = null
        try {
            compressedImgFile.createNewFile()
            fos = FileOutputStream(compressedImgFile)
            outBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, fos)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                srcBitmap.recycle()
                outBitmap.recycle()
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return compressedImgFile
    }

    private fun getDigest(str: String): String {
        return if (TextUtils.isEmpty(str)) "" else str.hashCode().toString() + ""
    }

    /**
     * 图片入系统相册
     */
    fun saveMedia2Album(context: Context, mediaFile: File?) {
        val uri = Uri.fromFile(mediaFile)
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
    }


    /**
     * 根据文件流判断图片类型
     *
     * @param is
     * @return jpg/png/gif/bmp
     */
    fun getPicType(b: ByteArray): String {
        val type = byteArrayToHexString(b).toUpperCase()
        //读取文件的前几个字节来判断图片格式
        return if (type.contains("FFD8FF")) {
            "jpg"
        } else if (type.contains("89504E47")) {
            "png"
        } else if (type.contains("47494638")) {
            "gif"
        } else if (type.contains("424D")) {
            "bmp"
        } else {
            "unknown"
        }
    }

    /**
     * 转换字节数组为十六进制字符串
     *
     * @return 十六进制字符串
     */
    private fun byteArrayToHexString(b: ByteArray): String {
        val resultSb = StringBuffer()
        for (i in b.indices) {
            resultSb.append(byteToHexString(b[i]))
        }
        return resultSb.toString()
    }

    // 十六进制下数字到字符的映射数组
    private val hexDigits = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f")

    /**
     * 将一个字节转化成十六进制形式的字符串
     */
    private fun byteToHexString(b: Byte): String {
        var n = b.toInt()
        if (n < 0) n = 256 + n
        val d1 = n / 16
        val d2 = n % 16
        return hexDigits[d1] + hexDigits[d2]
    }
}