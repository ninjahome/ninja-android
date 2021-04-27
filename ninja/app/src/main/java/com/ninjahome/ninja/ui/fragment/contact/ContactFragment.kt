package com.ninjahome.ninja.ui.fragment.contact

import android.text.TextUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.ninja.android.lib.base.BaseFragment
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.databinding.FragmentContactBinding
import com.ninjahome.ninja.ui.adapter.ContactAdapter
import com.ninjahome.ninja.view.ContactsUtils
import com.ninjahome.ninja.view.contacts.CustomItemDecoration
import com.ninjahome.ninja.view.contacts.SideBar
import com.ninjahome.ninja.view.contacts.itemanimator.SlideInOutLeftItemAnimator
import com.ninjahome.ninja.viewmodel.ContactViewModel
import kotlinx.android.synthetic.main.fragment_contact.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ContactFragment :
    BaseFragment<ContactViewModel, FragmentContactBinding>(R.layout.fragment_contact) {
    private var decoration: CustomItemDecoration? = null
    lateinit var contactAdapter: ContactAdapter
    var nameList = mutableListOf<Contact>()
    private var layoutManager: LinearLayoutManager? = null

    override val mViewModel: ContactViewModel by viewModel()

    override fun initView() {
        contactAdapter = ContactAdapter(context)
        layoutManager = LinearLayoutManager(context)
        contactsRecyclerView.setLayoutManager(layoutManager)
        contactsRecyclerView.addItemDecoration(CustomItemDecoration(mActivity).also {
            decoration = it
        })
        contactsRecyclerView.setItemAnimator(SlideInOutLeftItemAnimator(contactsRecyclerView))
        contactsRecyclerView.setAdapter(contactAdapter)

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
    }


    override fun initData() {
        val names = arrayOf(
            "孙尚香", "安其拉", "白起", "不知火舞", "@小马快跑", "_德玛西亚之力_", "妲己", "狄仁杰", "典韦", "韩信",
            "老夫子", "刘邦", "刘禅", "鲁班七号", "墨子", "孙膑", "孙尚香", "孙悟空", "项羽", "亚瑟",
            "周瑜", "庄周", "蔡文姬", "甄姬", "廉颇", "程咬金", "后羿", "扁鹊", "钟无艳", "小乔", "王昭君", "虞姬",
            "李元芳", "张飞", "刘备", "牛魔", "张良", "兰陵王", "露娜", "貂蝉", "达摩", "曹操", "芈月", "荆轲", "高渐离",
            "钟馗", "花木兰", "关羽", "李白", "宫本武藏", "吕布", "嬴政", "娜可露露", "武则天", "赵云", "姜子牙"
        )
        for (name in names) {
            val bean = Contact()
            bean.name = name
            nameList.add(bean)
        }
        //对数据源进行排序
        ContactsUtils.sortData(nameList)
        //返回一个包含所有Tag字母在内的字符串并赋值给tagsStr
        val tagsStr: String = ContactsUtils.getTags(nameList)
        sideBar.setIndexStr(tagsStr)
        decoration!!.setDatas(nameList, tagsStr)
        contactAdapter.addAll(nameList)

    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
    }


}