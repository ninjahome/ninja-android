package com.ninjahome.ninja.ui.fragment.conversation

import android.text.TextUtils
import androidlib.Androidlib
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseFragment
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.FragmentConversationListBinding
import com.ninjahome.ninja.event.EventReceiveConversation
import com.ninjahome.ninja.event.EventSendConversation
import com.ninjahome.ninja.event.EventUnReadMessages
import com.ninjahome.ninja.event.EventUpdateConversationNickName
import com.ninjahome.ninja.model.bean.ChatMessage
import com.ninjahome.ninja.model.bean.Conversation
import com.ninjahome.ninja.model.bean.TextData
import com.ninjahome.ninja.model.bean.UnreadMessageItem
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.utils.ChatMessageFactory
import com.ninjahome.ninja.utils.fromJson
import com.ninjahome.ninja.viewmodel.ConversationListViewModel
import kotlinx.android.synthetic.main.fragment_conversation_list.*
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
class ConversationListFragment : BaseFragment<ConversationListViewModel, FragmentConversationListBinding>(R.layout.fragment_conversation_list) {
    override val mViewModel: ConversationListViewModel by viewModel()

    override fun initView() {
        EventBus.getDefault().register(this)
        recyclerView.itemAnimator = null
    }

    override fun initData() {
    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
        mViewModel.finishRefreshingEvent.observe(this) {
            swipeRefreshLayout.isRefreshing = false
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun receiveNewConversation(eventConversation: EventReceiveConversation) {
        addAdapterData(eventConversation.fromAddress, eventConversation.chatMessage, false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun sendConversation(eventSendConversation: EventSendConversation) {
        addAdapterData(eventSendConversation.fromAddress, eventSendConversation.chatMessage, true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateNickName(eventUpdateConversationNickName: EventUpdateConversationNickName) {
        mViewModel.updateConversation()
    }

    private fun addAdapterData(fromAddress: String, chatMessage: ChatMessage, isSend: Boolean) {

        if (NinjaApp.instance.conversations.contains(fromAddress)) {
            NinjaApp.instance.conversations.get(fromAddress)!!.lastMessage = chatMessage
            NinjaApp.instance.conversations.get(fromAddress)!!.messages.add(chatMessage)
            if (isSend) {
                NinjaApp.instance.conversations.get(fromAddress)!!.unreadNo = 0
                NinjaApp.instance.conversations.get(fromAddress)!!.unreadNoStr = "0"
            } else {
                NinjaApp.instance.conversations.get(fromAddress)!!.unreadNo = NinjaApp.instance.conversations.get(fromAddress)!!.unreadNo + 1
                NinjaApp.instance.conversations.get(fromAddress)!!.unreadNoStr = (NinjaApp.instance.conversations.get(fromAddress)!!.unreadNo).toString()
            }
            mViewModel.updateConversation()
        } else {
            val chatMessages = mutableListOf<ChatMessage>()
            chatMessages.add(chatMessage)
            rxLifeScope.launch {
                var nickName = ContactDBManager.queryNickNameByUID(fromAddress)
                if (TextUtils.isEmpty(nickName)) {
                    nickName = fromAddress
                }
                val conversation = Conversation(fromAddress, chatMessages, chatMessage, 1, "1", System.currentTimeMillis(), nickName!!, "https://img0.baidu.com/it/u=1950977217,4151841346&fm=26&fmt=auto&gp=0.jpg")
                NinjaApp.instance.conversations.put(fromAddress, conversation)
                mViewModel.updateConversation()
            }

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    fun receiveUnReadMessage(eventUnReadMessages: EventUnReadMessages) {
        val unreadMessageItems = eventUnReadMessages.unReadMessagesJson.fromJson<List<UnreadMessageItem>>()
        unreadMessageItems?.forEach {
            val textData: TextData = String(Androidlib.unmarshalGoByte(it.payLoad)).fromJson()!!
            val chatMessage = ChatMessageFactory.createChatMessage("0", it.from, "https://img0.baidu.com/it/u=1950977217,4151841346&fm=26&fmt=auto&gp=0.jpg", true, textData.data)
            if (NinjaApp.instance.conversations.contains(it.from)) {
                NinjaApp.instance.conversations.get(it.from)!!.lastMessage = chatMessage
                NinjaApp.instance.conversations.get(it.from)!!.unreadNo = NinjaApp.instance.conversations.get(it.from)!!.unreadNo + 1
                NinjaApp.instance.conversations.get(it.from)!!.unreadNoStr = NinjaApp.instance.conversations.get(it.from)!!.unreadNo.toString()
                NinjaApp.instance.conversations.get(it.from)!!.messages.add(chatMessage)
            } else {
                val chatMessages = mutableListOf<ChatMessage>()
                chatMessages.add(chatMessage)
                rxLifeScope.launch {
                    var nickName = ContactDBManager.queryNickNameByUID(it.from)
                    if (TextUtils.isEmpty(nickName)) {
                        nickName = it.from
                    }
                    val conversation = Conversation(it.from, chatMessages, chatMessage, 1, "1", System.currentTimeMillis(), it.from)
                    NinjaApp.instance.conversations.put(it.from, conversation)
                }

            }
        }

        mViewModel.updateConversation()

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}