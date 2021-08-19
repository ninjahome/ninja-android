package com.ninjahome.ninja.view

import android.content.Context
import android.os.Build
import android.text.InputType
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.gyf.immersionbar.OSUtils.isEMUI
import com.lxj.xpopup.core.CenterPopupView
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.R


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class RechargePop(context: Context, val listener: ClickListener) : CenterPopupView(context) {
    interface ClickListener {
        fun clickSure()
        fun clickCancel()
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_recharge
    }

    override fun onCreate() {
        super.onCreate()
        findViewById<Button>(R.id.notYetBtn).setOnClickListener {
            listener.clickCancel()
            dismiss()
        }
        findViewById<Button>(R.id.activateBtn).setOnClickListener {
            listener.clickSure()
            dismiss()
        }

    }
}