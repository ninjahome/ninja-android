package com.ninjahome.ninja.model

import chatLib.ChatLib
import com.ninjahome.ninja.event.EventOnline
import com.ninjahome.ninja.push.PushHelper
import com.ninjahome.ninja.utils.AccountUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class UnlockModel {

    suspend fun loadAccount(path: String): String {
        return withContext(Dispatchers.IO) {
            return@withContext AccountUtils.loadAccountJsonByPath(path)
        }
    }

    suspend fun openAccount(accountJson: String, password: String) {
        withContext(Dispatchers.IO) {
            ChatLib.activeWallet(accountJson, password, PushHelper.token)
        }
    }

    suspend fun online() {
        withContext(Dispatchers.IO) {
            ChatLib.wsOnline()
            EventBus.getDefault().post(EventOnline())
        }
    }
}