package com.ninjahome.ninja.ui.activity.groupchat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityGroupChatRemoveMemberBinding
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.ui.adapter.GroupRemoveMemberAdapter
import com.ninjahome.ninja.viewmodel.GroupChatRemoveMemberViewModel
import kotlinx.android.synthetic.main.activity_group_chat_remove_member.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class GroupChatRemoveMemberActivity : BaseActivity<GroupChatRemoveMemberViewModel, ActivityGroupChatRemoveMemberBinding>(R.layout.activity_group_chat_remove_member), GroupRemoveMemberAdapter.ClickItemListener {
    private val memberAdapter: GroupRemoveMemberAdapter by lazy { GroupRemoveMemberAdapter(this) }
    override val mViewModel: GroupChatRemoveMemberViewModel by viewModel()

    override fun initView() {
        mViewModel.title.set(getString(R.string.group_chat_remove_member_title))
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        memberRv.layoutManager = layoutManager

        memberRv.adapter = memberAdapter
        memberAdapter.setItemCLickListener(this)
    }


    override fun initData() {

    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel
    override fun onSelected(position: Int, contact: Contact) {

    }

}