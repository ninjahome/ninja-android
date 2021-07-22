package com.ninjahome.ninja.ui.activity.conversation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.core.content.ContextCompat
import com.cjt2325.cameralibrary.JCameraView
import com.cjt2325.cameralibrary.listener.JCameraListener
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityTakePhotoBinding
import com.ninjahome.ninja.viewmodel.TakePhotoViewModel
import kotlinx.android.synthetic.main.activity_take_photo.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class TakePhotoActivity : BaseActivity<TakePhotoViewModel, ActivityTakePhotoBinding>(R.layout.activity_take_photo) {

    override val mViewModel: TakePhotoViewModel by viewModel()

    override fun initView() {
        val name = intent.getStringExtra(IntentKey.NAME)
        mViewModel.sendUserName.value = mViewModel.sendUserName.value + name
        initPermission()
        cameraview.setSaveVideoPath(Environment.getExternalStorageDirectory().path)
        cameraview.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE)
        //设置小视频保存路径
        val file: File = File(Constants.VIDEO_SAVE_DIR)
        if (!file.exists()) file.mkdirs()
        cameraview.setSaveVideoPath(Constants.VIDEO_SAVE_DIR)
        cameraview.setJCameraLisenter(object : JCameraListener {

            override fun captureSuccess(bitmap: Bitmap) {
                //获取到拍照成功后返回的Bitmap
                val path: String = saveBitmap(bitmap, Constants.PHOTO_SAVE_DIR)
                val data = Intent()
                data.putExtra("take_photo", true)
                data.putExtra("path", path)
                setResult(RESULT_OK, data)
                finish()
            }

            override fun takePhoto() {
                visible(sendBtn, userAvatarIv)
                gone(userNameTv)
            }

            override fun recordSuccess(url: String?, firstFrame: Bitmap?) {
                //获取成功录像后的视频路径
                val data = Intent()
                data.putExtra("take_photo", false)
                data.putExtra("path", url)
                setResult(RESULT_OK, data)
                finish()
            }

        })
    }

    @AfterPermissionGranted(Constants.CODE_OPEN_CAMERA)
    private fun initPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !== PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(this, getString(R.string.import_apply_album_permission), Constants.CODE_OPEN_CAMERA, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun initData() {
    }

    override fun initObserve() {
        mViewModel.backEvent.observe(this) {
            if (cameraview.isTaked) {
                gone(sendBtn, userAvatarIv)
                visible(userNameTv)
                cameraview.resetScreen()
            } else {
                finish()
            }
        }

        mViewModel.sendEvent.observe(this) {
            cameraview.confirm()
        }
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel


    fun saveBitmap(bm: Bitmap, dir: String?): String {
        var path = ""
        val f = File(dir, "NINJA_" + SystemClock.currentThreadTimeMillis() + ".png")
        if (f.exists()) {
            f.delete()
        }
        try {
            val out = FileOutputStream(f)
            bm.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            path = f.absolutePath
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return path
    }

    override fun onResume() {
        super.onResume()
        if (cameraview != null) cameraview.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (cameraview != null) cameraview.onPause()
    }
}