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
class CreateGroupAdapter(private val mContext: Context) : RecyclerView.Adapter<CreateGroupAdapter.MyRecycleHolder>() {
    private val FONT_SIZE = 30
    private val contactBeanList: MutableList<Contact>?
    var clickItemListener: ClickItemListener? = null

    interface ClickItemListener {
        fun clickDelete(position: Int, contact: Contact)
        fun clickAdd(position: Int, contact: Contact)
    }

    fun setItemCLickListener(clickItemListener: ClickItemListener) {
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_create_group_chat, parent, false)
        return MyRecycleHolder(view)
    }

    override fun onBindViewHolder(holder: MyRecycleHolder, position: Int) {
        if (contactBeanList == null || contactBeanList.size == 0 || contactBeanList.size <= position) return
        val contact = contactBeanList[position]
        holder.nameTv.text = contact.nickName
        if (contact.isSelected) {
            holder.deleteIv.visibility = View.VISIBLE
            holder.selectedIv.setImageBitmap(BitmapFactory.decodeResource(mContext.resources, R.drawable.group_number_selected))
        } else {
            holder.deleteIv.visibility = View.GONE
            holder.selectedIv.setImageBitmap(BitmapFactory.decodeResource(mContext.resources, R.drawable.group_number_normal))
        }
        holder.deleteIv.setOnClickListener {
            contact.isSelected = false
            clickItemListener?.clickDelete(position, contact)
            notifyItemChanged(position)
        }
        holder.selectedIv.setOnClickListener {
            if (!contact.isSelected) {
                contact.isSelected = true
                notifyItemChanged(position)
                clickItemListener?.clickAdd(position, contact)
            }

        }
        val drawable = ContactIconUtils.getDrawable(FONT_SIZE, contact.uid, contact.subName)
        holder.iconIv.setImageDrawable(drawable)
    }

    override fun getItemCount(): Int {
        return contactBeanList!!.size
    }

    class MyRecycleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv: TextView = itemView.findViewById<View>(R.id.tv_name) as TextView
        val iconIv: ImageView = itemView.findViewById<View>(R.id.iv_img) as ImageView
        val deleteIv: ImageView = itemView.findViewById<View>(R.id.deleteIv) as ImageView
        val selectedIv: ImageView = itemView.findViewById<View>(R.id.selectedIv) as ImageView

    }

    init {
        contactBeanList = ArrayList()
    }
}