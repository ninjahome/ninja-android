package com.ninjahome.ninja.ui.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import chatLib.ChatLib
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.lqr.emoji.MoonUtils
import com.ninja.android.lib.provider.context
import com.ninja.android.lib.utils.SharedPref
import com.ninja.android.lib.utils.dp
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.imageloader.ImageLoaderProxy
import com.ninjahome.ninja.model.bean.Message
import com.ninjahome.ninja.model.bean.Message.SentStatus
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.room.GroupDBManager
import com.ninjahome.ninja.ui.adapter.lqr.LQRAdapterForRecyclerView
import com.ninjahome.ninja.utils.TimeUtils
import com.ninjahome.ninja.utils.UIUtils
import com.ninjahome.ninja.utils.fromJson
import com.ninjahome.ninja.view.BubbleImageView
import com.ninjahome.ninja.view.contacts.ColorUtil
import com.ninjahome.ninja.view.contacts.TextDrawable
import com.zhy.autolayout.utils.ScreenUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * @描述 会话界面的消息列表适配器
 */
class ConversationAdapter(private val mContext: Context, private val mData: List<Message>, val isGroup: Boolean, val groupId: String, val clickListener: ClickListener) : LQRAdapterForRecyclerView<Message>(mContext, mData) {
    var receiverIcon: TextDrawable? = null
    private val mDrawableBuilder = TextDrawable.builder().beginConfig().fontSize(30)
    private val userName: String by SharedPref(context(), Constants.KEY_USER_NAME, "")
    private val conversationIconIndex = ChatLib.iconIndex(NinjaApp.instance.account.address, ColorUtil.colorSize)
    private val conversationIconColor = ColorUtil.colors[conversationIconIndex]
    val subName = if (userName.length >= 2) userName.substring(0, 2) else userName
    private val myIcon = mDrawableBuilder.textColor(mContext.getColor(R.color.white)).endConfig().buildRound(subName, mContext.resources.getColor(conversationIconColor, null))

    interface ClickListener {
        fun clickItemReCommit(item: Message)
        fun clickItemAvatar(item: Message)
        fun clickItemName(item: Message)
    }

    override fun convert(helper: LQRViewHolderForRecyclerView, item: Message, position: Int) {
        setTime(helper, item, position)
        setView(helper, item)
        setAvatar(helper, item, position)
        setName(helper, item, position)
        setStatus(helper, item, position)
        setOnClick(helper, item, position)
    }

    private fun setView(helper: LQRViewHolderForRecyclerView, item: Message) {
        //根据消息类型设置消息显示内容
        if (item.type == Message.Type.TEXT) {
            MoonUtils.identifyFaceExpression(mContext, helper.getView(R.id.tvText), item.msg, ImageSpan.ALIGN_BOTTOM)
        } else if (item.type == Message.Type.IMAGE) {
            val bivPic = helper.getView<BubbleImageView>(R.id.bivPic)
            ImageLoaderProxy.loadImage(item.uri, bivPic, R.drawable.default_img_failed)
        } else if (item.type == Message.Type.LOCATION) {
            helper.setText(R.id.tvTitle, item.locationAddress)
            val ivLocation = helper.getView<ImageView>(R.id.ivLocation)
            val url = "http://st.map.qq.com/api?size=708*270&center=${item.lng},${item.lat}&zoom=16&referer=weixin"
            ImageLoaderProxy.loadImage(url, ivLocation)
        } else if (item.type == Message.Type.VOICE) {
            val increment = (UIUtils.getDisplayWidth() / 2 / Constants.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND * item.duration)
            val rlAudio = helper.setText(R.id.tvDuration, item.duration.toString() + "''").getView<RelativeLayout>(R.id.rlAudio)
            val params = rlAudio.layoutParams
            params.width = (65.dp + UIUtils.dip2Px(increment)).toInt()
            rlAudio.layoutParams = params
        }
    }

    private fun setOnClick(helper: LQRViewHolderForRecyclerView, item: Message, position: Int) {
        helper.getView<View>(R.id.llError).setOnClickListener {
            clickListener.clickItemReCommit(item)
        }
        if (isGroup) {
            helper.getView<View>(R.id.ivAvatar).setOnClickListener {
                clickListener.clickItemAvatar(item)
            }
            helper.getView<View>(R.id.tvName).setOnClickListener {
                clickListener.clickItemName(item)
            }
        }

    }

