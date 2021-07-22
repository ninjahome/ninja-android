package com.ninjahome.ninja.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ninjahome.ninja.model.bean.Message

@Dao
interface MessageDao {

    @Query("SELECT * FROM message ")
    fun all(): LiveData<List<Message>?>

    @Query("SELECT * FROM message where conversationId = :conversationId order by time")
    fun queryByConversationId(conversationId: Long): LiveData<List<Message>?>

    @Query("SELECT count(*) FROM message where conversationId = :conversationId and unRead = 1")
    fun queryUnReadCount(conversationId: Long): Int

    @Query("UPDATE message SET unRead = 0 where conversationId = :conversationId ")
    fun updateMessage2Read(conversationId: Long)

    @Insert
    fun insert(msg: Message): Long

    @Update
    fun updateMessage(vararg msg: Message)

    @Delete
    fun delete(vararg msg: Message)

    @Query("DELETE FROM message")
    fun deleteAll()

    @Query("DELETE FROM message where unRead = 0")
    fun deleteAllReadMessage()

}