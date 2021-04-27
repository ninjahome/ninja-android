package com.ninjahome.ninja.view.contacts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup
import com.ninjahome.ninja.view.contacts.ColorUtil.setPaintColor

/**
 * Created by MQ on 2017/5/18.
 */
class IndexBar @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(
    mContext, attrs, defStyleAttr
) {
    private var mHeight = 0
    private var mWidth = 0
    private var mPaint: Paint? = null
    private var centerY = 0f
    private var tag = ""
    private var isShowTag = false
    private var position = 0
    private val circleRadius = 100f
    private fun init(attrs: AttributeSet?) {
        setWillNotDraw(false)
        mPaint = Paint()
        mPaint!!.color = Color.RED
        mPaint!!.isAntiAlias = true
        mPaint!!.isDither = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mHeight = h
        mWidth = w
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        //MeasureSpec封装了父View传给子View的布局要求
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)
        when (wMode) {
            MeasureSpec.EXACTLY -> mWidth = wSize
            MeasureSpec.AT_MOST -> mWidth = wSize
            MeasureSpec.UNSPECIFIED -> {
            }
        }
        when (hMode) {
            MeasureSpec.EXACTLY -> mHeight = hSize
            MeasureSpec.AT_MOST -> mHeight = hSize
            MeasureSpec.UNSPECIFIED -> {
            }
        }
        setMeasuredDimension(mWidth, mHeight)
    }

    private var childWidth = 0
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childNum = childCount
        if (childNum <= 0) return
        //得到SideBar
        val childView = getChildAt(0)
        childWidth = childView.measuredWidth
        //把SideBar排列到最右侧
        childView.layout(mWidth - childWidth, 0, mWidth, mHeight)
    }

    /**
     * @param centerY  要绘制的圆的Y坐标
     * @param tag      要绘制的字母Tag
     * @param position 字母Tag所在位置
     */
    fun setDrawData(centerY: Float, tag: String, position: Int) {
        this.position = position
        this.centerY = centerY
        this.tag = tag
        isShowTag = true
        invalidate()
    }

    /**
     * 通过标志位来控制是否来显示圆
     *
     * @param isShowTag 是否显示圆
     */
    fun setTagStatus(isShowTag: Boolean) {
        this.isShowTag = isShowTag
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isShowTag) {
            //根据位置来不断变换Paint的颜色
            setPaintColor(mPaint!!, position)
            //绘制圆和文字
            canvas.drawCircle(
                ((mWidth - childWidth) / 2).toFloat(),
                centerY,
                circleRadius,
                mPaint!!
            )
            mPaint!!.color = Color.WHITE
            mPaint!!.textSize = 80f
            canvas.drawText(
                tag,
                (mWidth - childWidth - mPaint!!.measureText(tag)) / 2,
                centerY - (mPaint!!.ascent() + mPaint!!.descent()) / 2,
                mPaint!!
            )
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return super.generateDefaultLayoutParams()
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return super.generateLayoutParams(p)
    }

    init {
        init(attrs)
    }
}