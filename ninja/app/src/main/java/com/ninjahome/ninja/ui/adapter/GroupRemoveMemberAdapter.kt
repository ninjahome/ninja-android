package com.ninjahome.ninja.ui.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ninjahome.ninja.R
import com.ninjahome.ninja.model.bean.Contact
import com.ninjahome.ninja.utils.ContactIconUtils
import java.util.*

/**
 * Created by MQ on 2017/5/8.
 */
class GroupRemoveMemberAdapter(private val mContext: Context) : RecyclerView.Adapter<GroupRemoveMemberAdapter.MyRecycleHolder>() {
    private val FONT_SIZE = 30
    private val contactBeanList: MutableList<Contact>?
    var clickItemListener: ClickItemListener? = null

    interface ClickItemListener {
        fun onSelected(position: Int, contact: Contact)
    }

    fun setItemCLickListener(clickItemListener: ClickItemListener){
        this.clickItemListener = clickItemListener
    }
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group_chat_remove_member, parent, false)
        return MyRecycleHolder(view)
    }

    override fun onBindViewHolder(holder: MyRecycleHolder, position: Int) {
        if (contactBeanList == null || contactBeanList.size == 0 || contactBeanList.size <= position) return
        val contact = contactBeanList[position]
        holder.tv_name.text = contact.nickName
        if(contact.isSelected){
            holder.selectedIv.setImageBitmap(BitmapFactory.decodeResource(mContext.resources,R.drawable.group_number_selected))
        }else{
            holder.selectedIv.setImageBitmap(BitmapFactory.decodeResource(mContext.resources,R.drawable.remove_member_normal))
        }
        holder.selectedIv.setOnClickListener{
            if(!contact.isSelected){
                contact.isSelected = true
                notifyItemChanged(position)
                clickItemListener?.onSelected(position,contact)
            }

        }
        val drawable = ContactIconUtils.getDrawable(FONT_SIZE, contact.uid, contact.subName)
        holder.iv_img.setImageDrawable(drawable)
    }

    override fun getItemCount(): Int {
        return contactBeanList!!.size
    }

    class MyRecycleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_name: TextView = itemView.findViewById<View>(R.id.tv_name) as TextView
        val iv_img: ImageView = itemView.findViewById<View>(R.id.iv_img) as ImageView
        val selectedIv: ImageView = itemView.findViewById<View>(R.id.selectedIv) as ImageView

    }

    init {
        contactBeanList = ArrayList()
    }
}