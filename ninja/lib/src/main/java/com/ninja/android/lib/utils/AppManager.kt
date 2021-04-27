package com.ninja.android.lib.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.view.inputmethod.InputMethodManager
import java.util.*


object AppManager {
    private val mActivities = Stack<Activity>()

    fun addActivity(activity: Activity?) {
        mActivities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        hideSoftKeyBoard(activity)
        mActivities.remove(activity)
    }

    fun removeAllActivity() {
        for (activity in mActivities) {
            activity.finish()
        }
        mActivities.clear()
    }

    fun <T : Activity> hasActivity(tClass: Class<T>): Boolean {
        for (activity in mActivities) {
            if (tClass.name == activity.javaClass.name) {
                return !activity.isDestroyed || !activity.isFinishing
            }
        }
        return false
    }

    fun hideSoftKeyBoard(activity: Activity) {
        val localView = activity.currentFocus
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (localView != null) {
            imm.hideSoftInputFromWindow(localView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun killAppProcess(context: Context){
        val activityManager: ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mList = activityManager.runningAppProcesses
        for (runningAppProcessInfo in mList) {
            if (runningAppProcessInfo.pid != Process.myPid()) {
                Process.killProcess(runningAppProcessInfo.pid)
            }
        }
        Process.killProcess(Process.myPid())
        System.exit(0)
    }
}