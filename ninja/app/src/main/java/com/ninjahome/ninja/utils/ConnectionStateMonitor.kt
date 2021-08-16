package com.ninjahome.ninja.utils

import android.content.Context
import android.net.*
import com.ninjahome.ninja.event.EventNetWorkChange
import org.greenrobot.eventbus.EventBus

/**
 *Author:Mr'x
 *Time:2021/8/16
 *Description:
 */
class ConnectionStateMonitor: ConnectivityManager.NetworkCallback() {
    private val networkRequest:NetworkRequest = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()

    fun enable(context :Context){
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest,this)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        EventBus.getDefault().post(EventNetWorkChange())
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        EventBus.getDefault().post(EventNetWorkChange())
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            EventBus.getDefault().post(EventNetWorkChange())
        }
    }

}