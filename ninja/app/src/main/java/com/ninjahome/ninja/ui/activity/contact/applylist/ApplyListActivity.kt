package com.ninjahome.ninja.ui.activity.contact.applylist

import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityApplyListBinding
import com.ninjahome.ninja.viewmodel.ApplyListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ApplyListActivity : BaseActivity<ApplyListViewModel, ActivityApplyListBinding>(R.layout.activity_apply_list) {
    override val mViewModel: ApplyListViewModel by viewModel()
    override fun initView() {
    }

    override fun initData() {
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel
}