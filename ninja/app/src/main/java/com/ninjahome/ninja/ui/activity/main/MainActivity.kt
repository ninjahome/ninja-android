package com.ninjahome.ninja.ui.activity.main

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidlib.Androidlib
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.NinjaApp
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityMainBinding
import com.ninjahome.ninja.ui.adapter.MainFragmentPagerAdapter
import com.ninjahome.ninja.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

@KoinApiExtension
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(R.layout.activity_main) {
    private val TAB_MESSAGE = 0
    private val TAB_CONTACT = 1
    private val tabIcons = arrayListOf(R.drawable.tab_message, R.drawable.tab_contact, R.drawable.tab_my)
    private val tabName = arrayListOf(R.string.message, R.string.contact, R.string.my)
    override val mViewModel: MainViewModel by viewModel()

    override fun initView() {
        viewPager.adapter = MainFragmentPagerAdapter(supportFragmentManager)
        viewPager.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(viewPager)
        initTabLayout()

    }

    private fun initTabLayout() {
        tabIcons.forEachIndexed { index, i ->
            tabLayout.getTabAt(index)!!.setCustomView(getTabItemView(index))

        }

    }

    private fun getTabItemView(index: Int): View {
        val item = View.inflate(this, R.layout.tab_item, null)
        item.findViewById<TextView>(R.id.tabName).setText(tabName[index])
        item.findViewById<ImageView>(R.id.tabIcon).setBackgroundResource(tabIcons[index])
        return item
    }

    override fun initData() {
    }

    override fun initObserve() {

    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

    var last: Long = -1

    override fun onBackPressed() {
        val now = System.currentTimeMillis()
        if (last == -1L) {
            toast(getString(R.string.main_click_exit_application))
            last = now
        } else {
            val doubleClickDifference = 2000
            if (now - last < doubleClickDifference) {
                finish()
            } else {
                last = now
                toast(getString(R.string.main_click_exit_application))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Androidlib.wsOffline()
        NinjaApp.instance.conversations.clear()
    }


}