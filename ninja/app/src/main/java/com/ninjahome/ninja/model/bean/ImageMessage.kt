package com.ninjahome.ninja.model.bean

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ImageMessage(direction: MessageDirection, status: SentStatus, time: Long, var localUri: String) : Message(direction, status, time, Type.IMAGE, null, "[图片]")