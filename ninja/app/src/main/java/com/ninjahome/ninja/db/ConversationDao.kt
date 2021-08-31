package com.ninjahome.ninja.db

import androidx.room.*
import com.ninjahome.ninja.model.bean.Conversation
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {
    //查询user表中所有数据
    @Query("SELECT * FROM conversation order by time desc")
    fun all(): Flow<List<Conversation>?>

    @Query("SELECT * FROM conversation where `from` = :from")
    fun queryByFrom(from: String): Conversation?

    @Query("SELECT * FROM conversation where  groupId = :groupId")
    fun queryByGroupId(groupId: String): Conversation?

    @Query("SELECT * FROM conversation where id = :id")
    fun queryByID(id: Long): Conversation?

    @Insert
    fun insert(conversation: Conversation): Long

    @Update
    fun updateConversations(vararg conversation: Conversation)

    @Delete
    fun delete(vararg conversation: Conversation)

    @Query("DELETE FROM conversation")
    fun deleteAll()

    @Query("UPDATE  conversation SET msg = '' where unreadCount = 0")
    fun deleteReadConversation()


    @Query("UPDATE  conversation SET unreadCount = 0")
    fun clearUnreadNumber()

}