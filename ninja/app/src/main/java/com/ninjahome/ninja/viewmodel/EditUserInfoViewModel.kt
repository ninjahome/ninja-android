package com.ninjahome.ninja.viewmodel

import android.text.TextUtils
import androidlib.Androidlib
import androidx.lifecycle.MutableLiveData
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.provider.context
import com.ninja.android.lib.utils.SharedPref
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.ui.activity.main.MainActivity
import com.ninjahome.ninja.view.contacts.ColorGenerator
import com.ninjahome.ninja.view.contacts.ColorUtil
import com.ninjahome.ninja.view.contacts.TextDrawable
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class EditUserInfoViewModel : BaseViewModel() {
    var userName: String by SharedPref(context(), Constants.KEY_USER_NAME, "", commit = true)
    private val mColorGenerator = ColorGenerator.MATERIAL
    private val mDrawableBuilder = TextDrawable.builder().beginConfig().fontSize(40)
    val iconIndex = Androidlib.iconIndex(NinjaApp.instance.account.address, ColorUtil.colorSize)
    val iconColor = ColorUtil.colors[iconIndex]
    private var subName = if (TextUtils.isEmpty(userName)) NinjaApp.instance.account.address.subSequence(0, 2).toString() else if (userName.length >= 2) userName.substring(0, 2) else userName
    val iconDrawable = mDrawableBuilder.textColor(context().getColor(R.color.white)).endConfig().buildRound(subName, context().resources.getColor(iconColor))
    val name = MutableLiveData("")
    var isEdit = false

    val clickSure = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            userName = name.value!!
            if (isEdit) {
                finish()
            } else {
                startActivityAndFinish(MainActivity::class.java)
            }
        }
    })
}