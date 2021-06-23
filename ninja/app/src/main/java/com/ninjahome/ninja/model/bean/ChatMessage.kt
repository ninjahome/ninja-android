package com.ninjahome.ninja.model.bean

import java.util.*


class ChatMessage @JvmOverloads constructor(private val id: String, private val user: User, private var text: String, private var createdAt: Date = Date())  /*and this one is for custom content type (in this case - voice message)*/ {
    private var image: Image? = null
    var voice: Voice? = null

    val status: String
        get() = "Sent"

    fun setText(text: String) {
        this.text = text
    }


    class Image(internal val url: String)
    class Voice(val url: String, val duration: Int)
}