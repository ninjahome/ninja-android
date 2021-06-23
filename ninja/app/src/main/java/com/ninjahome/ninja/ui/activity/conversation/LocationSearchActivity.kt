package com.ninjahome.ninja.ui.activity.conversation

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import com.lqr.adapter.LQRAdapterForRecyclerView
import com.lqr.adapter.LQRViewHolder
import com.lqr.adapter.LQRViewHolderForRecyclerView
import com.lqr.adapter.OnItemClickListener
import com.ninja.android.lib.base.BaseActivity
import com.ninjahome.ninja.BR
import com.ninjahome.ninja.IntentKey
import com.ninjahome.ninja.R
import com.ninjahome.ninja.databinding.ActivityLocationSearchBinding
import com.ninjahome.ninja.utils.UnitConversionUtils
import com.ninjahome.ninja.viewmodel.LocationSearchViewModel
import com.tencent.lbssearch.`object`.result.Geo2AddressResultObject
import com.tencent.lbssearch.`object`.result.SuggestionResultObject
import com.tencent.map.geolocation.TencentLocationUtils
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.activity_search_contact.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *Author:Mr'x
 *Time:
 *Description:
 */
class LocationSearchActivity : BaseActivity<LocationSearchViewModel, ActivityLocationSearchBinding>(R.layout.activity_location_search) {

    private var mSelectedPosi = 0
    private var currentLat=0.0
    private var currentLng=0.0
    private lateinit var mAdapter: LQRAdapterForRecyclerView<SuggestionResultObject.SuggestionData>

    override val mViewModel: LocationSearchViewModel by viewModel()

    override fun initView() {
        contactIdEt.setOnEditorActionListener { v, actionId, event ->
            mViewModel.searchAddress()
            return@setOnEditorActionListener true
        }

        mAdapter = object : LQRAdapterForRecyclerView<SuggestionResultObject.SuggestionData>(this, mViewModel.suggestionDatas, R.layout.item_location_poi) {
            override fun convert(helper: LQRViewHolderForRecyclerView, item: SuggestionResultObject.SuggestionData, position: Int) {
                if(currentLat ==0.0){
                    helper.setText(R.id.tvTitle, item.title).setText(R.id.tvDesc, item.address).setViewVisibility(R.id.ivSelected, if (mSelectedPosi == position) View.VISIBLE else View.GONE)
                }else{
                    val distanceM = TencentLocationUtils.distanceBetween(currentLat, currentLng, item.location.lat.toDouble(), item.location.lng.toDouble())
                    val distance = UnitConversionUtils.m2Km(distanceM.toFloat())
                    helper.setText(R.id.tvTitle, item.title).setText(R.id.tvDesc, "${distance} |${item.address}").setViewVisibility(R.id.ivSelected, if (mSelectedPosi == position) View.VISIBLE else View.GONE)
                }

            }
        }
        rvPOI.adapter = mAdapter
        mAdapter.onItemClickListener = OnItemClickListener { _: LQRViewHolder?, _: ViewGroup?, _: View?, position: Int ->
            mSelectedPosi = position
            mAdapter.notifyDataSetChangedWrapper()
            val data = Intent()
            val location = mViewModel.suggestionDatas.get(position).location
            data.putExtra(IntentKey.LOCATION_LAT,location.lat)
            data.putExtra(IntentKey.LOCATION_LNG,location.lng)
            setResult(RESULT_OK,data)
            finish()
        }
    }

    override fun initData() {
        currentLat = intent.getDoubleExtra(IntentKey.LOCATION_LAT,0.0)
        currentLng = intent.getDoubleExtra(IntentKey.LOCATION_LNG,0.0)
    }

    override fun initObserve() {
        mViewModel.searchSuccessEvent.observe(this) {
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun statusBarStyle(): Int = STATUSBAR_STYLE_WHITE

    override fun initVariableId(): Int = BR.viewModel
}