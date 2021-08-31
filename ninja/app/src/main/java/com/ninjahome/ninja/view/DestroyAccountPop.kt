package com.ninjahome.ninja.view

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.lxj.xpopup.core.BottomPopupView
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.R


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class DestroyAccountPop(context: Context, val listener: ClickListener) : BottomPopupView(context), View.OnClickListener {


    interface ClickListener {
        fun clickSure(password:String)
        fun clickClose()
    }

    override fun getImplLayoutId(): Int {
        return R.layout.pop_destroy_account
    }

    override fun onCreate() {
        super.onCreate()
        findViewById<ImageView>(R.id.closeIv).setOnClickListener {
            listener.clickClose()
            dismiss()
        }
        val destroyAccountEt = findViewById<EditText>(R.id.destroyAccountEt)
        val destroyAccount2Et = findViewById<EditText>(R.id.destroyAccount2Et)
        findViewById<TextView>(R.id.sureTv).setOnClickListener {
            if(TextUtils.isEmpty(destroyAccountEt.text)){
                Toast.makeText(context(),context().getString(R.string.create_account_input_password),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(!destroyAccountEt.text.toString().trim().equals(destroyAccount2Et.text.toString().trim())){
                Toast.makeText(context(),context().getString(R.string.create_account_password_not_equal),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            listener.clickSure(destroyAccountEt.text.toString().trim())
            dismiss()

        }
    }

    override fun onClick(v: View) {

    }
}