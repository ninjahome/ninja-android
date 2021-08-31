package com.ninja.android.lib.viewadapter.view

import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.databinding.BindingAdapter
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.command.ResponseCommand
import com.ninja.android.lib.utils.clickWithTrigger
import com.ninja.android.lib.view.MessageBubbleView

/**
 * requireAll 是意思是是否需要绑定全部参数, false为否
 * View的onClick事件绑定
 * onClickCommand 绑定的命令,
 * isThrottleFirst 是否开启防止过快点击
 */
@BindingAdapter("onClickCommand")
fun onClickCommand(view: View?, clickCommand: BindingCommand<*>?) {
    view?.clickWithTrigger {
        clickCommand?.execute()
    }
}

/**
 * view的onLongClick事件绑定
 */
@BindingAdapter(value = ["onLongClickCommand"], requireAll = false)
fun onLongClickCommand(view: View, clickCommand: BindingCommand<*>) {
    view.setOnLongClickListener {
        clickCommand.execute()
        false
    }
}



/**
 * 回调控件本身
 *
 * @param currentView
 * @param bindingCommand
 */
@BindingAdapter(value = ["currentView"], requireAll = false)
fun replyCurrentView(currentView: View, bindingCommand: BindingCommand<View>) {
    bindingCommand.execute(currentView)
}

/**
 * view是否需要获取焦点
 */
@BindingAdapter("requestFocus")
fun requestFocusCommand(view: View, needRequestFocus: Boolean) {
    if (needRequestFocus) {
        view.isFocusableInTouchMode = true
        view.requestFocus()
    } else {
        view.clearFocus()
    }
}

/**
 * view的焦点发生变化的事件绑定
 */
@BindingAdapter("onFocusChangeCommand")
fun onFocusChangeCommand(view: View, onFocusChangeCommand: BindingCommand<Boolean?>?) {
    view.onFocusChangeListener =
        OnFocusChangeListener { _, hasFocus -> onFocusChangeCommand?.execute(hasFocus) }
}

/**
 * view的显示隐藏
 */
@BindingAdapter(value = ["isVisible"])
fun isVisible(view: View, visibility: Boolean) {
    if (visibility) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

    @BindingAdapter("onTouchCommand")
   fun onTouchCommand(view:View, onTouchCommand : ResponseCommand<MotionEvent, Boolean>) {
        view.setOnTouchListener { _, event ->
            onTouchCommand.execute(event)
            false
        }
    }