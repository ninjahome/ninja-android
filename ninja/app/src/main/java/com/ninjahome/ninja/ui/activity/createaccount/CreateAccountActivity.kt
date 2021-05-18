package com.ninjahome.ninja.ui.activity.createaccount

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import com.google.zxing.integration.android.IntentIntegrator
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityCreateAccountBinding
import com.ninjahome.ninja.ui.activity.scan.ScanActivity
import com.ninjahome.ninja.utils.AccountUtils
import com.ninjahome.ninja.utils.DialogUtils
import com.ninjahome.ninja.utils.PermissionUtils
import com.ninjahome.ninja.view.PasswordPop
import com.ninjahome.ninja.viewmodel.CreateAccountViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class CreateAccountActivity : BaseActivity<CreateAccountViewModel, ActivityCreateAccountBinding>(R.layout.activity_create_account) {
    override val mViewModel: CreateAccountViewModel by viewModel()
    override fun initView() {
    }

    override fun initData() {
        val showImportDialog = intent.getBooleanExtra(IntentKey.SHOW_IMPORT_DIALOG, false)
        if (showImportDialog) {
            showImportDialog()
        }
    }

    override fun initObserve() {
        mViewModel.showImportDialog.observe(this, {
            showImportDialog()
        })
    }

    private fun showImportDialog() {
        DialogUtils.showImportDialog(this) { position, _ ->
            if (DialogUtils.POSITION_ALBUM == position) {
                requestLocalMemoryPermission()
            } else if (DialogUtils.POSITION_CAMERA == position) {
                ScanActivity.start(this)
            }
        }
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

    @AfterPermissionGranted(Constants.CODE_OPEN_ALBUM)
    fun requestLocalMemoryPermission() {
        if (PermissionUtils.hasStoragePermission(this)) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, Constants.CODE_OPEN_ALBUM)
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.import_apply_album_permission), Constants.CODE_OPEN_ALBUM, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (Constants.CODE_OPEN_ALBUM == requestCode) {
            if (null == data) {
                return
            }
            loadIdCardFromUri(data.data)
        } else {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                    ?: return
            if (result.contents == null) {
                return
            }
            try {
                val walletStr = result.contents
                showPasswordDialog(walletStr)
            } catch (ex: Exception) {
                toast(getString(R.string.import_qr_error))
            }
        }
    }

    private fun loadIdCardFromUri(uri: Uri?) {
        if (null == uri) {
            toast(getString(R.string.import_qr_error))
            return
        }
        try {
            val accountStr = AccountUtils.parseQRCodeFile(uri, contentResolver)
            showPasswordDialog(accountStr)
        } catch (e: Exception) {
            toast(getString(R.string.import_qr_error))
            e.printStackTrace()
        }
    }

    private fun showPasswordDialog(accountJson: String) {
        DialogUtils.showPasswordDialog(this, object : PasswordPop.InputPasswordListener {
            override fun input(password: String) {
                mViewModel.importAccount(accountJson, password)
            }

        })
    }

}