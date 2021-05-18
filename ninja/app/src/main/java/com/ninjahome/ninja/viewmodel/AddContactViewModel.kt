package com.ninjahome.ninja.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.event.EventRefreshContact
import com.ninjahome.ninja.event.EventUpdateConversationNickName
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.utils.AccountUtils
import com.orhanobut.logger.Logger
import org.greenrobot.eventbus.EventBus
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class AddContactViewModel : BaseViewModel() {

    val address = MutableLiveData("")
    val nickName = MutableLiveData("")
    val remark = MutableLiveData("")
    var contact: Contact? = null

    val clickSave = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            if (TextUtils.isEmpty(nickName.value)) {
                showToast(R.string.create_account_input_alias)
                return
            }
            rxLifeScope.launch({
                val owner = AccountUtils.getAddress(context())
                if (contact == null) {
                    contact = Contact(0, "", nickName.value!!, owner!!, remark.value!!, address.value!!)
                    ContactDBManager.insert(contact!!)
                } else {
                    contact!!.nickName = nickName.value!!
                    contact!!.remark = remark.value!!
                    contact!!.uid = address.value!!
                    ContactDBManager.updateAccounts(contact!!)
                }
                NinjaApp.instance.conversations.get(contact!!.uid)?.let { it.nickName = contact!!.nickName }
                EventBus.getDefault().post(EventRefreshContact())
                EventBus.getDefault().post(EventUpdateConversationNickName(contact!!.uid))
                showToast(R.string.search_contact_save_success)
                finish()

            }, {
                showToast(R.string.search_contact_save_error)
                Logger.e(it.stackTraceToString())

            })

        }
    })
}