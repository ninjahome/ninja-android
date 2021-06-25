package com.ninjahome.ninja.ui.activity.conversation

import android.Manifest
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidlib.Androidlib.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.rxLifeScope
import cn.bingoogolapple.refreshlayout.BGARefreshLayout
import com.gyf.immersionbar.ImmersionBar
import com.lqr.audio.AudioPlayManager
import com.lqr.audio.AudioRecordManager
import com.lqr.audio.IAudioPlayListener
import com.lqr.audio.IAudioRecordListener
import com.lqr.emoji.EmotionKeyboard
import com.lqr.emoji.IEmotionSelectedListener
import com.lqr.emoji.LQREmotionKit
import com.lqr.emoji.MoonUtils
import com.lqr.imagepicker.ImagePicker
import com.lqr.imagepicker.bean.ImageItem
import com.lqr.imagepicker.ui.ImageGridActivity
import com.lxj.xpopup.core.BasePopupView
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.dp
import com.ninjahome.ninja.*
import com.ninjahome.ninja.databinding.ActivityConversationBinding
import com.ninjahome.ninja.event.*
import com.ninjahome.ninja.model.bean.*
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.ui.adapter.ConversationAdapter
import com.ninjahome.ninja.utils.DialogUtils
import com.ninjahome.ninja.view.ConversationMoreActionPop
import com.ninjahome.ninja.viewmodel.ConversationViewModel
import kotlinx.android.synthetic.main.activity_conversation.*
import me.xfans.lib.voicewaveview.VoiceWaveView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.*


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class ConversationActivity : BaseActivity<ConversationViewModel, ActivityConversationBinding>(R.layout.activity_conversation), BGARefreshLayout.BGARefreshLayoutDelegate, EasyPermissions.PermissionCallbacks {

    private val REQUEST_IMAGE_PICKER = 1000
    private val REQUEST_TAKE_PHOTO = 1001
    private val REQUEST_LOCATION = 1002

    private lateinit var conversationAdapter: ConversationAdapter

    private var mIsFirst = false
    private lateinit var mEmotionKeyboard: EmotionKeyboard
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val mData: List<Message> = ArrayList()
    override val mViewModel: ConversationViewModel by viewModel()

    private val moreActionDialog: BasePopupView by lazy {
        DialogUtils.showMoreActionDialog(this, object : ConversationMoreActionPop.ConversationMoreActionListener {
            override fun action(index: Int) {
                when (index) {
                    ConversationMoreActionPop.ALBUM -> startAlbumActivity()
                    ConversationMoreActionPop.TAKE_PHOTO -> startTakePhotoActivity()
                    ConversationMoreActionPop.LOCATION -> startLocationActivity()
                }
                moreActionDialog.dismiss()
            }

        })
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        ImmersionBar.with(this).statusBarColor(com.ninja.android.lib.R.color.white).barEnable(true).keyboardEnable(true).statusBarDarkFont(true).fitsSystemWindows(true).init()
        initEmotionKeyboard()


    }


    override fun initData() {
        mViewModel.uid = intent.getStringExtra(IntentKey.UID)!!
        setTitle()
        conversationAdapter = ConversationAdapter(this, mData)
        rvMsg.adapter = conversationAdapter


        val conversation = NinjaApp.instance.conversations.get(mViewModel.uid)
        if (conversation?.messages != null && conversation.messages.size != 0) {
            conversation?.messages.forEach {
                conversationAdapter.addLastItem(it)
            }
            notifyAdapter()
        }

        conversationAdapter.setOnItemClickListener { helper, _, _, position ->
            val message = mData[position]
            if (message is ImageMessage) {
                val intent = Intent(this, ShowBigImageActivity::class.java)
                //                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, itemView, "share").toBundle()
                intent.putExtra(IntentKey.URL, message.localUri)
                startActivity(intent)
            } else if (message is VoiceMessage) {
                val ivAudio: ImageView = helper.getView(R.id.ivAudioL)

                AudioPlayManager.getInstance().startPlay(this@ConversationActivity, Uri.parse(message.localUrl), object : IAudioPlayListener {
                    override fun onStart(var1: Uri) {
                        if (ivAudio.background is AnimationDrawable) {
                            val animation = ivAudio.background as AnimationDrawable
                            animation.start()
                        }
                    }

                    override fun onStop(var1: Uri) {
                        if (ivAudio.background is AnimationDrawable) {
                            val animation = ivAudio.background as AnimationDrawable
                            animation.stop()
                            animation.selectDrawable(0)
                        }
                    }

                    override fun onComplete(var1: Uri) {
                        if (ivAudio.background is AnimationDrawable) {
                            val animation = ivAudio.background as AnimationDrawable
                            animation.stop()
                            animation.selectDrawable(0)
                        }
                    }
                })
            }else if (message is LocationMessage) {
                val intent = Intent(this@ConversationActivity, LocationShowActivity::class.java)
                intent.putExtra(IntentKey.LOCATION_LAT,message.lat)
                intent.putExtra(IntentKey.LOCATION_LNG,message.lng)
                intent.putExtra(IntentKey.LOCATION_ADDRESS,message.poi)
                startActivity(intent)
            }
        }
        initAudioRecordManager()
    }

    private fun setTitle() {
        rxLifeScope.launch {
            val title = ContactDBManager.queryNickNameByUID(mViewModel.uid)
            if (TextUtils.isEmpty(title)) {
                mViewModel.title.set(mViewModel.uid)
            } else {
                mViewModel.title.set(title)
            }
        }
    }

    override fun initObserve() {

        mViewModel.clickAudioEvent.observe(this) {
            if (btnAudio.isShown) {
                hideAudioButton()
                etContent.requestFocus()
                mEmotionKeyboard.showSoftInput()
            } else {
                if (!checkPermissions()) {
                    return@observe
                }
                etContent.clearFocus()
                showAudioButton()
                hideEmotionLayout()
                hideMoreLayout()
            }
            moveToBottom()
        }

        mViewModel.textChangeEvent.observe(this) {
            if (TextUtils.isEmpty(it)) {
                btnSend.visibility = View.GONE
                ivMore.visibility = View.VISIBLE
            } else {
                btnSend.visibility = View.VISIBLE
                ivMore.visibility = View.GONE
            }

        }
        mViewModel.clickSendEvent.observe(this) {
            conversationAdapter.addLastItem(TextMessage(Message.MessageDirection.SEND, Message.SentStatus.SENT, System.currentTimeMillis(), etContent.text.toString().trim()))
            mViewModel.sendText(etContent.text.toString().trim())
            moveToBottom()
            mViewModel.textData.value = ""
        }

        mViewModel.clickAlbumEvent.observe(this) {
            startAlbumActivity()
        }

        mViewModel.clickTakePhotoEvent.observe(this) {
            startTakePhotoActivity()
        }

        mViewModel.clickLocationEvent.observe(this) {
            startLocationActivity()
        }

        mViewModel.touchRecyclerEvent.observe(this) {
            closeBottomAndKeyboard()
        }

        mViewModel.touchAudioEvent.observe(this) {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> AudioRecordManager.getInstance(this@ConversationActivity).startRecord()
                MotionEvent.ACTION_MOVE -> if (isCancelled(btnAudio, it)) {
                    AudioRecordManager.getInstance(this@ConversationActivity).willCancelRecord()
                } else {
                    AudioRecordManager.getInstance(this@ConversationActivity).continueRecord()
                }
                MotionEvent.ACTION_UP -> {
                    AudioRecordManager.getInstance(this@ConversationActivity).stopRecord()
                    AudioRecordManager.getInstance(this@ConversationActivity).destroyRecord()
                }
            }
        }

        mViewModel.finishRefreshingEvent.observe(this) {
            swipeRefreshLayout.isRefreshing = false

        }
    }


    fun moveToBottom() {

        handler.postDelayed({
            rvMsg.smoothMoveToPosition((rvMsg.adapter?.itemCount ?: 1) - 1)
        }, 50)
    }

    fun checkPermissions(): Boolean {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_extra_write), Constants.RC_LOCAL_MEMORY_PERM, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
            return false
        }
        return true
    }

    private fun isCancelled(view: View, event: MotionEvent): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return event.rawX < location[0] || event.rawX > location[0] + view.width || event.rawY < location[1] - 40
    }

    private fun startLocationActivity() {
        val intent = Intent(this@ConversationActivity, LocationActivity::class.java)
        startActivityForResult(intent, REQUEST_LOCATION)
    }

    private fun startAlbumActivity() {
        val intent = Intent(this, ImageGridActivity::class.java)
        startActivityForResult(intent, REQUEST_IMAGE_PICKER)
    }

    private fun startTakePhotoActivity() {
        val intent = Intent(this, TakePhotoActivity::class.java)
        intent.putExtra(IntentKey.NAME,mViewModel.title.get())
        startActivityForResult(intent, REQUEST_TAKE_PHOTO)
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

    override fun onResume() {
        super.onResume()
        if (!mIsFirst) {
            etContent.clearFocus()
        } else {
            mIsFirst = false
        }
    }

    private fun initEmotionKeyboard() {
        mEmotionKeyboard = EmotionKeyboard.with(this)
        mEmotionKeyboard.bindToEditText(etContent)
        mEmotionKeyboard.bindToContent(llContent)
        mEmotionKeyboard.setEmotionLayout(flEmotionView)
        mEmotionKeyboard.bindToEmotionButton(ivEmo, ivMore)
        elEmotion.setEmotionSelectedListener(object : IEmotionSelectedListener {
            override fun onEmojiSelected(key: String) {
                if (etContent == null) return
                val editable: Editable = etContent.text
                if (key == "/DEL") {
                    etContent.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                } else {
                    var start: Int = etContent.selectionStart
                    var end: Int = etContent.selectionEnd
                    start = if (start < 0) 0 else start
                    end = if (start < 0) 0 else end
                    editable.replace(start, end, key)
                    val editEnd: Int = etContent.selectionEnd
                    MoonUtils.replaceEmoticons(LQREmotionKit.getContext(), editable, 0, editable.toString().length)
                    etContent.setSelection(editEnd)
                }
            }

            override fun onStickerSelected(categoryName: String, stickerName: String, stickerBitmapPath: String) {}
        })
        mEmotionKeyboard.setOnEmotionButtonOnClickListener { view: View ->
            when (view.id) {
                R.id.ivEmo -> {
                    handler.postDelayed({
                        rvMsg.smoothMoveToPosition((rvMsg.adapter?.itemCount ?: 0) - 1)
                    }, 50)
                    etContent.clearFocus()
                    if (!elEmotion.isShown) {
                        if (moreActionDialog.isShown) {
                            showEmotionLayout()
                            hideMoreLayout()
                            hideAudioButton()
                            return@setOnEmotionButtonOnClickListener true
                        }
                    } else if (elEmotion.isShown && !moreActionDialog.isShown) {
                        ivEmo.setImageResource(R.drawable.ic_cheat_emo)
                        return@setOnEmotionButtonOnClickListener false
                    }
                    showEmotionLayout()
                    hideMoreLayout()
                    hideAudioButton()
                    return@setOnEmotionButtonOnClickListener false
                }
                R.id.ivMore -> {
                    handler.postDelayed(({
                        rvMsg.smoothMoveToPosition((rvMsg.adapter?.itemCount ?: 0) - 1)
                    }), 50)
                    etContent.clearFocus()
                    flEmotionView.visibility = View.GONE
                    hideEmotionLayout()
                    hideAudioButton()
                    showMoreLayout()

                    return@setOnEmotionButtonOnClickListener true
                }
            }
            false
        }
    }


    private fun showAudioButton() {
        btnAudio.visibility = View.VISIBLE
        voiceLeftIv.visibility = View.VISIBLE
        voiceRightIv.visibility = View.VISIBLE
        etContent.visibility = View.GONE
        ivAudio.setImageResource(R.drawable.ic_cheat_keyboard)
        if (flEmotionView.isShown) {
            mEmotionKeyboard.interceptBackPress()
        } else {
            mEmotionKeyboard.hideSoftInput()
        }
    }

    private fun hideAudioButton() {
        btnAudio.visibility = View.GONE
        voiceLeftIv.visibility = View.GONE
        voiceRightIv.visibility = View.GONE
        etContent.visibility = View.VISIBLE
        ivAudio.setImageResource(R.drawable.ic_cheat_voice)
    }

    private fun showEmotionLayout() {
        elEmotion.visibility = View.VISIBLE
        ivEmo.setImageResource(R.drawable.ic_cheat_keyboard)
    }

    private fun hideEmotionLayout() {
        elEmotion.visibility = View.GONE
        ivEmo.setImageResource(R.drawable.ic_cheat_emo)
    }

    private fun showMoreLayout() {
        moreActionDialog.show()
    }

    private fun hideMoreLayout() {
        moreActionDialog.dismiss()
    }

    private fun closeBottomAndKeyboard() {
        elEmotion.visibility = View.GONE
        moreActionDialog.dismiss()
        mEmotionKeyboard.interceptBackPress()
        ivEmo.setImageResource(R.drawable.ic_cheat_emo)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_IMAGE_PICKER -> {
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) { //返回多张照片
                    if (data != null) {
                        //                        //是否发送原图
                        //                        val isOrig = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false)
                        val images = data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) as ArrayList<ImageItem>?
                        for (imageItem in images!!) {
                            mViewModel.sendImage(imageItem.path)
                            conversationAdapter.addLastItem(ImageMessage(Message.MessageDirection.SEND, Message.SentStatus.SENT, System.currentTimeMillis(), imageItem.path))
                        }
                    }
                }
                if (resultCode == RESULT_OK) {
                    val path = data!!.getStringExtra("path")
                    if (data.getBooleanExtra("take_photo", true)) {
                        //照片
                        //                                                val imgMsg: ImageMessage = ImageMessage.obtain(imageFileThumbUri, imageFileSourceUri)
                        //                        mPresenter.sendImgMsg(ImageUtils.genThumbImgFile(path), File(path))
                        conversationAdapter.addLastItem(ImageMessage(Message.MessageDirection.SEND, Message.SentStatus.SENT, System.currentTimeMillis(), path.toString()))
                        path?.let { mViewModel.sendImage(it) }
                    } else {
                        //小视频
                        //                        mPresenter.sendFileMsg(File(path))
                    }
                }
                moveToBottom()
            }
            REQUEST_TAKE_PHOTO -> if (resultCode == RESULT_OK) {
                val path = data!!.getStringExtra("path")
                if (data.getBooleanExtra("take_photo", true)) {
                    conversationAdapter.addLastItem(ImageMessage(Message.MessageDirection.SEND, Message.SentStatus.SENT, System.currentTimeMillis(), path.toString()))
                    path?.let { mViewModel.sendImage(it) }
                } else {
                    //                    mPresenter.sendFileMsg(File(path))
                }
                moveToBottom()
            }
            REQUEST_LOCATION -> if (resultCode == RESULT_OK) {
                val locationData: LocationData = data!!.getSerializableExtra("location") as LocationData
                val locationMessage = LocationMessage(Message.MessageDirection.SEND, Message.SentStatus.SENT, System.currentTimeMillis(), locationData.lat, locationData.lng, locationData.poi)
                conversationAdapter.addLastItem(locationMessage)
                mViewModel.sendLocation(locationMessage)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBGARefreshLayoutBeginRefreshing(refreshLayout: BGARefreshLayout?) {
        //        mPresenter.loadMore()
    }

    override fun onBGARefreshLayoutBeginLoadingMore(refreshLayout: BGARefreshLayout?): Boolean {
        return false
    }

    private fun initAudioRecordManager() {
        AudioRecordManager.getInstance(this).maxVoiceDuration = Constants.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND
        val audioDir: File = File(Constants.AUDIO_SAVE_DIR)
        if (!audioDir.exists()) {
            audioDir.mkdirs()
        }
        AudioRecordManager.getInstance(this).setAudioSavePath(audioDir.absolutePath)
        AudioRecordManager.getInstance(this).audioRecordListener = object : IAudioRecordListener {
            private lateinit var timerTV: TextView
            private lateinit var waveView: VoiceWaveView
            private lateinit var popRoot: ConstraintLayout
            private lateinit var stateTV: TextView
            private lateinit var voiceBg: ImageView
            private var mRecordWindow: PopupWindow? = null
            private var startRecordTime: Long = 0
            override fun initTipView() {
                val view = View.inflate(this@ConversationActivity, R.layout.popup_audio, null)
                popRoot = view.findViewById(R.id.popRoot)
                waveView = view.findViewById(R.id.waveview)
                timerTV = view.findViewById(R.id.timeTv)
                stateTV = view.findViewById(R.id.stateTV)
                voiceBg = view.findViewById(R.id.voiceBg)
                mRecordWindow = PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, (resources.displayMetrics.heightPixels - 104.dp).toInt())
                mRecordWindow!!.showAtLocation(llRoot, Gravity.TOP, 0, 0)
                mRecordWindow!!.isFocusable = true
                mRecordWindow!!.isOutsideTouchable = false
                mRecordWindow!!.isTouchable = false
                waveView?.apply {
                    lineWidth = 3.dp
                    duration = 150
                    addHeader(2)
                    addBody(12)
                    addBody(6)
                    addBody(8)
                    addBody(4)
                    addFooter(2)
                    start()
                }
            }

            override fun setTimeoutTipView(counter: Int) {
                if (mRecordWindow != null) {
                    stateTV.setText(R.string.voice_rec)
                    voiceBg.setBackgroundResource(R.drawable.pop_voice_bg)

                }
            }

            override fun setRecordingTipView() {
                if (mRecordWindow != null) {
                    stateTV.setText(R.string.voice_rec)
                    voiceBg.setBackgroundResource(R.drawable.pop_voice_bg)

                }
            }

            override fun setAudioShortTipView() {
                if (mRecordWindow != null) {
                    stateTV.setText(R.string.voice_short)
                }
            }

            override fun setCancelTipView() {
                if (mRecordWindow != null) {
                    stateTV.setText(R.string.voice_cancel)
                    voiceBg.setBackgroundResource(R.drawable.pop_voice_cancle_bg)
                }
            }

            override fun destroyTipView() {
                if (mRecordWindow != null) {
                    mRecordWindow!!.dismiss()
                    mRecordWindow = null
                }
            }

            override fun onStartRecord() {
                startRecordTime = System.currentTimeMillis()
                println("--------------开始录音")
                checkPermissions()
            }

            override fun onFinish(audioPath: Uri, duration: Int) {
                //发送文件
                val file = File(audioPath.path)
                if (file.exists()) {
                    conversationAdapter.addLastItem(VoiceMessage(Message.MessageDirection.SEND, Message.SentStatus.SENT, System.currentTimeMillis(), audioPath.path!!, duration.toLong()))
                    mViewModel.sendAudio(audioPath, duration)
                    moveToBottom()
                }
            }

            override fun onAudioDBChanged(db: Int) {
                timerTV.setText(String.format("%d\"", (System.currentTimeMillis() - startRecordTime) / 1000))
            }
        }
    }

    override fun onBackPressed() {
        if (elEmotion.isShown || moreActionDialog.isShown) {
            mEmotionKeyboard.interceptBackPress()
            ivEmo.setImageResource(R.drawable.ic_cheat_emo)
        } else {
            super.onBackPressed()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun receiveNewConversation(eventConversation: EventReceiveConversation) {
        if (eventConversation.fromAddress == mViewModel.uid) {
            NinjaApp.instance.conversations[mViewModel.uid]!!.unreadNo = 0
            NinjaApp.instance.conversations[mViewModel.uid]!!.unreadNoStr = "0"
            rvMsg.smoothMoveToPosition((rvMsg.adapter?.itemCount ?: 1) - 1)
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun receiveTextMessage(eventReceiveTextMessage: EventReceiveTextMessage) {
        if (eventReceiveTextMessage.fromAddress == mViewModel.uid) {
            conversationAdapter.addLastItem(eventReceiveTextMessage.textMessage)
            notifyAdapter()
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun receiveImageMessage(eventReceiveImageMessage: EventReceiveImageMessage) {
        if (eventReceiveImageMessage.fromAddress == mViewModel.uid) {
            conversationAdapter.addLastItem(eventReceiveImageMessage.imageMessage)
            notifyAdapter()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun receiveVoiceMessage(eventReceivevoiceMessage: EventReceiveVoiceMessage) {
        if (eventReceivevoiceMessage.fromAddress == mViewModel.uid) {
            conversationAdapter.addLastItem(eventReceivevoiceMessage.voiceMessage)
            notifyAdapter()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun receiveLocationMessage(eventReceiveLocationMessage: EventReceiveLocationMessage) {
        if (eventReceiveLocationMessage.fromAddress == mViewModel.uid) {
            conversationAdapter.addLastItem(eventReceiveLocationMessage.locationMessage)
            notifyAdapter()
        }
    }


    private fun notifyAdapter() {
        conversationAdapter.data.sortBy { message -> message.time }
        conversationAdapter.notifyDataSetChanged()
        rvMsg.smoothMoveToPosition((rvMsg.adapter?.itemCount ?: 1) - 1)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateConversationNickName(eventUpdateConversationNickName: EventUpdateConversationNickName) {
        if (eventUpdateConversationNickName.uid == mViewModel.uid) {
            NinjaApp.instance.conversations.get(mViewModel.uid)?.let { mViewModel.title.set(it.nickName) }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}