package com.ninjahome.ninja.model.bean

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class VoiceMessage(direction: MessageDirection, status: SentStatus, time: Long, var localUrl: String, val duration: Long) : Message(direction, status, time, Type.VOICE, null, "语音")