package com.ninjahome.ninja.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import chatLib.ChatLib
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.provider.context
import com.ninja.android.lib.utils.SharedPref
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.db.ContactDBManager
import com.ninjahome.ninja.db.ConversationDBManager
import com.ninjahome.ninja.db.MessageDBManager
import com.ninjahome.ninja.event.EventChangeAccount
import com.ninjahome.ninja.model.CreateAccountModel
import com.ninjahome.ninja.model.UnlockModel
import com.ninjahome.ninja.ui.activity.edituserinfo.EditUserInfoActivity
import com.ninjahome.ninja.ui.activity.main.MainActivity
import com.ninjahome.ninja.utils.AccountUtils
import com.ninjahome.ninja.utils.fromJson
import com.ninjahome.ninja.view.contacts.ColorUtil
import com.ninjahome.ninja.view.contacts.TextDrawable
import com.orhanobut.logger.Logger
import org.greenrobot.eventbus.EventBus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class UnLockViewModel(val model: UnlockModel) : BaseViewModel(), KoinComponent {
    val password = MutableLiveData("")
    val accountJson = MutableLiveData("")
    val createAccountModel: CreateAccountModel by inject()
    val iconDrawable = MutableLiveData<TextDrawable>()
    var openDestroy: Boolean by SharedPref(context(), Constants.KEY_DESTROY, false)
    var destroyPassword: String by SharedPref(context(), Constants.KEY_DESTROY_PASSWORD, "")
    var userName: String by SharedPref(context(), Constants.KEY_USER_NAME, "")
    private val subName: String = if (userName.length >= 2) userName.substring(0, 2) else userName
    private val mDrawableBuilder = TextDrawable.builder().beginConfig().fontSize(40)
    var openFingerPrint: Boolean by SharedPref(context(), Constants.KEY_OPEN_FINGERPRINT, false)

    fun loadAccount() {
        val job = rxLifeScope.launch({
            accountJson.value = model.loadAccount(AccountUtils.getAccountPath(context()))
            NinjaApp.instance.account = accountJson.value!!.fromJson()!!
            val iconIndex = ChatLib.iconIndex(NinjaApp.instance.account.address, ColorUtil.colorSize)
            val iconColor = ColorUtil.colors[iconIndex.toInt()]
            iconDrawable.value = mDrawableBuilder.textColor(context().getColor(R.color.white)).endConfig().buildRound(subName, context().resources.getColor(iconColor))

        }, {
            showToast(R.string.load_account_error)
        })

        jobs.add(job)
    }

    val clickUnlock = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            if (TextUtils.isEmpty(password.value)) {
                showToast(R.string.create_account_input_password)
                return
            }
            if (openDestroy && password.value.equals(destroyPassword)) {
                createAccount(password.value)
                return
            }
            val job = rxLifeScope.launch({
                NinjaApp.instance.configApp()
                model.openAccount(accountJson.value!!, password.value!!)
                if (TextUtils.isEmpty(userName)) {
                    startActivityAndFinish(EditUserInfoActivity::class.java)
                } else {
                    startActivityAndFinish(MainActivity::class.java)
                }
                dismissDialog()
            }, {
                dismissDialog()
                Logger.e(it.message!!)
                showToast(R.string.open_error)
            }, {
                showDialog()
            })
            jobs.add(job)

        }
    })

    private fun createAccount(value: String?) {
        rxLifeScope.launch({
            ChatLib.wsOffline()
            val account = createAccountModel.createAccount(password.value!!)
            NinjaApp.instance.configApp()
            createAccountModel.activeAccount(account, password.value!!)
            Logger.d(account)
            createAccountSuccess(account)

        }, {
            it.printStackTrace()
            dismissDialog()
            showToast(R.string.open_error)
        }, {
            showDialog()
        })
    }

    private suspend fun createAccountSuccess(account: String) {
        AccountUtils.saveAccountToPath(AccountUtils.getAccountPath(context()), account)
        NinjaApp.instance.account = account.fromJson()!!
        dismissDialog()
        openFingerPrint = false
        userName = ""
        openDestroy = false
        destroyPassword = ""
        ConversationDBManager.deleteAll()
        ContactDBManager.deleteAll()
        MessageDBManager.deleteAll()
        EventBus.getDefault().post(EventChangeAccount())
        startActivityAndFinish(EditUserInfoActivity::class.java)

    }
}