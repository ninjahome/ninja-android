package com.ninjahome.ninja.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class SearchContactViewModel : BaseViewModel() {
    val inputID = MutableLiveData<String>()
    val startScanActivityEvnet = SingleLiveEvent<Any>()
    val clickCancel = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            finish()
        }
    })

    val clickSacnQR = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startScanActivityEvnet.call()
        }
    })

}