package com.ninjahome.ninja.model

import androidlib.Androidlib
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ConversationModel {

    suspend fun sendTextMessage(uid: String, data: String) {
        withContext(Dispatchers.IO) {
            if (!Androidlib.wsIsOnline()) {
                Androidlib.wsOnline()
            }
            Androidlib.writeMessage(uid, data)
        }
    }

    suspend fun sendImageMessage(uid: String, path: String) {
        withContext(Dispatchers.IO) {
            if (!Androidlib.wsIsOnline()) {
                Androidlib.wsOnline()
            }

            Androidlib.writeImageMessage(uid, File(path).readBytes())
        }
    }

    suspend fun sendAudioMessage(uid: String, audioPath: String, duration: Int) {
        withContext(Dispatchers.IO) {
            if (!Androidlib.wsIsOnline()) {
                Androidlib.wsOnline()
            }

            Androidlib.writeVoiceMessage(uid, File(audioPath).readBytes(), duration.toLong())
        }
    }

    suspend fun sendLocationMessage(uid: String, lat: Float, lng: Float, poi: String) {
        withContext(Dispatchers.IO) {
            if (!Androidlib.wsIsOnline()) {
                Androidlib.wsOnline()
            }
            Androidlib.writeLocationMessage(uid, lat, lng, poi)
        }
    }


}