package com.ninjahome.ninja.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ninjahome.ninja.model.bean.Contact

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class ContactDB : RoomDatabase() {
    abstract fun contactDao(): ContactDao

    companion object {
        private var instance: ContactDB? = null
        fun getInstance(context: Context): ContactDB {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, ContactDB::class.java, "contact.db" //数据库名称
                ).allowMainThreadQueries().build()
            }
            return instance as ContactDB
        }
    }
}