package com.ninjahome.ninja

import android.app.Activity
import android.net.Uri
import android.widget.ImageView
import androidlib.Androidlib
import androidlib.AppCallBack
import coil.load
import com.lqr.emoji.LQREmotionKit
import com.lqr.imagepicker.ImagePicker
import com.lqr.imagepicker.loader.ImageLoader
import com.lqr.imagepicker.view.CropImageView
import com.ninja.android.lib.base.BaseApplication
import com.ninjahome.ninja.event.*
import com.ninjahome.ninja.model.ConversationModel
import com.ninjahome.ninja.model.CreateAccountModel
import com.ninjahome.ninja.model.UnlockModel
import com.ninjahome.ninja.model.bean.*
import com.ninjahome.ninja.push.PushHelper
import com.ninjahome.ninja.utils.FileUtils
import com.ninjahome.ninja.viewmodel.*
import com.orhanobut.logger.*
import com.umeng.commonsdk.UMConfigure
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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
        initPush()
        initImagePicker()
        UMConfigure.setLogEnabled(true)
        UMConfigure.init(this, "609ce659c9aacd3bd4d3f092", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "")
        LQREmotionKit.init(this) { context, path, imageView -> imageView.load(path) }
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
            viewModel { AddContactViewModel() }
            viewModel { ContactDetailViewModel() }
            viewModel { AccountManagerViewModel() }
            viewModel { ConversationViewModel() }
            viewModel { TakePhotoViewModel() }
            viewModel { LocationViewModel() }
            viewModel { ShowBigImageViewModel() }
            viewModel { LocationSearchViewModel() }
            viewModel { LocationShowViewModel() }

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
                MainScope().launch {
                    val imageMessage = ImageMessage(Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, String(payload))
                    imageMessage.localUri = FileUtils.saveImageToPath(Constants.PHOTO_SAVE_DIR, payload)
                    EventBus.getDefault().postSticky(EventReceiveImageMessage(from, to, imageMessage))
                }
            }

            override fun locationMessage(from: String, to: String, lng: Float, lat: Float, name: String, time: Long) {
                val locationMessage = LocationMessage(Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, lat, lng, name)
                EventBus.getDefault().postSticky(EventReceiveLocationMessage(from, to, locationMessage))
            }

            override fun textMessage(from: String, to: String, data: String, time: Long) {
                val textMessage = TextMessage(Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, data)
                EventBus.getDefault().postSticky(EventReceiveTextMessage(from, to, textMessage))
            }

            override fun voiceMessage(from: String, to: String, payload: ByteArray, length: Long, time: Long) {
                MainScope().launch {
                    val voiceMessage = VoiceMessage(Message.MessageDirection.RECEIVE, Message.SentStatus.RECEIVED, time * 1000, "", length)
                    voiceMessage.localUrl = FileUtils.saveVoiceToPath(Constants.AUDIO_SAVE_DIR, payload)
                    EventBus.getDefault().postSticky(EventReceiveVoiceMessage(from, to, voiceMessage))
                }
            }


            override fun webSocketClosed() {
                Logger.d("webSocketClosed")
                EventBus.getDefault().post(EventOffline())
            }

        })
    }


    private fun initImagePicker() {
        val imagePicker = ImagePicker.getInstance()
        imagePicker.imageLoader = object : ImageLoader {
            override fun displayImage(activity: Activity, path: String, imageView: ImageView, width: Int, height: Int) {
                imageView.load(Uri.parse("file://$path").toString())
            }

            override fun clearMemoryCache() {}
        } //设置图片加载器
        imagePicker.isShowCamera = true //显示拍照按钮
        imagePicker.isCrop = true //允许裁剪（单选才有效）
        imagePicker.isSaveRectangle = true //是否按矩形区域保存
        imagePicker.selectLimit = 9 //选中数量限制
        imagePicker.style = CropImageView.Style.RECTANGLE //裁剪框的形状
        imagePicker.focusWidth = 800 //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.focusHeight = 800 //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.outPutX = 1000 //保存文件的宽度。单位像素
        imagePicker.outPutY = 1000 //保存文件的高度。单位像素
    }
}