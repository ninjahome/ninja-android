package com.ninjahome.ninja.model.bean

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ImageMessage(conversationId:Long,direction: MessageDirection, status: SentStatus, time: Long, var localUri: String) : Message(0,conversationId,direction, status, time, Type.IMAGE,  msg = "[图片]")