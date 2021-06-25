package com.ninjahome.ninja.viewmodel

import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class LocationShowViewModel : BaseViewModel() {


    val clickNavigation = BindingCommand<Any>(object : BindingAction {
        override fun call() {
        }
    })
}