package com.ninjahome.ninja.view

import com.github.promeg.pinyinhelper.Pinyin
import com.ninjahome.ninja.model.bean.Contact
import java.util.*

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class ContactsUtils {
    companion object {
        fun sortData(list: List<Contact>?) {
            if (list == null || list.size == 0) return
            for (i in list.indices) {
                val bean: Contact = list[i]
                val tag: String = Pinyin.toPinyin(bean.nickName.substring(0, 1).toCharArray()[0]).substring(0, 1)
                if (tag.matches(Regex("[A-Z]"))) {
                    bean.indexTag = tag
                } else {
                    bean.indexTag = "#"
                }
            }
            Collections.sort(list, Comparator<Contact> { o1, o2 ->
                if ("#" == o1.indexTag) {
                    1
                } else if ("#" == o2.indexTag) {
                    -1
                } else {
                    o1.indexTag.compareTo(o2.indexTag)
                }
            })
        }

        fun getTags(beans: List<Contact>): String {
            val builder = StringBuilder()
            for (i in beans.indices) {
                if (!builder.toString().contains(beans[i].indexTag)) {
                    builder.append(beans[i].indexTag)
                }
            }
            return builder.toString()
        }
    }


}