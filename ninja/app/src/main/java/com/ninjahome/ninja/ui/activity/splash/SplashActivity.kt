package com.ninjahome.ninja.ui.activity.splash

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.SharedPref
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivitySplashBinding
import com.ninjahome.ninja.ui.activity.createaccount.CreateAccountActivity
import com.ninjahome.ninja.ui.activity.edituserinfo.EditUserInfoActivity
import com.ninjahome.ninja.ui.activity.unlock.UnLockActivity
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
    private val mHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val userName: String by SharedPref(this, Constants.KEY_USER_NAME, "")
    override val mViewModel: SplashViewModel by viewModel()


    override fun initView() {
        mHandler.postDelayed({
            if (!mViewModel.hasAccount) {
                startActivity(CreateAccountActivity::class.java)
                finish()
                return@postDelayed
            }

            if (TextUtils.isEmpty(userName)) {
                startActivity(EditUserInfoActivity::class.java)
                finish()
            } else {
                startActivity(UnLockActivity::class.java)
                finish()
            }

        }, 1000)

    }

    override fun initData() {
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_TRANSPARENT

    override fun initVariableId(): Int = BR.viewModel

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

}