package com.ninjahome.ninja.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.ui.activity.contact.EditContactActivity
import com.ninjahome.ninja.ui.activity.conversation.ConversationActivity

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ScanContactSuccessViewModel : BaseViewModel() {
    val name = SingleLiveEvent<String>()
    val uid = MutableLiveData<String>()
    var contact: Contact? = null

    val clickSend = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            val bundle = Bundle()
            bundle.putString(IntentKey.UID, uid.value)
            startActivity(ConversationActivity::class.java, bundle)
        }
    })


    val clickAdd = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            val bundle = Bundle()
            bundle.putParcelable(IntentKey.CONTACT, contact)
            bundle.putString(IntentKey.UID, uid.value)
            startActivity(EditContactActivity::class.java, bundle, true)
        }
    })

}