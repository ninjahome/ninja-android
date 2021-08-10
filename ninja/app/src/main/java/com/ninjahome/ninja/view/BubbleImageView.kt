package com.ninjahome.ninja.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import com.ninjahome.ninja.R

/**
 * @描述 气泡型ImageView
 */
class BubbleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ImageView(context, attrs, defStyle) {
    private var mAngle = dp2px(10)
    private var mArrowTop = dp2px(40)
    private var mArrowWidth = dp2px(20)
    private var mArrowHeight = dp2px(20)
    private var mArrowOffset = 0
    private var mArrowLocation = LOCATION_LEFT
    private var mDrawableRect: Rect? = null
    private var mBitmap: Bitmap? = null
    private var mBitmapShader: BitmapShader? = null
    private var mBitmapPaint: Paint? = null
    private var mShaderMatrix: Matrix? = null
    private var mBitmapWidth = 0
    private var mBitmapHeight = 0
    private val mPaint: Paint
    private var percent = 0
    private var mShowText = true //是否显示文字
    private var mShowShadow = true //是否显示阴影

    /**
     * 是否显示阴影
     */
    fun showShadow(showShadow: Boolean) {
        mShowShadow = showShadow
        postInvalidate()
    }

    /**
     * 设置进度的百分比
     */
    fun setPercent(percent: Int) {
        this.percent = percent
        postInvalidate()
    }

    /**
     * 设置进度文字是否显示
     */
    fun setProgressVisible(show: Boolean) {
        mShowText = show
        postInvalidate()
    }

