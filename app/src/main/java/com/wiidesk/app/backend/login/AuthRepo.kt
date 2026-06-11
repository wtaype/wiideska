@file:Suppress("DEPRECATION")

package com.wiidesk.app.backend.login

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.wiidesk.app.R
import com.wiidesk.app.backend.perfil.Smile
import com.wiidesk.app.wiStore
import kotlinx.coroutines.tasks.await
import java.text.Normalizer

class AuthRepo {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    val currentUser get() = auth.currentUser
    val currentEmail: String? get() = auth.currentUser?.email
    val isLoggedIn: Boolean get() = auth.currentUser != null

    fun ensureReady(context: Context) {
        FirebaseApp.initializeApp(context.applicationContext)
    }

    suspend fun getSessionProfile(): Smile? {
        val user = auth.currentUser ?: return null
        return getProfileForSignedUser(user)
    }

    suspend fun login(emailOrUser: String, password: String): Smile {
        val clean = emailOrUser.trim().lowercase()
        val email = if ("@" in clean) clean else getEmailByUser(clean) ?: error("Usuario no encontrado")
        val user = auth.signInWithEmailAndPassword(email, password).await().user ?: error("No se pudo iniciar sesion")
        return getProfileForSignedUser(user) ?: run {
            auth.signOut()
            error("Perfil no encontrado en Firestore")
        }
    }

    suspend fun register(usuario: String, nombre: String, apellidos: String, email: String, password: String): Smile {
        val cleanUser = usuario.usuarioKey()
        val cleanEmail = email.trim().lowercase()
        require(cleanUser.length >= 4) { "El usuario necesita al menos 4 caracteres" }
        require(cleanEmail.contains("@")) { "Ingresa un email valido" }
        require(password.length >= 6) { "La contrasena necesita al menos 6 caracteres" }
        require(!userExists(cleanUser)) { "Ese usuario ya existe" }
        require(!emailExists(cleanEmail)) { "Ese email ya existe" }

        val user = auth.createUserWithEmailAndPassword(cleanEmail, password).await().user ?: error("No se pudo crear la cuenta")
        val profile = Smile(
            uid = user.uid,
            usuario = cleanUser,
            nombre = nombre.trim(),
            apellidos = apellidos.trim(),
            email = cleanEmail,
            registradoCon = "correo",
            terminos = true,
        )

        runCatching {
            user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(cleanUser).build()).await()
            crearPerfilAtomico(profile)
        }.onFailure {
            rollbackNewAuthUser(user)
            throw it
        }

