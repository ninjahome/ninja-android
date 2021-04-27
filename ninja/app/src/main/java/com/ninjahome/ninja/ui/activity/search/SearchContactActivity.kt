package com.ninjahome.ninja.ui.activity.search

import android.content.Intent
import com.google.zxing.integration.android.IntentIntegrator
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivitySearchContactBinding
import com.ninjahome.ninja.ui.activity.scan.ScanActivity
import com.ninjahome.ninja.viewmodel.SearchContactViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class SearchContactActivity :
    BaseActivity<SearchContactViewModel, ActivitySearchContactBinding>(R.layout.activity_search_contact) {
    override val mViewModel: SearchContactViewModel by viewModel()

    override fun initView() {
    }

    override fun initData() {
    }

    override fun initObserve() {
        mViewModel.startScanActivityEvnet.observe(this) {
            val ii = IntentIntegrator(this)
            ii.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            ii.captureActivity = ScanActivity::class.java
            ii.setCameraId(0)
            ii.setBarcodeImageEnabled(true)
            ii.setRequestCode(IntentIntegrator.REQUEST_CODE)
            ii.initiateScan()
        }
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data) ?: return
        if (result.contents == null) {
            return
        }
        mViewModel.inputID.value = result.contents


    }
}