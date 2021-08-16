package com.ninjahome.ninja.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.model.bean.Conversation
import com.ninjahome.ninja.model.bean.GroupInfo
import com.ninjahome.ninja.model.bean.Message

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@Database(entities = [Contact::class, Message::class, Conversation::class, GroupInfo::class], version = 1, exportSchema = false)
abstract class NinjaDB : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao
    abstract fun groupDao(): GroupDao

    companion object {
        private var instance: NinjaDB? = null
        fun getInstance(context: Context): NinjaDB {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, NinjaDB::class.java, "ninja.db" //数据库名称
                ).allowMainThreadQueries().build()
            }
            return instance as NinjaDB
        }
    }
}