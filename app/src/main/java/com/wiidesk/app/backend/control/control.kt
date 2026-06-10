package com.wiidesk.app.backend.core.wol

import com.google.firebase.Timestamp

data class DispositivoControl(
    val id: String = "",
    val uid: String = "",
    val usuario: String = "",
    val equipo: String = "",
    val ipLocal: String = "",
    val ipBroadcast: String = "",
    val macAddress: String = "",
    val comando: String = "ninguno",
    val pin: Boolean = false,
    val creado: Timestamp? = null,
    val actualizado: Timestamp? = null
) {
    val isOnline: Boolean
        get() {
            val lastUpdate = actualizado?.toDate()?.time ?: return false
            return (System.currentTimeMillis() - lastUpdate) < 70000 // Activo si reportó en los últimos 70s (latido es de 30s)
        }
}
