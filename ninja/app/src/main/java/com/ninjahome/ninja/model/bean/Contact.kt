package com.ninjahome.ninja.model.bean

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@Entity
@Parcelize
data class Contact(@PrimaryKey(autoGenerate = true) var id: Int = 0, var avatar: String = "", var nickName: String = "", var owner: String = "", var remark: String = "", var uid: String = "", @Ignore var indexTag: String = "") : Parcelable