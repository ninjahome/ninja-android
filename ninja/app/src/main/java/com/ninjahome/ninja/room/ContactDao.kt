package com.ninjahome.ninja.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ninjahome.ninja.model.bean.Contact

@Dao
interface ContactDao {
    //查询user表中所有数据
    @Query("SELECT * FROM contact")
    fun all(): LiveData<List<Contact>?>

    @Query("SELECT * FROM contact where uid = :uid")
    fun queryByUID(uid: String): Contact?

    @Query("SELECT nickName FROM contact where uid = :uid")
    fun queryTitleByUID(uid: String): String?

    @Query("SELECT nickName FROM contact where uid = :uid")
    fun observeNickNameByUID(uid: String): LiveData<String?>

    @Insert
    fun insert(account: Contact)

    @Update
    fun updateAccounts(vararg account: Contact)

    @Delete
    fun delete(vararg account: Contact)

    @Query("DELETE FROM contact")
    fun deleteAll()

}