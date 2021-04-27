package com.ninjahome.ninja.ui.fragment.message

import com.ninja.android.lib.base.BaseFragment
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.FragmentMessageBinding
import com.ninjahome.ninja.viewmodel.MessageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MessageFragment :
    BaseFragment<MessageViewModel, FragmentMessageBinding>(R.layout.fragment_message) {
    override val mViewModel: MessageViewModel by viewModel()

    override fun initView() {

    }

    override fun initData() {
    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
    }


}