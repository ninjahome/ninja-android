package com.ninjahome.ninja.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidlib.Androidlib
import androidx.recyclerview.widget.RecyclerView
import com.ninja.android.lib.provider.context
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.ui.adapter.ContactAdapter.MyRecycleHolder
import com.ninjahome.ninja.view.contacts.ColorGenerator
import com.ninjahome.ninja.view.contacts.ColorUtil
import com.ninjahome.ninja.view.contacts.TextDrawable
import com.ninjahome.ninja.view.contacts.TextDrawable.Companion.builder
import java.util.*

/**
 * Created by MQ on 2017/5/8.
 */
class ContactAdapter(private val mContext: Context) : RecyclerView.Adapter<MyRecycleHolder>() {
    private val contactBeanList: MutableList<Contact>?
    var clickItemListener: ClickItemListener? = null

    interface ClickItemListener {
        fun clickItem(position: Int, contact: Contact)
    }

    // declare the color generator and drawable builder
    private val mColorGenerator = ColorGenerator.MATERIAL
    private val mDrawableBuilder =  TextDrawable.builder().beginConfig().fontSize(30)
    fun addAll(beans: List<Contact>?) {
        if (contactBeanList!!.size > 0) {
            contactBeanList.clear()
        }
        contactBeanList.addAll(beans!!)
        notifyDataSetChanged()
    }

    fun add(bean: Contact, position: Int) {
        contactBeanList!!.add(position, bean)
        notifyItemInserted(position)
    }

    fun add(bean: Contact) {
        contactBeanList!!.add(bean)
        notifyItemChanged(contactBeanList.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecycleHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return MyRecycleHolder(view)
    }

    override fun onBindViewHolder(holder: MyRecycleHolder, position: Int) {
        if (contactBeanList == null || contactBeanList.size == 0 || contactBeanList.size <= position) return
        val contact = contactBeanList[position]
        holder.itemView.setOnClickListener {
            clickItemListener?.let {
                it.clickItem(position, contact)
            }
        }
        holder.tv_name.text = contact.nickName
        var subName:String = contact.nickName[0].toString()
        if(contact.nickName.length>=2){
            subName = contact.nickName.substring(0,2)
        }
        val index = Androidlib.iconIndex(contact.uid, ColorUtil.colorSize)
        val iconColor = ColorUtil.colors[index]
        val drawable = mDrawableBuilder.textColor(context().getColor(R.color.white)).endConfig().buildRound(subName,mContext.resources.getColor(iconColor))
        holder.iv_img.setImageDrawable(drawable)
    }

    override fun getItemCount(): Int {
        return contactBeanList!!.size
    }

    class MyRecycleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_name: TextView = itemView.findViewById<View>(R.id.tv_name) as TextView
        val iv_img: ImageView = itemView.findViewById<View>(R.id.iv_img) as ImageView

    }

    init {
        contactBeanList = ArrayList()
    }
}