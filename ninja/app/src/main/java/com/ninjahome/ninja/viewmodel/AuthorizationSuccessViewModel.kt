package com.ninjahome.ninja.viewmodel

import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninjahome.ninja.ui.activity.main.MainActivity

/**
 *Author:Mr'x
 *Time:2021/9/7
 *Description:
 */
class AuthorizationSuccessViewModel : BaseViewModel() {

    val clickContinue = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            finish()
        }
    })

    val clickChat = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startActivityAndFinish(MainActivity::class.java)
        }
    })
}