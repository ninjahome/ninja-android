package com.ninjahome.ninja

import android.text.TextUtils
import androidlib.Androidlib
import androidlib.AppCallBack
import coil.load
import com.lqr.emoji.LQREmotionKit
import com.ninja.android.lib.base.BaseApplication
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.event.*
import com.ninjahome.ninja.model.ConversationModel
import com.ninjahome.ninja.model.CreateAccountModel
import com.ninjahome.ninja.model.UnlockModel
import com.ninjahome.ninja.model.bean.*
import com.ninjahome.ninja.push.PushHelper
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.room.ConversationDBManager
import com.ninjahome.ninja.room.MessageDBManager
import com.ninjahome.ninja.utils.FileUtils
import com.ninjahome.ninja.viewmodel.*
import com.orhanobut.logger.*
import com.umeng.commonsdk.UMConfigure
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.core.context.startKoin
import org.koin.dsl.module


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class NinjaApp : BaseApplication() {
    private val mutex = Mutex()
    lateinit var account: Account

    companion object {
        lateinit var instance: NinjaApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initLogger()
        initKoin()
        initPush()
        initImagePicker()
        UMConfigure.setLogEnabled(true)
        UMConfigure.init(this, "609ce659c9aacd3bd4d3f092", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "")
        LQREmotionKit.init(this) { _, path, imageView -> imageView.load(path) }
    }

    private fun initPush() {
        PushHelper.preInit(this)
        if (PushHelper.isMainProcess(this)) {
            //建议在线程中执行初始化
            PushHelper.init(applicationContext)
        }
    }

    private fun initLogger() {
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return true
            }
        })
    }

    private fun initKoin() {

        val appModule = module {

            viewModel { SplashViewModel() }
            viewModel { CreateAccountViewModel() }
            viewModel { EditUserInfoViewModel() }
            viewModel { MainViewModel() }
            viewModel { ContactListViewModel() }
            viewModel { ConversationListViewModel() }
            viewModel { MyViewModel() }
            viewModel { ApplyListViewModel() }
            viewModel { SearchContactViewModel() }
            viewModel { ScanViewModel() }
            viewModel { ShowIDQRCodeViewModel() }
            viewModel { UnLockViewModel() }
            viewModel { EditContactViewModel() }
            viewModel { ContactDetailViewModel() }
            viewModel { AccountManagerViewModel() }
            viewModel { ConversationViewModel() }
            viewModel { TakePhotoViewModel() }
            viewModel { LocationViewModel() }
            viewModel { ShowBigImageViewModel() }
            viewModel { LocationSearchViewModel() }
            viewModel { LocationShowViewModel() }
            viewModel { ScanContactSuccessViewModel() }

            single { UnlockModel() }
            single { CreateAccountModel() }
            single { ConversationModel() }
        }

        startKoin {
            androidLogger()
            androidContext(this@NinjaApp)
            modules(appModule)
        }

    }

    fun configApp() {
        Androidlib.configApp("", object : AppCallBack {
            override fun imageMessage(from: String, to: String, payload: ByteArray, time: Long) {
                println("-----------------------------图片------------------------------")
                MainScope().launch {
                    val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_image), time)
                    val message = Message(0, conversation.id, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.IMAGE, msg = "[图片]")
                    message.uri = FileUtils.saveImageToPath(Constants.PHOTO_SAVE_DIR, payload)
                    insertMessage(message, conversation)

                }
            }

            override fun locationMessage(from: String, to: String, lng: Float, lat: Float, locationAddress: String, time: Long) {
                println("-----------------------------位置------------------------------")
                MainScope().launch {
                    val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_location), time)
                    val message = Message(0, conversation.id, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.LOCATION, lat = lat, lng = lng, locationAddress = locationAddress, msg = "[定位]")
                    insertMessage(message, conversation)
                }

            }

            override fun textMessage(from: String, to: String, data: String, time: Long) {
                println("-----------------------------${data}------------------------------")
                MainScope().launch {
                    val conversation = insertOrUpdateConversation(from, data, time)
                    val message = Message(0, conversation.id, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.TEXT, msg = data)
                    insertMessage(message, conversation)
                }

            }

            override fun voiceMessage(from: String, to: String, payload: ByteArray, length: Long, time: Long) {
                println("-----------------------------语音${length}------------------------------")
                MainScope().launch {
                    val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_voice), time)
                    val message = Message(0, conversation.id, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.VOICE, msg = "[语音]", duration = length.toInt())
                    message.uri = FileUtils.saveVoiceToPath(Constants.AUDIO_SAVE_DIR, payload)
                    insertMessage(message, conversation)
                }
            }


            override fun webSocketClosed() {
                Logger.d("webSocketClosed")
                EventBus.getDefault().post(EventOffline())
            }
        })
    }

    private suspend fun insertMessage(message: Message, conversation: Conversation) {
        MessageDBManager.insert(message)
        updateConversationUnreadCount(conversation)
    }


    private fun initImagePicker() {


    }

    private suspend fun insertOrUpdateConversation(from: String, msg: String, time: Long): Conversation {
        mutex.withLock {
            var conversation = ConversationDBManager.queryByFrom(from)
            if (conversation == null) {
                conversation = Conversation(0, from, msg, time, 1)
                val nickName = ContactDBManager.queryNickNameByUID(from)
                conversation.nickName = if (TextUtils.isEmpty(nickName)) from else nickName!!
                conversation.id = ConversationDBManager.insert(conversation)
            } else {
                conversation.msg = msg
                conversation.time = time
                val nickName = ContactDBManager.queryNickNameByUID(from)
                conversation.nickName = if (TextUtils.isEmpty(nickName)) from else nickName!!
                ConversationDBManager.updateConversations(conversation)
            }
            return conversation
        }


    }

    suspend fun updateConversationUnreadCount(conversation: Conversation) {
        val unreadCount = MessageDBManager.queryUnReadCount(conversation.id)
        conversation.unreadCount = unreadCount
        ConversationDBManager.updateConversations(conversation)
    }
}