package com.ninjahome.ninja.ui.activity.conversation

import android.net.Uri
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityShowBigImageBinding
import com.ninjahome.ninja.viewmodel.ShowBigImageViewModel
import kotlinx.android.synthetic.main.activity_show_big_image.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ShowBigImageActivity : BaseActivity<ShowBigImageViewModel, ActivityShowBigImageBinding>(R.layout.activity_show_big_image) {
    override val mViewModel: ShowBigImageViewModel by viewModel()

    override fun initView() {
        val mUrl = intent.getStringExtra(IntentKey.URL)
        if (TextUtils.isEmpty(mUrl)) {
            finish()
        }
        if (mUrl!!.startsWith("http")) {
            Glide.with(this).load(Uri.parse(mUrl)).placeholder(R.mipmap.default_image).into(pv)
        } else {
            Glide.with(this).load(File(mUrl)).placeholder(R.mipmap.default_image).into(pv)
        }

        pv.setOnClickListener {
            ActivityCompat.finishAfterTransition(this@ShowBigImageActivity)
        }
    }

    override fun initData() {
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_TRANSPARENT

    override fun initVariableId(): Int = BR.viewModel
}