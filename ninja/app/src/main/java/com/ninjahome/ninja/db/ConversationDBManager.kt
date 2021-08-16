package com.ninjahome.ninja.db

import androidx.lifecycle.LiveData
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.model.bean.Conversation
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object ConversationDBManager {
    private val conversationDao: ConversationDao by lazy { NinjaDB.getInstance(context()).conversationDao() }

    fun all(): Flow<List<Conversation>?> {
        return conversationDao.all()
    }

    suspend fun insert(conversation: Conversation): Long {
        return withContext(Dispatchers.IO) {
            return@withContext conversationDao.insert(conversation)
        }
    }

    suspend fun queryByFrom(from: String): Conversation? {
        return withContext(Dispatchers.IO) {
            return@withContext conversationDao.queryByFrom(from)
        }
    }

    suspend fun queryByGroupId(groupId: String): Conversation? {
        return withContext(Dispatchers.IO) {
            return@withContext conversationDao.queryByGroupId(groupId)
        }
    }

    suspend fun updateConversations(vararg conversation: Conversation) {
        withContext(Dispatchers.IO) {
            conversationDao.updateConversations(*conversation)
        }
    }

    suspend fun delete(vararg conversation: Conversation) {
        withContext(Dispatchers.IO) {
            conversationDao.delete(*conversation)
        }
    }


    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            conversationDao.deleteAll()
        }
    }

    suspend fun deleteReadConversation() {
        withContext(Dispatchers.IO) {
            conversationDao.deleteReadConversation()
        }
    }
}