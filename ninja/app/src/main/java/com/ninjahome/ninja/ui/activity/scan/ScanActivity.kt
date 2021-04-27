package com.ninjahome.ninja.ui.activity.scan

import android.os.Bundle
import android.view.KeyEvent
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.ViewfinderView
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.R
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.databinding.ActivityScanBinding
import com.ninjahome.ninja.viewmodel.ScanViewModel
import kotlinx.android.synthetic.main.activity_scan.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ScanActivity : BaseActivity<ScanViewModel, ActivityScanBinding>(R.layout.activity_scan) {

    private val capture: CaptureManager by lazy { CaptureManager(this, zxing_barcode_scanner) }
    var bundle: Bundle? = null


    override val mViewModel: ScanViewModel by viewModel()

    override fun initView() {
        capture.initializeFromIntent(intent, bundle)
        capture.decode()
        zxing_barcode_scanner.findViewById<ViewfinderView>(R.id.zxing_viewfinder_view)
        zxing_barcode_scanner.findViewById<BarcodeView>(R.id.zxing_barcode_surface)
    }

    override fun initData() {
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_TRANSPARENT

    override fun initVariableId(): Int = BR.viewModel

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return zxing_barcode_scanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }


}