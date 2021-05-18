package com.ninjahome.ninja.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ApplyListViewModel : BaseViewModel() {

    var finishRefreshingEvent = SingleLiveEvent<Any>()
    var finishResultActivityEvent = SingleLiveEvent<String>()
    val items: ObservableList<ApplyListItemViewModel> = ObservableArrayList()
    val itemBinding = ItemBinding.of<ApplyListItemViewModel>(BR.item, R.layout.item_apply)


    init {
        title.set(context().getString(R.string.apply_list_title))
        rightIv.set(R.drawable.add)
        showRightIv.set(true)
        showBackImage.set(false)
    }

    val refreshCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {

        }
    })
}