package com.ninjahome.ninja.view.contacts

import android.graphics.Color
import android.graphics.Paint
import com.ninjahome.ninja.R
import java.util.*

/**
 * Created by MQ on 2017/5/18.
 */
object ColorUtil {
    val colors = Arrays.asList(R.color.color_f4cce3,R.color.color_d6ccf4,
    R.color.color_bacef0,R.color.color_abddee,R.color.color_cbeea8,R.color.color_baf1e6,
    R.color.color_fae5a6,R.color.color_f0b5b2,R.color.color_acefba,R.color.color_bad4ee,
    R.color.color_ebefae,R.color.color_f2c2b4
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