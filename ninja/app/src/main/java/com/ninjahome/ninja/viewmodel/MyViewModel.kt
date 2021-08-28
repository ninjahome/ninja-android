package com.ninjahome.ninja.viewmodel

import android.os.Bundle
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import chatLib.ChatLib
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
import com.ninjahome.ninja.ui.activity.activation.ActivationActivity
import com.ninjahome.ninja.ui.activity.edituserinfo.EditUserInfoActivity
import com.ninjahome.ninja.utils.AccountUtils
import com.ninjahome.ninja.utils.CommonUtils
import com.ninjahome.ninja.utils.toJson
import com.ninjahome.ninja.view.contacts.ColorUtil
import com.ninjahome.ninja.view.contacts.TextDrawable
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MyViewModel(val model: UnlockModel) : BaseViewModel() {
    val name = SingleLiveEvent<String>()
    val id = SingleLiveEvent<String>()
    val showIDQR = SingleLiveEvent<Any>()
    val expireTime = SingleLiveEvent<Long>()


    val iconDrawable = MutableLiveData<TextDrawable>()
    val fingerPrintEvent = SingleLiveEvent<Boolean>()
    val destroyEvent = SingleLiveEvent<Boolean>()
    val showFingerPrintDialogEvent = SingleLiveEvent<String>()
    val dismissPasswordDialogEvent = SingleLiveEvent<Boolean>()
    var openFingerPrint: Boolean by SharedPref(context(), Constants.KEY_OPEN_FINGERPRINT, false)
    var openDestroy: Boolean by SharedPref(context(), Constants.KEY_DESTROY, false)
    var openFingerPrintObservable: ObservableBoolean = ObservableBoolean(openFingerPrint)
    var destroyObservable: ObservableBoolean = ObservableBoolean(openDestroy)
    private val userName: String by SharedPref(context(), Constants.KEY_USER_NAME, "")

    private val mDrawableBuilder = TextDrawable.builder().beginConfig().fontSize(40)

    init {
        setValue()
    }

    val copyId = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            id.value?.let {
                CommonUtils.copyToMemory(context(), it)
                showToast(R.string.copy_success)
            }


        }
    })


    val clickFresh = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            getExpireTime()
        }
    })

    fun getExpireTime() {
        rxLifeScope.launch {
            withContext(Dispatchers.IO) {
                expireTime.postValue(ChatLib.getExpireTime())
            }
        }
    }

    fun setValue() {
        name.value = userName
        rxLifeScope.launch {
            id.value = AccountUtils.getAddress(context())
            id.postValue(id.value)
            val iconIndex = ChatLib.iconIndex(id.value!!, ColorUtil.colorSize)
            val iconColor = ColorUtil.colors[iconIndex.toInt()]
            val subName: String = if (userName.length >= 2) userName.substring(0, 2) else userName
            iconDrawable.postValue(mDrawableBuilder.textColor(context().getColor(R.color.white)).endConfig().buildRound(subName, context().resources.getColor(iconColor, null)))
        }
    }


    val clickShowIDQR = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showIDQR.call()
        }
    })

    val clickStartActivation = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startActivity(ActivationActivity::class.java)
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
        override fun call(t: Boolean) {
            openFingerPrintObservable.set(t)
            if (t) {
                fingerPrintEvent.postValue(t)
            }
        }

    })

    val onCheckedDestroy = BindingCommand(bindConsumer = object : BindingConsumer<Boolean> {
        override fun call(t: Boolean) {
            destroyObservable.set(t)
            if (t) {
                destroyEvent.postValue(t)
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