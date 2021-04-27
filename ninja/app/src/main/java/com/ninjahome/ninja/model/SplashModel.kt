package com.ninjahome.ninja.model

import androidlib.Androidlib
import com.ninjahome.ninja.utils.AccountUtils
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class SplashModel {

//    fun loadCard(path: String): Single<Boolean> {
//        return Single.create(SingleOnSubscribe<Boolean> { emitter ->
//            MainScope().launch {
//                val walletJson = AccountUtils.loadIDCardJson(path)
//                Androidlib.activeWallet(walletJson)
//                emitter.onSuccess(loadResult)
//            }
//        }).compose(CommonSchedulers.io2mainAndTimeout<Boolean>())
//    }
//
//    fun import(idCardJson: String, password: String): Single<String> {
//        return Single.create(SingleOnSubscribe<String> { emitter ->
//            val id = String(Androidgolib.import_(password, idCardJson))
//            emitter.onSuccess(id)
//        }).compose(CommonSchedulers.io2mainAndTimeout<String>())
//    }
}