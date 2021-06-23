package com.ninjahome.ninja.ui.adapter

import android.content.Context
import android.text.style.ImageSpan
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.lqr.emoji.MoonUtils
import com.ninja.android.lib.utils.dp
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.*
import com.ninjahome.ninja.model.bean.Message.SentStatus
import com.ninjahome.ninja.utils.TimeUtils
import com.ninjahome.ninja.utils.UIUtils
import com.ninjahome.ninja.view.BubbleImageView

/**
 * @描述 会话界面的消息列表适配器
 */
class ConversationAdapter(private val mContext: Context, private val mData: List<Message>) : LQRAdapterForRecyclerView<Message>(mContext, mData) {
    override fun convert(helper: LQRViewHolderForRecyclerView, item: Message, position: Int) {
        setTime(helper, item, position)
        setView(helper, item, position)
        setAvatar(helper, item, position)
        setName(helper, item, position)
        setStatus(helper, item, position)
        setOnClick(helper, item, position)
    }

    private fun setView(helper: LQRViewHolderForRecyclerView, item: Message, position: Int) {
        //根据消息类型设置消息显示内容
        //        MessageContent msgContent = item.getContent()
        if (item is TextMessage) {
            MoonUtils.identifyFaceExpression(mContext, helper.getView(R.id.tvText), item.data?.let { String(it) }, ImageSpan.ALIGN_BOTTOM)
        } else if (item is ImageMessage) {
            val bivPic = helper.getView<BubbleImageView>(R.id.bivPic)
            Glide.with(mContext).load(item.localUri ?: item.localUri).error(R.drawable.default_img_failed).override(80.dp.toInt(), 150.dp.toInt()).centerCrop().into(bivPic)
        } else if (item is LocationMessage) {
            helper.setText(R.id.tvTitle, item.poi)
            val ivLocation = helper.getView<ImageView>(R.id.ivLocation)
            Glide.with(mContext).load("http://st.map.qq.com/api?size=708*270&center=${item.lng},${item.lat}&zoom=17&referer=weixin").placeholder(R.drawable.default_location).centerCrop().into(ivLocation)
        } else if (item is VoiceMessage) {
            val increment = (UIUtils.getDisplayWidth() / 2 / Constants.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND * item.duration)
            val rlAudio = helper.setText(R.id.tvDuration, item.duration.toString() + "''").getView<RelativeLayout>(R.id.rlAudio)
            val params = rlAudio.layoutParams
            params.width = (65.dp + UIUtils.dip2Px(increment.toInt())).toInt()
            rlAudio.layoutParams = params
        }
        //        else if (item instanceof FileMessage) {
        //            FileMessage fileMessage = (FileMessage) msgContent
        //            if (MediaFileUtils.isImageFileType(fileMessage.getName())) {
        //                ImageView ivPic = helper.getView(R.id.ivSticker)
        //                Glide.with(mContext).load(fileMessage.getLocalPath() == null ? fileMessage.getMediaUrl() :
        //                        fileMessage.getLocalPath()).placeholder(R.drawable.logo).error(R.mipmap.default_img_failed).centerCrop().into(ivPic)
        //            } else if (MediaFileUtils.isVideoFileType(fileMessage.getName())) {
        //                BubbleImageView bivPic = helper.getView(R.id.bivPic)
        //                if (fileMessage.getLocalPath() != null && new File(fileMessage.getLocalPath().getPath()).exists()) {
        //                    VideoThumbLoader.getInstance().showThumb(fileMessage.getLocalPath().getPath(), bivPic, 200, 200)
        //                } else {
        //                    bivPic.setImageResource(R.mipmap.img_video_default)
        //                }
        //            }
        //        }
    }

