package com.ninjahome.ninja.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninjahome.ninja.ui.activity.groupchat.GroupChatRemoveMemberActivity

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class GroupChatDetailViewModel : BaseViewModel() {
    val name = MutableLiveData<String>()

    val clickUpdateGroupName = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showToast("修改群名称")
        }
    })

    val clickShowQRCode = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showToast("显示群二维码")
        }
    })

    val clickRemoveMember = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showToast("移除群成员")
            startActivity(GroupChatRemoveMemberActivity::class.java)
        }
    })

    val clickSignOut = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showToast("退出群聊")
        }
    })
}