package com.ninjahome.ninja.model

import android.text.TextUtils
import chatLib.ChatLib
import com.ninjahome.ninja.model.bean.LicenseResult
import com.ninjahome.ninja.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 *Author:Mr'x
 *Time:2021/8/18
 *Description:
 */
class ActivationModel {
    suspend fun decodeLicense(slicense: String): String {
        return withContext(Dispatchers.IO) {
            return@withContext ChatLib.decodeLicense(slicense)
        }
    }

    suspend fun isValidLicense(slicense: String): Long {
        return withContext(Dispatchers.IO) {
            return@withContext ChatLib.isValidLicense(slicense)
        }
    }

    suspend fun importLicense(slicense: String): LicenseResult? {
        return withContext(Dispatchers.IO) {
            val result = ChatLib.importLicense(slicense)
            delay(5000)
            if (TextUtils.isEmpty(result)) {
                return@withContext null
            } else {
                val license = result.fromJson<LicenseResult>()
                return@withContext license
            }
        }
    }
}