    private fun initView(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.BubbleImageView)
            mAngle = a.getDimension(R.styleable.BubbleImageView_bubble_angle, mAngle.toFloat()).toInt()
            mArrowHeight = a.getDimension(R.styleable.BubbleImageView_bubble_arrowHeight, mArrowHeight.toFloat()).toInt()
            mArrowOffset = a.getDimension(R.styleable.BubbleImageView_bubble_arrowOffset, mArrowOffset.toFloat()).toInt()
            mArrowTop = a.getDimension(R.styleable.BubbleImageView_bubble_arrowTop, mArrowTop.toFloat()).toInt()
            mArrowWidth = a.getDimension(R.styleable.BubbleImageView_bubble_arrowWidth, mAngle.toFloat()).toInt()
            mArrowLocation = a.getInt(R.styleable.BubbleImageView_bubble_arrowLocation, mArrowLocation)
            mShowText = a.getBoolean(R.styleable.BubbleImageView_bubble_showText, mShowText)
            mShowShadow = a.getBoolean(R.styleable.BubbleImageView_bubble_showShadow, mShowShadow)
            a.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            return
        }
        val rect = RectF(paddingLeft.toFloat(), paddingTop.toFloat(), (right - left - paddingRight).toFloat(), (bottom - top - paddingBottom).toFloat())
        val path = Path()
        if (mArrowLocation == LOCATION_LEFT) {
            leftPath(rect, path)
        } else {
            rightPath(rect, path)
        }
        canvas.drawPath(path, mBitmapPaint!!)
        drawText(canvas, mAngle)
    }

    /**
     * 画进度文字和设置透明度
     *
     * @param canvas
     * @param radiusPx 圆角的半径
     */
    private fun drawText(canvas: Canvas, radiusPx: Int) {
        mPaint.isAntiAlias = true // 消除锯齿
        mPaint.style = Paint.Style.FILL
        if (mShowShadow) { //根据是否要画阴影
            // 画阴影部分
            mPaint.color = Color.parseColor("#70000000") // 半透明
            var shadowRect: Rect? = null
            shadowRect = if (mArrowLocation == LOCATION_LEFT) {
                //如果是在左边
                Rect(mArrowWidth, 0, width, height - height * percent / 100) //阴影的宽度（图片的宽度）为ImageView的宽度减去箭头的宽度
            } else {
                Rect(0, 0, width - mArrowWidth, height - height * percent / 100) //阴影的宽度（图片的宽度）为ImageView的宽度减去箭头的宽度
            }
            val shadowRectF = RectF(shadowRect)
            //shadowRectF.set(0, 0, getWidth(), getHeight() - getHeight()* percent / 100 );
            canvas.drawRoundRect(shadowRectF, radiusPx.toFloat(), radiusPx.toFloat(), mPaint)
        }
        if (mShowText) { //是否画文字
            //画文字
            mPaint.textSize = 30f
            mPaint.color = Color.parseColor("#FFFFFF")
            mPaint.strokeWidth = 2f
            var rect: Rect? = null
            var marginLeft = 0 //文字的左边距
            if (mArrowLocation == LOCATION_LEFT) { //如果是向左的
                rect = Rect(mArrowWidth, 0, 0, 0)
                marginLeft = (width - mArrowWidth) / 2
            } else {
                rect = Rect(mArrowWidth, 0, 0, 0)
                marginLeft = width / 2 - mArrowWidth
            }
            mPaint.getTextBounds("100%", 0, "100%".length, rect) // 确定文字的宽度
            canvas.drawText("$percent%", marginLeft.toFloat(), (height / 2).toFloat(), mPaint)
        }
    }

    fun rightPath(rect: RectF, path: Path) {
        path.moveTo(mAngle.toFloat(), rect.top)
        path.lineTo(rect.width(), rect.top)
        path.arcTo(RectF(rect.right - mAngle * 2 - mArrowWidth, rect.top, rect.right - mArrowWidth, mAngle * 2 + rect.top), 270f, 90f)
        path.lineTo(rect.right - mArrowWidth, mArrowTop.toFloat())
        path.lineTo(rect.right, (mArrowTop - mArrowOffset).toFloat())
        path.lineTo(rect.right - mArrowWidth, (mArrowTop + mArrowHeight).toFloat())
        path.lineTo(rect.right - mArrowWidth, rect.height() - mAngle)
        path.arcTo(RectF(rect.right - mAngle * 2 - mArrowWidth, rect.bottom - mAngle * 2, rect.right - mArrowWidth, rect.bottom), 0f, 90f)
        path.lineTo(rect.left, rect.bottom)
        path.arcTo(RectF(rect.left, rect.bottom - mAngle * 2, mAngle * 2 + rect.left, rect.bottom), 90f, 90f)
        path.lineTo(rect.left, rect.top)
        path.arcTo(RectF(rect.left, rect.top, mAngle * 2 + rect.left, mAngle * 2 + rect.top), 180f, 90f)
        path.close()
    }

    fun leftPath(rect: RectF, path: Path) {
        path.moveTo((mAngle + mArrowWidth).toFloat(), rect.top)
        path.lineTo(rect.width(), rect.top)
        path.arcTo(RectF(rect.right - mAngle * 2, rect.top, rect.right, mAngle * 2 + rect.top), 270f, 90f)
        path.lineTo(rect.right, rect.top)
        path.arcTo(RectF(rect.right - mAngle * 2, rect.bottom - mAngle * 2, rect.right, rect.bottom), 0f, 90f)
        path.lineTo(rect.left + mArrowWidth, rect.bottom)
        path.arcTo(RectF(rect.left + mArrowWidth, rect.bottom - mAngle * 2, mAngle * 2 + rect.left + mArrowWidth, rect.bottom), 90f, 90f)
        path.lineTo(rect.left + mArrowWidth, (mArrowTop + mArrowHeight).toFloat())
        path.lineTo(rect.left, (mArrowTop - mArrowOffset).toFloat())
        path.lineTo(rect.left + mArrowWidth, mArrowTop.toFloat())
        path.lineTo(rect.left + mArrowWidth, rect.top)
        path.arcTo(RectF(rect.left + mArrowWidth, rect.top, mAngle * 2 + rect.left + mArrowWidth, mAngle * 2 + rect.top), 180f, 90f)
        path.close()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        mBitmap = bm
        setup()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        mBitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        mBitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else try {
            val bitmap: Bitmap
            bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG)
            } else {
                Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, BITMAP_CONFIG)
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: OutOfMemoryError) {
            null
        }
    }

    private fun setup() {
        if (mBitmap == null) {
            return
        }
        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint = Paint()
        mBitmapPaint!!.isAntiAlias = true
        mBitmapPaint!!.shader = mBitmapShader
        mBitmapHeight = mBitmap!!.height
        mBitmapWidth = mBitmap!!.width
        updateShaderMatrix()
        invalidate()
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f
        mShaderMatrix = Matrix()
        mShaderMatrix!!.set(null)
        mDrawableRect = Rect(0, 0, right - left, bottom - top)
        if (mBitmapWidth * mDrawableRect!!.height() > mDrawableRect!!.width() * mBitmapHeight) {
            scale = mDrawableRect!!.height() / mBitmapHeight.toFloat()
            dx = (mDrawableRect!!.width() - mBitmapWidth * scale) * 0.5f
        } else {
            scale = mDrawableRect!!.width() / mBitmapWidth.toFloat()
            dy = (mDrawableRect!!.height() - mBitmapHeight * scale) * 0.5f
        }
        mShaderMatrix!!.setScale(scale, scale)
        mShaderMatrix!!.postTranslate((dx + 0.5f), (dy + 0.5f))
        mBitmapShader!!.setLocalMatrix(mShaderMatrix)
    }

    private fun dp2px(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    companion object {
        private const val LOCATION_LEFT = 0
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private const val COLORDRAWABLE_DIMENSION = 1
    }

    init {
        initView(attrs)
        mPaint = Paint()
    }
}