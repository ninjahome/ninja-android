package com.ninjahome.ninja.model

import androidlib.Androidlib
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
            return@withContext Androidlib.newWallet(password)
        }
    }

    suspend fun activeAccount(accountJson: String, password: String) {
        return withContext(Dispatchers.IO) {
            return@withContext Androidlib.activeWallet(accountJson, password, PushHelper.token)
        }
    }

}