    private fun setOnClick(helper: LQRViewHolderForRecyclerView, item: Message, position: Int) {
        //        helper.getView(R.id.llError).setOnClickListener(
        //                v ->
        //TODO 重新发送
        //                RongIMClient.getInstance().deleteMessages(new int[]{item.getMessageId()}, new RongIMClient.ResultCallback<Boolean>() {
        //                    @Override
        //                    public void onSuccess(Boolean aBoolean) {
        //                        mData.remove(position)
        //                        mPresenter.setAdapter()
        //                        MessageContent content = item.getContent()
        //                        if (content instanceof TextMessage) {
        //                            mPresenter.sendTextMsg(((TextMessage) content).getContent())
        //                        } else if (content instanceof ImageMessage) {
        //                            mPresenter.sendImgMsg(((ImageMessage) content).getThumUri(), ((ImageMessage) content).getLocalUri())
        //                        } else if (content instanceof FileMessage) {
        //                            mPresenter.sendFileMsg(new File(((FileMessage) content).getLocalPath().getPath()))
        //                        } else if (content instanceof VoiceMessage) {
        //                            VoiceMessage voiceMessage = (VoiceMessage) content
        //                            mPresenter.sendAudioFile(voiceMessage.getUri(), voiceMessage.getDuration())
        //                        }
        //                    }
        //
        //                    @Override
        //                    public void onError(RongIMClient.ErrorCode errorCode) {
        //
        //                    }
        //                })
        //        )
        helper.getView<View>(R.id.ivAvatar).setOnClickListener { v: View? -> }
    }

    private fun setStatus(helper: LQRViewHolderForRecyclerView, item: Message, position: Int) {
        if (item is TextMessage || item is LocationMessage || item is VoiceMessage) {
            //只需要设置自己发送的状态
            val sentStatus = item.sentStatus
            if (sentStatus === SentStatus.SENDING) {
                helper.setViewVisibility(R.id.pbSending, View.VISIBLE).setViewVisibility(R.id.llError, View.GONE)
            } else if (sentStatus === SentStatus.FAILED) {
                helper.setViewVisibility(R.id.pbSending, View.GONE).setViewVisibility(R.id.llError, View.VISIBLE)
            } else if (sentStatus === SentStatus.SENT) {
                helper.setViewVisibility(R.id.pbSending, View.GONE).setViewVisibility(R.id.llError, View.GONE)
            }
        } else if (item is ImageMessage) {
            val bivPic = helper.getView<BubbleImageView>(R.id.bivPic)
            val isSend = if (item.direction === Message.MessageDirection.SEND) true else false
            if (isSend) {
                val sentStatus = item.sentStatus
                if (sentStatus === SentStatus.SENDING) {
                    bivPic.setProgressVisible(true)
                    //                    if (!TextUtils.isEmpty(item.getExtra()))
                    //                        bivPic.setPercent(Integer.valueOf(item.getExtra()))
                    bivPic.showShadow(true)
                    helper.setViewVisibility(R.id.llError, View.GONE)
                } else if (sentStatus === SentStatus.FAILED) {
                    bivPic.setProgressVisible(false)
                    bivPic.showShadow(false)
                    helper.setViewVisibility(R.id.llError, View.VISIBLE)
                } else if (sentStatus === SentStatus.SENT) {
                    bivPic.setProgressVisible(false)
                    bivPic.showShadow(false)
                    helper.setViewVisibility(R.id.llError, View.GONE)
                }
            } else {
                bivPic.setProgressVisible(false)
                bivPic.showShadow(false)
                helper.setViewVisibility(R.id.llError, View.GONE)
            }
        }
        //        else if (msgContent instanceof FileMessage) {
        //            BubbleImageView bivPic = helper.getView(R.id.bivPic)
        //            FileMessage fileMessage = (FileMessage) msgContent
        //            boolean isSend = item.getMessageDirection() == Message.MessageDirection.SEND ? true : false
        //
        //            if (MediaFileUtils.isImageFileType(fileMessage.getName())) {
        //                if (isSend) {
        //                    Message.SentStatus sentStatus = item.getSentStatus()
        //                    if (sentStatus == Message.SentStatus.SENDING) {
        //                    } else if (sentStatus == Message.SentStatus.FAILED) {
        //                        helper.setViewVisibility(R.id.llError, View.VISIBLE)
        //                    } else if (sentStatus == Message.SentStatus.SENT) {
        //                        helper.setViewVisibility(R.id.llError, View.GONE)
        //                    }
        //                } else {
        //                    if (bivPic != null) {
        //                        bivPic.setProgressVisible(false)
        //                        bivPic.showShadow(false)
        //                    }
        //                    helper.setViewVisibility(R.id.llError, View.GONE)
        //                }
        //            } else if (MediaFileUtils.isVideoFileType(fileMessage.getName())) {
        //                CircularProgressBar cpbLoading = helper.getView(R.id.cpbLoading)
        //                if (isSend) {
        //                    Message.SentStatus sentStatus = item.getSentStatus()
        //                    if (sentStatus == Message.SentStatus.SENDING || fileMessage.getLocalPath() == null || (fileMessage.getLocalPath() != null && !new File(fileMessage.getLocalPath().getPath()).exists())) {
        //                        if (!TextUtils.isEmpty(item.getExtra())) {
        //                            cpbLoading.setMax(100)
        //                            cpbLoading.setProgress(Integer.valueOf(item.getExtra()))
        //                        } else {
        //                            cpbLoading.setMax(100)
        //                            cpbLoading.setProgress(0)
        //                        }
        //                        helper.setViewVisibility(R.id.llError, View.GONE).setViewVisibility(R.id.cpbLoading, View.VISIBLE)
        //                        bivPic.showShadow(true)
        //                    } else if (sentStatus == Message.SentStatus.FAILED) {
        //                        helper.setViewVisibility(R.id.llError, View.VISIBLE).setViewVisibility(R.id.cpbLoading, View.GONE)
        //                        bivPic.showShadow(false)
        //                    } else if (sentStatus == Message.SentStatus.SENT) {
        //                        helper.setViewVisibility(R.id.llError, View.GONE).setViewVisibility(R.id.cpbLoading, View.GONE)
        //                        bivPic.showShadow(false)
        //                    }
        //                } else {
        //                    Message.ReceivedStatus receivedStatus = item.getReceivedStatus()
        //                    if (receivedStatus.isDownload() || fileMessage.getLocalPath() != null) {
        //                        helper.setViewVisibility(R.id.llError, View.GONE).setViewVisibility(R.id.cpbLoading, View.GONE)
        //                        bivPic.showShadow(false)
        //                    } else {
        //                        if (!TextUtils.isEmpty(item.getExtra())) {
        //                            cpbLoading.setMax(100)
        //                            cpbLoading.setProgress(Integer.valueOf(item.getExtra()))
        //                        } else {
        //                            cpbLoading.setMax(100)
        //                            cpbLoading.setProgress(0)
        //                        }
        //                        helper.setViewVisibility(R.id.llError, View.GONE).setViewVisibility(R.id.cpbLoading, View.VISIBLE)
        //                        bivPic.showShadow(true)
        //                    }
        //                }
        //            }
        //        }
    }

