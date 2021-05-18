package com.ninjahome.ninja.view.contacts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.ninja.android.lib.utils.dp

/**
 * Created by MQ on 2017/5/17.
 */
class SideBar @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(mContext, attrs, defStyleAttr) {
    private var mHeight = 0
    private var mWidth = 0
    private var mPaint: Paint? = null
    private var singleHeight = 0
    private var listener: indexChangeListener? = null
    private val TOTAL_MARGIN = 160
    private val TOP_MARGIN = 80
    private fun init() {
        mPaint = Paint()
        mPaint!!.isDither = true
        mPaint!!.isAntiAlias = true
        mPaint!!.color = Color.GRAY
        mPaint!!.textSize = 35f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //导航栏居中显示，上下各有80dp的边距
        mHeight = (h - TOTAL_MARGIN.dp).toInt()
        mWidth = w
        if (indexStr.length != 0) {
            singleHeight = mHeight / indexStr.length
        }
    }

    override fun onDraw(canvas: Canvas) {
        for (i in 0 until indexStr.length) {
            val textTag = indexStr.substring(i, i + 1)
            val xPos = (mWidth - mPaint!!.measureText(textTag)) / 2
            canvas.drawText(textTag, xPos, singleHeight * (i + 1) + TOP_MARGIN.dp, mPaint!!)
        }
    }

    private var indexStr = "ABCDEFGHIJKLMNOPQRSTUVWXY#"
    fun setIndexStr(indexStr: String) {
        this.indexStr = indexStr
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //按下
                mPaint!!.color = Color.BLACK
                invalidate()
                //滑动 event.getY()得到在父View中的Y坐标，通过和总高度的比例再乘以字符个数总长度得到按下的位置
                val position = ((event.y - top - 80.dp) / mHeight * indexStr.toCharArray().size).toInt()
                if (position >= 0 && position < indexStr.length) {
                    (parent as IndexBar).setDrawData(event.y, indexStr.toCharArray()[position].toString(), position)
                    if (listener != null) {
                        listener!!.indexChanged(indexStr.substring(position, position + 1))
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val position = ((event.y - top - 80.dp) / mHeight * indexStr.toCharArray().size).toInt()
                if (position >= 0 && position < indexStr.length) {
                    (parent as IndexBar).setDrawData(event.y, indexStr.toCharArray()[position].toString(), position)
                    if (listener != null) {
                        listener!!.indexChanged(indexStr.substring(position, position + 1))
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                //抬起
                (parent as IndexBar).setTagStatus(false)
                mPaint!!.color = Color.GRAY
                invalidate()
            }
        }
        return true
    }

    interface indexChangeListener {
        fun indexChanged(tag: String?)
    }

    fun setIndexChangeListener(listener: indexChangeListener?) {
        this.listener = listener
    }

    init {
        init()
    }
}