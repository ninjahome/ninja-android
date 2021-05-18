package com.ninjahome.ninja.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ninjahome.ninja.ui.fragment.contact.ContactListFragment
import com.ninjahome.ninja.ui.fragment.conversation.ConversationListFragment
import com.ninjahome.ninja.ui.fragment.my.MyFragment
import org.koin.core.component.KoinApiExtension

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
@KoinApiExtension
class MainFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragments = arrayListOf(ConversationListFragment(), ContactListFragment(), MyFragment())

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

}