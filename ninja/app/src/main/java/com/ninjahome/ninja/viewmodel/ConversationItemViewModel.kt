package com.ninjahome.ninja.viewmodel

import android.os.Bundle
import androidlib.Androidlib
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.ItemViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.Conversation
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.ui.activity.conversation.ConversationActivity
import com.ninjahome.ninja.view.contacts.ColorUtil
import com.ninjahome.ninja.view.contacts.TextDrawable
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class ConversationItemViewModel(viewModel: ConversationListViewModel, val conversation: Conversation) : ItemViewModel<ConversationListViewModel>(viewModel) {
    val receiverIconIndex = Androidlib.iconIndex(conversation.from, ColorUtil.colorSize)
    var receiverIconColor = ColorUtil.colors[receiverIconIndex]
    private val mDrawableBuilder = TextDrawable.builder().beginConfig().fontSize(30)
    val subName = if (conversation.title.length >= 2) conversation.title.substring(0, 2) else conversation.title
    var receiverIcon: MutableLiveData<TextDrawable> = MutableLiveData()

    init {
        rxLifeScope.launch {
            val contact = ContactDBManager.queryByID(conversation.from)
            if (contact == null) {
                receiverIconColor = R.color.color_d8d8d8
            }
            receiverIcon.value = mDrawableBuilder.textColor(context().getColor(R.color.white)).endConfig().buildRound(subName, context().resources.getColor(receiverIconColor,null))
        }
    }

    val clickItem = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            val bundle = Bundle()
            bundle.putString(IntentKey.UID, conversation.from)
            bundle.putBoolean(IntentKey.IS_GROUP, conversation.isGroup)
            viewModel.startActivity(ConversationActivity::class.java, bundle)
        }
    })
}