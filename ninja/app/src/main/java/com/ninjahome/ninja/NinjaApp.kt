package com.ninjahome.ninja

import chatLib.MulticastCallBack
import chatLib.UnicastCallBack
import coil.load
import com.lqr.emoji.LQREmotionKit
import com.ninja.android.lib.base.BaseApplication
import com.ninjahome.ninja.event.*
import com.ninjahome.ninja.imageloader.GlideImageLoader
import com.ninjahome.ninja.imageloader.ImageLoaderProxy
import com.ninjahome.ninja.model.ConversationModel
import com.ninjahome.ninja.model.CreateAccountModel
import com.ninjahome.ninja.model.UnlockModel
import com.ninjahome.ninja.model.bean.*
import com.ninjahome.ninja.push.PushHelper
import com.ninjahome.ninja.db.GroupDBManager
import com.ninjahome.ninja.utils.*
import com.ninjahome.ninja.viewmodel.*
import com.orhanobut.logger.*
import com.rxlife.coroutine.RxLifeScope
import com.umeng.commonsdk.UMConfigure
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class NinjaApp : BaseApplication(), UnicastCallBack {
    val rxLifeScope = RxLifeScope()
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
            viewModel { CreateAccountViewModel(get()) }
            viewModel { EditUserInfoViewModel() }
            viewModel { MainViewModel() }
            viewModel { ContactListViewModel() }
            viewModel { ConversationListViewModel() }
            viewModel { MyViewModel(get()) }
            viewModel { ApplyListViewModel() }
            viewModel { SearchContactViewModel() }
            viewModel { ScanViewModel() }
            viewModel { ShowIDQRCodeViewModel() }
            viewModel { UnLockViewModel(get()) }
            viewModel { EditContactViewModel() }
            viewModel { ContactDetailViewModel() }
            viewModel { AccountManagerViewModel() }
            viewModel { ConversationViewModel(get()) }
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
            viewModel { ActivationViewModel() }

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

            }

            override fun createGroup(groupId: String, groupName: String, owner: String, memberIdList: String, memberNickNameList: String) {
                this@NinjaApp.createGroup(groupId, groupName, owner, memberIdList, memberNickNameList)

            }

            override fun dismisGroup(groupId: String) {
                rxLifeScope.launch {
                    val groupChat = GroupDBManager.queryByGroupId(groupId)
                    if (groupChat != null) {
                        GroupDBManager.delete(groupChat)
                    }
                }
            }

            override fun fileMessage(p0: String, p1: String, p2: ByteArray, p3: Long, p4: String?) {
            }

            override fun quitGroup(from: String, groupId: String, quitId: String) {
                rxLifeScope.launch {
                    val group = GroupDBManager.queryByGroupId(groupId)
                    if (group != null) {
                        val newIds = ArrayList<String>()
                        val newNicKName = ArrayList<String>()
                        val oldIdList = MoshiUtils.listFromJson<String>(group.memberIdList)
                        val oldNickNameList = MoshiUtils.listFromJson<String>(group.memberNickNameList)
                        for (i in 0 until oldIdList.size) {
                            if (oldIdList[i] != quitId) {
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

            override fun syncGroup(groupId: String): String {
                var syncGroup: SyncGroup? = null
                rxLifeScope.launch {
                    val groupInfo = GroupDBManager.queryByGroupId(groupId)
                    if (groupInfo != null) {
                        syncGroup = SyncGroup(groupInfo.groupId, groupInfo.groupName, groupInfo.owner, groupInfo.banTalking, MoshiUtils.listFromJson(groupInfo.memberIdList), MoshiUtils.listFromJson(groupInfo.memberNickNameList))
                    }
                }
                return if (syncGroup == null) {
                    ""
                } else {
                    syncGroup!!.toJson()
                }
            }

            override fun syncGroupAck(groupId: String, groupName: String, owner: String, banTalking: Boolean, memberIdList: String, memberNickNameList: String) {
                this@NinjaApp.createGroup(groupId, groupName, owner, memberIdList, memberNickNameList)
            }

            override fun joinGroup(from: String?, groupId: String, groupName: String?, owner: String?, memberIdList: String, memberNickNameList: String, newIdList: String, banTalkding: Boolean) {
                rxLifeScope.launch {
                    val groupInfo = GroupDBManager.queryByGroupId(groupId)
                    if (groupInfo != null) {
                        groupInfo.memberIdList = memberIdList
                        groupInfo.memberNickNameList = memberNickNameList
                        GroupDBManager.updateGroup(groupInfo)
                    }
                }
            }

            override fun kickOutUser(from: String, groupId: String, kickIds: String) {
                rxLifeScope.launch {
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
                ConversationManager.receiveGroupTextMessage(from, groupId, payload, time)
            }


            override fun imageMessage(from: String, groupId: String, payload: ByteArray, time: Long) {
                ConversationManager.receiveGroupImageMessage(from, groupId, payload, time)
            }

            override fun voiceMessage(from: String, groupId: String, payload: ByteArray, length: Long, time: Long) {
                ConversationManager.receiveGroupVoiceMessage(from, groupId, payload, length, time)
            }

            override fun locationMessage(from: String, groupId: String, lng: Float, lat: Float, name: String, time: Long) {
                ConversationManager.receiverGroupLocationMessage(from, groupId, lng, lat, name, time)

            }


        })
    }

    private fun createGroup(groupId: String, groupName: String, owner: String, memberIdList: String, memberNickNameList: String) {
        rxLifeScope.launch {
            val group = GroupDBManager.queryByGroupId(groupId)
            if (group == null) {
                val groupConversation = GroupInfo(0, groupId, groupName, owner, memberIdList, memberNickNameList)
                GroupDBManager.insert(groupConversation)
            }
        }
    }


    override fun locationMessage(from: String, to: String, lng: Float, lat: Float, locationAddress: String, time: Long) {
        ConversationManager.receiverLocationMessage(from, to, lng, lat, locationAddress, time)
    }

    override fun fileMessage(from: String?, to: String?, payload: ByteArray?, size: Long, name: String?) {
    }

    override fun imageMessage(from: String, to: String, payload: ByteArray, time: Long) {
        ConversationManager.receiveImageMessage(from, to, payload, time)
    }


    override fun textMessage(from: String, to: String, data: String, time: Long) {
        ConversationManager.receiveTextMessage(from, to, data, time)
    }

    override fun voiceMessage(from: String, to: String, payload: ByteArray, length: Long, time: Long) {
        ConversationManager.receiveVoiceMessage(from, to, payload, length, time)
    }


    override fun webSocketClosed() {
        Logger.d("webSocketClosed")
        EventBus.getDefault().post(EventOffline())
    }

}