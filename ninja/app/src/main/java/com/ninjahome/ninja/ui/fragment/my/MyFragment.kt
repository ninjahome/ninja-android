package com.ninjahome.ninja.ui.fragment.my

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.components.SimpleImmersionOwner
import com.gyf.immersionbar.components.SimpleImmersionProxy
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.ninja.android.lib.base.BaseFragment
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.FragmentMyBinding
import com.ninjahome.ninja.ui.activity.showidqrcode.ShowIDQRCodeActivity
import com.ninjahome.ninja.utils.*
import com.ninjahome.ninja.view.PasswordPop
import com.ninjahome.ninja.viewmodel.MyViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_my.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class MyFragment : BaseFragment<MyViewModel, FragmentMyBinding>(R.layout.fragment_my) {

    private lateinit var passwordDialog: BasePopupView
    val biometricManager: BiometricManager by lazy { BiometricManager.from(mActivity) }
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var cryptographyManager: CryptographyManager

    override val mViewModel: MyViewModel by viewModel()
    override fun initView() {
        versionTv.text = String.format(getString(R.string.version), UIUtils.getVersion(mActivity))
    }

    override fun initData() {
    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
        mViewModel.showIDQR.observe(this, {
            showIDQRCode()
        })

        mViewModel.fingerPrintEvent.observe(this, object : Observer<Boolean> {
            override fun onChanged(open: Boolean?) {
                val status = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                if (status == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE || status == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
                    toast(getString(R.string.my_no_support_fingerprint))
                    mViewModel.openFingerPrintObservable.set(false)
                    return
                }
                if (status == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
                    showStartFingerPrintsDialog()
                    mViewModel.openFingerPrintObservable.set(false)
                    return
                }
                showPasswordDialog()
            }
        })


        mViewModel.showFingerPrintDialogEvent.observe(this, { password ->
            cryptographyManager = CryptographyManager()
            biometricPrompt = createBiometricPrompt(password)
            promptInfo = createPromptInfo()
            try {
                val cipher = cryptographyManager.getInitializedCipherForEncryption(Constants.KEY_NINJA_BIOMETRIC)
                biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            } catch (e: Exception) {

            }

        })

        mViewModel.dismissPasswordDialogEvent.observe(this, {
            if (this::passwordDialog.isInitialized && passwordDialog.isShow) {
                passwordDialog.dismiss()
            }
        })

    }

    private fun showPasswordDialog() {
        passwordDialog = DialogUtils.showPasswordDialog(mActivity, object : PasswordPop.InputPasswordListener {
            override fun input(password: String) {
                mViewModel.openAccount(password)
            }

        }, object : SimpleCallback() {
            override fun onBackPressed(popupView: BasePopupView?): Boolean {
                mViewModel.openFingerPrintObservable.set(false)
                return false
            }
        })
    }

    private fun showStartFingerPrintsDialog() {
        DialogUtils.showStartFingerPrintsDialog(mActivity, {
            startFingerPrintsSetting()
        }, {

        })
    }

    private fun startFingerPrintsSetting() {
        val pcgName = "com.android.settings"
        val clsName = "com.android.settings.Settings"
        val intent = Intent()
        val componentName = ComponentName(pcgName, clsName)
        intent.action = Intent.ACTION_VIEW
        intent.component = componentName
        startActivity(intent)
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder().setTitle(getString(R.string.fingerprint_recognition_title)).setSubtitle(getString(R.string.fingerprint_recognition_subtitle)).setConfirmationRequired(false).setNegativeButtonText(getString(R.string.cancel)).build()
    }

    private fun createBiometricPrompt(password: String): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(mActivity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Logger.d("$errorCode :: $errString")
                toast(errString.toString())
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON || errorCode == BiometricPrompt.ERROR_USER_CANCELED || errorCode == BiometricPrompt.ERROR_LOCKOUT) {
                    mViewModel.openFingerPrintObservable.set(false)
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Logger.d("Authentication failed for an unknown reason")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Logger.d("Authentication was successful")
                mViewModel.openFingerPrint = true
                processData(password, result.cryptoObject)
            }
        }

        return BiometricPrompt(this, executor, callback)
    }


    private fun processData(password: String, cryptoObject: BiometricPrompt.CryptoObject?) {
        val encryptedData = cryptographyManager.encryptData(password, cryptoObject?.cipher!!)
        val encryptedPreference = EncryptedPreferencesUtils(mActivity)
        encryptedPreference.putString(Constants.KEY_BIOMETRIC_PASSWORD, StringUtils.bytesToHexString(encryptedData.ciphertext))
        encryptedPreference.putString(Constants.KEY_BIOMETRIC_INITIALIZATION_VECTOR, StringUtils.bytesToHexString(encryptedData.initializationVector))
    }


    private fun showIDQRCode() {
        val intent = Intent(mActivity, ShowIDQRCodeActivity::class.java)
        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, idQRIv, "image").toBundle()
        intent.putExtra(IntentKey.ADDRESS, mViewModel.id.value)
        startActivity(intent, bundle)
    }


    override fun onResume() {
        super.onResume()
        mViewModel.setValue()
    }

}