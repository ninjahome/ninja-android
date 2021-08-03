package com.ninjahome.ninja.ui.activity.conversation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolder
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.lqr.adapter.OnItemClickListener
import com.ninja.android.lib.base.BaseActivity
import com.ninja.android.lib.utils.dp
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.Constants
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityLocationBinding
import com.ninjahome.ninja.model.bean.LocationData
import com.ninjahome.ninja.utils.UnitConversionUtils
import com.ninjahome.ninja.viewmodel.LocationViewModel
import com.orhanobut.logger.Logger
import com.tencent.lbssearch.TencentSearch
import com.tencent.lbssearch.`object`.Location
import com.tencent.lbssearch.`object`.param.Geo2AddressParam
import com.tencent.lbssearch.`object`.result.Geo2AddressResultObject
import com.tencent.lbssearch.httpresponse.BaseObject
import com.tencent.lbssearch.httpresponse.HttpResponseListener
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.tencent.mapsdk.raster.model.*
import com.tencent.tencentmap.mapsdk.map.TencentMap
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.activity_location.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.math.abs

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class LocationActivity : BaseActivity<LocationViewModel, ActivityLocationBinding>(R.layout.activity_location), TencentLocationListener, EasyPermissions.PermissionCallbacks {
    private val CODE_REQUEST = 200
    var maxHeight: Int = 300.dp.toInt()
    var minHeight: Int = 150.dp.toInt()
    private lateinit var mSensorManager: SensorManager
    private lateinit var mOrientationSensor: Sensor
    private lateinit var mLocationManager: TencentLocationManager
    private lateinit var mLocationRequest: TencentLocationRequest
    private lateinit var mTencentMap: TencentMap
    private var myLocation: Marker? = null
    var currentLat = 0.0
    var currentLng = 0.0
    private var accuracy: Circle? = null
    private var mTencentSearch: TencentSearch? = null
    private val mData: MutableList<Geo2AddressResultObject.ReverseAddressResult.Poi> = mutableListOf()
    private var mSelectedPosi = 0
    private lateinit var mAdapter: LQRAdapterForRecyclerView<Geo2AddressResultObject.ReverseAddressResult.Poi>

    private val handler = Handler(Looper.getMainLooper())

    override val mViewModel: LocationViewModel by viewModel()

    override fun initView() {
        val rightTv = findViewById<TextView>(R.id.title_right_tv)
        rightTv.setTextColor(resources.getColor(R.color.white, null))
        rightTv.layoutParams.width = 64.dp.toInt()
        rightTv.layoutParams.height = 32.dp.toInt()
        rightTv.background = ResourcesCompat.getDrawable(resources, R.drawable.bg_location_send, null)
        setRlMapHeight(maxHeight)
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        mLocationManager = TencentLocationManager.getInstance(this)
        mLocationRequest = TencentLocationRequest.create()
        mTencentMap = map.map
        mTencentSearch = TencentSearch(this)
        mTencentMap.setOnMapCameraChangeListener(object : TencentMap.OnMapCameraChangeListener {

            override fun onCameraChange(cameraPosition: CameraPosition?) {
                myLocation?.position = mTencentMap.mapCenter
            }

            override fun onCameraChangeFinish(cameraPosition: CameraPosition?) {
                accuracy?.center = mTencentMap.mapCenter
                search(mTencentMap.mapCenter)
            }
        })
        requestLocationPermission()
    }

    override fun initData() {

        mAdapter = object : LQRAdapterForRecyclerView<Geo2AddressResultObject.ReverseAddressResult.Poi>(this, mData, R.layout.item_location_poi) {
            override fun convert(helper: LQRViewHolderForRecyclerView, item: Geo2AddressResultObject.ReverseAddressResult.Poi, position: Int) {
                helper.setText(R.id.tvTitle, item.title).setText(R.id.tvDesc, "${UnitConversionUtils.m2Km(item._distance)} | ${item.address}").setViewVisibility(R.id.ivSelected, if (mSelectedPosi == position) View.VISIBLE else View.GONE)
                //                helper.setText(R.id.tvTitle, item.title).setText(R.id.tvDesc, "${item.address}").setViewVisibility(R.id.ivSelected, if (mSelectedPosi == position) View.VISIBLE else View.GONE)
            }
        }
        rvPOI.adapter = mAdapter
        mAdapter.onItemClickListener = OnItemClickListener { _: LQRViewHolder?, _: ViewGroup?, _: View?, position: Int ->
            mSelectedPosi = position
            mAdapter.notifyDataSetChangedWrapper()
        }

    }


    override fun initObserve() {
        mViewModel.sendEvent.observe(this) {
            if (mData.size > mSelectedPosi) {
                val poi: Geo2AddressResultObject.ReverseAddressResult.Poi = mData[mSelectedPosi]
                val data = Intent()
                val locationData = LocationData(poi.location.lat, poi.location.lng, "${poi.title}(${poi.address})")
                data.putExtra("location", locationData)
                setResult(RESULT_OK, data)
                finish()
            }
        }

        mViewModel.requestLocationEvent.observe(this) {
            requestLocationUpdate()
        }

        mViewModel.scrolledEvent.observe(this) {
            if (it.scrollY > 0 && abs(it.scrollY) > 10 && (rvPOI.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition() <= 1 && rlMap.height == maxHeight) {
                setRlMapHeight(minHeight)
                handler.postDelayed({ rvPOI.moveToPosition(0) }, 0)
            } else if (it.scrollY < 0 && abs(it.scrollY) > 10 && (rvPOI.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition() == 1 && rlMap.height == minHeight) {
                setRlMapHeight(maxHeight)
                handler.postDelayed({ rvPOI.moveToPosition(0) }, 0)
            }
        }

        mViewModel.startLocationSearchEvent.observe(this) {
            val intent = Intent(this, LocationSearchActivity::class.java)
            intent.putExtra(IntentKey.LOCATION_LAT, currentLat)
            intent.putExtra(IntentKey.LOCATION_LNG, currentLng)
            startActivityForResult(intent, CODE_REQUEST)
        }
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel


    private fun setRlMapHeight(height: Int) {
        val params = rlMap.layoutParams as LinearLayout.LayoutParams
        params.height = height
        rlMap.layoutParams = params
    }

    private fun requestLocationUpdate() {
        //开启定位
        when (mLocationManager.requestLocationUpdates(mLocationRequest, this@LocationActivity)) {
            0 -> Logger.d("成功注册监听器")
            1 -> Logger.d("设备缺少使用腾讯定位服务需要的基本条件")
            2 -> Logger.d("manifest 中配置的 key 不正确")
            3 -> Logger.d("自动加载libtencentloc.so失败")
        }
    }

    override fun onLocationChanged(tencentLocation: TencentLocation, i: Int, s: String) {
        if (i == TencentLocation.ERROR_OK) {
            val latLng = LatLng(tencentLocation.latitude, tencentLocation.longitude)
            if (myLocation == null) {
                myLocation = mTencentMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.arm)).anchor(0.5f, 0.8f))
                currentLat = tencentLocation.latitude
                currentLng = tencentLocation.longitude

            }
            if (accuracy == null) {
                accuracy = mTencentMap.addCircle(CircleOptions().center(latLng).radius(tencentLocation.accuracy.toDouble()).fillColor(0x440000ff).strokeWidth(0f))
            }
            myLocation!!.position = latLng
            accuracy!!.center = latLng
            accuracy!!.radius = tencentLocation.accuracy.toDouble()
            mTencentMap.animateTo(latLng)
            mTencentMap.setZoom(17)
            //取消定位
            mLocationManager.removeUpdates(this@LocationActivity)
        } else {
            Logger.e("location failed:$s")
        }
    }

    private fun search(latLng: LatLng) {
        pb.visibility = View.VISIBLE
        rvPOI.visibility = View.GONE
        val location = Location(latLng.latitude.toFloat(), latLng.longitude.toFloat())
        //还可以传入其他坐标系的坐标，不过需要用coord_type()指明所用类型
        //这里设置返回周边poi列表，可以在一定程度上满足用户获取指定坐标周边poi的需求
        val geo2AddressParam: Geo2AddressParam = Geo2AddressParam().location(location).get_poi(true)
        mTencentSearch!!.geo2address(geo2AddressParam, object : HttpResponseListener {

            override fun onFailure(arg0: Int, arg1: String, arg2: Throwable?) {

                pb.visibility = View.GONE
                rvPOI.visibility = View.VISIBLE
                arg2?.message?.let { Logger.e(it) }

            }

            override fun onSuccess(p0: Int, arg1: BaseObject?) {
                pb.visibility = View.GONE
                rvPOI.visibility = View.VISIBLE
                if (arg1 == null) {
                    return
                }

                mData.clear()
                mData.addAll((arg1 as Geo2AddressResultObject).result.pois)
                mAdapter.notifyDataSetChanged()
            }
        })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return

        if (requestCode == CODE_REQUEST) {
            data?.let {
                val lat = it.getFloatExtra(IntentKey.LOCATION_LAT, 0.0f)
                val lng = it.getFloatExtra(IntentKey.LOCATION_LNG, 0.0f)
                val latLng = LatLng(lat.toDouble(), lng.toDouble())
                mTencentMap.animateTo(latLng)
                search(latLng)
            }


        }
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

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}