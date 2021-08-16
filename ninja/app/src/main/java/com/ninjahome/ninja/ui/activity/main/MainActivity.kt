package com.ninjahome.ninja.ui.activity.main

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import chatLib.ChatLib
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityMainBinding
import com.ninjahome.ninja.ui.adapter.MainFragmentPagerAdapter
import com.ninjahome.ninja.utils.ConnectionStateMonitor
import com.ninjahome.ninja.utils.ConversationManager
import com.ninjahome.ninja.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(R.layout.activity_main){
    private val tabIcons = arrayListOf(R.drawable.tab_message, R.drawable.tab_contact, R.drawable.tab_my)
    private val tabName = arrayListOf(R.string.message, R.string.contact, R.string.my)
    val connectionStateMonitor = ConnectionStateMonitor()
    override val mViewModel: MainViewModel by viewModel()


    override fun initView() {
        viewPager.adapter = MainFragmentPagerAdapter(supportFragmentManager)
        viewPager.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(viewPager)
        initTabLayout()
        lifecycle.addObserver(ConversationManager)
        connectionStateMonitor.enable(this)

    }

    private fun initTabLayout() {
        tabIcons.forEachIndexed { index, _ ->
            tabLayout.getTabAt(index)!!.customView = getTabItemView(index)
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

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_TRANSPARENT

    override fun initVariableId(): Int = BR.viewModel

    private var last: Long = -1

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
        ChatLib.wsOffline()


    }




}