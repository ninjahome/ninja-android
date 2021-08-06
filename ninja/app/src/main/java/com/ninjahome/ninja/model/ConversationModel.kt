package com.ninjahome.ninja.model

import chatLib.ChatLib
import com.ninjahome.ninja.room.GroupDBManager
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
            if (!ChatLib.wsIsOnline()) {
                ChatLib.wsOnline()
            }
            ChatLib.writeMessage(uid, data)
        }
    }

    suspend fun sendImageMessage(uid: String, path: String, compress: Boolean) {
        withContext(Dispatchers.IO) {
            if (!ChatLib.wsIsOnline()) {
                ChatLib.wsOnline()
            }
            var imageFileSource: File? = null
            if (compress) {
                imageFileSource = ImageUtils.compressImage(path)
            }
            imageFileSource = imageFileSource ?: File(path)
            ChatLib.writeImageMessage(uid, File(imageFileSource.path).readBytes())
        }
    }

    suspend fun sendVoiceMessage(uid: String, audioPath: String, duration: Int) {
        withContext(Dispatchers.IO) {
            if (!ChatLib.wsIsOnline()) {
                ChatLib.wsOnline()
            }

            ChatLib.writeVoiceMessage(uid, File(audioPath).readBytes(), duration.toLong())
        }
    }

    suspend fun sendLocationMessage(uid: String, lng: Float, lat: Float, poi: String) {
        withContext(Dispatchers.IO) {
            if (!ChatLib.wsIsOnline()) {
                ChatLib.wsOnline()
            }
            ChatLib.writeLocationMessage(uid, lng, lat, poi)
        }
    }


    suspend fun sendGroupTextMessage(id: String, data: String, memberIdList: String) {
        withContext(Dispatchers.IO) {
            if (!ChatLib.wsIsOnline()) {
                ChatLib.wsOnline()
            }
            ChatLib.writeGroupMessage(memberIdList, id, data)

        }
    }


    suspend fun sendGroupImageMessage(id: String, path: String, compress: Boolean) {
        withContext(Dispatchers.IO) {
            if (!ChatLib.wsIsOnline()) {
                ChatLib.wsOnline()
            }
            var imageFileSource: File? = null
            if (compress) {
                imageFileSource = ImageUtils.compressImage(path)
            }
            imageFileSource = imageFileSource ?: File(path)
            val groupChat = GroupDBManager.queryByGroupId(id)
            groupChat?.let {
                ChatLib.writeImageGroupMessage(it.memberIdList, File(imageFileSource.path).readBytes(), id)
            }
        }

    }

    suspend fun sendGroupVoiceMessage(id: String, audioPath: String, duration: Int) {
        withContext(Dispatchers.IO) {
            if (!ChatLib.wsIsOnline()) {
                ChatLib.wsOnline()
            }
            val groupChat = GroupDBManager.queryByGroupId(id)
            groupChat?.let {

                ChatLib.writeVoiceGroupMessage(it.memberIdList, File(audioPath).readBytes(), duration.toLong(), id)
            }
        }
    }

    suspend fun sendGroupLocationMessage(id: String, lng: Float, lat: Float, poi: String) {
        withContext(Dispatchers.IO) {
            if (!ChatLib.wsIsOnline()) {
                ChatLib.wsOnline()
            }
            val groupChat = GroupDBManager.queryByGroupId(id)
            groupChat?.let {

                ChatLib.writeLocationGroupMessage(it.memberIdList, lng, lat, poi, id)
            }
        }
    }
}