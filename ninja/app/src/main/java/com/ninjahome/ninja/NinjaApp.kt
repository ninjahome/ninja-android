package com.ninjahome.ninja

import android.text.TextUtils
import chatLib.MulticastCallBack
import chatLib.UnicastCallBack
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
import com.ninjahome.ninja.utils.MoshiUtils
import com.ninjahome.ninja.utils.toJson
import com.ninjahome.ninja.viewmodel.*
import com.orhanobut.logger.*
import com.umeng.commonsdk.UMConfigure
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
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
            viewModel { GroupChatAddMemberViewModel() }

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
        chatLib.ChatLib.configApp("", this, object : MulticastCallBack {
            override fun banTalking(groupId: String?) {
                Logger.d("-----------------banTalking-----groupId:${groupId}")
            }

            override fun createGroup(groupId: String, groupName: String, owner: String, memberIdList: String, memberNickNameList: String) {
                Logger.d("-----------------createGroup-----groupId:${groupId}   groupName: ${groupName}   memberIdList:${memberIdList}   memberNickNameList:${memberNickNameList} ")
                MainScope().launch {
                    val group = GroupDBManager.queryByGroupId(groupId)
                    if (group == null) {
                        val groupConversation = GroupChat(0, groupId, groupName, owner, memberIdList, memberNickNameList)
                        GroupDBManager.insert(groupConversation)
                    }
                }
            }

            override fun dismisGroup(groupId: String?) {
                Logger.d("-----------------dismisGroup-----groupId:${groupId}  ---------")
            }

            override fun fileMessage(p0: String, p1: String, p2: ByteArray, p3: Long, p4: String?) {
            }

            override fun quitGroup(from: String, groupId: String, quitId: String) {
                Logger.d("-----------------quitGroup-----from:${from}   groupId:${groupId}   quitId:${quitId}---------")
                MainScope().launch {
                    val group = GroupDBManager.queryByGroupId(groupId)
                    if (group != null) {
                        val newIds = ArrayList<String>()
                        val newNicKName = ArrayList<String>()
                        val oldIdList = MoshiUtils.listFromJson<String>(group.memberIdList)
                        val oldNickNameList = MoshiUtils.listFromJson<String>(group.memberNickNameList)
                        for (i  in 0 until oldIdList.size) {
                            if(!oldIdList[i].equals(quitId)){
                                newIds.add(oldIdList[i])
                                newNicKName.add(oldNickNameList[i])
                            }
                        }

                        group.memberIdList = newIds.toJson()
                        group.memberNickNameList = newNicKName.toJson()
                        GroupDBManager.updateGroup(group)
                    }
                }
            }

            override fun syncGroup(groupId: String?): String {
                Logger.d("-----------------syncGroup-----   groupId:${groupId}---------")
                return ""
            }

            override fun syncGroupAck(groupId: String?, groupName: String?, owner: String?, p3: Boolean, memberIdList: String?, memberNickNameList: String?) {
                Logger.d("-----------------syncGroupAck-----   groupId:${groupId}---------")
            }

            override fun joinGroup(from: String?, groupId: String, groupName: String?, owner: String?, memberIdList: String, memberNickNameList: String, newIdList: String, banTalkding: Boolean) {
                Logger.d("-----------------joinGroup-----from:${from}   groupId:${groupId}    groupName:${groupName}  owner:${owner}  memberIdList:${memberIdList}   memberNickNameList:${memberNickNameList}    newIdList:${newIdList} ---------")
                MainScope().launch {
                    val group = GroupDBManager.queryByGroupId(groupId)
                    if (group != null) {
                        group.memberIdList = memberIdList
                        group.memberNickNameList = memberNickNameList
                        GroupDBManager.updateGroup(group)
                    }
                }
            }

            override fun kickOutUser(from: String, groupId: String, kickIds: String) {
                Logger.d("-----------------kickOutUser-----from:${from}   groupId:${groupId}   kickId:${kickIds} ")
                MainScope().launch {
                    val group = GroupDBManager.queryByGroupId(groupId)
                    if (group != null) {
                        val newIds = ArrayList<String>()
                        val newNicKName = ArrayList<String>()
                        val kickIdList = MoshiUtils.listFromJson<String>(kickIds)
                        val oldIdList = MoshiUtils.listFromJson<String>(group.memberIdList)
                        val oldNickNameList = MoshiUtils.listFromJson<String>(group.memberNickNameList)

                        for (i in 0 until oldIdList.size) {
                            if (!kickIdList.contains(oldIdList[i])) {
                                newIds.add(oldIdList[i])
                                newNicKName.add(oldNickNameList[i])
                            }
                        }
                        kickIdList.forEach {
                            if (!oldIdList.contains(it)) {
                                newIds.add(it)
                            }
                        }
                        group.memberIdList = newIds.toJson()
                        group.memberNickNameList = newNicKName.toJson()
                        GroupDBManager.updateGroup(group)
                    }
                }
            }


            override fun textMessage(from: String, groupId: String, payload: String, time: Long) {
                MainScope().launch {
                    val conversation = insertOrUpdateConversation(from, payload, time, true, groupId)
                    val message = Message(0, conversation.id, from, instance.account.address, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.TEXT, msg = payload)
                    insertMessage(message, conversation)
                }
            }


            override fun imageMessage(from: String, groupId: String, payload: ByteArray, time: Long) {
                MainScope().launch {
                    val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_image), time, true, groupId)
                    val message = Message(0, conversation.id, from, instance.account.address, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.IMAGE, msg = context().getString(R.string.message_type_image))
                    message.uri = FileUtils.saveImageToPath(Constants.PHOTO_SAVE_DIR, payload)
                    insertMessage(message, conversation)

                }
            }

            override fun voiceMessage(from: String, groupId: String, payload: ByteArray, length: Long, time: Long) {
                MainScope().launch {
                    val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_voice), time, true, groupId)
                    val message = Message(0, conversation.id, from, account.address, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.VOICE, msg = context().getString(R.string.message_type_voice), duration = length.toInt())
                    message.uri = FileUtils.saveVoiceToPath(Constants.AUDIO_SAVE_DIR, payload)
                    insertMessage(message, conversation)
                }
            }

            override fun locationMessage(from: String, groupId: String, lng: Float, lat: Float, name: String, time: Long) {
                MainScope().launch {
                    val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_location), time, true, groupId)
                    val message = Message(0, conversation.id, from, account.address, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.LOCATION, lat = lat, lng = lng, locationAddress = name, msg = context().getString(R.string.message_type_location))
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

    private suspend fun insertOrUpdateConversation(from: String, msg: String, time: Long, isGroup: Boolean, groupId: String = ""): Conversation {
        mutex.withLock {
            var conversation: Conversation?
            var nickName: String? = from
            if (isGroup) {
                conversation = ConversationDBManager.queryByGroupId(groupId)
                val group = GroupDBManager.queryByGroupId(groupId)
                group?.let {
                    val memberIds = JSONArray(it.memberIdList)
                    val memberNames = JSONArray(it.memberNickNameList)
                    for (index in 0 until memberIds.length()) {
                        if (memberIds[index].equals(from)) {
                            nickName = memberNames[index] as String
                        }
                    }
                }
            } else {
                conversation = ConversationDBManager.queryByFrom(from)
            }
            val contactNickName = ContactDBManager.queryNickNameByUID(from)
            contactNickName?.let {
                nickName = it
            }

            if (conversation == null) {
                conversation = Conversation(0, from, isGroup, msg, time * 1000, 1, groupId = groupId)
                if (isGroup) {
                    val group = GroupDBManager.queryByGroupId(groupId)
                    group?.let { conversation.title = it.groupName }
                    val msg = if (TextUtils.isEmpty(nickName)) from else "${nickName!!}:$msg"
                    conversation.msg = msg

                } else {
                    conversation?.title = if (TextUtils.isEmpty(nickName)) from else nickName!!
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
            val message = Message(0, conversation.id, from, to, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.LOCATION, lat = lat, lng = lng, locationAddress = locationAddress, msg = context().getString(R.string.message_type_location))
            insertMessage(message, conversation)
        }

    }

    override fun fileMessage(from: String?, to: String?, payload: ByteArray?, size: Long, name: String?) {
    }

    override fun imageMessage(from: String, to: String, payload: ByteArray, time: Long) {
        println("-----------------------------图片------------------------------")
        MainScope().launch {
            val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_image), time, false)
            val message = Message(0, conversation.id, from, to, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.IMAGE, msg = context().getString(R.string.message_type_image))
            message.uri = FileUtils.saveImageToPath(Constants.PHOTO_SAVE_DIR, payload)
            insertMessage(message, conversation)

        }
    }


    override fun textMessage(from: String, to: String, data: String, time: Long) {
        println("-----------------------------${data}------------------------------")
        MainScope().launch {
            val conversation = insertOrUpdateConversation(from, data, time, false)
            val message = Message(0, conversation.id, from, to, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.TEXT, msg = data)
            insertMessage(message, conversation)
        }

    }

    override fun voiceMessage(from: String, to: String, payload: ByteArray, length: Long, time: Long) {
        println("-----------------------------语音${length}------------------------------")
        MainScope().launch {
            val conversation = insertOrUpdateConversation(from, context().getString(R.string.message_type_voice), time, false)
            val message = Message(0, conversation.id, from, to, Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, Message.Type.VOICE, msg = context().getString(R.string.message_type_voice), duration = length.toInt())
            message.uri = FileUtils.saveVoiceToPath(Constants.AUDIO_SAVE_DIR, payload)
            insertMessage(message, conversation)
        }
    }


    override fun webSocketClosed() {
        Logger.d("webSocketClosed")
        EventBus.getDefault().post(EventOffline())
    }
}