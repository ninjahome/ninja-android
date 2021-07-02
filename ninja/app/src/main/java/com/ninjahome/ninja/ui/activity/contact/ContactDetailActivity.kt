package com.ninjahome.ninja.ui.activity.contact

import com.lxj.xpopup.interfaces.OnConfirmListener
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityContactDetailBinding
import com.ninjahome.ninja.utils.DialogUtils
import com.ninjahome.ninja.viewmodel.ContactDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class ContactDetailActivity : BaseActivity<ContactDetailViewModel, ActivityContactDetailBinding>(R.layout.activity_contact_detail), OnConfirmListener {
    override val mViewModel: ContactDetailViewModel by viewModel()

    override fun initView() {
        mViewModel.showBackImage.set(true)
        mViewModel.title.set(getString(R.string.edit_user_info_title))
    }

    override fun initData() {
        val uid = intent.getStringExtra(IntentKey.UID)!!
        mViewModel.getContact(uid)
        mViewModel.showRightIv.set(true)
        mViewModel.rightIv.set(R.drawable.contact_delete)
    }

    override fun initObserve() {
        mViewModel.showDeleteDialogEvent.observe(this){
            DialogUtils.showDeleteContactDialog(this,this)
        }

        mViewModel.deleteSuccessEvent.observe(this){
            toast(getString(R.string.delete_success))
            finish()
        }
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel
    override fun onConfirm() {
        mViewModel.deleteContact()
    }
}