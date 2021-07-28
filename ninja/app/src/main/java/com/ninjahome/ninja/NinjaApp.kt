package com.ninjahome.ninja

import android.text.TextUtils
import androidlib.Androidlib
import androidlib.MulticastCallBack
import androidlib.UnicastCallBack
import coil.load
import com.lqr.emoji.LQREmotionKit
import com.ninja.android.lib.base.BaseApplication
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.event.*
import com.ninjahome.ninja.imageloader.GlideImageLoader
import com.ninjahome.ninja.imageloader.ImageLoaderProxy
import com.ninjahome.ninja.model.ConversationModel
import com.ninjahome.ninja.model.CreateAccountModel
import com.ninjahome.ninja.model.UnlockModel
import com.ninjahome.ninja.model.bean.*
import com.ninjahome.ninja.push.PushHelper
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.room.ConversationDBManager
import com.ninjahome.ninja.room.GroupDBManager
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
class NinjaApp : BaseApplication(), UnicastCallBack {
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
        ImageLoaderProxy.initLoader(GlideImageLoader())
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
            viewModel { CreateGroupChatViewModel() }
            viewModel { GroupChatDetailViewModel() }
            viewModel { GroupChatRemoveMemberViewModel() }

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
        Androidlib.configApp("", this, object : MulticastCallBack {
            override fun createGroup(groupId: String, groupName: String, owner: String, memberIdList: String, memberNickNameList: String) {
                println("-----------------createGroup-----groupId:${groupId}   groupName: ${groupName}   memberIdList:${memberIdList}   memberNickNameList:${memberNickNameList} ")
                Logger.d("-----------------createGroup-----groupId:${groupId}   groupName: ${groupName}   memberIdList:${memberIdList}   memberNickNameList:${memberNickNameList} ")
                MainScope().launch {
                    groupId?.let {
                        val group = GroupDBManager.queryByGroupId(it)
                        if (group == null) {
                            val groupConversation = GroupChat(0, groupId, groupName, owner, memberIdList, memberNickNameList)
                            GroupDBManager.insert(groupConversation)
                        }
                    }

                }
            }

            override fun dismisGroup(groupId: String?) {
            }

            override fun quitGroup(from: String?, groupId: String?, quitId: String?) {

            }

            override fun syncGroup(groupId: String?): String {
                return ""
            }

            override fun syncGroupAck(groupId: String?, groupName: String?, owner: String?, memberIdList: String?, memberNickNameList: String?) {

            }

            override fun joinGroup(from: String?, groupId: String?, groupName: String?, owner: String?, memberIdList: String?, memberNickNameList: String?, newIdList: String?) {
            }

            override fun kickOutUser(from: String?, groupId: String?, kickId: String?) {
            }


            override fun textMessage(from: String, groupId: String, payload: String, time: Long) {
                MainScope().launch {
                        val conversation = insertOrUpdateConversation(groupId, payload, time, true)
                        val message = Message(0, conversation.id, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.TEXT, msg = payload)
                        insertMessage(message, conversation)
                }
            }


            override fun imageMessage(from: String, groupId: String, payload: ByteArray, time: Long) {
                MainScope().launch {
                    val conversation = insertOrUpdateConversation(groupId, context().getString(R.string.message_type_image), time,true)
                    val message = Message(0, conversation.id, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.IMAGE, msg = context().getString(R.string.message_type_image))
                    message.uri = FileUtils.saveImageToPath(Constants.PHOTO_SAVE_DIR, payload)
                    insertMessage(message, conversation)

                }
            }

            override fun voiceMessage(from: String, groupId: String, payload: ByteArray, length: Long, time: Long) {
                MainScope().launch {
                    val conversation = insertOrUpdateConversation(groupId, context().getString(R.string.message_type_voice), time,true)
                    val message = Message(0, conversation.id, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.VOICE, msg =context().getString(R.string.message_type_voice), duration = length.toInt())
                    message.uri = FileUtils.saveVoiceToPath(Constants.AUDIO_SAVE_DIR, payload)
                    insertMessage(message, conversation)
                }
            }

            override fun locationMessage(from: String, groupId: String, lng: Float, lat: Float, name: String, time: Long) {
                MainScope().launch {
                    val conversation = insertOrUpdateConversation(groupId, context().getString(R.string.message_type_location), time, true)
                    val message = Message(0, conversation.id, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.LOCATION, lat = lat, lng = lng, locationAddress = name, msg = context().getString(R.string.message_type_location))
                    insertMessage(message, conversation)
                }

            }


        })
    }

    private suspend fun insertMessage(message: Message, conversation: Conversation) {
        MessageDBManager.insert(message)
        updateConversationUnreadCount(conversation)
    }


    private fun initImagePicker() {


    }

    private suspend fun insertOrUpdateConversation(from: String, msg: String, time: Long, isGroup: Boolean): Conversation {
        mutex.withLock {
            var conversation = ConversationDBManager.queryByFrom(from)
            if (conversation == null) {
                conversation = Conversation(0, from, isGroup, msg, time * 1000, 1)
                if (isGroup) {
                    val group = GroupDBManager.queryByGroupId(from)
                    group?.let {
                        conversation.title = it.groupName
                    }

                } else {
                    val nickName = ContactDBManager.queryNickNameByUID(from)
                    conversation.title = if (TextUtils.isEmpty(nickName)) from else nickName!!
                }

                conversation.id = ConversationDBManager.insert(conversation)
            } else {
                conversation.msg = msg
                conversation.time = time * 1000
                if (isGroup) {
                    val group = GroupDBManager.queryByGroupId(from)
                    group?.let {
                        conversation.title = it.groupName
                    }

                } else {
                    val nickName = ContactDBManager.queryNickNameByUID(from)
                    conversation.title = if (TextUtils.isEmpty(nickName)) from else nickName!!
                }
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


    override fun locationMessage(from: String, to: String, lng: Float, lat: Float, locationAddress: String, time: Long) {
        println("-----------------------------位置------------------------------")
        MainScope().launch {
            val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_location), time, false)
            val message = Message(0, conversation.id, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.LOCATION, lat = lat, lng = lng, locationAddress = locationAddress, msg = context().getString(R.string.message_type_location))
            insertMessage(message, conversation)
        }

    }

    override fun imageMessage(from: String, to: String, payload: ByteArray, time: Long) {
        println("-----------------------------图片------------------------------")
        MainScope().launch {
            val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_image), time,false)
            val message = Message(0, conversation.id, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.IMAGE, msg = context().getString(R.string.message_type_image))
            message.uri = FileUtils.saveImageToPath(Constants.PHOTO_SAVE_DIR, payload)
            insertMessage(message, conversation)

        }
    }


    override fun textMessage(from: String, to: String, data: String, time: Long) {
        println("-----------------------------${data}------------------------------")
        MainScope().launch {
            val conversation = insertOrUpdateConversation(from, data, time,false)
            val message = Message(0, conversation.id, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.TEXT, msg = data)
            insertMessage(message, conversation)
        }

    }

    override fun voiceMessage(from: String, to: String, payload: ByteArray, length: Long, time: Long) {
        println("-----------------------------语音${length}------------------------------")
        MainScope().launch {
            val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_voice), time,false)
            val message = Message(0, conversation.id, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.VOICE, msg =context().getString(R.string.message_type_voice), duration = length.toInt())
            message.uri = FileUtils.saveVoiceToPath(Constants.AUDIO_SAVE_DIR, payload)
            insertMessage(message, conversation)
        }
    }


    override fun webSocketClosed() {
        Logger.d("webSocketClosed")
        EventBus.getDefault().post(EventOffline())
    }
}