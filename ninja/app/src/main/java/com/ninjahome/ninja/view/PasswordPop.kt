package com.ninjahome.ninja.view

import android.content.Context
import android.os.Build
import android.text.InputType
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
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
class PasswordPop(context: Context, val listener: InputPasswordListener) : CenterPopupView(context) {
    interface InputPasswordListener {
        fun input(password: String)
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_password
    }

    override fun onCreate() {
        super.onCreate()
        val password = findViewById<EditText>(R.id.password)
        findViewById<TextView>(R.id.unlock).setOnClickListener {
            if (TextUtils.isEmpty(password.text.toString().trim())) {
                toast(context.getString(R.string.create_account_input_password))
                return@setOnClickListener
            }
            listener.input(password.text.toString())
            if (isEMUI() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                password.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }
}