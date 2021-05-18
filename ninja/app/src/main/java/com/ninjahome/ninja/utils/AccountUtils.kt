package com.ninjahome.ninja.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.model.bean.Account
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object AccountUtils {
    var spec: KeyGenParameterSpec = KeyGenParameterSpec.Builder("_androidx_security_master_key_", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setKeySize(256).build()
    private val masterKeyAlias = MasterKey.Builder(context()).setKeyGenParameterSpec(spec).build()

    fun getAccountPath(context: Context): String {
        return context.filesDir.absolutePath + "/wallet.json"
    }

    fun saveAccountToPath(path: String, data: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
        MainScope().launch {
            withContext(Dispatchers.IO) {
                val encryptedFile = getEncryptedFile(path)
                saveAccountToFile(encryptedFile, data)
            }
        }

    }

    private fun saveAccountToFile(encryptedFile: EncryptedFile, data: String) {
        try {
            encryptedFile.openFileOutput().apply {
                write(data.toByteArray(Charset.forName("UTF-8")))
                flush()
                close()
            }
        } catch (e: IOException) {
            Logger.d(e.message)
        }
    }

    suspend fun getAddress(context: Context): String? {
        val accountPath = getAccountPath(context)
        return loadAccountByPath(accountPath)?.address
    }


    suspend fun loadAccountByPath(path: String): Account? {
        return withContext(Dispatchers.IO) {
            val accountJson = loadAccountJsonByPath(path)
            Logger.d(accountJson)
            return@withContext JsonUtils.json2Object(accountJson, Account::class.java)
        }

    }


    suspend fun loadAccountJsonByPath(path: String): String {
        return withContext(Dispatchers.IO) {
            return@withContext loadAccountByFile(getEncryptedFile(path))
        }

    }

    private fun getEncryptedFile(path: String): EncryptedFile {
        val file = File(path)
        return EncryptedFile.Builder(context(), file, masterKeyAlias, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB).build()
    }

    private fun loadAccountByFile(encryptedFile: EncryptedFile): String {
        return encryptedFile.openFileInput().bufferedReader().readText()
    }


    fun hasAccount(path: String): Boolean {
        return File(path).exists()
    }


    @Throws(Exception::class)
    fun parseQRCodeFile(uri: Uri, cr: ContentResolver): String {
        val bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri))
        return parseQRcodeFromBitmap(bitmap)
    }

    @Throws(Exception::class)
    private fun parseQRcodeFromBitmap(bitmap: Bitmap): String {
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source: LuminanceSource = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        val bb = BinaryBitmap(HybridBinarizer(source))
        val hints: MutableMap<DecodeHintType, Any?> = LinkedHashMap()
        hints[DecodeHintType.PURE_BARCODE] = java.lang.Boolean.TRUE
        val reader: Reader = MultiFormatReader()
        val r = reader.decode(bb, hints)
        return r.text
    }

    fun copyToMemory(context: Context, src: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("id", src)
        clipboard.setPrimaryClip(clip)
    }

}