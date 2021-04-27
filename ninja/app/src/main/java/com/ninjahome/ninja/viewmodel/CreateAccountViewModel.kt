package com.ninjahome.ninja.viewmodel

import android.text.TextUtils
import androidlib.Androidlib
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.CreateAccountModel
import com.ninjahome.ninja.ui.activity.edituserinfo.EditUserInfoActivity
import com.orhanobut.logger.Logger
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class CreateAccountViewModel : BaseViewModel(), KoinComponent {

    val password = MutableLiveData("")
    val rePassword = MutableLiveData("")
    val model: CreateAccountModel by inject()

    val clickSure = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            when {
                TextUtils.isEmpty(password.value) -> showToast(R.string.create_account_password_is_null)
                TextUtils.isEmpty(rePassword.value) -> showToast(R.string.create_account_password_is_null)
                !rePassword.value.equals(password.value) -> showToast(R.string.create_account_password_not_equal)
                else -> {
                    createAccount()

                }
            }

        }
    })

    private fun createAccount() {
        rxLifeScope.launch({
           val account= model.createAccount(password.value!!)
            startActivity(EditUserInfoActivity::class.java)
        },{
            println(it.message)
            showToast(R.string.createAccount_error)
        })
    }

    val clickImport = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            toast("导入二维码")
        }
    })
}