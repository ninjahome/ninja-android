package com.ninjahome.ninja.model

import androidlib.Androidlib
import com.ninjahome.ninja.push.PushHelper
import com.ninjahome.ninja.utils.AccountUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            Androidlib.activeWallet(accountJson, password, PushHelper.token)
        }
    }
}