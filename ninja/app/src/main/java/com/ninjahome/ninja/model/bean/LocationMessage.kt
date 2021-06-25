package com.ninjahome.ninja.model.bean

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class LocationMessage(direction: MessageDirection, status: SentStatus, time: Long, val lat: Float, val lng: Float, val poi: String) : Message(direction, status, time, Type.LOCATION, null, "[位置]")