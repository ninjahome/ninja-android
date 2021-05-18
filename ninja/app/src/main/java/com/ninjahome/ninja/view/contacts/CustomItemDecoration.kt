package com.ninjahome.ninja.view.contacts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ninja.android.lib.utils.dp
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.view.contacts.ColorUtil.setPaintColor

/**
 * Created by MQ on 2017/5/8.
 */
class CustomItemDecoration(private val mContext: Context) : ItemDecoration() {
    private val mPaint: Paint
    private var mBeans: List<Contact>? = null
    private val mBounds = Rect()
    private var tagsStr: String? = null
    fun setDatas(mBeans: List<Contact>?, tagsStr: String?) {
        this.mBeans = mBeans
        this.tagsStr = tagsStr
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null) {
            return
        }
        canvas.save()
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val position = params.viewLayoutPosition
            if (mBeans == null || mBeans!!.size == 0 || mBeans!!.size <= position || position < 0) {
                continue
            }
            if (position == 0) {
                //第一条数据有bar
                drawTitleBar(canvas, parent, child, mBeans!![position], tagsStr!!.indexOf(mBeans!![position].indexTag))
            } else if (position > 0) {
                if (TextUtils.isEmpty(mBeans!![position].indexTag)) continue
                //与上一条数据中的tag不同时，该显示bar了
                if (mBeans!![position].indexTag != mBeans!![position - 1].indexTag) {
                    drawTitleBar(canvas, parent, child, mBeans!![position], tagsStr!!.indexOf(mBeans!![position].indexTag))
                }
            }
        }
        canvas.restore()
    }

    /**
     * 绘制bar
     *
     * @param canvas Canvas
     * @param parent RecyclerView
     * @param child  ItemView
     */
    private fun drawTitleBar(canvas: Canvas, parent: RecyclerView, child: View, bean: Contact, position: Int) {
        val left = 0
        val right = parent.width
        //返回一个包含Decoration和Margin在内的Rect
        parent.getDecoratedBoundsWithMargins(child, mBounds)
        val top = mBounds.top
        val bottom = mBounds.top + Math.round(ViewCompat.getTranslationY(child)) + dividerHeight
        mPaint.color = Color.WHITE
        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
        //根据位置不断变换Paint的颜色
        setPaintColor(mPaint, position)
        mPaint.textSize = 40f
        canvas.drawCircle(43.dp, (bottom - dividerHeight / 2).toFloat(), 35f, mPaint)
        mPaint.color = Color.WHITE
        canvas.drawText(bean.indexTag, 43.dp, (bottom - dividerHeight / 3).toFloat(), mPaint)
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        //用来绘制悬浮框
        val position = (parent.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
        if (mBeans == null || mBeans!!.size == 0 || mBeans!!.size <= position || position < 0) {
            return
        }
        val bottom = parent.paddingTop + dividerHeight
        mPaint.color = Color.WHITE
        canvas.drawRect(parent.left.toFloat(), parent.paddingTop.toFloat(), (parent.right - parent.paddingRight).toFloat(), (parent.paddingTop + dividerHeight).toFloat(), mPaint)
        setPaintColor(mPaint, tagsStr!!.indexOf(mBeans!![position].indexTag))
        mPaint.textSize = 40f
        canvas.drawCircle(43.dp, (bottom - dividerHeight / 2).toFloat(), 35f, mPaint)
        mPaint.color = Color.WHITE
        canvas.drawText(mBeans!![position].indexTag, 43.dp, (bottom - dividerHeight / 3).toFloat(), mPaint)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (mBeans == null || mBeans!!.size == 0 || mBeans!!.size <= position || position < 0) {
            super.getItemOffsets(outRect, view, parent, state)
            return
        }
        if (position == 0) {
            //第一条数据有bar
            outRect[0, dividerHeight, 0] = 0
        } else if (position > 0) {
            if (TextUtils.isEmpty(mBeans!![position].indexTag)) return
            //与上一条数据中的tag不同时，该显示bar了
            if (mBeans!![position].indexTag != mBeans!![position - 1].indexTag) {
                outRect[0, dividerHeight, 0] = 0
            }
        }
    }

    companion object {
        private const val dividerHeight = 80
    }

    init {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.textAlign = Paint.Align.CENTER
    }
}