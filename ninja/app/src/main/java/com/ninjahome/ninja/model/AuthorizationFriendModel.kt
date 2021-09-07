package com.ninjahome.ninja.model

import chatLib.ChatLib
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *Author:Mr'x
 *Time:2021/9/6
 *Description:
 */
class AuthorizationSingleFriendModel {
     suspend fun transferLicense(address:String,days:Long){
        withContext(Dispatchers.IO){
            ChatLib.transferLicense(address,days)
        }
    }
}