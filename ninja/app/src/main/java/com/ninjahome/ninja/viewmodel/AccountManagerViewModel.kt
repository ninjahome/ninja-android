package com.ninjahome.ninja.viewmodel

import android.os.Bundle
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.ui.activity.createaccount.CreateAccountActivity
import com.ninjahome.ninja.utils.BitmapUtils
import com.ninjahome.ninja.utils.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class AccountManagerViewModel : BaseViewModel() {


    val requestLocalPermissionEvent = SingleLiveEvent<Boolean>()

    val clickCreate = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            startActivity(CreateAccountActivity::class.java)
        }

    })

    val clickImport = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            val bundle = Bundle()
            bundle.putBoolean(IntentKey.SHOW_IMPORT_DIALOG, true)
            startActivity(CreateAccountActivity::class.java, bundle)
        }

    })

    val clickExport = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            requestLocalPermissionEvent.call()

        }

    })

    fun saveAccount() {
        rxLifeScope.launch({
            withContext(Dispatchers.IO) {
                val accountJson = NinjaApp.instance.account.toJson()
                val isSave = BitmapUtils.saveBitmapToAlbum(context(), BitmapUtils.stringToQRBitmap(accountJson), context().getString(R.string.account_manager_qr_name))
                if (isSave) {
                    showToast(R.string.account_manager_save_account_success)
                } else {
                    showToast(R.string.account_manager_save_account_failure)
                }
                dismissDialog()
            }
        }, {
            showToast(R.string.account_manager_save_account_failure)
            dismissDialog()
        }, { showDialog() })
    }
}