        return profile
    }

    suspend fun recover(emailOrUser: String) {
        val clean = emailOrUser.trim().lowercase()
        val email = if ("@" in clean) clean else getEmailByUser(clean) ?: error("Usuario no encontrado")
        auth.sendPasswordResetEmail(email).await()
    }

    fun googleIntent(context: Context): Intent {
        val webClientId = context.webClientId()
        require(webClientId.isNotBlank()) { "Falta OAuth web client en google-services.json" }
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, options).signInIntent
    }

    suspend fun loginWithGoogle(data: Intent?): Smile? {
        val account = GoogleSignIn.getSignedInAccountFromIntent(data).await()
        val token = account.idToken ?: error("Google no devolvio idToken")
        val result = auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
        val user = result.user ?: error("No se pudo iniciar con Google")
        val isNewAuthUser = result.additionalUserInfo?.isNewUser == true

        getProfileForSignedUser(user)?.let { return it }

        val email = user.email?.trim()?.lowercase().orEmpty()
        if (email.isNotBlank() && emailExists(email)) {
            if (isNewAuthUser) rollbackNewAuthUser(user) else auth.signOut()
            error("Ese email ya esta registrado. Ingresa con correo y contrasena.")
        }

        return null
    }

    suspend fun completeGoogleRegistration(usuario: String): Smile {
        val user = auth.currentUser ?: error("Vuelve a iniciar con Google")
        val cleanUser = usuario.usuarioKey()
        val email = user.email?.trim()?.lowercase().orEmpty()
        require(cleanUser.matches(Regex("[a-z0-9_-]{4,}"))) { "Usuario minimo 4 caracteres" }
        require(email.contains("@")) { "Google no devolvio email valido" }
        require(!userExists(cleanUser)) { "Ese usuario ya existe" }
        require(!emailExists(email)) { "Ese email ya esta registrado" }

        val parts = user.displayName.orEmpty().trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val profile = Smile(
            uid = user.uid,
            usuario = cleanUser,
            nombre = parts.firstOrNull() ?: cleanUser,
            apellidos = parts.drop(1).joinToString(" "),
            email = email,
            avatar = user.photoUrl?.toString(),
            registradoCon = "google",
            terminos = true,
        )

        runCatching {
            user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(cleanUser).build()).await()
            crearPerfilAtomico(profile)
            user.getIdToken(true).await()
        }.onFailure {
            rollbackNewAuthUser(user)
            throw it
        }

        return profile
    }

    suspend fun discardPendingGoogleRegistration() {
        val user = auth.currentUser ?: return
        val hasProfile = getProfileForSignedUser(user) != null
        if (hasProfile) auth.signOut() else rollbackNewAuthUser(user)
    }

    fun logout(context: Context) {
        runCatching { auth.signOut() }
        runCatching { GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut() }
        val store = wiStore(context)
        store.remove("wiSmile", "saved_username")
    }

    suspend fun updateProfilePhoto(usuario: String, avatar: String?) {
        firestore.collection("smiles").document(usuario.lowercase().trim()).update(
            mapOf("avatar" to avatar)
        ).await()
    }

    suspend fun updateProfileTheme(usuario: String, themeName: String) {
        firestore.collection("smiles").document(usuario.lowercase().trim()).update(
            mapOf("tema" to themeName)
        ).await()
    }

    suspend fun userExists(usuario: String): Boolean =
        firestore.collection("registros").document(usuario.trim().lowercase()).get().await().exists()

    suspend fun emailExists(email: String): Boolean =
        firestore.collection("registros")
            .whereEqualTo("email", email.trim().lowercase())
            .limit(1)
            .get()
            .await()
            .documents
            .isNotEmpty()

    private suspend fun getEmailByUser(usuario: String): String? =
        firestore.collection("registros").document(usuario.trim().lowercase()).get().await().getString("email")

    private suspend fun getProfileForSignedUser(user: FirebaseUser): Smile? {
        val displayUser = user.displayName?.usuarioKey().orEmpty()
        if (displayUser.isNotBlank()) {
            getProfileByUsuario(displayUser)?.let { profile ->
                if (profile.uid == user.uid || profile.email.equals(user.email.orEmpty(), ignoreCase = true)) return profile
            }
        }

        val usuarioByUid = firestore.collection("registros")
            .whereEqualTo("uid", user.uid)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.getString("usuario")
            ?.usuarioKey()

        if (!usuarioByUid.isNullOrBlank()) {
            getProfileByUsuario(usuarioByUid)?.let { return it }
        }

        return null
    }

    private suspend fun getProfileByUsuario(usuario: String): Smile? {
        val snap = runCatching {
            firestore.collection("smiles").document(usuario.trim().lowercase()).get().await()
        }.getOrNull() ?: return null
        return if (snap.exists()) snap.toSmile() else null
    }

    private suspend fun crearPerfilAtomico(profile: Smile) {
        val usuario = profile.usuario.trim().lowercase()
        val batch = firestore.batch()
        batch.set(firestore.collection("smiles").document(usuario), profile.toFirestore(newDocument = true))
        batch.set(firestore.collection("registros").document(usuario), registroMap(usuario, profile.email, profile.uid))
        batch.commit().await()
    }

    private suspend fun rollbackNewAuthUser(user: FirebaseUser) {
        runCatching { user.delete().await() }
            .onFailure { auth.signOut() }
    }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toSmile(): Smile {
    val rawFecha = get("fechaNacimiento")
    val fechaNacimientoStr = when (rawFecha) {
        is com.google.firebase.Timestamp -> {
            try {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(rawFecha.toDate())
            } catch (e: Exception) {
                ""
            }
        }
        is String -> rawFecha
        else -> ""
    }
    return Smile(
        uid = getString("uid").orEmpty(),
        userId = getString("userId").orEmpty(),
        usuario = getString("usuario").orEmpty(),
        nombre = getString("nombre").orEmpty(),
        apellidos = getString("apellidos").orEmpty(),
        email = getString("email").orEmpty(),
        avatar = getString("avatar") ?: getString("foto"),
        plan = getString("plan") ?: "free",
        rol = getString("rol") ?: "usuario",
        estado = getString("estado") ?: "activo",
        activo = getBoolean("activo") ?: true,
        registradoCon = getString("registradoCon") ?: getString("registradoPor") ?: "correo",
        terminos = getBoolean("terminos") ?: true,
        tema = getString("tema") ?: "Futuro",
        verificado = getBoolean("verificado") ?: false,
        segmento = getString("segmento") ?: "publico",
        fechaNacimiento = fechaNacimientoStr,
        pais = getString("pais").orEmpty(),
        genero = getString("genero").orEmpty(),
        gustos = getString("gustos").orEmpty(),
        bio = getString("bio").orEmpty(),
    )
}

private fun Context.webClientId(): String =
    runCatching { getString(R.string.default_web_client_id) }.getOrDefault("")

private fun String.usuarioKey(): String =
    Normalizer.normalize(lowercase().trim(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .replace(Regex("[^a-z0-9_-]+"), "")
        .take(32)

private fun registroMap(usuario: String, email: String, uid: String): Map<String, Any> = mapOf(
    "usuario" to usuario,
    "email" to email.trim().lowercase(),
    "uid" to uid,
    "userId" to uid,
    "creado" to FieldValue.serverTimestamp(),
)
