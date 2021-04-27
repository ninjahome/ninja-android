package com.ninjahome.ninja.utils

import com.ninjahome.ninja.model.bean.ChatMessage
import com.ninjahome.ninja.model.bean.User
import java.security.SecureRandom

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object  ChatMessageFactory {

    fun createChatMessage(
        id: String,
        name: String,
        avatar: String,
        online: Boolean,
        message: String
    ):ChatMessage{
        val user = User(id, name, avatar, online)
        return ChatMessage(id, getUser(), message)
    }

    private fun getUser(): User? {
        val even: Boolean =  SecureRandom().nextBoolean()
        return User(
            if (even) "0" else "1",
            if (even) "xiaoming" else "laowang",
            if (even) "https://cdn.pixabay.com/photo/2017/12/25/17/48/waters-3038803_1280.jpg" else "https://pics4.baidu.com/feed/72f082025aafa40fdfda6880cfa0654779f0199f.jpeg?token=3e6c2e8c525249f4edd2b20a159c7224",
            true
        )
    }
}