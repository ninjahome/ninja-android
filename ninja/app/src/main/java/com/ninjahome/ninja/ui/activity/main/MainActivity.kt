package com.ninjahome.ninja.ui.activity.main

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import chatLib.ChatLib
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.event.TotalUnReadNumber
import com.ninja.android.lib.utils.toast
import com.ninja.android.lib.view.MessageBubbleView
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityMainBinding
import com.ninjahome.ninja.event.EventOffline
import com.ninjahome.ninja.ui.activity.activation.ActivationActivity
import com.ninjahome.ninja.ui.adapter.MainFragmentPagerAdapter
import com.ninjahome.ninja.utils.ConnectionStateMonitor
import com.ninjahome.ninja.utils.ConversationManager
import com.ninjahome.ninja.utils.DialogUtils
import com.ninjahome.ninja.view.RechargePop
import com.ninjahome.ninja.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tab_item.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(R.layout.activity_main) {
    private val tabIcons = arrayListOf(R.drawable.tab_message, R.drawable.tab_contact, R.drawable.tab_my)
    private val tabName = arrayListOf(R.string.message, R.string.contact, R.string.my)
    val connectionStateMonitor = ConnectionStateMonitor()


    override val mViewModel: MainViewModel by viewModel()


    override fun initView() {
        EventBus.getDefault().register(this)
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
        if(index==0){
            item.findViewById<MessageBubbleView>(R.id.bubbleView).setOnActionListener(object:MessageBubbleView.ActionListener{
                override fun onDrag() {
                }

                override fun onDisappear() {
                    mViewModel.clearUnreadNumber()
                }

                override fun onRestore() {
                }

                override fun onMove() {
                }

            })
        }
        return item
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
        val bubbleView = tabLayout.getChildAt(0).bubbleView
        if(number.number==0){
            bubbleView.visibility = View.GONE
        }else{
            bubbleView.visibility = View.VISIBLE
            bubbleView.resetBezierView()
        }

        bubbleView.setNumber(number.number.toString())
    }
    override fun onDestroy() {
        super.onDestroy()
        ChatLib.wsOffline()
        EventBus.getDefault().unregister(this)


    }


}