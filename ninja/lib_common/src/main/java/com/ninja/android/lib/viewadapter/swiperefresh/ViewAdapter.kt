package com.ninja.android.lib.viewadapter.swiperefresh

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ninja.android.lib.command.BindingCommand


//下拉刷新命令
@BindingAdapter("onRefreshCommand")
fun onRefreshCommand(swipeRefreshLayout: SwipeRefreshLayout, onRefreshCommand: BindingCommand<*>?) {
    swipeRefreshLayout.setOnRefreshListener { onRefreshCommand?.execute() }
}

//是否刷新中
@BindingAdapter("refreshing")
fun setRefreshing(swipeRefreshLayout: SwipeRefreshLayout, refreshing: Boolean) {
    swipeRefreshLayout.isRefreshing = refreshing
}