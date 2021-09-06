package com.ninjahome.ninja.viewmodel

import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand

/**
 *Author:Mr'x
 *Time:2021/9/6
 *Description:
 */
class AuthorizationFriendViewModel:BaseViewModel() {
    
    val clickInputId = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            
        }
    })

    val clickSelectFriend = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            
        }
    })
}