    private fun setStatus(helper: LQRViewHolderForRecyclerView, item: Message, position: Int) {
        if (item.type == Message.Type.TEXT || item.type == Message.Type.LOCATION || item.type == Message.Type.VOICE) {
            //只需要设置自己发送的状态
            val sentStatus = item.sentStatus
            if (sentStatus === SentStatus.SENDING) {
                helper.setViewVisibility(R.id.pbSending, View.VISIBLE).setViewVisibility(R.id.llError, View.GONE)
            } else if (sentStatus === SentStatus.FAILED) {
                helper.setViewVisibility(R.id.pbSending, View.GONE).setViewVisibility(R.id.llError, View.VISIBLE)
            } else if (sentStatus === SentStatus.SENT) {
                helper.setViewVisibility(R.id.pbSending, View.GONE).setViewVisibility(R.id.llError, View.GONE)
            }
        } else if (item.type == Message.Type.IMAGE) {
            val bivPic = helper.getView<BubbleImageView>(R.id.bivPic)
            val isSend = item.direction == Message.MessageDirection.SEND
            if (isSend) {
                when (item.sentStatus) {
                    SentStatus.SENDING -> {
                        bivPic.setProgressVisible(true)
                        bivPic.showShadow(true)
                        helper.setViewVisibility(R.id.llError, View.GONE)
                    }
                    SentStatus.FAILED -> {
                        bivPic.setProgressVisible(false)
                        bivPic.showShadow(false)
                        helper.setViewVisibility(R.id.llError, View.VISIBLE)
                    }
                    SentStatus.SENT -> {
                        bivPic.setProgressVisible(false)
                        bivPic.showShadow(false)
                        helper.setViewVisibility(R.id.llError, View.GONE)
                    }
                    else -> {

                    }
                }
            } else {
                bivPic.setProgressVisible(false)
                bivPic.showShadow(false)
                helper.setViewVisibility(R.id.llError, View.GONE)
            }
        }
    }

    private fun setAvatar(helper: LQRViewHolderForRecyclerView, item: Message, position: Int) {
        if (item.direction == Message.MessageDirection.SEND) {
            helper.itemView.findViewById<ImageView>(R.id.ivAvatar).background = myIcon
        } else {
            setReceiverAvatar(helper, item)
        }
    }

    private fun setReceiverAvatar(helper: LQRViewHolderForRecyclerView, item: Message) {
        CoroutineScope(Dispatchers.IO).launch {
            var name = item.from
            if (isGroup) {
                val group = GroupDBManager.queryByGroupId(groupId)
                group?.let {
                    val memberIds = it.memberIdList.fromJson<List<String>>()
                    val memberNames = it.memberNickNameList.fromJson<List<String>>()
                    for (index in memberIds!!.indices) {
                        if (memberIds[index] == item.from) {
                            name = memberNames!![index]
                        }
                    }
                }

            }
            val contact = ContactDBManager.queryByID(item.from)
            contact?.let {
                name = it.nickName
            }
            helper.itemView.findViewById<TextView>(R.id.tvName).text = name
            val subName = if (name.length >= 2) name.substring(0, 2) else name
            val receiverIconIndex = ChatLib.iconIndex(item.from, ColorUtil.colorSize)
            val receiverIconColor = ColorUtil.colors[receiverIconIndex]
            receiverIcon = mDrawableBuilder.textColor(mContext.getColor(R.color.white)).endConfig().buildRound(subName, mContext.resources.getColor(receiverIconColor, null))
            helper.itemView.findViewById<ImageView>(R.id.ivAvatar).background = receiverIcon

        }
    }


    private fun setName(helper: LQRViewHolderForRecyclerView, item: Message, position: Int) {

    }

    private fun setTime(helper: LQRViewHolderForRecyclerView, item: Message, position: Int) {
        val msgTime = item.time
        if (position > 0) {
            val preMsg = mData[position - 1]
            val preMsgTime = preMsg.time
            if (msgTime - preMsgTime > (5 * 60 * 1000)) {
                helper.setViewVisibility(R.id.tvTime, View.VISIBLE).setText(R.id.tvTime, TimeUtils.getMsgFormatTime(msgTime))
            } else {
                helper.setViewVisibility(R.id.tvTime, View.GONE)
            }
        } else {
            helper.setViewVisibility(R.id.tvTime, View.VISIBLE).setText(R.id.tvTime, TimeUtils.getMsgFormatTime(msgTime))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val msg = mData[position]
        val isSend = msg.direction == Message.MessageDirection.SEND

        if (msg.type == Message.Type.TEXT) {
            return if (isSend) SEND_TEXT else RECEIVE_TEXT
        }
        if (msg.type == Message.Type.IMAGE) {
            return if (isSend) SEND_IMAGE else RECEIVE_IMAGE
        }
        if (msg.type == Message.Type.LOCATION) {
            return if (isSend) SEND_LOCATION else RECEIVE_LOCATION
        }
        if (msg.type == Message.Type.VOICE) {
            return if (isSend) SEND_VOICE else RECEIVE_VOICE
        }


        return UNDEFINE_MSG
    }

    companion object {
        private const val SEND_TEXT = R.layout.item_text_send
        private const val RECEIVE_TEXT = R.layout.item_text_receive
        private const val SEND_IMAGE = R.layout.item_image_send
        private const val RECEIVE_IMAGE = R.layout.item_image_receive
        private const val SEND_LOCATION = R.layout.item_location_send
        private const val RECEIVE_LOCATION = R.layout.item_location_receive
        private const val RECEIVE_VOICE = R.layout.item_audio_receive
        private const val SEND_VOICE = R.layout.item_audio_send
        private const val UNDEFINE_MSG = R.layout.item_no_support_msg_type
    }
}