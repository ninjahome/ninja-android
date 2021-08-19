package com.ninjahome.ninja.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.Days
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * @描述 时间工具（需要joda-time）
 */
object TimeUtils {
    /**
     * 得到仿微信日期格式输出
     *
     * @param msgTimeMillis
     * @return
     */
    fun getMsgFormatTime(msgTimeMillis: Long): String {
        val nowTime = DateTime()
        //        LogUtils.sf("nowTime = " + nowTime);
        val msgTime = DateTime(msgTimeMillis)
        //        LogUtils.sf("msgTime = " + msgTime);
        val days = Math.abs(Days.daysBetween(msgTime, nowTime).days)
        //        LogUtils.sf("days = " + days);
        return if (days < 1) {
            //早上、下午、晚上 1:40
            getTime(msgTime)
        } else if (days == 1) {
            //昨天
            "昨天 " + getTime(msgTime)
        } else if (days <= 7) {
            //星期
            when (msgTime.dayOfWeek) {
                DateTimeConstants.SUNDAY -> return "周日 " + getTime(msgTime)
                DateTimeConstants.MONDAY -> return "周一 " + getTime(msgTime)
                DateTimeConstants.TUESDAY -> return "周二 " + getTime(msgTime)
                DateTimeConstants.WEDNESDAY -> return "周三 " + getTime(msgTime)
                DateTimeConstants.THURSDAY -> return "周四 " + getTime(msgTime)
                DateTimeConstants.FRIDAY -> return "周五 " + getTime(msgTime)
                DateTimeConstants.SATURDAY -> return "周六 " + getTime(msgTime)
            }
            ""
        } else {
            //12月22日
            msgTime.toString("MM月dd日 " + getTime(msgTime))
        }
    }

    private fun getTime(msgTime: DateTime): String {
        val hourOfDay = msgTime.hourOfDay
        val `when`: String
        `when` = if (hourOfDay >= 18) { //18-24
            "晚上"
        } else if (hourOfDay >= 13) { //13-18
            "下午"
        } else if (hourOfDay >= 11) { //11-13
            "中午"
        } else if (hourOfDay >= 5) { //5-11
            "早上"
        } else { //0-5
            "凌晨"
        }
        return `when` + " " + msgTime.toString("hh:mm")
    }

    fun formatData(time:Long):String{
       return SimpleDateFormat("yyyy-MM-dd").format(time)
    }
}