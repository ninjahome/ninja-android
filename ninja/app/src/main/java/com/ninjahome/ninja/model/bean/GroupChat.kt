package com.ninjahome.ninja.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@Entity
data class GroupChat(@PrimaryKey(autoGenerate = true) var id: Int = 0, var groupId: String, var groupName: String, var owner: String, var memberIdList: String, var memberNickNameList: String)