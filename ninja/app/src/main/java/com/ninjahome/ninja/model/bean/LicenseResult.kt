package com.ninjahome.ninja.model.bean

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 *Author:Mr'x
 *Time:2021/8/18
 *Description:
 */
@JsonClass(generateAdapter = true)
class LicenseResult(@Json(name = "result_code")val resultCode:Int, @Json(name="result_message")val resultMessage:String, val tx:String)
enum class LicenseResultCode(val value:Int){
    SUCCESS(0),ParseJsonErr(1),ConnectionErr(2),CallContractErr(3),OtherErr(4)
}