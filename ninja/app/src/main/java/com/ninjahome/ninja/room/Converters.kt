package com.ninjahome.ninja.room

import androidx.room.TypeConverter
import com.ninjahome.ninja.model.bean.Message

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class Converters {

    @TypeConverter
    fun messageDirection2Int(messageDirection: Message.MessageDirection): Int {
        return messageDirection.value
    }

    @TypeConverter
    fun int2messageDirection(direction: Int): Message.MessageDirection {
        val values = Message.MessageDirection.values()
        values.forEach {
            if (direction == it.value) {
                return it
            }
        }

        return Message.MessageDirection.SEND
    }

    @TypeConverter
    fun type2Int(type: Message.Type): Int {
        return type.value
    }

    @TypeConverter
    fun int2Type(type: Int): Message.Type {
        val types = Message.Type.values()
        types.forEach {
            if (type == it.value) {
                return it
            }
        }
        return Message.Type.TEXT
    }

    @TypeConverter
    fun sentStatus2Int(status: Message.SentStatus): Int {
        return status.value
    }

    @TypeConverter
    fun int2SentStatus(statu: Int): Message.SentStatus {
        val status = Message.SentStatus.values()
        status.forEach {
            if (statu == it.value) {
                return it
            }
        }
        return Message.SentStatus.SENT
    }
}