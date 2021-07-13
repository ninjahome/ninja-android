package com.ninjahome.ninja.view.contacts

import android.graphics.Color
import android.graphics.Paint
import com.ninjahome.ninja.R
import java.util.*

/**
 * Created by MQ on 2017/5/18.
 */
object ColorUtil {
    val colors = Arrays.asList(R.color.color_F4CCE3,R.color.color_D6CCF4,
    R.color.color_BACEF0,R.color.color_ABDDEE,R.color.color_CBEEA8,R.color.color_BAF1E6,
    R.color.color_FAE5A6,R.color.color_F0B5B2,R.color.color_ACEFBA,R.color.color_BAD4EE,
    R.color.color_EBEFAE,R.color.color_F2C2B4
    )
    val colorSize = colors.size.toLong()
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