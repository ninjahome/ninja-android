package com.ninjahome.ninja.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.provider.context
import com.ninja.android.lib.utils.SharedPref
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.ui.activity.main.MainActivity
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class EditUserInfoViewModel : BaseViewModel() {
    var userName: String by SharedPref(context(), Constants.KEY_USER_NAME, "", commit = true)
    val name = MutableLiveData("")
    var isEdit = false

    val clickSure = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            userName = name.value!!
            if (isEdit) {
                finish()
            } else {
                startActivityAndFinish(MainActivity::class.java)
            }
        }
    })
}