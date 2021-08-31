package com.ninjahome.ninja.viewmodel

import androidx.lifecycle.rxLifeScope
import chatLib.ChatLib
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninjahome.ninja.db.ConversationDBManager
import com.ninjahome.ninja.db.MessageDBManager
import com.ninjahome.ninja.event.EventOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

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

    fun online() {
        rxLifeScope.launch {
            withContext(Dispatchers.IO) {
                ChatLib.wsOnline()
                EventBus.getDefault().post(EventOnline())
            }
        }
    }

    fun clearUnreadNumber() {
        rxLifeScope.launch {
            withContext(Dispatchers.IO) {
                MessageDBManager.updateAllMessage2Read()
                ConversationDBManager.clearUnreadNumber()
            }
        }
    }
}