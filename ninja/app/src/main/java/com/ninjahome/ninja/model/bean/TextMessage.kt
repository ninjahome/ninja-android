package com.ninjahome.ninja.model.bean

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TextMessage(direction: MessageDirection, status:SentStatus, time: Long, data:String):Message(direction,status,time,Type.TEXT,data.toByteArray(),data)