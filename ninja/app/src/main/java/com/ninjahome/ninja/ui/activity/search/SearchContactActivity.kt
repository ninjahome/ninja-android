package com.ninjahome.ninja.ui.activity.search

import android.content.Intent
import androidlib.Androidlib
import com.google.zxing.integration.android.IntentIntegrator
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivitySearchContactBinding
import com.ninjahome.ninja.ui.activity.contact.EditContactActivity
import com.ninjahome.ninja.ui.activity.scan.ScanActivity
import com.ninjahome.ninja.viewmodel.SearchContactViewModel
import kotlinx.android.synthetic.main.activity_search_contact.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class SearchContactActivity : BaseActivity<SearchContactViewModel, ActivitySearchContactBinding>(R.layout.activity_search_contact) {
    override val mViewModel: SearchContactViewModel by viewModel()

    override fun initView() {
        contactIdEt.setOnEditorActionListener { v, actionId, event ->
            if (!Androidlib.isValidNinjaAddr(mViewModel.inputID.value)) {
                toast(getString(R.string.search_contact_address_error))
            } else {
                val intent = Intent(this, EditContactActivity::class.java)
                intent.putExtra(IntentKey.UID, mViewModel.inputID.value)
                startActivity(intent)
            }
            return@setOnEditorActionListener true
        }
    }

    override fun initData() {
    }

    override fun initObserve() {
        mViewModel.startScanActivityEvnet.observe(this) {
            ScanActivity.start(this)
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
        val intent = Intent(this, EditContactActivity::class.java)
        intent.putExtra(IntentKey.UID, result.contents)
        startActivity(intent)


    }
}