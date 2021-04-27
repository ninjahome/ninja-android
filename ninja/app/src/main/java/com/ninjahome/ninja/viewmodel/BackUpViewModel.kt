package com.ninjahome.ninja.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.utils.toast

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class BackUpViewModel:BaseViewModel() {

    val id = MutableLiveData("")


    val clickSave = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            toast("点击保存")
        }
    })

    val clickSkip = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            toast("点击跳过")
        }
    })
}