package com.ninjahome.ninja.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ninjahome.ninja.model.bean.GroupChat

@Dao
interface GroupDao {
    //查询user表中所有数据
    @Query("SELECT * FROM groupchat")
    fun all(): LiveData<List<GroupChat>?>

    @Query("SELECT * FROM groupchat where groupId = :groupId")
    fun queryByGroupId(groupId: String): GroupChat?

    @Insert
    fun insert(group: GroupChat)

    @Update
    fun updateAccounts(vararg group: GroupChat)

    @Delete
    fun delete(vararg group: GroupChat)

    @Query("DELETE FROM groupchat")
    fun deleteAll()

}