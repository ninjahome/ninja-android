package com.ninjahome.ninja.ui.fragment.contact

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.ninja.android.lib.base.BaseFragment
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.FragmentContactListBinding
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.ui.activity.contact.ContactDetailActivity
import com.ninjahome.ninja.ui.adapter.ContactAdapter
import com.ninjahome.ninja.view.ContactsUtils
import com.ninjahome.ninja.view.contacts.CustomItemDecoration
import com.ninjahome.ninja.view.contacts.itemanimator.SlideInOutLeftItemAnimator
import com.ninjahome.ninja.viewmodel.ContactListViewModel
import com.zhy.autolayout.utils.ScreenUtils
import kotlinx.android.synthetic.main.fragment_contact_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class ContactListFragment : BaseFragment<ContactListViewModel, FragmentContactListBinding>(R.layout.fragment_contact_list) {
    private var decoration: CustomItemDecoration? = null
    private val contactAdapter: ContactAdapter by lazy { ContactAdapter(mActivity) }
    private var nameList = mutableListOf<Contact>()
    private var layoutManager: LinearLayoutManager? = null

    override val mViewModel: ContactListViewModel by viewModel()

    override fun initView() {
        initMarginTop()
        layoutManager = LinearLayoutManager(mActivity)
        contactsRecyclerView.layoutManager = layoutManager
        contactsRecyclerView.addItemDecoration(CustomItemDecoration(mActivity).also {
            decoration = it
        })
        contactsRecyclerView.itemAnimator = SlideInOutLeftItemAnimator(contactsRecyclerView)
        contactsRecyclerView.adapter = contactAdapter

        contactAdapter.clickItemListener = object : ContactAdapter.ClickItemListener {
            override fun clickItem(position: Int, contact: Contact) {

                val intent = Intent(mActivity, ContactDetailActivity::class.java)
                intent.putExtra(IntentKey.UID, contact.uid)
                startActivity(intent)
            }

        }

    }

    private fun initMarginTop() {
        val layoutParams = status_bar_view.layoutParams
        layoutParams.height =  ScreenUtils.getStatusBarHeight(mActivity)
        status_bar_view.layoutParams = layoutParams
    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
        ContactDBManager.all().observe(this) {
            nameList.clear()
            it?.let { it1 ->
                nameList.addAll(it1)
                ContactsUtils.sortData(nameList)
                //返回一个包含所有Tag字母在内的字符串并赋值给tagsStr
                val tagsStr: String = ContactsUtils.getTags(nameList)
                decoration!!.setDatas(nameList, tagsStr)
                contactAdapter.addAll(nameList)
            }

        }


    }

    override fun initData() {

    }


}