package com.ninja.android.lib.viewadapter.listview

import android.widget.AbsListView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.databinding.BindingAdapter
import com.ninja.android.lib.command.BindingCommand


@BindingAdapter(
    value = ["onScrollChangeCommand", "onScrollStateChangedCommand"],
    requireAll = false
)
fun onScrollChangeCommand(
    listView: ListView,
    onScrollChangeCommand: BindingCommand<ListViewScrollDataWrapper?>?,
    onScrollStateChangedCommand: BindingCommand<Int?>?
) {
    listView.setOnScrollListener(object : AbsListView.OnScrollListener {
        private var scrollState = 0
        override fun onScrollStateChanged(
            view: AbsListView,
            scrollState: Int
        ) {
            this.scrollState = scrollState
            onScrollStateChangedCommand?.execute(scrollState)
        }

        override fun onScroll(
            view: AbsListView,
            firstVisibleItem: Int,
            visibleItemCount: Int,
            totalItemCount: Int
        ) {
            onScrollChangeCommand?.execute(
                ListViewScrollDataWrapper(
                    scrollState,
                    firstVisibleItem,
                    visibleItemCount,
                    totalItemCount
                )
            )
        }
    })
}

@BindingAdapter(value = ["onItemClickCommand"], requireAll = false)
fun onItemClickCommand(
    listView: ListView,
    onItemClickCommand: BindingCommand<Int?>?
) {
    listView.onItemClickListener =
        OnItemClickListener { parent, view, position, id -> onItemClickCommand?.execute(position) }
}

class ListViewScrollDataWrapper(
    var scrollState: Int,
    var firstVisibleItem: Int,
    var visibleItemCount: Int,
    var totalItemCount: Int
)