package com.ninjahome.ninja.event

import com.ninjahome.ninja.model.bean.VoiceMessage


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class EventReceiveVoiceMessage(var fromAddress: String, var toAddress: String, var voiceMessage: VoiceMessage)