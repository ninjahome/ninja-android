package com.ninjahome.ninja.db

import androidx.lifecycle.LiveData
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.model.bean.GroupInfo
import kotlinx.coroutines.*

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object GroupDBManager {
    private val groupDao: GroupDao by lazy { NinjaDB.getInstance(context()).groupDao() }

    fun all(): LiveData<List<GroupInfo>?> {
        return groupDao.all()
    }

    suspend fun queryByGroupId(groupId: String): GroupInfo? {
        return withContext(Dispatchers.IO) {
            return@withContext groupDao.queryByGroupId(groupId)
        }
    }

    suspend fun queryLiveDataByGroupId(groupId: String): LiveData<GroupInfo?> {
        return withContext(Dispatchers.IO) {
            return@withContext groupDao.queryLiveDataByGroupId(groupId)
        }
    }

    suspend fun queryGroupName(groupId: String): LiveData<String?> {
        return withContext(Dispatchers.IO) {
            return@withContext groupDao.queryGroupName(groupId)
        }
    }


    suspend fun insert(group: GroupInfo) {
        withContext(Dispatchers.IO) {
            groupDao.insert(group)
        }
    }

    suspend fun updateGroup(vararg group: GroupInfo) {
        withContext(Dispatchers.IO) {
            groupDao.updateAccounts(*group)
        }

    }

    suspend fun delete(vararg group: GroupInfo) {
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