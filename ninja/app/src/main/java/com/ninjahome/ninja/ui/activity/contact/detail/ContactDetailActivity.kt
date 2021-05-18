package com.ninjahome.ninja.ui.activity.contact.detail

import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityContactDetailBinding
import com.ninjahome.ninja.viewmodel.ContactDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ContactDetailActivity : BaseActivity<ContactDetailViewModel, ActivityContactDetailBinding>(R.layout.activity_contact_detail) {
    override val mViewModel: ContactDetailViewModel by viewModel()

    override fun initView() {
        mViewModel.showBackImage.set(true)
        mViewModel.title.set(getString(R.string.edit_user_info_title))
    }

    override fun initData() {
        val uid = intent.getStringExtra(IntentKey.UID)!!
        mViewModel.getContact(uid)
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel
}