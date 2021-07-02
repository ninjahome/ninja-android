package com.ninjahome.ninja.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.room.ConversationDBManager
import com.ninjahome.ninja.ui.activity.contact.EditContactActivity
import com.ninjahome.ninja.ui.activity.conversation.ConversationActivity
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class ContactDetailViewModel : BaseViewModel() {
    val name = SingleLiveEvent<String>()
    val showDeleteDialogEvent = SingleLiveEvent<Any>()
    val deleteSuccessEvent = SingleLiveEvent<Any>()
    val uid = MutableLiveData<String>()
    val isContact = MutableLiveData(false)
    var contact: Contact? = null


    val clickSend = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            val bundle = Bundle()
            bundle.putString(IntentKey.UID, uid.value)
            startActivity(ConversationActivity::class.java, bundle)
        }
    })

    override fun clickRightIv() {
        super.clickRightIv()
        showDeleteDialogEvent.call()
    }

    val clickAdd = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            val bundle = Bundle()
            bundle.putParcelable(IntentKey.CONTACT, contact)
            bundle.putString(IntentKey.UID, uid.value)
            startActivity(EditContactActivity::class.java, bundle, true)
        }
    })

    fun getContact(uid: String) {
        rxLifeScope.launch {
            contact = ContactDBManager.queryByID(uid)
            if (contact != null) {
                isContact.value = true
                name.value = contact!!.nickName
            }
            this@ContactDetailViewModel.uid.value = uid
        }
    }

    fun deleteContact() {
        rxLifeScope.launch {
            contact?.let {
                ContactDBManager.delete(it)
                val conversation = ConversationDBManager.queryByFrom(it.uid)
                conversation?.let {c->
                    c.nickName = contact!!.uid
                    ConversationDBManager.updateConversations(c)
                }
            }

            deleteSuccessEvent.call()
        }
    }
}