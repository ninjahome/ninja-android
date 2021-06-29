package com.ninjahome.ninja.model.bean

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TextMessage(conversationId:Long,direction: MessageDirection, status: SentStatus, time: Long, data: String) : Message(0,conversationId,direction, status, time, Type.TEXT, data.toByteArray(), data)