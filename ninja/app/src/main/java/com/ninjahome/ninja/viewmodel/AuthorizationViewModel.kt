package com.ninjahome.ninja.viewmodel

import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninjahome.ninja.ui.activity.authorization.AuthorizationAccountActivity
import com.ninjahome.ninja.ui.activity.authorization.AuthorizationFriendActivity

/**
 *Author:Mr'x
 *Time:2021/9/6
 *Description:
 */
class AuthorizationViewModel : BaseViewModel() {
    val clickInputId = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startActivity(AuthorizationAccountActivity::class.java)
        }
    })

    val clickSelectFriend = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startActivity(AuthorizationFriendActivity::class.java)
        }
    })
}