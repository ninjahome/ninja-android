package com.ninjahome.ninja.room

import androidx.lifecycle.LiveData
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.model.bean.GroupChat
import kotlinx.coroutines.*

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object GroupDBManager {
    private val groupDao: GroupDao by lazy { NinjaDB.getInstance(context()).groupDao() }

    fun all(): LiveData<List<GroupChat>?> {
        return groupDao.all()
    }

    suspend fun queryByGroupId(groupId: String): GroupChat? {
        return withContext(Dispatchers.IO) {
            return@withContext groupDao.queryByGroupId(groupId)
        }

    }


    suspend fun insert(group: GroupChat) {
        withContext(Dispatchers.IO) {
            groupDao.insert(group)
        }
    }

    suspend fun updateGroup(vararg group: GroupChat) {
        withContext(Dispatchers.IO) {
            groupDao.updateAccounts(*group)
        }

    }

    suspend fun delete(vararg group: GroupChat) {
        withContext(Dispatchers.IO) {
            groupDao.delete(*group)
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            groupDao.deleteAll()
        }
    }
}