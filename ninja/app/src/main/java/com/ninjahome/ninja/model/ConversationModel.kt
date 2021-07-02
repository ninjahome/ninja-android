package com.ninjahome.ninja.model

import androidlib.Androidlib
import com.ninjahome.ninja.utils.ImageUtils
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

    suspend fun sendImageMessage(uid: String, path: String,compress:Boolean) {
        withContext(Dispatchers.IO) {
            if (!Androidlib.wsIsOnline()) {
                Androidlib.wsOnline()
            }
            var imageFileSource:File? = null
            if(compress){
                imageFileSource = ImageUtils.compressImage(path)
            }
            imageFileSource = imageFileSource ?: File(path)
            Androidlib.writeImageMessage(uid, File(imageFileSource!!.path).readBytes())
        }
    }

    suspend fun sendVoiceMessage(uid: String, audioPath: String, duration: Int) {
        withContext(Dispatchers.IO) {
            if (!Androidlib.wsIsOnline()) {
                Androidlib.wsOnline()
            }

            Androidlib.writeVoiceMessage(uid, File(audioPath).readBytes(), duration.toLong())
        }
    }

    suspend fun sendLocationMessage(uid: String, lng: Float, lat: Float, poi: String) {
        withContext(Dispatchers.IO) {
            if (!Androidlib.wsIsOnline()) {
                Androidlib.wsOnline()
            }
            Androidlib.writeLocationMessage(uid, lng, lat, poi)
        }
    }


}