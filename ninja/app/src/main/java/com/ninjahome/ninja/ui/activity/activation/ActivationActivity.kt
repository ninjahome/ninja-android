package com.ninjahome.ninja.ui.activity.activation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.lifecycle.observe
import androidx.lifecycle.rxLifeScope
import com.google.zxing.integration.android.IntentIntegrator
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityActivationBinding
import com.ninjahome.ninja.ui.activity.scan.ScanActivity
import com.ninjahome.ninja.utils.AccountUtils
import com.ninjahome.ninja.utils.DialogUtils
import com.ninjahome.ninja.utils.PermissionUtils
import com.ninjahome.ninja.viewmodel.ActivationViewModel
import kotlinx.android.synthetic.main.activity_activation.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

/**
 *Author:Mr'x
 *Time:2021/8/12
 *Description:
 */
class ActivationActivity : BaseActivity<ActivationViewModel, ActivityActivationBinding>(R.layout.activity_activation) {
    override val mViewModel: ActivationViewModel by viewModel()

    override fun initView() {
    }

    override fun initData() {
    }

    override fun initObserve() {
        mViewModel.importEvent.observe(this) {
            showImportDialog()
        }

        mViewModel.license.observe(this){
            slienseTv.text = it
        }
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

    private fun showImportDialog() {
        DialogUtils.showImportDialog(this) { position, _ ->
            if (DialogUtils.POSITION_ALBUM == position) {
                requestLocalMemoryPermission()
            } else if (DialogUtils.POSITION_CAMERA == position) {
                ScanActivity.start(this)
            }
        }
    }

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
            if (null == data || data.data == null) {
                return
            }
            val activationCode = AccountUtils.parseQRCodeFile(data.data!!, contentResolver)
            mViewModel.activationCode.value = activationCode
        } else {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                    ?: return
            if (result.contents == null) {
                return
            }
            try {
                mViewModel.activationCode.value = result.contents
               mViewModel.decodeLicense()
            } catch (ex: Exception) {
                toast(getString(R.string.import_qr_error))
            }
        }
    }



}