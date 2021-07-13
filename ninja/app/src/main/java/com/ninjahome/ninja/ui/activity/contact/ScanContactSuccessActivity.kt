package com.ninjahome.ninja.ui.activity.contact

import android.graphics.Color
import androidlib.Androidlib
import androidx.constraintlayout.widget.ConstraintLayout
import com.gyf.immersionbar.ImmersionBar
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.R
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.databinding.ActivityScanContactSuccessBinding
import com.ninjahome.ninja.view.contacts.ColorGenerator
import com.ninjahome.ninja.view.contacts.ColorUtil
import com.ninjahome.ninja.view.contacts.TextDrawable
import com.ninjahome.ninja.viewmodel.ScanContactSuccessViewModel
import kotlinx.android.synthetic.main.activity_contact_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class ScanContactSuccessActivity:BaseActivity<ScanContactSuccessViewModel,ActivityScanContactSuccessBinding>(R.layout.activity_scan_contact_success) {
    private val mDrawableBuilder = TextDrawable.builder().beginConfig().fontSize(40)
    override val mViewModel: ScanContactSuccessViewModel by viewModel()

    override fun initView() {
        ImmersionBar.with(this).titleBar(findViewById<ConstraintLayout>(R.id.titleLayout), false).transparentBar().init()

    }

    override fun initData() {
        val uid = intent.getStringExtra(IntentKey.UID)!!
        mViewModel.uid.value = uid
       val drawable = mDrawableBuilder.textColor(resources.getColor(R.color.white)).endConfig().buildRound(uid.substring(0,2),resources.getColor(R.color.color_D8D8D8) )
        nameIv.setImageDrawable(drawable)
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_CUSTOMER

    override fun initVariableId(): Int= BR.viewModel
}