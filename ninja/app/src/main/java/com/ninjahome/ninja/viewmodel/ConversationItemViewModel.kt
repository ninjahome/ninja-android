package com.ninjahome.ninja.viewmodel

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import chatLib.ChatLib
import com.ninja.android.lib.base.ItemViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.Conversation
import com.ninjahome.ninja.db.ContactDBManager
import com.ninjahome.ninja.db.ConversationDBManager
import com.ninjahome.ninja.db.MessageDBManager
import com.ninjahome.ninja.ui.activity.conversation.ConversationActivity
import com.ninjahome.ninja.view.contacts.ColorUtil
import com.ninjahome.ninja.view.contacts.ConversationGroupIcon
import com.ninjahome.ninja.view.contacts.TextDrawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ConversationItemViewModel(viewModel: ConversationListViewModel, val conversation: Conversation) : ItemViewModel<ConversationListViewModel>(viewModel) {
    val receiverIconIndex = ChatLib.iconIndex(conversation.from, ColorUtil.colorSize)
    var receiverIconColor = ColorUtil.colors[receiverIconIndex.toInt()]
    private val mDrawableBuilder = TextDrawable.builder().beginConfig().fontSize(30)
    val subName = if (conversation.title.length >= 2) conversation.title.substring(0, 2) else conversation.title
    var receiverIcon: MutableLiveData<Drawable> = MutableLiveData()

    init {
        if(conversation.isGroup){
            receiverIcon.value = ConversationGroupIcon(subName)
        }else{
            receiverIcon.value = mDrawableBuilder.textColor(context().getColor(R.color.white)).endConfig().buildRound(subName, context().resources.getColor(receiverIconColor, null))
            rxLifeScope.launch {
                val contact = ContactDBManager.queryByID(conversation.from)
                if (contact == null) {
                    receiverIconColor = R.color.color_d8d8d8
                }

            }
        }
    }

    val clickItem = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            val bundle = Bundle()
            if (conversation.isGroup) {
                bundle.putString(IntentKey.ID, conversation.groupId)
            } else {
                bundle.putString(IntentKey.ID, conversation.from)
            }
            bundle.putBoolean(IntentKey.IS_GROUP, conversation.isGroup)
            viewModel.startActivity(ConversationActivity::class.java, bundle)
        }
    })

    val onDragBubleView = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            clearUnreadNumber()
        }
    })

    private fun clearUnreadNumber() {
        rxLifeScope.launch {
            withContext(Dispatchers.IO){
                conversation.unreadCount =0
                MessageDBManager.updateMessage2Read(conversation.id)
                ConversationDBManager.updateConversations(conversation)
            }
        }
    }

}