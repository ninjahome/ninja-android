package com.ninjahome.ninja.ui.activity.conversation

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityLocationShowBinding
import com.ninjahome.ninja.viewmodel.LocationShowViewModel
import com.orhanobut.logger.Logger
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.tencent.mapsdk.raster.model.*
import com.tencent.tencentmap.mapsdk.map.TencentMap
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.activity_location.map
import kotlinx.android.synthetic.main.activity_location_show.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.EasyPermissions

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class LocationShowActivity : BaseActivity<LocationShowViewModel, ActivityLocationShowBinding>(R.layout.activity_location_show), TencentLocationListener, EasyPermissions.PermissionCallbacks {
    private lateinit var mLocationManager: TencentLocationManager
    private lateinit var mLocationRequest: TencentLocationRequest
    private lateinit var mTencentMap: TencentMap

    override val mViewModel: LocationShowViewModel by viewModel()

    override fun initView() {
        mViewModel.title.set(resources.getString(R.string.location_info))
        mLocationManager = TencentLocationManager.getInstance(this)
        mLocationRequest = TencentLocationRequest.create()
        mTencentMap = map.map
        requestLocationPermission()
    }

    override fun initData() {


    }

    override fun initObserve() {
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel

    private fun requestLocationUpdate() {
        //开启定位
        when (mLocationManager.requestLocationUpdates(mLocationRequest, this@LocationShowActivity)) {
            0 -> {
                Logger.d("成功注册监听器")
                val lat = intent.getFloatExtra(IntentKey.LOCATION_LAT, 0.0f)
                val lng = intent.getFloatExtra(IntentKey.LOCATION_LNG, 0.0f)
                val locationAddress = intent.getStringExtra(IntentKey.LOCATION_ADDRESS)
                addressTv.text = locationAddress
                val latLng = LatLng(lat.toDouble(), lng.toDouble())
                mTencentMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.arm)).anchor(0.5f, 0.8f))
                mTencentMap.animateTo(latLng)
                mTencentMap.setZoom(17)
            }
            1 -> Logger.d("设备缺少使用腾讯定位服务需要的基本条件")
            2 -> Logger.d("manifest 中配置的 key 不正确")
            3 -> Logger.d("自动加载libtencentloc.so失败")
        }
    }

    override fun onLocationChanged(tencentLocation: TencentLocation, i: Int, s: String) {
        if (i == TencentLocation.ERROR_OK) {
            //取消定位
            mLocationManager.removeUpdates(this@LocationShowActivity)
        } else {
            Logger.e("location failed:$s")
        }
    }

    override fun onStatusUpdate(s: String?, i: Int, s1: String?) {
        var desc = ""
        when (i) {
            TencentLocationListener.STATUS_DENIED -> desc = "权限被禁止"
            TencentLocationListener.STATUS_DISABLED -> desc = "模块关闭"
            TencentLocationListener.STATUS_ENABLED -> desc = "模块开启"
            TencentLocationListener.STATUS_GPS_AVAILABLE -> desc = "GPS可用，代表GPS开关打开，且搜星定位成功"
            TencentLocationListener.STATUS_GPS_UNAVAILABLE -> desc = "GPS不可用，可能 gps 权限被禁止或无法成功搜星"
            TencentLocationListener.STATUS_LOCATION_SWITCH_OFF -> desc = "位置信息开关关闭，在android M系统中，此时禁止进行wifi扫描"
            TencentLocationListener.STATUS_UNKNOWN -> {
            }
        }
        Logger.d("location status:$s, $s1 $desc")
    }


    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(this, getString(R.string.import_apply_location_permission), Constants.CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            requestLocationUpdate()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        requestLocationUpdate()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        finish()
    }
}