package com.ninjahome.ninja.viewmodel

import android.text.TextUtils
import androidlib.Androidlib
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.provider.context
import com.ninja.android.lib.utils.SharedPref
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.UnlockModel
import com.ninjahome.ninja.ui.activity.main.MainActivity
import com.ninjahome.ninja.utils.AccountUtils
import com.ninjahome.ninja.utils.fromJson
import com.ninjahome.ninja.view.contacts.ColorGenerator
import com.ninjahome.ninja.view.contacts.ColorUtil
import com.ninjahome.ninja.view.contacts.TextDrawable
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
class UnLockViewModel : BaseViewModel(), KoinComponent {
    val model: UnlockModel by inject()
    val password = MutableLiveData("")
    val accountJson = MutableLiveData("")
    val iconDrawable = MutableLiveData<TextDrawable>()
    val userName: String by SharedPref(context(), Constants.KEY_USER_NAME, "")
    private val subName: String = if (userName.length >= 2) userName.substring(0, 2) else userName
    private val mDrawableBuilder = TextDrawable.builder().beginConfig().fontSize(40)
    var openFingerPrint: Boolean by SharedPref(context(), Constants.KEY_OPEN_FINGERPRINT, false)

    fun loadAccount() {
        rxLifeScope.launch({
            accountJson.value = model.loadAccount(AccountUtils.getAccountPath(context()))
            NinjaApp.instance.account = accountJson.value!!.fromJson()!!
            val iconIndex = Androidlib.iconIndex(NinjaApp.instance.account.address, ColorUtil.colorSize)
            val iconColor = ColorUtil.colors[iconIndex]
            iconDrawable.value = mDrawableBuilder.textColor(context().getColor(R.color.white)).endConfig().buildRound(subName, context().resources.getColor(iconColor))

        }, {
            showToast(R.string.load_account_error)
        })

    }

    val clickUnlock = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            if (TextUtils.isEmpty(password.value)) {
                showToast(R.string.create_account_input_password)
                return
            }
            rxLifeScope.launch({
                NinjaApp.instance.configApp()
                model.openAccount(accountJson.value!!, password.value!!)
                startActivityAndFinish(MainActivity::class.java)
                //                delay(1000)
                //                showToast(Androidlib.wsIsOnline().toString())
                dismissDialog()
            }, {
                dismissDialog()
                Logger.e(it.message!!)
                showToast(R.string.open_error)
            }, {
                showDialog()
            })


        }
    })
}