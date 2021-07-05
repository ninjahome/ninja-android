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
import com.lqr.adapter.LQRViewHolder
import com.lqr.adapter.OnItemClickListener
import com.lqr.audio.AudioPlayManager
import com.lqr.audio.AudioRecordManager
import com.lqr.audio.IAudioPlayListener
import com.lqr.audio.IAudioRecordListener
import com.lqr.emoji.EmotionKeyboard
import com.lqr.emoji.IEmotionSelectedListener
import com.lqr.emoji.LQREmotionKit
import com.lqr.emoji.MoonUtils
import com.lxj.xpopup.core.BasePopupView
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.dp
import com.ninjahome.ninja.*
import com.ninjahome.ninja.databinding.ActivityConversationBinding
import com.ninjahome.ninja.event.*
import com.ninjahome.ninja.imagepicker.PhotoFromPhotoAlbum
import com.ninjahome.ninja.imagepicker.WechatImagePicker
import com.ninjahome.ninja.model.bean.*
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.room.ConversationDBManager
import com.ninjahome.ninja.room.MessageDBManager
import com.ninjahome.ninja.ui.adapter.ConversationAdapter
import com.ninjahome.ninja.utils.DialogUtils
import com.ninjahome.ninja.view.ConversationMoreActionPop
import com.ninjahome.ninja.viewmodel.ConversationViewModel
import com.qingmei2.rximagepicker.core.RxImagePicker.create
import com.qingmei2.rximagepicker_extension.MimeType
import com.qingmei2.rximagepicker_extension_wechat.WechatConfigrationBuilder
import com.qingmei2.rximagepicker_extension_wechat.ui.WechatImagePickerFragment
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import me.xfans.lib.voicewaveview.VoiceWaveView
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
class ConversationActivity : BaseActivity<ConversationViewModel, ActivityConversationBinding>(R.layout.activity_conversation), BGARefreshLayout.BGARefreshLayoutDelegate, EasyPermissions.PermissionCallbacks, OnItemClickListener {

    private val REQUEST_IMAGE_PICKER = 1000
    private val REQUEST_TAKE_PHOTO = 1001
    private val REQUEST_LOCATION = 1002
    lateinit var imagePicker: WechatImagePicker

    private lateinit var conversationAdapter: ConversationAdapter

    private var mIsFirst = false
    private lateinit var mEmotionKeyboard: EmotionKeyboard
    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val mData: MutableList<Message> = mutableListOf()

    var conversation: Conversation? = null
    override val mViewModel: ConversationViewModel by viewModel()

    private lateinit var moreActionDialog: BasePopupView

