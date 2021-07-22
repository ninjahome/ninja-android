package com.ninjahome.ninja.view

import android.content.Context
import android.view.View
import android.widget.TextView
import com.lxj.xpopup.core.BottomPopupView
import com.ninjahome.ninja.R


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ConversationMoreActionPop(context: Context, val listener: ConversationMoreActionListener) : BottomPopupView(context), View.OnClickListener {
    companion object {
        const val ALBUM = 0
        const val TAKE_PHOTO = 1
        const val LOCATION = 2
        const val CANCEL = 3
    }

    interface ConversationMoreActionListener {
        fun action(index: Int)
    }

    override fun getImplLayoutId(): Int {
        return R.layout.pop_more_action
    }

    override fun onCreate() {
        super.onCreate()
        findViewById<TextView>(R.id.albumTv).setOnClickListener(this)
        findViewById<TextView>(R.id.takePhotoTv).setOnClickListener(this)
        findViewById<TextView>(R.id.locationTv).setOnClickListener(this)
        findViewById<TextView>(R.id.cancelTv).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.albumTv -> listener.action(ALBUM)
            R.id.takePhotoTv -> listener.action(TAKE_PHOTO)
            R.id.locationTv -> listener.action(LOCATION)
            R.id.cancelTv -> listener.action(CANCEL)

        }
    }
}