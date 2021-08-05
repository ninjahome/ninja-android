package com.ninjahome.ninja.view.contacts

import android.graphics.*
import android.graphics.drawable.Drawable
import com.ninja.android.lib.provider.context
import com.ninja.android.lib.utils.dp
import com.ninjahome.ninja.R

/**
 *Author:Mr'x
 *Time:2021/8/5
 *Description:
 */
class ConversationGroupIcon(var subName: String, var fontSize: Float = 30f, var textColor: Int = Color.WHITE) : Drawable() {
    val textRect = Rect()
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = textColor
        this.textSize = fontSize
    }

    override fun draw(canvas: Canvas) {
        mPaint.getTextBounds(subName, 0, subName.length, textRect)
        canvas.drawBitmap(BitmapFactory.decodeResource(context().resources, R.drawable.bg_conversation_group_avatar), 0.0f, 0.0f, mPaint)
        canvas.drawText(subName, (intrinsicWidth - textRect.width()) / 2f, intrinsicHeight / 2 - (mPaint.descent() + mPaint.ascent()) / 2, mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun getIntrinsicHeight(): Int {
        return 48.dp.toInt()
    }

    override fun getIntrinsicWidth(): Int {
        return 48.dp.toInt()
    }
}