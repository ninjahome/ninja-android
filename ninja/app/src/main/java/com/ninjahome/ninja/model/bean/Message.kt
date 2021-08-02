package com.ninjahome.ninja.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ninjahome.ninja.room.Converters

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@Entity
@TypeConverters(Converters::class)
open class Message(@PrimaryKey(autoGenerate = true) var id: Long = 0, var conversationId: Long, var from: String, var to: String, var direction: MessageDirection, var sentStatus: SentStatus, var time: Long, var type: Type, var data: ByteArray = ByteArray(0), var msg: String = "", var unRead: Boolean = true, var uri: String = "", val lat: Float = 0.0f, var lng: Float = 0.0f, var locationAddress: String = "", var duration: Int = 0) {

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