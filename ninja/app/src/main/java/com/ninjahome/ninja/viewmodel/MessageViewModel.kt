package com.ninjahome.ninja.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import me.tatarka.bindingcollectionadapter2.ItemBinding
import com.ninjahome.ninja.R
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.ui.activity.chat.ChatActivity

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MessageViewModel:BaseViewModel() {
    var finishRefreshingEvent = SingleLiveEvent<Any>()
    var finishResultActivityEvent = SingleLiveEvent<String>()
    val items: ObservableList<MessageItemViewModel> = ObservableArrayList()
    val itemBinding = ItemBinding.of<MessageItemViewModel>(BR.item, R.layout.item_message)


    init {
        title.set(context().getString(R.string.message))
        rightIv.set(R.drawable.add)
        showRightIv.set(true)
        showBackImage.set(false)
    }
    val refreshCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {

        }
    })

    override fun clickRightIv() {
        super.clickRightIv()
        showToast("点击了+")
        startActivity(ChatActivity::class.java)
    }
}