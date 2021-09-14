package com.ninjahome.ninja.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.command.BindingConsumer
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.R
import com.ninjahome.ninja.event.EventActivationSuccess
import com.ninjahome.ninja.model.ActivationModel
import com.ninjahome.ninja.model.bean.LicenseResultCode
import com.ninjahome.ninja.model.bean.Verifylicense
import com.ninjahome.ninja.utils.CommonUtils
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus

/**
 *Author:Mr'x
 *Time:2021/8/12
 *Description:
 */
class ActivationViewModel(val mModel: ActivationModel) : BaseViewModel() {
    val activationCode = MutableLiveData<String>("")
    val license = MutableLiveData<String>()
    val enable = MutableLiveData<Boolean>()
    val importEvent = SingleLiveEvent<Any>()

    init {
        title.set(context().getString(R.string.my_activate_code))
        showBackImage.set(true)
    }

    val textChange = BindingCommand(bindConsumer = object : BindingConsumer<String> {

        override fun call(t: String) {
            enable.value = !TextUtils.isEmpty(t) && t.length > 1
        }
    })
    val clickImport = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            importEvent.call()
        }
    })
    val clickVerify = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            verivy()
            decodeLicense()
        }

    })

    val longClick = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            license.value?.let{
                CommonUtils.copyToMemory(context(),it)
                showToast(R.string.copy_success)
            }

        }
    })

    fun decodeLicense() {
        rxLifeScope.launch {
            license.value = mModel.decodeLicense(activationCode.value!!)
        }
    }

    private fun verivy() {
        rxLifeScope.launch( {

           val verifyCode = mModel.isValidLicense(activationCode.value!!)
            when (verifyCode) {
                Verifylicense.ContractErr.value, Verifylicense.CallContractErr.value, Verifylicense.ValidTrue.value -> {
                    showToast(R.string.my_verify_error)
                }
                Verifylicense.ValidFalse.value -> {
                    showToast(R.string.my_verify_success)
                }
                Verifylicense.DecodeLicenseErr.value -> {
                    showToast(R.string.my_import_license_error)
                }
            }
            dismissDialog()
        },{
            dismissDialog()
            showToast(R.string.my_verify_error)
        },{showDialog()})
    }

    val clickSure = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            import()
        }
    })

    private fun import() {

        rxLifeScope.launch({

            val licenseResult = mModel.importLicense(activationCode.value!!)

            dismissDialog()
            if (licenseResult == null) {
                showToast(R.string.my_import_license_error)
            } else {
                when (licenseResult.resultCode) {
                    LicenseResultCode.SUCCESS.value -> {
                        showToast(R.string.my_import_license_success)
                        EventBus.getDefault().postSticky(EventActivationSuccess())
                        finish()
                    }
                    LicenseResultCode.ParseJsonErr.value -> {
                        showToast(R.string.my_import_license_parse_error)

                    }
                    LicenseResultCode.CallContractErr.value -> {
                        showToast(R.string.my_import_license_contract_error)
                    }
                }
            }


        }, {
            dismissDialog()
            showToast(R.string.my_import_license_error)
        },{showDialog()})
    }

}