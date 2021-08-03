package com.ninjahome.ninja.model.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@Entity
@Parcelize
data class GroupInfo(@PrimaryKey(autoGenerate = true) var id: Int = 0, var groupId: String, var groupName: String, var owner: String, var memberIdList: String, var memberNickNameList: String, val banTalking: Boolean = false) : Parcelable