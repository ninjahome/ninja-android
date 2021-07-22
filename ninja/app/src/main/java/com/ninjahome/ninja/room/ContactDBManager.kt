package com.ninjahome.ninja.room

import androidx.lifecycle.LiveData
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.model.bean.Contact
import kotlinx.coroutines.*

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object ContactDBManager {
    private val contactDao: ContactDao by lazy { NinjaDB.getInstance(context()).contactDao() }

    fun all(): LiveData<List<Contact>?> {
        return contactDao.all()
    }

    suspend fun queryByID(uid: String): Contact? {
        return withContext(Dispatchers.IO) {
            return@withContext contactDao.queryByUID(uid)
        }

    }

    suspend fun queryNickNameByUID(uid: String): String? {
        return withContext(Dispatchers.IO) {
            return@withContext contactDao.queryNickNameByUID(uid)
        }

    }

    fun observeNickNameByUID(uid: String): LiveData<String?> {
        return contactDao.observeNickNameByUID(uid)
    }

    suspend fun insert(contact: Contact) {
        withContext(Dispatchers.IO) {
            contactDao.insert(contact)
        }
    }

    suspend fun updateAccounts(vararg contact: Contact) {
        withContext(Dispatchers.IO) {
            contactDao.updateAccounts(*contact)
        }

    }

    suspend fun delete(vararg contact: Contact) {
        withContext(Dispatchers.IO) {
            contactDao.delete(*contact)
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            contactDao.deleteAll()
        }
    }
}