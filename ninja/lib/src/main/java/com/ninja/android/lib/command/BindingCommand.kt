package com.ninja.android.lib.command

/**
 *Author:
 *Time:
 *Description:
 */
open class BindingCommand<T>(
    var bindAction: BindingAction? = null,
    var bindConsumer: BindingConsumer<T>? = null,
    var bindingFunction: BindingFunction<Boolean>? = null
) {

    fun execute() {
        bindAction?.call()
    }

    fun execute(parameter: T) {
        if(canExecute()){
            bindConsumer?.call(parameter)
        }
    }

    fun canExecute(): Boolean {
        if (bindingFunction == null) {
            return true
        }
        return bindingFunction!!.call()
    }
}