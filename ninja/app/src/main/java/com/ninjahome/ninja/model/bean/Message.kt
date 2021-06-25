package com.ninjahome.ninja.model.bean

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
open class Message(var direction: MessageDirection, var sentStatus: SentStatus, val time: Long, var type: Type, var data: ByteArray?, var des: String) {

    enum class MessageDirection(val value: Int) {
        SEND(1), RECEIVE(2)
    }

    enum class SentStatus(val value: Int) {
        SENDING(10), FAILED(20), SENT(30), RECEIVED(40), READ(50), DESTROYED(60), CANCELED(70)
    }

    enum class Type(val value: Int) {
        TEXT(1), IMAGE(2), VOICE(3), LOCATION(4),
    }
}