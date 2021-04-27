package com.ninjahome.ninja.viewmodel

import com.ninja.android.lib.base.ItemViewModel
import com.ninjahome.ninja.model.bean.Message

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MessageItemViewModel(viewModel: MessageViewModel, val message: Message):ItemViewModel<MessageViewModel>(viewModel) {
}