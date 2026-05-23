package com.wiidesk.app.network

sealed class ConnectionState {
    data object Disconnected : ConnectionState()
    data class Connecting(val ip: String, val port: Int) : ConnectionState()
    data class Connected(val ip: String, val port: Int, val latency: Long = 0L, val fps: Int = 0) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}
