package com.ninja.android.lib.viewadapter.checkbox

import android.widget.CheckBox
import androidx.databinding.BindingAdapter
import com.ninja.android.lib.command.BindingCommand


@BindingAdapter(value = ["onCheckedChangedCommand"], requireAll = false)
fun setCheckedChanged(
    checkBox: CheckBox,
    bindingCommand: BindingCommand<Boolean>
) {
    checkBox.setOnCheckedChangeListener { compoundButton, b -> bindingCommand.execute(b) }
}