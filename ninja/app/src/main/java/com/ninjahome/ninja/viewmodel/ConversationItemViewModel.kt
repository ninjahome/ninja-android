package com.ninjahome.ninja.viewmodel

import android.os.Bundle
import com.ninja.android.lib.base.ItemViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.Conversation
import com.ninjahome.ninja.ui.activity.conversation.ConversationActivity
import com.ninjahome.ninja.view.contacts.ColorGenerator
import com.ninjahome.ninja.view.contacts.TextDrawable
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class ConversationItemViewModel(viewModel: ConversationListViewModel, val conversation: Conversation) : ItemViewModel<ConversationListViewModel>(viewModel) {
    val receiverIconColor= ColorGenerator.MATERIAL.getColor(conversation.from)
    private val mDrawableBuilder = TextDrawable.builder().beginConfig().fontSize(30)
    val subName = if(conversation.nickName.length>=2) conversation.nickName.substring(0,2) else conversation.nickName
    val receiverIcon = mDrawableBuilder.textColor(context().getColor(R.color.white)).endConfig().buildRound(subName, context().resources.getColor(receiverIconColor) )
    val clickItem = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            val bundle = Bundle()
            bundle.putString(IntentKey.UID, conversation.from)
            viewModel.startActivity(ConversationActivity::class.java, bundle)
        }
    })
}