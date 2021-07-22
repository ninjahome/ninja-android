package com.ninjahome.ninja.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.room.ContactDBManager
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class CreateGroupChatViewModel : BaseViewModel() {
    val allContact = ContactDBManager.all()
    val contacts = mutableListOf<Contact>()
    val showCreateGroupChatPop = SingleLiveEvent<Any>()
    val contactIconItem: ObservableList<CreateGroupChatIconItemViewModel> = ObservableArrayList()
    val itemBinding = ItemBinding.of<CreateGroupChatIconItemViewModel>(BR.item, R.layout.item_group_chat_icon)

    val clickSure = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showCreateGroupChatPop.call()
        }
    })

}