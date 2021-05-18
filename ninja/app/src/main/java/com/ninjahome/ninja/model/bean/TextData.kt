package com.ninjahome.ninja.model.bean

import com.squareup.moshi.JsonClass

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@JsonClass(generateAdapter = true)
data class TextData(var data: String, var type: Int)