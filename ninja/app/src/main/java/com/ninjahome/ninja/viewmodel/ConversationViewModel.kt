package com.ninjahome.ninja.viewmodel

import android.net.Uri
import android.view.MotionEvent
import androidlib.Androidlib
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.command.BindingConsumer
import com.ninja.android.lib.command.ResponseCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.ConversationModel
import com.ninjahome.ninja.model.bean.Conversation
import com.ninjahome.ninja.model.bean.Message
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.room.ConversationDBManager
import com.ninjahome.ninja.room.MessageDBManager
import com.orhanobut.logger.Logger
import io.reactivex.rxjava3.functions.Function
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class ConversationViewModel : BaseViewModel(), KoinComponent {
    val model: ConversationModel by inject()
    var uid: String = ""
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
                    if (!Androidlib.wsIsOnline()) {
                        Androidlib.wsOnline()
                    }
                }

            }

            finishRefreshingEvent.call()
        }
    })
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
        val message = Message(0, 0, Message.MessageDirection.SEND, Message.SentStatus.SENDING, System.currentTimeMillis(), Message.Type.TEXT, unRead = false, msg = data)
        rxLifeScope.launch({
            val conversationId = getConversationId(data)
            message.conversationId = conversationId
            message.id = MessageDBManager.insert(message)
            model.sendTextMessage(uid, data)
            message.sentStatus = Message.SentStatus.SENT
            MessageDBManager.updateMessage(message)
        }, {
            showToast(R.string.send_error)
            updateFailedStatus(message)
            Logger.e("send error ${it.message}")
        })


    }

    fun sendImage(path: String, compress: Boolean) {
        val message = Message(0, 0, Message.MessageDirection.SEND, Message.SentStatus.SENDING, System.currentTimeMillis(), Message.Type.IMAGE, unRead = false, uri = path, msg = context().getString(R.string.message_type_image))
        rxLifeScope.launch({
            val conversationId = getConversationId(context().getString(R.string.message_type_image))
            message.conversationId = conversationId
            message.id = MessageDBManager.insert(message)
            model.sendImageMessage(uid, path, compress)
            message.sentStatus = Message.SentStatus.SENT
            MessageDBManager.updateMessage(message)
        }, {
            showToast(R.string.send_error)
            updateFailedStatus(message)
            it.printStackTrace()
        })

    }


    fun sendAudio(audioPath: Uri, duration: Int) {
        val message = Message(0, 0, Message.MessageDirection.SEND, Message.SentStatus.SENDING, System.currentTimeMillis(), Message.Type.VOICE, unRead = false, uri = audioPath.path.toString(), duration = duration, msg = context().getString(R.string.message_type_voice))
        rxLifeScope.launch({
            audioPath.path?.let {
                val conversationId = getConversationId(context().getString(R.string.message_type_voice))
                message.conversationId = conversationId
                message.id = MessageDBManager.insert(message)
                model.sendVoiceMessage(uid, it, duration)
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
        val message = Message(0, 0, Message.MessageDirection.SEND, Message.SentStatus.SENDING, System.currentTimeMillis(), Message.Type.LOCATION, unRead = false, lat = lat, lng = lng, locationAddress = poi, msg = context().getString(R.string.message_type_location))
        rxLifeScope.launch({
            val conversationId = getConversationId(context().getString(R.string.message_type_location))
            message.conversationId = conversationId
            message.id = MessageDBManager.insert(message)
            model.sendLocationMessage(uid, lng, lat, poi)
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
            var nickName = ContactDBManager.queryNickNameByUID(uid)
            if (nickName == null) {
                nickName = uid
            }
            conversation = Conversation(0, uid, msg, System.currentTimeMillis(), 0, nickName)
            id = ConversationDBManager.insert(conversation)
            observableConversationEvent.call()
        } else {
            conversation.msg = msg
            conversation.time = System.currentTimeMillis()
            ConversationDBManager.updateConversations(conversation)
            id = conversation.id
        }

        return id
    }

    suspend fun queryConversation(): Conversation? {
        return ConversationDBManager.queryByFrom(uid)
    }

    fun updateMessage(message: Message) {
        rxLifeScope.launch({
            message.sentStatus = Message.SentStatus.SENDING
            message.time = System.currentTimeMillis()
            MessageDBManager.updateMessage(message)
            when (message.type) {
                Message.Type.TEXT -> {
                    model.sendTextMessage(uid, message.msg)
                }
                Message.Type.IMAGE -> {
                    model.sendImageMessage(uid, message.uri, true)
                }
                Message.Type.VOICE -> {
                    model.sendVoiceMessage(uid, message.uri, message.duration)
                }
                Message.Type.LOCATION -> {
                    model.sendLocationMessage(uid, message.lng, message.lat, message.locationAddress)
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

}