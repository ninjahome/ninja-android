package com.ninjahome.ninja.viewmodel

import androidlib.Androidlib
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class ConversationListViewModel : BaseViewModel() {
    var finishRefreshingEvent = SingleLiveEvent<Any>()
    val unline = MutableLiveData<Boolean>()

    val items: ObservableList<ConversationItemViewModel> = ObservableArrayList()
    val itemBinding = ItemBinding.of<ConversationItemViewModel>(BR.item, R.layout.item_message)


    init {
        title.set(context().getString(R.string.message))
        showBackImage.set(false)
    }

    val refreshCommand = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            rxLifeScope.launch {
                withContext(Dispatchers.IO) {
                    if (!Androidlib.wsIsOnline()) {
                        Androidlib.wsOnline()
                    }
                }
            }
            finishRefreshingEvent.call()
        }
    })


}