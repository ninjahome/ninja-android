package com.ninjahome.ninja.view

import android.graphics.PointF
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs


class StackLayoutManager : RecyclerView.LayoutManager() {


    //设置每个子view的LayoutParams
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    //是否自动测量
    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }

    //表示支持横向滑动。但是滑动逻辑需要自己实现
    override fun canScrollHorizontally(): Boolean {
        return true
    }

    /**
     * 布局初始化的方法
     * 键盘弹出或收起会重新回调这个方法
     * scrollToPosition也会，smoothScrollToPosition不会
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0) {//没有Item，界面空着吧
            detachAndScrapAttachedViews(recycler)
            return
        }
        if (childCount == 0 && state.isPreLayout()) {//state.isPreLayout()是支持动画的
            return
        }
        detachAndScrapAttachedViews(recycler)

//        recycler.getViewForPosition()
        //测量子view
//        measureChild()
//        measureChildWithMargins()

        //获取view的宽高
//        getDecoratedMeasuredWidth(View child)
//        getDecoratedMeasuredHeight(View child)

        //布局子view
//        layoutDecorated(View child, int left, int top, int right, int bottom)
//        layoutDecoratedWithMargins(View child, int left, int top, int right, int bottom)

    }



}