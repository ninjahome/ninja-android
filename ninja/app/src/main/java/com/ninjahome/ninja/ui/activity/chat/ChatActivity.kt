package com.ninjahome.ninja.ui.activity.chat

import android.text.TextUtils
import android.widget.ImageView
import androidlib.Androidlib
import androidx.lifecycle.rxLifeScope
import coil.load
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityChatBinding
import com.ninjahome.ninja.event.EventReceiveConversation
import com.ninjahome.ninja.event.EventSendConversation
import com.ninjahome.ninja.event.EventUpdateConversationNickName
import com.ninjahome.ninja.model.bean.ChatMessage
import com.ninjahome.ninja.model.bean.Conversation
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.utils.ChatMessageFactory
import com.ninjahome.ninja.viewmodel.ChatViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_chat.*
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
open class ChatActivity : BaseActivity<ChatViewModel, ActivityChatBinding>(R.layout.activity_chat) {

    companion object {
        var conversation: Conversation? = null
    }

    override val mViewModel: ChatViewModel by viewModel()

    override fun initView() {
        EventBus.getDefault().register(this)
        mViewModel.uid = intent.getStringExtra(IntentKey.UID)!!


        mViewModel.rightIv.set(R.drawable.contact_new_friend)
        mViewModel.showRightIv.set(true)
        setChatTitle()
    }

    private fun setChatTitle() {
        rxLifeScope.launch {
            val title = ContactDBManager.queryNickNameByUID(mViewModel.uid)
            if (TextUtils.isEmpty(title)) {
                mViewModel.title.set(mViewModel.uid)
            } else {
                mViewModel.title.set(title)
            }
        }

    }

    override fun initData() {
        initAdapter()
        conversation = NinjaApp.instance.conversations.get(mViewModel.uid)
        conversation?.let {
            it.messages.forEach {
//                messagesAdapter.addToStart(ChatMessageFactory.createChatMessage(it.user.id, it.user.name, it.user.avatar, true, it.text), true)
            }

        }
    }

    override fun initObserve() {
        mViewModel.finishRefreshingEvent.observe(this) {
            try {
                Androidlib.wsOnline()
                setLineState()
                swipeRefreshLayout.isRefreshing = false
            } catch (e: Exception) {
                Logger.d(e.message)
                swipeRefreshLayout.isRefreshing = false
            }

        }
    }

    override fun onResume() {
        super.onResume()
        setLineState()
    }

    fun setLineState() {
        mViewModel.unline.value = !Androidlib.wsIsOnline()
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel


    private fun initAdapter() {
//        val holdersConfig = MessageHolders().setIncomingTextLayout(R.layout.item_custom_incoming_text_message).setOutcomingTextLayout(R.layout.item_custom_outcoming_text_message).setIncomingImageLayout(R.layout.item_custom_incoming_image_message).setOutcomingImageLayout(R.layout.item_custom_outcoming_image_message)
//        messagesAdapter = MessagesListAdapter<ChatMessage>(NinjaApp.instance.account.address, holdersConfig, imageLoader)
//        //        messagesAdapter.setOnMessageLongClickListener(this)
//        messagesAdapter.setLoadMoreListener(this)
//        messagesList.setAdapter(messagesAdapter)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun receiveNewConversation(eventConversation: EventReceiveConversation) {
        if (eventConversation.fromAddress == mViewModel.uid) {
            NinjaApp.instance.conversations[mViewModel.uid]!!.unreadNo = 0
            NinjaApp.instance.conversations[mViewModel.uid]!!.unreadNoStr = "0"
//            messagesAdapter.addToStart(ChatMessageFactory.createChatMessage(eventConversation.fromAddress, mViewModel.title.get()!!, eventConversation.chatMessage.user.avatar, true, eventConversation.chatMessage.text), true)
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateConversationNickName(eventUpdateConversationNickName: EventUpdateConversationNickName) {
        if (eventUpdateConversationNickName.uid == mViewModel.uid) {
            NinjaApp.instance.conversations.get(mViewModel.uid)?.let { mViewModel.title.set(it.nickName) }

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun sendConversation(eventSendConversation: EventSendConversation) {
//        messagesAdapter.addToStart(ChatMessageFactory.createChatMessage(NinjaApp.instance.account.address, "xiaoming", "https://img0.baidu.com/it/u=232786431,3173227563&fm=26&fmt=auto&gp=0.jpg", true, mViewModel.message.value!!), true)
        mViewModel.message.value = ""
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}