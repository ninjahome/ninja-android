package com.ninjahome.ninja.viewmodel

import android.text.TextUtils
import androidlib.Androidlib
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.ninja.android.lib.utils.SharedPref
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.event.EventChangeAccount
import com.ninjahome.ninja.model.CreateAccountModel
import com.ninjahome.ninja.ui.activity.edituserinfo.EditUserInfoActivity
import com.ninjahome.ninja.utils.AccountUtils
import com.ninjahome.ninja.utils.fromJson
import com.orhanobut.logger.Logger
import org.greenrobot.eventbus.EventBus
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
    val showImportDialog = SingleLiveEvent<Boolean>()
    var userName: String by SharedPref(context(), Constants.KEY_USER_NAME, "", commit = true)
    var openFingerPrint: Boolean by SharedPref(context(), Constants.KEY_OPEN_FINGERPRINT, false)
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
            val account = model.createAccount(password.value!!)
            Androidlib.wsOffline()
            NinjaApp.instance.configApp()
            model.activeAccount(account, password.value!!)
            Logger.d(account)
            createAccountSuccess(account)

        }, {
            println(it.message)
            dismissDialog()
            showToast(R.string.createAccount_error)
        }, {
            showDialog()
        })
    }

    fun importAccount(accountJson: String, password: String) {
        rxLifeScope.launch({
            Androidlib.wsOffline()
            NinjaApp.instance.configApp()
            model.activeAccount(accountJson, password)
            createAccountSuccess(accountJson)
            dismissDialog()
        }, {
            println(it.message)
            dismissDialog()
            showToast(R.string.createAccount_import_error)
        }, {
            showDialog()
        })
    }

    val clickImport = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showImportDialog.call()
        }
    })

    private fun createAccountSuccess(account: String) {
        AccountUtils.saveAccountToPath(AccountUtils.getAccountPath(context()), account)
        NinjaApp.instance.account = account.fromJson()!!
        dismissDialog()
        openFingerPrint = false
        userName = ""
        NinjaApp.instance.conversations.clear()
        EventBus.getDefault().post(EventChangeAccount())

        NinjaApp.instance.configApp()
        startActivityAndFinish(EditUserInfoActivity::class.java)

    }
}