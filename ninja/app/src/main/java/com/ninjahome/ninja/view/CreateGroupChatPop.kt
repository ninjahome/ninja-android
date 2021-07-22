package com.ninjahome.ninja.view

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.lxj.xpopup.core.BottomPopupView
import com.ninjahome.ninja.R


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class CreateGroupChatPop(context: Context, val listener: ClickListener) : BottomPopupView(context), View.OnClickListener {
    lateinit var nameEt :EditText
    interface ClickListener {
        fun clickSure(name:String)
        fun clickNoName()
    }

    override fun getImplLayoutId(): Int {
        return R.layout.pop_create_group_chat
    }

    override fun onCreate() {
        super.onCreate()
        nameEt = findViewById(R.id.nameEt)
        findViewById<ImageView>(R.id.closeIv).setOnClickListener(this)
        findViewById<Button>(R.id.NoNameBtn).setOnClickListener(this)
        findViewById<Button>(R.id.createBtn).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.closeIv -> dismiss()
            R.id.NoNameBtn -> listener.clickNoName()
            R.id.createBtn -> {
                val name = nameEt.text.toString().trim()
                listener.clickSure(name)
            }


        }
    }
}