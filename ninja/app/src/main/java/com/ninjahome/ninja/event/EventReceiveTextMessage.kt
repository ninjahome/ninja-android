package com.ninjahome.ninja.event

import com.ninjahome.ninja.model.bean.TextMessage


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class EventReceiveTextMessage(var fromAddress: String, var toAddress: String, var textMessage: TextMessage)