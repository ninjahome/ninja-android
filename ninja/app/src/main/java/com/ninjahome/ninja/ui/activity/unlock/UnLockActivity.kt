package com.ninjahome.ninja.ui.activity.unlock

import android.text.TextUtils
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityUnlockBinding
import com.ninjahome.ninja.db.ConversationDBManager
import com.ninjahome.ninja.db.MessageDBManager
import com.ninjahome.ninja.utils.CryptographyManager
import com.ninjahome.ninja.utils.EncryptedPreferencesUtils
import com.ninjahome.ninja.utils.StringUtils
import com.ninjahome.ninja.viewmodel.UnLockViewModel
import com.orhanobut.logger.Logger
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class UnLockActivity : BaseActivity<UnLockViewModel, ActivityUnlockBinding>(R.layout.activity_unlock) {


    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var cryptographyManager: CryptographyManager

    override val mViewModel: UnLockViewModel by viewModel()


    override fun initView() {

    }

    override fun initData() {
        clearReadMessage()
        mViewModel.loadAccount()
        if (mViewModel.openFingerPrint) {
            cryptographyManager = CryptographyManager()
            val encryptedPreference = EncryptedPreferencesUtils(this)
            val encryptedPassword = encryptedPreference.getString(Constants.KEY_BIOMETRIC_PASSWORD, "")
            val encryptedVector = encryptedPreference.getString(Constants.KEY_BIOMETRIC_INITIALIZATION_VECTOR, "")
            biometricPrompt = createBiometricPrompt(encryptedPassword)
            promptInfo = createPromptInfo()
            try {
                val cipher = cryptographyManager.getInitializedCipherForDecryption(Constants.KEY_NINJA_BIOMETRIC, StringUtils.hexStringToByte(encryptedVector))
                biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            } catch (e: Exception) {
                Logger.d("initData: ${e.message}")
                mViewModel.openFingerPrint = false

            }

        }
    }
    private fun clearReadMessage() {
        rxLifeScope.launch{

            val readMessages = MessageDBManager.queryReadMessage()
            var file: File
            readMessages.forEach {
                if(!TextUtils.isEmpty(it.uri)){
                    file = File(it.uri)
                    if(file.exists()){
                        file.delete()
                    }
                }
            }
            MessageDBManager.deleteAllReadMessage()
            ConversationDBManager.deleteReadConversation()
        }
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

    private fun createBiometricPrompt(encryptedPassword: String): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                toast(errString.toString())
                Logger.d("$errorCode :: $errString")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Logger.d("Authentication failed for an unknown reason")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Logger.d("Authentication was successful")
                processData(encryptedPassword, result.cryptoObject)
            }

        }

        return BiometricPrompt(this, executor, callback)
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder().setTitle(getString(R.string.fingerprint_recognition_title)).setSubtitle(getString(R.string.fingerprint_recognition_subtitle)).setConfirmationRequired(false).setNegativeButtonText(getString(R.string.fingerprint_recognition_use_password)).build()
    }


    private fun processData(encryptedPassword: String, cryptoObject: BiometricPrompt.CryptoObject?) {
        val password = cryptographyManager.decryptData(StringUtils.hexStringToByte(encryptedPassword), cryptoObject?.cipher!!)
        mViewModel.password.value = password
        mViewModel.clickUnlock.execute()
    }

    override fun cancelJob() {
        super.cancelJob()
        mViewModel.jobs.forEach { it.cancel() }
    }
}