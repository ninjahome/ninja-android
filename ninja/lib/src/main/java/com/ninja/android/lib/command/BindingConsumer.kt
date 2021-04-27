package com.ninja.android.lib.command

interface BindingConsumer<T> {
    fun call(t: T)
}