package com.ninjahome.ninja.event

import com.ninjahome.ninja.model.bean.ImageMessage
import com.ninjahome.ninja.model.bean.TextMessage


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class EventReceiveImageMessage(var fromAddress: String, var toAddress: String, var imageMessage: ImageMessage)