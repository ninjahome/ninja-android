package com.ninjahome.ninja.viewmodel

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
class GroupChatAddMemberItemViewModel(viewModel: GroupChatAddMemberViewModel, val conversation: Contact) : ItemViewModel<GroupChatAddMemberViewModel>(viewModel) {
    val iconDrawable = MutableLiveData<TextDrawable>()

    init {
        iconDrawable.value = ContactIconUtils.getDrawable(30, conversation.uid, conversation.subName)
    }
}