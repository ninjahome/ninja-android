package com.ninjahome.ninja.ui.activity.groupchat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.core.BasePopupView
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityCreateGroupChatBinding
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.model.bean.GroupInfo
import com.ninjahome.ninja.ui.adapter.CreateGroupAdapter
import com.ninjahome.ninja.utils.fromJson
import com.ninjahome.ninja.view.ContactsUtils
import com.ninjahome.ninja.view.contacts.CustomItemDecoration
import com.ninjahome.ninja.view.contacts.itemanimator.SlideInOutLeftItemAnimator
import com.ninjahome.ninja.viewmodel.GroupChatAddMemberItemViewModel
import com.ninjahome.ninja.viewmodel.GroupChatAddMemberViewModel
import kotlinx.android.synthetic.main.activity_create_group_chat.*
import kotlinx.android.synthetic.main.fragment_contact_list.contactsRecyclerView
import org.json.JSONArray
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class GroupChatAddMemberActivity : BaseActivity<GroupChatAddMemberViewModel, ActivityCreateGroupChatBinding>(R.layout.activity_group_chat_add_member), CreateGroupAdapter.ClickItemListener {

    private val ids = ArrayList<String>()
    private val contacts = ArrayList<Contact>()
    private lateinit var createGroupChatDialog: BasePopupView
    private var decoration: CustomItemDecoration = CustomItemDecoration(this)
    private var layoutManager: LinearLayoutManager? = null
    private val contactAdapter: CreateGroupAdapter by lazy { CreateGroupAdapter(this) }

    override val mViewModel: GroupChatAddMemberViewModel by viewModel()

    override fun initView() {
        mViewModel.title.set(getString(R.string.create_group_chat_add_member))
        val selectedContactLayoutManager = LinearLayoutManager(this)
        selectedContactLayoutManager.orientation = RecyclerView.HORIZONTAL
        selectedContactRv.layoutManager = selectedContactLayoutManager
        selectedContactRv.itemAnimator = null

        layoutManager = LinearLayoutManager(this)
        contactsRecyclerView.layoutManager = layoutManager
        contactsRecyclerView.addItemDecoration(decoration)
        contactsRecyclerView.itemAnimator = SlideInOutLeftItemAnimator(contactsRecyclerView)
        contactsRecyclerView.adapter = contactAdapter
        contactAdapter.setItemCLickListener(this)
    }

    override fun initData() {
        val groupDetail = intent.getParcelableExtra<GroupInfo>(IntentKey.GROUPCHAT)
        mViewModel.groupDetail.value = groupDetail
        groupDetail?.let { ids.addAll(it.memberIdList.fromJson<ArrayList<String>>()!!)}

    }

    override fun initObserve() {
        mViewModel.allContact.observe(this) {
            it?.let {
                it.forEach { contact ->
                    if (!ids.contains(contact.uid)) {
                        contacts.add(contact)
                    }
                }
                ContactsUtils.sortData(contacts)
                //返回一个包含所有Tag字母在内的字符串并赋值给tagsStr
                val tagsStr: String = ContactsUtils.getTags(contacts)
                decoration.setDatas(contacts, tagsStr)
                contactAdapter.addAll(contacts)
            }

        }

    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

    override fun clickDelete(position: Int, contact: Contact) {
        mViewModel.contacts.remove(contact)
        setCreateBtnText()
        mViewModel.contactIconItem.clear()
        mViewModel.contacts.forEach {
            mViewModel.contactIconItem.add(GroupChatAddMemberItemViewModel(mViewModel, it))
        }
    }

    override fun clickAdd(position: Int, contact: Contact) {
        mViewModel.contacts.add(contact)
        setCreateBtnText()
        mViewModel.contactIconItem.clear()
        mViewModel.contacts.forEach {
            mViewModel.contactIconItem.add(GroupChatAddMemberItemViewModel(mViewModel, it))
        }
    }

    private fun setCreateBtnText() {
        var text = getString(R.string.create_group_chat_add)
        if (mViewModel.contacts.size != 0) {
            text = getString(R.string.create_group_chat_add) + "(${mViewModel.contacts.size})"
            createBtn.isEnabled = true
        } else {
            createBtn.isEnabled = false
        }
        createBtn.text = text
    }
}