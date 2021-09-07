package com.ninjahome.ninja.ui.activity.authorization

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityAuthorizationFriendBinding
import com.ninjahome.ninja.db.ContactDBManager
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.ui.activity.contact.ContactDetailActivity
import com.ninjahome.ninja.ui.adapter.ContactAdapter
import com.ninjahome.ninja.view.ContactsUtils
import com.ninjahome.ninja.viewmodel.AuthorizationFriendViewModel
import kotlinx.android.synthetic.main.activity_authorization_friend.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:2021/9/7
 *Description:
 */
class AuthorizationFriendActivity : BaseActivity<AuthorizationFriendViewModel, ActivityAuthorizationFriendBinding>(R.layout.activity_authorization_friend) {
    private val contactAdapter: ContactAdapter by lazy { ContactAdapter(this) }
    private var nameList = mutableListOf<Contact>()
    override val mViewModel: AuthorizationFriendViewModel by viewModel()

    override fun initView() {
        mViewModel.title.set(getString(R.string.authorization_select_friend))
        val layoutManager = LinearLayoutManager(this)
        friendRecyclerView.layoutManager = layoutManager
        friendRecyclerView.adapter = contactAdapter

        contactAdapter.clickItemListener = object : ContactAdapter.ClickItemListener {
            override fun clickItem(position: Int, contact: Contact) {

                val intent = Intent(this@AuthorizationFriendActivity, AuthorizationFriendDaysActivity::class.java)
                intent.putExtra(IntentKey.CONTACT, contact)
                startActivity(intent)
            }

        }
    }

    override fun initData() {
    }

    override fun initObserve() {
        ContactDBManager.all().observe(this) {
            it?.let { it1 ->
                nameList.addAll(it1)
                ContactsUtils.sortData(nameList)
                contactAdapter.addAll(nameList)
            }

        }
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel
}