package com.ninjahome.ninja.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * 设置viewpager不可滑动 默认可以滑动
 *
 */
class NoScrollViewPager : ViewPager {
    private var isScroll = false

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    /**
     * Sets scroll. true 可以滑动  false 不可以滑动
     *
     * @param isScroll the is scroll
     */
    fun setScroll(isScroll: Boolean) {
        this.isScroll = isScroll
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return isScroll && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return isScroll && super.onInterceptTouchEvent(ev)
    }

    override fun setCurrentItem(item: Int) {
        setCurrentItem(item, false)
    }
}