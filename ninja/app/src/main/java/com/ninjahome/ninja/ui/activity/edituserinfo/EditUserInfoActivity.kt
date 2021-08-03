package com.ninjahome.ninja.ui.activity.edituserinfo

import android.text.TextUtils
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityEditUserInfoBinding
import com.ninjahome.ninja.viewmodel.EditUserInfoViewModel
import kotlinx.android.synthetic.main.activity_edit_user_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class EditUserInfoActivity : BaseActivity<EditUserInfoViewModel, ActivityEditUserInfoBinding>(R.layout.activity_edit_user_info) {
    override val mViewModel: EditUserInfoViewModel by viewModel()


    override fun initView() {
        mViewModel.title.set(getString(R.string.edit_user_info_title))
    }

    override fun initData() {
        val name = intent.getStringExtra(IntentKey.NAME)
        if (!TextUtils.isEmpty(name)) {
            mViewModel.isEdit = true
            mViewModel.name.value = name
        }

    }

    override fun initObserve() {
        mViewModel.name.observe(this) {
            nameEt.setSelection(nameEt.text.length)
        }
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel
}