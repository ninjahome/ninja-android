package com.ninjahome.ninja.utils

import android.graphics.Canvas
import android.graphics.Paint

object DrawUtils {
    /**
     * 测量文本内容宽度
     *
     * @param text  文本内容
     * @param paint 画笔
     * @return 内容宽度
     */
    fun measureTextWidth(text: String?, paint: Paint): Float {
        return paint.measureText(text)
    }

    /**
     * 测量文本内容高度
     *
     * @param paint 画笔
     * @return 内容高度
     */
    fun measureTextHeight(paint: Paint): Float {
        val fm = paint.fontMetrics
        return fm.bottom - fm.top
    }

    /**
     * 文本内容相对于centerX点居中
     *
     * @param canvas  画布
     * @param paint   画笔
     * @param text    文本内容
     * @param centerX x轴方向中心点
     * @param y       文本内容y轴方向偏移量
     */
    fun drawTextByCenterX(canvas: Canvas, paint: Paint, text: String?, centerX: Float, y: Float) {
        canvas.drawText(text!!, (centerX - measureTextWidth(text, paint) * 0.5).toFloat(), y, paint)
    }

    /**
     * 文本内容相对于centerY点居中
     *
     * @param canvas  画布
     * @param paint   画笔
     * @param text    文本内容
     * @param x       文本内容x轴方向偏移量
     * @param centerY y轴方向中心点
     */
    fun drawTextByCenterY(canvas: Canvas, paint: Paint, text: String?, x: Float, centerY: Float) {
        val bottom = paint.fontMetrics.bottom
        canvas.drawText(text!!, x, (0.5 * measureTextHeight(paint) - bottom + centerY).toFloat(), paint)
    }

    /**
     * 文本内容相对于某一点居中
     *
     * @param canvas  画布
     * @param paint   画笔
     * @param text    文本内容
     * @param centerX x轴方向中心点
     * @param centerY y轴方向中心点
     */
    fun drawTextInCenter(canvas: Canvas, paint: Paint, text: String?, centerX: Float, centerY: Float) {
        val bottom = paint.fontMetrics.bottom
        canvas.drawText(text!!, (centerX - measureTextWidth(text, paint) * 0.5).toFloat(), (0.5 * measureTextHeight(paint) - bottom + centerY).toFloat(), paint)
    }
}