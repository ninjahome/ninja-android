package com.ninjahome.ninja.viewmodel

import android.os.Bundle
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.rxLifeScope
import chatLib.ChatLib
import com.ninja.android.lib.base.BaseViewModel
import com.ninja.android.lib.command.BindingAction
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.event.SingleLiveEvent
import com.ninja.android.lib.provider.context
import com.ninja.android.lib.utils.SharedPref
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.*
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.model.bean.GroupInfo
import com.ninjahome.ninja.db.ContactDBManager
import com.ninjahome.ninja.db.GroupDBManager
import com.ninjahome.ninja.ui.activity.conversation.ConversationActivity
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
class CreateGroupChatViewModel : BaseViewModel() {
    var userName: String by SharedPref(context(), Constants.KEY_USER_NAME, "", commit = true)
    val allContact = ContactDBManager.all()
    val contacts = mutableListOf<Contact>()
    val showCreateGroupChatPop = SingleLiveEvent<Any>()
    val contactIconItem: ObservableList<CreateGroupChatIconItemViewModel> = ObservableArrayList()
    val itemBinding = ItemBinding.of<CreateGroupChatIconItemViewModel>(BR.item, R.layout.item_group_chat_icon)

    val clickSure = BindingCommand<Any>(object : BindingAction {
        override fun call() {
            showCreateGroupChatPop.call()
        }
    })

    fun createGroupChat(name: String, contactBeanList: MutableList<Contact>?) {

        contactBeanList?.let {
            val groupId = ChatLib.newGroupId()
            val selectContacts = contactBeanList.filter { it.isSelected }
            val ids = arrayListOf<String>()
            val names = arrayListOf<String>()
            ids.add(NinjaApp.instance.account.address)
            names.add(userName)
            for (contact in selectContacts) {
                ids.add(contact.uid)
                names.add(contact.nickName)
            }
            rxLifeScope.launch({
                showDialog()
                withContext(Dispatchers.IO) {
                    ChatLib.createGroup(ids.toJson().toLowerCase(Locale.getDefault()), names.toJson(), groupId, name)
                    val groupConversation = GroupInfo(0, groupId, name, NinjaApp.instance.account.address, ids.toJson(), names.toJson())
                    GroupDBManager.insert(groupConversation)
                    val bundle = Bundle()
                    bundle.putString(IntentKey.ID, groupId)
                    bundle.putBoolean(IntentKey.IS_GROUP, true)
                    startActivity(ConversationActivity::class.java, bundle)
                    dismissDialog()
                }
                finish()
            }, {
                dismissDialog()
                it.message?.let { errMsg -> toast(errMsg) }
            })

        }

    }
}