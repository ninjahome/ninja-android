package com.ninjahome.ninja.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import chatLib.ChatLib
import com.ninjahome.ninja.view.navigator.BottomNavigatorAdapter
import com.ninjahome.ninja.view.navigator.FragmentNavigator
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.event.TotalUnReadNumber
import com.ninja.android.lib.utils.toast
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityMainBinding
import com.ninjahome.ninja.event.EventOffline
import com.ninjahome.ninja.ui.activity.activation.ActivationActivity
import com.ninjahome.ninja.ui.fragment.contact.ContactListFragment
import com.ninjahome.ninja.ui.fragment.conversation.ConversationListFragment
import com.ninjahome.ninja.ui.fragment.my.MyFragment
import com.ninjahome.ninja.utils.ConnectionStateMonitor
import com.ninjahome.ninja.utils.ConversationManager
import com.ninjahome.ninja.utils.DialogUtils
import com.ninjahome.ninja.view.RechargePop
import com.ninjahome.ninja.view.navigator.BottomNavigatorView
import com.ninjahome.ninja.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(R.layout.activity_main) {
    private val mFragmentArray = arrayOf(ConversationListFragment::class.java, ContactListFragment::class.java, MyFragment::class.java)
    private lateinit var mNavigator: FragmentNavigator
    val connectionStateMonitor = ConnectionStateMonitor()


    override val mViewModel: MainViewModel by viewModel()

    override fun create(savedInstanceState: Bundle?) {
        initNavigator(savedInstanceState)
    }

    private fun initNavigator(savedInstanceState: Bundle?) {
        val bottomNavigatorAdapter = BottomNavigatorAdapter(this)
        for (fragment in mFragmentArray) {
            bottomNavigatorAdapter.addTab(BottomNavigatorAdapter.TabInfo(fragment.simpleName, fragment, null))
        }
        mNavigator = FragmentNavigator(supportFragmentManager, bottomNavigatorAdapter, R.id.content_frame)
        mNavigator.setDefaultPosition(Constants.TAB_CONVERSATION)
        mNavigator.onCreate(savedInstanceState)
    }
    override fun initView() {
        EventBus.getDefault().register(this)
        lifecycle.addObserver(ConversationManager)
        connectionStateMonitor.enable(this)
        setCurrentTab(Constants.TAB_CONVERSATION)
        navigatorView.setOnBottomNavigatorViewItemClickListener(object : BottomNavigatorView.OnBottomNavigatorViewItemClickListener {
            override fun onBottomNavigatorViewItemClick(position: Int, view: View?) {
                setCurrentTab(position)
            }
        })
        navigatorView.badgeDragListener = object : BottomNavigatorView.BadgeDragListener{
            override fun onDisappear(index: Int) {
                mViewModel.clearUnreadNumber()
            }

        }


    }

    private fun setCurrentTab(position: Int) {
        mNavigator.showFragment(position)
        if (null != navigatorView) {
            navigatorView.select(position)
        }
    }
    override fun initData() {
        mViewModel.getExpireTime()
    }

    override fun initObserve() {
        mViewModel.expireTime.observe(this) {
            if (it * 1000 < System.currentTimeMillis()) {
                EventBus.getDefault().postSticky(EventOffline())
                DialogUtils.showRechargeDialog(this@MainActivity, object : RechargePop.ClickListener {
                    override fun clickSure() {
                        startActivationActivity()
                    }

                    override fun clickCancel() {
                    }

                })
            }else{
                mViewModel.online()
            }
        }

    }

    private fun startActivationActivity() {
        startActivity(Intent(this, ActivationActivity::class.java))
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun showUnreadNumber(number: TotalUnReadNumber){
        navigatorView.showBadgeView(0,number.number,true)
    }
    override fun onDestroy() {
        super.onDestroy()
        ChatLib.wsOffline()
        EventBus.getDefault().unregister(this)


    }


}