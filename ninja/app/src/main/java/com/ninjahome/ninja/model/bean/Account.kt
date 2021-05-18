package com.ninjahome.ninja.model.bean

import com.squareup.moshi.JsonClass

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@JsonClass(generateAdapter = true)
data class Account(val address: String, val crypto: Crypto, val id: String, val version: Int)

@JsonClass(generateAdapter = true)
data class Crypto(val cipher: String, val cipherParams: CipherParams, val ciphertext: String, val kdf: String, val kdfParams: KdfParams, val mac: String)

@JsonClass(generateAdapter = true)
data class CipherParams(val iv: String)

@JsonClass(generateAdapter = true)
data class KdfParams(val dklen: Int, val n: Int, val p: Int, val r: Int, val salt: String)