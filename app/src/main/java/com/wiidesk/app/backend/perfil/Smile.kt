package com.wiidesk.app.backend.perfil

import com.google.firebase.firestore.FieldValue

data class Smile(
    val uid: String = "",
    val userId: String = "",
    val usuario: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val email: String = "",
    val avatar: String? = null,
    val plan: String = "free",
    val rol: String = "usuario",
    val estado: String = "activo",
    val activo: Boolean = true,
    val registradoCon: String = "correo",
    val terminos: Boolean = true,
    val tema: String = "Futuro",
    val verificado: Boolean = false,
    val segmento: String = "publico",
    val fechaNacimiento: String = "",
    val pais: String = "",
    val genero: String = "",
    val gustos: String = "",
    val bio: String = "",
) {
    val nombreCompleto: String
        get() = listOf(nombre, apellidos).filter { it.isNotBlank() }.joinToString(" ")

    fun toFirestore(newDocument: Boolean = false): Map<String, Any?> = buildMap {
        put("uid", uid)
        put("userId", uid)
        put("usuario", usuario)
        put("email", email.trim().lowercase())
        put("nombre", nombre.trim())
        put("apellidos", apellidos.trim())
        put("avatar", avatar)
        put("plan", plan)
        put("rol", rol)
        put("estado", estado)
        put("activo", activo)
        put("registradoCon", registradoCon)
        put("terminos", terminos)
        put("tema", tema)
        put("verificado", verificado)
        put("segmento", segmento)
        put("fechaNacimiento", fechaNacimiento)
        put("pais", pais)
        put("genero", genero)
        put("gustos", gustos)
        put("bio", bio)
        if (newDocument) {
            put("creado", FieldValue.serverTimestamp())
            put("terminosFecha", FieldValue.serverTimestamp())
        }
        put("actualizado", FieldValue.serverTimestamp())
        put("ultActividad", FieldValue.serverTimestamp())
    }
}
