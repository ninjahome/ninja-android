package com.ninjahome.ninja.ui.activity.splash

import android.os.Handler
import androidx.lifecycle.ViewModel
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.R
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.databinding.ActivitySplashBinding
import com.ninjahome.ninja.ui.activity.createaccount.CreateAccountActivity
import com.ninjahome.ninja.viewmodel.SplashViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class SplashActivity : BaseActivity<SplashViewModel, ActivitySplashBinding>(R.layout.activity_splash) {
    override val mViewModel: SplashViewModel by viewModel()


    override fun initView() {
        Handler(mainLooper).postDelayed({
//            if (mViewModel.hasAccount) {
//                mViewModel.loadCard()
//            } else {
//                startActivity(GuideActivity::class.java)
//                finish()
//            }
            startActivity(CreateAccountActivity::class.java)
                finish()
        }, 1000)

    }

    override fun initData() {
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_TRANSPARENT

    override fun initVariableId(): Int = BR.viewModel


}