    private fun setAvatar(helper: LQRViewHolderForRecyclerView, item: Message, position: Int) {
//                ImageView ivAvatar = helper.getView(R.id.ivAvatar)
//                UserInfo userInfo = DBManager.getInstance().getUserInfo(item.getSenderUserId())
//                if (userInfo != null) {
//                    Glide.with(mContext).load(userInfo.getPortraitUri()).centerCrop().into(ivAvatar)
//                }
    }

    private fun setName(helper: LQRViewHolderForRecyclerView, item: Message, position: Int) {
//                if (item.getConversationType() == Conversation.ConversationType.PRIVATE) {
//                    helper.setViewVisibility(R.id.tvName, View.GONE)
//                } else {
//                    helper.setViewVisibility(R.id.tvName, View.GONE)
//                            .setText(R.id.tvName, item.getContent().getUserInfo().getName())
//                }
    }

    private fun setTime(helper: LQRViewHolderForRecyclerView, item: Message, position: Int) {
                val msgTime = item.time
                if (position > 0) {
                    val preMsg = mData.get(position - 1)
                    val preMsgTime =preMsg.time
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
                val msg = mData.get(position)
                val isSend = msg.direction == Message.MessageDirection.SEND

                if (msg is TextMessage) {
                    return  if (isSend)  SEND_TEXT else RECEIVE_TEXT
                }
                if (msg is ImageMessage) {
                    return  if (isSend) SEND_IMAGE else RECEIVE_IMAGE
                }
//                if (msg is FileMessage) {
//                    FileMessage fileMessage = (FileMessage) msgContent
//                    if (MediaFileUtils.isImageFileType(fileMessage.getName())) {
//                        return isSend ? SEND_STICKER : RECEIVE_STICKER
//                    } else if (MediaFileUtils.isVideoFileType(fileMessage.getName())) {
//                        return isSend ? SEND_VIDEO : RECEIVE_VIDEO
//                    }
//                }
                if (msg is LocationMessage) {
                    return if (isSend) SEND_LOCATION else RECEIVE_LOCATION
                }
                if (msg is VoiceMessage) {
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