package com.wiidesk.app.backend.smile

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TransmisorMagico {
    suspend fun despertarDispositivo(mac: String, ipBroadcast: String = "255.255.255.255") = withContext(Dispatchers.IO) {
        try {
            val macBytes = mac.split(":", "-").map { it.toInt(16).toByte() }.toByteArray()
            val packetData = ByteArray(6 + 16 * macBytes.size).apply {
                for (i in 0..5) this[i] = 0xFF.toByte()
                var offset = 6
                while (offset < size) {
                    System.arraycopy(macBytes, 0, this, offset, macBytes.size)
                    offset += macBytes.size
                }
            }
            val address = InetAddress.getByName(ipBroadcast)
            val packet = DatagramPacket(packetData, packetData.size, address, 9)
            DatagramSocket().use { socket ->
                socket.broadcast = true
                socket.send(packet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
