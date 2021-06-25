package com.ninjahome.ninja.viewmodel

import android.os.Bundle
import com.ninja.android.lib.base.ItemViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.event.EventUpdateConversationNickName
import com.ninjahome.ninja.model.bean.Conversation
import com.ninjahome.ninja.ui.activity.conversation.ConversationActivity
import org.greenrobot.eventbus.EventBus
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class ConversationItemViewModel(viewModel: ConversationListViewModel, val conversation: Conversation) : ItemViewModel<ConversationListViewModel>(viewModel) {
    val clickItem = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            conversation.unreadNo = 0
            conversation.unreadNoStr = ""
            EventBus.getDefault().post(EventUpdateConversationNickName(conversation.address))
            val bundle = Bundle()
            bundle.putString(IntentKey.UID, conversation.address)
            viewModel.startActivity(ConversationActivity::class.java, bundle)
        }
    })
}