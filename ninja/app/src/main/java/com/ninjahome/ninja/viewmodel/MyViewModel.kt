package com.ninjahome.ninja.viewmodel

import android.os.Bundle
import androidlib.Androidlib
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.command.BindingConsumer
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.ninja.android.lib.utils.SharedPref
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.UnlockModel
import com.ninjahome.ninja.ui.activity.accountmanager.AccountManagerActivity
import com.ninjahome.ninja.ui.activity.edituserinfo.EditUserInfoActivity
import com.ninjahome.ninja.utils.AccountUtils
import com.ninjahome.ninja.utils.toJson
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
class MyViewModel : BaseViewModel(), KoinComponent {
    val model: UnlockModel by inject()
    val name = SingleLiveEvent<String>()
    val id = SingleLiveEvent<String>()
    val showIDQR = SingleLiveEvent<Any>()
    val iconDrawable = SingleLiveEvent<TextDrawable>()
    val fingerPrintEvent = SingleLiveEvent<Boolean>()
    val showFingerPrintDialogEvent = SingleLiveEvent<String>()
    val dismissPasswordDialogEvent = SingleLiveEvent<Boolean>()
    var openFingerPrint: Boolean by SharedPref(context(), Constants.KEY_OPEN_FINGERPRINT, false)
    var openFingerPrintObservable: ObservableBoolean = ObservableBoolean(openFingerPrint)
    val userName: String by SharedPref(context(), Constants.KEY_USER_NAME, "")

    private val mDrawableBuilder = TextDrawable.builder().beginConfig().fontSize(40)

    init {
        setValue()
    }

    fun setValue() {
        name.value = userName
        rxLifeScope.launch({
            id.value = AccountUtils.getAddress(context())
            val iconIndex = Androidlib.iconIndex(id.value!!, ColorUtil.colorSize)
            val iconColor = ColorUtil.colors[iconIndex]
            val subName: String = if (userName.length >= 2) userName.substring(0, 2) else userName
            iconDrawable.value = mDrawableBuilder.textColor(context().getColor(R.color.white)).endConfig().buildRound(subName, context().resources.getColor(iconColor))
        }, {
            Logger.e(it.message!!)
        })
    }


    val clickShowIDQR = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showIDQR.call()
        }
    })

    val clickEdit = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            val bundle = Bundle()
            bundle.putString(IntentKey.NAME, name.value)
            startActivity(EditUserInfoActivity::class.java, bundle)
        }
    })

    val clickAccountManager = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startActivity(AccountManagerActivity::class.java)
        }
    })

    val onCheckedFingerprint = BindingCommand(bindConsumer = object : BindingConsumer<Boolean> {
        override fun call(isChecked: Boolean) {
            openFingerPrintObservable.set(isChecked)
            if (isChecked) {
                fingerPrintEvent.postValue(isChecked)
            }
        }

    })

    fun openAccount(password: String) {
        showDialog()
        rxLifeScope.launch({
            model.openAccount(NinjaApp.instance.account.toJson(), password)

            dismissPasswordDialogEvent.call()
            showFingerPrintDialogEvent.postValue(password)
            dismissDialog()
        }, {
            dismissDialog()
            Logger.e(it.message!!)
            showToast(R.string.open_error)
        }, {
            showDialog()
        })
    }

}