package com.ninjahome.ninja.ui.fragment.conversation

import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.lifecycle.rxLifeScope
import androidx.recyclerview.widget.ItemTouchHelper
import chatLib.ChatLib
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.AttachListPopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.ninja.android.lib.base.BaseFragment
import com.ninja.android.lib.event.TotalUnReadNumber
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.FragmentConversationListBinding
import com.ninjahome.ninja.db.ConversationDBManager
import com.ninjahome.ninja.event.*
import com.ninjahome.ninja.model.bean.*
import com.ninjahome.ninja.ui.activity.groupchat.GroupChatCreateActivity
import com.ninjahome.ninja.ui.activity.search.SearchContactActivity
import com.ninjahome.ninja.ui.adapter.ItemTouchHelperCallback
import com.ninjahome.ninja.viewmodel.ConversationItemViewModel
import com.ninjahome.ninja.viewmodel.ConversationListViewModel
import com.zhy.autolayout.utils.ScreenUtils
import kotlinx.android.synthetic.main.activity_apply_list.*
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.fragment_contact_list.*
import kotlinx.android.synthetic.main.fragment_conversation_list.*
import kotlinx.android.synthetic.main.fragment_conversation_list.recyclerView
import kotlinx.android.synthetic.main.fragment_conversation_list.status_bar_view
import kotlinx.android.synthetic.main.fragment_conversation_list.swipeRefreshLayout
import kotlinx.coroutines.flow.collect
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ConversationListFragment : BaseFragment<ConversationListViewModel, FragmentConversationListBinding>(R.layout.fragment_conversation_list), Handler.Callback, ItemTouchHelperCallback.RemoveCallBack {
    val ADD_FRIEND = 0
    var sumUnreadNumber = 0
    lateinit var animator: ObjectAnimator
    lateinit var animatorRecover: ObjectAnimator
    lateinit var rightIv: ImageView
    lateinit var moreActionPop: AttachListPopupView
    var delayRunnable : DelayRunnable? = null

    override val mViewModel: ConversationListViewModel by viewModel()
    private val handler: Handler by lazy { Handler(Looper.getMainLooper(), this@ConversationListFragment) }
    override fun initView() {
        EventBus.getDefault().register(this)
        rightIv = mDatabinding.root.findViewById(R.id.title_right_iv)
        animator = ObjectAnimator.ofFloat(rightIv, "rotation", 0.0F, 45.0F)
        animatorRecover = ObjectAnimator.ofFloat(rightIv, "rotation", -45.0F, 0.0F)
        recyclerView.itemAnimator = null
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(this))
        itemTouchHelper.attachToRecyclerView(recyclerView)
        initMarginTop()
    }

    private fun initMarginTop() {
        val layoutParams = status_bar_view.layoutParams
        layoutParams.height = ScreenUtils.getStatusBarHeight(mActivity)
        status_bar_view.layoutParams = layoutParams
    }

    override fun initData() {

        rxLifeScope.launch {
            ConversationDBManager.all().collect {
                if(delayRunnable!=null){
                    handler.removeCallbacks(delayRunnable!!)
                }
                delayRunnable = DelayRunnable(it)
                println("执行了 获取到消息")
                handler.postDelayed(delayRunnable!!, 100)
            }

        }

    }

    inner class DelayRunnable(val list: List<Conversation>?) : Runnable {
        override fun run() {
            mViewModel.items.clear()
            sumUnreadNumber = 0
            list?.forEach {
                sumUnreadNumber += it.unreadCount
                mViewModel.items.add(ConversationItemViewModel(mViewModel, it))
            }
            println("执行了消息刷新")
            EventBus.getDefault().post(TotalUnReadNumber(sumUnreadNumber))
        }

    }

    override fun initVariableId(): Int = BR.viewModel

    override fun initObserve() {
        mViewModel.finishRefreshingEvent.observe(this) {
            setLineState()
            swipeRefreshLayout.isRefreshing = false
        }

        mViewModel.showPop.observe(this) {
            animator.start()
            showPop()
        }
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

    fun setLineState() {
        handler.postDelayed({
            mViewModel.online.value = ChatLib.wsIsOnline()
        }, 500)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun offline(eventOffline: EventOffline) {
        setLineState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun online(eventOnline: EventOnline) {
        setLineState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun netWorkChange(eventNetWorkChange: EventNetWorkChange) {
        mViewModel.refreshCommand.execute()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun activationSuccess(eventActivationSuccess: EventActivationSuccess) {
        mViewModel.refreshCommand.execute()
    }

    override fun handleMessage(msg: android.os.Message): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun remove(index: Int) {
        mViewModel.removeItemAt(mViewModel.items.get(index).conversation)
    }

}