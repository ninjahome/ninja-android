package com.ninjahome.ninja.model

import chatLib.ChatLib
import com.ninjahome.ninja.push.PushHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class CreateAccountModel {

    suspend fun createAccount(password: String): String {
        return withContext(Dispatchers.IO) {
            return@withContext ChatLib.newWallet(password)
        }
    }

    suspend fun activeAccount(accountJson: String, password: String) {
        return withContext(Dispatchers.IO) {
            return@withContext ChatLib.activeWallet(accountJson, password, PushHelper.token)
        }
    }

}