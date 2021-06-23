package com.ninja.android.lib.command

import io.reactivex.rxjava3.functions.Function

/**
 * About : kelin的ResponseCommand
 * 执行的命令事件转换
 */
class ResponseCommand<T, R>(var execute: BindingFunction<R>? = null,var function: Function<T, R>?= null,var canExecute: BindingFunction<Boolean>?= null) {

    /**
     * like [BindingCommand],but ResponseCommand can return result when command has executed!
     *
     * @param execute function to execute when event occur.
     */

    fun execute(): R? {
        return if (execute != null && canExecute()) {
            execute!!.call()
        } else null
    }

    private fun canExecute(): Boolean {
        return if (canExecute == null) {
            true
        } else canExecute!!.call()
    }

    @Throws(Exception::class)
    fun execute(parameter: T): R? {
        if (function != null && canExecute()) {
            try {
                return function!!.apply(parameter)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }
        return null
    }
}