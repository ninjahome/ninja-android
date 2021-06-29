package com.ninjahome.ninja.model.bean

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class LocationMessage(conversationId:Long,direction: MessageDirection, status: SentStatus, time: Long,  val poi: String) : Message(0,conversationId,direction, status, time, Type.LOCATION, msg="[位置]")