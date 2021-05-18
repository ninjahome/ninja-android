package com.ninjahome.ninja.ui.activity.contact.addcontact

import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityAddContactBinding
import com.ninjahome.ninja.viewmodel.AddContactViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class AddContactActivity : BaseActivity<AddContactViewModel, ActivityAddContactBinding>(R.layout.activity_add_contact) {
    override val mViewModel: AddContactViewModel by viewModel()

    override fun initView() {
        mViewModel.title.set(getString(R.string.search_contact_add_title))
    }

    override fun initData() {
        mViewModel.contact = intent.getParcelableExtra(IntentKey.CONTACT)
        mViewModel.address.value = intent.getStringExtra(IntentKey.UID)


        mViewModel.contact?.let {
            mViewModel.nickName.value = it.nickName
            mViewModel.remark.value = it.remark
        }
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel
}