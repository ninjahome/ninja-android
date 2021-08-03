package com.ninjahome.ninja.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ninjahome.ninja.model.bean.GroupInfo

@Dao
interface GroupDao {
    //查询user表中所有数据
    @Query("SELECT * FROM groupinfo")
    fun all(): LiveData<List<GroupInfo>?>

    @Query("SELECT * FROM groupinfo where groupId = :groupId")
    fun queryByGroupId(groupId: String): GroupInfo?

    @Query("SELECT * FROM groupinfo where groupId = :groupId")
    fun queryLiveDataByGroupId(groupId: String): LiveData<GroupInfo?>


    @Query("SELECT groupName FROM groupinfo where groupId = :groupId")
    fun queryGroupName(groupId: String): LiveData<String?>

    @Insert
    fun insert(group: GroupInfo)

    @Update
    fun updateAccounts(vararg group: GroupInfo)

    @Delete
    fun delete(vararg group: GroupInfo)

    @Query("DELETE FROM groupinfo")
    fun deleteAll()

}