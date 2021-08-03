package com.ninjahome.ninja.viewmodel

import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.utils.AccountUtils

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class SplashViewModel : BaseViewModel() {

    val hasAccount: Boolean = AccountUtils.hasAccount(AccountUtils.getAccountPath(context()))


}