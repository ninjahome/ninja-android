package com.ninjahome.ninja.viewmodel

import android.os.Bundle
import android.text.TextUtils
import androidlib.Androidlib
import androidx.lifecycle.MutableLiveData
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.event.EventSendConversation
import com.ninjahome.ninja.model.bean.TextData
import com.ninjahome.ninja.ui.activity.contact.detail.ContactDetailActivity
import com.ninjahome.ninja.utils.ChatMessageFactory
import com.ninjahome.ninja.utils.JsonUtils
import org.greenrobot.eventbus.EventBus

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ChatViewModel : BaseViewModel() {
    lateinit var uid: String
    var message = MutableLiveData<String>()
    override fun clickRightIv() {
        super.clickRightIv()
        val bundle = Bundle()
        bundle.putString(IntentKey.UID, uid)
        startActivity(ContactDetailActivity::class.java, bundle)
    }

    val clickSend = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            if (TextUtils.isEmpty(message.value)) {
                showToast(R.string.input_send_message)
                return
            }
            Androidlib.writeMessage(uid, JsonUtils.object2Json(TextData(message.value!!, 0), TextData::class.java).toByteArray())
            val chatMessage = ChatMessageFactory.createChatMessage(NinjaApp.instance.account.address, uid, "https://img0.baidu.com/it/u=232786431,3173227563&fm=26&fmt=auto&gp=0.jpg", true, message.value!!)

            EventBus.getDefault().postSticky(EventSendConversation(uid, NinjaApp.instance.account.address, chatMessage))


        }
    })

}