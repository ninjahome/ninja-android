package com.ninjahome.ninja.viewmodel

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.ItemViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.GroupMember
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.ui.activity.contact.ContactDetailActivity
import com.ninjahome.ninja.ui.activity.contact.ScanContactSuccessActivity
import com.ninjahome.ninja.ui.activity.groupchat.GroupChatAddMemberActivity
import com.ninjahome.ninja.utils.ContactIconUtils
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:2021/8/2
 *Description:
 */
class GroupChatDetailItemViewModel(viewModel: GroupChatDetailViewModel, var groupMember: GroupMember) : ItemViewModel<GroupChatDetailViewModel>(viewModel) {
    private val FONT_SIZE = 30
    var iconDrawable = MutableLiveData<Drawable>()

    init {
        if (TextUtils.isEmpty(groupMember.address)) {
            iconDrawable.value = ResourcesCompat.getDrawable(context().resources, R.drawable.group_member_add, null)
        } else {
            val name = if (groupMember.name.length > 2) groupMember.name.subSequence(0, 2).toString() else groupMember.name
            val drawable = ContactIconUtils.getDrawable(FONT_SIZE, groupMember.address, name)
            iconDrawable.value = drawable
        }

    }

    val clickMember = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            if (TextUtils.isEmpty(groupMember.address)) {
                val bundle = Bundle()
                bundle.putParcelable(IntentKey.GROUPCHAT, viewModel.groupDetail.value)
                viewModel.startActivity(GroupChatAddMemberActivity::class.java, bundle)
            } else {
                rxLifeScope.launch {
                    val contact = ContactDBManager.queryByID(groupMember.address)
                    if (contact == null) {
                        val bundle = Bundle()
                        bundle.putString(IntentKey.ID, groupMember.address)
                        viewModel.startActivity(ScanContactSuccessActivity::class.java, bundle)
                    } else {
                        val bundle = Bundle()
                        bundle.putString(IntentKey.ID, groupMember.address)
                        viewModel.startActivity(ContactDetailActivity::class.java, bundle)
                    }
                }
            }
        }

    })
}