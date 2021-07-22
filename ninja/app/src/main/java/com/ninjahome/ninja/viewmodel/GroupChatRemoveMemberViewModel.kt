package com.ninjahome.ninja.viewmodel

import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class GroupChatRemoveMemberViewModel :BaseViewModel(){

    val clickComplete = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showToast("点击了完成按钮")
        }

    })
}
