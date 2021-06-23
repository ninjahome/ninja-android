package com.ninjahome.ninja.ui.fragment.contact

import android.content.Intent
import android.text.TextUtils
import androidx.lifecycle.rxLifeScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ninja.android.lib.base.BaseFragment
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.FragmentContactListBinding
import com.ninjahome.ninja.event.EventRefreshContact
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.room.ContactDBManager
import com.ninjahome.ninja.ui.activity.contact.ContactDetailActivity
import com.ninjahome.ninja.ui.adapter.ContactAdapter
import com.ninjahome.ninja.view.ContactsUtils
import com.ninjahome.ninja.view.contacts.CustomItemDecoration
import com.ninjahome.ninja.view.contacts.SideBar
import com.ninjahome.ninja.view.contacts.itemanimator.SlideInOutLeftItemAnimator
import com.ninjahome.ninja.viewmodel.ContactListViewModel
import kotlinx.android.synthetic.main.fragment_contact_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ContactListFragment : BaseFragment<ContactListViewModel, FragmentContactListBinding>(R.layout.fragment_contact_list) {
    private var decoration: CustomItemDecoration? = null
    lateinit var contactAdapter: ContactAdapter
    var nameList = mutableListOf<Contact>()
    private var layoutManager: LinearLayoutManager? = null

    override val mViewModel: ContactListViewModel by viewModel()

    override fun initView() {
        EventBus.getDefault().register(this)
        contactAdapter = ContactAdapter(mActivity)
        layoutManager = LinearLayoutManager(mActivity)
        contactsRecyclerView.layoutManager = layoutManager
        contactsRecyclerView.addItemDecoration(CustomItemDecoration(mActivity).also {
            decoration = it
        })
        contactsRecyclerView.itemAnimator = SlideInOutLeftItemAnimator(contactsRecyclerView)
        contactsRecyclerView.adapter = contactAdapter

        sideBar.setIndexChangeListener(object : SideBar.indexChangeListener {

            override fun indexChanged(tag: String?) {
                if (TextUtils.isEmpty(tag) || nameList.size <= 0) return
                for (i in nameList.indices) {
                    if (tag == nameList[i].indexTag) {
                        layoutManager!!.scrollToPositionWithOffset(i, 0)
                        return
                    }
                }
            }
        })

        contactAdapter.clickItemListener = object : ContactAdapter.ClickItemListener {
            override fun clickItem(position: Int, contact: Contact) {

                val intent = Intent(mActivity, ContactDetailActivity::class.java)
                intent.putExtra(IntentKey.UID, contact.uid)
                startActivity(intent)
            }

        }
    }


    override fun initData() {

        nameList.clear()
        rxLifeScope.launch {
            val names = ContactDBManager.all()
            names?.let { nameList.addAll(it) }

            //对数据源进行排序
            ContactsUtils.sortData(nameList)
            //返回一个包含所有Tag字母在内的字符串并赋值给tagsStr
            val tagsStr: String = ContactsUtils.getTags(nameList)
            sideBar.setIndexStr(tagsStr)
            decoration!!.setDatas(nameList, tagsStr)
            contactAdapter.addAll(nameList)
        }


    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun reloadIDcard(event: EventRefreshContact) {
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}