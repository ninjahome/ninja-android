package com.ninjahome.ninja.viewmodel

import android.graphics.Color
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.room.ConversationDBManager
import com.ninjahome.ninja.utils.AccountUtils
import com.ninjahome.ninja.view.contacts.ColorGenerator
import com.orhanobut.logger.Logger
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class EditContactViewModel : BaseViewModel() {

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
                    val subName: String = if (nickName.value!!.length >= 2) nickName.value!!.substring(0, 2) else nickName.value!!
                    contact = Contact(0, "", nickName.value!!,subName, owner!!, remark.value!!, address.value!!)
                    ContactDBManager.insert(contact!!)
                } else {
                    contact!!.nickName = nickName.value!!
                    contact!!.remark = remark.value!!
                    contact!!.uid = address.value!!
                    contact!!.subName = if (nickName.value!!.length >= 2) nickName.value!!.substring(0, 2) else nickName.value!!
                    ContactDBManager.updateAccounts(contact!!)
                }
                val conversation = ConversationDBManager.queryByFrom(contact!!.uid)
                conversation?.let {
                    it.nickName = contact!!.nickName
                    ConversationDBManager.updateConversations(it)
                }

                showToast(R.string.search_contact_save_success)
                finish()

            }, {
                showToast(R.string.search_contact_save_error)
                Logger.e(it.stackTraceToString())

            })

        }
    })

    fun queryContract(){
        rxLifeScope.launch {
            contact= address.value?.let { ContactDBManager.queryByID(it) }
            contact?.let {
                nickName.value= it.nickName
                remark.value = it.remark
            }

        }

    }
}