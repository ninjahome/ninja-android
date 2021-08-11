package com.ninjahome.ninja.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.widget.Toast
import com.ninjahome.ninja.NinjaApp
import com.zhy.autolayout.utils.ScreenUtils

object UIUtils {
    var mToast: Toast? = null
    var screenWidth = 0
    var screenHeight = 0
    var screenMin // 宽高中，小的一边
            = 0
    var screenMax // 宽高中，较大的值
            = 0
    var density = 0f
    var scaleDensity = 0f
    var xdpi = 0f
    var ydpi = 0f
    var densityDpi = 0
    var statusbarheight = 0
    var navbarheight = 0

    /**
     * 得到上下文
     */
    val context: Context
        get() = NinjaApp.instance.applicationContext

    /**
     * 得到resources对象
     */
    val resource: Resources
        get() = context.resources

    /**
     * 得到string.xml中的字符串
     */
    fun getString(resId: Int): String {
        return resource.getString(resId)
    }

    /**
     * 得到string.xml中的字符串，带点位符
     */
    fun getString(id: Int, vararg formatArgs: Any?): String {
        return resource.getString(id, *formatArgs)
    }

    /**
     * 得到string.xml中和字符串数组
     */
    fun getStringArr(resId: Int): Array<String> {
        return resource.getStringArray(resId)
    }

    /**
     * 得到colors.xml中的颜色
     */
    fun getColor(colorId: Int): Int {
        return resource.getColor(colorId)
    }

    /**
     * 得到应用程序的包名
     */
    val packageName: String
        get() = context.packageName

    /**
     * dip-->px
     */
    fun dip2Px(dip: Int): Int {
        // px/dip = density;
        // density = dpi/160
        // 320*480 density = 1 1px = 1dp
        // 1280*720 density = 2 2px = 1dp
        val density = resource.displayMetrics.density
        return (dip * density + 0.5f).toInt()
    }

    /**
     * px-->dip
     */
    fun px2dip(px: Int): Int {
        val density = resource.displayMetrics.density
        return (px / density + 0.5f).toInt()
    }

    /**
     * sp-->px
     */
    fun sp2px(sp: Int): Int {
        return (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), resource.displayMetrics) + 0.5f).toInt()
    }

    val displayWidth: Int
        get() {
            if (screenWidth == 0) {
                context
            }
            return screenWidth
        }
    val displayHeight: Int
        get() {
            if (screenHeight == 0) {
                context
            }
            return screenHeight
        }

    fun GetInfo(context: Context?) {
        if (null == context) {
            return
        }
        val dm = context.applicationContext.resources.displayMetrics
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels
        screenMin = Math.min(screenWidth, screenHeight)
        screenMax = Math.max(screenWidth, screenHeight)
        density = dm.density
        scaleDensity = dm.scaledDensity
        xdpi = dm.xdpi
        ydpi = dm.ydpi
        densityDpi = dm.densityDpi
        statusbarheight = ScreenUtils.getStatusBarHeight(context)
        navbarheight = getNavBarHeight(context)
    }

    fun getNavBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    fun getVersion(context: Context): String {
        return try {
            val manager = context.packageManager
            val info = manager.getPackageInfo(context.packageName, 0)
            info.versionName
        } catch (e: Exception) {
            e.printStackTrace()
            "无法获取到版本号"
        }
    }
}