package com.ninjahome.ninja.ui.activity.chat

import android.widget.ImageView
import coil.load
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.ChatMessage
import com.ninjahome.ninja.databinding.ActivityChatBinding
import com.ninjahome.ninja.utils.ChatMessageFactory
import com.ninjahome.ninja.viewmodel.ChatViewModel
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput.AttachmentsListener
import com.stfalcon.chatkit.messages.MessageInput.InputListener
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.stfalcon.chatkit.messages.MessagesListAdapter.OnMessageLongClickListener
import kotlinx.android.synthetic.main.activity_chat.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ChatActivity : BaseActivity<ChatViewModel, ActivityChatBinding>(R.layout.activity_chat),
    OnMessageLongClickListener<ChatMessage>,
    InputListener,
    AttachmentsListener, MessagesListAdapter.OnLoadMoreListener {
    protected lateinit var messagesAdapter: MessagesListAdapter<ChatMessage>
    protected lateinit var imageLoader: ImageLoader
    override val mViewModel: ChatViewModel by viewModel()

    override fun initView() {
        input.setInputListener(this)
        input.setAttachmentsListener(this)

        imageLoader =
            ImageLoader { imageView: ImageView?, url: String?, payload: Any? ->
                imageView?.load(url)
            }

    }

    override fun initData() {
        initAdapter()
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

    override fun onSubmit(input: CharSequence): Boolean {
        messagesAdapter.addToStart(
            ChatMessageFactory.createChatMessage(
                "0",
                "xiaoming",
                "https://cdn.pixabay.com/photo/2017/12/25/17/48/waters-3038803_1280.jpg",
                true,
                input.toString()
            ), true
        )
        return true
    }

    override fun onAddAttachments() {
//        messagesAdapter.addToStart(MessagesFixtures.getImageMessage(), true)
    }

    override fun onMessageLongClick(message: ChatMessage?) {

    }

    private fun initAdapter() {
        val holdersConfig = MessageHolders()
            .setIncomingTextLayout(R.layout.item_custom_incoming_text_message)
            .setOutcomingTextLayout(R.layout.item_custom_outcoming_text_message)
            .setIncomingImageLayout(R.layout.item_custom_incoming_image_message)
            .setOutcomingImageLayout(R.layout.item_custom_outcoming_image_message)
        messagesAdapter = MessagesListAdapter<ChatMessage>("0", holdersConfig, imageLoader)
        messagesAdapter.setOnMessageLongClickListener(this)
        messagesAdapter.setLoadMoreListener(this)
        messagesList.setAdapter(messagesAdapter)
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int) {
    }
}