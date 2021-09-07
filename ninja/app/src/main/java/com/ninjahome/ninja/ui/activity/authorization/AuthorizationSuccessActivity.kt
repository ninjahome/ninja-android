package com.ninjahome.ninja.ui.activity.authorization

import androidx.constraintlayout.widget.ConstraintLayout
import com.gyf.immersionbar.ImmersionBar
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.R
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.databinding.ActivityAuthorizationSuccessBinding
import com.ninjahome.ninja.viewmodel.AuthorizationSuccessViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:2021/9/7
 *Description:
 */
class AuthorizationSuccessActivity:BaseActivity<AuthorizationSuccessViewModel,ActivityAuthorizationSuccessBinding>(R.layout.activity_authorization_success) {
    override val mViewModel: AuthorizationSuccessViewModel by viewModel()

    override fun initView() {
        ImmersionBar.with(this).titleBar(findViewById<ConstraintLayout>(R.id.titleLayout), false).transparentBar().init()
    }

    override fun initData() {
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_TRANSPARENT

    override fun initVariableId(): Int = BR.viewModel


}