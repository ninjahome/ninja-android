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
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.event.EventSendImageMessage
import com.ninjahome.ninja.event.EventSendLocationMessage
import com.ninjahome.ninja.event.EventSendTextMessage
import com.ninjahome.ninja.event.EventSendVoiceMessage
import com.ninjahome.ninja.model.ConversationModel
import com.ninjahome.ninja.model.bean.*
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
            model.sendTextMessage(uid, data)
            val textMessage = TextMessage(Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, System.currentTimeMillis(), data)
            EventBus.getDefault().postSticky(EventSendTextMessage(uid, NinjaApp.instance.account.address, textMessage))
        }, {
            showToast(R.string.send_error)
            Logger.e("send error ${it.message}")
        })


    }

    fun sendImage(path: String) {
        rxLifeScope.launch({
            model.sendImageMessage(uid, path)
            val imageMessage = ImageMessage(Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, System.currentTimeMillis(), path)
            EventBus.getDefault().postSticky(EventSendImageMessage(uid, NinjaApp.instance.account.address, imageMessage))
        }, {
            showToast(R.string.send_error)
            Logger.e("send error ${it.message}")
        })


    }

    fun sendAudio(audioPath: Uri, duration: Int) {
        rxLifeScope.launch({
            audioPath.path?.let {
                model.sendAudioMessage(uid, it, duration)
                val voiceMessage = VoiceMessage(Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, System.currentTimeMillis(), audioPath.path!!, duration.toLong())
                EventBus.getDefault().postSticky(EventSendVoiceMessage(uid, NinjaApp.instance.account.address, voiceMessage))
            }
        }, {
            showToast(R.string.send_error)
            Logger.e("send error ${it.message}")
        })


    }

    fun sendLocation(locationMessage: LocationMessage) {
        rxLifeScope.launch {
            model.sendLocationMessage(uid,locationMessage.lat,locationMessage.lng,locationMessage.poi)
            EventBus.getDefault().postSticky(EventSendLocationMessage(uid, NinjaApp.instance.account.address, locationMessage))
        }

    }

}