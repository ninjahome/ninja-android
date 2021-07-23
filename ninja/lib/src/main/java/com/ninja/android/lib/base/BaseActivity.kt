package com.ninja.android.lib.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gyf.immersionbar.ImmersionBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import com.ninja.android.lib.R
import com.ninja.android.lib.utils.AppManager
import com.ninja.android.lib.utils.toast

/**
 * @description:
 * @author:  Mr.x
 * @date :   2020/11/3 8:01 AM
 */
abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding>(@LayoutRes val layoutId: Int) : AppCompatActivity() {
    val TAG = this.javaClass.name
    private lateinit var mDataBinding: DB
    private var viewModelId = 0
    protected val STATUSBAR_STYLE_TRANSPARENT = 1
    protected val STATUSBAR_STYLE_WHITE = 2
    private val STATUSBAR_STYLE_GRAY = 3
    protected val STATUSBAR_STYLE_CUSTOMER = 4
    private lateinit var loadingDialog: LoadingPopupView
    protected abstract val mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.addActivity(this)
        mDataBinding = DataBindingUtil.setContentView(this, layoutId)
        mDataBinding.lifecycleOwner = this
        actionBar?.let {}
        if (statusBarStyle() == STATUSBAR_STYLE_TRANSPARENT) {
            ImmersionBar.with(this).transparentStatusBar().barEnable(true).statusBarDarkFont(true).init()
        } else if (statusBarStyle() == STATUSBAR_STYLE_WHITE) {
            ImmersionBar.with(this).statusBarColor(R.color.white).barEnable(true).statusBarDarkFont(true).fitsSystemWindows(true).init()
        } else if (statusBarStyle() == STATUSBAR_STYLE_GRAY) {
            ImmersionBar.with(this).statusBarColor(R.color.color_f8f8f9).barEnable(true).statusBarDarkFont(true).fitsSystemWindows(true).init()
        }
        lifecycle.addObserver(mViewModel)
        viewModelId = initVariableId()
        mDataBinding.setVariable(viewModelId, mViewModel)
        registorUIChangeLiveDataCallBack()
        initView()
        initData()
        initObserve()
    }

    abstract fun initView()

    abstract fun initData()
    abstract fun initObserve()
    abstract fun statusBarStyle(): Int

    abstract fun initVariableId(): Int

    open fun <T : ViewModel> createViewModel(cls: Class<T>): T {
        return ViewModelProvider(this).get(cls)
    }

    //注册ViewModel与View的契约UI回调事件
    protected open fun registorUIChangeLiveDataCallBack() {
        //加载对话框显示
        mViewModel.uc.showDialogEvent.observe(this, { titleId -> showDialog(resources.getString(titleId), true) })

        mViewModel.uc.showDialogNotCancelEvent.observe(this, { titleId -> showDialog(resources.getString(titleId), false) })

        mViewModel.uc.showDialogNotCancelStrEvent.observe(this, { title -> showDialog(title, false) })

        //加载对话框消失
        mViewModel.uc.dismissDialogEvent.observe(this, { dismissDialog() })

        mViewModel.uc.toastEvent.observe(this, { msgId -> toast(getString(msgId)) })

        mViewModel.uc.toastStrEvent.observe(this, { msg -> toast(msg) })
        //跳入新页面
        mViewModel.uc.startActivityEvent.observe(this, { params ->
            val clz = params[ParameterField.CLASS] as Class<*>
            val bundle = params[ParameterField.BUNDLE] as Bundle?
            val finishActivity = params[ParameterField.FINISH] as Boolean
            startActivity(clz, bundle)
            if (finishActivity) {
                finish()
            }
        })

        mViewModel.uc.startWebActivityEvent.observe(this, { url ->
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            intent.data = Uri.parse(url)
            startActivity(intent, null)
        })
        //关闭界面
        mViewModel.uc.finishEvent.observe(this, { finish() })
        //关闭上一层
        mViewModel.uc.onBackPressedEvent.observe(this, { onBackPressed() })
    }

    @JvmOverloads
    open fun showDialog(title: String, cancelable: Boolean = true) {
        if (this::loadingDialog.isInitialized && loadingDialog.isShow) {
            loadingDialog.setTitle(title)
            return
        }
        loadingDialog = XPopup.Builder(this).dismissOnBackPressed(cancelable).asLoading(title).show() as LoadingPopupView
    }

    open fun dismissDialog() {
        if (this::loadingDialog.isInitialized && loadingDialog.isShow) {
            loadingDialog.dismiss()
        }
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    open fun startActivity(clz: Class<*>?) {
        startActivity(Intent(this, clz))
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    open fun startActivity(clz: Class<*>?, bundle: Bundle?) {
        val intent = Intent(this, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissDialog()
        AppManager.removeActivity(this)
    }

    protected fun gone(vararg view:View){
        view.forEach {
          it.visibility = View.GONE
        }
    }

    protected fun visible(vararg view:View){
        view.forEach {
            it.visibility = View.VISIBLE
        }
    }

    protected fun inVisible(vararg view:View){
        view.forEach {
            it.visibility = View.INVISIBLE
        }
    }
}