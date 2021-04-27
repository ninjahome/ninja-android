package com.ninjahome.ninja.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import com.journeyapps.barcodescanner.ViewfinderView
import com.ninja.android.lib.utils.dp
import com.ninjahome.ninja.R

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ZxingViewfinderView(context: Context, val attrs: AttributeSet) : ViewfinderView(context, attrs) {

    private val DIFFERENCE = 6.dp

    private val rectPaint = Paint().apply {
        color = context.resources.getColor(R.color.color_26253c, null)
        strokeWidth = 2.dp
        style = Paint.Style.STROKE

    }

    private val innerRectPaint = Paint().apply {
        color = context.resources.getColor(R.color.color_26253c, null)
        strokeWidth = 1.dp
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (cameraPreview.framingRect == null) {
            return
        }
        val frame = cameraPreview.framingRect
        //画外方框
        canvas.drawRoundRect(frame.left.toFloat(), frame.top.toFloat(), frame.right.toFloat(), frame.bottom.toFloat(), 1.dp, 1.dp, rectPaint)
        //画内方框
        canvas.drawRoundRect(frame.left.toFloat() + DIFFERENCE, frame.top.toFloat() + DIFFERENCE, frame.right.toFloat() - DIFFERENCE, frame.bottom.toFloat() - DIFFERENCE, 1.dp, 1.dp, innerRectPaint)
    }
}