package com.ninjahome.ninja.ui.fragment.conversation

import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import chatLib.ChatLib
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.AttachListPopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.ninja.android.lib.base.BaseFragment
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.FragmentConversationListBinding
import com.ninjahome.ninja.event.*
import com.ninjahome.ninja.model.bean.*
import com.ninjahome.ninja.room.ConversationDBManager
import com.ninjahome.ninja.ui.activity.groupchat.GroupChatCreateActivity
import com.ninjahome.ninja.ui.activity.search.SearchContactActivity
import com.ninjahome.ninja.viewmodel.ConversationItemViewModel
import com.ninjahome.ninja.viewmodel.ConversationListViewModel
import com.zhy.autolayout.utils.ScreenUtils
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.fragment_contact_list.*
import kotlinx.android.synthetic.main.fragment_conversation_list.*
import kotlinx.android.synthetic.main.fragment_conversation_list.status_bar_view
import kotlinx.android.synthetic.main.fragment_conversation_list.swipeRefreshLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ConversationListFragment : BaseFragment<ConversationListViewModel, FragmentConversationListBinding>(R.layout.fragment_conversation_list), Handler.Callback {
    val ADD_FRIEND = 0
    lateinit var animator: ObjectAnimator
    lateinit var animatorRecover: ObjectAnimator
    var isRotate = false
    lateinit var rightIv: ImageView
    lateinit var moreActionPop: AttachListPopupView

    override val mViewModel: ConversationListViewModel by viewModel()
    private val handler: Handler by lazy { Handler(Looper.getMainLooper(), this@ConversationListFragment) }
    override fun initView() {
        EventBus.getDefault().register(this)
        rightIv = mDatabinding.root.findViewById(R.id.title_right_iv)
        animator = ObjectAnimator.ofFloat(rightIv, "rotation", 0.0F, 45.0F)
        animatorRecover = ObjectAnimator.ofFloat(rightIv, "rotation", -45.0F, 0.0F)

        initMarginTop()
    }

    private fun initMarginTop() {
        val layoutParams = status_bar_view.layoutParams
        layoutParams.height = ScreenUtils.getStatusBarHeight(mActivity)
        status_bar_view.layoutParams = layoutParams
    }

    override fun initData() {

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

        ConversationDBManager.all().observe(this) {
            mViewModel.items.clear()
            it?.forEach {
                mViewModel.items.add(ConversationItemViewModel(mViewModel, it))
            }
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
            mViewModel.unline.value = !ChatLib.wsIsOnline()
        }, 1000)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun offline(eventOffline: EventOffline) {
        setLineState()
    }

    override fun handleMessage(msg: android.os.Message): Boolean {
        return false
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}