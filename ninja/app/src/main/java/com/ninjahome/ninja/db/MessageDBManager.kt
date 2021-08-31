package com.ninjahome.ninja.db

import androidx.lifecycle.LiveData
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.model.bean.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object MessageDBManager {
    private val msgDao: MessageDao by lazy { NinjaDB.getInstance(context()).messageDao() }

    fun all(): LiveData<List<Message>?> {
        return msgDao.all()
    }

    suspend fun insert(msg: Message): Long {
        return withContext(Dispatchers.IO) {
            return@withContext msgDao.insert(msg)
        }
    }

    suspend fun updateMessage(vararg msg: Message) {
        withContext(Dispatchers.IO) {
            msgDao.updateMessage(*msg)
        }
    }

    suspend fun queryByConversationId(conversationId: Long): Flow<List<Message>?> {
        return withContext(Dispatchers.IO) {
            return@withContext msgDao.queryByConversationId(conversationId)
        }
    }

    suspend fun queryUnReadCount(conversationId: Long): Int {
        return withContext(Dispatchers.IO) {
            return@withContext msgDao.queryUnReadCount(conversationId)
        }
    }

    suspend fun queryReadMessage(): List<Message> {
        return withContext(Dispatchers.IO) {
            return@withContext msgDao.queryReadMessage()
        }
    }

    suspend fun updateMessage2Read(conversationId: Long) {
        withContext(Dispatchers.IO) {
            msgDao.updateMessage2Read(conversationId)
        }
    }

    suspend fun updateAllMessage2Read() {
        withContext(Dispatchers.IO) {
            msgDao.updateAllMessage2Read()
        }
    }

    suspend fun delete(vararg msg: Message) {
        withContext(Dispatchers.IO) {
            msgDao.delete(*msg)
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            msgDao.deleteAll()
        }
    }

    suspend fun deleteAllReadMessage() {
        withContext(Dispatchers.IO) {
            msgDao.deleteAllReadMessage()
        }
    }
}