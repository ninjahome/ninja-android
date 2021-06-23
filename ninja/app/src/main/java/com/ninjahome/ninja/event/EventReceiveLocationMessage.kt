package com.ninjahome.ninja.event

import com.ninjahome.ninja.model.bean.LocationMessage
import com.ninjahome.ninja.model.bean.TextMessage


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class EventReceiveLocationMessage(var fromAddress: String, var toAddress: String, var locationMessage: LocationMessage)