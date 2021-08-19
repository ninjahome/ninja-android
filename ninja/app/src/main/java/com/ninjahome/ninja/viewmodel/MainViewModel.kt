package com.ninjahome.ninja.viewmodel

import androidx.lifecycle.rxLifeScope
import chatLib.ChatLib
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.event.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MainViewModel : BaseViewModel() {
    val expireTime = SingleLiveEvent<Long>()

    fun getExpireTime() {
        rxLifeScope.launch {
            withContext(Dispatchers.IO) {
                expireTime.postValue(ChatLib.getExpireTime())
            }
        }
    }
}