package com.ninjahome.ninja.model.bean

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class VoiceMessage(conversationId:Long,direction: MessageDirection, status: SentStatus, time: Long, var localUrl: String,  duration: Long) : Message(0,conversationId,direction, status, time, Type.VOICE, msg = "语音")