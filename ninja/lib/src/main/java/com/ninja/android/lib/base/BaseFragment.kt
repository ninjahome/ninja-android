package com.ninja.android.lib.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import com.ninja.android.lib.R
import com.ninja.android.lib.utils.toast

/**
 * @description:
 * @author:  Mr.x
 * @date :   2020/11/3 9:21 AM
 */
abstract class BaseFragment<VM : BaseViewModel, DB : ViewDataBinding>(@LayoutRes val layoutId:Int) : Fragment() {
    val TAG = this.javaClass.name
    protected lateinit var mDatabinding: DB
    lateinit var mActivity: AppCompatActivity
    private  lateinit var loadingDialog: LoadingPopupView
    private var isShown = false
    protected abstract val mViewModel: VM

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mDatabinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mDatabinding.lifecycleOwner = this
        return mDatabinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDatabinding.setVariable(initVariableId(),mViewModel)
        registorUIChangeLiveDataCallBack()
        initView()
        initData()
        initObserve()
    }


    abstract fun initView()

    abstract fun initData()

    abstract fun initVariableId(): Int
    abstract fun initObserve()


    open fun <T : ViewModel> createViewModel(cls: Class<T>): T {
        return ViewModelProvider(this).get(cls)
    }

    open fun onShow() {}
    fun onHide() {}
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            isShown = true
            onShow()
        } else {
            isShown = false
            onHide()
        }
    }
    //??????ViewModel???View?????????UI????????????
    protected open fun registorUIChangeLiveDataCallBack() { //?????????????????????
        mViewModel.uc.showDialogEvent.observe(this,
            Observer { title -> showDialog(title) })

        mViewModel.uc.showDialogStrEvent.observe(this,
            Observer { t -> showDialog(t) })
        mViewModel.uc.showDialogNotCancelEvent.observe(this,
            Observer { titleId -> showDialog(titleId) })

        mViewModel.uc.showDialogNotCancelStrEvent.observe(this,
            Observer { title -> showDialog(title) })
        //?????????????????????
        mViewModel.uc.dismissDialogEvent
            .observe(this, Observer { dismissDialog() })


        mViewModel.uc.toastEvent.observe(this,
            Observer { msgId -> toast(getString(msgId)) })

        mViewModel.uc.toastStrEvent.observe(this,
            Observer { msg -> toast(msg) })
        //???????????????
        mViewModel.uc.startActivityEvent.observe(
            this,
            Observer { params ->
                val clz =
                    params[ParameterField.CLASS] as Class<*>
                val bundle = params[ParameterField.BUNDLE] as Bundle?
                startActivity(clz, bundle)
            })

        mViewModel.uc.startWebActivityEvent.observe(
            this, Observer { url ->
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.data = Uri.parse(url)
                startActivity(intent, null)
            })

        //????????????
        mViewModel.uc.finishEvent.observe(this,
            Observer { mActivity.finish() })
        //???????????????
        mViewModel.uc.onBackPressedEvent
            .observe(this,
                Observer { mActivity.onBackPressed() })
    }



    /**
     * ????????????
     *
     * @param clz ??????????????????Activity???
     */
    open fun startActivity(clz: Class<*>?) {
        startActivity(Intent(mActivity, clz))
    }

    /**
     * ????????????
     *
     * @param clz    ??????????????????Activity???
     * @param bundle ????????????????????????
     */
    open fun startActivity(clz: Class<*>?, bundle: Bundle?) {
        val intent = Intent(mActivity, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }
    open fun showDialog(titleId: Int = R.string.loading) {
        showDialog(getString(titleId))
    }
    fun showDialog(title:String){
        if(this::loadingDialog.isInitialized && loadingDialog.isShow){
            loadingDialog.setTitle(title)
            return
        }
        loadingDialog= XPopup.Builder(mActivity)
            .dismissOnBackPressed(false)
            .asLoading(title)
            .show() as LoadingPopupView


    }

    open fun dismissDialog() {
        if (this::loadingDialog.isInitialized && loadingDialog != null && loadingDialog?.isShow!!) {
            loadingDialog?.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissDialog()
    }

}