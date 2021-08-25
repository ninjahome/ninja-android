package com.ninjahome.ninja.utils

import chatLib.ChatLib
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
    private val mDrawableBuilder = TextDrawable.builder().beginConfig()

    fun getDrawable(fontSize: Int, address: String, subName: String): TextDrawable {
        mDrawableBuilder.fontSize(fontSize)
        val index = ChatLib.iconIndex(address, ColorUtil.colorSize)
        val iconColor = ColorUtil.colors[index.toInt()]
        val drawable = mDrawableBuilder.textColor(context().getColor(R.color.white)).endConfig().buildRound(subName, context().resources.getColor(iconColor))
        return drawable
    }
}