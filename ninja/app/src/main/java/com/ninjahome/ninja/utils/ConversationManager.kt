package com.ninjahome.ninja.utils

import android.text.TextUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import chatLib.ChatLib
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.Conversation
import com.ninjahome.ninja.model.bean.Message
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.room.ConversationDBManager
import com.ninjahome.ninja.room.GroupDBManager
import com.ninjahome.ninja.room.MessageDBManager
import com.rxlife.coroutine.RxLifeScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 *Author:Mr'x
 *Time:2021/8/11
 *Description:
 */
object ConversationManager: LifecycleObserver {
    val rxLifeScope: RxLifeScope = RxLifeScope()
    var job: Job? = null
        private set

    private val mutex = Mutex()

    fun receiveTextMessage(from: String, to: String, data: String, time: Long) {
        job = rxLifeScope.launch {
            val conversation = insertOrUpdateConversation(from, data, time, false)
            val message = Message(0, conversation.id, from, to, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.TEXT, msg = data)
            insertMessage(message, conversation)
        }
    }

    fun receiveImageMessage(from: String, to: String, payload: ByteArray, time: Long) {
        job = rxLifeScope.launch {
            val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_image), time, false)
            val message = Message(0, conversation.id, from, to, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.IMAGE, data = payload, msg = context().getString(R.string.message_type_image))
            message.uri = FileUtils.saveImageToPath(Constants.PHOTO_SAVE_DIR, payload)
            insertMessage(message, conversation)

        }
    }

    fun receiveVoiceMessage(from: String, to: String, payload: ByteArray, length: Long, time: Long) {
        job = rxLifeScope.launch {
            val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_voice), time, false)
            val message = Message(0, conversation.id, from, to, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.VOICE, msg = context().getString(R.string.message_type_voice), duration = length.toInt())
            message.uri = FileUtils.saveVoiceToPath(Constants.AUDIO_SAVE_DIR, payload)
            insertMessage(message, conversation)
        }
    }

    fun receiverLocationMessage(from: String, to: String, lng: Float, lat: Float, locationAddress: String, time: Long) {
        job = rxLifeScope.launch {
            val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_location), time, false)
            val message = Message(0, conversation.id, from, to, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.LOCATION, lat = lat, lng = lng, locationAddress = locationAddress, msg = context().getString(R.string.message_type_location))
            insertMessage(message, conversation)
        }

    }


    fun receiveGroupTextMessage(from: String, groupId: String, payload: String, time: Long) {
        job =  rxLifeScope.launch {
            val conversation = insertOrUpdateConversation(from, payload, time, true, groupId)
            val message = Message(0, conversation.id, from, NinjaApp.instance.account.address, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.TEXT, msg = payload)
            insertMessage(message, conversation)
        }
    }

    fun receiveGroupImageMessage(from: String, groupId: String, payload: ByteArray, time: Long) {
        job = rxLifeScope.launch {
            val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_image), time, true, groupId)
            val message = Message(0, conversation.id, from, NinjaApp.instance.account.address, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.IMAGE, msg = context().getString(R.string.message_type_image))
            message.uri = FileUtils.saveImageToPath(Constants.PHOTO_SAVE_DIR, payload)
            insertMessage(message, conversation)

        }
    }

    fun receiveGroupVoiceMessage(from: String, groupId: String, payload: ByteArray, length: Long, time: Long) {
        job = rxLifeScope.launch {
            val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_voice), time, true, groupId)
            val message = Message(0, conversation.id, from, NinjaApp.instance.account.address, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.VOICE, msg = context().getString(R.string.message_type_voice), duration = length.toInt())
            message.uri = FileUtils.saveVoiceToPath(Constants.AUDIO_SAVE_DIR, payload)
            insertMessage(message, conversation)
        }
    }

    fun receiverGroupLocationMessage(from: String, groupId: String, lng: Float, lat: Float, name: String, time: Long) {
        job =  rxLifeScope.launch {
            val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_location), time, true, groupId)
            val message = Message(0, conversation.id, from, NinjaApp.instance.account.address, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.LOCATION, lat = lat, lng = lng, locationAddress = name, msg = context().getString(R.string.message_type_location))
            insertMessage(message, conversation)
        }
    }

    private suspend fun insertOrUpdateConversation(from: String, msg: String, time: Long, isGroup: Boolean, groupId: String = ""): Conversation {
        mutex.withLock {
            var conversation: Conversation?
            var nickName: String? = from
            if (isGroup) {
                conversation = ConversationDBManager.queryByGroupId(groupId)
                val groupInfo = GroupDBManager.queryByGroupId(groupId)
                if (groupInfo != null) {
                    val memberIds = groupInfo.memberIdList.fromJson<List<String>>()
                    val memberNames = groupInfo.memberNickNameList.fromJson<List<String>>()
                    for (index in 0 until memberIds!!.size) {
                        if (memberIds[index].equals(from)) {
                            nickName = memberNames!![index] as String
                        }
                    }
                } else {
                    ChatLib.syncGroup(from, groupId)
                }
            } else {
                conversation = ConversationDBManager.queryByFrom(from)
            }
            val contactNickName = ContactDBManager.queryTitleByUID(from)
            contactNickName?.let {
                nickName = it
            }

            if (conversation == null) {
                conversation = Conversation(0, from, isGroup, msg, time * 1000, 1, groupId = groupId)
                if (isGroup) {
                    conversation.from = ""
                    val groupInfo = GroupDBManager.queryByGroupId(groupId)
                    groupInfo?.let { conversation.title = it.groupName }
                    val msg = if (TextUtils.isEmpty(nickName)) from else "${nickName!!}:$msg"
                    conversation.msg = msg
                } else {
                    conversation.title = if (TextUtils.isEmpty(nickName)) from else nickName!!
                }

                conversation.id = ConversationDBManager.insert(conversation)
            } else {

                conversation.time = time * 1000
                if (isGroup) {
                    val msg = if (TextUtils.isEmpty(nickName)) from else "${nickName!!}:$msg"
                    conversation.msg = msg
                    val group = GroupDBManager.queryByGroupId(groupId)
                    group?.let {
                        conversation.msg = msg
                        conversation.title = it.groupName
                    }
                } else {
                    val nickName = ContactDBManager.queryTitleByUID(from)
                    conversation.title = if (TextUtils.isEmpty(nickName)) from else nickName!!
                    conversation.msg = msg
                }
                ConversationDBManager.updateConversations(conversation)
            }
            return conversation
        }

    }

    private suspend fun insertMessage(message: Message, conversation: Conversation) {
        MessageDBManager.insert(message)
        updateConversationUnreadCount(conversation)
    }

    suspend fun updateConversationUnreadCount(conversation: Conversation) {
        val unreadCount = MessageDBManager.queryUnReadCount(conversation.id)
        conversation.unreadCount = unreadCount
        ConversationDBManager.updateConversations(conversation)
    }

    fun clearUnreadNumber(id: String, isGroup: Boolean) {
        rxLifeScope.launch {
            val conversation = if (isGroup) {
                ConversationDBManager.queryByGroupId(id)
            } else {
                ConversationDBManager.queryByFrom(id)
            }
            conversation?.let {
                MessageDBManager.updateMessage2Read(it.id)
                it.unreadCount = 0
                ConversationDBManager.updateConversations(it)
            }

        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        job?.cancel()
    }

}