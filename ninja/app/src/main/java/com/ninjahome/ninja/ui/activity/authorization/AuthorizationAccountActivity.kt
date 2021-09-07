package com.ninjahome.ninja.ui.activity.authorization

import android.app.Activity
import android.content.Intent
import com.google.zxing.integration.android.IntentIntegrator
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityAuthorizationAccountBinding
import com.ninjahome.ninja.ui.activity.scan.ScanActivity
import com.ninjahome.ninja.viewmodel.AuthorizationAccountViewModel
import kotlinx.android.synthetic.main.activity_authorization_account.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:2021/9/6
 *Description:
 */
class AuthorizationAccountActivity : BaseActivity<AuthorizationAccountViewModel, ActivityAuthorizationAccountBinding>(R.layout.activity_authorization_account) {
    override val mViewModel: AuthorizationAccountViewModel by viewModel()

    override fun initView() {
        mViewModel.title.set(getString(R.string.authorization_friends))
    }

    override fun initData() {}


    override fun initObserve() {
        mViewModel.scan.observe(this) {
            ScanActivity.start(this)
        }
        mViewModel.enable.observe(this) {
            authorizationrBtn.isEnabled = it
        }

        mViewModel.expireTime.observe(this) {
            val allDays = String.format(getString(R.string.authorization_remaining_activation_days), mViewModel.getAllDays())
            remainingActivationDaysTv.text = allDays
        }
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

    override fun onResume() {
        super.onResume()
        mViewModel.getExpireTime()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data) ?: return
        if (result.contents == null) {
            return
        }
        try {
            mViewModel.address.value = result.contents
        } catch (ex: Exception) {
            toast(getString(R.string.import_qr_error))
        }
    }

}