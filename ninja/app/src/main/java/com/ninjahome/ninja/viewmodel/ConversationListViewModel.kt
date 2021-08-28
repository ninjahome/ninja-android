package com.ninjahome.ninja.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import chatLib.ChatLib
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.db.ConversationDBManager
import com.ninjahome.ninja.model.bean.Conversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ConversationListViewModel : BaseViewModel() {
    var finishRefreshingEvent = SingleLiveEvent<Any>()
    var showPop = SingleLiveEvent<Any>()
    val online = MutableLiveData(true)

    val items: ObservableList<ConversationItemViewModel> = ObservableArrayList()
    val itemBinding = ItemBinding.of<ConversationItemViewModel>(BR.item, R.layout.item_message)


    init {
        title.set(context().getString(R.string.message))
        showBackImage.set(false)
        showRightIv.set(true)
        rightIv.set(R.drawable.add)
    }

    override fun clickRightIv() {
        super.clickRightIv()
        showPop.call()

    }

    fun removeItemAt(conversation: Conversation) {
        rxLifeScope.launch {
            withContext(Dispatchers.IO){
                ConversationDBManager.delete(conversation)
            }
        }

    }

    val refreshCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            rxLifeScope.launch({
                withTimeout(3000){
                    withContext(Dispatchers.IO) {
                        ChatLib.wsOnline()
                    }
                    finishRefreshingEvent.call()
                }

            },{
                finishRefreshingEvent.call()
            })

        }
    })


}