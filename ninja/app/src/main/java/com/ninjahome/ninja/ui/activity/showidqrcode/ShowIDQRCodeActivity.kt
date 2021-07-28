package com.ninjahome.ninja.ui.activity.showidqrcode

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.gyf.immersionbar.ImmersionBar
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.dp
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityShowIdQrCodeBinding
import com.ninjahome.ninja.viewmodel.ShowIDQRCodeViewModel
import kotlinx.android.synthetic.main.activity_show_id_qr_code.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShowIDQRCodeActivity : BaseActivity<ShowIDQRCodeViewModel, ActivityShowIdQrCodeBinding>(R.layout.activity_show_id_qr_code) {


    override fun initView() {
        mViewModel.title.set(getString(R.string.my_id))
        ImmersionBar.with(this).titleBar(findViewById<ConstraintLayout>(R.id.titleLayout), false).transparentBar().init()
    }

    override fun initData() {
        val id = intent.getStringExtra(IntentKey.ADDRESS)
        val barcodeEncoder = BarcodeEncoder()
        try {
            val bitmap = barcodeEncoder.encodeBitmap(id, BarcodeFormat.QR_CODE, 288.dp.toInt(), 288.dp.toInt())
            qrIv.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    override fun initObserve() {
        mViewModel.finishAfterTransitionEvent.observe(this, { ActivityCompat.finishAfterTransition(this@ShowIDQRCodeActivity) })


    }

    override fun initVariableId(): Int = BR.viewModel
    override val mViewModel: ShowIDQRCodeViewModel by viewModel()


    override fun statusBarStyle(): Int = STATUSBAR_STYLE_TRANSPARENT
}