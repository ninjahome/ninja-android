package com.ninjahome.ninja.ui.activity.edituserinfo

import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityEditUserInfoBinding
import com.ninjahome.ninja.viewmodel.EditUserInfoViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class EditUserInfoActivity :
    BaseActivity<EditUserInfoViewModel, ActivityEditUserInfoBinding>(R.layout.activity_edit_user_info) {
    override val mViewModel: EditUserInfoViewModel by viewModel()


    override fun initView() {
        mViewModel.title.set(getString(R.string.edit_user_info_title))
    }

    override fun initData() {
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel
}