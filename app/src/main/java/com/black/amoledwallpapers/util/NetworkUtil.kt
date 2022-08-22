package com.black.amoledwallpapers.util

import android.content.Context
import android.net.ConnectivityManager


object NetworkUtil {
    private const val TYPE_WIFI = 1
    private const val TYPE_MOBILE = 2
    private const val TYPE_NOT_CONNECTED = 0


    fun getNetworkState(context: Context): Int {
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI
            }

            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE
            }
        }
        return TYPE_NOT_CONNECTED
    }

    fun isConnected(context: Context): Boolean {
        return getNetworkState(context) != TYPE_NOT_CONNECTED
    }
}