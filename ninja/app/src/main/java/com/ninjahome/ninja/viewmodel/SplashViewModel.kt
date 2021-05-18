package com.ninjahome.ninja.viewmodel

import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.utils.AccountUtils
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class SplashViewModel : BaseViewModel(), KoinComponent {

    val hasAccount: Boolean = AccountUtils.hasAccount(AccountUtils.getAccountPath(context()))


}