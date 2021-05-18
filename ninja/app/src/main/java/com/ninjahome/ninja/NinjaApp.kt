package com.ninjahome.ninja

import androidlib.Androidlib
import androidlib.AppCallBack
import com.ninja.android.lib.base.BaseApplication
import com.ninjahome.ninja.event.EventReceiveConversation
import com.ninjahome.ninja.event.EventUnReadMessages
import com.ninjahome.ninja.model.CreateAccountModel
import com.ninjahome.ninja.model.UnlockModel
import com.ninjahome.ninja.model.bean.Account
import com.ninjahome.ninja.model.bean.Conversation
import com.ninjahome.ninja.model.bean.TextData
import com.ninjahome.ninja.utils.ChatMessageFactory
import com.ninjahome.ninja.utils.JsonUtils
import com.ninjahome.ninja.viewmodel.*
import com.orhanobut.logger.*
import com.umeng.commonsdk.UMConfigure
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
    lateinit var account: Account
    val conversations = hashMapOf<String, Conversation>()

    companion object {
        lateinit var instance: NinjaApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initLogger()
        initKoin()
        UMConfigure.setLogEnabled(true)
        UMConfigure.init(this, "609ce659c9aacd3bd4d3f092", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
    }

    private fun initLogger() {
        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
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
            viewModel { ChatViewModel() }
            viewModel { ShowIDQRCodeViewModel() }
            viewModel { UnLockViewModel() }
            viewModel { AddContactViewModel() }
            viewModel { ContactDetailViewModel() }
            viewModel { AccountManagerViewModel() }

            single { UnlockModel() }
            single { CreateAccountModel() }
        }

        startKoin {
            androidLogger()
            androidContext(this@NinjaApp)
            modules(appModule)
        }

    }

    fun configApp() {
        Androidlib.configApp("", object : AppCallBack {
            override fun immediateMessage(from: String, to: String, payload: ByteArray, time: Long) {
                Logger.d(from)

                Logger.d(to)

                Logger.d(String(payload))

                val textData = JsonUtils.json2Object(String(payload), TextData::class.java)
                val chatMessage = ChatMessageFactory.createChatMessage(from, from, "https://img0.baidu.com/it/u=1950977217,4151841346&fm=26&fmt=auto&gp=0.jpg", true, textData!!.data)
                EventBus.getDefault().postSticky(EventReceiveConversation(from, to, chatMessage))

                Logger.d(time)
            }

            override fun unreadMsg(data: ByteArray?) {
                data?.let {
                    Logger.d(String(it))
                    EventBus.getDefault().postSticky(EventUnReadMessages(String(it)))

                }


            }

            override fun webSocketClosed() {
                Logger.d("webSocketClosed")
            }

        })
    }
}