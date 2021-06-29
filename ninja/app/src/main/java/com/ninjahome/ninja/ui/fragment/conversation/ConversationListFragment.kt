package com.ninjahome.ninja.ui.fragment.conversation

import android.os.Handler
import android.os.Looper
import androidlib.Androidlib
import androidlib.Androidlib.*
import com.ninja.android.lib.base.BaseFragment
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.FragmentConversationListBinding
import com.ninjahome.ninja.event.*
import com.ninjahome.ninja.model.bean.*
import com.ninjahome.ninja.room.ConversationDBManager
import com.ninjahome.ninja.viewmodel.ConversationItemViewModel
import com.ninjahome.ninja.viewmodel.ConversationListViewModel
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.fragment_conversation_list.*
import kotlinx.android.synthetic.main.fragment_conversation_list.swipeRefreshLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class ConversationListFragment : BaseFragment<ConversationListViewModel, FragmentConversationListBinding>(R.layout.fragment_conversation_list), Handler.Callback {
    override val mViewModel: ConversationListViewModel by viewModel()
    private val handler: Handler by lazy { Handler(Looper.getMainLooper(), this@ConversationListFragment) }

    override fun initView() {
        EventBus.getDefault().register(this)
        recyclerView.itemAnimator = null
    }

    override fun initData() {

    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
        mViewModel.finishRefreshingEvent.observe(this) {
            setLineState()
            swipeRefreshLayout.isRefreshing = false

        }

        ConversationDBManager.all().observe(this) {
            println("------------------------------${it?.size}")
            mViewModel.items.clear()
            it?.forEach {
                mViewModel.items.add(ConversationItemViewModel(mViewModel, it))
            }
        }
    }


    override fun onShow() {
        super.onShow()
        setLineState()
    }

    override fun onResume() {
        super.onResume()
        setLineState()
    }

    fun setLineState() {
        handler.postDelayed({
            mViewModel.unline.value = !Androidlib.wsIsOnline()
        }, 1000)

    }


    //    @Subscribe(threadMode = ThreadMode.MAIN)
    //    fun receiveTextMessage(eventReceiveTextMessage: EventReceiveTextMessage) {
    //        addAdapterData(eventReceiveTextMessage.fromAddress, eventReceiveTextMessage.textMessage, false)
    //    }
    //
    //
    //    @Subscribe(threadMode = ThreadMode.MAIN)
    //    fun receiveImageMessage(eventReceiveImageMessage: EventReceiveImageMessage) {
    //        addAdapterData(eventReceiveImageMessage.fromAddress, eventReceiveImageMessage.imageMessage, false)
    //    }
    //
    //
    //    @Subscribe(threadMode = ThreadMode.MAIN)
    //    fun receiveVoiceMessage(eventReceiveVoiceMessage: EventReceiveVoiceMessage) {
    //        addAdapterData(eventReceiveVoiceMessage.fromAddress, eventReceiveVoiceMessage.voiceMessage, false)
    //    }


    //    @Subscribe(threadMode = ThreadMode.MAIN)
    //    fun receiveLocationMessage(eventReceiveLocationMessage: EventReceiveLocationMessage) {
    //        addAdapterData(eventReceiveLocationMessage.fromAddress, eventReceiveLocationMessage.locationMessage, false)
    //    }
    //
    //
    //    @Subscribe(threadMode = ThreadMode.MAIN)
    //    fun sendTextMessage(eventSendTextMessage: EventSendTextMessage) {
    //        addAdapterData(eventSendTextMessage.fromAddress, eventSendTextMessage.message, true)
    //    }
    //
    //    @Subscribe(threadMode = ThreadMode.MAIN)
    //    fun sendImageMessage(eventSendImageMessage: EventSendImageMessage) {
    //        addAdapterData(eventSendImageMessage.fromAddress, eventSendImageMessage.message, true)
    //    }
    //
    //    @Subscribe(threadMode = ThreadMode.MAIN)
    //    fun sendVoiceMessage(eventSendVoiceMessage: EventSendVoiceMessage) {
    //        addAdapterData(eventSendVoiceMessage.fromAddress, eventSendVoiceMessage.message, true)
    //    }
    //
    //    @Subscribe(threadMode = ThreadMode.MAIN)
    //    fun sendLocationMessage(eventSendLocationMessage: EventSendLocationMessage) {
    //        addAdapterData(eventSendLocationMessage.fromAddress, eventSendLocationMessage.message, true)
    //    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun offline(eventOffline: EventOffline) {
        setLineState()
    }


    //    private fun addAdapterData(fromAddress: String, message: Message, isSend: Boolean) {
    //        message.direction = if (isSend) Message.MessageDirection.SEND else Message.MessageDirection.RECEIVE
    //
    //        if (NinjaApp.instance.conversations.contains(fromAddress)) {
    //
    //            NinjaApp.instance.conversations[fromAddress]!!.messages.add(message)
    //            NinjaApp.instance.conversations[fromAddress]!!.lastMessage = message
    //
    //            if (isSend) {
    //                NinjaApp.instance.conversations[fromAddress]!!.unreadNo = 0
    //                NinjaApp.instance.conversations[fromAddress]!!.unreadNoStr = "0"
    //            } else {
    //                NinjaApp.instance.conversations[fromAddress]!!.unreadNo = NinjaApp.instance.conversations[fromAddress]!!.unreadNo + 1
    //                NinjaApp.instance.conversations[fromAddress]!!.unreadNoStr = (NinjaApp.instance.conversations[fromAddress]!!.unreadNo).toString()
    //            }
    //            mViewModel.updateConversation()
    //        } else {
    //            val chatMessages = mutableListOf<Message>()
    //            chatMessages.add(message)
    //            var unreadNo = 1
    //            if (isSend) {
    //                unreadNo = 0
    //            }
    //            val conversation = Conversation2(fromAddress, chatMessages, message, unreadNo, unreadNo.toString(), System.currentTimeMillis(), fromAddress, "https://img0.baidu.com/it/u=1950977217,4151841346&fm=26&fmt=auto&gp=0.jpg")
    //            NinjaApp.instance.conversations[fromAddress] = conversation
    //            rxLifeScope.launch {
    //                var nickName = ContactDBManager.queryNickNameByUID(fromAddress)
    //                if (TextUtils.isEmpty(nickName)) {
    //                    nickName = fromAddress
    //                }
    //                conversation.nickName = nickName!!
    //                mViewModel.updateConversation()
    //            }
    //
    //        }
    //    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun handleMessage(msg: android.os.Message): Boolean {
        return false
    }

}