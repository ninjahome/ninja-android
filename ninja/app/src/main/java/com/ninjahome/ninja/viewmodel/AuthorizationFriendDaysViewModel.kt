package com.ninjahome.ninja.viewmodel

import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import chatLib.ChatLib
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.command.BindingConsumer
import com.ninja.android.lib.utils.dp
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.R
import com.ninjahome.ninja.event.EventAuthorizationSuccess
import com.ninjahome.ninja.model.AuthorizationFriendModel
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.ui.activity.authorization.AuthorizationSuccessActivity
import com.ninjahome.ninja.utils.ContactIconUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

/**
 *Author:Mr'x
 *Time:2021/9/7
 *Description:
 */
class AuthorizationFriendDaysViewModel(model: AuthorizationFriendModel) : BaseViewModel() {
    lateinit var contact: Contact
    val days = MutableLiveData<String>()
    var expireTime = MutableLiveData<Long>()
    var icon = MutableLiveData<Drawable>()
    val enable = MutableLiveData<Boolean>()
    val clickAllAuthorization = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            days.value = getAllDays().toString()
        }
    })

    val daysChange = BindingCommand(bindConsumer = object : BindingConsumer<String> {
        override fun call(t: String) {
            enable.value = t.isNotEmpty()
        }
    })

    fun setContactIcon() {
        icon.value = ContactIconUtils.getDrawable(12.dp.toInt(), contact.uid, contact.subName)
    }

    val clickSure = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            if (TextUtils.isEmpty(days.value)) {
                showToast(R.string.authorization_input_days)
                return
            }
            if (days.value!!.toLong() > getAllDays()) {
                showToast(R.string.authorization_days_error)
                return
            }
            showDialog()
            rxLifeScope.launch({
                model.transferLicense(contact.uid, days.value!!.toLong())
                delay(2000)
                dismissDialog()
                EventBus.getDefault().post(EventAuthorizationSuccess())
                startActivity(AuthorizationSuccessActivity::class.java)

            }, {
                it.printStackTrace()
                showToast(R.string.authorization_failed)
                dismissDialog()
            })
        }
    })

    fun getAllDays(): Int {
        var allDays = (expireTime.value!! - System.currentTimeMillis()) / Constants.DAY
        if (allDays < 0) {
            allDays = 0
        }
        return allDays.toInt()
    }

    fun getExpireTime() {
        rxLifeScope.launch {
            withContext(Dispatchers.IO) {
                expireTime.postValue(ChatLib.getExpireTime() * Constants.SECOND)
            }
        }
    }
}