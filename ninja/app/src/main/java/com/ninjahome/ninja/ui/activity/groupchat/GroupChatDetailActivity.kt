package com.ninjahome.ninja.ui.activity.groupchat

import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityGroupChatDetailBinding
import com.ninjahome.ninja.viewmodel.GroupChatDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class GroupChatDetailActivity:BaseActivity<GroupChatDetailViewModel,ActivityGroupChatDetailBinding>(R.layout.activity_group_chat_detail) {
    override val mViewModel: GroupChatDetailViewModel by viewModel()

    override fun initView() {
    }

    override fun initData() {
    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int =STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

}