package com.wiidesk.app.backend.smile

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class DispositivoControl(
    @DocumentId val id: String = "",
    val idEquipo: String = "",
    val userId: String = "",
    val usuario: String = "",
    val equipo: String = "",
    val localIp: String = "",
    val ipBroadcast: String = "",
    val macAddress: String = "",
    val comando:     String     = "ninguno",
    val estado:      String     = "ninguno", // encendido | suspendido | apagado | ninguno
    val pin:         Boolean    = false,
    val creado: Timestamp? = null,
    val actualizado: Timestamp? = null
) {
    // Getters virtuales para retrocompatibilidad con la interfaz
    val uid: String get() = userId
    val ipLocal: String get() = localIp

    val isOnline: Boolean
        get() {
            val lastUpdate = actualizado?.toDate()?.time ?: return false
            return (System.currentTimeMillis() - lastUpdate) < 70000 // Activo si reportó en los últimos 70s (latido es de 30s)
        }
}
