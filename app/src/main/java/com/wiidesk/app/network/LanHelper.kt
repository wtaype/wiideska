package com.wiidesk.app.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import java.net.Inet4Address

object LanHelper {

    fun getLocalIp(context: Context): String {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return "Sin red"
            val props = cm.getLinkProperties(network) ?: return "Sin red"
            props.linkAddresses
                .firstOrNull { !it.address.isLoopbackAddress && it.address is Inet4Address }
                ?.address?.hostAddress ?: "Sin red"
        } catch (e: Exception) { "Sin red" }
    }

    @SuppressLint("MissingPermission")
    fun getNetworkName(context: Context): String {
        return try {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ssid = wm.connectionInfo?.ssid?.replace("\"", "")
            ssid?.takeIf { it.isNotEmpty() && it != "<unknown ssid>" } ?: "Red Local"
        } catch (e: Exception) { "Red Local" }
    }

    fun getLanInfo(context: Context): Pair<String, String> {
        return Pair(getNetworkName(context), getLocalIp(context))
    }
}
