package com.ninja.android.lib.viewadapter.mswitch

import android.os.Handler
import android.os.Looper
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.BindingAdapter
import com.ninja.android.lib.command.BindingCommand


@BindingAdapter("switchState")
fun setSwitchState(mSwitch: SwitchCompat, isChecked: Boolean) {
    mSwitch.isChecked = isChecked
}

/**
 * Switch的状态改变监听
 *
 * @param mSwitch        Switch控件
 * @param changeListener 事件绑定命令
 */
@BindingAdapter("onCheckedChangeCommand")
fun onCheckedChangeCommand(mSwitch: SwitchCompat, changeListener: BindingCommand<Boolean?>?) {
    if (changeListener != null) {
        //延迟设置监听。防止第一次设置值也去执行回调
        Handler(Looper.getMainLooper()).postDelayed({
            mSwitch.setOnCheckedChangeListener { _, isChecked ->
                changeListener.execute(isChecked)
            }
        },200)

    }
}