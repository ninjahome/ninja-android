package com.ninjahome.ninja.utils

import com.ninjahome.ninja.model.bean.ChatMessage
import com.ninjahome.ninja.model.bean.User

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object ChatMessageFactory {

    fun createChatMessage(id: String, name: String, avatar: String, online: Boolean, message: String): ChatMessage {
        val user = User(id, name, avatar, online)
        return ChatMessage(id, user, message)
    }

}