package com.ninjahome.ninja.ui.fragment.my

import com.ninja.android.lib.base.BaseFragment
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.FragmentMyBinding
import com.ninjahome.ninja.viewmodel.MyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MyFragment : BaseFragment<MyViewModel, FragmentMyBinding>(R.layout.fragment_my) {
    override val mViewModel: MyViewModel by viewModel()
    override fun initView() {
    }

    override fun initData() {
    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
    }


}