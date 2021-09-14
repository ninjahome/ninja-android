package com.ninjahome.ninja.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.R
import com.ninjahome.ninja.utils.AccountUtils
import com.ninjahome.ninja.utils.BitmapUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ShowIDQRCodeViewModel : BaseViewModel() {
    val id = SingleLiveEvent<String>()
    val eventStartShare = MutableLiveData<Uri?>()

    init {
        rxLifeScope.launch {
            id.value = AccountUtils.getAddress(context())
        }
    }

    val finishAfterTransitionEvent = SingleLiveEvent<Boolean>()

    val finishCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            finishAfterTransitionEvent.postValue(true)
        }

    })

    val clickSave = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            rxLifeScope.launch({
                withContext(Dispatchers.IO) {
                    id.value?.let {
                        val uri = BitmapUtils.saveBitmapToAlbum(context(), BitmapUtils.stringToQRBitmap(it), context().getString(R.string.account_manager_qr_id_name))
                        if (uri != null) {
                            showToast(R.string.account_manager_save_account_success)
                        } else {
                            showToast(R.string.account_manager_save_account_failure)
                        }
                        dismissDialog()
                    }

                }
            }, {
                showToast(R.string.account_manager_save_account_failure)
                dismissDialog()
            }, { showDialog() })
        }

    })

    val clickCopy = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            id.value?.let {
                AccountUtils.copyToMemory(context(), it)
                showToast(R.string.copy_success)
            }

        }

    })

    val clickShare = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            rxLifeScope.launch({
                withContext(Dispatchers.IO) {
                    id.value?.let {
                        val uri = BitmapUtils.saveBitmapToAlbum(context(), BitmapUtils.stringToQRBitmap(it), context().getString(R.string.account_manager_qr_id_name))
                        eventStartShare.postValue(uri)
                    }
                }
                dismissDialog()
            },{
                dismissDialog()
                showToast(R.string.share_error)
            },onStart = {
                showDialog()
            })

        }

    })



}