    override fun initView() {
        imagePicker = create(WechatImagePicker::class.java)
        ImmersionBar.with(this).statusBarColor(com.ninja.android.lib.R.color.white).barEnable(true).keyboardEnable(true).statusBarDarkFont(true).fitsSystemWindows(true).init()
        initEmotionKeyboard()
        rvMsg.itemAnimator = null
        moreActionDialog = DialogUtils.showMoreActionDialog(this, object : ConversationMoreActionPop.ConversationMoreActionListener {
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


    override fun initData() {
        mViewModel.uid = intent.getStringExtra(IntentKey.UID)!!
        setTitle()
        conversationAdapter = ConversationAdapter(this, mData, mViewModel)
        rvMsg.adapter = conversationAdapter
        conversationAdapter.onItemClickListener = this
        initAudioRecordManager()
    }

    private fun setTitle() {
        rxLifeScope.launch {
            ContactDBManager.observeNickNameByUID(mViewModel.uid).observe(this@ConversationActivity) {
                if (TextUtils.isEmpty(it)) {
                    mViewModel.title.set(mViewModel.uid)
                } else {
                    mViewModel.title.set(it)
                }
            }
        }


        mViewModel.rightIv.set(R.drawable.contact_more)
        mViewModel.showRightIv.set(true)
    }

    override fun initObserve() {
        observeConversation()
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

        mViewModel.observableConversationEvent.observe(this) {
            observeConversation()
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


    private fun observeConversation() {
        MainScope().launch {
            conversation = mViewModel.queryConversation()
            if (conversation != null) {
                MessageDBManager.queryByConversationId(conversation!!.id).observe(this@ConversationActivity) { it ->
                    mData.clear()
                    if (it != null) {
                        mData.addAll(it)
                    }
                    conversationAdapter.notifyDataSetChanged()
                    rvMsg.smoothMoveToPosition((rvMsg.adapter?.itemCount ?: 1) - 1)
                }
            }
        }

    }

    private fun clearUnreadNumber() {
        MainScope().launch {
            val conversation = mViewModel.queryConversation()
            conversation?.let {
                MessageDBManager.updateMessage2Read(it.id)
                it.unreadCount = 0
                if (conversationAdapter.lastItem != null) {
                    it.msg = conversationAdapter.lastItem.msg
                }
                ConversationDBManager.updateConversations(it)
            }

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
        val build = WechatConfigrationBuilder(MimeType.INSTANCE.ofImage(), false).maxSelectable(9).countable(true).spanCount(4).countable(false).build()
        imagePicker.openGallery(this, build).subscribe {
                    val original = it.getBooleanExtra(WechatImagePickerFragment.EXTRA_ORIGINAL_IMAGE, false)
                    var path = PhotoFromPhotoAlbum.getRealPathFromUri(this@ConversationActivity, it.uri)
                    if (path == null) {
                        path = it.uri.toString()
                    }
                    mViewModel.sendImage(path, !original)
                    moveToBottom()
                }
    }

    private fun startTakePhotoActivity() {
        val intent = Intent(this, TakePhotoActivity::class.java)
        intent.putExtra(IntentKey.NAME, mViewModel.title.get())
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

                    etContent.clearFocus()
                    flEmotionView.visibility = View.GONE
                    hideEmotionLayout()
                    hideAudioButton()
                    mEmotionKeyboard.hideSoftInput()
                    handler.postDelayed(({
                        showMoreLayout()
                    }), 50)
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

            REQUEST_TAKE_PHOTO -> if (resultCode == RESULT_OK) {
                val path = data!!.getStringExtra("path")
                if (data.getBooleanExtra("take_photo", true)) {
                    path?.let { mViewModel.sendImage(it, true) }
                }
                moveToBottom()
            }
            REQUEST_LOCATION -> if (resultCode == RESULT_OK) {
                val locationData: LocationData = data!!.getSerializableExtra("location") as LocationData
                mViewModel.sendLocation(locationData.lng, locationData.lat, locationData.poi)
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
        val audioDir = File(Constants.AUDIO_SAVE_DIR)
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
                checkPermissions()
            }

            override fun onFinish(audioPath: Uri, duration: Int) {
                //发送文件
                val file = File(audioPath.path)
                if (file.exists()) {
                    mViewModel.sendAudio(audioPath, duration)
                    moveToBottom()
                }
            }

            override fun onAudioDBChanged(db: Int) {
                timerTV.text = String.format("%d\"", (System.currentTimeMillis() - startRecordTime) / 1000)
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
        clearUnreadNumber()
    }

    override fun onItemClick(helper: LQRViewHolder, parent: ViewGroup?, itemView: View?, position: Int) {
        val message = mData[position]
        if (message.type == Message.Type.IMAGE) {
            val intent = Intent(this, ShowBigImageActivity::class.java)
            //                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, itemView, "share").toBundle()
            intent.putExtra(IntentKey.URL, message.uri)
            startActivity(intent)
        } else if (message.type == Message.Type.VOICE) {
            val ivAudio: ImageView = helper.getView(R.id.ivAudioL)

            AudioPlayManager.getInstance().startPlay(this@ConversationActivity, Uri.parse(message.uri), object : IAudioPlayListener {
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
        } else if (message.type == Message.Type.LOCATION) {
            val intent = Intent(this@ConversationActivity, LocationShowActivity::class.java)
            intent.putExtra(IntentKey.LOCATION_LAT, message.lat)
            intent.putExtra(IntentKey.LOCATION_LNG, message.lng)
            intent.putExtra(IntentKey.LOCATION_ADDRESS, message.locationAddress)
            startActivity(intent)
        }
    }

}