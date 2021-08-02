package com.ninjahome.ninja.ui.activity.groupchat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityGroupChatRemoveMemberBinding
import com.ninjahome.ninja.model.bean.GroupChat
import com.ninjahome.ninja.model.bean.GroupMember
import com.ninjahome.ninja.ui.adapter.GroupRemoveMemberAdapter
import com.ninjahome.ninja.utils.MoshiUtils
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
        layoutManager.orientation = RecyclerView.VERTICAL
        memberRv.layoutManager = layoutManager

        memberRv.adapter = memberAdapter
        memberAdapter.setItemCLickListener(this)
    }


    override fun initData() {
        val groupDetail = intent.getParcelableExtra<GroupChat>(IntentKey.GROUPCHAT)
        mViewModel.groupChat = groupDetail
        groupDetail?.memberIdList?.let {
            val ids = MoshiUtils.listFromJson<String>(it)
            val nickNames = MoshiUtils.listFromJson<String>(groupDetail?.memberNickNameList)
            var groupMember: GroupMember
            val curAddress = NinjaApp.instance.account.address
            for (i: Int in 0 until ids.size) {
                if (i < nickNames.size&& !ids[i].equals(curAddress)) {
                    groupMember = GroupMember(ids[i], nickNames[i])
                    memberAdapter.add(groupMember)
                }

            }

        }

    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

    override fun onSelected(position: Int, member: GroupMember) {
        mViewModel.groupMembers = memberAdapter.groupMemberList!!
    }

}