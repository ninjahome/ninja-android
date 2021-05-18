package com.ninjahome.ninja.utils

import androidx.appcompat.app.AppCompatActivity
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.OnCancelListener
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.lxj.xpopup.interfaces.OnSelectListener
import com.lxj.xpopup.interfaces.SimpleCallback
import com.ninjahome.ninja.R
import com.ninjahome.ninja.view.PasswordPop
import com.orhanobut.logger.Logger

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
object DialogUtils {
    const val POSITION_ALBUM = 0
    const val POSITION_CAMERA = 1

    internal class NinjaXPopupListener : SimpleCallback() {
        override fun onCreated(pv: BasePopupView) {
            Logger.d("tag", "弹窗创建了")
        }

        override fun onShow(popupView: BasePopupView) {
            Logger.d("tag", "onShow")
        }

        override fun onDismiss(popupView: BasePopupView) {
            Logger.d("tag", "onDismiss")
        }

        override fun beforeDismiss(popupView: BasePopupView) {
            Logger.d("tag", "准备消失的时候执行")
        }

        //如果你自己想拦截返回按键事件，则重写这个方法，返回true即可
        override fun onBackPressed(popupView: BasePopupView): Boolean {
            return false
        }

        override fun onKeyBoardStateChanged(popupView: BasePopupView, height: Int) {
            super.onKeyBoardStateChanged(popupView, height)
            Logger.d("onKeyBoardStateChanged height: $height")
        }
    }

    fun showImportDialog(activity: AppCompatActivity, selectListener: OnSelectListener) {
        XPopup.Builder(activity).asBottomList(activity.getString(R.string.guide_dialog_title), arrayOf(activity.getString(R.string.guide_import_album), activity.getString(R.string.guide_import_camera), activity.getString(R.string.cancel)), selectListener).show()
    }

    fun showPasswordDialog(activity: AppCompatActivity, listener: PasswordPop.InputPasswordListener, xpopListener: SimpleCallback = NinjaXPopupListener()): BasePopupView {
        return XPopup.Builder(activity).dismissOnTouchOutside(false).dismissOnBackPressed(true).setPopupCallback(xpopListener).isDestroyOnDismiss(true).asCustom(PasswordPop(activity, listener)).show()

    }


    fun showStartFingerPrintsDialog(activity: AppCompatActivity, confirmListerer: OnConfirmListener, cancelListener: OnCancelListener): BasePopupView {
        return XPopup.Builder(activity).dismissOnTouchOutside(false).dismissOnBackPressed(false).isDestroyOnDismiss(true).asConfirm("", activity.getString(R.string.my_no_fingerprint), activity.getString(R.string.cancel), activity.getString(R.string.my_input), confirmListerer, cancelListener, false).show()

    }

}