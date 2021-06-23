package com.ninjahome.ninja.utils

import android.app.Activity
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.SystemClock
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.utils.FileUtils.getFileNameFromPath
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object ImageUtils {
    private val thumbImgDirPath = context().cacheDir.absolutePath
    private var thumbImgDir: File? = null

    /**
     * 根据一个网络连接(String)获取bitmap图像
     *
     * @param imageUri
     * @return
     */
    fun getNetBitmap(imageUri: String?): Bitmap? {
        // 显示网络上的图片
        var bitmap: Bitmap? = null
        try {
            val myFileUrl = URL(imageUri)
            val conn = myFileUrl.openConnection() as HttpURLConnection
            conn.doInput = true
            conn.connect()
            val `is` = conn.inputStream
            bitmap = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            bitmap = null
        } catch (e: IOException) {
            e.printStackTrace()
            bitmap = null
        }
        return bitmap
    }

    // 图片sd地址 上传服务器时把图片调用下面方法压缩后 保存到临时文件夹 图片压缩后小于100KB，失真度不明显
    @Throws(IOException::class)
    fun revitionImageSize(path: String?): Bitmap? {
        var `in` = BufferedInputStream(FileInputStream(File(path)))
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        // Bitmap btBitmap=BitmapFactory.decodeFile(path);
        // System.out.println("原尺寸高度："+btBitmap.getHeight());
        // System.out.println("原尺寸宽度："+btBitmap.getWidth());
        BitmapFactory.decodeStream(`in`, null, options)
        `in`.close()
        var i = 0
        var bitmap: Bitmap? = null
        while (true) {
            if (options.outWidth shr i <= 800 && options.outHeight shr i <= 800) {
                `in` = BufferedInputStream(FileInputStream(File(path)))
                options.inSampleSize = Math.pow(2.0, i.toDouble()).toInt()
                options.inJustDecodeBounds = false
                bitmap = BitmapFactory.decodeStream(`in`, null, options)
                break
            }
            i += 1
        }
        // 当机型为三星时图片翻转
        //		bitmap = Photo.photoAdapter(path, bitmap);
        //		System.out.println("-----压缩后尺寸高度：" + bitmap.getHeight());
        //		System.out.println("-----压缩后尺寸宽度度：" + bitmap.getWidth());
        return bitmap
    }

    fun getLoacalBitmap(url: String?): Bitmap? {
        return try {
            val fis = FileInputStream(url)
            BitmapFactory.decodeStream(fis) // /把流转化为Bitmap图片
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * @param x              图像的宽度
     * @param y              图像的高度
     * @param image          源图片
     * @param outerRadiusRat 圆角的大小
     * @return 圆角图片
     */
    fun createFramedPhoto(x: Int, y: Int, image: Bitmap?, outerRadiusRat: Float): Bitmap {
        // 根据源文件新建一个darwable对象
        val imageDrawable: Drawable = BitmapDrawable(image)

        // 新建一个新的输出图片
        val output = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        // 新建一个矩形
        val outerRect = RectF(0.0f, 0.0f, x.toFloat(), y.toFloat())

        // 产生一个红色的圆角矩形
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED
        canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat, paint)

        // 将源图片绘制到这个圆角矩形上
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        imageDrawable.setBounds(0, 0, x, y)
        canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG)
        imageDrawable.draw(canvas)
        canvas.restore()
        return output
    }

    fun zoomForFilePath(context: Activity, filePath: String?): Bitmap {
        var bitmap = BitmapFactory.decodeFile(filePath)
        val opt = BitmapFactory.Options()
        //这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true

        //获取到这个图片的原始宽度和高度
        val picWidth = bitmap.width
        val picHeight = bitmap.height

        //获取屏的宽度和高度
        val windowManager = context.windowManager
        val display = windowManager.defaultDisplay
        val screenWidth = display.width
        val screenHeight = display.height

        //isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1
        //根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picWidth > screenWidth) opt.inSampleSize = picWidth / screenWidth
        } else {
            if (picHeight > screenHeight) opt.inSampleSize = picHeight / screenHeight
        }

        //这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false
        bitmap = BitmapFactory.decodeFile(filePath, opt)
        return bitmap
    }

    /**
     * 高斯模糊
     */
    fun doBlur(sentBitmap: Bitmap, radius: Int, canReuseInBitmap: Boolean): Bitmap? {
        val bitmap: Bitmap
        bitmap = if (canReuseInBitmap) {
            sentBitmap
        } else {
            sentBitmap.copy(sentBitmap.config, true)
        }
        if (radius < 1) {
            return null
        }
        val w = bitmap.width
        val h = bitmap.height
        val pix = IntArray(w * h)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)
        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1
        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))
        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }
        yi = 0
        yw = yi
        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int
        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - Math.abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius
            x = 0
            while (x < w) {
                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vmin[x]]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x
                sir = stack[i + radius]
                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]
                rbs = r1 - Math.abs(i)
                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {

                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (-0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum])
                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum
                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]
                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]
                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
                rsum += rinsum
                gsum += ginsum
                bsum += binsum
                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]
                yi += w
                y++
            }
            x++
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h)
        return bitmap
    }

    fun genThumbImgFile(srcImgPath: String?): File? {
        if (thumbImgDir == null) thumbImgDir = File(thumbImgDirPath)
        if (!thumbImgDir!!.exists()) thumbImgDir!!.mkdirs()
        val thumbImgName = SystemClock.currentThreadTimeMillis().toString() + getFileNameFromPath(srcImgPath)
        var imageFileThumb: File? = null
        try {
            // 读取图片。
            val `is`: InputStream = FileInputStream(srcImgPath)
            val bmpSource = BitmapFactory.decodeStream(`is`)
            val imageFileSource = File(srcImgPath)
            imageFileSource.createNewFile()
            val fosSource = FileOutputStream(imageFileSource)

            // 保存原图。
            bmpSource.compress(Bitmap.CompressFormat.JPEG, 100, fosSource)

            // 创建缩略图变换矩阵。
            val m = Matrix()
            m.setRectToRect(RectF(0.0f, 0.0f, bmpSource.width.toFloat(), bmpSource.height.toFloat()), RectF(0.0f, 0.0f, 160f, 160f), Matrix.ScaleToFit.CENTER)

            // 生成缩略图。
            val bmpThumb = Bitmap.createBitmap(bmpSource, 0, 0, bmpSource.width, bmpSource.height, m, true)
            imageFileThumb = File(thumbImgDirPath, thumbImgName)
            imageFileThumb.createNewFile()
            val fosThumb = FileOutputStream(imageFileThumb)

            // 保存缩略图。
            bmpThumb.compress(Bitmap.CompressFormat.JPEG, 60, fosThumb)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageFileThumb
    }

    /**
     * 获取压缩后的图片
     *
     * @param res
     * @param resId
     * @param reqWidth  所需图片压缩尺寸最小宽度
     * @param reqHeight 所需图片压缩尺寸最小高度
     * @return
     */
    fun decodeSampledBitmapFromResource(res: Resources?, resId: Int, reqWidth: Int, reqHeight: Int): Bitmap {
        /**
         * 1.获取图片的像素宽高(不加载图片至内存中,所以不会占用资源)
         * 2.计算需要压缩的比例
         * 3.按将图片用计算出的比例压缩,并加载至内存中使用
         */
        // 首先不加载图片,仅获取图片尺寸
        val options = BitmapFactory.Options()
        // 当inJustDecodeBounds设为true时,不会加载图片仅获取图片尺寸信息
        options.inJustDecodeBounds = true
        // 此时仅会将图片信息会保存至options对象内,decode方法不会返回bitmap对象
        BitmapFactory.decodeResource(res, resId, options)

        // 计算压缩比例,如inSampleSize=4时,图片会压缩成原图的1/4
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // 当inJustDecodeBounds设为false时,BitmapFactory.decode...就会返回图片对象了
        options.inJustDecodeBounds = false
        options.inScaled = false
        // 利用计算的比例值获取压缩后的图片对象
        return BitmapFactory.decodeResource(res, resId, options)
    }

    /**
     * 计算压缩比例值
     *
     * @param options   解析图片的配置信息
     * @param reqWidth  所需图片压缩尺寸最小宽度
     * @param reqHeight 所需图片压缩尺寸最小高度
     * @return
     */
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // 保存图片原宽高值
        val height = options.outHeight
        val width = options.outWidth
        // 初始化压缩比例为1
        var inSampleSize = 1

        // 当图片宽高值任何一个大于所需压缩图片宽高值时,进入循环计算系统
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // 压缩比例值每次循环两倍增加,
            // 直到原图宽高值的一半除以压缩值后都~大于所需宽高值为止
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}