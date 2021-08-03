package com.ninjahome.ninja.viewmodel

import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import chatLib.ChatLib
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.command.BindingConsumer
import com.ninja.android.lib.command.ResponseCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.ninja.android.lib.utils.SharedPref
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.ConversationModel
import com.ninjahome.ninja.model.bean.Conversation
import com.ninjahome.ninja.model.bean.GroupInfo
import com.ninjahome.ninja.model.bean.Message
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.room.ConversationDBManager
import com.ninjahome.ninja.room.MessageDBManager
import com.ninjahome.ninja.ui.activity.contact.ContactDetailActivity
import com.ninjahome.ninja.ui.activity.contact.ScanContactSuccessActivity
import com.ninjahome.ninja.ui.activity.groupchat.GroupChatDetailActivity
import com.orhanobut.logger.Logger
import io.reactivex.rxjava3.functions.Function
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ConversationViewModel(val model: ConversationModel) : BaseViewModel() {
    var userName: String by SharedPref(context(), Constants.KEY_USER_NAME, "", commit = true)
    var id: String = ""
    var groupChat: GroupInfo? = null
    var isGroup: Boolean = false
    val textData = MutableLiveData("")
    val clickAudioEvent = SingleLiveEvent<Any>()
    val clickSendEvent = SingleLiveEvent<Any>()
    val clickAlbumEvent = SingleLiveEvent<Any>()
    val clickTakePhotoEvent = SingleLiveEvent<Any>()
    val clickLocationEvent = SingleLiveEvent<Any>()
    val touchRecyclerEvent = SingleLiveEvent<Any>()
    val touchAudioEvent = SingleLiveEvent<MotionEvent>()
    val textChangeEvent = SingleLiveEvent<String>()
    var finishRefreshingEvent = SingleLiveEvent<Any>()
    var observableConversationEvent = SingleLiveEvent<Any>()

    val refreshCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            rxLifeScope.launch {
                withContext(Dispatchers.IO) {
                    if (!ChatLib.wsIsOnline()) {
                        ChatLib.wsOnline()
                    }
                }

            }

            finishRefreshingEvent.call()
        }
    })

    override fun clickRightIv() {
        super.clickRightIv()
        if (!groupIsExist()) {
            return
        }

        rxLifeScope.launch {
            if (isGroup) {
                val bundle = Bundle()
                bundle.putParcelable(IntentKey.GROUPCHAT, groupChat)
                startActivity(GroupChatDetailActivity::class.java, bundle)
                return@launch
            }

            val contact = ContactDBManager.queryByID(id)
            if (contact == null) {
                val bundle = Bundle()
                bundle.putString(IntentKey.ID, id)
                startActivity(ScanContactSuccessActivity::class.java, bundle)
            } else {
                val bundle = Bundle()
                bundle.putString(IntentKey.ID, id)
                startActivity(ContactDetailActivity::class.java, bundle)
            }
        }


    }

    val clickAudio = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            clickAudioEvent.call()
        }
    })

    val clickSend = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            clickSendEvent.call()
        }
    })

    val clickAlbum = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            clickAlbumEvent.call()
        }
    })


    val clickTakePhoto = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            clickTakePhotoEvent.call()
        }
    })

    val clickLocation = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            clickLocationEvent.call()
        }
    })

    val textChanged = BindingCommand(bindConsumer = object : BindingConsumer<String> {
        override fun call(t: String) {
            textChangeEvent.value = t
        }

    })

    val touchRecycler = ResponseCommand(function = Function<MotionEvent, Boolean> {
        touchRecyclerEvent.call()
        false
    })

    val touchAudio = ResponseCommand(function = Function<MotionEvent, Boolean> { t ->
        touchAudioEvent.postValue(t)
        false
    })

    fun sendText(data: String) {

        if (!groupIsExist()) {
            return
        }
        val message = Message(0, 0, NinjaApp.instance.account.address, id, Message.MessageDirection.SEND, Message.SentStatus.SENDING, System.currentTimeMillis(), Message.Type.TEXT, unRead = false, msg = data)
        rxLifeScope.launch({
            val conversationId = getConversationId(data)
            message.conversationId = conversationId
            message.id = MessageDBManager.insert(message)
            if (isGroup) {
                model.sendGroupTextMessage(id, data, groupChat!!.memberIdList)
            } else {
                model.sendTextMessage(id, data)
            }
            message.sentStatus = Message.SentStatus.SENT

            MessageDBManager.updateMessage(message)
        }, {
            showToast(R.string.send_error)
            updateFailedStatus(message)
            Logger.e("send error ${it.message}")
        })


    }

    fun sendImage(path: String, compress: Boolean) {
        if (!groupIsExist()) {
            return
        }
        val message = Message(0, 0, NinjaApp.instance.account.address, id, Message.MessageDirection.SEND, Message.SentStatus.SENDING, System.currentTimeMillis(), Message.Type.IMAGE, unRead = false, uri = path, msg = context().getString(R.string.message_type_image))
        rxLifeScope.launch({
            val conversationId = getConversationId(context().getString(R.string.message_type_image))
            message.conversationId = conversationId
            message.id = MessageDBManager.insert(message)
            if (isGroup) {
                model.sendGroupImageMessage(id, path, compress)
            } else {

                model.sendImageMessage(id, path, compress)
            }
            message.sentStatus = Message.SentStatus.SENT
            MessageDBManager.updateMessage(message)
        }, {
            showToast(R.string.send_error)
            updateFailedStatus(message)
            it.printStackTrace()
        })

    }


    fun sendAudio(audioPath: Uri, duration: Int) {
        if (!groupIsExist()) {
            return
        }
        val message = Message(0, 0, NinjaApp.instance.account.address, id, Message.MessageDirection.SEND, Message.SentStatus.SENDING, System.currentTimeMillis(), Message.Type.VOICE, unRead = false, uri = audioPath.path.toString(), duration = duration, msg = context().getString(R.string.message_type_voice))
        rxLifeScope.launch({
            audioPath.path?.let {
                val conversationId = getConversationId(context().getString(R.string.message_type_voice))
                message.conversationId = conversationId
                message.id = MessageDBManager.insert(message)
                if (isGroup) {
                    model.sendGroupVoiceMessage(id, it, duration)
                } else {

                    model.sendVoiceMessage(id, it, duration)
                }
                message.sentStatus = Message.SentStatus.SENT
                MessageDBManager.updateMessage(message)
            }
        }, {
            showToast(R.string.send_error)
            updateFailedStatus(message)
            Logger.e("send error ${it.message}")
        })


    }

    fun sendLocation(lng: Float, lat: Float, poi: String) {
        if (!groupIsExist()) {
            return
        }
        val message = Message(0, 0, NinjaApp.instance.account.address, id, Message.MessageDirection.SEND, Message.SentStatus.SENDING, System.currentTimeMillis(), Message.Type.LOCATION, unRead = false, lat = lat, lng = lng, locationAddress = poi, msg = context().getString(R.string.message_type_location))
        rxLifeScope.launch({
            val conversationId = getConversationId(context().getString(R.string.message_type_location))
            message.conversationId = conversationId
            message.id = MessageDBManager.insert(message)
            if (isGroup) {
                model.sendGroupLocationMessage(id, lng, lat, poi)
            } else {

                model.sendLocationMessage(id, lng, lat, poi)
            }
            message.sentStatus = Message.SentStatus.SENT
            MessageDBManager.updateMessage(message)
        }, {
            showToast(R.string.send_error)
            updateFailedStatus(message)
            Logger.e("send error ${it.message}")
        })

    }

    private suspend fun getConversationId(msg: String): Long {
        var id: Long = 0
        var conversation = queryConversation()
        if (conversation == null) {
            var title: String?
            if (isGroup) {
                title = groupChat?.groupName
            } else {
                title = ContactDBManager.queryNickNameByUID(this.id)
                if (title == null) {
                    title = this.id
                }
            }

            conversation = Conversation(0, this.id, isGroup, msg, System.currentTimeMillis(), 0, title!!, groupId = this.id)
            id = ConversationDBManager.insert(conversation)
            observableConversationEvent.call()
        } else {
            if (isGroup) {
                conversation.msg = "$userName:$msg"
            } else {
                conversation.msg = msg
            }

            conversation.time = System.currentTimeMillis()
            ConversationDBManager.updateConversations(conversation)
            id = conversation.id
        }
        return id
    }

    suspend fun queryConversation(): Conversation? {
        return if (isGroup) {
            ConversationDBManager.queryByGroupId(id)
        } else {
            ConversationDBManager.queryByFrom(id)
        }
    }

    fun updateMessage(message: Message) {
        rxLifeScope.launch({
            message.sentStatus = Message.SentStatus.SENDING
            message.time = System.currentTimeMillis()
            MessageDBManager.updateMessage(message)
            when (message.type) {
                Message.Type.TEXT -> {
                    model.sendTextMessage(id, message.msg)
                }
                Message.Type.IMAGE -> {
                    model.sendImageMessage(id, message.uri, true)
                }
                Message.Type.VOICE -> {
                    model.sendVoiceMessage(id, message.uri, message.duration)
                }
                Message.Type.LOCATION -> {
                    model.sendLocationMessage(id, message.lng, message.lat, message.locationAddress)
                }
            }
            message.sentStatus = Message.SentStatus.SENT
            message.time = System.currentTimeMillis()
            MessageDBManager.updateMessage(message)
        }, {
            updateFailedStatus(message)
        })

    }

    private fun updateFailedStatus(message: Message) {
        rxLifeScope.launch({
            message.sentStatus = Message.SentStatus.FAILED
            message.time = System.currentTimeMillis()
            MessageDBManager.updateMessage(message)
        }, {
            it.printStackTrace()
        })
    }

    fun groupIsExist(): Boolean {
        if (isGroup && groupChat == null) {
            showToast(R.string.create_group_chat_disbanded)
            return false
        }
        return true
    }

}