package com.ninjahome.ninja.ui.fragment.conversation

import android.os.Handler
import android.os.Looper
import androidlib.Androidlib
import androidlib.Androidlib.*
import com.ninja.android.lib.base.BaseFragment
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.FragmentConversationListBinding
import com.ninjahome.ninja.event.*
import com.ninjahome.ninja.model.bean.*
import com.ninjahome.ninja.room.ConversationDBManager
import com.ninjahome.ninja.viewmodel.ConversationItemViewModel
import com.ninjahome.ninja.viewmodel.ConversationListViewModel
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.fragment_conversation_list.*
import kotlinx.android.synthetic.main.fragment_conversation_list.swipeRefreshLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class ConversationListFragment : BaseFragment<ConversationListViewModel, FragmentConversationListBinding>(R.layout.fragment_conversation_list), Handler.Callback {
    override val mViewModel: ConversationListViewModel by viewModel()
    private val handler: Handler by lazy { Handler(Looper.getMainLooper(), this@ConversationListFragment) }

    override fun initView() {
        EventBus.getDefault().register(this)
        recyclerView.itemAnimator = null
    }

    override fun initData() {

    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
        mViewModel.finishRefreshingEvent.observe(this) {
            setLineState()
            swipeRefreshLayout.isRefreshing = false

        }

        ConversationDBManager.all().observe(this) {
            mViewModel.items.clear()
            it?.forEach {
                mViewModel.items.add(ConversationItemViewModel(mViewModel, it))
            }
        }
    }


    override fun onShow() {
        super.onShow()
        setLineState()
    }

    override fun onResume() {
        super.onResume()
        setLineState()
    }

    fun setLineState() {
        handler.postDelayed({
            mViewModel.unline.value = !Androidlib.wsIsOnline()
        }, 1000)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun offline(eventOffline: EventOffline) {
        setLineState()
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun handleMessage(msg: android.os.Message): Boolean {
        return false
    }

}