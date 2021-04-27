package com.ninjahome.ninja.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.ui.activity.main.MainActivity

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class EditUserInfoViewModel:BaseViewModel() {

    val name = MutableLiveData("")

    val clickSure = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            toast("点击了确定")
            startActivity(MainActivity::class.java)
        }
    })
}