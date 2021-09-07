package com.ninjahome.ninja.ui.activity.authorization

import androidx.constraintlayout.widget.ConstraintLayout
import com.gyf.immersionbar.ImmersionBar
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityAuthorizationFriendDaysBinding
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.viewmodel.AuthorizationFriendDaysViewModel
import kotlinx.android.synthetic.main.activity_authorization_account.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:2021/9/7
 *Description:
 */
class AuthorizationFriendDaysActivity : BaseActivity<AuthorizationFriendDaysViewModel, ActivityAuthorizationFriendDaysBinding>(R.layout.activity_authorization_friend_days) {
    override val mViewModel: AuthorizationFriendDaysViewModel by viewModel()

    override fun initView() {
        mViewModel.title.set(getString(R.string.my_authorization_friend))
        ImmersionBar.with(this).titleBar(findViewById<ConstraintLayout>(R.id.titleLayout), false).transparentBar().init()
    }

    override fun initData() {
       mViewModel.contact = intent.getParcelableExtra(IntentKey.CONTACT)!!
        mViewModel.setContactIcon()

    }

    override fun initObserve() {
        mViewModel.expireTime.observe(this) {
            val allDays = String.format(getString(R.string.authorization_remaining_activation_days), mViewModel.getAllDays())
            remainingActivationDaysTv.text = allDays
        }
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel
    override fun onResume() {
        super.onResume()
        mViewModel.getExpireTime()
    }

}