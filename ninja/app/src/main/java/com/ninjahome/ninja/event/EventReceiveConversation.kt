package com.ninjahome.ninja.event

import com.ninjahome.ninja.model.bean.ChatMessage

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class EventReceiveConversation(var fromAddress: String, var toAddress: String, var chatMessage: ChatMessage)