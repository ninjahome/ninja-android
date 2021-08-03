package com.ninjahome.ninja.ui.activity.groupchat

import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityGroupChatDetailBinding
import com.ninjahome.ninja.model.bean.GroupInfo
import com.ninjahome.ninja.model.bean.GroupMember
import com.ninjahome.ninja.room.GroupDBManager
import com.ninjahome.ninja.viewmodel.GroupChatDetailItemViewModel
import com.ninjahome.ninja.viewmodel.GroupChatDetailViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class GroupChatDetailActivity : BaseActivity<GroupChatDetailViewModel, ActivityGroupChatDetailBinding>(R.layout.activity_group_chat_detail) {
    override val mViewModel: GroupChatDetailViewModel by viewModel()

    override fun initView() {
    }

    override fun initData() {
        val groupDetail = intent.getParcelableExtra<GroupInfo>(IntentKey.GROUPCHAT)
        mViewModel.groupDetail.value = groupDetail
        mViewModel.title.set(groupDetail?.groupName)
        mViewModel.isLord.value = groupDetail?.owner.equals(NinjaApp.instance.account.address)
    }

    override fun initObserve() {
        mViewModel.groupDetail.value?.let {
            MainScope().launch {
                GroupDBManager.queryLiveDataByGroupId(mViewModel.groupDetail.value!!.groupId).observe(this@GroupChatDetailActivity) { groupChat ->
                    mViewModel.groupDetail.value = groupChat
                    mViewModel.memberIconItem.clear()
                    if (groupChat == null) {
                        finish()
                        return@observe
                    }
                    val ids = JSONArray(groupChat!!.memberIdList)
                    val nickNames = JSONArray(groupChat.memberNickNameList)
                    var groupMember: GroupMember
                    for (i: Int in 0 until ids.length()) {
                        if (i < nickNames.length()) {
                            groupMember = GroupMember(ids[i].toString(), nickNames[i].toString())
                            mViewModel.memberIconItem.add(GroupChatDetailItemViewModel(mViewModel, groupMember))
                        }

                    }
                    val addGroupMember = GroupMember("", "")

                    mViewModel.memberIconItem.add(GroupChatDetailItemViewModel(mViewModel, addGroupMember))
                }
            }

        }

    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

}