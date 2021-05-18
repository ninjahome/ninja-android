package com.ninjahome.ninja.model.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@JsonClass(generateAdapter = true)
data class UnreadMessageItem(@Json(name = "From") val from: String, @Json(name = "PayLoad") val payLoad: String, @Json(name = "To") val to: String, @Json(name = "UnixTime") val unixTime: Int)