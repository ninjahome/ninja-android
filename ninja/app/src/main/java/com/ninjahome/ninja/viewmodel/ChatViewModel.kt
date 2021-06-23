package com.ninjahome.ninja.viewmodel

import android.os.Bundle
import android.text.TextUtils
import androidlib.Androidlib
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.ui.activity.contact.ContactDetailActivity
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class ChatViewModel : BaseViewModel() {
    lateinit var uid: String
    val unline = MutableLiveData<Boolean>()
    var finishRefreshingEvent = SingleLiveEvent<Any>()
    var message = MutableLiveData<String>()
    override fun clickRightIv() {
        super.clickRightIv()
        val bundle = Bundle()
        bundle.putString(IntentKey.UID, uid)
        startActivity(ContactDetailActivity::class.java, bundle)
    }

    val refreshCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            rxLifeScope.launch {
                withTimeout(200) {
                    withContext(Dispatchers.IO) {
                        if (!Androidlib.wsIsOnline()) {
                            Androidlib.wsOnline()
                        }
                    }

                }

            }

            finishRefreshingEvent.call()
        }
    })

    val clickSend = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            if (TextUtils.isEmpty(message.value)) {
                showToast(R.string.input_send_message)
                return
            }
            rxLifeScope.launch({
                withContext(Dispatchers.IO) {

                    //                    Androidlib.writeMessage(cahtMsg)
                    //                    val chatMessage = ChatMessageFactory.createChatMessage(NinjaApp.instance.account.address, uid, "https://img0.baidu.com/it/u=232786431,3173227563&fm=26&fmt=auto&gp=0.jpg", true, message.value!!)
                    //                    EventBus.getDefault().postSticky(EventSendConversation(uid, NinjaApp.instance.account.address, cahtMsg))

                }
            }, {
                showToast(R.string.send_error)
                Logger.e("send error ${it.message}")
            })


        }
    })

}