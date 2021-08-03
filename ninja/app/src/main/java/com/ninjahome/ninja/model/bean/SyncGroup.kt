package com.ninjahome.ninja.model.bean

import com.squareup.moshi.Json

/**
 *Author:Mr'x
 *Time:2021/8/3
 *Description:
 */
class SyncGroup(@Json(name = "group_id") var groupId: String, @Json(name = "group_name") var groupName: String, @Json(name = "owner_id") var ownerId: String, @Json(name = "ban_talking") var banTalking: Boolean, @Json(name = "member_id") var memberIds: MutableList<String>, @Json(name = "nick_name") var nickNames: MutableList<String>)