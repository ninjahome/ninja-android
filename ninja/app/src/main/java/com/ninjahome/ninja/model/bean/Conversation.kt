package com.ninjahome.ninja.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@Entity
class Conversation(@PrimaryKey(autoGenerate = true) var id: Long = 0, var from: String, var isGroup: Boolean, var msg: String, var time: Long, var unreadCount: Int, var title: String = "", var groupId: String = "")