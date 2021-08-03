package com.ninjahome.ninja.ui.activity.contact

import androidx.constraintlayout.widget.ConstraintLayout
import chatLib.ChatLib
import com.gyf.immersionbar.ImmersionBar
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityContactDetailBinding
import com.ninjahome.ninja.utils.DialogUtils
import com.ninjahome.ninja.view.contacts.ColorUtil
import com.ninjahome.ninja.view.contacts.TextDrawable
import com.ninjahome.ninja.viewmodel.ContactDetailViewModel
import kotlinx.android.synthetic.main.activity_contact_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ContactDetailActivity : BaseActivity<ContactDetailViewModel, ActivityContactDetailBinding>(R.layout.activity_contact_detail), OnConfirmListener {
    private val mDrawableBuilder = TextDrawable.builder().beginConfig().fontSize(40)
    override val mViewModel: ContactDetailViewModel by viewModel()

    override fun initView() {
        mViewModel.showBackImage.set(true)
        ImmersionBar.with(this).titleBar(findViewById<ConstraintLayout>(R.id.titleLayout), false).transparentBar().init()
    }

    override fun initData() {
        val uid = intent.getStringExtra(IntentKey.ID)!!
        mViewModel.uid.value = uid
        mViewModel.getContact(uid)
        mViewModel.showBottomLine.set(false)
        mViewModel.showRightIv.set(true)
        mViewModel.rightIv.set(R.drawable.contact_delete)
    }

    override fun initObserve() {
        mViewModel.name.observe(this) {
            var subName: String = it.toString()
            if (it.length >= 2) {
                subName = it.substring(0, 2)
            }
            val drawable = mViewModel.contact.value?.let { _ ->
                val index = ChatLib.iconIndex(mViewModel.uid.value!!, ColorUtil.colorSize)
                val iconColor = ColorUtil.colors[index]
                mDrawableBuilder.textColor(resources.getColor(R.color.white, null)).endConfig().buildRound(subName, resources.getColor(iconColor, null))
            }
            nameIv.setImageDrawable(drawable)
        }
        mViewModel.showDeleteDialogEvent.observe(this) {
            DialogUtils.showDeleteContactDialog(this, this)
        }

        mViewModel.deleteSuccessEvent.observe(this) {
            toast(getString(R.string.delete_success))
            finish()
        }
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_CUSTOMER

    override fun initVariableId(): Int = BR.viewModel
    override fun onConfirm() {
        mViewModel.deleteContact()
    }
}