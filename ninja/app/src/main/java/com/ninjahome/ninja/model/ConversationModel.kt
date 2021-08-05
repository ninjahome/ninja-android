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

    suspend fun sendImageMessage(uid: String, data: ByteArray) {
        withContext(Dispatchers.IO) {
            if (!ChatLib.wsIsOnline()) {
                ChatLib.wsOnline()
            }
            ChatLib.writeImageMessage(uid, data)
        }
    }

    suspend fun sendVoiceMessage(uid: String, data: ByteArray, duration: Int) {
        withContext(Dispatchers.IO) {
            if (!ChatLib.wsIsOnline()) {
                ChatLib.wsOnline()
            }

            ChatLib.writeVoiceMessage(uid, data, duration.toLong())
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


    suspend fun sendGroupImageMessage(id: String, data: ByteArray) {
        withContext(Dispatchers.IO) {
            if (!ChatLib.wsIsOnline()) {
                ChatLib.wsOnline()
            }

            val groupChat = GroupDBManager.queryByGroupId(id)
            groupChat?.let {
                ChatLib.writeImageGroupMessage(it.memberIdList, data, id)
            }
        }

    }

    suspend fun sendGroupVoiceMessage(id: String, data: ByteArray, duration: Int) {
        withContext(Dispatchers.IO) {
            if (!ChatLib.wsIsOnline()) {
                ChatLib.wsOnline()
            }
            val groupChat = GroupDBManager.queryByGroupId(id)
            groupChat?.let {
                ChatLib.writeVoiceGroupMessage(it.memberIdList, data, duration.toLong(), id)
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