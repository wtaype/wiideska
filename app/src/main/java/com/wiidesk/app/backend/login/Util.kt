package com.wiidesk.app.backend.login

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.wiidesk.app.wiStore

val Context.wiUserId: String?
    get() = wiStore(this).getls("wiSmile")?.optString("userId")
        ?: FirebaseAuth.getInstance().currentUser?.uid

object Comparar {
    fun esMio(context: Context, otherUserId: String?): Boolean {
        val current = context.wiUserId
        return !current.isNullOrBlank() && current == otherUserId
    }
}
