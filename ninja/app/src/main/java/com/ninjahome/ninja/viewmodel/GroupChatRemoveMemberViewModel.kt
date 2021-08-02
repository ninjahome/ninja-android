package com.ninjahome.ninja.viewmodel

import androidx.lifecycle.rxLifeScope
import chatLib.ChatLib
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.model.bean.GroupChat
import com.ninjahome.ninja.model.bean.GroupMember
import com.ninjahome.ninja.room.GroupDBManager
import com.ninjahome.ninja.utils.MoshiUtils
import com.ninjahome.ninja.utils.toJson

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class GroupChatRemoveMemberViewModel : BaseViewModel() {
    var groupChat: GroupChat? = null
    var groupMembers = arrayListOf<GroupMember>()
    val clickComplete = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            rxLifeScope.launch({
                groupChat?.let {
                    showDialog()
                    val kickUserIds = arrayListOf<String>()
                    groupMembers.filter { it.isSelected }.forEach {
                        kickUserIds.add(it.address)
                    }

                    ChatLib.kickOutUser(groupChat?.memberIdList, groupChat?.groupId, groupChat?.owner, kickUserIds.toJson())

                    val ids = MoshiUtils.listFromJson<String>(groupChat!!.memberIdList)
                    val nickNames = MoshiUtils.listFromJson<String>(groupChat!!.memberNickNameList)
                    val newIds = ArrayList<String>()
                    val newNickNames = ArrayList<String>()

                    for (i in 0 until ids.size) {
                        if (!kickUserIds.contains(ids[i])) {
                            newIds.add(ids[i])
                            newNickNames.add(nickNames[i])
                        }
                    }
                    groupChat!!.memberIdList = newIds.toJson()
                    groupChat!!.memberNickNameList = newNickNames.toJson()
                    GroupDBManager.updateGroup(groupChat!!)

                    finish()
                    dismissDialog()
                }

            }, {
                dismissDialog()
                it.message?.let { it1 -> toast(it1) }
            })
        }

    })
}
