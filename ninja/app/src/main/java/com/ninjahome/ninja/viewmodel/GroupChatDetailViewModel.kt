package com.ninjahome.ninja.viewmodel

import android.os.Bundle
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.rxLifeScope
import chatLib.ChatLib
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.GroupChat
import com.ninjahome.ninja.room.GroupDBManager
import com.ninjahome.ninja.ui.activity.groupchat.GroupChatRemoveMemberActivity
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class GroupChatDetailViewModel : BaseViewModel() {
    val groupDetail = MutableLiveData<GroupChat>()
    val isLord = MutableLiveData<Boolean>()

    val memberIconItem: ObservableList<GroupChatDetailItemViewModel> = ObservableArrayList()
    val itemBinding = ItemBinding.of<GroupChatDetailItemViewModel>(BR.item, R.layout.item_group_chat_detail_icon)
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
            val bundle= Bundle()
            bundle.putParcelable(IntentKey.GROUPCHAT,groupDetail.value)
            startActivity(GroupChatRemoveMemberActivity::class.java,bundle)
        }
    })

    val clickSignOut = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            rxLifeScope.launch ({
                showDialog()
                ChatLib.quitGroup(groupDetail.value?.memberIdList,groupDetail.value?.groupId)
                GroupDBManager.delete(groupDetail.value!!)
                dismissDialog()
            },{
                dismissDialog()
                it.message?.let { it1 -> toast(it1) }
            })


        }
    })
}