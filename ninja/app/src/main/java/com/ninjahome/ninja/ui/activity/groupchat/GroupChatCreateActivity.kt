package com.ninjahome.ninja.ui.activity.groupchat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.core.BasePopupView
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityCreateGroupChatBinding
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.ui.adapter.CreateGroupAdapter
import com.ninjahome.ninja.utils.DialogUtils
import com.ninjahome.ninja.view.ContactsUtils
import com.ninjahome.ninja.view.CreateGroupChatPop
import com.ninjahome.ninja.view.contacts.CustomItemDecoration
import com.ninjahome.ninja.view.contacts.itemanimator.SlideInOutLeftItemAnimator
import com.ninjahome.ninja.viewmodel.CreateGroupChatIconItemViewModel
import com.ninjahome.ninja.viewmodel.CreateGroupChatViewModel
import kotlinx.android.synthetic.main.activity_create_group_chat.*
import kotlinx.android.synthetic.main.fragment_contact_list.contactsRecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class GroupChatCreateActivity : BaseActivity<CreateGroupChatViewModel, ActivityCreateGroupChatBinding>(R.layout.activity_create_group_chat), CreateGroupAdapter.ClickItemListener {

    private lateinit var createGroupChatDialog: BasePopupView
    private var decoration: CustomItemDecoration = CustomItemDecoration(this)
    private var layoutManager: LinearLayoutManager? = null
    private val contactAdapter: CreateGroupAdapter by lazy { CreateGroupAdapter(this) }

    override val mViewModel: CreateGroupChatViewModel by viewModel()

    override fun initView() {
        mViewModel.title.set(getString(R.string.contact_group_create))
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

    }

    override fun initObserve() {
        mViewModel.allContact.observe(this) {
            it?.let {
                contactAdapter.addAll(it)
                ContactsUtils.sortData(it)
                //返回一个包含所有Tag字母在内的字符串并赋值给tagsStr
                val tagsStr: String = ContactsUtils.getTags(it)
                decoration.setDatas(it, tagsStr)
                contactAdapter.addAll(it)
            }

        }

        mViewModel.showCreateGroupChatPop.observe(this) {
            createGroupChatDialog = DialogUtils.showCreateGroupChatDialog(this@GroupChatCreateActivity, object : CreateGroupChatPop.ClickListener {
                override fun clickSure(name: String) {
                    toast(name)
                    createGroupChatDialog.dismiss()
                }

                override fun clickNoName() {
                    toast("没有群名创建")
                    createGroupChatDialog.dismiss()
                }

            })
        }


    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

    override fun clickDelete(position: Int, contact: Contact) {
        mViewModel.contacts.remove(contact)
        setCreateBtnText()
        mViewModel.contactIconItem.clear()
        mViewModel.contacts.forEach {
            mViewModel.contactIconItem.add(CreateGroupChatIconItemViewModel(mViewModel, it))
        }
    }

    override fun clickAdd(position: Int, contact: Contact) {
        mViewModel.contacts.add(contact)
        setCreateBtnText()
        mViewModel.contactIconItem.clear()
        mViewModel.contacts.forEach {
            mViewModel.contactIconItem.add(CreateGroupChatIconItemViewModel(mViewModel, it))
        }
    }

    private fun setCreateBtnText() {
        var text = getString(R.string.contact_group_chat_complete)
        if (mViewModel.contacts.size != 0) {
            text = getString(R.string.contact_group_chat_complete) + "(${mViewModel.contacts.size})"
            createBtn.isEnabled = true
        } else {
            createBtn.isEnabled = false
        }
        createBtn.text = text
    }
}