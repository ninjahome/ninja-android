package com.ninjahome.ninja.ui.activity.contact

import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityEditContactBinding
import com.ninjahome.ninja.viewmodel.EditContactViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class EditContactActivity : BaseActivity<EditContactViewModel, ActivityEditContactBinding>(R.layout.activity_edit_contact) {

    override val mViewModel: EditContactViewModel by viewModel()

    override fun initView() {
        mViewModel.title.set(getString(R.string.search_contact_add_title))
    }

    override fun initData() {
        mViewModel.contact = intent.getParcelableExtra(IntentKey.CONTACT)
        mViewModel.address.value = intent.getStringExtra(IntentKey.ID)
        if (mViewModel.contact != null) {
            mViewModel.nickName.value = mViewModel.contact!!.nickName
            mViewModel.remark.value = mViewModel.contact!!.remark
        } else {
            mViewModel.queryContract()
        }
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel
}