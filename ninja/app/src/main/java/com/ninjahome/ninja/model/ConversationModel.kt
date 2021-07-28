package com.ninjahome.ninja.model

import androidlib.Androidlib
import com.google.gson.JsonObject
import com.ninjahome.ninja.room.GroupDBManager
import com.ninjahome.ninja.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
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

    suspend fun sendImageMessage(uid: String, path: String, compress: Boolean) {
        withContext(Dispatchers.IO) {
            if (!Androidlib.wsIsOnline()) {
                Androidlib.wsOnline()
            }
            var imageFileSource: File? = null
            if (compress) {
                imageFileSource = ImageUtils.compressImage(path)
            }
            imageFileSource = imageFileSource ?: File(path)
            Androidlib.writeImageMessage(uid, File(imageFileSource.path).readBytes())
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


    suspend fun sendGroupTextMessage(id: String, data: String) {
        withContext(Dispatchers.IO) {
            if (!Androidlib.wsIsOnline()) {
                Androidlib.wsOnline()
            }
//            val ids = Androidlib.newSlice2Str()
//            val groupChat = GroupDBManager.queryByGroupId(id)
//            groupChat?.let {
//                val addresses = JSONArray(it.memberIdList)
//
//                for (index in 0..addresses.length()) {
//                    ids.add(addresses.get(index).toString())
//                }
//                Androidlib.writeGroupMessage(ids, id, data)
//            }

        }
    }


    suspend fun sendGroupImageMessage(id: String, path: String, compress: Boolean) {
        withContext(Dispatchers.IO) {
            if (!Androidlib.wsIsOnline()) {
                Androidlib.wsOnline()
            }
            var imageFileSource: File? = null
            if (compress) {
                imageFileSource = ImageUtils.compressImage(path)
            }
            imageFileSource = imageFileSource ?: File(path)
//            val ids = Androidlib.newSlice2Str()
//            val groupChat = GroupDBManager.queryByGroupId(id)
//            groupChat?.let {
//                val addresses = JSONArray(it.memberIdList)
//
//                for (index in 0..addresses.length()) {
//                    ids.add(addresses.get(index).toString())
//                }
//                Androidlib.writeImageGroupMessage(ids, File(imageFileSource.path).readBytes(), id)
//            }
        }

    }

    suspend fun sendGroupVoiceMessage(id: String, audioPath: String, duration: Int) {
        withContext(Dispatchers.IO) {
            if (!Androidlib.wsIsOnline()) {
                Androidlib.wsOnline()
            }
//            val ids = Androidlib.newSlice2Str()
//            val groupChat = GroupDBManager.queryByGroupId(id)
//            groupChat?.let {
//                val addresses = JSONArray(it.memberIdList)
//
//                for (index in 0..addresses.length()) {
//                    ids.add(addresses.get(index).toString())
//                }
//                Androidlib.writeVoiceGroupMessage(ids, File(audioPath).readBytes(), duration.toLong(), id)
//            }
        }
    }

    suspend fun sendGroupLocationMessage(id: String, lng: Float, lat: Float, poi: String) {
        withContext(Dispatchers.IO) {
            if (!Androidlib.wsIsOnline()) {
                Androidlib.wsOnline()
            }
//            val ids = Androidlib.newSlice2Str()
//            val groupChat = GroupDBManager.queryByGroupId(id)
//            groupChat?.let {
//                val addresses = JSONArray(it.memberIdList)
//
//                for (index in 0..addresses.length()) {
//                    ids.add(addresses.get(index).toString())
//                }
//                Androidlib.writeLocationGroupMessage(ids, lng, lat, poi,id)
//            }
        }
    }
}