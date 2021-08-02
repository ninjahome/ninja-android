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
import com.ninjahome.ninja.model.bean.GroupMember
import com.ninjahome.ninja.utils.ContactIconUtils
import java.util.*

/**
 * Created by MQ on 2017/5/8.
 */
class GroupRemoveMemberAdapter(private val mContext: Context) : RecyclerView.Adapter<GroupRemoveMemberAdapter.MyRecycleHolder>() {
    private val FONT_SIZE = 30
    val groupMemberList: ArrayList<GroupMember>?
    var clickItemListener: ClickItemListener? = null

    interface ClickItemListener {
        fun onSelected(position: Int, member: GroupMember)
    }

    fun setItemCLickListener(clickItemListener: ClickItemListener) {
        this.clickItemListener = clickItemListener
    }

    fun addAll(beans: List<GroupMember>?) {
        if (groupMemberList!!.size > 0) {
            groupMemberList.clear()
        }
        groupMemberList.addAll(beans!!)
        notifyDataSetChanged()
    }

    fun add(bean: GroupMember, position: Int) {
        groupMemberList!!.add(position, bean)
        notifyItemInserted(position)
    }

    fun add(bean: GroupMember) {
        groupMemberList!!.add(bean)
        notifyItemChanged(groupMemberList.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecycleHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group_chat_remove_member, parent, false)
        return MyRecycleHolder(view)
    }

    override fun onBindViewHolder(holder: MyRecycleHolder, position: Int) {
        if (groupMemberList == null || groupMemberList.size == 0 || groupMemberList.size <= position) return
        val member = groupMemberList[position]
        holder.nameTv.text = member.name
        if (member.isSelected) {
            holder.selectedIv.setImageBitmap(BitmapFactory.decodeResource(mContext.resources, R.drawable.group_number_selected))
        } else {
            holder.selectedIv.setImageBitmap(BitmapFactory.decodeResource(mContext.resources, R.drawable.remove_member_normal))
        }
        holder.selectedIv.setOnClickListener {
            member.isSelected = !member.isSelected
            notifyItemChanged(position)
            clickItemListener?.onSelected(position, member)

        }
        val name = if(member.name.length>2) member.name.substring(0,2) else member.name
        val drawable = ContactIconUtils.getDrawable(FONT_SIZE, member.address, name)
        holder.iconIv.setImageDrawable(drawable)
    }

    override fun getItemCount(): Int {
        return groupMemberList!!.size
    }

    class MyRecycleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv: TextView = itemView.findViewById<View>(R.id.tv_name) as TextView
        val iconIv: ImageView = itemView.findViewById<View>(R.id.iv_img) as ImageView
        val selectedIv: ImageView = itemView.findViewById<View>(R.id.selectedIv) as ImageView

    }

    init {
        groupMemberList = ArrayList()
    }
}