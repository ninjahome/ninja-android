package com.ninjahome.ninja

import com.ninjahome.ninja.utils.FileUtils
import rxhttp.wrapper.annotation.DefaultDomain

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object Constants {
    val KEY_USER_NAME: String = "key_user_name"
    val KEY_OPEN_FINGERPRINT: String = "key_open_fingerprint"
    val KEY_DESTROY: String = "key_destroy"
    const val KEY_NINJA_BIOMETRIC = "key_ninja_biometric"
    const val KEY_ENCRYPTED_PASSWORD = "key_password"
    const val KEY_BIOMETRIC_PASSWORD = "key_biometric_password"
    const val KEY_BIOMETRIC_INITIALIZATION_VECTOR = "key_biometric_initialization_vector"

    const val CODE_OPEN_ALBUM = 100
    const val CODE_OPEN_CAMERA = 101
    const val CODE_LOCATION = 102

    const val RC_LOCAL_MEMORY_PERM = 123

    @DefaultDomain
    const val URL = "http://39.99.198.143:60998"
    const val TAG_NAME = "ninja"

    //语音存放位置
    val AUDIO_SAVE_DIR: String = FileUtils.getDir("audio")
    const val DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND = 60

    //视频存放位置
    val VIDEO_SAVE_DIR: String = FileUtils.getDir("video")

    //照片存放位置
    val PHOTO_SAVE_DIR: String = FileUtils.getDir("photo")

    //头像保存位置
    val HEADER_SAVE_DIR: String = FileUtils.getDir("header")
}