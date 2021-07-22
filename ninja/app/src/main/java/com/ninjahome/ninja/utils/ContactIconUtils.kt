package com.ninjahome.ninja.utils

import androidlib.Androidlib
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.R
import com.ninjahome.ninja.view.contacts.ColorUtil
import com.ninjahome.ninja.view.contacts.TextDrawable

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object ContactIconUtils {
    private val mDrawableBuilder =  TextDrawable.builder().beginConfig()

    fun getDrawable(fontSize:Int,address:String,subName:String):TextDrawable{
        mDrawableBuilder.fontSize(fontSize)
        val index = Androidlib.iconIndex(address, ColorUtil.colorSize)
        val iconColor = ColorUtil.colors[index]
        val drawable = mDrawableBuilder.textColor(context().getColor(R.color.white)).endConfig().buildRound(subName, context()
                .resources.getColor(iconColor))
        return drawable
    }
}