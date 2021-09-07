package com.ninjahome.ninja.ui.activity.authorization

import androidx.constraintlayout.widget.ConstraintLayout
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.R
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.databinding.ActivityAuthorizationFriendBinding
import com.ninjahome.ninja.viewmodel.AuthorizationFriendViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:2021/9/6
 *Description:
 */
class AuthorizationFriendActivity:BaseActivity<AuthorizationFriendViewModel,ActivityAuthorizationFriendBinding>(R.layout.activity_authorization_friend) {
    override val mViewModel: AuthorizationFriendViewModel by viewModel()

    override fun initView() {
        mViewModel.title.set(getString(R.string.authorization_friends))
        findViewById<ConstraintLayout>(R.id.titleLayout).setBackgroundColor(resources.getColor(R.color.white,null))
    }

    override fun initData() {
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

}