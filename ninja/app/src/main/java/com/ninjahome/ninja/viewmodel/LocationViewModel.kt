package com.ninjahome.ninja.viewmodel

import android.service.controls.actions.CommandAction
import androidx.lifecycle.MutableLiveData
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.command.BindingConsumer
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.ninja.android.lib.viewadapter.recyclerview.ViewAdapter
import com.ninjahome.ninja.R
import com.ninjahome.ninja.ui.activity.main.MainActivity

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class LocationViewModel:BaseViewModel() {
    val startLocationSearchEvent = SingleLiveEvent<Any>()
    init {
        title.set(context().getString(R.string.location))
        showRightText.set(true)
        rightText.set(context().getString(R.string.send))
    }

    val sendEvent= SingleLiveEvent<Any>()
    val requestLocationEvent= SingleLiveEvent<Any>()
    val scrolledEvent= SingleLiveEvent<ViewAdapter.ScrollDataWrapper>()

    override fun clickRightTv() {
        super.clickRightTv()
        sendEvent.call()
    }

    val clickShowLocation = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            requestLocationEvent.call()
        }
    })

    val scrolled = BindingCommand<ViewAdapter.ScrollDataWrapper>(bindConsumer = object : BindingConsumer<ViewAdapter.ScrollDataWrapper> {
        override fun call(t: ViewAdapter.ScrollDataWrapper) {
            scrolledEvent.value = t
        }

    })

    val clickSearch = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startLocationSearchEvent.call()
        }

    })
}