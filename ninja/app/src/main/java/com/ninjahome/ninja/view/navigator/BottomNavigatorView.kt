package com.ninjahome.ninja.view.navigator

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.ninja.android.lib.view.MessageBubbleView
import com.ninjahome.ninja.R
import java.util.*

class BottomNavigatorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private val mImageArray = intArrayOf(R.drawable.message_normal, R.drawable.contact_normal, R.drawable.user_normal)
    private val mImageSelectedArray = intArrayOf(R.drawable.message_selected, R.drawable.contact_selected, R.drawable.user_selected)
    private val mTextArray = intArrayOf(R.string.message, R.string.contact, R.string.my)
    var mOnBottomNavigatorViewItemClickListener: OnBottomNavigatorViewItemClickListener? = null
    var badgeDragListener: BadgeDragListener? = null
    interface BadgeDragListener{
        fun onDisappear(index:Int)
    }
    interface OnBottomNavigatorViewItemClickListener {
        fun onBottomNavigatorViewItemClick(position: Int, view: View?)
    }

    private val frameLayoutList: MutableList<FrameLayout> = ArrayList()
    private fun initView() {
        for (i in frameLayoutList.indices) {
            val frameLayout = frameLayoutList[i]
            val icon = frameLayout.findViewById<ImageView>(R.id.tab_title_iv)
            val text = frameLayout.findViewById<TextView>(R.id.tab_title_tv)
            val badge = frameLayout.findViewById<MessageBubbleView>(R.id.tab_title_badge_tv)
            icon.setImageResource(mImageArray[i])
            text.setText(mTextArray[i])
            badge.setOnActionListener(object:MessageBubbleView.ActionListener{
                override fun onDrag() {
                }

                override fun onDisappear() {
                    badgeDragListener?.onDisappear(i)
                }

                override fun onRestore() {
                }

                override fun onMove() {

                }

            })
        }
    }

    fun select(position: Int) {
        for (i in frameLayoutList.indices) {
            val child: View = frameLayoutList[i]
            if (i == position) {
                selectChild(child, true, i)
            } else {
                selectChild(child, false, i)
            }
        }
    }

    private fun selectChild(child: View, select: Boolean, position: Int) {
        child.isSelected = select
        val icon = child.findViewById<ImageView>(R.id.tab_title_iv)
        val text = child.findViewById<TextView>(R.id.tab_title_tv)
        val badge = child.findViewById<MessageBubbleView>(R.id.tab_title_badge_tv)
        if (select) {
            icon.setImageResource(mImageSelectedArray[position])
            text.setTextColor(resources.getColor(R.color.color_0c123d))
        } else {
            icon.setImageResource(mImageArray[position])
            text.setTextColor(resources.getColor(R.color.color_8826253c))
        }
    }

    fun setOnBottomNavigatorViewItemClickListener(listener: OnBottomNavigatorViewItemClickListener?) {
        mOnBottomNavigatorViewItemClickListener = listener
    }

    fun showBadgeView(position: Int, number: Int, show: Boolean) {
        if (position < 0 || position >= frameLayoutList.size) {
            return
        }
        val badge = frameLayoutList[position].findViewById<MessageBubbleView>(R.id.tab_title_badge_tv)
        badge.resetBezierView()
        badge.visibility = if (show) View.VISIBLE else View.GONE
        if (number > 0) {
            if(number>99){
                badge.setNumber("99+")
            }else{
                badge.setNumber(number.toString())
            }
        } else {
            badge.setNumber("")
        }
    }

    init {
        orientation = HORIZONTAL
        View.inflate(context, R.layout.layout_bottom_navigator, this)
        val bottomNavigatorLine = findViewById<View>(R.id.bottom_navigator_line)
        val conversation = findViewById<FrameLayout>(R.id.bottom_navigator_conversation)
        val contacts = findViewById<FrameLayout>(R.id.bottom_navigator_contacts)
        val my = findViewById<FrameLayout>(R.id.bottom_navigator_my)
        frameLayoutList.add(conversation)
        frameLayoutList.add(contacts)
        frameLayoutList.add(my)
        for (i in frameLayoutList.indices) {
            val view: View = frameLayoutList[i]
            view.setOnClickListener {
                if (null != mOnBottomNavigatorViewItemClickListener) {
                    mOnBottomNavigatorViewItemClickListener!!.onBottomNavigatorViewItemClick(i, view)
                }
            }
        }
        initView()
    }
}