package com.ninjahome.ninja.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ninjahome.ninja.model.bean.Conversation

@Dao
interface ConversationDao {
    //查询user表中所有数据
    @Query("SELECT * FROM conversation order by time desc")
    fun all(): LiveData<List<Conversation>?>

    @Query("SELECT * FROM conversation where `from` = :from")
    fun queryByFrom(from: String): Conversation?

    @Query("SELECT * FROM conversation where id = :id")
    fun queryByID(id: Long): Conversation?

    @Insert
    fun insert(conversation: Conversation):Long

    @Update
    fun updateConversations(vararg conversation: Conversation)

    @Delete
    fun delete(vararg conversation: Conversation)

    @Query("DELETE FROM conversation")
    fun deleteAll()

    @Query("DELETE FROM conversation where unreadCount = 0")
    fun deleteReadConversation()

}