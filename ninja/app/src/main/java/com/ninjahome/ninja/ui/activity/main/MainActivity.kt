package com.ninjahome.ninja.ui.activity.main

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import com.gyf.immersionbar.ImmersionBar
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityMainBinding
import com.ninjahome.ninja.ui.adapter.MainFragmentPagerAdapter
import com.ninjahome.ninja.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(R.layout.activity_main) {
    private val TAB_MESSAGE = 0
    private val TAB_CONTACT = 1
    private val tabIcons =
        arrayListOf(R.drawable.tab_message, R.drawable.tab_contact, R.drawable.tab_my)
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
            val tab = tabLayout.getTabAt(index)!!.setCustomView(getTabItemView(index))

        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    TAB_MESSAGE, TAB_CONTACT -> ImmersionBar.with(this@MainActivity)
                        .fitsSystemWindows(true)
                        .statusBarColor(R.color.white)
                        .init()
                    else -> {
                        ImmersionBar.with(this@MainActivity).transparentBar().fullScreen(false)
                            .statusBarDarkFont(true).init()
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

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


}