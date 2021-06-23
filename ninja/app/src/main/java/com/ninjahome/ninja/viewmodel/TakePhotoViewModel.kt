package com.ninjahome.ninja.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.R

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TakePhotoViewModel:BaseViewModel() {
    val backEvent = SingleLiveEvent<Any>()
    val sendEvent = SingleLiveEvent<Any>()
    val sendUserName = MutableLiveData<String>(context().getString(R.string.take_photo_send_user_name))

    val clickBack = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            backEvent.call()
        }

    })

    val clickSend = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            sendEvent.call()
        }

    })
}