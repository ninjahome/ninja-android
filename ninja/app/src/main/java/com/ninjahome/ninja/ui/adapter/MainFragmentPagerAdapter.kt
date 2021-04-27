package com.ninjahome.ninja.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ninjahome.ninja.ui.fragment.contact.ContactFragment
import com.ninjahome.ninja.ui.fragment.message.MessageFragment
import com.ninjahome.ninja.ui.fragment.my.MyFragment

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class MainFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    val fragments = arrayListOf(MessageFragment(), ContactFragment(),MyFragment())
    override fun getCount(): Int {
       return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

}