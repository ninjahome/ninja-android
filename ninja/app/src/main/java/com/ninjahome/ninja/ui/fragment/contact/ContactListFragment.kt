package com.ninjahome.ninja.ui.fragment.contact

import android.animation.ObjectAnimator
import android.content.Intent
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.AttachListPopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.ninja.android.lib.base.BaseFragment
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.FragmentContactListBinding
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.db.ContactDBManager
import com.ninjahome.ninja.ui.activity.contact.ContactDetailActivity
import com.ninjahome.ninja.ui.activity.groupchat.GroupChatCreateActivity
import com.ninjahome.ninja.ui.activity.search.SearchContactActivity
import com.ninjahome.ninja.ui.adapter.ContactAdapter
import com.ninjahome.ninja.view.ContactsUtils
import com.ninjahome.ninja.view.contacts.CustomItemDecoration
import com.ninjahome.ninja.view.contacts.itemanimator.SlideInOutLeftItemAnimator
import com.ninjahome.ninja.viewmodel.ContactListViewModel
import com.zhy.autolayout.utils.ScreenUtils
import kotlinx.android.synthetic.main.fragment_contact_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ContactListFragment : BaseFragment<ContactListViewModel, FragmentContactListBinding>(R.layout.fragment_contact_list) {
    val ADD_FRIEND = 0
    lateinit var rightIv: ImageView
    lateinit var moreActionPop: AttachListPopupView
    lateinit var animator: ObjectAnimator
    lateinit var animatorRecover: ObjectAnimator

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
                intent.putExtra(IntentKey.ID, contact.uid)
                startActivity(intent)
            }

        }
        rightIv = mDatabinding.root.findViewById(R.id.title_right_iv)
        animator = ObjectAnimator.ofFloat(rightIv, "rotation", 0.0F, 45.0F)
        animatorRecover = ObjectAnimator.ofFloat(rightIv, "rotation", -45.0F, 0.0F)
    }

    private fun initMarginTop() {
        val layoutParams = status_bar_view.layoutParams
        layoutParams.height = ScreenUtils.getStatusBarHeight(mActivity)
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

        mViewModel.showPop.observe(this) {
            animator.start()
            showPop()

        }


    }

    override fun initData() {

    }

    private fun showPop() {
        if (!this::moreActionPop.isInitialized) {
            moreActionPop = XPopup.Builder(context).hasShadowBg(false).popupAnimation(PopupAnimation.ScrollAlphaFromTop).atView(rightIv).setPopupCallback(object : SimpleCallback() {

                override fun beforeDismiss(popupView: BasePopupView?) {
                    super.beforeDismiss(popupView)
                    animatorRecover.start()
                }
            }).asAttachList(arrayOf(resources.getString(R.string.add_friend), resources.getString(R.string.create_group_chat)), intArrayOf(R.drawable.home_add_friend, R.drawable.home_group_chat), { position, text ->
                if (position == ADD_FRIEND) {
                    startActivity(SearchContactActivity::class.java)
                } else {
                    startActivity(GroupChatCreateActivity::class.java)
                }
            }, 0, 0)

        }
        moreActionPop.show()
    }

}