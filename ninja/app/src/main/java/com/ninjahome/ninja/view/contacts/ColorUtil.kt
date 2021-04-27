package com.ninjahome.ninja.view.contacts

import android.graphics.Color
import android.graphics.Paint

/**
 * Created by MQ on 2017/5/18.
 */
object ColorUtil {
    /**
     * 根据数据位置来给Paint循环设置颜色
     *
     * @param mPaint   Paint
     * @param position position
     */
    @JvmStatic
    fun setPaintColor(mPaint: Paint, position: Int) {
        val pos = position % 6
        when (pos) {
            0 -> mPaint.color = Color.parseColor("#EC5745")
            1 -> mPaint.color = Color.parseColor("#377caf")
            2 -> mPaint.color = Color.parseColor("#4ebcd3")
            3 -> mPaint.color = Color.parseColor("#6fb30d")
            4 -> mPaint.color = Color.parseColor("#FFA500")
            5 -> mPaint.color = Color.parseColor("#bf9e5a")
        }
    }
}