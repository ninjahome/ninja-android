package com.ninjahome.ninja.room

import androidx.room.*
import com.ninjahome.ninja.model.bean.Contact

@Dao
interface ContactDao {
    //查询user表中所有数据
    @get:Query("SELECT * FROM contact")
    val all: List<Contact>?

    @Query("SELECT * FROM contact where uid = :uid")
    fun queryByUID(uid: String): Contact?

    @Query("SELECT nickName FROM contact where uid = :uid")
    fun queryNickNameByUID(uid: String): String

    @Insert
    fun insert(account: Contact)

    @Update
    fun updateAccounts(vararg account: Contact)

    @Delete
    fun delete(vararg account: Contact)

}