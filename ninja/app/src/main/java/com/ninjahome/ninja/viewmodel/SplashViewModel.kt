package com.ninjahome.ninja.viewmodel

import com.ninja.android.lib.base.BaseViewModel
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class SplashViewModel : BaseViewModel(), KoinComponent {
//    private val guideModel: GuideModel by inject()
//    val hasAccount: Boolean = IDCardUtils.hasIDCard(IDCardUtils.getIDCardPath(context()))
//
//    fun loadCard() {
//        guideModel.loadCard(IDCardUtils.getIDCardPath(context())).subscribe(object : SingleObserver<Boolean> {
//            override fun onSuccess(loadResult: Boolean) {
//                startActivityAndFinish(MainActivity::class.java)
//            }
//
//            override fun onSubscribe(d: Disposable) {
//                addSubscribe(d)
//            }
//
//            override fun onError(e: Throwable) {
//                showErrorToast(R.string.splash_load_error, e)
//                finish()
//            }
//
//        })
//    }
}