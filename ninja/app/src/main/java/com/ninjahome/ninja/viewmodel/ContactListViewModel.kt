package com.ninjahome.ninja.viewmodel

import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.R
import com.ninjahome.ninja.ui.activity.groupchat.GroupChatCreateActivity
import com.ninjahome.ninja.ui.activity.search.SearchContactActivity

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ContactListViewModel : BaseViewModel() {
    init {
        title.set(context().getString(R.string.contact))
        showRightIv.set(true)
        rightIv.set(R.drawable.contact_add)
        showBackImage.set(false)

    }

    override fun clickRightIv() {
        super.clickRightIv()
        startActivity(SearchContactActivity::class.java)
    }

    val clickGroup = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startActivity(GroupChatCreateActivity::class.java)
        }
    })

}