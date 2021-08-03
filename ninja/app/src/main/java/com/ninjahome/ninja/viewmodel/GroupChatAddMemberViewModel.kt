package com.ninjahome.ninja.viewmodel

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
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.model.bean.GroupInfo
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.room.GroupDBManager
import com.ninjahome.ninja.utils.MoshiUtils
import com.ninjahome.ninja.utils.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.util.*

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class GroupChatAddMemberViewModel : BaseViewModel() {
    val groupDetail = MutableLiveData<GroupInfo>()
    val allContact = ContactDBManager.all()
    val contacts = mutableListOf<Contact>()
    val newIds = ArrayList<String>()
    val newNickNames = ArrayList<String>()

    val contactIconItem: ObservableList<GroupChatAddMemberItemViewModel> = ObservableArrayList()
    val itemBinding = ItemBinding.of<GroupChatAddMemberItemViewModel>(BR.item, R.layout.item_group_chat_add_member_icon)

    val clickSure = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            rxLifeScope.launch({
                showDialog()
                withContext(Dispatchers.IO) {
                    newIds.clear()
                    newNickNames.clear()
                    val idsJson = groupDetail.value?.let { MoshiUtils.listFromJson<String>(it.memberIdList) }
                    val nickNameJson = groupDetail.value?.let { MoshiUtils.listFromJson<String>(it.memberNickNameList) }
                    contactIconItem.forEach {
                        newIds.add(it.conversation.uid)
                        idsJson?.add(it.conversation.uid)
                        newNickNames.add(it.conversation.nickName)
                        nickNameJson?.add(it.conversation.nickName)
                    }

                    ChatLib.joinGroup(idsJson?.toJson(), nickNameJson?.toJson(), groupDetail.value?.groupId, groupDetail.value?.groupName, groupDetail.value?.owner, false, newIds.toJson())
                    groupDetail.value?.memberIdList = idsJson?.toJson()!!
                    groupDetail.value?.memberNickNameList = nickNameJson?.toJson()!!
                    groupDetail.value?.let { GroupDBManager.updateGroup(it) }
                }
                dismissDialog()
                finish()
            }, {
                dismissDialog()
                it.message?.let { errMsg -> toast(errMsg) }
            })

        }
    })


}