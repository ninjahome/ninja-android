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
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.event.EventSendImageMessage
import com.ninjahome.ninja.event.EventSendLocationMessage
import com.ninjahome.ninja.event.EventSendTextMessage
import com.ninjahome.ninja.event.EventSendVoiceMessage
import com.ninjahome.ninja.model.ConversationModel
import com.ninjahome.ninja.model.bean.*
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.room.ConversationDBManager
import com.ninjahome.ninja.room.MessageDBManager
import com.orhanobut.logger.Logger
import io.reactivex.rxjava3.functions.Function
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
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
        rxLifeScope.launch({
            val conversationId = getConversationId(data)
            val message = Message(0,conversationId,Message.MessageDirection.SEND, Message.SentStatus.SENT,System.currentTimeMillis(),Message.Type.TEXT,unRead = false,msg=data)
            MessageDBManager.insert(message)
            model.sendTextMessage(uid, data)
            val textMessage = TextMessage(0,Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, System.currentTimeMillis(), data)
            EventBus.getDefault().postSticky(EventSendTextMessage(uid, NinjaApp.instance.account.address, textMessage))
        }, {
            showToast(R.string.send_error)
            Logger.e("send error ${it.message}")
        })


    }

    fun sendImage(path: String) {
        rxLifeScope.launch({
            val conversationId = getConversationId(context().getString(R.string.message_type_image))
            val message = Message(0,conversationId,Message.MessageDirection.SEND, Message.SentStatus.SENT,System.currentTimeMillis(),Message.Type.IMAGE,unRead = false,uri = path)
            MessageDBManager.insert(message)
            model.sendImageMessage(uid, path)
//            val imageMessage = ImageMessage(0,Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, System.currentTimeMillis(), path)
//            EventBus.getDefault().postSticky(EventSendImageMessage(uid, NinjaApp.instance.account.address, imageMessage))
        }, {
            showToast(R.string.send_error)
            Logger.e("send error ${it.message}")
        })

    }

    fun sendAudio(audioPath: Uri, duration: Int) {
        rxLifeScope.launch({
            audioPath.path?.let {
                val conversationId = getConversationId(context().getString(R.string.message_type_voice))
                val message = Message(0,conversationId,Message.MessageDirection.SEND, Message.SentStatus.SENT,System.currentTimeMillis(),Message.Type.VOICE,unRead = false,uri = audioPath.path.toString(),duration=duration)
                MessageDBManager.insert(message)
                model.sendAudioMessage(uid, it, duration)
//                val voiceMessage = VoiceMessage(0,Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, System.currentTimeMillis(), audioPath.path!!, duration.toLong())
//                EventBus.getDefault().postSticky(EventSendVoiceMessage(uid, NinjaApp.instance.account.address, voiceMessage))
            }
        }, {
            showToast(R.string.send_error)
            Logger.e("send error ${it.message}")
        })


    }

    fun sendLocation(locationMessage: LocationMessage) {
        rxLifeScope.launch ({
            val conversationId = getConversationId(context().getString(R.string.message_type_location))
            val message = Message(0,conversationId,Message.MessageDirection.SEND, Message.SentStatus.SENT,System.currentTimeMillis(),Message.Type.LOCATION,unRead = false,lat = locationMessage.lat,lng = locationMessage.lng,locationAddress = locationMessage.poi)
            MessageDBManager.insert(message)
            model.sendLocationMessage(uid, locationMessage.lng, locationMessage.lat, locationMessage.poi)
//            EventBus.getDefault().postSticky(EventSendLocationMessage(uid, NinjaApp.instance.account.address, locationMessage))
        }, {
            showToast(R.string.send_error)
            Logger.e("send error ${it.message}")
        })

    }

    private suspend fun getConversationId(msg:String):Long {
        var id:Long=0
        var conversation = queryConversation()
        if(conversation == null){
            var nickName = ContactDBManager.queryNickNameByUID(uid)
            if(nickName ==null ){
                nickName = uid
            }
            conversation = Conversation(0,uid, msg,System.currentTimeMillis(),0,nickName)
            id= ConversationDBManager.insert(conversation)
            observableConversationEvent.call()
        }else{
            conversation.msg = msg
            conversation.time = System.currentTimeMillis()
            ConversationDBManager.updateConversations(conversation)
            id= conversation.id
        }

        return id
    }

    suspend fun queryConversation(): Conversation? {
       return ConversationDBManager.queryByFrom(uid)
    }

}