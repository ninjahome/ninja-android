package com.ninjahome.ninja.viewmodel

import com.ninja.android.lib.base.ItemViewModel
import com.ninjahome.ninja.model.bean.Contact

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ApplyListItemViewModel(viewModel: ApplyListViewModel, val contact: Contact) : ItemViewModel<ApplyListViewModel>(viewModel) {}