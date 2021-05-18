package com.ninjahome.ninja.ui.activity.backup

import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityBackUpBinding
import com.ninjahome.ninja.viewmodel.BackUpViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class BackUpActivity : BaseActivity<BackUpViewModel, ActivityBackUpBinding>(R.layout.activity_back_up) {
    override val mViewModel: BackUpViewModel by viewModel()


    override fun initView() {
    }

    override fun initData() {
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_TRANSPARENT

    override fun initVariableId(): Int = BR.viewModel

}