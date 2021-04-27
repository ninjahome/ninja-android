package com.ninjahome.ninja.ui.activity.createaccount

import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityCreateAccountBinding
import com.ninjahome.ninja.viewmodel.CreateAccountViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class CreateAccountActivity :
    BaseActivity<CreateAccountViewModel, ActivityCreateAccountBinding>(R.layout.activity_create_account) {
    override val mViewModel: CreateAccountViewModel by viewModel()
    override fun initView() {
    }

    override fun initData() {
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel
}