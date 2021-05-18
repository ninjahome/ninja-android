package com.ninjahome.ninja

import rxhttp.wrapper.annotation.DefaultDomain

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object Constants {
    val KEY_USER_NAME: String = "key_user_name"
    val KEY_OPEN_FINGERPRINT: String = "key_open_fingerprint"
    const val KEY_NINJA_BIOMETRIC = "key_ninja_biometric"
    const val KEY_ENCRYPTED_PASSWORD = "key_password"
    const val KEY_BIOMETRIC_PASSWORD = "key_biometric_password"
    const val KEY_BIOMETRIC_INITIALIZATION_VECTOR = "key_biometric_initialization_vector"

    const val CODE_OPEN_ALBUM = 100
    const val CODE_OPEN_CAMERA = 101

    @DefaultDomain
    const val URL = "http://39.99.198.143:60998"
    const val TAG_NAME = "ninja"
}