package com.ninjahome.ninja.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.command.BindingConsumer
import com.ninja.android.lib.command.BindingFunction
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.orhanobut.logger.Logger
import com.tencent.lbssearch.TencentSearch
import com.tencent.lbssearch.`object`.param.SuggestionParam
import com.tencent.lbssearch.`object`.result.SearchResultObject
import com.tencent.lbssearch.`object`.result.SuggestionResultObject
import com.tencent.lbssearch.httpresponse.BaseObject
import com.tencent.lbssearch.httpresponse.HttpResponseListener


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class LocationSearchViewModel : BaseViewModel() {

    val address = MutableLiveData<String>()
    var suggestionDatas = mutableListOf<SuggestionResultObject.SuggestionData>()
    val searchSuccessEvent = SingleLiveEvent<Any>()

    val clickCancel = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            finish()
        }
    })

    val changeAddress = BindingCommand(bindConsumer = object : BindingConsumer<String> {

        override fun call(t: String) {
            if(!TextUtils.isEmpty(t) && t.length>1){
                searchAddress()
            }else{
                suggestionDatas.clear()
                searchSuccessEvent.call()
            }
        }
    })
    fun searchAddress() {
        val tencentSearch = TencentSearch(context())
        val suggestionParam = SuggestionParam()
        suggestionParam.keyword(address.value)
        //suggestion也提供了filter()方法和region方法
        //具体说明见文档，或者官网的webservice对应接口
        //suggestion也提供了filter()方法和region方法
        //具体说明见文档，或者官网的webservice对应接口
        tencentSearch.suggestion(suggestionParam, object : HttpResponseListener {
            override fun onSuccess(arg0: Int, arg1: BaseObject) {
                suggestionDatas.clear()
                suggestionDatas.addAll((arg1 as SuggestionResultObject).data)
                searchSuccessEvent.call()
            }

            override fun onFailure(arg0: Int, arg1: String, arg2: Throwable) {
                Logger.e("error code:$arg0, msg:$arg1")
            }
        })
    }



}