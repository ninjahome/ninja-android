package com.ninjahome.ninja.ui.activity.accountmanager

import android.Manifest
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityAccountManagerBinding
import com.ninjahome.ninja.utils.PermissionUtils
import com.ninjahome.ninja.viewmodel.AccountManagerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class AccountManagerActivity : BaseActivity<AccountManagerViewModel, ActivityAccountManagerBinding>(R.layout.activity_account_manager) {
    override val mViewModel: AccountManagerViewModel by viewModel()

    override fun initView() {
        mViewModel.title.set(getString(R.string.my_account_manager))
    }

    override fun initData() {
    }

    override fun initObserve() {
        mViewModel.requestLocalPermissionEvent.observe(this, {
            requestLocalMemoryPermission()
        })
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel


    @AfterPermissionGranted(Constants.CODE_OPEN_ALBUM)
    fun requestLocalMemoryPermission() {
        if (PermissionUtils.hasStoragePermission(this)) {
            mViewModel.saveAccount()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.import_apply_album_permission), Constants.CODE_OPEN_ALBUM, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}