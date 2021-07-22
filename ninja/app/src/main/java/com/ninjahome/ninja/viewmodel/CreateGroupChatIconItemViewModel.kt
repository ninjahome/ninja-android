package com.ninjahome.ninja.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ninja.android.lib.base.ItemViewModel
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.utils.ContactIconUtils
import com.ninjahome.ninja.view.contacts.TextDrawable

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class CreateGroupChatIconItemViewModel(viewModel: CreateGroupChatViewModel, val conversation: Contact) : ItemViewModel<CreateGroupChatViewModel>(viewModel) {
    val iconDrawable = MutableLiveData<TextDrawable>()
    init {
        iconDrawable.value = ContactIconUtils.getDrawable(30, conversation.uid,conversation.subName)
    }
}