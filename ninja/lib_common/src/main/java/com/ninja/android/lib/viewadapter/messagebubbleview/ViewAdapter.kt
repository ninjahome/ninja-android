package com.ninja.android.lib.viewadapter.messagebubbleview

import android.view.View
import androidx.databinding.BindingAdapter
import com.ninja.android.lib.command.BindingCommand
import com.ninja.android.lib.view.MessageBubbleView

/**
 *Author:Mr'x
 *Time:2021/8/31
 *Description:
 */
@BindingAdapter(value = ["onDragCommand"], requireAll = false)
fun onDragCommand(view: View, clickCommand: BindingCommand<*>) {
    if(view is MessageBubbleView){
        view.setOnActionListener(object: MessageBubbleView.ActionListener{
            override fun onDrag() {
            }

            override fun onDisappear() {
                clickCommand.execute()
            }

            override fun onRestore() {
            }

            override fun onMove() {
            }

        })
    }

}

@BindingAdapter(value = ["app:textNumber"],requireAll = false)
fun textNumber(view: MessageBubbleView,number:Int){
    view.setNumber(number.toString())
}