package com.ninja.android.lib.view

import android.animation.*
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import com.ninja.android.lib.R

/**
 * Created by szd on 2017/3/28.
 */
class MessageBubbleView : View {
    var mPaint: Paint? = null
    var textPaint: Paint? = null
    var disappearPaint: Paint? = null
    var mPath: Path? = null
    var textMove //字体垂直偏移量
            = 0f
    var centerRadius //中心园半径
            = 0f
    var dragRadius //拖拽圆半径
            = 0f
    var dragCircleX = 0
    var centerCircleX = 0
    var dragCircleY = 0
    var centerCircleY = 0
    var d //两个圆的距离
            = 0f
    var mWidth = 0
    var mHeight = 0
    var mNumber: String? = null
    var maxDragLength //最大可拖拽距离
            = 0
    var textSize //用户设定的字体大小
            = 0f
    var textColor //用户设定的字体颜色
            = 0
    var circleColor //用户设定的圆圈颜色
            = 0
    private var disappearPic: IntArray? = null
    var disappearBitmap: Array<Bitmap?>? = null
    var bitmapRect: Rect? = null
    var bitmapIndex //消失动画播放图片的index
            = 0
    var startDisappear //判断是否正在播放消失动画，防止死循环重复绘制
            = false
    var actionListener: ActionListener? = null

