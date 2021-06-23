package com.ninjahome.ninja.model.bean


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class Conversation(var address: String, var messages: MutableList<Message> = mutableListOf(), var lastMessage: Message, var unreadNo: Int, var unreadNoStr: String, var updateTime: Long, var nickName: String = "", var avatar: String = "")