package com.wiidesk.app.frontend.rutas

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.Monitor
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wiidesk.app.GlassCard
import com.wiidesk.app.LocalWiMessenger
import com.wiidesk.app.R
import com.wiidesk.app.WiButton
import com.wiidesk.app.WiCss
import com.wiidesk.app.WiMsgType
import com.wiidesk.app.WiText
import com.wiidesk.app.Wii
import com.wiidesk.app.backend.login.AuthRepo
import com.wiidesk.app.lib.GoldPill
import com.wiidesk.app.backend.perfil.Smile
import com.wiidesk.app.premiumBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private enum class AuthMode { Login, Register, Recover, GoogleProfile }

@Composable
fun Login(
    navController: NavController? = null,
    auth: AuthRepo = remember { AuthRepo() },
    onAuthenticated: (Smile) -> Unit = {},
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val messenger = LocalWiMessenger.current
    val scope = rememberCoroutineScope()
    var mode by remember { mutableStateOf(AuthMode.Login) }
    var emailLoading by remember { mutableStateOf(false) }
    var googleLoading by remember { mutableStateOf(false) }
    var emailOrUser by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }
    var googleEmail by remember { mutableStateOf("") }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) { auth.ensureReady(context) }
    }

    fun show(message: String, type: WiMsgType = WiMsgType.Info) {
        messenger.Mensaje(message, type)
    }

    fun openLegal(route: String) {
        if (navController != null) navController.navigate(route) else uriHandler.openUri("${Wii.web}/$route")
    }

    fun runAuth(block: suspend () -> Smile) {
        scope.launch {
            emailLoading = true
            runCatching {
                withContext(Dispatchers.IO) { auth.ensureReady(context) }
                block()
            }
                .onSuccess {
                    show("Bienvenido, ${it.nombre.ifBlank { it.usuario }}", WiMsgType.Success)
                    onAuthenticated(it)
                }
                .onFailure { show(it.message ?: "No se pudo completar la accion", WiMsgType.Error) }
            emailLoading = false
        }
    }

    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            show("Google cancelo el inicio de sesion", WiMsgType.Warning)
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            googleLoading = true
            runCatching {
                withContext(Dispatchers.IO) { auth.ensureReady(context) }
                auth.loginWithGoogle(result.data)
            }
                .onSuccess { profile ->
                    if (profile == null) {
                        googleEmail = auth.currentEmail.orEmpty()
                        usuario = ""
                        acceptedTerms = false
                        mode = AuthMode.GoogleProfile
                    } else {
                        show("Sesion iniciada con Google", WiMsgType.Success)
                        onAuthenticated(profile)
                    }
                }
                .onFailure { e ->
                    runCatching { auth.discardPendingGoogleRegistration() }
                    val msg = when {
                        e.message?.contains("DEVELOPER_ERROR", ignoreCase = true) == true ->
                            "Error Google: revisa SHA-1 en Firebase Console"
                        e.message?.contains("network", ignoreCase = true) == true ->
                            "Sin conexion a internet"
                        else -> e.message ?: "Google no pudo iniciar sesion"
                    }
                    show(msg, WiMsgType.Error)
                }
            googleLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .premiumBackground()
            .padding(24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Spacer(Modifier.height(34.dp))
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = WiCss.mco.copy(alpha = 0.16f)),
                modifier = Modifier.size(90.dp),
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Monitor, null, tint = WiCss.mco, modifier = Modifier.size(48.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(Wii.appName, style = WiText.display.copy(color = WiCss.mco), textAlign = TextAlign.Center)
            Text("Control remoto seguro para tu escritorio", style = WiText.small, textAlign = TextAlign.Center)
            Spacer(Modifier.height(18.dp))

            GlassCard(Modifier.fillMaxWidth(), intensity = 0.72f) {
                AnimatedContent(targetState = mode, label = "auth-mode") { currentMode ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(11.dp)) {
                        when (currentMode) {
                            AuthMode.Login -> {
                                AuthTitle("Bienvenido", "Entra para sincronizar tus dispositivos.")
                                GoogleAccessButton(googleLoading) {
                                    runCatching { googleLauncher.launch(auth.googleIntent(context)) }
                                        .onFailure { show(it.message ?: "Google no esta disponible", WiMsgType.Error) }
                                }
                                AuthField(emailOrUser, { emailOrUser = it }, "Email o usuario", Icons.Rounded.Email)
                                AuthPassword(password, { password = it }, "Contrasena")
                                WiButton(
                                    text = "Entrar",
                                    onClick = { runAuth { auth.login(emailOrUser, password) } },
                                    loading = emailLoading,
                                    icon = Icons.Rounded.Login,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                AuthTextAction("Olvide mi contrasena", Icons.AutoMirrored.Rounded.Help) { mode = AuthMode.Recover }
                                AuthTextAction("Crear cuenta nueva", Icons.Rounded.PersonAdd) { mode = AuthMode.Register }
                            }

                            AuthMode.Register -> {
                                AuthTitle("Crear cuenta", "Tu perfil guardara la sesion y preferencias.")
                                GoogleAccessButton(googleLoading) {
                                    runCatching { googleLauncher.launch(auth.googleIntent(context)) }
                                        .onFailure { show(it.message ?: "Google no esta disponible", WiMsgType.Error) }
                                }
                                AuthField(usuario, { usuario = cleanUserInput(it) }, "Usuario", Icons.Rounded.Badge)
                                AuthField(email, { email = it }, "Email", Icons.Rounded.Email, keyboardType = KeyboardType.Email)
                                AuthField(nombre, { nombre = it }, "Nombre", Icons.Rounded.Person)
                                AuthField(apellidos, { apellidos = it }, "Apellidos", Icons.Rounded.Person)
                                AuthPassword(password, { password = it }, "Contrasena")
                                AuthPassword(confirmPassword, { confirmPassword = it }, "Confirmar contrasena")
                                TermsRow(
                                    checked = acceptedTerms,
                                    onCheckedChange = { acceptedTerms = it },
                                    onTerms = { openLegal("terminos") },
                                    onPrivacy = { openLegal("privacidad") },
                                )
                                WiButton(
                                    text = "Crear cuenta",
                                    onClick = {
                                        when {
                                            password != confirmPassword -> show("Las contrasenas no coinciden", WiMsgType.Warning)
                                            !acceptedTerms -> show("Acepta terminos y privacidad", WiMsgType.Warning)
                                            else -> runAuth { auth.register(usuario, nombre, apellidos, email, password) }
                                        }
                                    },
                                    loading = emailLoading,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                AuthTextAction("Ya tengo cuenta", Icons.Rounded.Person) { mode = AuthMode.Login }
                            }

                            AuthMode.Recover -> {
                                AuthTitle("Recuperar acceso", "Te enviaremos un enlace al correo.")
                                AuthField(email, { email = it }, "Email", Icons.Rounded.Email, keyboardType = KeyboardType.Email)
                                WiButton(
                                    text = "Enviar enlace",
                                    onClick = {
                                        scope.launch {
                                            emailLoading = true
                                            runCatching { auth.recover(email) }
                                                .onSuccess {
                                                    show("Listo, revisa tu correo", WiMsgType.Success)
                                                    mode = AuthMode.Login
                                                }
                                                .onFailure { show(it.message ?: "No se pudo enviar el correo", WiMsgType.Error) }
                                            emailLoading = false
                                        }
                                    },
                                    loading = emailLoading,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                AuthTextAction("Volver a ingresar", Icons.Rounded.Person) { mode = AuthMode.Login }
                            }

                            AuthMode.GoogleProfile -> {
                                AuthTitle("Casi listo", "Elige tu usuario para completar Google.")
                                if (googleEmail.isNotBlank()) {
                                    GoldPill(text = googleEmail, modifier = Modifier.align(Alignment.CenterHorizontally))
                                }
                                AuthField(usuario, { usuario = cleanUserInput(it) }, "Usuario", Icons.Rounded.Badge)
                                if (usuario.isNotBlank() && usuario.length < 4) {
                                    Text(
                                        text = "Mínimo 4 caracteres (letras, números, _ o -)",
                                        style = WiText.small,
                                        color = WiCss.error,
                                        modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
                                    )
                                }
                                TermsRow(
                                    checked = acceptedTerms,
                                    onCheckedChange = { acceptedTerms = it },
                                    onTerms = { openLegal("terminos") },
                                    onPrivacy = { openLegal("privacidad") },
                                )
                                WiButton(
                                    text = "Guardar perfil",
                                    onClick = {
                                        when {
                                            usuario.length < 4 -> show("Usuario minimo 4 caracteres", WiMsgType.Warning)
                                            !acceptedTerms -> show("Acepta terminos y privacidad", WiMsgType.Warning)
                                            else -> runAuth { auth.completeGoogleRegistration(usuario) }
                                        }
                                    },
                                    loading = emailLoading,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                AuthTextAction("Cancelar y volver", Icons.Rounded.Person) {
                                    scope.launch {
                                        runCatching { auth.discardPendingGoogleRegistration() }
                                        googleEmail = ""
                                        usuario = ""
                                        acceptedTerms = false
                                        mode = AuthMode.Login
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthTitle(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = WiText.h2.copy(color = WiCss.tx1), textAlign = TextAlign.Center)
        Text(subtitle, style = WiText.small, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 3.dp))
    }
}

@Composable
private fun GoogleAccessButton(loading: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        enabled = !loading,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (loading) {
                androidx.compose.material3.CircularProgressIndicator(
                    color = Color(0xFF1F2937),
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painterResource(R.drawable.ic_google_logo), contentDescription = "Google", modifier = Modifier.size(25.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Continua con Google", style = WiText.body.copy(color = Color(0xFF1F2937), fontWeight = FontWeight.SemiBold))
                }
            }
        }
    }
}

@Composable
private fun AuthTextAction(text: String, icon: ImageVector, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Icon(icon, null, tint = WiCss.mco, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, style = WiText.small.copy(color = WiCss.mco, fontWeight = FontWeight.SemiBold))
    }
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontFamily = com.wiidesk.app.fPoppins) },
        leadingIcon = { Icon(icon, null, tint = WiCss.mco, modifier = Modifier.size(20.dp)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = WiCss.mco,
            unfocusedBorderColor = WiCss.brd.copy(alpha = 0.60f),
            focusedContainerColor = WiCss.inp.copy(alpha = 0.80f),
            unfocusedContainerColor = WiCss.inp.copy(alpha = 0.50f),
            focusedTextColor = WiCss.tx1,
            unfocusedTextColor = WiCss.tx1,
        ),
    )
}

@Composable
private fun AuthPassword(value: String, onValueChange: (String) -> Unit, label: String) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontFamily = com.wiidesk.app.fPoppins) },
        leadingIcon = { Icon(Icons.Rounded.Lock, null, tint = WiCss.mco, modifier = Modifier.size(20.dp)) },
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, null, tint = WiCss.mco)
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = WiCss.mco,
            unfocusedBorderColor = WiCss.brd.copy(alpha = 0.60f),
            focusedContainerColor = WiCss.inp.copy(alpha = 0.80f),
            unfocusedContainerColor = WiCss.inp.copy(alpha = 0.50f),
            focusedTextColor = WiCss.tx1,
            unfocusedTextColor = WiCss.tx1,
        ),
    )
}

@Composable
private fun TermsRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTerms: () -> Unit,
    onPrivacy: () -> Unit,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange, colors = CheckboxDefaults.colors(checkedColor = WiCss.mco))
        Text("Acepto ", style = WiText.small, color = WiCss.tx1)
        Text("terminos", style = WiText.small.copy(color = WiCss.mco, fontWeight = FontWeight.Bold), modifier = Modifier.clickable(onClick = onTerms))
        Text(" y ", style = WiText.small, color = WiCss.tx1)
        Text("privacidad", style = WiText.small.copy(color = WiCss.mco, fontWeight = FontWeight.Bold), modifier = Modifier.clickable(onClick = onPrivacy))
    }
}

private fun cleanUserInput(value: String): String =
    value.lowercase().filter { it.isLetterOrDigit() || it == '_' || it == '-' }.take(32)