    //当前状态
    var curState = 0

    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MessageBubble)
        circleColor = ta.getColor(R.styleable.MessageBubble_circleColor, Color.RED)
        textColor = ta.getColor(R.styleable.MessageBubble_textColor, Color.WHITE)
        textSize = ta.getDimension(R.styleable.MessageBubble_textSize, sp2px(12f).toFloat())
        centerRadius = ta.getDimension(R.styleable.MessageBubble_radius, dp2px(12f).toFloat())
        mNumber = ta.getString(R.styleable.MessageBubble_textNumber)
        if (mNumber == null) { //防止xml中未给mNumber赋值造成预览时报错
            mNumber = ""
        }
        ta.recycle()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        init()
    }

    private fun init() {
        bringToFront()
        //画圆的Paint
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.color = circleColor
        //画数字的Paint
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint!!.color = textColor
        textPaint!!.textAlign = Paint.Align.CENTER
        textPaint!!.textSize = textSize
        //画消失图片的Paint
        disappearPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        disappearPaint!!.isFilterBitmap = false
        startDisappear = false
        val textFontMetrics = textPaint!!.fontMetrics
        textMove = -textFontMetrics.ascent - (-textFontMetrics.ascent + textFontMetrics.descent) / 2 // drawText从baseline开始
        // ，baseline的值为0，baseline的上面为负值，baseline的下面为正值，即这里ascent为负值，descent为正值,比如ascent为-20
        // ，descent为5，那需要移动的距离就是20 - （20 + 5）/ 2
        mPath = Path()
        if (centerRadius <= 2) { //如果不是第一次创建，上次的拖动删除会因为中心圆半径随着拖放变为零
            centerRadius = dragRadius
        } else {
            dragRadius = centerRadius
        }
        maxDragLength = (4 * dragRadius).toInt()

        //设定圆的初始位置为View正中心
        centerCircleX = width / 2
        centerCircleY = height / 2
        //防止被拖动圆因上一次拖动而未回到原位
        dragCircleX = centerCircleX
        dragCircleX = centerCircleX
        if (disappearPic == null) {
            disappearPic = intArrayOf(R.drawable.explosion_one, R.drawable.explosion_two, R.drawable.explosion_three, R.drawable.explosion_four, R.drawable.explosion_five)
        }
        disappearBitmap = arrayOfNulls(disappearPic!!.size)
        for (i in disappearPic!!.indices) {
            disappearBitmap!![i] = BitmapFactory.decodeResource(resources, disappearPic!![i])
        }
        curState = STATE_NORMAL
    }

    override fun onMeasure(widthMeasure: Int, heightMeasure: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasure)
        val widthSize = MeasureSpec.getSize(widthMeasure)
        val heightMode = MeasureSpec.getMode(heightMeasure)
        val heightSize = MeasureSpec.getSize(heightMeasure)
        mWidth = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            paddingLeft + dp2px(30f) + paddingRight
        }
        mHeight = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            paddingTop + dp2px(30f) + paddingBottom
        }
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                if (curState != STATE_DISAPPEAR) {
                    //计算点击位置与气泡的距离
                    d = Math.hypot((centerCircleX - event.x).toDouble(), (centerCircleY - event.y).toDouble()).toFloat()
                    curState = if (d < centerRadius + 10) {
                        STATE_DRAGING
                    } else {
                        STATE_NORMAL
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                dragCircleX = event.x.toInt()
                dragCircleY = event.y.toInt()
                if (curState == STATE_DRAGING) { //拖拽状态下计算拖拽距离，超出後不再計算
                    d = Math.hypot((centerCircleX - event.x).toDouble(), (centerCircleY - event.y).toDouble()).toFloat()
                    if (d <= maxDragLength - maxDragLength / 7) {
                        centerRadius = dragRadius - d / 4
                        if (actionListener != null) {
                            actionListener!!.onDrag()
                        }
                    } else {
                        centerRadius = 0f
                        curState = STATE_MOVE
                    }
                } else if (curState == STATE_MOVE) { //超出最大拖拽距离，则中间的圆消失
                    if (actionListener != null) {
                        actionListener!!.onMove()
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
                //当正在拖动时，抬起手指才会做响应的处理
                if (curState == STATE_DRAGING || curState == STATE_MOVE) {
                    d = Math.hypot((centerCircleX - event.x).toDouble(), (centerCircleY - event.y).toDouble()).toFloat()
                    if (d > maxDragLength) { //如果拖拽距离大于最大可拖拽距离，则消失
                        curState = STATE_DISAPPEAR
                        startDisappear = true
                        disappearAnim()
                    } else { //小于可拖拽距离，则复原气泡位置
                        restoreAnim()
                    }
                    invalidate()
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        if (mNumber!!.isEmpty()) {
            return
        }
        if (curState == STATE_NORMAL) {
            //画初始圆
            canvas.drawCircle(centerCircleX.toFloat(), centerCircleY.toFloat(), centerRadius, mPaint!!)
            //画数字（要在画完贝塞尔曲线之后绘制，不然会被挡住）
            canvas.drawText(mNumber!!, centerCircleX.toFloat(), centerCircleY + textMove, textPaint!!)
        }
        //如果开始拖拽，则画dragCircle
        if (curState == STATE_DRAGING) {
            //画初始圆
            canvas.drawCircle(centerCircleX.toFloat(), centerCircleY.toFloat(), centerRadius, mPaint!!)
            //画被拖拽的圆
            canvas.drawCircle(dragCircleX.toFloat(), dragCircleY.toFloat(), dragRadius, mPaint!!)
            drawBezier(canvas)
            canvas.drawText(mNumber!!, dragCircleX.toFloat(), dragCircleY + textMove, textPaint!!)
        }
        if (curState == STATE_MOVE) {
            canvas.drawCircle(dragCircleX.toFloat(), dragCircleY.toFloat(), dragRadius, mPaint!!)
            canvas.drawText(mNumber!!, dragCircleX.toFloat(), dragCircleY + textMove, textPaint!!)
        }
        if (curState == STATE_DISAPPEAR && startDisappear) {
            if (disappearBitmap != null) {
                canvas.drawBitmap(disappearBitmap!![bitmapIndex]!!, null, bitmapRect!!, disappearPaint)
            }
        }
    }

    /**
     * 气泡消失动画
     */
    private fun disappearAnim() {
        bitmapRect = Rect(dragCircleX - dragRadius.toInt(), dragCircleY - dragRadius.toInt(), dragCircleX + dragRadius.toInt(), dragCircleY + dragRadius.toInt())
        val disappearAnimator = ValueAnimator.ofInt(0, disappearBitmap!!.size)
        disappearAnimator.addUpdateListener { animation ->
            bitmapIndex = animation.animatedValue as Int
            invalidate()
        }
        disappearAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                startDisappear = false
                if (actionListener != null) {
                    actionListener!!.onDisappear()
                }
            }
        })
        disappearAnimator.interpolator = LinearInterpolator()
        disappearAnimator.duration = 500
        disappearAnimator.start()
    }

    /**
     * 气泡复原动画
     */
    private fun restoreAnim() {
        val valueAnimator = ValueAnimator.ofObject(MyPointFEvaluator(), PointF(dragCircleX.toFloat(), dragCircleY.toFloat()), PointF(centerCircleX.toFloat(), centerCircleY.toFloat()))
        valueAnimator.duration = 200
        valueAnimator.interpolator = TimeInterpolator { input ->
            val f = 0.571429f
            (Math.pow(2.0, (-4 * input).toDouble()) * Math.sin((input - f / 4) * (2 * Math.PI) / f) + 1).toFloat()
        }
        valueAnimator.addUpdateListener { animation ->
            val pointF = animation.animatedValue as PointF
            dragCircleX = pointF.x.toInt()
            dragCircleY = pointF.y.toInt()
            invalidate()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                //复原了
                centerRadius = dragRadius
                curState = STATE_NORMAL
                if (actionListener != null) {
                    actionListener!!.onRestore()
                }
            }
        })
        valueAnimator.start()
    }

    /**
     * 绘制贝塞尔曲线
     *
     * @param canvas canvas
     */
    private fun drawBezier(canvas: Canvas) {
        val controlX = ((centerCircleX + dragCircleX) / 2).toFloat() //贝塞尔曲线控制点X坐标
        val controlY = ((dragCircleY + centerCircleY) / 2).toFloat() //贝塞尔曲线控制点Y坐标
        //计算曲线的起点终点
        d = Math.hypot((centerCircleX - dragCircleX).toDouble(), (centerCircleY - dragCircleY).toDouble()).toFloat()
        val sin = (centerCircleY - dragCircleY) / d
        val cos = (centerCircleX - dragCircleX) / d
        val dragCircleStartX = dragCircleX - dragRadius * sin
        val dragCircleStartY = dragCircleY + dragRadius * cos
        val centerCircleEndX = centerCircleX - centerRadius * sin
        val centerCircleEndY = centerCircleY + centerRadius * cos
        val centerCircleStartX = centerCircleX + centerRadius * sin
        val centerCircleStartY = centerCircleY - centerRadius * cos
        val dragCircleEndX = dragCircleX + dragRadius * sin
        val dragCircleEndY = dragCircleY - dragRadius * cos
        mPath!!.reset()
        mPath!!.moveTo(centerCircleStartX, centerCircleStartY)
        mPath!!.quadTo(controlX, controlY, dragCircleEndX, dragCircleEndY)
        mPath!!.lineTo(dragCircleStartX, dragCircleStartY)
        mPath!!.quadTo(controlX, controlY, centerCircleEndX, centerCircleEndY)
        mPath!!.close()
        canvas.drawPath(mPath!!, mPaint!!)
    }

    /**
     * 重置
     */
    fun resetBezierView() {
        init()
        invalidate()
    }

    /**
     * 设置显示的消息数量(超过99需要自己定义为"99+")
     *
     * @param number 消息的数量
     */
    fun setNumber(number: String?) {
        mNumber = number
        invalidate()
    }

    /**
     * 设置消失动画
     *
     * @param disappearPic 动画图片
     */
    fun setDisappearPic(disappearPic: IntArray?) {
        if (disappearPic != null) {
            this.disappearPic = disappearPic
        }
    }

    interface ActionListener {
        /**
         * 被拖动时
         */
        fun onDrag()

        /**
         * 消失后
         */
        fun onDisappear()

        /**
         * 拖动距离不足，气泡回到原位后
         */
        fun onRestore()

        /**
         * 拖动时超出了最大粘连距离，气泡单独移动时
         */
        fun onMove()
    }

    fun setOnActionListener(actionListener: ActionListener?) {
        this.actionListener = actionListener
    }

    /**
     * PointF动画估值器(复原时的振动动画)
     */
    private inner class MyPointFEvaluator : TypeEvaluator<PointF> {
        override fun evaluate(fraction: Float, startPointF: PointF, endPointF: PointF): PointF {
            val x = startPointF.x + fraction * (endPointF.x - startPointF.x)
            val y = startPointF.y + fraction * (endPointF.y - startPointF.y)
            return PointF(x, y)
        }
    }

    companion object {
        private const val TAG = "BezierView"

        //原位
        var STATE_NORMAL = 0

        //消失
        var STATE_DISAPPEAR = 1

        //拖拽
        var STATE_DRAGING = 2

        //移动（无粘连效果）
        var STATE_MOVE = 3
        fun dp2px(dpValue: Float): Int {
            val scale = Resources.getSystem().displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        fun sp2px(spValue: Float): Int {
            val fontScale = Resources.getSystem().displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }
    }
}